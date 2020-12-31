package org.fhi360.ddd.adapter;

import org.fhi360.ddd.Db.DDDDb;
import org.fhi360.ddd.R;
import org.fhi360.ddd.domain.ARV;
import org.fhi360.ddd.domain.Regimen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ARVListAdapter extends BaseAdapter {
    public List<ARV> encounters;
    private Activity activity;

    public ARVListAdapter(Activity activity, List<ARV> encounters) {
        super();
        this.activity = activity;
        this.encounters = encounters;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return encounters.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return encounters.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView textFirst;
        TextView textSecond;
        TextView textThird;
        TextView quantityPrescibed;
        TextView quantityDispensed;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.encounter_list_view, null);
            holder.textFirst = convertView.findViewById(R.id.date_visit);
            holder.textSecond = convertView.findViewById(R.id.regimen1);
            holder.textThird = convertView.findViewById(R.id.duration1);
            holder.quantityDispensed = convertView.findViewById(R.id.quantityDispense);
            holder.quantityPrescibed = convertView.findViewById(R.id.quantityPrescibed);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ARV encounter = encounters.get(position);
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("MM-dd-yyyy").parse(encounter.getDateVisit());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.textFirst.setText((CharSequence) date1);
        Regimen regimen = DDDDb.getInstance(activity).regimenRepository().findByOne(encounter.getRegimen1());
        holder.textSecond.setText(regimen.getName());
        holder.textThird.setText("duration in days:   " + encounter.getDuration1());
        holder.quantityPrescibed.setText("QTY prescribed:  " + Integer.parseInt(encounter.getPrescribed1()));
        holder.quantityDispensed.setText("Qty Dispensed:  " + Integer.parseInt(encounter.getDispensed1()));
        return convertView;
    }
}