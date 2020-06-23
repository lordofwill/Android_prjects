package com.example.bus;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    String name;
    double latitude;
    double longitude;
    boolean zoom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude",0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng station = new LatLng(latitude, longitude);
        //mMap.addMarker(new MarkerOptions().position(station).title(name));
        mMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(station,18));
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(mMap.getCameraPosition().zoom>16)stationMarking(mMap);
                else mMap.clear();
            }
        });
    }


    public void stationMarking(GoogleMap map){
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, latitude, longitude FROM tb_station",null);

        String name;
        double latitude;
        double longitude;

        int i=0;
        while(cursor.moveToNext()){
            name = cursor.getString(0);
            latitude = cursor.getDouble(1);
            longitude = cursor.getDouble(2);
            i++;

            LatLng mark = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(mark).title(name));
        }
        db.close();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        double latitude = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, id, next, bookmark, latitude, longitude FROM tb_station WHERE latitude ="+latitude+" AND  longitude ="+longitude,null);
        String name = "";
        int id = 0;
        int bookmark=0;
        String next="";

        int i=0;
        while(cursor.moveToNext()){
            name = cursor.getString(0);
            id = cursor.getInt(1);
            next= cursor.getString(2);
            bookmark = cursor.getInt(3);
            i++;
        }

        Intent intent = new Intent(this, ArriveActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("id",Integer.toString(id));
        intent.putExtra("next",next);
        intent.putExtra("bookmark",bookmark);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        startActivity(intent);
        db.close();
        return false;
    }


    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
