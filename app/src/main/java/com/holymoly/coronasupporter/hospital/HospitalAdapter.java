package com.holymoly.coronasupporter.hospital;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HospitalAdapter extends ArrayAdapter<HospitalVO> {
    Context context;
    int resId;
    ArrayList<HospitalVO> data;

    public HospitalAdapter(Context context, int resId, ArrayList<HospitalVO> data) {
        super(context, resId);
        this.context = context;
        this.resId = resId;
        this.data = data;
    }


    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resId, null);
            HospitalViewHolder holder = new HospitalViewHolder(convertView);
            convertView.setTag(holder);
        }

        HospitalViewHolder holder = (HospitalViewHolder) convertView.getTag();
        TextView text1 = holder.text1;
        TextView text2 = holder.text2;
        TextView text3 = holder.text3;

        final HospitalVO vo = data.get(position);

        text1.setText(vo.name);
        text2.setText(vo.addr);
        text3.setText(vo.tel);

        return convertView;
    }

}
