package com.zjk.wifiproject.app;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.Formatter;

import com.zjk.wifiproject.presenters.BasePresenterAdapter;

public class AppGridAdapter extends BasePresenterAdapter<AppEntity, AppItemVu> {

    private AppVu appVu;

    public AppGridAdapter(AppVu appVu, Context context, List<AppEntity> list) {
        super(context, list);
        this.appVu = appVu;
    }

    @Override
    protected Class<AppItemVu> getVuClass() {
        return AppItemVu.class;
    }

    @Override
    protected void onBindItemVu(int position) {
        AppEntity item = list.get(position);
        vu.setAppIcon(((BitmapDrawable) item.getIcon()).getBitmap());
        vu.setAppName(item.getName());
        vu.setAppSize(Formatter.formatFileSize(context, item.length()));
        vu.setChecked(appVu.getSelectedList().contains(item));
    }
}
