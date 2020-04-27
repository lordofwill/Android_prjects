package com.holymoly.coronasupporter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    Context context;

    public DBHelper(Context context) {
        super(context, "district_database", null, DATABASE_VERSION);
        this.context =  context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE tb_district" + "(id integer primary key autoincrement, latitude, longitude)";
        db.execSQL(query);
        query = "CREATE TABLE tb_hospital1" + "(id integer primary key autoincrement, name, addr, tel)";
        db.execSQL(query);
        query = "CREATE TABLE tb_hospital2" + "(id integer primary key autoincrement, name, addr, tel)";
        db.execSQL(query);

        setHospitalDatabase(context, db, "hospital_data1.json", "tb_hospital1");
        System.out.println("국민안심병원 데이터 세팅 완료");
        setHospitalDatabase(context, db, "hospital_data2.json", "tb_hospital2");
        System.out.println("선별진료소 데이터 세팅 완료");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == DATABASE_VERSION) {
            String query = "DROP TABLE tb_district";
            db.execSQL(query);
            onCreate(db);
            db.close();
        }
    }


    private void setHospitalDatabase(Context context, SQLiteDatabase db, String fileName, String table){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(fileName)));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while(line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            reader.close();

            String json = sb.toString();
            JSONObject obj1 = new JSONObject(json);
            JSONArray obj2 = (JSONArray) obj1.get("result");
            for(int i=0; i<obj2.length(); i++) {
                JSONObject obj3 = (JSONObject) obj2.get(i);
                String query = "INSERT INTO "+ table +" (name, addr, tel) VALUES ('"+
                        obj3.getString("의료기관명") +"','"+
                        obj3.getString("주소")+"','"+
                        obj3.getString("전화번호")+"')";
                db.execSQL(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
