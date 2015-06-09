package com.zjk.wifiproject.connection;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.BaseApplication;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.activity.wifiap.WifiConst;
import com.zjk.wifiproject.chat.ChatActivity;
import com.zjk.wifiproject.config.ConfigIntent;
import com.zjk.wifiproject.entity.Users;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.socket.udp.IPMSGConst;
import com.zjk.wifiproject.socket.udp.IPMSGProtocol;
import com.zjk.wifiproject.socket.udp.UDPMessageListener;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.PixelUtil;
import com.zjk.wifiproject.util.TextUtils;
import com.zjk.wifiproject.util.WifiUtils;
import com.zjk.wifiproject.view.CircularProgress;
import com.zjk.wifiproject.view.SearchView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CreateConnectionVu implements Vu, OnClickListener, UDPMessageListener.OnNewMsgListener {

    private View     view;
    private Context  context;
    private EditText edit;

    //创建或者加入的布局
    private View      layout_hide;
    private CheckBox  mHelpCheckBox; //顶部帮助按钮
    private ImageView mHideButton;  //底部关闭按钮
    private ImageView mCreate;      //创建
    private ImageView mJoin;        //加入
    private ImageView mRightImage;  //右边小人
    private ImageView mLeftImage;   //左边小人


    private View             mCenterCircle;     //中间布局
    private CircularProgress mCircleProgress;
    private TextView         mStatus; //当前热点状态

    private View       mCenterSearch;
    private SearchView mSearchView;
    private Timer      timer, cennectTimer;
    private String localHostName;  //创建的热点的名字
    private File   file;

    private String             localIPaddress; // 本地WifiIP
    private String             serverIPaddres; // 热点IP
    private UDPMessageListener mUDPListener;

    private List<String> mList = new ArrayList<>();//扫描到的WiFi列表


    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.activity_create_connection, null);
        context = inflater.getContext();
        bindViews();
        setListener();

        if (BaseApplication.sendFileStates.keySet().size() > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onCreateButtonClick();
                }
            }, 600);
        } else {
            showTwoButtonAnimation();
        }
    }

    @Override
    public View getView() {
        return view;
    }

    private void bindViews() {
//        view2.findViewById(R.id.open_ap).setOnClickListener(this);
//        view2.findViewById(R.id.open_wifi).setOnClickListener(this);
//        view2.findViewById(R.id.close_ap).setOnClickListener(this);
//        view2.findViewById(R.id.close_wifi).setOnClickListener(this);
//        view2.findViewById(R.id.connect_wifi).setOnClickListener(this);
//        view2.findViewById(R.id.send).setOnClickListener(this);
//        edit = (EditText)view2.findViewById(R.id.editText);
        //  layout_hide = view.findViewById(R.id.layout_create_or_join);

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

        String path = ((Activity) context).getIntent().getStringExtra(ConfigIntent.EXTRA_BLUR_PATH);
//        view.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeFile(path)));
    }

    private void setListener() {
        //返回按钮
        mHideButton.setOnClickListener(this);
        //创建按钮
        mCreate.setOnClickListener(this);
        //加入按钮
        mJoin.setOnClickListener(this);
        //提示按钮
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

    /**
     * 创建无线热点
     *
     * @param context
     * @version 1.0
     * @author zyh
     */
    public void createAP(final Context context) {
        // WifiUtils wifiUtils = WifiUtils.getInstance(context);
        // WifiConfiguration wifiConfiguration =
        // wifiUtils.createWifiInfo(WifiConst.WIFI_AP_HEADER,
        // WifiConst.WIFI_AP_PASSWORD, 3, "ap");
        // wifiUtils.createWiFiAP(wifiConfiguration, true);
        if (WifiUtils.isWifiEnabled()) {
            // 执行关闭wifi
            WifiUtils.closeWifi();
        }
        localHostName = getLocalHostName();
        // 创建热点
        WifiUtils.startWifiAp(WifiConst.WIFI_AP_HEADER + localHostName, WifiConst.WIFI_AP_PASSWORD,
                mHandler);
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
                //     layout_hide.setVisibility(View.VISIBLE);
                showTwoImageAnimation();
                break;
            case R.id.hideButton:
//                closeAP();
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

//        return;


        //动画
        startJoinAnim();


        //如果WiFi热点开启
//        if (!WifiUtils.isWifiApEnabled()) {
        WifiUtils.closeWifiAp();
//        }

        //如果WiFi没有开启
        if (!WifiUtils.isWifiEnabled()) {
            WifiUtils.OpenWifi();
        }

      /*  JSONObject json = new JSONObject();
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.z");
        try {
            json.put("", file);
            Logger.d("" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        //定时器,延迟2秒开启
        timer = new Timer();
        //定时任务：任务，开始时间，间隔时间
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //扫描
                WifiUtils.startScan();
                //给mHandler发送消息，消息在mHandler中处理
                mHandler.sendEmptyMessage(WifiConst.ApScanResult);
            }
        }, new Date(System.currentTimeMillis() + 2000), 2000);

/*
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchView.addApView("HTC");
            }
        },4000);*/

        //
        mSearchView.setOnAvatarClickListener(new SearchView.OnAvatarClickListener() {
            @Override
            /**
             * 点击热点头像，在addview里头像biew的点击事件触发时返回调用
             */
            public void onClick(View v) {
                onAvatarClick(v);
            }
        });

    }

    /**
     * 点击出现的热点头像
     *
     * @param v
     */
    private void onAvatarClick(final View v) {
        //动画
        startAvatarClickAnim(v);
        //停止搜索
        if (timer != null) {
            timer.cancel();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScanResult wifi = (ScanResult) v.getTag();
                connectAp(wifi.SSID);
            }
        }, 1500);

    }

    private void startAvatarClickAnim(final View v) {
        int[] mAvatarLocation = new int[2];
        v.getLocationOnScreen(mAvatarLocation);

        int mCenterSearchLocation[] = new int[2];
        mSearchView.getLocationOnScreen(mCenterSearchLocation);

        int mLeft = mCenterSearchLocation[0] + mSearchView.getWidth() / 2 - mAvatarLocation[0] - v.getWidth() / 2;
        int mTop = mCenterSearchLocation[1] + mSearchView.getHeight() / 2 - mAvatarLocation[1] - v.getHeight() / 2 + PixelUtil.dp2px(13);

        mSearchView.clearApViewButOne(v);//清除其他view
        mSearchView.hideBackground(); //隐藏背景红
        v.animate().translationX(mLeft).translationY(mTop).
                setDuration(300).setInterpolator(new DecelerateInterpolator()).start(); //头像移动到中心
        //300ms后执行的
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCenterCircle.setVisibility(View.VISIBLE);
                mStatus.setText("正在连接...");
                mCircleProgress.startScaleAnim(0); //圆+圆环动画
                mCircleProgress.startAnim(800); //进度条动画
            }
        }, 300);
        mSearchView.setTextColor(v, Color.BLACK); //设置字体颜色


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
        }, 2000);
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
        if (timer != null) {
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

    private void connectAp(final String hostName) {
        if (hostName.startsWith(WifiConst.WIFI_AP_HEADER)) {
            // 连接网络
            final boolean connFlag = WifiUtils.connectWifi(hostName, WifiConst.WIFI_AP_PASSWORD,
                    WifiUtils.WifiCipherType.WIFICIPHER_WPA);


            if (connFlag) {
                //扫描动画背景结束
                mCircleProgress.finishAnim();
                //这个定时器是为了每隔一秒发送一个消息验证是否正确连接（可能在connectWifi返回成功但还没有真正连接上），
                //验证逻辑在handler里
                cennectTimer = new Timer();
                cennectTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //给handler发一个连接成功的消息
                        Message.obtain(mHandler, WifiConst.WiFiConnectSuccess, hostName).sendToTarget();
//                        mHandler.sendEmptyMessage(WifiConst.WiFiConnectSuccess);
                    }
                }, new Date(), 1000);
            }


        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WifiConst.ApCreateApSuccess: //创建AP热点成功
                    createApSuccess();
                    break;
                case WifiConst.ApScanResult:
                    handleList(WifiUtils.getScanResults());
                    break;
                case WifiConst.WiFiConnectSuccess:
                    Logger.i("ip=" + serverIPaddres);
                    if (isValidated()) { //已连接上

                        mStatus.setText("连接成功");
                        //取消发送成功消息的定时器
                        cennectTimer.cancel();

                        //开启UDP连接，得到一个UDP线程的单例（可以监听端口和发送消息）
                        mUDPListener = UDPMessageListener.getInstance(context);
                        //将自身加入消息发送对象列表中
                        mUDPListener.addMsgListener(CreateConnectionVu.this);
                        mUDPListener.connectUDPSocket();

                        //创建"连接成功"指令 发送给服务器
                        IPMSGProtocol command = new IPMSGProtocol();

                        command.senderIP = localIPaddress;
                        command.targetIP = serverIPaddres;
                        command.commandNo = IPMSGConst.NO_CONNECT_SUCCESS; //连接成功指令发送给服务器，服务器确认后再跳展
                        command.packetNo = new Date().getTime() + "";

                        mUDPListener.sendUDPdata(command); //通知服务器已连接

                    }
                    break;


                //------------------------处理UDP指令
                //--------------------服务端
                case IPMSGConst.NO_CONNECT_SUCCESS: // 客户端连接成功
                    IPMSGProtocol command = (IPMSGProtocol) msg.obj;

                    Users user = new Users();
                    user.setIpaddress(command.senderIP);
                    user.setDevice(getPhoneModel());
                    goChatActivity(user);
                    break;

                //-------------------客户端
                case IPMSGConst.AN_CONNECT_SUCCESS: // 服务器确认连接成功
                    Users user2 = new Users();
                    user2.setDevice(getPhoneModel());
                    user2.setIpaddress(serverIPaddres);
                    goChatActivity(user2);
                    break;

                /*case IPMSGConst.IPMSG_ANSENTRY: // 用户上线应答
                    goChatActivity(null);
                    break;
                case IPMSGConst.IPMSG_BR_ENTRY: // 服务器收到用户上线应答
                    Logger.i("用户上线");
                    Users user = (Users) msg.obj; //取出客户端信息
                    goChatActivity(user);
                    break;*/
            }
        }
    };


    private void goChatActivity(Users user) {
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra(ConfigIntent.EXTRA_CHAT_USER, user);
        A.goOtherActivityFinish(context, i);
    }

    /**
     * 处理扫描到的Wifi列表
     *
     * @param scanResults
     */
    private void handleList(List<ScanResult> scanResults) {
        //遍历扫描到的结果
        for (ScanResult wifi : scanResults) {
            String s = wifi.SSID;
            //以ZChat_开头
            if (s.startsWith(WifiConst.WIFI_AP_HEADER)) {
                if (!mList.contains(s)) {//没有添加过
                    mList.add(wifi.SSID);
                    //添加热点头像
                    mSearchView.addApView(wifi);
                }
            }
        }

    }

    /**
     * 创建成功
     */
    private void createApSuccess() {
        String s = "创建成功\n请朋友加入\"" + localHostName + "\"";
        SpannableString ss = new SpannableString(s);
        ss.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(new RelativeSizeSpan(1.2f), 0, 4, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        mStatus.setText(ss);
        mCircleProgress.finishAnim();

        //开始监听 UDP
        mUDPListener = UDPMessageListener.getInstance(context);
        mUDPListener.addMsgListener(this);
        mUDPListener.connectUDPSocket();

    }

    /**
     * 设置IP地址信息
     */
    public void setIPaddress() {
        if (WifiUtils.isWifiApEnabled()) {
            serverIPaddres = localIPaddress = "192.168.43.1";
        } else {
            localIPaddress = WifiUtils.getLocalIPAddress();
            serverIPaddres = WifiUtils.getServerIPAddress();
        }
        L.i("localIPaddress:" + localIPaddress + " serverIPaddres:" + serverIPaddres);
    }

    /**
     * IP地址正确性验证
     *
     * @return boolean 返回是否为正确， 正确(true),不正确(false)
     */
    private boolean isValidated() {
        setIPaddress();
        String nullIP = "0.0.0.0";

        if (nullIP.equals(localIPaddress) || nullIP.equals(serverIPaddres)
                || localIPaddress == null || serverIPaddres == null) {
            return false;
        }

        return true;
    }

    /**
     * 接收到指令的回调处理
     *
     * @param pMsg
     */
    @Override
    public void processMessage(IPMSGProtocol pMsg) {
        Message msg = Message.obtain();
        msg.what = pMsg.commandNo;
        msg.obj = pMsg;
        mHandler.sendMessage(msg);
    }

    public void onDestroy() {
        if (mUDPListener != null) {
            mUDPListener.removeMsgListener(this);
        }

    }
}
