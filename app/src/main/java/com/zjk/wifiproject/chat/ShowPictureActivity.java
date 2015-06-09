/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package com.zjk.wifiproject.chat;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zjk.wifiproject.R;

/**
 * Created by Hanks on 2015/6/9.
 */
public class ShowPictureActivity extends Activity {
    private SimpleDraweeView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        initView();
    }

    private int              mLeft;
    private int              mTop;
    private float            mScaleX;
    private float            mScaleY;
    private SimpleDraweeView mImageView;
    private View             mLayout;
    private Drawable         mBackground;


    public void initView() {


        final int left = getIntent().getIntExtra("locationX", 0);
        final int top = getIntent().getIntExtra("locationY", 0);
        final int width = getIntent().getIntExtra("width", 0);
        final int height = getIntent().getIntExtra("height", 0);
        String path = getIntent().getStringExtra("url");

        mLayout = findViewById(R.id.id_layout);
        mBackground = new ColorDrawable(Color.BLACK);
        mLayout.setBackgroundDrawable(mBackground);
        mImageView = (SimpleDraweeView) findViewById(R.id.image);
        mImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                int location[] = new int[2];
                mImageView.getLocationOnScreen(location);
                mLeft = left - location[0];
                mTop = top - location[1];
                mScaleX = width * 1.0f / mImageView.getWidth();
                mScaleY = height * 1.0f / mImageView.getHeight();
                Log.v("zgy", "========resId========" + mImageView.getWidth());
                Log.v("zgy", "========resId========" + mScaleY);
                activityEnterAnim();
                return true;
            }
        });
//        Log.v("zgy", "========mBitmap========" + mBitmap.getWidth());
//        mImageView.setImageDrawable(mBitmapDrawable);
        mImageView.setImageURI(Uri.parse("file://"+path));
    }


    private void activityEnterAnim() {
        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.setScaleX(mScaleX);
        mImageView.setScaleY(mScaleY);
        mImageView.setTranslationX(mLeft);
        mImageView.setTranslationY(mTop);
        mImageView.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).
                setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(1000);
        objectAnimator.start();
    }

    private void activityExitAnim(Runnable runnable) {
        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.animate().scaleX(mScaleX).scaleY(mScaleY).translationX(mLeft).translationY(mTop).
                withEndAction(runnable).
                setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mBackground, "alpha", 255, 0);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(1000);
        objectAnimator.start();
    }

    @Override
    public void onBackPressed() {
        activityExitAnim(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
}
