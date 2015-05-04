package com.zjk.wifiproject.connection;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.activity.wifiap.WifiConst;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.socket.UDPMessageListener;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.PixelUtil;
import com.zjk.wifiproject.util.T;
import com.zjk.wifiproject.util.TextUtils;
import com.zjk.wifiproject.util.WifiUtils;

public class CreateConnectionVu implements Vu, OnClickListener {

    private View view;
    private Context context;
    private EditText edit;

    //创建或者加入的布局
    private View layout_hide;
    private CheckBox mHelpCheckBox;
    private ImageView mHideButton;
    private ImageView mCreate;
    private ImageView mJoin;
    private ImageView mRightImage;
    private ImageView mLeftImage;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.activity_create_connection, null);
        context = inflater.getContext();
        bindViews(view);
        setListener();
        showTwoButtonAnimation();
    }

    private void setListener() {
        mHideButton.setOnClickListener(this);
        mHelpCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showTwoImageAnimation();
                    mHideButton.setVisibility(View.GONE);
                } else {
                    hideTwoImageAnimation();
                    mHideButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void bindViews(View view2) {
//        view2.findViewById(R.id.open_ap).setOnClickListener(this);
//        view2.findViewById(R.id.open_wifi).setOnClickListener(this);
//        view2.findViewById(R.id.close_ap).setOnClickListener(this);
//        view2.findViewById(R.id.close_wifi).setOnClickListener(this);
//        view2.findViewById(R.id.connect_wifi).setOnClickListener(this);
//        view2.findViewById(R.id.send).setOnClickListener(this);
//        edit = (EditText)view2.findViewById(R.id.editText);

        layout_hide = view.findViewById(R.id.layout_create_or_join);
        mHelpCheckBox = (CheckBox) view.findViewById(R.id.helpCheckBox);
        mHideButton = (ImageView) view.findViewById(R.id.hideButton);
        mCreate = (ImageView) view.findViewById(R.id.create);
        mJoin = (ImageView) view.findViewById(R.id.join);
        mLeftImage = (ImageView) view.findViewById(R.id.leftImage);
        mRightImage = (ImageView) view.findViewById(R.id.rightImage);
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
        // wifiUtils.createWifiInfo(WifiConst.WIFI_AP_HEADER,
        // WifiConst.WIFI_AP_PASSWORD, 3, "ap");
        // wifiUtils.createWiFiAP(wifiConfiguration, true);

        if (WifiUtils.isWifiEnabled()) {
            // 执行关闭热点事件
            WifiUtils.closeWifi();
        }
        Handler mHandler = new Handler();
        // 创建热点
//        WifiUtils.startWifiAp(WifiConst.WIFI_AP_HEADER + getLocalHostName(), WifiConst.WIFI_AP_PASSWORD,
//                mHandler);
        WifiUtils.startWifiAp("ZChat_google_608", WifiConst.WIFI_AP_PASSWORD,
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
        switch (v.getId()) {
            case R.id.helpCheckBox:
                layout_hide.setVisibility(View.VISIBLE);
                showTwoImageAnimation();
                break;
            case R.id.hideButton:
                 onBackPressed();
                break;
        }
    }
    /**
     * 返回按钮
     */
    public void onBackPressed() {
        ((Activity)context).setResult(Activity.RESULT_OK);
        A.finishSelfNoAnim(context);
    }
    /**
     * 按钮出现动画
     */
    private void showTwoButtonAnimation() {
        final int dis = PixelUtil.dp2px(140);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(200);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mCreate.setTranslationY(-value * dis);
                mCreate.setTranslationX(-value * dis * 0.2f);
                mJoin.setTranslationX(-value * dis);
                mJoin.setTranslationY(-value * dis *0.2f);
                mCreate.setScaleX(0.7f + value);
                mCreate.setScaleY(0.7f + value);
                mJoin.setScaleX(0.7f + value);
                mJoin.setScaleY(0.7f + value);
            }
        });
        valueAnimator.start();

    }

    /**
     * 两侧出现小人动画
     */
    private void showTwoImageAnimation() {
        final int dis = PixelUtil.dp2px(160);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(200);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mLeftImage.setTranslationX(value * dis);
                mRightImage.setTranslationX(-value * dis);
            }
        });
        valueAnimator.start();
    }
    /**
     * 隐藏两侧出现小人动画
     */
    private void hideTwoImageAnimation() {
        final int dis = PixelUtil.dp2px(150);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(200);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mLeftImage.setTranslationX((1-value) * dis);
                mRightImage.setTranslationX((value-1) * dis);
            }
        });
        valueAnimator.start();
    }

//    @Override
//    public void onClick(View v) {
//        L.e("v=" + v.toString());;
//        switch (v.getId()) {
//            case R.id.close_ap:
//                closeAP();
//                break;
//            case R.id.open_ap:
//                createAP(context);
//                break;
//            case R.id.close_wifi:
//                WifiUtils.closeWifi();
//                break;
//            case R.id.open_wifi:
//                WifiUtils.OpenWifi();
////                A.goOtherActivity(context, ConnectAcivity.class);
//                break;
//            case R.id.connect_wifi:
//                connectAp();
//                break;
//            case R.id.send:
//                String ip = WifiUtils.getServerIPAddress();
//                Logger.i("ip:" + ip);
//                UDPMessageListener.getInstance(context).sendUDPdata(IPMSGConst.IPMSG_GETINFO, ip);
//                break;
//            default:
//                break;
//        }
//    }

    private void connectAp() {
        String ssid = WifiUtils.getApSSID();
        L.e(ssid);
        if (ssid.startsWith(WifiConst.WIFI_AP_HEADER)) {
            // 连接网络
            boolean connFlag = WifiUtils.connectWifi("ZChat_google_608", WifiConst.WIFI_AP_PASSWORD,
                    WifiUtils.WifiCipherType.WIFICIPHER_WPA);
            if (connFlag) {
                T.show(context, "已连接");
                edit.setText("已连接");
//                new Thread(new ConnectAPThread()).start();

            }
        }
    }
}
