package com.zjk.wifiproject.presenters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.util.SystemBarTintManager;

/**
 * 所有activity的父类，这里有所有activity必须执行的一些动作
 * @param <V>
 */
public abstract class BasePresenterActivity<V extends Vu> extends Activity {

    public V vu;

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        context = this;
        try {
            //这里调用的方法实际上是在vu的实现类里实现的方法
            vu = getVuClass().newInstance();
            vu.init(getLayoutInflater(), null);
            setContentView(vu.getView());
            onBindVu();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改顶部状态栏颜色，沉浸
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initStatusBar() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.main_red));
    }
    @Override
    protected final void onDestroy() {
        onDestroyVu();
        vu = null;
        super.onDestroy();
    }

    protected abstract void onBindVu();

    protected void onDestroyVu() {
    };

    protected abstract Class<V> getVuClass();

}
