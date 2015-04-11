package com.zjk.wifiproject.app;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

public class AppItemVu implements Vu {

    private View view;
    private ImageView iv_app_icon;
    private TextView tv_app_size;
    private TextView tv_app_name;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_app_item, container, false);
        iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
        tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
        tv_app_size = (TextView) view.findViewById(R.id.tv_app_size);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setAppIcon(Bitmap bitmap) {
        iv_app_icon.setImageBitmap(bitmap);
    }

    public void setAppName(String appName) {
        tv_app_name.setText(appName);
    }

    public void setAppSize(String appSize) {
        tv_app_size.setText(appSize);
    }
}
