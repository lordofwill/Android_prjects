package com.example.bus;

import android.view.View;
import android.widget.TextView;

public class ArriveViewHolder {
    public TextView text1;
    public TextView text2;
    public TextView text3;

    public ArriveViewHolder(View root){
        text1 = root.findViewById(R.id.text1_a);
        text2 = root.findViewById(R.id.text2_a);
        text3 = root.findViewById(R.id.text3_a);
    }
}
