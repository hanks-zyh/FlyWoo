package com.zjk.wifiproject.app;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.Formatter;

import com.zjk.wifiproject.presenters.BasePresenterAdapter;

public class AppGridAdapter extends BasePresenterAdapter<AppModle, AppItemVu> {

    public AppGridAdapter(Context context, List<AppModle> list) {
        super(context, list);
    }

    @Override
    protected Class<AppItemVu> getVuClass() {
        return AppItemVu.class;
    }

    @Override
    protected void onBindItemVu(int position) {
        AppModle item = list.get(position);
        vu.setAppIcon(((BitmapDrawable) item.getIcon()).getBitmap());
        vu.setAppName(item.getName());
        vu.setAppSize(Formatter.formatFileSize(context, item.getApkSize()));
    }

}
