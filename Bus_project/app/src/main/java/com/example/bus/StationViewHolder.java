package com.example.bus;

import android.view.View;
import android.widget.TextView;

public class StationViewHolder {
    public TextView text1;
    public TextView text2;

    public StationViewHolder(View root){
        text1 = root.findViewById(R.id.text1);
        text2 = root.findViewById(R.id.text2);
    }
}

