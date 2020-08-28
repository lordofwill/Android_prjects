package com.geos.touchfollower;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class TouchFollowingDot extends View {
    private float _mRadius = 80f;
    private final Paint MPAINT = new Paint();

    private static final int ANIMATION_DURATION = 4000;
    private static final long ANIMATION_DELAY = 1000;
    private AnimatorSet _mPulseAnimatorSet = new AnimatorSet();

    private float _mX;
    private float _mY;

    private float _dX;
    private float _dY;


    public TouchFollowingDot(Context context) {
        super(context);
    }

    public TouchFollowingDot(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void set_dX(float xCoordinate) {
        _dX = xCoordinate;
        invalidate();
    }

    public void set_dY(float yCoordinate) {
        _dY = yCoordinate;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            _mX=event.getX();
            _mY=event.getY();

        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(0,0,_mRadius,MPAINT);
    }




}
