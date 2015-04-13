package com.zjk.wifiproject.app;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.util.L;

public class AppItemVu implements Vu {

    private View view;
    private ImageView iv_app_icon;
    private TextView tv_app_size;
    private TextView tv_app_name;
    private View checked;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_app_item, container, false);
        iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
        tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
        tv_app_size = (TextView) view.findViewById(R.id.tv_app_size);
        checked = view.findViewById(R.id.checked);
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

    public boolean toggleChecked() {
        L.e("click");
        if (checked.getVisibility() == View.VISIBLE) {
            checked.setVisibility(View.INVISIBLE);
            return false;
        } else {
            checked.setVisibility(View.VISIBLE);
            return true;
        }
    }

    public boolean isChecked() {
        return checked.getVisibility() == View.VISIBLE;
    }

    public void setChecked(boolean isChecked) {
        checked.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
    }
}
