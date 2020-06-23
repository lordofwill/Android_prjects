package com.example.topnavigation_20200517;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

//    private final int LODING_DISPLAY_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        setTitle("드론날다:3 ");

        IntroThread introThread = new IntroThread(handler);
        introThread.start();



    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage( Message msg) {
            if(msg.what == 1){
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                IntroActivity.this.finish();
            }
        }
    };


}
