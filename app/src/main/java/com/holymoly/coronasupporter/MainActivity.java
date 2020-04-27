package com.holymoly.coronasupporter;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.holymoly.coronasupporter.district.MyLocationService;
import com.holymoly.coronasupporter.hospital.HospitalFragment;
import com.holymoly.coronasupporter.infopage.InfoFragment;
import com.holymoly.coronasupporter.newsfeed.NewsFragment;
import com.holymoly.coronasupporter.publicmask.MaskFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private FragmentManager fragmentManager;

    private MainFragment f1;
    private HospitalFragment f2;
    private MaskFragment f3;
    private NewsFragment f4;
    private InfoFragment f5;

    private long almond;


    //드로워 관련
    DrawerLayout drawerLayout;
    View drawerView;
    Switch service_switch;

    //타이틀바 관련
    ImageView menu_icon, creditButton;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.VIBRATE}, 200);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerView = findViewById(R.id.drawerView);
        service_switch = findViewById(R.id.service_switch);
        service_switch.setChecked(locationServiceRunningCheck());


        almond = System.currentTimeMillis();
        fragmentManager = getSupportFragmentManager();

        f1 = new MainFragment();
        f2 = new HospitalFragment();
        //f3 = new MaskFragment();
        f4 = new NewsFragment();
        f5 = new InfoFragment();

        fragmentManager.beginTransaction().replace(R.id.frame_layout, f1).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, f2).commit();
        //fragmentManager.beginTransaction().add(R.id.frame_layout, f3).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, f4).commit();
        fragmentManager.beginTransaction().add(R.id.frame_layout, f5).commit();
        fragmentManager.beginTransaction().show(f1).commit();
        fragmentManager.beginTransaction().hide(f2).commit();
        //fragmentManager.beginTransaction().hide(f3).commit();
        fragmentManager.beginTransaction().hide(f4).commit();
        fragmentManager.beginTransaction().hide(f5).commit();

        menu_icon = findViewById(R.id.menu_icon);
        creditButton = findViewById(R.id.creditButton);
        service_switch.setOnCheckedChangeListener(this);
        menu_icon.setOnClickListener(this);
        creditButton.setOnClickListener(this);

        sp = getSharedPreferences("corona", Activity.MODE_PRIVATE);

        BottomNavigationView bottomNavigationView = findViewById(R.id.frame_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        //자동로그인 부분

    }


    @Override
    public void onClick(View v) {
        if (v == menu_icon) {
            drawerLayout.openDrawer(drawerView);
        } else if (v == creditButton) {
            CreditDialog credit = new CreditDialog(MainActivity.this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Intent intent = new Intent(this, MyLocationService.class);
            startService(intent);
        } else {
            Intent intent = new Intent(this, MyLocationService.class);
            stopService(intent);
        }
    }


    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_item1:
                   fragmentManager.beginTransaction().show(f1).commit();
                    fragmentManager.beginTransaction().hide(f2).commit();
                    fragmentManager.beginTransaction().hide(f3).commit();
                    fragmentManager.beginTransaction().hide(f4).commit();
                    fragmentManager.beginTransaction().hide(f5).commit();
                    break;

                case R.id.menu_item2:
                    fragmentManager.beginTransaction().hide(f1).commit();
                    fragmentManager.beginTransaction().show(f2).commit();
                    fragmentManager.beginTransaction().hide(f3).commit();
                    fragmentManager.beginTransaction().hide(f4).commit();
                    fragmentManager.beginTransaction().hide(f5).commit();
                    break;

                case R.id.menu_item3:
                    fragmentManager.beginTransaction().hide(f1).commit();
                    fragmentManager.beginTransaction().hide(f2).commit();
                    fragmentManager.beginTransaction().show(f3).commit();
                    fragmentManager.beginTransaction().hide(f4).commit();
                    fragmentManager.beginTransaction().hide(f5).commit();
                    break;

                case R.id.menu_item4:
                    fragmentManager.beginTransaction().hide(f1).commit();
                    fragmentManager.beginTransaction().hide(f2).commit();
                    fragmentManager.beginTransaction().hide(f3).commit();
                    fragmentManager.beginTransaction().show(f4).commit();
                    fragmentManager.beginTransaction().hide(f5).commit();
                    break;
                case R.id.menu_item5:
                    fragmentManager.beginTransaction().hide(f1).commit();
                    fragmentManager.beginTransaction().hide(f2).commit();
                    fragmentManager.beginTransaction().hide(f3).commit();
                    fragmentManager.beginTransaction().hide(f4).commit();
                    fragmentManager.beginTransaction().show(f5).commit();
                    break;
            }
            return true;
        }
    }



    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - almond > 3000) {
            Toast.makeText(this, "뒤로가기를 다시 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            almond = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    public boolean locationServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.holymoly.coronasupporter.district.MyLocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "위치 정보 요청이 거부됨", Toast.LENGTH_LONG).show();
                finish();
            } else {
                f3 = new MaskFragment();
                fragmentManager.beginTransaction().add(R.id.frame_layout, f3).commit();
                fragmentManager.beginTransaction().hide(f3).commit();
            }
        }
    }


}
