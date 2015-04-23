package com.zjk.wifiproject.vedio;

import java.util.List;

import android.content.Context;

import com.zjk.wifiproject.presenters.BasePresenterAdapter;

public class VedioAdapter extends BasePresenterAdapter<VedioEntity, VedioAdapterVu> {

    public VedioAdapter(Context context, List<VedioEntity> list) {
        super(context, list);
    }

    @Override
    protected void onBindItemVu(int position) {
        VedioEntity item = list.get(position);
        vu.setVedioThrmuil(item.getData());
        vu.setVedioName(item.getDisplayName());
        vu.setVedioSize(item.getSize());
        vu.setVedioDuration(item.getDuration());
    }

    @Override
    protected Class<VedioAdapterVu> getVuClass() {
        return VedioAdapterVu.class;
    }

}
