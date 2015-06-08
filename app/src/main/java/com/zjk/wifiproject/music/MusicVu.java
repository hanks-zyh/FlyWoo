package com.zjk.wifiproject.music;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

import java.io.File;
import java.util.List;

public class MusicVu implements Vu {

    private View view;
    private Context context;
    private ListView mListView;
    public MusicAdapter adapter;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_music, container, false);
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

    public void setData(final List<MusicEntity> newlist) {
        adapter = new MusicAdapter(context, newlist);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = new File(newlist.get(position).getAbsolutePath());
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                view.getContext().startActivity(intent);
            }
        });

    }



}
