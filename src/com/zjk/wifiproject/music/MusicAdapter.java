package com.zjk.wifiproject.music;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.text.format.Formatter;

import com.zjk.wifiproject.presenters.BasePresenterAdapter;

public class MusicAdapter extends BasePresenterAdapter<MusicEntity, MusicAdapterVu> {

    public MusicAdapter(Context context, List<MusicEntity> list) {
        super(context, list);
    }

    @Override
    protected void onBindItemVu(int position) {
        MusicEntity item = list.get(position);
        vu.setTitle(item.getTitle());
        long size = new File(item.getData()).length();
        vu.setSize(Formatter.formatFileSize(context, size));
    }

    @Override
    protected Class<MusicAdapterVu> getVuClass() {
        return MusicAdapterVu.class;
    }

}
