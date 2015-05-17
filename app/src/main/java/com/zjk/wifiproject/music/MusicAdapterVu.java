package com.zjk.wifiproject.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.view.TouchCheckBox;

public class MusicAdapterVu implements Vu {

    private View view;
    private TouchCheckBox mIsSelect;

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
        mIsSelect = (TouchCheckBox) view.findViewById(R.id.isSelect);
    }

    public void setChecked(boolean isSelect) {
        mIsSelect.setChecked(isSelect);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setSize(String size) {
        mSize.setText(size);
    }

    public void setOnCheckedChangeListener (TouchCheckBox.OnCheckedChangeListener listener){
        mIsSelect.setOnCheckedChangeListener(listener);
    }

    @Override
    public View getView() {
        return view;
    }

}
