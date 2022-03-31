package com.geos.newcustomtransition;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class CustomTransition extends Transition {
    private static final String PROPNAME_HEIGHT =
            "com.geos.custom_transition_practice:CustomTransition:height";
    private static final String PROPNAME_WIDTH =
            "com.geos.custom_transition_practice:CustomTransition:width";
    private static final String PROPNAME_VISIBILITY =
            "com.geos.custom_transition_practice:CustomTransition:visiblity";

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {

        final View view = endValues.view;
        int startHeight = (int)startValues.values.get(PROPNAME_HEIGHT);
        int startWidth = (int)startValues.values.get(PROPNAME_WIDTH);
        int endHeight = (int)endValues.values.get(PROPNAME_HEIGHT);
        int endtWidth = (int)endValues.values.get(PROPNAME_WIDTH);
        int startVisiblity = (int)startValues.values.get(PROPNAME_VISIBILITY);
        int endVisiblity = (int)endValues.values.get(PROPNAME_VISIBILITY);



//        Drawable startBackground = (Drawable) startValues.values.get(PROPNAME_BACKGROUND);
//        Drawable endBackground = (Drawable) endValues.values.get(PROPNAME_BACKGROUND);
//
//        ColorDrawable startColor = (ColorDrawable) startBackground;
//        ColorDrawable endColor = (ColorDrawable) endBackground;

//        if (startHeight != endHeight) {

                    // Check if the runtime version is at least Lollipop
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        // get the center for the clipping circle
                        int cx = endtWidth / 2;
                        int cy = endHeight / 2;

                        // get the final radius for the clipping circle
                        float finalRadius = (float) Math.hypot(cx, cy);

                        // create the animator for this view (the start radius is zero)
                        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius);//Lollipop에서부터 사용가능하다.
                        anim.setDuration(7500);
                        // make the view visible and start the animation
//                        view.setVisibility(View.VISIBLE);
                        return anim;
                    } else {
                        // set the view to invisible without a circular reveal animation below Lollipop
//                        view.setVisibility(View.INVISIBLE);
                    }
//                }


//
//            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(),
//                    startColor.getColor(), endColor.getColor());
//            animator.setInterpolator(new BounceInterpolator());
//            animator.setDuration(3000);
//            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    Object value = animation.getAnimatedValue();
//
//                    if (null != value) {
//                        view.setBackgroundColor((Integer) value);
//                    }
//                }
//            });
            // Return the Animator object to the transitions framework. As the framework changes
            // between the starting and ending layouts, it applies the animation you've created.
//            return animator;
//        }
//        }
        // For non-ColorDrawable backgrounds, we just return null, and no animation will take place.
        return null;
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        transitionValues.values.put(PROPNAME_HEIGHT, view.getHeight());
        transitionValues.values.put(PROPNAME_WIDTH, view.getWidth());
        transitionValues.values.put(PROPNAME_VISIBILITY,view.getVisibility());
    }
}
