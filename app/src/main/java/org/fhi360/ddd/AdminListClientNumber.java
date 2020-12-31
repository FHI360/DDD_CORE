package org.fhi360.ddd;


import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.mlsdev.animatedrv.AnimatedRecyclerView;

import org.fhi360.ddd.Db.DDDDb;
import org.fhi360.ddd.adapter.AdminListDDDClientAdapter;
import org.fhi360.ddd.adapter.ListDDDClientAdapter;

import org.fhi360.ddd.adapter.PatientNumberAdapter;
import org.fhi360.ddd.adapter.PatientRecyclerAdapter;
import org.fhi360.ddd.domain.Patient;
import org.fhi360.ddd.domain.Pharmacy;

import java.util.ArrayList;
import java.util.List;

import static org.fhi360.ddd.util.Constants.PREFERENCES_ENCOUNTER;

public class AdminListClientNumber extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private RecyclerView recyclerViewHts;
    private Pharmacy account;
    private List<Patient> accountList;
    private PatientNumberAdapter patientRecyclerAdapter;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_patient_recycler1);
        recyclerViewHts = (AnimatedRecyclerView) findViewById(R.id.patient_recycler);
        this.preferences = getSharedPreferences(PREFERENCES_ENCOUNTER, 0);
        restorePreferences();
        if (savedInstanceState != null) {
            String json = savedInstanceState.getString("account");
            account = new Gson().fromJson(json, Pharmacy.class);
        }
        accountList = new ArrayList<>();
        accountList.clear();
        accountList = DDDDb.getInstance(AdminListClientNumber.this).patientRepository().findByPharmacyId(account.getId());
        patientRecyclerAdapter = new PatientNumberAdapter(accountList, AdminListClientNumber.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AdminListClientNumber.this);
        recyclerViewHts.setLayoutManager(mLayoutManager);
        recyclerViewHts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewHts.setHasFixedSize(true);
        recyclerViewHts.setAdapter(patientRecyclerAdapter);
        patientRecyclerAdapter.notifyDataSetChanged();
        recyclerViewHts.scheduleLayoutAnimation();


    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putString("account", new Gson().toJson(account));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String json = savedInstanceState.getString("account");
        account = new Gson().fromJson(json, Pharmacy.class);
    }

    private void restorePreferences() {
        String json = preferences.getString("account", "");
        account = new Gson().fromJson(json, Pharmacy.class);
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

        List<Patient> filteredValues = new ArrayList<>(accountList);
        for (Patient value : accountList) {
            if (!value.getSurname().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }

        List<Patient> filteredValues1 = new ArrayList<>(accountList);
        for (Patient value : accountList) {
            if (!value.getSurname().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues1.remove(value);
            }
        }

        //today's visit today date - next refil date or today = next fil date
        // all patients
        //search by name
        //search by due date

        // under summary report
        //routine report
        //Defaulters List
        patientRecyclerAdapter = new PatientNumberAdapter(filteredValues, AdminListClientNumber.this);
        recyclerViewHts.setAdapter(patientRecyclerAdapter);

        return false;
    }

    public void resetSearch() {
        patientRecyclerAdapter = new PatientNumberAdapter(accountList, AdminListClientNumber.this);
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


}
