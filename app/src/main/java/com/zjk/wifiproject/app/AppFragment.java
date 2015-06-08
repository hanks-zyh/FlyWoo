package com.zjk.wifiproject.app;

import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.util.AppUtils;

import java.util.List;

public class AppFragment extends BasePresenterFragment<AppVu> {


    //先执行getVuclass
    @Override
    protected Class<AppVu> getVuClass() {
        return AppVu.class;
    }

    //会在xml转换成view对象之后调用
    @Override
    protected void onBindVu() {
        List<AppEntity> list = AppUtils.getAppList(context);
        vu.setData(context, list);
        vu.setOnItemClickListener();
    }
}
