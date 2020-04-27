package com.holymoly.coronahelper.publicmask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.holymoly.coronahelper.district.MapTotalActivity;
import com.holymoly.coronahelper.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MaskFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    LocationManager manager;
    Location myLocation = new Location("PROVIDER");

    double mLatitude = 0;
    double mLongitude = 0;
    float accuracy;


    ArrayList<MaskVO> data = new ArrayList<>();
    MaskAdapter adapter;
    ListView maskList;
    FloatingActionButton fab;


    @Override
    public synchronized View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mask, container, false);
        maskList = view.findViewById(R.id.mask_list);
        fab = view.findViewById(R.id.mask_fab);

        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        getLocation();
        setLocation(myLocation);

        getData();
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        maskList.setOnItemClickListener(this);
        fab.setOnClickListener(this);
        return view;
    }


    /* ******************** 공공 데이터 요청/어댑터 적용 메소드 ******************** */


    private synchronized void getData() {

        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    StringBuilder urlBuilder = new StringBuilder("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json");
                    urlBuilder.append("?" + URLEncoder.encode("lat", "UTF-8") + "=" + mLatitude);
                    urlBuilder.append("&" + URLEncoder.encode("lng", "UTF-8") + "=" + mLongitude);
                    urlBuilder.append("&" + URLEncoder.encode("m", "UTF-8") + "=3200");

                    URL url = new URL(urlBuilder.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-type", "application/json");

                    BufferedReader rd;
                    if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                    conn.disconnect();
                    String result = sb.toString();

                    JSONObject obj1 = new JSONObject(result);
                    JSONArray obj2 = (JSONArray) obj1.get("stores");

                    for (int i = 0; i < obj2.length(); i++) {
                        JSONObject obj3 = (JSONObject) obj2.get(i);

                        String name;
                        String addr;
                        int distance;
                        String remain;

                        name = obj3.getString("name"); /* 이름 */
                        addr = obj3.getString("addr"); /* 주소 */
                        double latitude = obj3.getDouble("lat");
                        double longitude = obj3.getDouble("lng");
                        Location storeLocation = new Location("PROVIDER");
                        storeLocation.setLatitude(latitude);
                        storeLocation.setLongitude(longitude);
                        distance = (int) myLocation.distanceTo(storeLocation); /* 거리 */
                        try {
                            remain = (String) obj3.get("remain_stat");
                        } catch (Exception e) {
                            remain = "null";
                        } /* 재고 */
                        switch (remain) {
                            case "plenty":
                                remain = "100개 이상";
                                break;
                            case "some":
                                remain = "30개 이상";
                                break;
                            case "few":
                                remain = "30개 미만";
                                break;
                            case "empty":
                                remain = "판매 완료";
                                break;
                            case "break":
                                remain = "판매 중지";
                                break;
                            case "null":
                                remain = "정보 없음";
                                break;
                            default:
                                remain = "DEFAULT";
                        }
                        data.add(new MaskVO(name, addr, distance, remain, latitude, longitude));
                    }

                    //얻은 데이터를 거리순 정렬하는 Sort
                    Collections.sort(data, new Comparator<MaskVO>() {
                        @Override
                        public int compare(MaskVO o1, MaskVO o2) {
                            if (o1.getDistance() < o2.getDistance()) {
                                return -1;
                            } else if (o1.getDistance() > o2.getDistance()) {
                                return 1;
                            }
                            return 0;
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                notifier();
            }
        };

        t1.start();
        adapter = new MaskAdapter(getActivity(), R.layout.adapter_mask, data);
        maskList.setAdapter(adapter);
    }


    private synchronized void notifier() {
        notify();
    }


    /* ******************** 목록 클릭 리스너 ******************** */


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), MapTotalActivity.class);
        intent.putExtra("지도타입", "위치확인");
        intent.putExtra("StoreName", data.get(position).name);
        intent.putExtra("StoreAddr", data.get(position).addr);
        intent.putExtra("StoreLatitude", data.get(position).latitude);
        intent.putExtra("StoreLongitude", data.get(position).longitude);
        startActivity(intent);
    }


    private void setLocation(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        accuracy = location.getAccuracy();
    }

    private synchronized void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        myLocation = manager.getLastKnownLocation("gps");
    }

    @Override
    public synchronized void onClick(View view) {
        Animation rotate = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
        if(view == fab) {
            view.setAnimation(rotate);
            data.removeAll(data);
            getLocation();
            setLocation(myLocation);
            getData();
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}