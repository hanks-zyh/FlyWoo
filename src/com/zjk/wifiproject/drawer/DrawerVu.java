package com.zjk.wifiproject.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

public class DrawerVu implements Vu {

    private View view;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_drawer, container, false);
    }

    @Override
    public View getView() {
        return view;
    }

}
