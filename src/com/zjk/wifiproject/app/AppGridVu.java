package com.zjk.wifiproject.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

public class AppGridVu implements Vu {

    private View view;
    private GridView gridView;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_app, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setAdapter(ListAdapter adapter) {
        gridView.setAdapter(adapter);
    }

}
