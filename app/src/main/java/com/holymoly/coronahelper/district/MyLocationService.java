package com.holymoly.coronahelper.district;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.holymoly.coronahelper.DBHelper;
import com.holymoly.coronahelper.receiver.NotificationReceiver;

import java.util.ArrayList;
import java.util.List;

public class MyLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //위치정보 관련 필드
    Location location;
    GoogleApiClient apiClient;
    FusedLocationProviderClient providerClient;

    //DB에서 격리구역 목록을 대입하는 리스트
    ArrayList<MyDistrictVO> district = new ArrayList<>();

    //토탈타임 저장용 SP객체
    SharedPreferences sp;
    SharedPreferences.Editor editor;


    /* ****************** 생명주기 함수 ****************** */


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        getDistrictDatabase();

        sp = getSharedPreferences("corona", Activity.MODE_PRIVATE);
        editor = sp.edit();

        //위치얻기용 프로바이더 객체연결
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        providerClient = LocationServices.getFusedLocationProviderClient(this);
        apiClient.connect();

        //서비스 실행시 포그라운드 알림 띄우는 코드
        NotificationChannel channel = new NotificationChannel("CHANNEL", "코로나서포터", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, "CHANNEL").build();
        startForeground(2, notification);
        Toast.makeText(this, "위치감지 서비스를 시작합니다.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        providerClient.removeLocationUpdates(locationCallback);
        Toast.makeText(this, "위치감지 서비스를 종료합니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    /* ****************** 데이터베이스 관련 함수 ****************** */

    //데이터베이스에 들어있는 격리구역 위도, 경도 받아서 -> list.get(i).latitude 에 들어감
    private void getDistrictDatabase() {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT latitude, longitude FROM tb_district", null);
        while (cursor.moveToNext()) {
            district.add(new MyDistrictVO(cursor.getDouble(0), cursor.getDouble(1)));
        }
        helper.close();
        db.close();
    }


    /* ****************** FUSED API 함수 ****************** */

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(60 * 1000);
        providerClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);


            //현재위치 Location 구하기
            List<Location> locationList = locationResult.getLocations();
            location = locationList.get(0);

            System.out.println("업데이트 : " + location.getLatitude() + ", " + location.getLongitude());

            ArrayList<Boolean> breakOut = new ArrayList<>();

            //현재위치A 와 데이터베이스에 있는 위치B를 비교해 반복문으로 직선거리를 얻음
            double distance = 0;
            Location locationA = location;
            Location locationB = new Location("Databases");

            for (int i = 0; i < district.size(); i++) {
                locationB.setLatitude(district.get(i).latitude);
                locationB.setLongitude(district.get(i).longitude);
                distance = locationA.distanceTo(locationB);

                if (distance > 50) {
                    breakOut.add(true);
                } else {
                    breakOut.add(false);
                }
            }

            int accumulate = sp.getInt("accumulate", 0);
            editor.putInt("accumulate", accumulate+1);
            editor.commit();

            for (int i = 0; i < breakOut.size(); i++) {
                if (!breakOut.get(i)) {
                    return;
                }
            }

            breakOut();

            Toast.makeText(getApplicationContext(), "자가격리구역을 이탈했습니다!", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    /* ****************** 알림 호출관련 ****************** */


    private void callNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        sendBroadcast(intent);
    }

    private void breakOut() {
        callNotification();
    }
}
