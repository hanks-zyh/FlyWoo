package com.zjk.wifiproject.connection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.activity.wifiap.WifiApConst;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.WifiUtils;
import com.zjk.wifiproject.util.WifiUtils.WifiCipherType;

public class ConnectVu implements Vu, OnClickListener {

    private View view;
    private Button mConnectButton;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {

        view = inflater.inflate(R.layout.vu_connect, container, false);
        bindView();
    }

    private void bindView() {
        mConnectButton = (Button) view.findViewById(R.id.btn_connect);
        mConnectButton.setOnClickListener(this);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                connectAp();
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
                    WifiCipherType.WIFICIPHER_WPA);
            if (!connFlag) {
                mConnectButton.setText("已连接");
                new Thread(new ConnectAPThread()).start();
            }
        }
    }
}
