package com.zjk.wifiproject.app;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;

import com.zjk.wifiproject.presenters.BasePresenterAdapter;

public class AppGridAdapter extends BasePresenterAdapter<AppModle, AppItemVu> {

    private AppVu appVu;

    public AppGridAdapter(AppVu appVu, Context context, List<AppModle> list) {
        super(context, list);
        this.appVu = appVu;
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
        vu.setChecked(appVu.getSelectedList().contains(vu));
    }

    public void onItemClick(int position) {
        vu.toggleChecked();
    }

    public void onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        vu.toggleChecked();
    }

}
