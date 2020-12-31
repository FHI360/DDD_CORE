package org.fhi360.ddd;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.library.NavigationBar;
import com.library.NvTab;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.fhi360.ddd.Db.DDDDb;
import org.fhi360.ddd.domain.ARV;
import org.fhi360.ddd.domain.Devolve;
import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.domain.Patient;
import org.fhi360.ddd.domain.PreLoadRegimen;
import org.fhi360.ddd.domain.Regimen;
import org.fhi360.ddd.dto.Response;
import org.fhi360.ddd.util.DateUtil;
import org.fhi360.ddd.util.EditTextDatePicker;
import org.fhi360.ddd.util.SpinnerUtil;
import org.fhi360.ddd.webservice.APIService;
import org.fhi360.ddd.webservice.ClientAPI;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Query;

import static java.lang.Math.round;
import static org.fhi360.ddd.util.Constants.PREFERENCES_ENCOUNTER;


public class ARVRefill extends AppCompatActivity {
    private int id;
    private Long patientId;
    private Date dateVisit;
    private String adverseIssue;
    private String regimen1;
    private String regimen2;
    private String regimen3;
    private String regimen4;
    private Integer duration1;
    //    private Integer duration2;
//    private Integer duration3;
//    private Integer duration4;
    private Integer prescribed1;
    //    private Integer prescribed2;
//    private Integer prescribed3;
//    private Integer prescribed4;
    private Integer dispensed1;
    //    private Integer dispensed2;
//    private Integer dispensed3;
//    private Integer dispensed4;
    private Date nextRefill;
    private Spinner anyAdverseReport;
    private String regimentype;
    private Spinner medicine1;
    //    private Spinner medicine2;
//    private Spinner medicine3;
    private EditText medicine4;
    private Patient patient;
    private boolean EDIT_MODE;
    private SharedPreferences preferences;
    private ScrollView activity_step_one;
    private ScrollView activity_step_two;
    private ScrollView activity_step_three;
    private ScrollView activity_step_four;
    private NavigationBar bar;
    private int position = 0;
    private Spinner spinner;
    private Double bodyWeight;
    private Double height;
    private Double bmi;
    private String bmiCategory;
    private Double muac;
    private String muacCategory;
    private String supplementaryFood;
    private String nutritionalStatusReferred;
    private EditText bodyWeightEditText;
    private EditText heightEditText;
    private LinearLayout layoutAdult;
    private LinearLayout layoutPediatrics;
    private LinearLayout layoutSupplement;
    private Double bp;
    EditText vital;
    EditText vital1;
    EditText dateVisit1, temprature, howmany, viral_load_deu_date, addversreactiontext,
            dateStartedTbTreatment, dateNextRefill, bodyWeight1, bp1,
            duration, quantityPrescribed, quantityDispensed,
            duration4, quantityPrescribed4, quantityDispensed4;

    ;
    private Calendar myCalendar = Calendar.getInstance();
    private Spinner missedanyRefil, adverseIssue1, itp,
            haveYouBeenCoughing, doYouHaveFever,
            areYouLosingWeight, areYouHavingSweet, tbReferred, eligibleIpt,
            doYouHaveSwellingNeck;
    private Button saveButton;
    private TextInputLayout howmanyTextInputLayout, addversreactiontext1;
    private LinearLayout outcome_tb, outcome_ipt;
    private ProgressDialog mPb;
    private Button btn_next, btn_prev;
    int counter = 1;

