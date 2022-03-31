package com.geos.newcustomtransition;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ViewGroup containerView;
    private Button btn, trigger;
    private TextView txt;
    private CustomTransition mytransition;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        containerView = findViewById(R.id.container);
        txt = findViewById(R.id.Da_word);
        btn = findViewById(R.id.Da_button);
        trigger = findViewById(R.id.trigger);
        mytransition = new CustomTransition();

        //화면표시 애니메이션
//        containerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {//view가 먼저 detatch되어 Animator anim에서 오류가 나서 stack overflow의 해결방법 참조
//            @Override
//            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//                containerView.removeOnLayoutChangeListener(this);
//                // Check if the runtime version is at least Lollipop
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//                    // get the center for the clipping circle
//                    int cx = containerView.getWidth() / 2;
//                    int cy = containerView.getHeight() / 2;
//
//                    // get the final radius for the clipping circle
//                    float finalRadius = (float) Math.hypot(cx, cy);
//
//                    // create the animator for this view (the start radius is zero)
//                    Animator anim = ViewAnimationUtils.createCircularReveal(containerView, cx, cy, 0f, finalRadius);//Lollipop에서부터 사용가능하다.
//                    anim.setDuration(500);
//                    // make the view visible and start the animation
//                    containerView.setVisibility(View.VISIBLE);
//                    anim.start();
//                } else {
//                    // set the view to invisible without a circular reveal animation below Lollipop
//                    containerView.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
        //.


        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(containerView, mytransition);
                containerView.setVisibility(View.VISIBLE);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

//                Toast.makeText(getApplicationContext(),"button pushed",Toast.LENGTH_SHORT).show();
                TransitionManager.beginDelayedTransition(containerView, mytransition);
                txt.setVisibility(View.VISIBLE);
            }
        });
    }
}