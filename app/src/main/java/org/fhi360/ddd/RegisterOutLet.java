package org.fhi360.ddd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shashank.sony.fancytoastlib.FancyToast;

import org.fhi360.ddd.Db.DDDDb;
import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.domain.Pharmacy;
import org.fhi360.ddd.dto.FacilityDto;
import org.fhi360.ddd.dto.PharmacyDto;
import org.fhi360.ddd.util.PrefManager;
import org.fhi360.ddd.webservice.APIService;
import org.fhi360.ddd.webservice.ClientAPI;

import java.text.SimpleDateFormat;
import java.util.*;

import lombok.Lombok;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterOutLet extends AppCompatActivity {

    private Button button;
    private EditText name, email, username, address;
    private EditText phone;
    private Spinner type, facilityName;
    private TextView pinCode;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private ProgressDialog progressdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_register);

        button = findViewById(R.id.register);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        type = findViewById(R.id.type);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        facilityName = findViewById(R.id.facilityName);
        pinCode = findViewById(R.id.pinCode);
        //  saveFacility();
        //ImageView back = findViewById(R.id.back);

//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), FacilityHome.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        phone.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });


        final ArrayList arrayListFacilityId = new ArrayList();
        ArrayList arrayListFacilityName = new ArrayList();
        List<Facility> facilityDtoArrayList = DDDDb.getInstance(RegisterOutLet.this).facilityRepository().findAll();

        for (Facility facility : facilityDtoArrayList) {
            arrayListFacilityId.add(facility.getId());
            arrayListFacilityName.add(facility.getName());
        }
        final ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(RegisterOutLet.this,
                R.layout.spinner_items, arrayListFacilityName);
        districtAdapter.setDropDownViewResource(R.layout.spinner_text_color);
        districtAdapter.notifyDataSetChanged();
        facilityName.setAdapter(districtAdapter);
        facilityName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long facId = (long) arrayListFacilityId.get(position);
                System.out.println("FACILTYID" + facId);
                new PrefManager(getApplicationContext()).saveFacId(facId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.outlet_type, R.layout.spinner_text_color);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.hintcolour);
        // Apply the adapter to the spinner
        type.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {
                String name1 = name.getText().toString();
                String type1 = type.getSelectedItem().toString();
                String phone1 = phone.getText().toString();
                String address1 = address.getText().toString();
                String email1 = email.getText().toString();
                if (validateInput(name1, type1, phone1, address1)) {
                    Pharmacy user = DDDDb.getInstance(RegisterOutLet.this).pharmacistAccountRepository().findByPhoneAndEmail(phone1, email1);
                    if (user != null) {
                        FancyToast.makeText(getApplicationContext(), "Outlet with these credentials already registered", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    } else {
                        //Long facilityId = DDDDb.getInstance(getApplicationContext()).facilityRepository().getFacility().getId();
                        PharmacyDto pharmacyDto = new PharmacyDto();
                        pharmacyDto.setAddress(address1);
                        pharmacyDto.setName(name1);
                        pharmacyDto.setPhone(phone1);
                        pharmacyDto.setEmail(email1);
                        pharmacyDto.setType(type1);
                        Facility facility = new Facility();
                        HashMap<String, String> facility1 = new PrefManager(RegisterOutLet.this).getFacId();
                        String facId = facility1.get("facId");
                        Long fac = Long.valueOf(facId);
                        System.out.println("FACILITYID " + fac);
                        facility.setId(Long.valueOf(facId));
                        pharmacyDto.setFacility(facility);
                        pharmacyDto.setUsername(username.getText().toString());
                        save(pharmacyDto);

                    }
                }
            }


        });

    }

    private boolean validateInput(String name1, String type1, String phone1, String address1) {
        if (name1.isEmpty()) {
            name.setError("name can not be empty");
            return false;
        } else if (type1.isEmpty()) {
            TextView errorText = (TextView) type.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Select a value");
            return false;
        } else if (phone1.isEmpty()) {
            phone.setError("Phone can not be empty");
            return false;
        } else if (address1.isEmpty()) {
            address.setError("Address can not be empty");
            return false;
        }
        return true;
    }

    protected void sendSMSMessage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone.getText().toString(), null, "DDD activation code " + 111, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @SuppressLint("Recycle")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1) && (resultCode == RESULT_OK)) {
            Cursor cursor = null;
            try {
                Uri uri = data.getData();
                assert uri != null;
                cursor = getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    String phone1 = cursor.getString(0);
                    phone.setText(phone1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void save(PharmacyDto account) {
        progressdialog = new ProgressDialog(RegisterOutLet.this);
        progressdialog.setMessage("DDD outlet saving...");
        progressdialog.setCancelable(false);
        progressdialog.setIndeterminate(false);
        progressdialog.setMax(100);
        progressdialog.show();
        ClientAPI clientAPI = APIService.createService(ClientAPI.class);
        Call<org.fhi360.ddd.dto.Response> objectCall = clientAPI.saveAccount(account);
        objectCall.enqueue(new Callback<org.fhi360.ddd.dto.Response>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<org.fhi360.ddd.dto.Response> call, Response<org.fhi360.ddd.dto.Response> response) {
                if (response.isSuccessful()) {
                    org.fhi360.ddd.dto.Response response1 = response.body();
                    if (Objects.requireNonNull(response1).getMessage() != null) {
                        FancyToast.makeText(getApplicationContext(), response1.getMessage(), FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        progressdialog.dismiss();
                    } else if (Objects.requireNonNull(response1).getPharmacy() != null) {
                        PharmacyDto account1 = Objects.requireNonNull(response.body()).getPharmacy();
                        System.out.println("ACCOUNT " + account1);
                        Pharmacy account = new Pharmacy();
                        account.setId(account.getId());
                        account.setAddress(account1.getAddress());
                        account.setName(account1.getName());
                        account.setPhone(account1.getPhone());
                        account.setEmail(account1.getEmail());
                        account.setType(account1.getType());
                        account.setPin(account1.getPin());
                        account.setFacilityId(account1.getFacility().getId());
                        account.setUsername(account1.getUsername());
                        account.setDateRegistration(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                        DDDDb.getInstance(RegisterOutLet.this).pharmacistAccountRepository().save(account);
                        FancyToast.makeText(getApplicationContext(), "notification has been sent email , kindly assigned client", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        pinCode.setText("Activation Code is : " + account1.getPin());
                        address.setText("");
                        name.setText("");
                        phone.setText("");
                        email.setText("");
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


}
