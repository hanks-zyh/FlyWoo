package com.zjk.wifiproject.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgress extends View {

    private Paint bgPaint, progressBgPaint, progressPaint;
    private float curProgress = 0;

    @SuppressLint("NewApi")
    public CircularProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public CircularProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CircularProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgress(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#F7F7F7"));

        progressBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressBgPaint.setColor(Color.parseColor("#88444459"));
        progressBgPaint.setStrokeWidth(8);
        progressBgPaint.setStyle(Paint.Style.STROKE); // 绘制空心圆

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.parseColor("#FF0000"));
        progressPaint.setStrokeWidth(8);
        progressPaint.setStyle(Paint.Style.STROKE); // 绘制空心圆
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(4000);
                va.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        setProgress(value);
                    }
                });
                va.start();
            }
        }, 2000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        // canvas.save();
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        canvas.drawCircle(width / 2, height / 2, width / 2 - 15, bgPaint);

        RectF oval = null;
        if (oval == null) {
            oval = new RectF(5, 5, width - 5, height - 5);
        }
        canvas.drawArc(oval, 0, 360, false, progressBgPaint);

        float p = 360 * curProgress;
        canvas.drawArc(oval, 270, p, false, progressPaint);

        // canvas.restore();
    }

    private synchronized void setProgress(float value) {
        if (value >= 0 && value <= 1) {
            curProgress = value;
            invalidate();
        }
    }
}
