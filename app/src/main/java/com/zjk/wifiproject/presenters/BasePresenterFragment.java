package com.zjk.wifiproject.presenters;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment的生命周期：onCreate - onCreateView
 * @param <V>
 */
public abstract class BasePresenterFragment<V extends Vu> extends Fragment {

    public V vu;
    protected Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            vu = getVuClass().newInstance();
            vu.init(inflater, container);
            onBindVu();
            view = vu.getView();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        onDestroyVu();
        vu = null;
        super.onDestroyView();
    }

    protected abstract void onBindVu();

    protected void onDestroyVu() {
    };

    protected abstract Class<V> getVuClass();

}
