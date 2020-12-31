package org.fhi360.ddd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;

import com.itextpdf.text.pdf.StringUtils;
import org.fhi360.ddd.AdminClientProfileActivity;
import org.fhi360.ddd.ClientProfileActivity2;
import org.fhi360.ddd.Db.DDDDb;
import org.fhi360.ddd.R;
import org.fhi360.ddd.ClientProfileActivity;
import org.fhi360.ddd.domain.Facility;
import org.fhi360.ddd.domain.Patient;
import org.fhi360.ddd.util.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

import static org.fhi360.ddd.util.Constants.PREFERENCES_ENCOUNTER;


public class PatientRecyclerAdapter6 extends RecyclerView.Adapter<PatientRecyclerAdapter6.ViewHolder> {
    //These variables will hold the data for the views
    private List<Patient> patientList;
    private Context context;


    //Provide a reference to views used in the recycler view. Each ViewHolder will display a CardView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    //Pass data to the adapter in its constructor
    public PatientRecyclerAdapter6(List<Patient> patientList, Context context) {
        this.patientList = patientList;
        this.context = context;

    }

    //Create a new view and specify what layout to use for the contents of the ViewHolder
    @NonNull
    @Override
    public PatientRecyclerAdapter6.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_card_view_pick, parent, false);
        return new ViewHolder(cardView);
    }

    //Set the values inside the given view.
    //This method get called whenever the recyler view needs to display data the in a view holder
    //It takes two parameters: the view holder that data needs to be bound to and the position in the data set of the data that needs to be bound.
    //Declare variable position as final because we are referencing it in the inner class View.OnClickListener
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        CardView cardView = holder.cardView;
        String firstLettersurname = String.valueOf(patientList.get(position).getSurname().charAt(0));
        Random mRandom = new Random();
        TextView circleImages = cardView.findViewById(R.id.circleImage);
        int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) circleImages.getBackground()).setColor(color);
        circleImages.setText(firstLettersurname);

        TextView profileView = cardView.findViewById(R.id.patient_profile);
        ImageView pickUp = cardView.findViewById(R.id.sign);
        String surname = patientList.get(position).getSurname();
        String otherNames = patientList.get(position).getOtherNames();

        String firstLettersOtherName = String.valueOf(patientList.get(position).getOtherNames().charAt(0));

        String fullSurname = firstLettersurname.toUpperCase() + surname.substring(1).toLowerCase();

        String fullOtherName = firstLettersOtherName.toUpperCase() + otherNames.substring(1).toLowerCase();

        String clientName = "<font color='#000'>" + fullSurname + "</font> &nbsp &nbsp" + "<font color='#000'>" + fullOtherName + "</font>";

        profileView.setText(Html.fromHtml(clientName), TextView.BufferType.SPANNABLE);

        TextView facilityView = cardView.findViewById(R.id.facility_name);
        TextView dateRegistration = cardView.findViewById(R.id.dateText);
        dateRegistration.setText(patientList.get(position).getDateNextRefill());
        String red = patientList.get(position).getRed();
        String green = patientList.get(position).getGreen();
        String blue = patientList.get(position).getBlue();

        if (Objects.equals(red, "red")) {
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound("+", Color.RED);
            pickUp.setImageDrawable(drawable);

        }
        if (Objects.equals(green, "green")) {
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound("+", Color.GREEN);
            pickUp.setImageDrawable(drawable);

        }
        if (Objects.equals(blue, "blue")) {
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound("+", Color.BLUE);
            pickUp.setImageDrawable(drawable);

        }
        Facility facilityName = DDDDb.getInstance(context).facilityRepository().findOne(patientList.get(position).getFacilityId());
        if (facilityName != null) {

            facilityView.setText(facilityName.getName());
        }


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences(patientList.get(position));
                Intent intent = new Intent(context, AdminClientProfileActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @SuppressLint("ApplySharedPref")
    private void savePreferences(Patient patient) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_ENCOUNTER, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("patient", new Gson().toJson(patient));
        editor.putBoolean("edit_mode", false);
        editor.commit();
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static void compareDates(Date date1, Date date2) {

        System.out.println("DATE 1 " + DateUtil.parseDateToString(date1, "yyyy-MM-dd"));
        System.out.println("DATE 2 " + DateUtil.parseDateToString(date2, "yyyy-MM-dd"));

        if (date1.after(date2)) {
            System.out.println("Date1 is after Date2");
        }

        if (date1.before(date2)) {
            System.out.println("Date1 is before Date2");
        }
        if (date1.equals(date2)) {
            System.out.println("Date1 is equal Date2");
        }

        System.out.println();
    }

}
