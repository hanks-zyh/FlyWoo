package com.zjk.wifiproject.connection;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.zjk.wifiproject.view.CircularProgress;
import com.zjk.wifiproject.view.SearchView;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CreateConnectionVu implements Vu, OnClickListener {

    private View view;
    private Context context;
    private EditText edit;

    //创建或者加入的布局
    private View layout_hide;
    private CheckBox mHelpCheckBox; //顶部帮助按钮
    private ImageView mHideButton;  //底部关闭按钮
    private ImageView mCreate;      //创建
    private ImageView mJoin;        //加入
    private ImageView mRightImage;  //右边小人
    private ImageView mLeftImage;   //左边小人


    private View mCenterCircle;     //中间布局
    private CircularProgress mCircleProgress;
    private TextView mStatus; //当前热点状态

    private View mCenterSearch;
    private SearchView mSearchView;
    private Timer timer;


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
        mCreate.setOnClickListener(this);
        mJoin.setOnClickListener(this);
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

        mCenterCircle = view.findViewById(R.id.centerCircle);
        mCircleProgress = (CircularProgress) view.findViewById(R.id.circleProgress);
        mStatus = (TextView) view.findViewById(R.id.status);

        mCenterSearch = view.findViewById(R.id.centerSearch);
        mSearchView = (SearchView) view.findViewById(R.id.searchView);

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
     * @param context
     * @version 1.0
     * @author zyh
     */
    public void createAP(Context context) {
        // WifiUtils wifiUtils = WifiUtils.getInstance(context);
        // WifiConfiguration wifiConfiguration =
        // wifiUtils.createWifiInfo(WifiConst.WIFI_AP_HEADER,
        // WifiConst.WIFI_AP_PASSWORD, 3, "ap");
        // wifiUtils.createWiFiAP(wifiConfiguration, true);

        if (WifiUtils.isWifiEnabled()) {
            // 执行关闭wifi
            WifiUtils.closeWifi();
        }

        // 创建热点
        WifiUtils.startWifiAp(WifiConst.WIFI_AP_HEADER + getLocalHostName(), WifiConst.WIFI_AP_PASSWORD,
                mHandler);
        //开启监听消息线程
        UDPMessageListener.getInstance(context).connectUDPSocket();
    }

    public void closeAP() {
        WifiUtils.closeWifiAp();
    }

    /**
     * 获取Wifi热点名
     * <p/>
     * <p>
     * BuildBRAND 系统定制商 ； BuildMODEL 版本
     * </p>
     *
     * @return 返回 定制商+版本 (String类型),用于创建热点。
     */
    public String getLocalHostName() {
        String str1 = Build.MODEL;
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
            case R.id.create:
                onCreateButtonClick();
                break;
            case R.id.join:
                onJoinButtonClick();
                break;
        }
    }

    /**
     * 点击加入按钮
     */
    private void onJoinButtonClick() {
        //动画
        startJoinAnim();


        //如果WiFi热点开启
        if(!WifiUtils.isWifiApEnabled()){
            WifiUtils.closeWifiAp();
        }

        //如果WiFi没有开启
        if(!WifiUtils.isWifiEnabled()){
            WifiUtils.OpenWifi();
        }

        //定时器
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //扫描
                WifiUtils.startScan();
                mHandler.sendEmptyMessage(WifiConst.ApScanResult);
            }
        }, new Date(), 2000);
    }

    /**
     * 加入动画
     */
    private void startJoinAnim() {
        mHelpCheckBox.setChecked(false);
        mHelpCheckBox.setVisibility(View.GONE);
        mCreate.setVisibility(View.GONE);

        hideButtonFlip();


        mSearchView.clearApView();

        int[] mJionLocation = new int[2];
        mJoin.getLocationOnScreen(mJionLocation);

        int mCenterSearchLocation[] = new int[2];
        mCenterSearch.getLocationOnScreen(mCenterSearchLocation);

//        Logger.d("createButton:" + mCreateLocation[0] + "," + mCreateLocation[1] + "      width:" + mCreate.getWidth() + ",height:" + mCreate.getHeight());
//        Logger.d("centerCircle:" + mCenterCircleLocation[0] + "," + mCenterCircleLocation[1] + "      width:" + mCenterCircle.getWidth() + ",height:" + mCenterCircle.getHeight());

        int mLeft = mJionLocation[0] - mCenterSearchLocation[0];
        int mTop = mJionLocation[1] - mCenterSearchLocation[1];

        float mScaleX = mJoin.getWidth() * 1.0f / mCenterSearch.getWidth();
        float mScaleY = mJoin.getHeight() * 1.0f / mCenterSearch.getHeight();


        float tx = -(mJionLocation[0] + mJoin.getWidth() / 2f - mCenterSearchLocation[0] - mCenterSearch.getWidth() / 2f) + mJoin.getTranslationX();
        float ty = -(mJionLocation[1] + mJoin.getHeight() / 2f - mCenterSearchLocation[1] - mCenterSearch.getHeight() / 2f) + mJoin.getTranslationY();

        mCenterSearch.setVisibility(View.VISIBLE);
        mCenterSearch.setPivotX(0f);
        mCenterSearch.setPivotY(0f);
        mCenterSearch.setScaleX(mScaleX);
        mCenterSearch.setScaleY(mScaleY);
        mCenterSearch.setTranslationX(mLeft);
        mCenterSearch.setTranslationY(mTop);
        mCenterSearch.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).
                setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        mJoin.animate().scaleX(0).scaleY(0).alpha(0).
                setDuration(200).setInterpolator(new DecelerateInterpolator()).start();

        mSearchView.startSearchAnim(1000);
    }

    /**
     * 点击创建按钮
     */
    private void onCreateButtonClick() {
        startCreateAnim();

        //2秒后创建
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createAP(context);
            }
        },2000);
    }

    /**
     * 创建热点动画
     */
    private void startCreateAnim() {
        mHelpCheckBox.setChecked(false);
        mHelpCheckBox.setVisibility(View.GONE);
        mJoin.setVisibility(View.GONE);

        hideButtonFlip();


        int[] mCreateLocation = new int[2];
        mCreate.getLocationOnScreen(mCreateLocation);

        int mCenterCircleLocation[] = new int[2];
        mCenterCircle.getLocationOnScreen(mCenterCircleLocation);

//        Logger.d("createButton:" + mCreateLocation[0] + "," + mCreateLocation[1] + "      width:" + mCreate.getWidth() + ",height:" + mCreate.getHeight());
//        Logger.d("centerCircle:" + mCenterCircleLocation[0] + "," + mCenterCircleLocation[1] + "      width:" + mCenterCircle.getWidth() + ",height:" + mCenterCircle.getHeight());

        int mLeft = mCreateLocation[0] - mCenterCircleLocation[0];
        int mTop = mCreateLocation[1] - mCenterCircleLocation[1];

        float mScaleX = mCreate.getWidth() * 1.0f / mCenterCircle.getWidth();
        float mScaleY = mCreate.getHeight() * 1.0f / mCenterCircle.getHeight();


        float tx = -(mCreateLocation[0] + mCreate.getWidth() / 2f - mCenterCircleLocation[0] - mCenterCircle.getWidth() / 2f) + mCreate.getTranslationX();
        float ty = -(mCreateLocation[1] + mCreate.getHeight() / 2f - mCenterCircleLocation[1] - mCenterCircle.getHeight() / 2f) + mCreate.getTranslationY();

        mCenterCircle.setVisibility(View.VISIBLE);
        mCenterCircle.setPivotX(0f);
        mCenterCircle.setPivotY(0f);
        mCenterCircle.setScaleX(mScaleX);
        mCenterCircle.setScaleY(mScaleY);
        mCenterCircle.setTranslationX(mLeft);
        mCenterCircle.setTranslationY(mTop);
        mCenterCircle.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).
                setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        mCreate.animate().scaleX(0).scaleY(0).alpha(0).
                setDuration(200).setInterpolator(new DecelerateInterpolator()).start();

        mCircleProgress.startCircleAnim(1000);
        mCircleProgress.startAnim(2000);
    }

    /**
     * 底部按钮flip
     */
    private void hideButtonFlip() {
        //flip动画
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(1000);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean first = true;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mHideButton.setRotationY(value * 180);
                if (value > 0.5 && first) {
                    mHideButton.setImageResource(R.drawable.btn_delete_normal);
                    first = false;
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 返回按钮
     */
    public void onBackPressed() {
        if(timer!=null){
            timer.cancel();
        }
        ((Activity) context).setResult(Activity.RESULT_OK);
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
                mJoin.setTranslationY(-value * dis * 0.2f);
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
                mLeftImage.setTranslationX((1 - value) * dis);
                mRightImage.setTranslationX((value - 1) * dis);
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

    private void connectAp(String hostName) {
        String ssid = WifiUtils.getApSSID();
        L.e(ssid);
        if (ssid.startsWith(WifiConst.WIFI_AP_HEADER)) {
            // 连接网络
            boolean connFlag = WifiUtils.connectWifi(hostName, WifiConst.WIFI_AP_PASSWORD,
                    WifiUtils.WifiCipherType.WIFICIPHER_WPA);
            if (connFlag) {
                T.show(context, "已连接");
//                edit.setText("已连接");
//                new Thread(new ConnectAPThread()).start();
            }
        }
    }

    private List<ScanResult> mList;//扫描到的WiFi列表

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//                super.handleMessage(msg);
            switch (msg.what) {
                case WifiConst.ApCreateApSuccess:
                    createApSuccess();
                    break;
                case WifiConst.ApScanResult:
                    mList = WifiUtils.getScanResults();
                    handleList();
                    break;
            }
        }
    };

    /**
     * 处理扫描到的Wifi列表
     */
    private void handleList() {
        for(ScanResult wifi : mList){
            String s = wifi.SSID;
            L.d("----------------"+wifi.SSID);
            if(s.startsWith(WifiConst.WIFI_AP_HEADER)){
                L.e("----------------"+wifi.SSID);
                mSearchView.addApView(wifi.SSID);
            }
        }
    }

    /**
     * 创建成功
     */
    private void createApSuccess() {

        String s = "创建成功\n请朋友加入\""+ getLocalHostName()+"\"";
        SpannableString ss = new SpannableString(s);
        ss.setSpan(new ForegroundColorSpan(Color.GREEN),0,4,SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(new RelativeSizeSpan(1.5f),0,4,SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        mStatus.setText(ss);
        mCircleProgress.finishAnim();
    }

}
