package com.zjk.wifiproject.connection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.activity.wifiap.WifiConst;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.socket.udp.IPMSGConst;
import com.zjk.wifiproject.socket.udp.UDPMessageListener;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.T;
import com.zjk.wifiproject.util.WifiUtils;
import com.zjk.wifiproject.util.WifiUtils.WifiCipherType;

public class ConnectVu implements Vu, OnClickListener {

    private View view;
    private Button mConnectButton;
    private Context context;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
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
        if (ssid.startsWith(WifiConst.WIFI_AP_HEADER)) {
            // 连接网络
            boolean connFlag = WifiUtils.connectWifi("ZChat_google_608", WifiConst.WIFI_AP_PASSWORD,
                    WifiCipherType.WIFICIPHER_WPA);
            if (!connFlag) {
                T.show(context, "已连接");
                mConnectButton.setText("已连接");
//                new Thread(new ConnectAPThread()).start();
                String ip = WifiUtils.getServerIPAddress();
                Logger.i("ip:"+ip);
                UDPMessageListener.getInstance(context).sendUDPdata(IPMSGConst.IPMSG_GETINFO,ip);

            }
        }
    }
}
