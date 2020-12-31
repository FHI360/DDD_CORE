package org.fhi360.ddd;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shashank.sony.fancytoastlib.FancyToast;

import org.fhi360.ddd.Db.DDDDb;
import org.fhi360.ddd.domain.District;
import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.domain.Pharmacy;
import org.fhi360.ddd.domain.Regimen;
import org.fhi360.ddd.domain.State;
import org.fhi360.ddd.domain.User;
import org.fhi360.ddd.dto.Data;
import org.fhi360.ddd.dto.PharmacyDto;
import org.fhi360.ddd.util.PrefManager;
import org.fhi360.ddd.webservice.ClientAPI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivation extends AppCompatActivity {
    //  private EditText otp_view;
    PrefManager prefManager;

    String androidId;
    ProgressDialog progressdialog;
    TextView serviceProvided;
    EditText notitoptxt;
    Button activate;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_activation);
//        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources()
//                .getColor(R.color.colorPrimaryDark)));

        //  otp_view = findViewById(R.id.code);
        activate = findViewById(R.id.activate_button);
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        prefManager = new PrefManager(getApplicationContext());

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // HashMap<String, String> role = getRole();
        //   String user = role.get("role");
//        if (Objects.requireNonNull(user).equalsIgnoreCase("DDD outlet")) {
//            Intent streamPlayerHome = new Intent(getApplicationContext(), OutletWelcome.class);
//            startActivity(streamPlayerHome);
//            finish();
//        } else {
        List<Facility> states = DDDDb.getInstance(getApplicationContext()).facilityRepository().findAll();
        if (states.isEmpty()) {
            activate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressdialog = new ProgressDialog(AdminActivation.this);
                    progressdialog.setMessage("Initializing DDD please wait...");
                    progressdialog.setCancelable(false);
                    progressdialog.setIndeterminate(false);
                    progressdialog.setMax(100);
                    progressdialog.show();
                    getRecordsFromDDDAPI(androidId);


                }
            });
        } else {
            Intent streamPlayerHome = new Intent(getApplicationContext(), AdminHomePage.class);
            startActivity(streamPlayerHome);
            finish();
        }
        // }

    }


    private void getRecordsFromDDDAPI(String diviceId) {
        ClientAPI clientAPI = org.fhi360.ddd.webservice.APIService.createService(ClientAPI.class);
        User user = DDDDb.getInstance(getApplicationContext()).userRepository().findByOne();
        String accountuserName = user.getUsername();
        String passwords = user.getPassword();
        Call<Data> objectCall = clientAPI.getFacilityCode(diviceId, accountuserName, passwords);
        objectCall.enqueue(new Callback<Data>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response.isSuccessful()) {
                    Data dataObject = response.body();
                    List<Facility> facility = Objects.requireNonNull(dataObject).getFacility();
                    for (Facility facility1 : facility) {
                        Facility facility2 = new Facility();
                        facility2.setId(facility1.getId());
                        facility2.setName(facility1.getName());
                        facility2.setStateId(facility1.getStateId());
                        facility2.setDistrictId(facility1.getDistrictId());
                        Long id = DDDDb.getInstance(getApplicationContext()).facilityRepository().save(facility2);
                        System.out.println("RETURN ID" + id);

                    }

                    List<State> state = dataObject.getState();
                    state.forEach(state1 -> {
                        DDDDb.getInstance(getApplicationContext()).stateRepository().save(state1);
                    });


                    List<District> districtDtos = dataObject.getDistrict();
                    for (District district : districtDtos) {

                        DDDDb.getInstance(getApplicationContext()).districtRepository().save(district);
                    }

                    List<Regimen> regimen = dataObject.getRegimens();
                    for (Regimen regimen1 : regimen) {
                        DDDDb.getInstance(getApplicationContext()).regimenRepository().save(regimen1);
                    }
                    List<PharmacyDto> communityPharmacies = dataObject.getCommunityPharmacies();
                    for (PharmacyDto pharmacyDto : communityPharmacies) {
                        Pharmacy account = new Pharmacy();
                        account.setId(pharmacyDto.getId());
                        account.setAddress(pharmacyDto.getAddress());
                        account.setName(pharmacyDto.getName());
                        account.setPhone(pharmacyDto.getPhone());
                        account.setEmail(pharmacyDto.getEmail());
                        account.setType(pharmacyDto.getType());
                        account.setPin(pharmacyDto.getPin());
                        account.setFacilityId(pharmacyDto.getFacility().getId());
                        account.setDateRegistration(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                        DDDDb.getInstance(getApplicationContext()).pharmacistAccountRepository().save(account);
                    }
                    DDDDb.getInstance(AdminActivation.this).facilityRepository().delete3();
                    DDDDb.getInstance(AdminActivation.this).facilityRepository().delete4();
                    DDDDb.getInstance(AdminActivation.this).facilityRepository().delete5();
                    DDDDb.getInstance(AdminActivation.this).facilityRepository().delete6();
                    Intent streamPlayerHome = new Intent(getApplicationContext(), FacilityHome.class);
                    startActivity(streamPlayerHome);
                    finish();
                    progressdialog.dismiss();
                    progressdialog.dismiss();
                } else {
                    FancyToast.makeText(getApplicationContext(), "Contact System Administrator", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    progressdialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                t.printStackTrace();
                FancyToast.makeText(getApplicationContext(), "No Internet Connection", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                progressdialog.dismiss();
            }


        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressdialog != null && progressdialog.isShowing()) {
            progressdialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    private boolean validateInput(String pin) {
        if (pin.isEmpty()) {
            //  otp_view.setError("Enter Facility code");
            return false;
        }
        return true;


    }


    public HashMap<String, String> getRole() {
        HashMap<String, String> name = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("usernameDB", Context.MODE_PRIVATE);
        name.put("role", sharedPreferences.getString("role", null));
        return name;
    }
}
