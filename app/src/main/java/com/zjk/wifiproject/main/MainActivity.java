package com.zjk.wifiproject.main;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.activity.wifiap.WifiConst;
import com.zjk.wifiproject.app.AppFragment;
import com.zjk.wifiproject.chat.ChatActivity;
import com.zjk.wifiproject.config.ConfigBroadcast;
import com.zjk.wifiproject.config.ConfigIntent;
import com.zjk.wifiproject.drawer.DrawerFragment;
import com.zjk.wifiproject.entity.Users;
import com.zjk.wifiproject.event.RefreshTipEvent;
import com.zjk.wifiproject.file.FileFragment;
import com.zjk.wifiproject.music.MusicFragment;
import com.zjk.wifiproject.picture.PictureFragment;
import com.zjk.wifiproject.presenters.BasePresenterActivity;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.T;
import com.zjk.wifiproject.util.WifiUtils;
import com.zjk.wifiproject.vedio.VedioFragment;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends BasePresenterActivity<MainVu> {

    private List<Fragment> list = new ArrayList<Fragment>();

    UpdataBottomLayoutReceiver receiver;
    @Override
    protected void onBindVu() {
        // 侧滑菜单
        DrawerFragment drawerMenu = new DrawerFragment();
        vu.setDrawerMenu(getFragmentManager(), drawerMenu);
        // 主界面
        list.add(new AppFragment());
        list.add(new MusicFragment());
        list.add(new PictureFragment());
        list.add(new VedioFragment());
        list.add(new FileFragment());
        vu.setViewPager(list);

        //注册更新底部布局的广播接收器
        receiver = new UpdataBottomLayoutReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConfigBroadcast.ACTION_UPDATE_BOTTOM);
        registerReceiver(receiver, filter);

        //EventBus
        EventBus.getDefault().register(this);

    }

    public void onEvent(RefreshTipEvent event) {
        /* Do something */
        checkConnectStatus();
    }

    @Override
    protected void onDestroyVu() {
        super.onDestroyVu();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected Class<MainVu> getVuClass() {
        return MainVu.class;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        vu.onActivityResult(requestCode, resultCode, data);
    }



    class UpdataBottomLayoutReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ConfigBroadcast.ACTION_UPDATE_BOTTOM)){
                vu.handleAnim();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectStatus();
    }

    /**
     * 检测当前的连接状态以及AP热点的开启情况
     */
    private void checkConnectStatus() {
            Logger.i(WifiUtils.getApSSID() + "," + WifiUtils.getSSID() + "," + WifiUtils.isWifiApEnabled() + "," + WifiUtils.isWifiConnect());
        View mTipLayout = findViewById(R.id.tip_layout);
        TextView mTip = (TextView)mTipLayout.findViewById(R.id.tip);
        //AP热点以及创建了
        if(WifiUtils.isWifiApEnabled() && WifiUtils.getApSSID().startsWith(WifiConst.WIFI_AP_HEADER)){
            //显示AP创建的提示布局
            T.show(context, "已经开启了热点:" + WifiUtils.getApSSID());
            final ArrayList<String> ips = WifiUtils.getConnectedHotIP();

            mTip.setText("已经开启了热点:" + WifiUtils.getApSSID().substring(6)+","+ips.size()+"个已连接");
            mTipLayout.setVisibility(View.VISIBLE);

            if(ips.size() == 1){
                mTipLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Users user = new Users();
                        user.setIpaddress(ips.get(0));
                        user.setDevice(getPhoneModel());
                        goChatActivity(user);
                    }
                });
            }
        }

        //已经连接到AP热点
        else if(WifiUtils.isWifiConnect() && WifiUtils.getSSID().substring(1).startsWith(WifiConst.WIFI_AP_HEADER)){
            //显示已连接到AP热点的布局
            T.show(context,"已经连接了热点:"+WifiUtils.getSSID()+":"+WifiUtils.getServerIPAddress());
            mTip.setText("已经连接了热点:" + WifiUtils.getApSSID().substring(6)+":"+WifiUtils.getServerIPAddress());
            mTipLayout.setVisibility(View.VISIBLE);
            mTipLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Users user = new Users();
                    user.setIpaddress(WifiUtils.getServerIPAddress());
                    user.setDevice(getPhoneModel());
                    goChatActivity(user);
                }
            });
        }
        //其他情况就不显示提示布局
        else{
            T.show(context,"快去创建或加入");
            mTipLayout.setVisibility(View.GONE);
        }


    }

    private void goChatActivity(Users user) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra(ConfigIntent.EXTRA_CHAT_USER, user);
        A.goOtherActivity(context, i);
    }

    public String getPhoneModel() {
        String str1 = Build.BRAND;
        String str2 = Build.MODEL;
        str2 = str1 + "_" + str2;
        return str2;
    }
}
