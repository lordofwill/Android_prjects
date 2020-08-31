package com.geos.touchfollower;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class TouchFollowingDot extends View {
    private float _mRadius = 80f;
    private final Paint MPAINT = new Paint();

    private static final int ANIMATION_DURATION = 5000;
    private static final long ANIMATION_DELAY = 1000;
    private ObjectAnimator _xAnimator;
    private AnimatorSet _mPulseAnimatorSet = new AnimatorSet();
    private Path _followingPath = new Path();

    private float _mX=500f;
    private float _mY=800;

    private float _lX=0;
    private float _lY=0;

    private float _dX =0;
    private float _dY =0;


    public TouchFollowingDot(Context context) {
        super(context);
    }

    public TouchFollowingDot(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setXCoordinate(float xCoordinate) {
        _dX = xCoordinate;
        MPAINT.setColor(Color.YELLOW );
    }

    public void setYCoordinate(float yCoordinate) {
        _dY = yCoordinate;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            _mX=event.getX();
            _mY=event.getY();
            _followingPath.reset();

            _followingPath.setLastPoint(_lX,_lY);
            _followingPath.lineTo(_mX, _mY);
            _followingPath.moveTo(_mX,_mY);

            _xAnimator = ObjectAnimator.ofFloat(this, "xCoordinate", "yCoordinate", _followingPath);
            _xAnimator.setDuration(ANIMATION_DURATION);
            _xAnimator.setInterpolator(new AnticipateInterpolator());
            _mPulseAnimatorSet.play(_xAnimator);

            Log.d("^^",""+_mX);
            if(_mPulseAnimatorSet != null && _mPulseAnimatorSet.isRunning()) {
                _mPulseAnimatorSet.cancel();


            } else {

            }
            _mPulseAnimatorSet.start();
            _lX=_mX;
            _lY=_mY;

            _xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    Log.d("^ㅠ^", "" + (float)_xAnimator.getAnimatedValue("xCoordinate"));
                    _lX=(float)_xAnimator.getAnimatedValue("xCoordinate");
                    _lY=(float) _xAnimator.getAnimatedValue("yCoordinate" );
                    invalidate();
                }
            });



        }
        return super.onTouchEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        _followingPath.lineTo(_mX, _mY);
//        _xAnimator = ObjectAnimator.ofFloat(this, "xCoordinate", "yCoordinate", _followingPath);
//        _xAnimator.setDuration(ANIMATION_DURATION);
//        _xAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

//        ObjectAnimator _yAnimator = ObjectAnimator.ofFloat(this, "yCoordinate", _mY);
//        _yAnimator.setDuration(ANIMATION_DURATION);
//        _yAnimator.setInterpolator(new LinearInterpolator());
//
//        _mPulseAnimatorSet.play(_xAnimator).with(_yAnimator);

//        ObjectAnimator repeatAnimator = ObjectAnimator.ofFloat(this, "xCoordinate", "yCoordinate", _followingPath);
//        repeatAnimator.setStartDelay(ANIMATION_DELAY);
//        repeatAnimator.setDuration(ANIMATION_DURATION);
//
//        repeatAnimator.setRepeatCount(1);
//
//        repeatAnimator.setRepeatCount(ValueAnimator.REVERSE);

//        _mPulseAnimatorSet.play(_xAnimator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(_dX,_dY,_mRadius,MPAINT);
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
