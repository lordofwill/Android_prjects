package com.example.bus;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
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

public class ArriveActivity extends AppCompatActivity implements ImageButton.OnClickListener, AdapterView.OnItemClickListener {

    ArrayList<ArriveVO> data = new ArrayList<>();
    ArriveAdapter adapter;
    ListView arriveList;
    ImageButton arrive_button_location, arrive_button_bookmark, arrive_button_refresh;

    String name, id, next;
    int bookmark;
    double latitude, longitude;

    ExecutorService threadPool;


    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrive);

        arriveList = findViewById(R.id.arriveList);
        arrive_button_location = findViewById(R.id.arrive_button_location);
        arrive_button_bookmark = findViewById(R.id.arrive_button_bookmark);
        arrive_button_refresh = findViewById(R.id.arrive_button_refresh);

        //누른곳에서 넘어온 정보 받기
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        id = intent.getStringExtra("id");
        next = intent.getStringExtra("next");
        bookmark = intent.getIntExtra("bookmark", 0);
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

        //북마크에 따른 별표시 설정
        if (bookmark == 1) {
            arrive_button_bookmark.setImageResource(R.drawable.icon_star_fill);
        }

        //도착정보 목록 불러오기
        setList();
        try {wait();} catch (InterruptedException e) {}

//        try{Thread.sleep(500);} catch(Exception e) {}

        //타이틀바에 뒤로가기버튼 추가
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //intent에서 넘겨받은 정류장 이름,방향,id 설정하기(상단 박스)
        TextView arrive_text1 = findViewById(R.id.arrive_text1);
        TextView arrive_text2 = findViewById(R.id.arrive_text2);
        TextView arrive_text3 = findViewById(R.id.arrive_text3);
        arrive_text1.setText(name);
        arrive_text2.setText("(" + next + " 방향)");
        arrive_text3.setText(id);

        //리스너 등록
        arrive_button_location.setOnClickListener(this);
        arrive_button_bookmark.setOnClickListener(this);
        arrive_button_refresh.setOnClickListener(this);
        arriveList.setOnItemClickListener(this);


    }




    /* ******************** 여기서부터 메소드 정의 ******************** */


    //버스 누르면 세부정보나오게 할꺼임
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    //상단 버튼액션
    @Override
    public void onClick(View v) {

        Animation animation_rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.btnrotate);
        Animation animation_scale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.btnscale);

        //지도 버튼
        if (v == arrive_button_location) {
            v.startAnimation(animation_scale);

            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        }

        //북마크 버튼
        else if (v == arrive_button_bookmark) {
            v.startAnimation(animation_scale);

            DBHelper helper = new DBHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();

            if (bookmark == 0) {
                db.execSQL("UPDATE tb_station SET bookmark=1 WHERE id=" + id);
                bookmark = 1;
                arrive_button_bookmark.setImageResource(R.drawable.icon_star_fill);
                Toast.makeText(this, "즐겨찾기에 등록했습니다.", Toast.LENGTH_SHORT).show();
            } else if (bookmark == 1) {
                db.execSQL("UPDATE tb_station SET bookmark=0 WHERE id=" + id);
                bookmark = 0;
                arrive_button_bookmark.setImageResource(R.drawable.icon_star_empty);
                Toast.makeText(this, "즐겨찾기를 해제했습니다.", Toast.LENGTH_SHORT).show();
            }

            Cursor cursor = db.rawQuery("SELECT name, bookmark FROM tb_station ORDER BY name", null);
            int i = 0;
            while (cursor.moveToNext()) {
                MainActivity.bookmark[i] = cursor.getInt(1);
                i++;
            }
            db.close();
            helper.close();
        }
        //새로고침 버튼
        else if (v == arrive_button_refresh) {
            v.startAnimation(animation_rotate);
            try {Thread.sleep(10);} catch (InterruptedException e) {}
            refresh();
        }
    }


    //익명객체를 통해 구현한 버스도착정보 받아오는 스레드풀 실행 메소드
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
            public synchronized void run() {
                try {
                    StringBuilder urlBuilder = new StringBuilder("http://api.gwangju.go.kr/json/arriveInfo"); /*URL*/
                    urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=oWF0PFTPl0sDZi7qUx2hmHkFzGto%2FfyMlyyZQ9La9P2AR4dho0G41dlBqXkj3VBYxtSGu405OerW22UxBCsUww%3D%3D"); /*Service Key*/
                    urlBuilder.append("&" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /**/
                    urlBuilder.append("&" + URLEncoder.encode("BUSSTOP_ID", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")); /*정류소 아이디*/

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


                    //변환한 json데이터를 파싱
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObj = (JSONObject) jsonParser.parse(sb.toString());

                    //파싱한 jsonObj 로 부터 BUSSTOP_LIST를 받아오기 (BUSSTOP_LIST뒤에 [ 로 시작하므로 JSONArray를 사용)
                    JSONArray busStop_List = (JSONArray) jsonObj.get("BUSSTOP_LIST");

                    for (int i = 0; i < busStop_List.size(); i++) {
                        //busStop_List는 배열형태이기 때문에 하나씩 데이터를 가져올 때 value에서 가져온다.
                        JSONObject value = (JSONObject) busStop_List.get(i);
                        String line_name = value.get("LINE_NAME").toString();     //노선 명
                        String remain_min = value.get("REMAIN_MIN").toString();    //도착 예정 시간    (곧도착 FLAG가 있는데 그냥 3분미만이면 곧도착으로 뜨게 하는게 빠를듯)
                        String busstop_name = value.get("BUSSTOP_NAME").toString();  //현재 정류소 명칭
                        String busID = value.get("LINE_ID").toString();  //버스 id

                        data.add(new ArriveVO(line_name, busstop_name, remain_min, busID));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UIThreadNotify();
            }
        };

        //스레드풀에 작업넣고 어댑터에 결과(data 리스트) 대입
        threadPool.execute(task);
        adapter = new ArriveAdapter(this, R.layout.adapter_arrive, data);
        arriveList.setAdapter(adapter);

    }

    //액티비티가 종료될때 스레드풀또한 종료시키도록 onDestroy를 재정의함
    @Override
    protected void onDestroy() {
        threadPool.shutdown();
        super.onDestroy();
    }

    //지도를 보다가(액티비티가 정지되었다가) 다시 정류장으로 넘어오면 자동새로고침 한번 시켜줌
    @Override
    protected void onRestart() {
        refresh();
        super.onRestart();
    }

    //새로고침 버튼. 스레드풀 정지-데이터 삭제-스레드풀 재호출-대기-어댑터 초기화
    private synchronized void refresh() {
        threadPool.shutdown();
        data.removeAll(data);
        setList();
        try {wait();}catch (InterruptedException e) {}
        adapter.notifyDataSetChanged();
    }

    synchronized void UIThreadNotify() {
        notify();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, RouteActivity.class);
        intent.putExtra("busID", data.get(position).busID);
        startActivity(intent);
    }
}
