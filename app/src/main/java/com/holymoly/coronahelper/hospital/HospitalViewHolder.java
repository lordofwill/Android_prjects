package com.holymoly.coronahelper.hospital;

import android.view.View;
import android.widget.TextView;

import com.holymoly.coronahelper.R;


public class HospitalViewHolder {
    TextView text1;
    TextView text2;
    TextView text3;

    public HospitalViewHolder(View root){
        text1 = root.findViewById(R.id.hospital_adapter_text1);
        text2 = root.findViewById(R.id.hospital_adapter_text2);
        text3 = root.findViewById(R.id.hospital_adapter_text3);
    }
}
