//package org.fhi360.ddd;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.github.clans.fab.FloatingActionButton;
//import com.github.clans.fab.FloatingActionMenu;
//
//import org.fhi360.ddd.R;
//
//import com.mlsdev.animatedrv.AnimatedRecyclerView;
//
//import org.fhi360.ddd.adapter.NewPatientAdapter;
//import org.fhi360.ddd.util.MyList;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ActivityOptionsNewPatient extends AppCompatActivity {
//    private List<MyList> myLists;
//    private NewPatientAdapter adapter;
//    RecyclerView recyclerView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_refil_darshbaord);
//        recyclerView = (AnimatedRecyclerView) findViewById(R.id.patient_recycler);
//        recyclerView = findViewById(R.id.rec);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        myLists = new ArrayList<>();
//        getdata();
//    }
//
//
//    private void getdata() {
//        myLists.add(new MyList("Check list of assigned clients"));
//        adapter = new NewPatientAdapter(myLists, getApplicationContext());
//        recyclerView.setAdapter(adapter);
//    }
//
//}
