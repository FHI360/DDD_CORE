package org.fhi360.ddd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.library.NavigationBar;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.fhi360.ddd.Db.DDDDb;
import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.domain.Patient;
import org.fhi360.ddd.domain.Pharmacy;
import org.fhi360.ddd.dto.PatientDto;
import org.fhi360.ddd.dto.Response;
import org.fhi360.ddd.util.PrefManager;
import org.fhi360.ddd.util.SettingConfig;
import org.fhi360.ddd.webservice.APIService;
import org.fhi360.ddd.webservice.ClientAPI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;

import static org.fhi360.ddd.util.Constants.PREFERENCES_ENCOUNTER;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AdminEdit extends AppCompatActivity implements View.OnClickListener {
    private DDDDb dddDb;
    private EditText lastViralLoad, dateRegistration,
            viralLoadDueDate, hospitalNum, uniqueId,
            surname, otherNames, dateBirth,
            dateLastViralLoad, age, address,
            dateLastRefill, dateNextRefill, dateLastClinic, dateNextClinic,
            phone;
    private AppCompatSpinner viralLoadType, lastClinicStage, gender;
    private TextInputLayout ageEstimateLayoutl;
    private Spinner dddoutlet, facilityName;
    private Button save;
    private HashMap<String, String> user = null;
    private HashMap<String, String> user1 = null;
    private Calendar myCalendar = Calendar.getInstance();
    //private String deviceconfigId;
    private CheckBox estimatedAge;
    private Patient patient;
    private SharedPreferences preferences;
    private ScrollView activityNewVisit1;
    private ScrollView activityNewVisit2;
    private ScrollView activityNewVisit3;

    private NavigationBar bar;
    private int position = 0;
    private Button btn_next, btn_prev;
    int counter = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_registration_home);
        this.preferences = getSharedPreferences(PREFERENCES_ENCOUNTER, 0);
        restorePreferences();
        restorePreferences();
        if (savedInstanceState != null) {
            String json = savedInstanceState.getString("patient");
            patient = new Gson().fromJson(json, Patient.class);
        }

        dddDb = DDDDb.getInstance(this);
        dateRegistration = findViewById(R.id.dateRegistrations);
        dddoutlet = findViewById(R.id.dddoutlet);
        lastClinicStage = findViewById(R.id.lastClinicalStage);
        estimatedAge = findViewById(R.id.estimatedAge);
        hospitalNum = findViewById(R.id.hospitalNum);
        uniqueId = findViewById(R.id.uniqueId);
        surname = findViewById(R.id.surnames);
        otherNames = findViewById(R.id.otherNamess);
        dateBirth = findViewById(R.id.dateBirthEnrollemt);
        dateLastViralLoad = findViewById(R.id.dateLastViralLoad);
        viralLoadDueDate = findViewById(R.id.viralLoadDueDate);
        age = findViewById(R.id.ageEnrollemt);
        facilityName = findViewById(R.id.facilityName);
        address = findViewById(R.id.addresss);
        lastViralLoad = findViewById(R.id.lastViralLoad);
        gender = findViewById(R.id.genders);
        phone = findViewById(R.id.phones);
        ageEstimateLayoutl = findViewById(R.id.ageEnrollemt1);

        viralLoadType = findViewById(R.id.viralLoadType);
        dateLastRefill = findViewById(R.id.dateLastRefill);
        dateNextRefill = findViewById(R.id.dateNextRefill);
        dateLastClinic = findViewById(R.id.dateLastClinic);
        dateNextClinic = findViewById(R.id.dateNextClinic);
        save = findViewById(R.id.finishButton);
        age.setVisibility(View.VISIBLE);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Client Update");
        estimatedAge.setOnClickListener(this);

        activityNewVisit1 = findViewById(R.id.personalDetailsCard1);
        activityNewVisit2 = findViewById(R.id.personalDetailsCard2);
        activityNewVisit3 = findViewById(R.id.personalDetailsCard3);
        bar = findViewById(R.id.navBar);
        btn_next = findViewById(R.id.btn_next);
        btn_prev = findViewById(R.id.btn_prev);


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                if (counter <= 3) {
                    System.out.println("counter 2 " + counter);
                    setup(true, counter);
                    btn_prev.setEnabled(true);
                    if (counter == 3) {
                        btn_next.setEnabled(false);
                    }
                }
            }


        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter--;
                if (!(counter < 0)) {
                    if (counter == 1) {
                        btn_prev.setEnabled(false);
                    }
                    setup(true, counter);
                    btn_next.setEnabled(true);
                }
            }
        });
        List<Long> pharmacyId = new ArrayList();
        List<String> outLetName = new ArrayList();
        List<Pharmacy> account = DDDDb.getInstance(this).pharmacistAccountRepository().findByAll();
        for (Pharmacy account1 : account) {
            pharmacyId.add(account1.getId());
            outLetName.add(account1.getName());
        }


        final ArrayAdapter pharmacy = new ArrayAdapter<>(AdminEdit.this,
                R.layout.support_simple_spinner_dropdown_item, outLetName);
        pharmacy.notifyDataSetChanged();
        dddoutlet.setAdapter(pharmacy);
        dddoutlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Long pharmacyId1 = pharmacyId.get(position);
                savePin(pharmacyId1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dateRegistration.setText(patient.getDateStarted());


        SettingConfig.setSpinText(lastClinicStage, patient.getLastClinicStage());

        hospitalNum.setText(patient.getHospitalNum());
        uniqueId.setText(patient.getUniqueId());
        surname.setText(patient.getSurname());
        otherNames.setText(patient.getOtherNames());
        dateBirth.setText(patient.getDateBirth());

        dateLastViralLoad.setText(patient.getDateLastViralLoad());


        viralLoadDueDate.setText(patient.getViralLoadDueDate());


        address.setText(patient.getAddress());
        lastViralLoad.setText(patient.getLastViralLoad() + "");
        SettingConfig.setSpinText(gender, patient.getGender());
        phone.setText(patient.getPhone());
        SettingConfig.setSpinText(viralLoadType, patient.getViralLoadType());
        dateLastRefill.setText(patient.getDateLastRefill());
        dateNextRefill.setText(patient.getDateNextRefill());
        dateLastClinic.setText(patient.getDateLastClinic());


        dateNextClinic.setText(patient.getDateNextClinic());


        final DatePickerDialog.OnDateSetListener dateNextClinic1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateOfNxetClinic();
            }

        };


        dateNextClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final DatePickerDialog mDatePicker = new DatePickerDialog(AdminEdit.this, dateNextClinic1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                mDatePicker.show();
            }
        });

        final DatePickerDialog.OnDateSetListener dateLastClinic1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateOfLastClinic();
            }

        };


        dateLastClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final DatePickerDialog mDatePicker = new DatePickerDialog(AdminEdit.this, dateLastClinic1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePicker.show();
            }
        });

        final DatePickerDialog.OnDateSetListener dateNextRefill1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateOfNextRefill();
            }

        };


        dateNextRefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                final DatePickerDialog mDatePicker = new DatePickerDialog(AdminEdit.this, dateNextRefill1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                mDatePicker.show();


            }
        });

        final DatePickerDialog.OnDateSetListener dateLastRefill1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateOfLastRefill();
            }

        };


        dateLastRefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final DatePickerDialog mDatePicker = new DatePickerDialog(AdminEdit.this, dateLastRefill1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePicker.show();


            }
        });


        final DatePickerDialog.OnDateSetListener viralLoadDueDate1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateViralLoadDueDate();
            }

        };


        viralLoadDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                final DatePickerDialog mDatePicker = new DatePickerDialog(AdminEdit.this, viralLoadDueDate1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                mDatePicker.show();


            }
        });


        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabe2();
            }

        };

        final DatePickerDialog.OnDateSetListener dateLastViraLload1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLastViralLoad();
            }

        };

        dateBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final DatePickerDialog mDatePicker = new DatePickerDialog(AdminEdit.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePicker.show();
            }
        });

        dateLastViralLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final DatePickerDialog mDatePicker = new DatePickerDialog(AdminEdit.this, dateLastViraLload1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePicker.show();
            }
        });

        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                Calendar todayDate = Calendar.getInstance();
                try {
                    todayDate.setTime(sdf.parse(dateRegistration.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //  int curYear = todayDate.get(Calendar.YEAR);
                //  int currentMonth = todayDate.get(Calendar.MONTH);
                // int currentDay = todayDate.get(Calendar.DAY_OF_MONTH);

//                Date now = new Date();
//                Date dob =now.compareTo() //now.cu(curYear).minusMonths(currentMonth).minusDays(currentDay);
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
//                dateBirth.setText(dob.format(formatter));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final ArrayList arrayListFacilityId = new ArrayList();
        ArrayList arrayListFacilityName = new ArrayList();
        List<Facility> facilityDtoArrayList = DDDDb.getInstance(AdminEdit.this).facilityRepository().findAll();
        for (Facility facility : facilityDtoArrayList) {
            arrayListFacilityId.add(facility.getId());
            if (facility.getName().equals("Redemption Hospital")) {
                arrayListFacilityName.add(facility.getName());
            } else {
                arrayListFacilityName.add(facility.getName());
            }


        }
        final ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(AdminEdit.this,
                R.layout.spinner_items, arrayListFacilityName);
        districtAdapter.setDropDownViewResource(R.layout.spinner_text_color);
        districtAdapter.notifyDataSetChanged();
        facilityName.setAdapter(districtAdapter);
        facilityName.setSelection(0);
        facilityName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long facId = (long) arrayListFacilityId.get(position);
                new PrefManager(getApplicationContext()).saveFacId(facId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        //SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");


        setupFloatingLabelError();
        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {
                // Long facilityId = DDDDb.getInstance(getApplicationContext()).facilityRepository().getFacility().getId();

                if (validateInput1(facilityName.getSelectedItem().toString(), hospitalNum.getText().toString(), surname.getText().toString(), otherNames.getText().toString(), dateBirth.getText().toString(), dateRegistration.getText().toString())) {
                    if (gender.getSelectedItem().toString().equals("")) {
                        FancyToast.makeText(getApplicationContext(), "Select gender", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                        FancyToast.makeText(getApplicationContext(), "Patient already exist", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    } else {
                        try {
                            PatientDto patient1 = new PatientDto();
                            patient1.setId(patient.getId());
                            patient1.setHospitalNum(hospitalNum.getText().toString());
                            Facility facility = new Facility();
                            HashMap<String, String> facility1 = new PrefManager(AdminEdit.this).getFacId();
                            String facId = facility1.get("facId");
                            Long fac = Long.valueOf(facId);
                            System.out.println("FACILITYID " + fac);
                            facility.setId(Long.valueOf(facId));
                            patient1.setFacility(facility);
                            patient1.setUniqueId(uniqueId.getText().toString());
                            patient1.setSurname(surname.getText().toString());
                            patient1.setOtherNames(otherNames.getText().toString());
                            patient1.setGender(gender.getSelectedItem().toString());
                            String dateBirth1 = dateBirth.getText().toString();
                            patient1.setDateBirth(dateBirth1);
                            patient1.setAddress(address.getText().toString());
                            patient1.setPhone(phone.getText().toString());
                            String dateRegistration1 = dateRegistration.getText().toString();
                            patient1.setDateStarted(dateRegistration1);
                            patient1.setLastClinicStage(lastClinicStage.getSelectedItem().toString());
                            patient1.setLastViralLoad(Double.parseDouble(lastViralLoad.getText().toString()));
                            String dateLastViralLoad1 = dateLastViralLoad.getText().toString();
                            patient1.setDateLastViralLoad(dateLastViralLoad1);
                            String viralLoadDueDate1 = viralLoadDueDate.getText().toString();
                            patient1.setViralLoadDueDate(viralLoadDueDate1);
                            patient1.setViralLoadType(viralLoadType.getSelectedItem().toString());
                            String dateLastClinic1 = dateLastClinic.getText().toString();
                            patient1.setDateLastClinic(dateLastClinic1);
                            String dateNextClinic1 = dateNextClinic.getText().toString();
                            patient1.setDateNextClinic(dateNextClinic1);
                            String dateLastRefill1 = dateLastRefill.getText().toString();
                            patient1.setDateLastRefill(dateLastRefill1);
                            String dateNextRefill2 = dateNextRefill.getText().toString();
                            patient1.setDateNextRefill(dateNextRefill2);
                            HashMap<String, String> pinCode = getPincode();
                            String pharmacyid = pinCode.get("pharmacyid");
                            patient1.setPharmacyId(Long.valueOf(pharmacyid));
                            System.out.println("patient " + patient);
                            save(patient1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                showAlertDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAlertDelete() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.forget_pop_up, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(promptsView);
        final TextView notitopOk, notitopNotnow;
        final TextView notitoptxt;
        notitopOk = promptsView.findViewById(R.id.notitopOk);
        notitopNotnow = promptsView.findViewById(R.id.notitopNotnow);
        notitoptxt = promptsView.findViewById(R.id.notitoptxt);
        notitoptxt.setVisibility(View.VISIBLE);
        notitoptxt.setText("Are you sure you want to deactivate this Client ?");
        notitopNotnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        notitopOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DDDDb.getInstance(getApplicationContext()).patientRepository().delete(patient);
                delete(patient.getId());
                Intent intent = new Intent(getApplicationContext(), AdminPatientList2.class);
                startActivity(intent);
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void save(PatientDto patientDto) {
        ProgressDialog progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("Saving patient...");
        progressdialog.setCancelable(false);
        progressdialog.setIndeterminate(false);
        progressdialog.setMax(100);
        progressdialog.show();
        ClientAPI clientAPI = APIService.createService(ClientAPI.class);
        Call<Response> objectCall = clientAPI.updatePatient(patientDto);
        objectCall.enqueue(new Callback<Response>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<org.fhi360.ddd.dto.Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    org.fhi360.ddd.dto.Response response1 = response.body();
                    if (Objects.requireNonNull(response1).getPatient() != null) {
                        PatientDto patient = response1.getPatient();
                        Patient patient1 = new Patient();
                        patient1.setId(patient.getId());
                        patient1.setHospitalNum(patient.getHospitalNum());
                        patient1.setFacilityId(patient.getFacility().getId());
                        patient1.setUniqueId(patient.getUniqueId());
                        patient1.setSurname(patient.getSurname());
                        patient1.setOtherNames(patient.getOtherNames());
                        patient1.setGender(patient.getGender());
                        patient1.setDateBirth(patient.getDateBirth());
                        patient1.setAddress(patient.getAddress());
                        patient1.setPhone(patient.getPhone());
                        patient1.setDateStarted(patient.getDateStarted());
                        patient1.setLastClinicStage(patient.getLastClinicStage());
                        patient1.setLastViralLoad(patient.getLastViralLoad());
                        patient1.setDateLastViralLoad(patient.getDateLastViralLoad());
                        patient1.setViralLoadDueDate(patient.getViralLoadDueDate());
                        patient1.setViralLoadType(patient.getViralLoadType());
                        patient1.setDateLastClinic(patient.getDateLastClinic());
                        patient1.setDateNextClinic(patient.getDateNextClinic());
                        patient1.setDateLastRefill(patient.getDateLastRefill());
                        patient1.setDateNextRefill(patient.getDateNextRefill());
                        patient1.setPharmacyId(patient.getPharmacyId());
                        //  patient1.setUuid(UUID.randomUUID().toString());
                        Patient patient2 = DDDDb.getInstance(getApplicationContext()).patientRepository().checkIfClientExist(patient.getHospitalNum());
                        if (patient2 != null) {
                            patient1.setId(patient2.getId());
                            DDDDb.getInstance(getApplicationContext()).patientRepository().update(patient1);
                        }
                        FancyToast.makeText(getApplicationContext(), "Patient record update successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
//159357
                        progressdialog.dismiss();

                    }

                } else {
                    FancyToast.makeText(getApplicationContext(), "Contact System administrator ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    progressdialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<org.fhi360.ddd.dto.Response> call, Throwable t) {
                t.printStackTrace();
                FancyToast.makeText(getApplicationContext(), "No Internet Connection", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                progressdialog.dismiss();
            }

        });

    }

    private void updateLastViralLoad() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateLastViralLoad.setText(sdf.format(myCalendar.getTime()));

    }

    private void updateViralLoadDueDate() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        viralLoadDueDate.setText(sdf.format(myCalendar.getTime()));

    }


    private void updateDateOfLastRefill() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateLastRefill.setText(sdf.format(myCalendar.getTime()));

    }

    private void updateDateOfNextRefill() {
        String myFormat = "MM/dd/yyyy";//In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateNextRefill.setText(sdf.format(myCalendar.getTime()));

    }

    private void updateDateOfNxetClinic() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateNextClinic.setText(sdf.format(myCalendar.getTime()));

    }

    private void updateDateOfLastClinic() {
        String myFormat = "MM/dd/yyyy";//In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateLastClinic.setText(sdf.format(myCalendar.getTime()));

    }


    private void updateLabe3() {
        String myFormat = "MM/dd/yyyy";//In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateRegistration.setText(sdf.format(myCalendar.getTime()));

    }


    private void setupFloatingLabelError() {
        final TextInputLayout floatingUsernameLabel = findViewById(R.id.invalidPhone);
        floatingUsernameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 11) {
                    floatingUsernameLabel.setErrorEnabled(false);
                } else {
                    floatingUsernameLabel.setError("Invalid Phone");
                    floatingUsernameLabel.setErrorEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void updateLabe2() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateBirth.setText(sdf.format(myCalendar.getTime()));
//
//        int age1 = 0;
//        try {
//            age1 = getAge(dateBirth.getText().toString());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String checkIfAgeContainsNegative = String.valueOf(age1);
//        if (checkIfAgeContainsNegative.contains("-")) {
//            age.setError("Invalid Age");
//        } else {
//            age.setText(String.valueOf(age1));
//        }
    }

    public static int getAge(String dateOfbirth) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar dob = Calendar.getInstance();
        dob.setTime(sdf.parse(dateOfbirth));

        Calendar today = Calendar.getInstance();
        int curYear = today.get(Calendar.YEAR);
        int dobYear = dob.get(Calendar.YEAR);
        int age = curYear - dobYear;
        int curMonth = today.get(Calendar.MONTH);
        int dobMonth = dob.get(Calendar.MONTH);
        if (dobMonth > curMonth) { // this year can't be counted!
            age--;
        } else if (dobMonth == curMonth) { // same month? check for day
            int curDay = today.get(Calendar.DAY_OF_MONTH);
            int dobDay = dob.get(Calendar.DAY_OF_MONTH);
            if (dobDay > curDay) { // this year can't be counted!
                age--;
            }
        }

        return age;
    }


    private boolean validateInput1(String facilityName1, String hospitalNum2, String
            surname1, String otherName1, String dateBirth1, String dateRegistration) {


        if (facilityName1.isEmpty()) {
            //facilityName.setError("Enter Facility Name");
            FancyToast.makeText(getApplicationContext(), "Enter Facility Name", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;

        }
        if (hospitalNum2.isEmpty()) {
            hospitalNum.setError("Enter Hospital Number");
            FancyToast.makeText(getApplicationContext(), "Enter Hospital Number", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;

        }

        if (dateRegistration.isEmpty()) {
            // facilityName.setError("Enter Date Registration");
            FancyToast.makeText(getApplicationContext(), "Enter Date Registration", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;

        }
//        if (uniqueId1.isEmpty()) {
//            uniqueId.setError("Enter UniqueId");
//            FancyToast.makeText(getApplicationContext(), "Enter UniqueId", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
//            return false;
//
//        }

        if (surname1.isEmpty()) {
            surname.setError("Enter surname");
            FancyToast.makeText(getApplicationContext(), "Enter surname", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;

        } else if (otherName1.isEmpty()) {
            otherNames.setError("Enter othername");
            // hospitalNum.setText(hospitalNum.getText().toString());
            FancyToast.makeText(getApplicationContext(), "Enter other name", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;

        }
        if (dateBirth1.isEmpty()) {
            dateBirth.setError("Enter Date of Birth");
            //hospitalNum.setText(hospitalNum.getText().toString());
            FancyToast.makeText(getApplicationContext(), "Enter Date of Birth", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        }


        return true;


    }


    private void setup(boolean reset, int count) {
        if (reset)
            bar.resetItems();
        bar.setTabCount(count);
        bar.animateView(3000);
        bar.setCurrentPosition(position <= 0 ? 0 : position);
        System.out.println("COUTNINSETUP " + count);
        switch (count) {
            case 1:
                activityNewVisit1.setVisibility(View.VISIBLE);
                activityNewVisit2.setVisibility(View.INVISIBLE);
                activityNewVisit3.setVisibility(View.INVISIBLE);

                break;
            case 2:
                activityNewVisit1.setVisibility(View.INVISIBLE);
                activityNewVisit2.setVisibility(View.VISIBLE);
                activityNewVisit3.setVisibility(View.INVISIBLE);

                break;
            case 3:
                activityNewVisit1.setVisibility(View.INVISIBLE);
                activityNewVisit2.setVisibility(View.INVISIBLE);
                activityNewVisit3.setVisibility(View.VISIBLE);

                break;
//            case 4:
//                activityNewVisit1.setVisibility(View.INVISIBLE);
//                activityNewVisit2.setVisibility(View.INVISIBLE);
//                activityNewVisit3.setVisibility(View.INVISIBLE);
//
//                break;

        }
    }

    @Override
    public void onClick(View v) {
        if (estimatedAge.isChecked() && dateBirth.getText().toString().equals("")) {
            age.setVisibility(View.VISIBLE);
            ageEstimateLayoutl.setVisibility(View.VISIBLE);
        }
        if (estimatedAge.isChecked() && !dateBirth.getText().toString().equals("")) {
            age.setVisibility(View.INVISIBLE);
            ageEstimateLayoutl.setVisibility(View.INVISIBLE);
            FancyToast.makeText(getApplicationContext(), "Age Can't be estimated due to known date of birth ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        }
        if (!estimatedAge.isChecked()) {
            age.setVisibility(View.INVISIBLE);
            ageEstimateLayoutl.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        //saveInstanceState.putBoolean("edit_mode", EDIT_MODE);
        saveInstanceState.putString("patient", new Gson().toJson(patient));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String json = savedInstanceState.getString("patient");
        patient = new Gson().fromJson(json, Patient.class);
    }

    private void restorePreferences() {
        String json = preferences.getString("patient", "");
        patient = new Gson().fromJson(json, Patient.class);
    }


    public void savePin(Long pinCode) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("pharmacyId", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("pharmacyid", String.valueOf(pinCode));
        editor.apply();
    }

    public HashMap<String, String> getPincode() {
        HashMap<String, String> pincode = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("pharmacyId", Context.MODE_PRIVATE);
        pincode.put("pharmacyid", sharedPreferences.getString("pharmacyid", null));
        return pincode;
    }

    private void delete(Long id) {
        ProgressDialog progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("Deleting patient...");
        progressdialog.setCancelable(false);
        progressdialog.setIndeterminate(false);
        progressdialog.setMax(100);
        progressdialog.show();
        ClientAPI clientAPI = APIService.createService(ClientAPI.class);
        Call<Void> objectCall = clientAPI.deletePatient(id);
        objectCall.enqueue(new Callback<Void>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    FancyToast.makeText(getApplicationContext(), "Record Deactivated successfully", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    progressdialog.dismiss();
                } else {
                    FancyToast.makeText(getApplicationContext(), "Contact System administrator ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    progressdialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                FancyToast.makeText(getApplicationContext(), "No Internet Connection", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                progressdialog.dismiss();
            }

        });

    }


}
