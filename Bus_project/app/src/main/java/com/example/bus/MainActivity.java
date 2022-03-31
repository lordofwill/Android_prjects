package com.example.bus;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;


//메인 액티비티 = StationActivity
public class MainActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener {

    //서치박스와 정류장 리스트
    EditText searchBox;
    ArrayList<StationVO> data = new ArrayList<>();
    StationAdapter adapter;
    ListView stationList;

    //드로워 관련 필드
    DrawerLayout drawerLayout;
    ListView lv_bookmark;
    BookmarkAdapter book_adapter;
    ArrayList<StationVO> data_forBookmark = new ArrayList<>();
    ActionBarDrawerToggle toggle;

    //데이터베이스->배열 필드(setDatabase 메소드에서 배열에 대입)
    String[] name = new String[3544];
    int[] id = new int[3544];
    String[] next = new String[3544];
    static int[] bookmark = new int[3544];
    double[] latitude = new double[3544];
    double[] longitude = new double[3544];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //객체 id등록
        drawerLayout = findViewById(R.id.drawer_layout);
        lv_bookmark = findViewById(R.id.lv_bookmark);
        searchBox = findViewById(R.id.searchBox);
        stationList = findViewById(R.id.stationList);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();

        //퍼미션
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);

        //데이터베이스 배열등록
        setDatabase();

        //북마크가 1일경우 드로워에 추가
        for (int i = 0; i < bookmark.length; i++) {
            if (bookmark[i] == 1) {
                data_forBookmark.add(new StationVO(name[i], id[i], next[i], bookmark[i], latitude[i], longitude[i]));
            }
        }


        //어댑터 등록 : 정류장 리스트
        adapter = new StationAdapter(this, R.layout.adapter_station, data);
        stationList.setAdapter(adapter);
        //어댑터 등록 : 즐겨찾기 리스트
        book_adapter = new BookmarkAdapter(this, R.layout.adapter_station, data_forBookmark);
        lv_bookmark.setAdapter(book_adapter);

        //리스너 등록
        stationList.setOnItemClickListener(this);
        searchBox.addTextChangedListener(this);
        lv_bookmark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ArriveActivity.class);
                intent.putExtra("name", data_forBookmark.get(position).name);
                intent.putExtra("id", data_forBookmark.get(position).id);
                intent.putExtra("next", data_forBookmark.get(position).next);
                intent.putExtra("bookmark", data_forBookmark.get(position).bookmark);
                intent.putExtra("latitude", data_forBookmark.get(position).latitude);
                intent.putExtra("longitude", data_forBookmark.get(position).longitude);
                startActivity(intent);
                drawerLayout.closeDrawer(Gravity.LEFT);//드로워 닫기
            }
        });

    }







    /* ************************여기서부터 메소드 정의 및 오버라이드************************ */


    //타이틀바 메뉴생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.station_menu, menu);
        return true;
    }

    //상단 타이틀바의 액션버튼 코드. 드로워버튼 및 지도버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mapmypotion_button) {
            Intent intent = new Intent(MainActivity.this, MapMyPotion.class);
            startActivity(intent);
        } else if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //사용 안함(그래도 있어야함)
    }

    @Override
    public void afterTextChanged(Editable s) {
        //사용 안함(그래도 있어야함)
    }

    //문자열이 바뀔때마다 정류장리스트 삭제및 생성
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 1) {
            data.removeAll(data);
            for (int i = 0; i < name.length; i++) {
                if (name[i].contains(s)) {
                    data.add(new StationVO(name[i], id[i], next[i], bookmark[i], latitude[i], longitude[i]));
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    //데이터베이스를 배열에 넣는 과정
    private void setDatabase() {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, id, next, bookmark, latitude, longitude FROM tb_station ORDER BY name", null);

        int i = 0;
        while (cursor.moveToNext()) {
            name[i] = cursor.getString(0);
            id[i] = cursor.getInt(1);
            next[i] = cursor.getString(2);
            bookmark[i] = cursor.getInt(3);
            latitude[i] = cursor.getDouble(4);
            longitude[i] = cursor.getDouble(5);
            i++;
        }
        db.close();
    }

    //리스트 클릭시 정류장으로 이동
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ArriveActivity.class);
        intent.putExtra("name", data.get(position).name);
        intent.putExtra("id", data.get(position).id);
        intent.putExtra("next", data.get(position).next);
        intent.putExtra("bookmark", data.get(position).bookmark);
        intent.putExtra("latitude", data.get(position).latitude);
        intent.putExtra("longitude", data.get(position).longitude);
        startActivity(intent);
    }


    //화면이 비활성됬다가 돌아올경우 리스트 초기화(북마크 관련)
    @Override
    protected void onRestart() {
        super.onRestart();
        String str = searchBox.getText().toString();
        if (str.length() > 1) {
            data.removeAll(data);
            for (int i = 0; i < name.length; i++) {
                if (name[i].contains(str)) {
                    data.add(new StationVO(name[i], id[i], next[i], bookmark[i], latitude[i], longitude[i]));
                }
            }
            adapter.notifyDataSetChanged();
        }
        data_forBookmark.removeAll(data_forBookmark);
        for (int i = 0; i < bookmark.length; i++) {
            if (bookmark[i] == 1) {
                data_forBookmark.add(new StationVO(name[i], id[i], next[i], bookmark[i], latitude[i], longitude[i]));
            }
        }
        book_adapter.notifyDataSetChanged();
    }

}