    @SuppressLint({"CutPasteId", "SimpleDateFormat"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        this.preferences = getSharedPreferences(PREFERENCES_ENCOUNTER, 0);
        restorePreferences();
        if (savedInstanceState != null) {
            String json = savedInstanceState.getString("patient");
            patient = new Gson().fromJson(json, Patient.class);
        }
        activity_step_one = findViewById(R.id.activity_step_one);
        activity_step_two = findViewById(R.id.activity_step_two);
        activity_step_three = findViewById(R.id.activity_step_three);
        activity_step_four = findViewById(R.id.activity_step_four);

        bar = findViewById(R.id.navBar);
        btn_next = findViewById(R.id.btn_next);
        btn_prev = findViewById(R.id.btn_prev);
        //enterdrug = findViewById(R.id.enterdrug);
        dateVisit1 = findViewById(R.id.date_visit);
        dateNextRefill = findViewById(R.id.next_refill);
        //  tb_treatment_yes = findViewById(R.id.tb_treatment_yes);
        // tb_treatment_no = findViewById(R.id.tb_treatment_no);
        outcome_ipt = findViewById(R.id.outcome_ipt);

        // tb_treatment_yes.setVisibility(View.INVISIBLE);
        //tb_treatment_no.setVisibility(View.INVISIBLE);
        outcome_ipt.setVisibility(View.INVISIBLE);
        //   enterdrug.setVisibility(View.INVISIBLE);

        bodyWeight1 = findViewById(R.id.body_weight);
        //date_next_clinic_visit = findViewById(R.id.date_next_clinic_visit);
        viral_load_deu_date = findViewById(R.id.viral_load_deu_date);
        bp1 = findViewById(R.id.bp2);
        itp = findViewById(R.id.ipt);
        //tbTreatment = findViewById(R.id.tb_treatment);
        //dateStartedTbTreatment = findViewById(R.id.date_started_tb_treatment);
        haveYouBeenCoughing = findViewById(R.id.have_you_been_coughing);
        doYouHaveFever = findViewById(R.id.do_you_have_fever);
        areYouLosingWeight = findViewById(R.id.are_you_losing_weight);
        areYouHavingSweet = findViewById(R.id.are_you_having_sweet);
        doYouHaveSwellingNeck = findViewById(R.id.do_you_have_swelling_neck);
        tbReferred = findViewById(R.id.tb_referred);
        eligibleIpt = findViewById(R.id.eligible_ipt);
        duration = findViewById(R.id.duration1);
        quantityPrescribed = findViewById(R.id.prescribed1);
        quantityDispensed = findViewById(R.id.dispensed1);
        //  duration22 = findViewById(R.id.duration2);
        //  prescribed22 = findViewById(R.id.prescribed2);
        //  dispensed22 = findViewById(R.id.dispensed1);
        // duration32 = findViewById(R.id.duration3);
        //prescribed32 = findViewById(R.id.prescribed3);
        // dispensed32 = findViewById(R.id.dispensed3);
        duration4 = findViewById(R.id.duration4);
        quantityPrescribed4 = findViewById(R.id.prescribed4);
        quantityDispensed4 = findViewById(R.id.prescribed4);
        adverseIssue1 = findViewById(R.id.adverseIssue);
        missedanyRefil = findViewById(R.id.missedanyRefil);
        temprature = findViewById(R.id.temprature);

        howmany = findViewById(R.id.howmany);
        saveButton = findViewById(R.id.save_button);

        howmanyTextInputLayout = findViewById(R.id.howmanyText);
        addversreactiontext1 = findViewById(R.id.addversreactiontext1);
        medicine1 = findViewById(R.id.regimen1);
        // medicine2 = findViewById(R.id.regimen2);
        // medicine3 = findViewById(R.id.regimen3);
        medicine4 = findViewById(R.id.regimen4);
        vital1 = findViewById(R.id.body_weight);
        addversreactiontext = findViewById(R.id.addversreactiontext);

        addItemsMedicineSpinners();
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_yes_no, R.layout.spinner_text_color);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.hintcolour);
        // Apply the adapter to the spinner
        // tbTreatment.setAdapter(adapter);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> haveYouAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_yes_no, R.layout.spinner_text_color);

        // Specify the layout to use when the list of choices appears
        haveYouAdapter.setDropDownViewResource(R.layout.hintcolour);
        // Apply the adapter to the spinner
        haveYouBeenCoughing.setAdapter(haveYouAdapter);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> doYouHaveFeverAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_yes_no, R.layout.spinner_text_color);

        // Specify the layout to use when the list of choices appears
        doYouHaveFeverAdapter.setDropDownViewResource(R.layout.hintcolour);
        // Apply the adapter to the spinner
        doYouHaveFever.setAdapter(doYouHaveFeverAdapter);


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> areYouLossingWeightAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_yes_no, R.layout.spinner_text_color);

        // Specify the layout to use when the list of choices appears
        areYouLossingWeightAdapter.setDropDownViewResource(R.layout.hintcolour);
        // Apply the adapter to the spinner
        areYouLosingWeight.setAdapter(areYouLossingWeightAdapter);

        ArrayAdapter<CharSequence> areYouHavingSweetAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_yes_no, R.layout.spinner_text_color);

        // Specify the layout to use when the list of choices appears
        areYouHavingSweetAdapter.setDropDownViewResource(R.layout.hintcolour);
        // Apply the adapter to the spinner
        areYouHavingSweet.setAdapter(areYouHavingSweetAdapter);


        ArrayAdapter<CharSequence> doYouHaveSwellingNeckAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_yes_no, R.layout.spinner_text_color);

        // Specify the layout to use when the list of choices appears
        doYouHaveSwellingNeckAdapter.setDropDownViewResource(R.layout.hintcolour);
        // Apply the adapter to the spinner
        doYouHaveSwellingNeck.setAdapter(doYouHaveSwellingNeckAdapter);


        ArrayAdapter<CharSequence> tbReferredAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_yes_no, R.layout.spinner_text_color);

        // Specify the layout to use when the list of choices appears
        tbReferredAdapter.setDropDownViewResource(R.layout.hintcolour);
        // Apply the adapter to the spinner
        tbReferred.setAdapter(tbReferredAdapter);


        ArrayAdapter<CharSequence> eligiableAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_yes_no, R.layout.spinner_text_color);

        // Specify the layout to use when the list of choices appears
        eligiableAdapter.setDropDownViewResource(R.layout.hintcolour);
        // Apply the adapter to the spinner
        eligibleIpt.setAdapter(eligiableAdapter);

