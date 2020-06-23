package com.example.bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RouteAdapter extends ArrayAdapter<RouteVO> {
    Context context;
    int resource;
    ArrayList<RouteVO> data;

    public RouteAdapter(Context context, int resource, ArrayList<RouteVO> data) {
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
            RouteViewHolder holder = new RouteViewHolder(convertView);
            convertView.setTag(holder);
        }

        RouteViewHolder holder = (RouteViewHolder) convertView.getTag();
        TextView text1 = holder.text1;

        final RouteVO vo = data.get(position);

        text1.setText(vo.busStopName);

        return convertView;
    }
}
