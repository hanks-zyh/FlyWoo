package com.zjk.wifiproject.activity;

import java.util.List;

import com.zjk.wifiproject.app.AppGridAdapter;
import com.zjk.wifiproject.app.AppGridVu;
import com.zjk.wifiproject.app.AppModle;
import com.zjk.wifiproject.presenters.BasePresenterActivity;
import com.zjk.wifiproject.util.AppUtils;

public class MainActivity extends BasePresenterActivity<AppGridVu> {

    private AppGridAdapter adapter;

    @Override
    protected void onBindVu() {
        super.onBindVu();
        List<AppModle> list = getInstalledApkList();
        adapter = new AppGridAdapter(context, list);
        vu.setAdapter(adapter);
    }

    private List<AppModle> getInstalledApkList() {
        return AppUtils.getAppList(context);
    }

    @Override
    protected Class<AppGridVu> getVuClass() {
        return AppGridVu.class;
    }
}
