package com.laocuo.raining;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;


public class Rain extends FrameLayout {
    private final String TAG = "laocuo";

    private boolean isStart;
    private RainRunnable mRunnable;
    private int width, height;

    public Rain(Context context) {
        this(context, null);
    }

    public Rain(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRunnable = new RainRunnable();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private class RainRunnable implements Runnable{

        @Override
        public void run() {
            if (isStart) {
                addDrip();
                postDelayed(mRunnable, (long) (Drip.DURATION * 0.5f));
            }
        }
    }

    private void addDrip() {
        int dripW = Utils.dipToPx(getContext(), 30);
        Drip view = new Drip(getContext(), dripW);
        view.setListener(new Drip.DripListener() {
            @Override
            public void complete(Drip v) {
                performAnim(v);
            }
        });
        view.setX((float) (Math.random()*(width - dripW)));
        addView(view);
    }

    private void performAnim(final Drip v) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(v, "translationY", 0, height);
        oa.setInterpolator(new AccelerateInterpolator());
        oa.setDuration(1000);
        oa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Rain.this.removeView(v);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        oa.start();
    }

    public void start() {
        isStart = true;
        postDelayed(mRunnable, (long) (Math.random()*500));
    }

    public void stop() {
        isStart = false;
    }
}