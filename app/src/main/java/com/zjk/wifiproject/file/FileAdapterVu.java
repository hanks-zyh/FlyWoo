package com.zjk.wifiproject.file;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

/**
 * Created by Administrator on 2015/4/23.
 */
public class FileAdapterVu implements Vu {

    private View view;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.item_file, container, false);
    }

    @Override
    public View getView() {
        return view;
    }
}
