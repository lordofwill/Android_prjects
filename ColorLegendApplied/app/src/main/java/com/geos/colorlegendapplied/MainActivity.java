package com.geos.colorlegendapplied;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnMyTapListener {
    private ColorLegendItem _item;
    private ColorLegend _colorLegend;
    private Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _colorLegend =(ColorLegend)findViewById(R.id.legend);


        _colorLegend.addItem(new ColorLegendItem(0xffff0000,"Road",false));
        _colorLegend.addItem(new ColorLegendItem(0xff00ff00,"Sewer", true));
        _colorLegend.addItem(new ColorLegendItem(0xff0000ff,"River", false));

        _colorLegend.setOnMyTapListener(this);
    }

    @Override
    public void onTap(ColorLegendItem item) {
        if(item.getToggleSwitch())
            Toast.makeText(getApplicationContext(),"Title : " + item.getTitle() + "  Color : " + item.getColor() + "  Status: ON"  ,Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(),"Title : " + item.getTitle() + "  Color : " + item.getColor() + "  Status: OFF" ,Toast.LENGTH_SHORT).show();
    }
}