package com.zjk.wifiproject.main;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.zjk.wifiproject.app.AppFragment;
import com.zjk.wifiproject.config.ConfigBroadcast;
import com.zjk.wifiproject.drawer.DrawerFragment;
import com.zjk.wifiproject.file.FileFragment;
import com.zjk.wifiproject.music.MusicFragment;
import com.zjk.wifiproject.picture.PictureFragment;
import com.zjk.wifiproject.presenters.BasePresenterActivity;
import com.zjk.wifiproject.vedio.VedioFragment;

import java.util.ArrayList;
import java.util.List;

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

        receiver = new UpdataBottomLayoutReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConfigBroadcast.ACTION_UPDATE_BOTTOM);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroyVu() {
        super.onDestroyVu();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
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
}
