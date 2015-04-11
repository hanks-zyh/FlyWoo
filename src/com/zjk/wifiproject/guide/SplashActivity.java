package com.zjk.wifiproject.guide;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.activity.MainActivity;
import com.zjk.wifiproject.base.BaseActivity;
import com.zjk.wifiproject.config.SharedKey;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.SP;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        goMainActivity();
    }

    private void goMainActivity() {
        new Handler() {
        }.postDelayed(new Runnable() {
            public void run() {
                boolean isFirst = (boolean) SP.get(context, SharedKey.isfirst, true);
                if (isFirst) {
                    A.goOtherActivityFinish(context, GuideActivity.class);
                } else {
                    A.goOtherActivityFinish(context, MainActivity.class);
                }
            }
        }, 400);
    }
}
