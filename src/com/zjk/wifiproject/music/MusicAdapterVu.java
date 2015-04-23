package com.zjk.wifiproject.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

public class MusicAdapterVu implements Vu {

    private View view;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.item_music, container, false);
        bindViews();
    }

    private TextView mTitle;
    private TextView mSize;

    private void bindViews() {
        mTitle = (TextView) view.findViewById(R.id.title);
        mSize = (TextView) view.findViewById(R.id.size);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setSize(String size) {
        mSize.setText(size);
    }

    @Override
    public View getView() {
        return view;
    }

}
