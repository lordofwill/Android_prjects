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
import android.graphics.PathMeasure;
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

    private static final int ANIMATION_DURATION = 1000;
    private ObjectAnimator _xAnimator;
    private AnimatorSet _mPulseAnimatorSet = new AnimatorSet();
    private Path _followingPath = new Path();
    private PathMeasure _movementLength;

    //for coordinates where I tap
    private float _mX=500f;
    private float _mY=800;
    //for coordinates where ball should start move after first tap
    private float _lX=0;
    private float _lY=0;
    //for coordinates where ball should be headed when tapped
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
            _movementLength = new PathMeasure(_followingPath, false);
            final long _mv =
                    /*(long)(1 + (Math.random()*3000));*/
                    (long)_movementLength.getLength();
            Log.i("^^V", String.valueOf(_mv));

            _xAnimator = ObjectAnimator.ofFloat(this, "xCoordinate", "yCoordinate", _followingPath);
            _xAnimator.setDuration(ANIMATION_DURATION /*+ _mv*/ );//the place need to be fixed
            _xAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            _mPulseAnimatorSet.play(_xAnimator);

            if(_mPulseAnimatorSet != null && _mPulseAnimatorSet.isRunning()) {
                //cannot get lX,lY coordinate with getAnimatedValue method..and cannot know why
                _mPulseAnimatorSet.cancel();
            }
            _mPulseAnimatorSet.start();
            _lX=_mX;
            _lY=_mY;

            _xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    _lX=(float)_xAnimator.getAnimatedValue("xCoordinate");
                    _lY=(float) _xAnimator.getAnimatedValue("yCoordinate" );
                    invalidate();
                }
            });
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(_dX,_dY,_mRadius,MPAINT);
    }

}
