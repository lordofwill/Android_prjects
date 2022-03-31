package com.example.bus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ArriveAdapter extends ArrayAdapter<ArriveVO> {
    Context context;
    int resource;
    ArrayList<ArriveVO> data;

    public ArriveAdapter(Context context, int resource, ArrayList<ArriveVO> data) {
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

        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, null);
            ArriveViewHolder holder = new ArriveViewHolder(convertView);
            convertView.setTag(holder);
        }

        ArriveViewHolder holder = (ArriveViewHolder) convertView.getTag();
        TextView text1 = holder.text1;
        TextView text2 = holder.text2;
        TextView text3 = holder.text3;

        final ArriveVO vo = data.get(position);

        text1.setText(vo.busName);
        text2.setText("현재 "+vo.busLocation);
        if(vo.busTime.equals("1")||vo.busTime.equals("2")||vo.busTime.equals("3")){
            text3.setText("곧 도착");
        } else { text3.setText(vo.busTime+"분"); }


        return convertView;
    }

}
