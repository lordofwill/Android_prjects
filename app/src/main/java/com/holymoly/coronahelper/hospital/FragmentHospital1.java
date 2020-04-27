package com.holymoly.coronahelper.hospital;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.holymoly.coronahelper.DBHelper;
import com.holymoly.coronahelper.district.MapTotalActivity;
import com.holymoly.coronahelper.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentHospital1 extends Fragment implements AdapterView.OnItemClickListener, TextWatcher {
    Geocoder geocoder;
    ListView listView;
    EditText searchBox;

    ArrayList<HospitalVO> data = new ArrayList<>();
    HospitalAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hospital_inside, container, false);

        geocoder = new Geocoder(getActivity());

        listView = v.findViewById(R.id.hospital_listView);
        searchBox = v.findViewById(R.id.hospital_searchBox);

        listView.setOnItemClickListener(this);
        searchBox.addTextChangedListener(this);


        setDatabaseList();
        return v;
    }


    private void setDatabaseList() {
        DBHelper helper = new DBHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tb_hospital1", null);

        String name, addr, tel;
        while (cursor.moveToNext()) {
            name = cursor.getString(1);
            addr = cursor.getString(2);
            tel = cursor.getString(3);
            data.add(new HospitalVO(name, addr, tel));
        }
        helper.close();
        db.close();
        cursor.close();

        adapter = new HospitalAdapter(getActivity(), R.layout.adapter_hospital, data);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName(data.get(position).addr, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = list.get(0);
        double latitude = address.getLatitude();
        double longitude = address.getLongitude();

        Intent intent = new Intent(getContext(), MapTotalActivity.class);
        intent.putExtra("지도타입", "위치확인");
        intent.putExtra("StoreName", data.get(position).name);
        intent.putExtra("StoreAddr", data.get(position).addr);
        intent.putExtra("StoreLatitude", latitude);
        intent.putExtra("StoreLongitude", longitude);
        startActivity(intent);
    }



    /* ******************** 문자열 감지 ******************** */

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 1) {
            data.removeAll(data);

            DBHelper helper = new DBHelper(getActivity());
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM tb_hospital1", null);

            String name, addr, tel;
            while (cursor.moveToNext()) {
                name = cursor.getString(1);
                addr = cursor.getString(2);
                tel = cursor.getString(3);

                if (name.contains(s) || addr.contains(s)) {
                    data.add(new HospitalVO(name, addr, tel));
                }
            }
            helper.close();
            db.close();
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