//        tbTreatment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (tbTreatment.getSelectedItem().equals("Yes")) {
//                    tb_treatment_yes.setVisibility(View.VISIBLE);
//                    tb_treatment_no.setVisibility(View.INVISIBLE);
//                    outcome_ipt.setVisibility(View.INVISIBLE);
//                    enterdrug.setVisibility(View.VISIBLE);
//                } else if (tbTreatment.getSelectedItem().equals("No")) {
//                    tb_treatment_yes.setVisibility(View.VISIBLE);
//                    tb_treatment_no.setVisibility(View.VISIBLE);
//                    outcome_ipt.setVisibility(View.VISIBLE);
//                    enterdrug.setVisibility(View.INVISIBLE);
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        adverseIssue1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (adverseIssue1.getSelectedItem().equals("Yes")) {
                    addversreactiontext.setVisibility(View.VISIBLE);
                } else if (adverseIssue1.getSelectedItem().equals("No")) {
                    addversreactiontext.setVisibility(View.INVISIBLE);
                    addversreactiontext1.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        missedanyRefil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (missedanyRefil.getSelectedItem().equals("Yes")) {
                    howmany.setVisibility(View.VISIBLE);
                } else if (missedanyRefil.getSelectedItem().equals("No")) {
                    howmany.setVisibility(View.INVISIBLE);
                    howmanyTextInputLayout.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        if (patient.getViralLoadDueDate() != null) {
            viral_load_deu_date.setText(patient.getViralLoadDueDate());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateVisit1.setText(dateFormat.format(new Date()));


        duration.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    myCalendar.setTime(Objects.requireNonNull(sdf.parse(dateFormat.format(new Date()))));
                    if (duration.getText().toString().equals("")) {

                    } else {
                        myCalendar.add(Calendar.DATE, Integer.parseInt(duration.getText().toString()));  // number of days to add
                        dateNextRefill.setText(sdf.format(myCalendar.getTime()));

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

//12-07-2022
//        prescribed11.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {
//                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//                try {
//                    myCalendar.setTime(Objects.requireNonNull(sdf.parse(dateFormat.format(new Date()))));
//                    if (prescribed11.getText().toString().equals("")) {
//
//                    } else if (prescribed11.getText().toString().equals("TDF+3TC+DTG(300+300+50mg)")) {
//                        String name, int basicUnit, ,int qtyrecieved, int dispense
//                        int balance = 20;
//                        int qtyPrescribed = Integer.parseInt(prescribed11.getText().toString()) - 20;
//                        int qtyDispensed = Integer.parseInt(prescribed11.getText().toString()) - 20;
//                        //Recievd and Balance is th
//                        new PrefManager(getApplicationContext()).saveDrug1("TDF+3TC+DTG(300+300+50mg)", 30, 20, qtyPrescribed );
//                    }
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput(dateVisit1.getText().toString(), duration.getText().toString(), quantityDispensed.getText().toString(), quantityDispensed.getText().toString(), dateNextRefill.getText().toString())) {
                    ARV arv = new ARV();
                    System.out.println("PATIENT one ");
                    String json = preferences.getString("patient", "");
                    patient = new Gson().fromJson(json, Patient.class);
                    System.out.println("patient OBJECT " + patient);
                    patientId = patient.getId();
                    Patient patient1 = new Patient();
                    patient1.setId(patientId);
                    arv.setPatient(patient1);
                    arv.setDateVisit(dateVisit1.getText().toString());
                    arv.setDateNextRefill(dateNextRefill.getText().toString());
                    arv.setBodyWeight(bodyWeight1.getText().toString());
                    arv.setBp(bp1.getText().toString());
                    arv.setFacilityId(patient.getFacilityId());

                    arv.setHaveYouBeenCoughing(haveYouBeenCoughing.getSelectedItem().toString());
                    arv.setDoYouHaveFever(doYouHaveFever.getSelectedItem().toString());
                    arv.setAreYouLosingWeight(areYouLosingWeight.getSelectedItem().toString());
                    arv.setAreYouHavingSweet(areYouHavingSweet.getSelectedItem().toString());
                    arv.setDoYouHaveSwellingNeck(doYouHaveSwellingNeck.getSelectedItem().toString());
                    arv.setTbReferred(tbReferred.getSelectedItem().toString());
                    HashMap<String, String> regime1 = getRegime1();
                    String regimen1Id = regime1.get("id");
                    System.out.println("REGIMEN one " + regimen1Id);
                    arv.setRegimen1(Long.valueOf(regimen1Id));

                    if (duration.getText().toString().equals("")) {
                        arv.setDuration1(0);
                    } else {
                        arv.setDuration1(Integer.parseInt(duration.getText().toString()));
                    }
                    arv.setPrescribed1(quantityPrescribed.getText().toString());
                    arv.setDispensed1(quantityDispensed.getText().toString());
//                    HashMap<String, String> cotrimazole = getGetCotrimazole();
//                    String cotrimazoleId = cotrimazole.get("id");
//                    System.out.println("REGIMEN two " + regimen1Id);
//                    arv.setRegimen2(Long.valueOf(cotrimazoleId));
//                    if (duration22.getText().toString().equals("")) {
//                        arv.setDuration2(0);
//                    } else {
//                        arv.setDuration2(Integer.parseInt(duration22.getText().toString()));
//                    }
//                    arv.setPrescribed2(prescribed22.getText().toString());
                    //arv.setDispensed2(dispensed22.getText().toString());
//                    HashMap<String, String> ipt = getGetIpt();
//                    String iptid = ipt.get("id");
//                    System.out.println("REGIMEN three " + iptid);
//                    arv.setRegimen3(Long.valueOf(iptid));
//                    if (duration32.getText().toString().equals("")) {
//                        arv.setDuration3(0);
//                    } else {
//                        arv.setDuration3(Integer.parseInt(duration32.getText().toString()));
//                    }
//                    arv.setPrescribed3(prescribed32.getText().toString());
                    arv.setRegimen4(medicine4.getText().toString());
                    if (duration4.getText().toString().equals("")) {
                        arv.setDuration4(0);
                    } else {
                        arv.setDuration4(Integer.parseInt(duration4.getText().toString()));
                    }
                    arv.setTemperature(temprature.getText().toString());
                    arv.setPrescribed4(quantityPrescribed4.getText().toString());
                    arv.setDispensed4(quantityDispensed4.getText().toString());
                    arv.setAdverseReport(addversreactiontext.getText().toString());
                    arv.setAdverseIssue(adverseIssue1.getSelectedItem().toString());
                    if (patient.getDateNextClinic() != null) {
                        arv.setDateNextClinic(patient.getDateNextClinic());
                    }
                    arv.setViralLoadDeuDate(viral_load_deu_date.getText().toString());
                    arv.setMissedRefill(missedanyRefil.getSelectedItem().toString());
                    arv.setHowMany(howmany.getText().toString());
                    arv.setUuid(UUID.randomUUID().toString());
                    DDDDb.getInstance(ARVRefill.this).patientRepository().updateDateNextRefil(dateNextRefill.getText().toString(), patient.getId());
                    Devolve devolve = DDDDb.getInstance(ARVRefill.this).devolveRepository().findByPatient(patient.getFacilityId(), patient.getId());
                    if (devolve != null) {
                        arv.setReasonDiscontinued(devolve.getReasonDiscontinued());
                        arv.setDiscontinued(1);
                        arv.setDateDiscontinued(devolve.getDateDiscontinued());
                    }
                    saveARV(arv);
                    update(dateNextRefill.getText().toString(), patientId);
                    DDDDb.getInstance(ARVRefill.this).arvRefillRepository().save(arv);
                    FancyToast.makeText(getApplicationContext(), "ARV Refill saved successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                    Intent intent = new Intent(ARVRefill.this, PatientList.class);
                    startActivity(intent);
                }
            }
        });

        dateVisit1 = findViewById(R.id.date_visit);
        final DatePickerDialog.OnDateSetListener dateVisit2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateOfDateVisit();
            }

        };

        dateVisit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog mDatePicker = new DatePickerDialog(ARVRefill.this, dateVisit2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                mDatePicker.show();
            }
        });

        final DatePickerDialog.OnDateSetListener dateStartedTbTreatment1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateStarted();
            }

        };

