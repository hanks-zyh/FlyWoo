package com.zjk.wifiproject.picture;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

public class PictureFolderVu implements Vu {

    private View view;
    private Context context;
    private ListView mListView;
    private PictureFolderAdapter adapter;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_picture, container, false);
        context = inflater.getContext();
        bindView();
    }

    private void bindView() {
        mListView = (ListView) view.findViewById(R.id.listView);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setDate(List<PictureFolderEntity> list) {
        adapter = new PictureFolderAdapter(context, list);
        mListView.setAdapter(adapter);
    }

}
