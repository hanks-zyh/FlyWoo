package com.zjk.wifiproject.file;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.entity.Entity;
import com.zjk.wifiproject.presenters.Vu;

public class FileVu implements Vu {

    private View view;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_file, container, false);
        bindViews();
    }

    private TextView mPath;
    private ImageButton mUp;
    private ListView mListView;

    // End Of Content View Elements

    private void bindViews() {

        mPath = (TextView) view.findViewById(R.id.path);
        mUp = (ImageButton) view.findViewById(R.id.up);
        mListView = (ListView) view.findViewById(R.id.listView);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setData(List<Entity> list) {
        // mListView.setAdapter(adapter);
    }

}
