package com.zjk.wifiproject.vedio;

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

public class VedioVu implements Vu {

    private View view;
    private ListView mListView;
    public VedioAdapter adapter;
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

    public void setDate(final List<VedioEntity> newlist) {
        adapter = new VedioAdapter(context, newlist);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             *点击播放视频
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File f = new File(newlist.get(position).getAbsolutePath());
                Intent it = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(f);
                it.setDataAndType(uri, "video/*");
                view.getContext().startActivity(it);
            }
        });
    }
}
