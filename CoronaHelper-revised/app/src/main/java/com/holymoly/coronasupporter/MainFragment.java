package com.holymoly.coronasupporter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.holymoly.coronasupporter.district.MapTotalActivity;
import com.holymoly.coronasupporter.district.MyDistrictVO;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainFragment extends Fragment implements View.OnClickListener {
    Button button1;
    TextView examiner, victim, recovered, dead, time;
    TextView main_text1, main_text2;
    ExecutorService threadPool;
    Handler statusHandler = new Handler();
    SharedPreferences sp;
    Integer v_victim=0, v_recovered=0, v_dead=0, v_testing=0;
    BarChart chart;

    @Override
    public synchronized View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        sp = getActivity().getSharedPreferences("corona", Activity.MODE_PRIVATE);
        button1 = view.findViewById(R.id.button1);
        button1.setOnClickListener(this);
        chart = view.findViewById(R.id.barchart);

        victim = view.findViewById(R.id.txt_victim);
        examiner = view.findViewById(R.id.txt_examiner);
        recovered = view.findViewById(R.id.txt_recovered);
        dead = view.findViewById(R.id.txt_dead);
        time = view.findViewById(R.id.time);
        main_text1 = view.findViewById(R.id.main_text1);
        main_text2 = view.findViewById(R.id.main_text2);
        time.setText(new SimpleDateFormat("yyyy년 MM월 dd일").format(Calendar.getInstance().getTime()));
        AcquireNumber();
        try {
            wait();
        } catch (InterruptedException e) {
        }

        Bchart(chart);

        return view;
    }

    private synchronized void AcquireNumber() {
        threadPool = new ThreadPoolExecutor(
                0,
                10,
                5,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>()
        );
        Runnable acquire = new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder urlBuilder = new StringBuilder("http://api.corona-19.kr/korea/?serviceKey=08dca4ae45bd27546320688a3a67194dd"); /*URL*/
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


                    JSONObject jsonObj = new JSONObject(sb.toString());
                    final String S_victim = jsonObj.get("TotalCase").toString();
                    final String S_recovered = jsonObj.get("TotalRecovered").toString();
                    final String S_dead = jsonObj.get("TotalDeath").toString();
                    final String S_testing = jsonObj.get("NowCase").toString();

                    v_victim = Integer.parseInt(S_victim.replaceAll(",",""));
                    v_recovered = Integer.parseInt(S_recovered.replaceAll(",",""));
                    v_dead = Integer.parseInt(S_dead.replaceAll(",",""));
                    v_testing = Integer.parseInt(S_testing.replaceAll(",",""));

                    statusHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            victim.setText("확진자\n" + S_victim + "명");
                            recovered.setText("완치자\n" + S_recovered + "명");
                            dead.setText("사망자\n" + S_dead + "명");
                            examiner.setText("검진 중\n" + S_testing + "명");
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
                notifier();
            }
        };
        threadPool.execute(acquire);

    }

    synchronized void notifier() {
        notify();
    }



    @Override
    public void onClick(View view) {
        if (view == button1) {
            Intent intent = new Intent(getActivity(), MapTotalActivity.class);
            ArrayList<MyDistrictVO> list = new ArrayList<>();

            //인텐트로 메뉴타입을 보내고
            intent.putExtra("지도타입", "자가격리");

            //인텐트로 내 격리구역 리스트를 보내주고
            DBHelper helper = new DBHelper(getActivity());
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT latitude, longitude FROM tb_district", null);
            while (cursor.moveToNext()) {
                list.add(new MyDistrictVO(cursor.getDouble(0), cursor.getDouble(1)));
            }
            helper.close();
            db.close();
            intent.putExtra("격리구역리스트", list);

            //인텐트 발생
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        int accumulate = sp.getInt("accumulate", 0);
        if(accumulate<60) {
            main_text1.setText(accumulate+"분");
        } else {
            main_text1.setText(accumulate/60+"시간");
        }

        DBHelper helper = new DBHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tb_district", null);
        main_text2.setText(cursor.getCount()+"/5");
        helper.close();
        db.close();
        super.onResume();
    }

    public void Bchart (BarChart chart) {


        ArrayList NoOfHuman = new ArrayList();

        NoOfHuman.add(new BarEntry(v_testing,0));
        NoOfHuman.add(new BarEntry(v_victim,1));
        NoOfHuman.add(new BarEntry(v_recovered,2));
        NoOfHuman.add(new BarEntry(v_dead,3));

        ArrayList graphNametag = new ArrayList();

        graphNametag.add("확진자");
        graphNametag.add("완치자");
        graphNametag.add("사망자");
        graphNametag.add("검진 중");

        BarDataSet barDataSet = new BarDataSet(NoOfHuman, "No of People");
        chart.animateY(3000);

        BarData data = new BarData(graphNametag, barDataSet);
        data.setValueTextSize(15);
        data.setValueTextColor(Color.WHITE);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.setDescription(null);
        chart.setTouchEnabled(false);
        barDataSet.setBarSpacePercent(20);

        Legend barName = chart.getLegend();
        barName.setEnabled(false);

        chart.setViewPortOffsets(0f, 30f, 0f, 0f);

        int[] colors = new int [] {0X330000FF,0X330000FF,0X330000FF,0X330000FF};
        barDataSet.setColors(colors);
        chart.setData(data);
    }
}