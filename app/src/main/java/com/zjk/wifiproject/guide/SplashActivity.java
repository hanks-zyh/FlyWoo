package com.zjk.wifiproject.guide;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.config.SharedKey;
import com.zjk.wifiproject.main.MainActivity;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.SP;

public class SplashActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_splash);
        goMainActivity();
    }

    private void goMainActivity() {
        new Handler() {
        }.postDelayed(new Runnable() {
            public void run() {
                boolean isFirst = (boolean) SP.get(context, SharedKey.isfirst, true);
                A.goOtherActivityFinish(context, isFirst ? GuideActivity.class : MainActivity.class);
            }
        }, 1000);
    }

}
