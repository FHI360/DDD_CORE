

package org.fhi360.ddd;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.fhi360.ddd.Db.DDDDb;
import org.fhi360.ddd.adapter.PatientRecyclerAdapter;
import org.fhi360.ddd.adapter.PatientRecyclerAdapter2;
import org.fhi360.ddd.adapter.PatientRecyclerAdapter6;
import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.domain.Patient;
import org.fhi360.ddd.domain.Regimen;
import org.fhi360.ddd.dto.Data;
import org.fhi360.ddd.dto.PatientDto;
import org.fhi360.ddd.webservice.APIService;
import org.fhi360.ddd.webservice.ClientAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static org.fhi360.ddd.util.Constants.PREFERENCES_ENCOUNTER;

public class AdminPatientList2 extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private RecyclerView recyclerViewHts;
    private List<Patient> listPatients;
    private PatientRecyclerAdapter6 patientRecyclerAdapter;
    private ProgressDialog mPb;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_patient_recycler1);
        this.preferences = getSharedPreferences(PREFERENCES_ENCOUNTER, 0);
        recyclerViewHts = findViewById(R.id.patient_recycler);
        listPatients = new ArrayList<>();
        listPatients.clear();
        getPatient();
        listPatients = DDDDb.getInstance(AdminPatientList2.this).patientRepository().findByAll();
        patientRecyclerAdapter = new PatientRecyclerAdapter6(listPatients, AdminPatientList2.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AdminPatientList2.this);
        recyclerViewHts.setLayoutManager(mLayoutManager);
        recyclerViewHts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewHts.setHasFixedSize(true);
        recyclerViewHts.setAdapter(patientRecyclerAdapter);
        patientRecyclerAdapter.notifyDataSetChanged();
        recyclerViewHts.scheduleLayoutAnimation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        List<Patient> filteredValues = new ArrayList<>(listPatients);
        for (Patient value : listPatients) {
            if (!value.getSurname().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }

        List<Patient> filteredValues1 = new ArrayList<>(listPatients);
        for (Patient value : listPatients) {
            if (!value.getSurname().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues1.remove(value);
            }
        }

        patientRecyclerAdapter = new PatientRecyclerAdapter6(filteredValues, AdminPatientList2.this);
        recyclerViewHts.setAdapter(patientRecyclerAdapter);

        return false;
    }

    public void resetSearch() {
        patientRecyclerAdapter = new PatientRecyclerAdapter6(listPatients, AdminPatientList2.this);
        recyclerViewHts.setAdapter(patientRecyclerAdapter);

    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }


    private void getPatient() {
        // this.uploadStatus(false);
        mPb = new ProgressDialog(AdminPatientList2.this);
        mPb.setProgress(0);
        mPb.setMessage("Uploading data please wait...");
        mPb.setCancelable(false);
        mPb.setIndeterminate(false);
        mPb.setProgress(0);
        mPb.setMax(100);
        mPb.show();
        @SuppressLint("HardwareIds")
        ClientAPI clientAPI = APIService.createService(ClientAPI.class);
        Call<Data> objectCall = clientAPI.getAllPatients();//downLoad(deviceId, pin, accountUserName, accountPassword);
        objectCall.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(@NonNull Call<Data> call, @NonNull retrofit2.Response<Data> response) {
                if (response.isSuccessful()) {
                    List<PatientDto> patientDtoList = Objects.requireNonNull(response.body()).getPatients();
                    for (PatientDto patientDto : patientDtoList) {
                        Patient patient = new Patient();
                        patient.setId(patientDto.getId());
                        patient.setHospitalNum(patientDto.getHospitalNum());
                        patient.setFacilityId(patientDto.getFacility().getId());
                        patient.setUniqueId(patientDto.getUniqueId());
                        patient.setSurname(patientDto.getSurname());
                        patient.setOtherNames(patientDto.getOtherNames());
                        patient.setGender(patientDto.getGender());
                        patient.setDateBirth(patientDto.getDateBirth());
                        patient.setAddress(patientDto.getAddress());
                        patient.setPhone(patientDto.getPhone());
                        patient.setDateStarted(patientDto.getDateStarted());
                        patient.setLastClinicStage(patientDto.getLastClinicStage());
                        patient.setLastViralLoad(patientDto.getLastViralLoad());
                        patient.setDateLastViralLoad(patientDto.getDateLastViralLoad());
                        patient.setViralLoadDueDate(patientDto.getViralLoadDueDate());
                        patient.setViralLoadType(patientDto.getViralLoadType());
                        patient.setDateLastClinic(patientDto.getDateLastClinic());
                        patient.setDateNextClinic(patientDto.getDateNextClinic());
                        patient.setDateLastRefill(patientDto.getDateLastRefill());
                        patient.setDateNextRefill(patientDto.getDateNextRefill());
                        patient.setPharmacyId(patientDto.getPharmacyId());
                        patient.setUuid(patientDto.getUuid());
                        patient.setRed(patientDto.getRed());
                        patient.setGreen(patientDto.getGreen());
                        patient.setBlue(patientDto.getBlue());
                        if (DDDDb.getInstance(AdminPatientList2.this).patientRepository().findOne(patientDto.getId()) != null) {
                            DDDDb.getInstance(AdminPatientList2.this).patientRepository().updateUsers(patient);
                            mPb.hide();
                        } else {
                            DDDDb.getInstance(AdminPatientList2.this).patientRepository().save(patient);
                            // FancyToast.makeText(AdminPatientList2.this, "Records Successfully downloaded", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            mPb.hide();
                        }

                    }


                    listPatients = DDDDb.getInstance(getApplicationContext()).patientRepository().findByAll();
                    patientRecyclerAdapter = new PatientRecyclerAdapter6(listPatients, AdminPatientList2.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AdminPatientList2.this);
                    recyclerViewHts.setLayoutManager(mLayoutManager);
                    recyclerViewHts.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewHts.setHasFixedSize(true);
                    recyclerViewHts.setAdapter(patientRecyclerAdapter);
                    patientRecyclerAdapter.notifyDataSetChanged();
                    recyclerViewHts.scheduleLayoutAnimation();

                } else {
                    FancyToast.makeText(AdminPatientList2.this, "DDD can't get patient records", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    mPb.hide();
                }


            }

            @Override
            public void onFailure(@NonNull Call<Data> call, @NonNull Throwable t) {
                t.printStackTrace();
                FancyToast.makeText(AdminPatientList2.this, "No internet connection ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                mPb.hide();
            }

        });

    }

    @SuppressLint("ApplySharedPref")
    private void uploadStatus(boolean status) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_ENCOUNTER, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("status", status);
        editor.commit();
    }

}
