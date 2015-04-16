package com.zjk.wifiproject.connection;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.activity.wifiap.WifiApConst;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.util.TextUtils;
import com.zjk.wifiproject.util.WifiUtils;

public class CreateConnectionVu implements Vu {

    private View view;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.activity_create_connection, null);
    }

    @Override
    public View getView() {
        return view;
    }

    /**
     * 创建无线热点
     * 
     * @version 1.0
     * @author zyh
     * @param context
     */
    public void createAP(Context context) {
        // WifiUtils wifiUtils = WifiUtils.getInstance(context);
        // WifiConfiguration wifiConfiguration =
        // wifiUtils.createWifiInfo(WifiApConst.WIFI_AP_HEADER,
        // WifiApConst.WIFI_AP_PASSWORD, 3, "ap");
        // wifiUtils.createWiFiAP(wifiConfiguration, true);

        if (WifiUtils.isWifiApEnabled()) {
            // 执行关闭热点事件
            WifiUtils.closeWifiAp();
            WifiUtils.OpenWifi();

        } else {
            Handler mHandler = new Handler();
            // 创建热点
            WifiUtils.startWifiAp(WifiApConst.WIFI_AP_HEADER + getLocalHostName(),
                    WifiApConst.WIFI_AP_PASSWORD, mHandler);
        }

    }

    /**
     * 获取Wifi热点名
     * 
     * <p>
     * BuildBRAND 系统定制商 ； BuildMODEL 版本
     * </p>
     * 
     * @return 返回 定制商+版本 (String类型),用于创建热点。
     */
    public String getLocalHostName() {
        String str1 = Build.BRAND;
        String str2 = TextUtils.getRandomNumStr(3);
        return str1 + "_" + str2;
    }

    public String getPhoneModel() {
        String str1 = Build.BRAND;
        String str2 = Build.MODEL;
        str2 = str1 + "_" + str2;
        return str2;
    }
}
