package com.example.bus;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RouteActivity extends AppCompatActivity {

    ExecutorService threadPool;

    ArrayList<RouteVO> dataUp = new ArrayList<>();
    ArrayList<RouteVO> dataDown = new ArrayList<>();

    RouteAdapter routeAdapterUp;
    RouteAdapter routeAdapterDown;

    ListView routeListUp;
    ListView routeListDown;

    String BusID;


    @Override
    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Intent intent = getIntent();
        BusID = intent.getStringExtra("busID");

        routeListUp = findViewById(R.id.routeListUp);
        routeListDown = findViewById(R.id.routeListDown);


        setList();
        try {wait();} catch (Exception e) {}

        setListViewHeightBasedOnChildren(routeListUp);
        setListViewHeightBasedOnChildren(routeListDown);
        routeListUp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                DBHelper helper = new DBHelper(RouteActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT name, id, next, bookmark, latitude, longitude FROM tb_station WHERE id ="+dataUp.get(position).busstopID,null);
                String name = "";
                int id = 0;
                int bookmark=0;
                String next="";
                double latitude=0.0;
                double longitude=0.0;

                int i=0;
                while(cursor.moveToNext()){
                    name = cursor.getString(0);
                    id = cursor.getInt(1);
                    next= cursor.getString(2);
                    bookmark = cursor.getInt(3);
                    latitude = cursor.getDouble(4);
                    longitude = cursor.getDouble(5);
                    i++;
                }

                Intent intent = new Intent(RouteActivity.this, ArriveActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("id",Integer.toString(id));
                intent.putExtra("next",next);
                intent.putExtra("bookmark",bookmark);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivity(intent);
                db.close();
            }
        });

        routeListDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                DBHelper helper = new DBHelper(RouteActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT name, id, next, bookmark, latitude, longitude FROM tb_station WHERE id ="+dataDown.get(position).busstopID,null);
                String name = "";
                int id = 0;
                int bookmark=0;
                String next="";
                double latitude=0.0;
                double longitude=0.0;

                int i=0;
                while(cursor.moveToNext()){
                    name = cursor.getString(0);
                    id = cursor.getInt(1);
                    next= cursor.getString(2);
                    bookmark = cursor.getInt(3);
                    latitude = cursor.getDouble(4);
                    longitude = cursor.getDouble(5);
                    i++;
                }

                Intent intent = new Intent(RouteActivity.this, ArriveActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("id",Integer.toString(id));
                intent.putExtra("next",next);
                intent.putExtra("bookmark",bookmark);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivity(intent);
                db.close();
            }
        });
    }

    private synchronized void setList() {

        //스레드풀을 짜서 5초이상 아무행동 하지않는 스레드는 제거함
        threadPool = new ThreadPoolExecutor(
                0,
                10,
                5,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>()
        );

        //작업내용(익명객체)
        Runnable task = new Runnable() {
            @Override
            public void run() {


                {
                    try {

                        StringBuilder urlBuilder = new StringBuilder("http://api.gwangju.go.kr/json/lineStationInfo"); /*URL*/
                        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=oWF0PFTPl0sDZi7qUx2hmHkFzGto%2FfyMlyyZQ9La9P2AR4dho0G41dlBqXkj3VBYxtSGu405OerW22UxBCsUww%3D%3D"); /*Service Key*/
                        urlBuilder.append("&" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /**/
                        urlBuilder.append("&" + URLEncoder.encode("LINE_ID", "UTF-8") + "=" + URLEncoder.encode(BusID, "UTF-8")); /*노선 ID*/


                        URL url = new URL(urlBuilder.toString());
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();


                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Content-type", "application/json");

                        System.out.println("Response code: " + conn.getResponseCode());


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

                        JSONParser jsonParser = new JSONParser();
                        System.out.println(sb.toString());

                        JSONObject jsonObj = (JSONObject) jsonParser.parse(sb.toString());
                        JSONArray busStop_List = (JSONArray) jsonObj.get("BUSSTOP_LIST");
                        int seperater = 0;
                        System.out.println(busStop_List.size());
                        for (int i = 0; i < busStop_List.size(); i++) {
                            //busStop_List는 배열형태이기 때문에 하나씩 데이터를 가져올 때 value에서 가져온다.

                            JSONObject value = (JSONObject) busStop_List.get(i);

                            String busstop_name = value.get("BUSSTOP_NAME").toString();  //현재 정류소 명칭
                            int returnFlag = Integer.parseInt(value.get("RETURN_FLAG").toString());
                            String busstop_id = value.get("BUSSTOP_ID").toString();


                            if (returnFlag == 3) {
                                ++seperater;
                            }

                            if (seperater == 0 || returnFlag == 3) {
                                dataUp.add(new RouteVO(busstop_name, returnFlag, busstop_id));
                            } else if (seperater > 0) {
                                dataDown.add(new RouteVO(busstop_name, returnFlag, busstop_id));
                            }
                        }

                        rd.close();
                        conn.disconnect();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    UIThreadNotify();

                }
            }
        };

        //스레드풀에 작업넣고 어댑터에 결과(data 리스트) 대입
        threadPool.execute(task);

        routeAdapterUp = new RouteAdapter(this, R.layout.adapter_route, dataUp);
        routeListUp.setAdapter(routeAdapterUp);


        routeAdapterDown = new RouteAdapter(this, R.layout.adapter_route, dataDown);
        routeListDown.setAdapter(routeAdapterDown);

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    synchronized void UIThreadNotify() {
        notify();
    }

}



