package com.example.bus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StationAdapter extends ArrayAdapter<StationVO> {
    Context context;
    int resource;
    ArrayList<StationVO> data;

    public StationAdapter(Context context, int resource, ArrayList<StationVO> data) {
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
    public View getView(int position,  View convertView,  ViewGroup parent) {

        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, null);
            StationViewHolder holder = new StationViewHolder(convertView);
            convertView.setTag(holder);
        }

        StationViewHolder holder = (StationViewHolder) convertView.getTag();
        TextView text1 = holder.text1;
        TextView text2 = holder.text2;
        final StationVO vo = data.get(position);

        text1.setText(vo.name);
        text2.setText(vo.id+"("+vo.next+" 방향)");





        return convertView;
    }
}
