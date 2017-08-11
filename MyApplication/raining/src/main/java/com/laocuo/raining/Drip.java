package com.laocuo.raining;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;


public class Drip extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private final String TAG = "laocuo";

    //默认水滴下落时间,单位毫秒
    public static final int DURATION = 1000;

    private final boolean showDebugPoints = false;

    private int duration = DURATION;
    private int width, height;
    private int ovalW, ovalH, a, b, gap;
    private RectF mOval, mOvalLine;
    private Path mPath;
    private Paint mPaint, mPaintPoint, mPaintLine;
    private ValueAnimator mAnimator;
    private int bottom;

    /*
     * left,ritht 是两边的起始点
     * left_x,right_x 是贝塞尔曲线参考点
     * left_bottom,right_bottom 是两边的结束点
     */
    private Point left, left_x, right, right_x, left_bottom, right_bottom;

    private DripListener mListener;
    private boolean isInit;

    public interface DripListener {
        void complete(Drip v);
    }

    public Drip(Context context, int w, int s) {
        this(context, null);
        this.width = this.height = w;
        duration = s;
    }

    public Drip(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaintLine = new Paint();
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeCap(Paint.Cap.ROUND);
        mPaintLine.setColor(Color.WHITE);
        mPaintLine.setAntiAlias(true);
        mPaintPoint = new Paint();
        mPaintPoint.setStyle(Paint.Style.FILL);
        mPaintPoint.setStrokeWidth(2f);
        mPaintPoint.setAntiAlias(true);
        mPath = new Path();
    }

    private void init() {
        if (isInit == false) {
            ovalW = (int) (width * 0.6f);
            ovalH = (int) (height * 0.8f);
            a = ovalH / 2;
            b = ovalW / 2;
            gap = b / 2;
            mOval = new RectF(0, 0, ovalW, ovalH);
            mOvalLine = new RectF(0, 0, ovalW*0.7f, ovalH*0.7f);
            mPaintLine.setStrokeWidth(ovalW*0.06f);
            left = new Point(width / 2 - gap, 0);
            left_x = new Point(width / 2, 0);
            left_bottom = new Point(width / 2, 0);
            right = new Point(width + gap, 0);
            right_x = new Point(width / 2, 0);
            right_bottom = new Point(width / 2, 0);
            mAnimator = ValueAnimator.ofInt(0, height);
            mAnimator.setDuration(duration);
            mAnimator.addUpdateListener(this);
            mAnimator.addListener(this);
            isInit = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        if (this.width <= 0) {
            if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
                width = Utils.dipToPx(getContext(), 60);
            } else {
                width = MeasureSpec.getSize(widthMeasureSpec);
            }
            this.width = this.height = width;
        } else {
            width = this.height = this.width;
        }
        setMeasuredDimension(width, width);
    }

    //y^2/a^2 + x^2/b^2 = 1
    @Override
    protected void onDraw(Canvas canvas) {
        if (isInit == false) {
            return;
        }

        //绘制水滴中的椭圆
        mOval.offsetTo((width-ovalW)/2, bottom - ovalH);
        LinearGradient mLG = new LinearGradient(mOval.centerX(), mOval.top, mOval.centerX(), mOval.bottom,
                new int[]{Color.WHITE, Color.GRAY}, null , Shader.TileMode.CLAMP);
        mPaint.setShader(mLG);
        canvas.drawOval(mOval, mPaint);

        //绘制水滴中反光的小弧线
        mOvalLine.offsetTo((width - mOvalLine.width())/2, bottom - ovalH + (ovalH - mOvalLine.height()) / 2);
        canvas.drawArc(mOvalLine, 20, 50, false, mPaintLine);

        int x = 0;
        if (bottom < 2*a) {
            double y = Math.abs(a - bottom);
            x = (int) Math.sqrt((1 - (y*y) / (a*a))*(b*b));
            left.x = width/2 - gap - x;
            left_x.x = width/2 - x;
            right.x = width/2 + gap + x;
            right_x.x = width/2 + x;
        } else {
            left.x = width/2 - height + bottom;
            right.x = width/2 + height - bottom;
            left_x.x = left.x;
            right_x.x = right.x;
            left_x.y = (bottom - 2*a)/2;
            right_x.y = (bottom - 2*a)/2;
        }

//        Point newLeft = new Point(left.x - width/2, left.y + ovalH/2 - bottom);
        int gap_n = ovalW/8;
        int bottom_n = bottom - gap_n;
        if (bottom < gap_n) {
            left_bottom.x = width/2;
            left_bottom.y = bottom;
            right_bottom.x = width/2;
            right_bottom.y = bottom;
        } else if (bottom < 2*a) {
            double y = Math.abs(a - bottom_n);
            x = (int) Math.sqrt((1 - (y*y) / (a*a))*(b*b));
            left_bottom.x = width/2 - x;
            left_bottom.y = gap_n;
            right_bottom.x = width/2 + x;
            right_bottom.y = gap_n;
        } else {
            double y = Math.abs(a - gap_n);
            x = (int) Math.sqrt((1 - (y*y) / (a*a))*(b*b));
            left_bottom.x = width/2 - x;
            left_bottom.y = gap_n + bottom - 2*a;
            right_bottom.x = width/2 + x;
            right_bottom.y = gap_n + bottom - 2*a;
        }

        //绘制水滴尾巴
        mPath.reset();
        mPath.moveTo(left.x, left.y);
        mPath.quadTo(left_x.x, left_x.y, left_bottom.x, left_bottom.y);
        mPath.lineTo(right_bottom.x, right_bottom.y);
        mPath.quadTo(right_x.x, right_x.y, right.x, right.y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        if (showDebugPoints) {
            mPaintPoint.setColor(Color.BLUE);
            drawPoint(left, canvas);
            drawPoint(left_bottom, canvas);
            drawPoint(right, canvas);
            drawPoint(right_bottom, canvas);
            mPaintPoint.setColor(Color.RED);
            drawPoint(left_x, canvas);
            drawPoint(right_x, canvas);
        }
    }

    private void drawPoint(Point p, Canvas canvas) {
        canvas.drawCircle(p.x, p.y, 4f, mPaintPoint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
        perfromAnim();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (mListener != null) {
            mListener.complete(Drip.this);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        bottom = (int) animation.getAnimatedValue();
        invalidate();
    }

    //水滴生成动画
    private void perfromAnim() {
        mAnimator.start();
    }

    public void setListener(DripListener listener) {
        mListener = listener;
    }
}
