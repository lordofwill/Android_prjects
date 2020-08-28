package com.geos.touchfollower;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class TouchFollowingDot extends View {
    private float _mRadius = 80f;
    private final Paint MPAINT = new Paint();

    private static final int ANIMATION_DURATION = 4000;
    private static final long ANIMATION_DELAY = 1000;
    private AnimatorSet _mPulseAnimatorSet = new AnimatorSet();

    private float _mX=500f;
    private float _mY=500;

    private float _dX;
    private float _dY;


    public TouchFollowingDot(Context context) {
        super(context);
    }

    public TouchFollowingDot(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setXCoordinate(float xCoordinate) {
        _dX = xCoordinate;
        invalidate();
    }

    public void setYCoordinate(float yCoordinate) {
        _dY = yCoordinate;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            _mX=event.getX();
            _mY=event.getY();
            Log.d("^^",""+_mX);
            if(_mPulseAnimatorSet != null && _mPulseAnimatorSet.isRunning()) {
                _mPulseAnimatorSet.cancel();
            }
            _mPulseAnimatorSet.start();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        ObjectAnimator _xAnimator = ObjectAnimator.ofFloat(this, "xCoordinate", _mX);
        _xAnimator.setDuration(ANIMATION_DURATION);
        _xAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator _yAnimator = ObjectAnimator.ofFloat(this, "yCoordinate", _mY);
        _yAnimator.setDuration(ANIMATION_DURATION);
        _yAnimator.setInterpolator(new LinearInterpolator());

        _mPulseAnimatorSet.play(_xAnimator).with(_yAnimator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(0,0,_mRadius,MPAINT);
    }

    protected void xMovementAnimation() {
        ObjectAnimator _xAnimator = ObjectAnimator.ofFloat(this, "xCoordinate", _dX);
        _xAnimator.setDuration(ANIMATION_DURATION);
        _xAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    protected void yMovementAnimation() {
        ObjectAnimator _yAnimator = ObjectAnimator.ofFloat(this, "yCoordinate", _dY);
        _yAnimator.setDuration(ANIMATION_DURATION);
        _yAnimator.setInterpolator(new LinearInterpolator());
    }


}
