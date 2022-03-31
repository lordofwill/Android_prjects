package com.holymoly.coronasupporter.district;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.holymoly.coronasupporter.DBHelper;
import com.holymoly.coronasupporter.R;

import java.util.ArrayList;

public class MapTotalActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    GoogleMap map;

    //인텐트로 넘겨받는 필드
    String mapType;
    ArrayList<MyDistrictVO> list = new ArrayList<>();
    int districtCount;
    String storeName;
    String storeAddr;
    double storeLatitude;
    double storeLongitude;


    @Override
    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //인텐트로 데이터 넘겨받고 검사
        Intent intent = getIntent();
        mapType = intent.getStringExtra("지도타입");

        if (mapType.equals("자가격리")) {
            list = (ArrayList<MyDistrictVO>) intent.getSerializableExtra("격리구역리스트");
            districtCount = list.size();
            getMyLocation();
            Toast.makeText(this, "지도를 길게 클릭해 격리구역을 설정합니다.", Toast.LENGTH_LONG).show();
        } else if (mapType.equals("위치확인")) {
            storeName = intent.getStringExtra("StoreName");
            storeAddr = intent.getStringExtra("StoreAddr");
            storeLatitude = intent.getDoubleExtra("StoreLatitude", 0);
            storeLongitude = intent.getDoubleExtra("StoreLongitude", 0);
        }



        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_district)).getMapAsync(this);
    }


    @Override
    public synchronized void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        if (mapType.equals("자가격리")) {
            addDistrict();
            map.setOnMapLongClickListener(this);
            map.setOnInfoWindowClickListener(this);
        } else if (mapType.equals("위치확인")) {
            LatLng position = new LatLng(storeLatitude, storeLongitude);

            MarkerOptions marker = new MarkerOptions();
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
            marker.position(position);
            marker.title(storeName);
            marker.snippet(storeAddr);
            map.addMarker(marker);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
        }
    }


    //내위치로 카메라를 이동시키고 격리구역을 마커와 반경으로 표기함
    private void getMyLocation() {
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(map!=null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                        } else {
                            getMyLocation();
                        }
                    }
                }
        );

    }


    //액티비티가 켜질때 맵에 마커+반경 찍기
    private void addDistrict() {
        //격리구역 개수만큼 반복
        for (int i = 0; i < list.size(); i++) {
            LatLng position = new LatLng(list.get(i).latitude, list.get(i).longitude);

            //마커
            MarkerOptions marker = new MarkerOptions();
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
            marker.position(position);
            marker.title("자가격리구역");
            marker.snippet("삭제하려면 클릭하세요");

            //반경
            CircleOptions districtArea = new CircleOptions().center(position)
                    .radius(50)        // 반지름(m)임 이거
                    .strokeWidth(0f)
                    .fillColor(Color.parseColor("#3232ffff"));


            map.addMarker(marker);
            map.addCircle(districtArea);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        final LatLng position = latLng;
        final double latitude = position.latitude;
        final double longitude = position.longitude;


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.app_icon);
        dialog.setTitle("자가격리구역 추가");
        dialog.setMessage("자가격리구역을 추가할까요?");
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (districtCount < 5) {
                    DBHelper helper = new DBHelper(getApplicationContext());
                    SQLiteDatabase db = helper.getWritableDatabase();
                    db.execSQL("INSERT INTO tb_district (latitude, longitude) VALUES (" + latitude + ", " + longitude + ")");
                    helper.close();
                    db.close();
                    districtCount++;

                    MarkerOptions marker = new MarkerOptions();
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
                    marker.position(position);
                    marker.title("자가격리구역");
                    marker.snippet("삭제하려면 클릭하세요");

                    //반경
                    CircleOptions districtArea = new CircleOptions().center(position)
                            .radius(50)        // 반지름(m)임 이거
                            .strokeWidth(0f)
                            .fillColor(Color.parseColor("#3232ffff"));

                    map.addMarker(marker);
                    map.addCircle(districtArea);
                    Toast.makeText(getApplicationContext(), "자가격리구역을 추가했습니다.", Toast.LENGTH_SHORT).show();
                    restartDistrictService();
                } else {
                    Toast.makeText(getApplicationContext(), "자가격리구역은 5개까지 설정 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("취소", null);
        dialog.show();
    }


    @Override
    public void onInfoWindowClick(final Marker marker) {
        final LatLng position = marker.getPosition();
        final double latitude = position.latitude;
        final double longitude = position.longitude;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.app_icon);
        dialog.setTitle("자가격리구역 삭제");
        dialog.setMessage("자가격리구역을 삭제할까요?");
        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper helper = new DBHelper(getApplicationContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                db.execSQL("DELETE FROM tb_district WHERE latitude = " + latitude + " AND longitude = " + longitude);
                helper.close();
                db.close();
                districtCount--;

                refreshDistrict();
                restartDistrictService();
            }
        });
        dialog.setNegativeButton("취소", null);
        dialog.show();
    }


    //새로 마커를 찍으면 맵에 그려진 객체들을 초기화하고 재설정
    private void refreshDistrict() {
        list.clear();
        map.clear();
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT latitude, longitude FROM tb_district", null);
        while (cursor.moveToNext()) {
            list.add(new MyDistrictVO(cursor.getDouble(0), cursor.getDouble(1)));
        }
        for (int i = 0; i < list.size(); i++) {
            //위치
            LatLng position = new LatLng(list.get(i).latitude, list.get(i).longitude);

            //마커
            MarkerOptions marker = new MarkerOptions();
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
            marker.position(position);
            marker.title("자가격리구역");
            marker.snippet("삭제하려면 클릭하세요");

            //반경
            CircleOptions districtArea = new CircleOptions().center(position)
                    .radius(50)        // 반지름(m)임 이거
                    .strokeWidth(0f)
                    .fillColor(Color.parseColor("#3232ffff"));


            map.addMarker(marker);
            map.addCircle(districtArea);
        }
        helper.close();
        db.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void restartDistrictService() {
        if (serviceRunningCheck()) {
            Intent intent = new Intent(this, MyLocationService.class);
            stopService(intent);
            intent = new Intent(this, MyLocationService.class);
            startService(intent);
        }
    }

    private boolean serviceRunningCheck() {
        ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.holymoly.coronasupporter.district.MyLocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}

