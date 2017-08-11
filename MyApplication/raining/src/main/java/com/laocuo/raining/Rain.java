package com.laocuo.raining;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;


public class Rain extends FrameLayout {
    private final String TAG = "laocuo";

    //默认水滴大小,单位dip
    private final int DEFAULT_DRIP_WIDTH = 30;

    //默认水滴生成的间隔时间,单位毫秒
    private final int DEFAULT_SPEED = 400;

    private boolean isStart;
    private RainRunnable mRunnable;
    private int width, height;
    private int drip_width, speed;

    public Rain(Context context) {
        this(context, null);
    }

    public Rain(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Rain);
        drip_width = (int) ta.getDimension(R.styleable.Rain_drip_width, DEFAULT_DRIP_WIDTH);
        speed = ta.getInt(R.styleable.Rain_speed, DEFAULT_SPEED);
        ta.recycle();
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
                postDelayed(mRunnable, speed);
            }
        }
    }

    private void addDrip() {
        if (width > 0) {
            int dripW = Utils.dipToPx(getContext(), drip_width);
            Drip view = new Drip(getContext(), dripW, speed * 2);
            view.setListener(new Drip.DripListener() {
                @Override
                public void complete(Drip v) {
                    performAnim(v);
                }
            });
            view.setX((float) (Math.random() * (width - dripW)));
            addView(view);
        }
    }

    //水滴下落动画
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