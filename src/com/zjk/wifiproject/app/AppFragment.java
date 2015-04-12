package com.zjk.wifiproject.app;

import java.util.List;

import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.util.AppUtils;

public class AppFragment extends BasePresenterFragment<AppGridVu> {

    @Override
    protected Class<AppGridVu> getVuClass() {
        return AppGridVu.class;
    }

    @Override
    protected void onBindVu() {
        List<AppModle> list = AppUtils.getAppList(context);
        AppGridAdapter adapter = new AppGridAdapter(context, list);
        vu.setAdapter(adapter);
    }
}
