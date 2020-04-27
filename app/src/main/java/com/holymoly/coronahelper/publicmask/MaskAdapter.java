package com.holymoly.coronahelper.publicmask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MaskAdapter extends ArrayAdapter<MaskVO> {
    Context context;
    int resource;
    ArrayList<MaskVO> data;

    public MaskAdapter(Context context, int resource, ArrayList<MaskVO> data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
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
            convertView = inflater.inflate(resource, null);
            MaskViewHolder holder = new MaskViewHolder(convertView);
            convertView.setTag(holder);
        }

        MaskViewHolder holder = (MaskViewHolder) convertView.getTag();
        TextView text1 = holder.text1;
        TextView text2 = holder.text2;
        TextView text3 = holder.text3;
        TextView text4 = holder.text4;

        final MaskVO vo = data.get(position);

        text1.setText(vo.name);
        text2.setText(vo.addr);
        if (vo.distance < 1000) {
            String str = vo.distance + "m";
            text3.setText(str);
        } else if (vo.distance < 2000) {
            String str = "2km 이내";
            text3.setText(str);
        } else if (vo.distance < 3000) {
            String str = "3km 이내";
            text3.setText(str);
        } else if (vo.distance < 4000) {
            String str = "4km 이내";
            text3.setText(str);
        } else if (vo.distance < 5000) {
            String str = "5km 이내";
            text3.setText(str);
        } else {
            String str = "거리 알수없음";
            text3.setText(str);
        }
        text4.setText(vo.remain);

        return convertView;
    }

}
