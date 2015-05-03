package com.zjk.wifiproject.presenters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public abstract class BasePresenterActivity<V extends Vu> extends Activity {

    public V vu;

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        try {
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
