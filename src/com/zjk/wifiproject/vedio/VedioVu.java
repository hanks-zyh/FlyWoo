package com.zjk.wifiproject.vedio;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

public class VedioVu implements Vu {

    private View view;
    private ListView mListView;
    private VedioAdapter adapter;
    private Context context;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
        view = inflater.inflate(R.layout.vu_vedio, container, false);
        bindViews();
    }

    private void bindViews() {
        mListView = (ListView) view.findViewById(R.id.listView);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setDate(List<VedioEntity> list) {
        adapter = new VedioAdapter(context, list);
        mListView.setAdapter(adapter);
    }
}
