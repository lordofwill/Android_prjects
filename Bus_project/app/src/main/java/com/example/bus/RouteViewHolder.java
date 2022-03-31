package com.example.bus;

import android.view.View;
import android.widget.TextView;

public class RouteViewHolder {
    public TextView text1;

    public RouteViewHolder(View root){
        text1 = root.findViewById(R.id.text);
    }
}
