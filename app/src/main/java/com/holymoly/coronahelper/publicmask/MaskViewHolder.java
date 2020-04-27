package com.holymoly.coronahelper.publicmask;

import android.view.View;
import android.widget.TextView;

import com.holymoly.coronahelper.R;


public class MaskViewHolder {
    public TextView text1;
    public TextView text2;
    public TextView text3;
    public TextView text4;

    public MaskViewHolder(View root){
        text1 = root.findViewById(R.id.mask_adapter_text1);
        text2 = root.findViewById(R.id.mask_adapter_text2);
        text3 = root.findViewById(R.id.mask_adapter_text3);
        text4 = root.findViewById(R.id.mask_adapter_text4);
    }
}
