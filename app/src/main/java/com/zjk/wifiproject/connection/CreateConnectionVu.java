package com.zjk.wifiproject.connection;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.activity.wifiap.WifiApConst;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.socket.IPMSGConst;
import com.zjk.wifiproject.socket.UDPMessageListener;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.T;
import com.zjk.wifiproject.util.TextUtils;
import com.zjk.wifiproject.util.WifiUtils;

public class CreateConnectionVu implements Vu, OnClickListener {

    private View view;
    private Context context;
    private EditText edit;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.activity_create_connection, null);
        context = inflater.getContext();
        bindView(view);
    }

    private void bindView(View view2) {
        view2.findViewById(R.id.open_ap).setOnClickListener(this);
        view2.findViewById(R.id.open_wifi).setOnClickListener(this);
        view2.findViewById(R.id.close_ap).setOnClickListener(this);
        view2.findViewById(R.id.close_wifi).setOnClickListener(this);
        view2.findViewById(R.id.connect_wifi).setOnClickListener(this);
        view2.findViewById(R.id.send).setOnClickListener(this);
        edit = (EditText)view2.findViewById(R.id.editText);
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

        if (WifiUtils.isWifiEnabled()) {
            // 执行关闭热点事件
            WifiUtils.closeWifi();
        }
        Handler mHandler = new Handler();
        // 创建热点
//        WifiUtils.startWifiAp(WifiApConst.WIFI_AP_HEADER + getLocalHostName(), WifiApConst.WIFI_AP_PASSWORD,
//                mHandler);
        WifiUtils.startWifiAp("ZChat_google_608", WifiApConst.WIFI_AP_PASSWORD,
                mHandler);

        L.d("localIP=" + WifiUtils.getServerIPAddress());

//        new Thread(new CreateAPThread()).start();

        UDPMessageListener.getInstance(context).connectUDPSocket();
    }

    public void closeAP() {
        WifiUtils.closeWifiAp();
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

    @Override
    public void onClick(View v) {
        L.e("v=" + v.toString());;
        switch (v.getId()) {
            case R.id.close_ap:
                closeAP();
                break;
            case R.id.open_ap:
                createAP(context);
                break;
            case R.id.close_wifi:
                WifiUtils.closeWifi();
                break;
            case R.id.open_wifi:
                WifiUtils.OpenWifi();
//                A.goOtherActivity(context, ConnectAcivity.class);
                break;
            case R.id.connect_wifi:
                connectAp();
                break;
            case R.id.send:
                String ip = WifiUtils.getServerIPAddress();
                Logger.i("ip:" + ip);
                UDPMessageListener.getInstance(context).sendUDPdata(IPMSGConst.IPMSG_GETINFO, ip);
                break;
            default:
                break;
        }
    }

    private void connectAp() {
        String ssid = WifiUtils.getApSSID();
        L.e(ssid);
        if (ssid.startsWith(WifiApConst.WIFI_AP_HEADER)) {
            // 连接网络
            boolean connFlag = WifiUtils.connectWifi("ZChat_google_608", WifiApConst.WIFI_AP_PASSWORD,
                    WifiUtils.WifiCipherType.WIFICIPHER_WPA);
            if (connFlag) {
                T.show(context, "已连接");
                edit.setText("已连接");
//                new Thread(new ConnectAPThread()).start();

            }
        }
    }
}
