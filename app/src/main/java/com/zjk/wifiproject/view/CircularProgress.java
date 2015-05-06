package com.zjk.wifiproject.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.zjk.wifiproject.util.PixelUtil;

public class CircularProgress extends View {

    private Paint bgPaint, progressBgPaint, progressPaint;
    private float curProgress = 0;

    private int ringBgWidth;
    private int ringWidth;
    private int gap,bgGap;
    private ValueAnimator progressAnim;

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

        ringBgWidth = PixelUtil.dp2px(8);
        ringWidth = PixelUtil.dp2px(8);
        gap = PixelUtil.dp2px(125);
        bgGap = PixelUtil.dp2px(0);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#F7F7F7"));

        progressBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressBgPaint.setColor(Color.parseColor("#AA444459"));
        progressBgPaint.setStrokeWidth(ringBgWidth);
        progressBgPaint.setStyle(Paint.Style.STROKE); // 绘制空心圆

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.parseColor("#FF0000"));
        progressPaint.setStrokeWidth(ringWidth);
        progressPaint.setStrokeJoin(Paint.Join.ROUND);
        progressPaint.setStyle(Paint.Style.STROKE); // 绘制空心圆

    }


    /**
     * 进度加载动画
     */
    public void startAnim(long delay) {
        progressAnim = ValueAnimator.ofFloat(0, 1).setDuration(20000);
        progressAnim.setInterpolator(new LinearInterpolator());
        progressAnim.setStartDelay(delay);
        progressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setProgress(value);
            }
        });
        progressAnim.start();
    }
    /**
     * finish动画
     */
    public void finishAnim() {
        progressAnim.cancel();
        final  float progress = curProgress;
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(1000);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue() + progress;
                value  = value >1 ? 1:value;
                setProgress(value);
            }
        });
        va.start();
    }


    /**
     * 外圆环的缩放
     */
    public void startCircleAnim(long delay) {
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(400);
        va.setInterpolator(new DecelerateInterpolator());
        va.setStartDelay(delay);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setRadius(value);
            }
        });
        va.start();
    }
    /**
     * 圆背景+外圆环的缩放
     */
    public void startScaleAnim(long delay) {
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(400);
        va.setInterpolator(new LinearInterpolator());
        va.setStartDelay(delay);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setBgRadius(value);
            }
        });
        va.start();
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startCircleAnim(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 设置背景圆变化
     * @param value
     */
    private void setBgRadius(float value) {
        bgGap = (int) (PixelUtil.dp2px(120)*(1-value)+.5f);
        invalidate();
    }

    /**
     * 设置圆环变化
     * @param value
     */
    private void setRadius(float value) {
        gap = (int) (PixelUtil.dp2px(125)*(1-value)+.5f);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        canvas.save();
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //圆环
        RectF oval = null;
        if (oval == null) {
            oval = new RectF(ringWidth + gap, ringBgWidth + gap, width - ringBgWidth - gap, height
                    - ringBgWidth - gap);
        }
        canvas.drawArc(oval, 0, 360, false, progressBgPaint);

        //圆
        canvas.drawCircle(width / 2, height / 2, width / 2 - ringBgWidth - ringBgWidth - bgGap , bgPaint);

        //进度条
        float p = 360 * curProgress;
        p = p > 360? 360:p; //防止超出360
        canvas.drawArc(oval, 270, p, false, progressPaint);

        canvas.restore();
    }

    private synchronized void setProgress(float value) {
        if (value >= 0 && value <= 1) {
            curProgress = value;
            invalidate();
        }
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }
}
