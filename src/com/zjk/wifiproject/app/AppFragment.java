package com.zjk.wifiproject.app;

import java.util.List;

import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.util.AppUtils;

public class AppFragment extends BasePresenterFragment<AppVu> {

    @Override
    protected Class<AppVu> getVuClass() {
        return AppVu.class;
    }

    @Override
    protected void onBindVu() {
        List<AppModle> list = AppUtils.getAppList(context);
        vu.setData(context, list);
        vu.setOnItemClickListener();
    }
}
