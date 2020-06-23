package com.example.bus;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    Context context;

    String[] name = new String[3544];
    int[] id = new int[3544];
    String[] next = new String[3544];
    int[] bookmark = new int[3544];
    double[] longitude = new double[3544];
    double[] latitude = new double[3544];

    public DBHelper(Context context) {
        super(context, "busdb", null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //DBHelper의 onCreate()메소드는 앱을 설치후 처음 실행될때만 수행함.

        //Assets폴더에서 JSON파일 읽어다 name,id,next배열에 데이터를 집어넣음
        try {
            String json = readAssets(context);
            jsonParsing(json);
        } catch (Exception e) {}

        //테이블 생성
        String query1 = "CREATE TABLE tb_station" + "(name, id primary key, next, latitude, longitude, bookmark)";
        db.execSQL(query1);

        //위에서 넣은 name,id,next배열의 데이터를 테이블에 삽입함
        for(int i=0; i<3544; i++) {
            query1 = "INSERT INTO tb_station (name, id, next, latitude, longitude, bookmark) VALUES ('"+
                    name[i]+"',"+
                    id[i]+",'"+
                    next[i]+"',"+
                    latitude[i]+","+
                    longitude[i]+","+
                    bookmark[i]+")";
            db.execSQL(query1);
        }

        Log.i("DB","DB설정 완료");

    }




    //Assets폴더에서 station_data.json 읽어서 문자열로 바꿔서 리턴
    public String readAssets(Context context) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("station_data.json")));

        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while(line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        String result = sb.toString();

        return result;
    }


    //매개변수로 json파일 읽어서 배열에 대입함.
    public void jsonParsing(String json) throws Exception{
        JSONObject obj = new JSONObject(json);
        JSONArray parse_STATION_LIST = (JSONArray) obj.get("STATION_LIST");


        for(int i=0; i<3544; i++) {
            JSONObject data = (JSONObject) parse_STATION_LIST.get(i);
            name[i] = (String)data.get("BUSSTOP_NAME");
            id[i] = (Integer)data.get("BUSSTOP_ID");
            try {next[i] = (String) data.get("NEXT_BUSSTOP");}
            catch(Exception e) {next[i] = "정보없음";}
            latitude[i] = (Double)data.get("LATITUDE");
            try {longitude[i] = (Double)data.get("LONGITUDE");}
            catch(Exception e) {longitude[i] = 126.38411294;}
            //공공데이터에 효산삼거리 위도가 없어서 개젖같은 오류가 남. 예외처리로 효산삼거리는 직접 위도를 넣음.
            System.out.println(name[i]);
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion ==  DATABASE_VERSION) {
            db.execSQL("DROP TABLE tb_station");
            onCreate(db);
        }
    }

}
