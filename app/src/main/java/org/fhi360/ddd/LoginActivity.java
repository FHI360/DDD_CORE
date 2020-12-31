package org.fhi360.ddd;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.shashank.sony.fancytoastlib.FancyToast;

import org.fhi360.ddd.Db.DDDDb;
import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.domain.Patient;
import org.fhi360.ddd.domain.User;
import org.fhi360.ddd.dto.FacilityDto;
import org.fhi360.ddd.dto.PatientDto;
import org.fhi360.ddd.dto.UserDto;
import org.fhi360.ddd.webservice.APIService;
import org.fhi360.ddd.webservice.ClientAPI;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText username1;
    private EditText password1;
    private TextView createUser, text_forget_password;
    private Button login;
    private ProgressDialog progressdialog;
    private String deviceconfigId;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @SuppressLint({"HardwareIds", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);
        disableAutofill();
        deviceconfigId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        username1 = findViewById(R.id.username1);
        password1 = findViewById(R.id.password1);
        createUser = findViewById(R.id.text_create_account);
        login = findViewById(R.id.sign_in_button);
        text_forget_password = findViewById(R.id.text_forget_password1);
     //   saveFacility();
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create();
            }
        });
        createUser.setMovementMethod(LinkMovementMethod.getInstance());
        text_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(deviceconfigId);
            }
        });
//        User id = DDDDb.getInstance(this).userRepository().findByOne();
//        if (id == null) {
//            createUser.setVisibility(View.VISIBLE);
//        } else {
//            createUser.setVisibility(View.VISIBLE);
//        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput(username1.getText().toString(), password1.getText().toString())) {
                    User user = DDDDb.getInstance(LoginActivity.this).userRepository().findByUsernameAndPassword(username1.getText().toString(), password1.getText().toString());
                    if (user != null && user.getRole().equalsIgnoreCase("DDD outlet")) {
                        Intent intent = new Intent(LoginActivity.this, OutletHome.class);
                        save(user.getName());
                        startActivity(intent);
                    } else if (user != null && user.getRole().equalsIgnoreCase("Facility")) {
                        Intent intent = new Intent(LoginActivity.this, FacilityActivation.class);
                        save(user.getName());
                        startActivity(intent);
                    } else if (user != null && user.getRole().equalsIgnoreCase("admin")) {
                        Intent intent = new Intent(LoginActivity.this, AdminActivation.class);
                        System.out.println("USERNAME " + user.getUsername());
                        save(user.getName());
                        startActivity(intent);
                    } else {
                        FancyToast.makeText(getApplicationContext(), "Wrong username or password or role", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    }
                }

            }
        });
    }

    private void create() {
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
    }


    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            username1.setError("username can not be empty");
            return false;
        } else if (password.isEmpty()) {
            password1.setError("password can not be empty");
            return false;
        }
        return true;


    }

    private void showAlert(final String deviceconfigId) {
        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
        View promptsView = li.inflate(R.layout.forget_pop_up, null);
        final AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).create();
        dialog.setView(promptsView);
        final TextView notitopOk, notitopNotnow;
        //  final EditText notitoptxt;
        notitopOk = promptsView.findViewById(R.id.notitopOk);
        notitopNotnow = promptsView.findViewById(R.id.notitopNotnow);
        //  notitoptxt = promptsView.findViewById(R.id.notitoptxt);
        notitopNotnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        notitopOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User usernameAndPassword = DDDDb.getInstance(LoginActivity.this).userRepository().findByOne();
                if (usernameAndPassword != null) {
                    username1.setText(usernameAndPassword.getUsername());
                    password1.setText(usernameAndPassword.getPassword());
                    dialog.dismiss();
                }

            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }


    public void saveRole(String role) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("usernameDB", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("role", role);
        editor.apply();
    }

    public HashMap<String, String> getRole() {
        HashMap<String, String> name = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("usernameDB", Context.MODE_PRIVATE);
        name.put("role", sharedPreferences.getString("role", null));
        return name;
    }


    public void save(String name) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("name", name);
        editor.apply();
    }

    public HashMap<String, String> get() {
        HashMap<String, String> name = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("name", Context.MODE_PRIVATE);
        name.put("name", sharedPreferences.getString("name", null));
        return name;
    }


    private void login(String username, String password, String role) {
        ProgressDialog progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("Saving patient...");
        progressdialog.setCancelable(false);
        progressdialog.setIndeterminate(false);
        progressdialog.setMax(100);
        progressdialog.show();
        ClientAPI clientAPI = APIService.createService(ClientAPI.class);
        Call<org.fhi360.ddd.dto.Response> objectCall = clientAPI.login(username, password, role);
        objectCall.enqueue(new Callback<org.fhi360.ddd.dto.Response>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<org.fhi360.ddd.dto.Response> call, retrofit2.Response<org.fhi360.ddd.dto.Response> response) {
                if (response.isSuccessful()) {
                    org.fhi360.ddd.dto.Response response1 = response.body();
                    if (Objects.requireNonNull(response1).getMessage() != null) {
                        FancyToast.makeText(getApplicationContext(), response1.getMessage(), FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        progressdialog.dismiss();
                    } else if (Objects.requireNonNull(response1).getUser() != null) {
                        UserDto user1 = response1.getUser();
                        Facility facility = DDDDb.getInstance(getApplicationContext()).facilityRepository().getFacility();
                        if (facility != null) {
                            facility.setId(user1.getFacilityId());
                            facility.setName(user1.getFacilityName());
                            DDDDb.getInstance(getApplicationContext()).facilityRepository().update(facility);
                        } else {
                            facility.setId(user1.getFacilityId());
                            facility.setName(user1.getFacilityName());
                            DDDDb.getInstance(getApplicationContext()).facilityRepository().save(facility);
                        }
                        User user = new User();
                        user.setId(user1.getId());
                        user.setUsername(user1.getUsername());
                        user.setPassword(user1.getPassword());
                        user.setRole(user1.getRole());
                        DDDDb.getInstance(getApplicationContext()).userRepository().save(user);
                        Intent intent = new Intent(LoginActivity.this, AdminHomePage.class);
                        startActivity(intent);
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

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
//        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    private void saveFacility() {
        ClientAPI clientAPI = APIService.createService(ClientAPI.class);
        Call<List<FacilityDto>> objectCall = clientAPI.getFacility();
        objectCall.enqueue(new Callback<List<FacilityDto>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<List<FacilityDto>> call, Response<List<FacilityDto>> response) {
                if (response.isSuccessful()) {
                    List<FacilityDto> facilityDtoList = response.body();
                    Objects.requireNonNull(facilityDtoList).forEach(facilityDto -> {
                        Facility facilityReturn = DDDDb.getInstance(getApplicationContext()).facilityRepository().findOne(facilityDto.getId());
                        if (facilityReturn == null) {
                            Facility facility = new Facility();
                            facility.setId(facilityDto.getId());
                            facility.setName(facilityDto.getName());
                            facility.setDistrictId(facilityDto.getDistrictId());
                            facility.setStateId(facilityDto.getStateId());
                            DDDDb.getInstance(getApplicationContext()).facilityRepository().save(facility);
                        }
                    });

                } else {
                    FancyToast.makeText(getApplicationContext(), "Contact System administrator ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    progressdialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<FacilityDto>> call, Throwable t) {
                t.printStackTrace();
                FancyToast.makeText(getApplicationContext(), "No Internet Connection", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                progressdialog.dismiss();
            }

        });

    }
}
