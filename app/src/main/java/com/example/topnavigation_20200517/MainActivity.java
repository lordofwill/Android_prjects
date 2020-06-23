package com.example.topnavigation_20200517;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    Context context;

    private BackPressCloseHandler backPressCloseHandler;


    private BottomNavigationView navigationView; // 탑 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private frag1 frag1;
    private frag2 frag2;
    private frag3 frag3;


    TextView Advertising;
//퍼미션
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            initView();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(context, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };
//퍼미션

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this.getBaseContext();
        setTitle("드론날다 :3");
        checkPermissions();
    }
//퍼미션 체크하는 부분
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) { // 마시멜로(안드로이드 6.0) 이상 권한 체크
            TedPermission.with(context)
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage("앱을 이용하기 위해서는 접근 권한이 필요합니다")
                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    })
                    .check();

        } else {
            initView();
        }
    }
//퍼미션 체크하는 부분

//메인의 실행부
    private void initView() {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        backPressCloseHandler = new BackPressCloseHandler(this);
        navigationView = findViewById(R.id.topNavi);
        navigationView.setItemIconTintList(null);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch (menuItem.getItemId()){
                    case R.id.action_sun:
                        setFrag(0);
                        break;
                    case R.id.action_map:
                        setFrag(1);
                        break;
                    case R.id.action_web:
                        try {
                            setFrag(2);
                        }catch (Exception e){
                        }
                        System.out.println("메모리 폭발");
                        break;
                }
                return true;
            }
        });
        frag1 = new frag1();
        frag2 = new frag2();
        frag3 = new frag3();

        fm.beginTransaction().add(R.id.frame, frag1).commit();
        fm.beginTransaction().add(R.id.frame, frag2).commit();
        fm.beginTransaction().add(R.id.frame, frag3).commit();
        fm.beginTransaction().hide(frag1).commit();
        fm.beginTransaction().hide(frag2).commit();
        fm.beginTransaction().show(frag3).commit();


        setFrag(0);
    }
//메인 실행부


        //프래그 교체되는 실행문
    private void setFrag(int n){
//        fm = getSupportFragmentManager();
//        ft = fm.beginTransaction();
        switch (n){
            case 0:
//                ft.replace(R.id.frame, frag1);
//                ft.commit();
                fm.beginTransaction().show(frag1).commit();
                fm.beginTransaction().hide(frag2).commit();
                fm.beginTransaction().hide(frag3).commit();
                break;
            case 1:
//                ft.replace(R.id.frame, frag2);
//                ft.commit();
                fm.beginTransaction().hide(frag1).commit();
                fm.beginTransaction().show(frag2).commit();
                fm.beginTransaction().hide(frag3).commit();
                break;
            case 2:
//                ft.replace(R.id.frame, frag3);
//                ft.commit();
                fm.beginTransaction().hide(frag1).commit();
                fm.beginTransaction().hide(frag2).commit();
                fm.beginTransaction().show(frag3).commit();
                break;

        }

    }


    @Override
    public void onBackPressed() {
       backPressCloseHandler.onBackPressed();
    }
}
