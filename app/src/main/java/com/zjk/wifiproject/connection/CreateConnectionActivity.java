package com.zjk.wifiproject.connection;

import com.zjk.wifiproject.presenters.BasePresenterActivity;

public class CreateConnectionActivity extends BasePresenterActivity<CreateConnectionVu> {

    @Override
    protected void onBindVu() {
//        vu.createAP(context);
    }

    @Override
    protected Class<CreateConnectionVu> getVuClass() {
        return CreateConnectionVu.class;
    }

}
