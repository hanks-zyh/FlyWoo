package com.zjk.wifiproject.music;

import android.content.Context;
import android.text.format.Formatter;

import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.file.OnFileCheckListener;
import com.zjk.wifiproject.presenters.BasePresenterAdapter;

import java.util.List;

public class MusicAdapter extends BasePresenterAdapter<MusicEntity, MusicAdapterVu> {

    public MusicAdapter(Context context, List<MusicEntity> list) {
        super(context, list);
    }

    @Override
    protected void onBindItemVu(int position) {
        MusicEntity item = list.get(position);
        vu.setTitle(item.getTitle());
        vu.setSize(Formatter.formatFileSize(context, item.length()));
        vu.setOnCheckedChangeListener(new OnFileCheckListener(item, Message.CONTENT_TYPE.MUSIC));
    }

    @Override
    protected Class<MusicAdapterVu> getVuClass() {
        return MusicAdapterVu.class;
    }

}