//        dateStartedTbTreatment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final DatePickerDialog mDatePicker = new DatePickerDialog(ARVRefill.this, dateStartedTbTreatment1, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH));
//                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
//                mDatePicker.show();
//            }
//        });
        final DatePickerDialog.OnDateSetListener dateNextRefill1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateNextRefill1Update();
            }

        };

        dateNextRefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog mDatePicker = new DatePickerDialog(ARVRefill.this, dateNextRefill1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.show();
            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                System.out.println("counter " + counter);
                if (counter <= 4) {
                    System.out.println("counter 2 " + counter);
                    setup(true, counter);
                    btn_prev.setEnabled(true);
                    if (counter == 4) {
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

        String json = preferences.getString("patient", "");
        patient = new Gson().fromJson(json, Patient.class);

        vital1.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1 || s.length() == 3) {

                } else {
                    vital.setError("Body weight is between 1 and 250");
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });


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
                activity_step_one.setVisibility(View.VISIBLE);
                activity_step_three.setVisibility(View.INVISIBLE);
                activity_step_two.setVisibility(View.INVISIBLE);
                activity_step_four.setVisibility(View.INVISIBLE);
                break;
            case 2:
                activity_step_one.setVisibility(View.INVISIBLE);
                activity_step_three.setVisibility(View.VISIBLE);
                activity_step_two.setVisibility(View.INVISIBLE);
                activity_step_four.setVisibility(View.INVISIBLE);
                break;
            case 3:
                activity_step_one.setVisibility(View.INVISIBLE);
                activity_step_three.setVisibility(View.INVISIBLE);
                activity_step_two.setVisibility(View.VISIBLE);
                activity_step_four.setVisibility(View.INVISIBLE);
                break;

            case 4:
                activity_step_one.setVisibility(View.INVISIBLE);
                activity_step_three.setVisibility(View.INVISIBLE);
                activity_step_two.setVisibility(View.INVISIBLE);
                activity_step_four.setVisibility(View.VISIBLE);
                break;

        }


    }

    public void addItemsMedicineSpinners() {
        //Get ARV
        ArrayList firstLineRegimentName = new ArrayList();
        ArrayList firstLineRegimentId = new ArrayList();
        List<Regimen> firstLineRegiment = DDDDb.getInstance(this).regimenRepository().getARV1();
        System.out.println("REGIMEN" + firstLineRegiment);
        for (Regimen preLoadRegimen : firstLineRegiment) {
            firstLineRegimentName.add(preLoadRegimen.getName());
            firstLineRegimentId.add(preLoadRegimen.getId());
        }
        // firstLineRegimentName.add(0, "Select");
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.spinner_text_color, firstLineRegimentName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicine1.setAdapter(adapter);
        medicine1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Long reid = (Long) firstLineRegimentId.get(position);
                saveRegimen1(reid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        //Get Cotrim
//        Long regimentypeId = 8L;
//        List<Regimen> cotrimazole = DDDDb.getInstance(this).regimenRepository().getRegimens(regimentypeId);
//        ArrayList cotrimazoleName = new ArrayList();
//        ArrayList cotrimazoleId = new ArrayList();
//        for (Regimen preLoadRegimen : cotrimazole) {
//            cotrimazoleName.add(preLoadRegimen.getName());
//            cotrimazoleId.add(preLoadRegimen.getId());
//        }
//        adapter = new ArrayAdapter<>(this, R.layout.spinner_text_color, cotrimazoleName);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        medicine2.setAdapter(adapter);
//        medicine2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Long cotrimazoleId1 = (Long) cotrimazoleId.get(position);
//                saveCotrimazole(cotrimazoleId1);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        //Get IPT
//        regimentypeId = 15L;
//        List<Regimen> ipt = DDDDb.getInstance(this).regimenRepository().getRegimens(regimentypeId);
//        ArrayList iptName = new ArrayList();
//        ArrayList iptId = new ArrayList();
//        for (Regimen preLoadRegimen : ipt) {
//            iptId.add(preLoadRegimen.getId());
//            iptName.add(preLoadRegimen.getName());
//
//        }
//        adapter = new ArrayAdapter<>(this, R.layout.spinner_text_color, iptName);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        medicine3.setAdapter(adapter);
//        medicine3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Long iptId1 = (Long) iptId.get(position);
//                saveIpt(iptId1);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


//        //Get other medicines
//        List<String> otherMedicines = DDDDb.getInstance(this).regimenRepository().getOtherMedicines();
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, otherMedicines);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        medicine4.setAdapter(adapter);

    }


    public void restorePreferences() {
        EDIT_MODE = preferences.getBoolean("edit_mode", false);
        String json = preferences.getString("patient", "");
        patient = new Gson().fromJson(json, Patient.class);
        patientId = patient.getId();

    }


    private void updateDateOfDateVisit() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateVisit1.setText(sdf.format(myCalendar.getTime()));

    }


    private void updateDateStarted() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateStartedTbTreatment.setText(sdf.format(myCalendar.getTime()));

    }

    private boolean validateInput(String date1, String duration1, String priscribed, String qytyDispensed, String date2) {
        if (date1.isEmpty()) {
            dateVisit1.setError("date visit can not be empty");
            return false;
        } else if (date2.isEmpty()) {
            dateNextRefill.setError("next refill can not be empty");
            return false;

        } else if (duration1.isEmpty()) {
            duration.setError("duration can not be empty");
            return false;

        } else if (priscribed.isEmpty()) {
            quantityPrescribed.setError("quantity prescribed can not be empty");
            return false;

        } else if (qytyDispensed.isEmpty()) {
            quantityDispensed.setError("quantity dispensed can not be empty");
            return false;

        }
        return true;


    }

    private void dateNextRefill1Update() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateNextRefill.setText(sdf.format(myCalendar.getTime()));

    }

    public void saveRegimen1(Long id) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("regimen1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("id", String.valueOf(id));
        editor.apply();
    }

    public HashMap<String, String> getRegime1() {
        HashMap<String, String> pincode = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("regimen1", Context.MODE_PRIVATE);
        pincode.put("id", sharedPreferences.getString("id", null));
        return pincode;
    }


    public void saveCotrimazole(Long id) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("cotrimazole", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("id", String.valueOf(id));
        editor.apply();
    }

    public HashMap<String, String> getGetCotrimazole() {
        HashMap<String, String> pincode = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("cotrimazole", Context.MODE_PRIVATE);
        pincode.put("id", sharedPreferences.getString("id", null));
        return pincode;
    }

    public void saveIpt(Long id) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("ipt", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("id", String.valueOf(id));
        editor.apply();
    }

    public HashMap<String, String> getGetIpt() {
        HashMap<String, String> pincode = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("ipt", Context.MODE_PRIVATE);
        pincode.put("id", sharedPreferences.getString("id", null));
        return pincode;
    }

    private void saveARV(ARV arv) {
        mPb = new ProgressDialog(ARVRefill.this);
        mPb.setProgress(0);
        mPb.setMessage("ARV saving, please wait...");
        mPb.setCancelable(false);
        mPb.setIndeterminate(false);
        mPb.setProgress(0);
        mPb.setMax(100);
        mPb.show();
        ClientAPI clientAPI = APIService.createService(ClientAPI.class);
        Call<Response> objectCall = clientAPI.saveARVRefill(arv);
        objectCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {

                    mPb.dismiss();
                } else {
                    mPb.dismiss();
                    // FancyToast.makeText(getApplicationContext(), "Syn was not successful to Server ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                t.printStackTrace();
                // FancyToast.makeText(getApplicationContext(), "No internet connection ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                mPb.dismiss();
            }

        });

    }

    private void update(String dateNextRefill,
                        Long id) {

        ClientAPI clientAPI = APIService.createService(ClientAPI.class);
        Call<Void> objectCall = clientAPI.update(dateNextRefill, id);
        objectCall.enqueue(new Callback<Void>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
//                    FancyToast.makeText(getApplicationContext(), "Record Deactivated successfully", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
//                    progressdialog.dismiss();
                } else {
                    FancyToast.makeText(getApplicationContext(), "Contact System administrator ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    //progressdialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                FancyToast.makeText(getApplicationContext(), "No Internet Connection", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                //  progressdialog.dismiss();
            }

        });

    }

}


