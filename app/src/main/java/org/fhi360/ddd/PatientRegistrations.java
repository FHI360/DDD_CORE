
package org.fhi360.ddd;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import com.google.android.material.textfield.TextInputLayout;
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
import retrofit2.Call;
import retrofit2.Callback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class PatientRegistrations extends AppCompatActivity implements View.OnClickListener {
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

    private Button finishButton;
    private ScrollView activityNewVisit1;
    private ScrollView activityNewVisit2;
    private ScrollView activityNewVisit3;

    private NavigationBar bar;
    private int position = 0;
    private Button btn_next, btn_prev;
    private EditText age1;
    int counter = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_registration_home);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorPrimaryDark)));

        activityNewVisit1 = findViewById(R.id.personalDetailsCard1);
        activityNewVisit2 = findViewById(R.id.personalDetailsCard2);
        activityNewVisit3 = findViewById(R.id.personalDetailsCard3);
        bar = findViewById(R.id.navBar);
        btn_next = findViewById(R.id.btn_next);
        btn_prev = findViewById(R.id.btn_prev);


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
        age1 = findViewById(R.id.age);
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
        age1.setVisibility(View.GONE);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Client Registration");
        estimatedAge.setOnClickListener(this);

        lastViralLoad.setText("00");
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                if (counter == 2) {
                    setup(true, counter);
                    btn_prev.setEnabled(true);
                    if (facilityName.getSelectedItem().equals("Select")) {
                        ((TextView) facilityName.getSelectedView()).setError("");
                        FancyToast.makeText(getApplicationContext(), "Select Facility name", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit2.setVisibility(View.GONE);
                    } else if (uniqueId.getText().toString().isEmpty()) {
                        uniqueId.setError("Enter UniqueId");
                        FancyToast.makeText(getApplicationContext(), "Enter UniqueId", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit2.setVisibility(View.GONE);
                    } else if (hospitalNum.getText().toString().isEmpty()) {
                        hospitalNum.setError("Enter Hospital Number");
                        FancyToast.makeText(getApplicationContext(), "Enter Hospital Number", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit2.setVisibility(View.GONE);
                    } else if (surname.getText().toString().isEmpty()) {
                        surname.setError("Enter surname");
                        FancyToast.makeText(getApplicationContext(), "Enter surname", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit2.setVisibility(View.GONE);
                    } else if (otherNames.getText().toString().isEmpty()) {
                        otherNames.setError("Enter othername");
                        FancyToast.makeText(getApplicationContext(), "Enter other name", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit2.setVisibility(View.GONE);
                    }

                }
                if (counter == 3) {
                    setup(true, counter);
                    btn_prev.setEnabled(true);

                    if (dateBirth.getText().toString().isEmpty()) {
                        dateBirth.setError("enter date of birth");
                        FancyToast.makeText(getApplicationContext(), "enter date of birth", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit3.setVisibility(View.GONE);
                    } else if (gender.getSelectedItem().toString().equals("Select")) {
                        ((TextView) gender.getSelectedView()).setError("");
                        FancyToast.makeText(getApplicationContext(), "select sender", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit3.setVisibility(View.GONE);
                    } else if (address.getText().toString().isEmpty()) {
                        address.setError("enter address");
                        FancyToast.makeText(getApplicationContext(), "enter address", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit3.setVisibility(View.GONE);
                    } else if (phone.getText().toString().isEmpty()) {
                        phone.setError("enter phone");
                        FancyToast.makeText(getApplicationContext(), "enter phone", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit3.setVisibility(View.GONE);
                    } else if (lastClinicStage.getSelectedItem().toString().equals("Select")) {
                        otherNames.setError("select clinical stage");
                        FancyToast.makeText(getApplicationContext(), "select clinical stage", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        btn_next.setEnabled(false);
                        activityNewVisit3.setVisibility(View.GONE);
                    }
                    btn_next.setEnabled(false);
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
        //LIB0022
        DDDDb.getInstance(this).pharmacistAccountRepository().deletePharmacy("Rockwood Pharmacy");
        DDDDb.getInstance(this).pharmacistAccountRepository().deletePharmacy("Sampson");
        DDDDb.getInstance(this).pharmacistAccountRepository().deletePharmacy("Duport Road Health Center");
        DDDDb.getInstance(this).pharmacistAccountRepository().deletePharmacyId(29L);
        //Duport Road Health Center
        //Eternal Love Winning Africa (ELWA) Hospitaloy
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateRegistration.setText(dateFormat.format(new Date()));
        List<Long> pharmacyId = new ArrayList();
        List<String> outLetName = new ArrayList();
        List<Pharmacy> account = DDDDb.getInstance(this).pharmacistAccountRepository().findByAll();
        System.out.println("account" + account);
        for (Pharmacy account1 : account) {
            //  id=30, name=Rockwood Pharmacy
            pharmacyId.add(account1.getId());
            outLetName.add(account1.getName());

        }

        final ArrayAdapter pharmacy = new ArrayAdapter<>(PatientRegistrations.this,
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
                final DatePickerDialog mDatePicker = new DatePickerDialog(PatientRegistrations.this, dateNextClinic1, myCalendar
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
                final DatePickerDialog mDatePicker = new DatePickerDialog(PatientRegistrations.this, dateLastClinic1, myCalendar
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

                final DatePickerDialog mDatePicker = new DatePickerDialog(PatientRegistrations.this, dateNextRefill1, myCalendar
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
                final DatePickerDialog mDatePicker = new DatePickerDialog(PatientRegistrations.this, dateLastRefill1, myCalendar
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

                final DatePickerDialog mDatePicker = new DatePickerDialog(PatientRegistrations.this, viralLoadDueDate1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                mDatePicker.show();


            }
        });
        uniqueId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    Patient patient = DDDDb.getInstance(getApplicationContext()).patientRepository().checkIfPatientExistByUniqueId(uniqueId.getText().toString());
                    if (patient != null) {
                        uniqueId.setError("this unique ID is already registered");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                final DatePickerDialog mDatePicker = new DatePickerDialog(PatientRegistrations.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePicker.show();
                //age1.setVisibility(View.VISIBLE);

            }
        });

        dateLastViralLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final DatePickerDialog mDatePicker = new DatePickerDialog(PatientRegistrations.this, dateLastViraLload1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePicker.show();
                myCalendar.add(Calendar.DATE, 365);  // number of days to add
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                viralLoadDueDate.setText(sdf.format(myCalendar.getTime()));
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

        List<Facility> facilityDtoArrayList = DDDDb.getInstance(PatientRegistrations.this).facilityRepository().findAll();
        for (Facility facility : facilityDtoArrayList) {
            arrayListFacilityId.add(facility.getId());
            arrayListFacilityName.add(facility.getName());


        }
        //arrayListFacilityId.add(0, 0);
        //  arrayListFacilityName.add(0, "Select");
        final ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(PatientRegistrations.this,
                R.layout.spinner_items, arrayListFacilityName);
        districtAdapter.setDropDownViewResource(R.layout.spinner_text_color);
        districtAdapter.notifyDataSetChanged();
        facilityName.setAdapter(districtAdapter);
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


        setupFloatingLabelError();
        save.setOnClickListener(new View.OnClickListener() {

            //SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");

            //try {
//
//                String reformattedStr = myFormat.format(fromUser.parse(inputString));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {

                if (validateInput1()) {
                    try {
                        PatientDto patient = new PatientDto();
                        patient.setHospitalNum(hospitalNum.getText().toString());
                        Facility facility = new Facility();
                        HashMap<String, String> facility1 = new PrefManager(PatientRegistrations.this).getFacId();
                        String facId = facility1.get("facId");
                        Long fac = Long.valueOf(facId);
                        System.out.println("FACILITYID " + fac);
                        facility.setId(Long.valueOf(facId));
                        patient.setFacility(facility);
                        patient.setUniqueId(uniqueId.getText().toString());
                        patient.setSurname(surname.getText().toString());
                        patient.setOtherNames(otherNames.getText().toString());
                        patient.setGender(gender.getSelectedItem().toString());
                        String dateBirth1 = dateBirth.getText().toString();
                        patient.setDateBirth(dateBirth1);
                        patient.setAddress(address.getText().toString());
                        patient.setPhone(phone.getText().toString());
                        String dateRegistration1 = dateRegistration.getText().toString();
                        patient.setDateStarted(dateRegistration1);
                        patient.setLastClinicStage(lastClinicStage.getSelectedItem().toString());
                        patient.setLastViralLoad(Double.parseDouble(lastViralLoad.getText().toString()));
                        String dateLastViralLoad1 = dateLastViralLoad.getText().toString();
                        patient.setDateLastViralLoad(dateLastViralLoad1);
                        String viralLoadDueDate1 = viralLoadDueDate.getText().toString();
                        patient.setViralLoadDueDate(viralLoadDueDate1);
                        patient.setViralLoadType(viralLoadType.getSelectedItem().toString());
                        String dateLastClinic1 = dateLastClinic.getText().toString();
                        patient.setDateLastClinic(dateLastClinic1);
                        String dateNextClinic1 = dateNextClinic.getText().toString();
                        patient.setDateNextClinic(dateNextClinic1);
                        String dateLastRefill1 = dateLastRefill.getText().toString();
                        patient.setDateLastRefill(dateLastRefill1);
                        String dateNextRefill1 = dateNextRefill.getText().toString();
                        patient.setDateNextRefill(dateNextRefill1);
                        HashMap<String, String> pinCode = getPincode();
                        String pharmacyid = pinCode.get("pharmacyid");
                        patient.setPharmacyId(Long.valueOf(pharmacyid));

                        save(patient);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });


    }


    private void save(PatientDto patientDto) {

        ProgressDialog progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("Saving patient...");
        progressdialog.setCancelable(false);
        progressdialog.setIndeterminate(false);
        progressdialog.setMax(100);
        progressdialog.show();
        ClientAPI clientAPI = APIService.createService(ClientAPI.class);
        System.out.println("Patient DTO " + patientDto);
        Call<Response> objectCall = clientAPI.savePatient(patientDto);
        objectCall.enqueue(new Callback<Response>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<org.fhi360.ddd.dto.Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    org.fhi360.ddd.dto.Response response1 = response.body();
                    if (Objects.requireNonNull(response1).getPatient() != null) {
                        PatientDto patient = response1.getPatient();
                        Patient patient1 = new Patient();
                        patient1.setId(patient1.getId());
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
                        patient1.setUuid(UUID.randomUUID().toString());

                        DDDDb.getInstance(getApplicationContext()).patientRepository().save(patient1);
                        FancyToast.makeText(getApplicationContext(), "Patient record save successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        hospitalNum.setText("");
                        uniqueId.setText("");
                        //facilityName.setText("");
                        surname.setText("");
                        otherNames.setText("");
                        dateBirth.setText("");
                        address.setText("");
                        SettingConfig.setSpinText(gender, "");
                        phone.setText("");
                        dateLastViralLoad.setText("");
                        viralLoadDueDate.setText("");
                        lastViralLoad.setText("");
                        dateLastClinic.setText("");
                        dateNextClinic.setText("");
                        dateNextRefill.setText("");
                        dateLastRefill.setText("");
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
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateNextRefill.setText(sdf.format(myCalendar.getTime()));

    }

    private void updateDateOfNxetClinic() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateNextClinic.setText(sdf.format(myCalendar.getTime()));

    }

    private void updateDateOfLastClinic() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateLastClinic.setText(sdf.format(myCalendar.getTime()));

    }


    private void updateLabe3() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
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
        age1.setVisibility(View.VISIBLE);


        int age2 = 0;
        try {
            age2 = getAge(dateBirth.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        String checkIfAgeContainsNegative = String.valueOf(age2);
        if (checkIfAgeContainsNegative.contains("-")) {
            age1.setError("Invalid Age");
        } else {
            age1.setText(String.valueOf(age2));
        }
    }

    public int getAge(String dateOfbirth) throws Exception {
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


    private boolean validateInputCounter2(String uniqueId1, String facilityName1, String hospitalNum2, String dateRegistration, String
            surname1, String otherName1) {
        if (uniqueId1.isEmpty()) {
            uniqueId.setError("Enter UniqueId");
            FancyToast.makeText(getApplicationContext(), "Enter UniqueId", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;

        }

        if (facilityName1.equals("Select")) {
            ((TextView) facilityName.getSelectedView()).setError("Select Facility name");
            FancyToast.makeText(getApplicationContext(), "Enter Facility Name", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        }
        if (hospitalNum2.isEmpty()) {
            hospitalNum.setError("Enter Hospital Number");
            FancyToast.makeText(getApplicationContext(), "Enter Hospital Number", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        }

        if (dateRegistration.isEmpty()) {

            FancyToast.makeText(getApplicationContext(), "Enter Date Registration", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;

        }

        if (surname1.isEmpty()) {
            surname.setError("Enter surname");
            FancyToast.makeText(getApplicationContext(), "Enter surname", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;

        } else if (otherName1.isEmpty()) {
            otherNames.setError("Enter othername");
            FancyToast.makeText(getApplicationContext(), "Enter other name", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;

        }


        return true;


    }

    private boolean validateInput1() {


        if (dateLastViralLoad.getText().toString().isEmpty()) {
            dateLastViralLoad.setError("enter date last viral load");
            FancyToast.makeText(getApplicationContext(), "date last viral load", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
            //activityNewVisit3.setVisibility(View.GONE);
        } else if (viralLoadDueDate.getText().toString().isEmpty()) {
            uniqueId.setError("enter viral due date");
            FancyToast.makeText(getApplicationContext(), "viral due date", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
            // activityNewVisit3.setVisibility(View.GONE);
        } else if (viralLoadType.getSelectedItem().toString().isEmpty()) {
            address.setError("enter viral type");
            FancyToast.makeText(getApplicationContext(), "viral type", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
            // activityNewVisit3.setVisibility(View.GONE);
        } else if (dateLastRefill.getText().toString().isEmpty()) {
            phone.setError("enter date last refill");
            FancyToast.makeText(getApplicationContext(), "date last refill", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
            // activityNewVisit3.setVisibility(View.GONE);
        } else if (dateNextRefill.getText().toString().isEmpty()) {
            otherNames.setError("enter date next refill");
            FancyToast.makeText(getApplicationContext(), "date next refill", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
            //    activityNewVisit3.setVisibility(View.GONE);
        } else if (dateLastClinic.getText().toString().isEmpty()) {
            otherNames.setError("enter date last clinic");
            FancyToast.makeText(getApplicationContext(), "enter date last clinic", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (dateNextClinic.getText().toString().isEmpty()) {
            otherNames.setError("enter date next clinic");
            FancyToast.makeText(getApplicationContext(), "enter date next clinic", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (dddoutlet.getSelectedItem().toString().isEmpty()) {
            otherNames.setError("select outlet");
            FancyToast.makeText(getApplicationContext(), "select outlet", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        }


        return true;


    }

    @Override
    public void onClick(View v) {
        if (estimatedAge.isChecked() && dateBirth.getText().toString().equals("")) {
            age.setVisibility(View.VISIBLE);
            dateBirth.setVisibility(View.GONE);
            ageEstimateLayoutl.setVisibility(View.VISIBLE);
        }
        if (estimatedAge.isChecked() && !dateBirth.getText().toString().equals("")) {
            age.setVisibility(View.INVISIBLE);
            ageEstimateLayoutl.setVisibility(View.INVISIBLE);
            FancyToast.makeText(getApplicationContext(), "Age Can't be unknown due to known date of birth ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
        }
        if (!estimatedAge.isChecked()) {
            age.setVisibility(View.INVISIBLE);
            ageEstimateLayoutl.setVisibility(View.INVISIBLE);
            dateBirth.setVisibility(View.VISIBLE);
        }
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

    public static <T> List<T> removeDuplicates(List<T> list) {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
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


}
