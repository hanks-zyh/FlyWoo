package com.zjk.wifiproject.vedio;

import java.util.List;

import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.util.FileUtils;

public class VedioFragment extends BasePresenterFragment<VedioVu> {

    @Override
    protected void onBindVu() {
        List<VedioEntity> list = FileUtils.getVedioList(context);
        vu.setDate(list);
    }

    @Override
    protected Class<VedioVu> getVuClass() {
        return VedioVu.class;
    }

}
