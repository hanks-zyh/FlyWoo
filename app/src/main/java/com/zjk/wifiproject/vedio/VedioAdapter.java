package com.zjk.wifiproject.vedio;

import android.content.Context;

import com.zjk.wifiproject.BaseApplication;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.file.OnFileCheckListener;
import com.zjk.wifiproject.presenters.BasePresenterAdapter;

import java.util.List;

public class VedioAdapter extends BasePresenterAdapter<VedioEntity, VedioAdapterVu> {

    public VedioAdapter(Context context, List<VedioEntity> list) {
        super(context, list);
    }

    @Override
    protected void onBindItemVu(int position) {
        VedioEntity item = list.get(position);
        vu.setVedioThrmuil(item.getAbsolutePath());
        vu.setVedioName(item.getDisplayName());
        vu.setVedioSize(item.length());
        vu.setVedioDuration(item.getDuration());
        vu.setChecked(BaseApplication.sendFileStates.containsKey(item.getAbsolutePath()));
        vu.setOnCheckedChangeListener(new OnFileCheckListener(item, Message.CONTENT_TYPE.VEDIO));
    }

    @Override
    protected Class<VedioAdapterVu> getVuClass() {
        return VedioAdapterVu.class;
    }

 
}
