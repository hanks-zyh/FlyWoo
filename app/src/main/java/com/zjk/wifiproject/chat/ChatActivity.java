package com.zjk.wifiproject.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.base.BaseActivity;
import com.zjk.wifiproject.config.ConfigBroadcast;
import com.zjk.wifiproject.config.ConfigIntent;
import com.zjk.wifiproject.entity.ChatEntity;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.entity.Users;
import com.zjk.wifiproject.picture.AlbumActivity;
import com.zjk.wifiproject.socket.tcp.TcpClient;
import com.zjk.wifiproject.socket.udp.IPMSGConst;
import com.zjk.wifiproject.socket.udp.IPMSGProtocol;
import com.zjk.wifiproject.socket.udp.UDPMessageListener;
import com.zjk.wifiproject.sql.SqlDBOperate;
import com.zjk.wifiproject.util.AlertDialogUtils;
import com.zjk.wifiproject.util.DateUtils;
import com.zjk.wifiproject.util.FileUtils;
import com.zjk.wifiproject.util.GsonUtils;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.T;
import com.zjk.wifiproject.util.WifiUtils;
import com.zjk.wifiproject.view.emoj.EmoViewPagerAdapter;
import com.zjk.wifiproject.view.emoj.EmoteAdapter;
import com.zjk.wifiproject.view.emoj.EmoticonsEditText;
import com.zjk.wifiproject.view.emoj.FaceText;
import com.zjk.wifiproject.view.emoj.FaceTextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/5/6.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, UDPMessageListener.OnNewMsgListener {

    //消息列表
    private List<ChatEntity> list = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChatAdapter mAdapter;

    //聊天底部按钮
    private Button btn_chat_emo, btn_chat_send, btn_chat_add, btn_chat_keyboard, btn_speak, btn_chat_voice;
    private EmoticonsEditText edit_user_comment;
    private String targetId = "";
    //    BmobChatUser targetUser;
    private static int MsgPagerNum;
    private LinearLayout layout_more, layout_emo, layout_add;
    private ViewPager pager_emo;
    private TextView tv_picture, tv_camera, tv_location;

    // 语音有关
    private RelativeLayout layout_record;
    private TextView tv_voice_tips;
    private ImageView iv_record;
    private Drawable[] drawable_Anims;// 话筒动画
    private ZRecordManager recordManager;


    //发送
    private String targetIp;

    //Handler
    private Handler mHandler;
    private MessageReveiver messageReveiver;
    private UDPMessageListener udpMessageListener;


    private String mIMEI;
    private Users mChatUser;
    private SqlDBOperate mDBOperate;
    private int mSenderID;
    private TcpClient tcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vu_chat);

        bindViews();
        initView();
        initListener();
        initHandler();
    }

    private void initListener() {
//        new Thread(new TcpReceiveThread(context, mHandler)).start();
        messageReveiver = new MessageReveiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConfigBroadcast.ACTION_NEW_MSG);
        registerReceiver(messageReveiver, filter);


        //初始化消息监听
        udpMessageListener = UDPMessageListener.getInstance(context);
        udpMessageListener.addMsgListener(this);
//        mID = SessionUtils.getLocalUserID();
//        mNickName = SessionUtils.getNickname();
        mIMEI = "";
        mChatUser = getIntent().getParcelableExtra(ConfigIntent.EXTRA_CHAT_USER);
        mDBOperate = new SqlDBOperate(this);
//        mSenderID = mDBOperate.getIDByIMEI(mChatUser.getIMEI());

//        Logger.d(mChatUser.toString());
        Logger.d(mChatUser.getIpaddress());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageReveiver != null) {
            unregisterReceiver(messageReveiver);
            messageReveiver = null;
        }
    }

    private void initView() {
        initRecylerView(); //list布局
        initBottomView();  //底部
        initVoiceView();   //语音
    }

    /**
     * 初始化RecylerView
     */
    private void initRecylerView() {
        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ChatAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void bindViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recylerView);
    }


    /**
     * 初始化语音布局
     *
     * @param
     * @return void
     * @throws
     * @Title: initVoiceView
     */
    private void initVoiceView() {
        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }

    /**
     * 录音管理器
     */
    private void initRecordManager() {
        // 语音相关管理器
        recordManager = ZRecordManager.getInstance(context);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new ZRecordManager.OnRecordChangeListener() {
            @Override
            public void onVolumnChanged(int value) {
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                L.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= ZRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    // 取消录音框
                    layout_record.setVisibility(android.view.View.INVISIBLE);
                    // 发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    // 是为了防止过了录音时间后，会多发一条语音出去的情况。
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                } else {
                }
            }
        });
    }

    @Override
    public void processMessage(IPMSGProtocol pMsg) {
        Logger.i("ChatActivity处理消息消息：" + GsonUtils.beanToJson(pMsg));
        android.os.Message msg = android.os.Message.obtain();
        msg.what = pMsg.commandNo;
        msg.obj = pMsg;
        mHandler.sendMessage(msg);
    }


    /**
     * 长按说话
     *
     * @author smile
     * @ClassName: VoiceTouchListen
     * @Description: TODO
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!FileUtils.isSdcardExist()) {
                        T.show(context, "发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(android.view.View.VISIBLE);
                        tv_voice_tips.setText(context.getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording(targetId);
                    } catch (Exception e) {
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips.setText(context.getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(context.getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(android.view.View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                            L.i("voice", "放弃发送语音");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                L.i("voice", "发送语音");
                                sendVoiceMessage(recordManager.getRecordFilePath(targetId), recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                layout_record.setVisibility(android.view.View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * 发送语音消息
     *
     * @param @param localPath
     * @return void
     * @throws
     * @Title: sendImageMessage
     * @Description: TODO
     */
    private void sendVoiceMessage(String local, int length) {
        ChatEntity chatMsg = new ChatEntity();
        chatMsg.setContent(local);
        chatMsg.setIsSend(true);
        chatMsg.setType(Message.CONTENT_TYPE.VOICE);
        chatMsg.setTime(System.currentTimeMillis());
        refreshMessage(chatMsg);
        //发送UDP
        sendMessage(local, Message.CONTENT_TYPE.VOICE);
    }

    private Toast toast;

    /**
     * 显示录音时间过短的Toast
     *
     * @return void
     * @throws
     * @Title: showShortToast
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(context);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    /**
     * 初始化语音动画资源
     *
     * @param
     * @return void
     * @throws
     * @Title: initVoiceAnimRes
     * @Description: TODO
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[]{context.getResources().getDrawable(R.drawable.chat_icon_voice2),
                context.getResources().getDrawable(R.drawable.chat_icon_voice3), context.getResources().getDrawable(R.drawable.chat_icon_voice4),
                context.getResources().getDrawable(R.drawable.chat_icon_voice5), context.getResources().getDrawable(R.drawable.chat_icon_voice6)};
    }

    /**
     * 加载消息历史，从数据库中读出
     */
//    private List<ChatEntity> initMsgData() {
//        List<ChatEntity> list = BmobDB.create(this).queryMessages(targetId, MsgPagerNum);
//        return list;
//    }

    /**
     * 界面刷新
     *
     * @param
     * @return void
     * @throws
     * @Title: initOrRefresh
     * @Description: TODO
     */
    private void initOrRefresh() {
//        if (mAdapter != null) {
//            if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
//                int news = MyMessageReceiver.mNewNum;// 有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
//                int size = initMsgData().size();
//                for (int i = (news - 1); i >= 0; i--) {
//                    mAdapter.add(initMsgData().get(size - (i + 1)));// 添加最后一条消息到界面显示
//                }
//                mRecyclerView.setSelection(mAdapter.getCount() - 1);
//            } else {
//                mAdapter.notifyDataSetChanged();
//            }
//        } else {
//            mAdapter = new MessageChatAdapter(this, initMsgData());
//            mRecyclerView.setAdapter(mAdapter);
//        }
    }

    private void initBottomView() {
        // 最左边
        btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
        btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
        btn_chat_add.setOnClickListener(this);
        btn_chat_emo.setOnClickListener(this);
        // 最右边
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);
        // 最下面
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);

        initAddView();
        initEmoView();

        // 最中间
        // 语音框
        btn_speak = (Button) findViewById(R.id.btn_speak);
        // 输入框
        edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
        edit_user_comment.setOnClickListener(this);
        edit_user_comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(android.view.View.VISIBLE);
                    btn_chat_keyboard.setVisibility(android.view.View.GONE);
                    btn_chat_voice.setVisibility(android.view.View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != android.view.View.VISIBLE) {
                        btn_chat_voice.setVisibility(android.view.View.VISIBLE);
                        btn_chat_send.setVisibility(android.view.View.GONE);
                        btn_chat_keyboard.setVisibility(android.view.View.GONE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 点击加号下的布局
     */
    private void initAddView() {
        tv_picture = (TextView) findViewById(R.id.tv_picture);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_picture.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
    }

    private List<FaceText> emos;

    /**
     * 初始化表情布局
     *
     * @throws
     */
    private void initEmoView() {
        pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtils.faceTexts;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
    }

    private View getGridView(final int i) {
        View view = View.inflate(context, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(context, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
                        int start = edit_user_comment.getSelectionStart();
                        CharSequence content = edit_user_comment.getText().insert(start, key);
                        edit_user_comment.setText(content);
                        // 定位光标位置
                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + key.length());
                        }
                    }
                } catch (Exception e) {

                }

            }
        });
        return view;
    }

    private void initXListView() {
//        // 首先不允许加载更多
//        mRecyclerView.setPullLoadEnable(false);
//        // 允许下拉
//        mRecyclerView.setPullRefreshEnable(true);
//        // 设置监听器
//        mRecyclerView.setXListViewListener(this);
//        mRecyclerView.pullRefreshing();
//        mRecyclerView.setDividerHeight(0);
//        // 加载数据
//        initOrRefresh();
//        mRecyclerView.setSelection(mAdapter.getCount() - 1);
//        mRecyclerView.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//                hideSoftInputView();
//                layout_more.setVisibility(View.GONE);
//                layout_add.setVisibility(View.GONE);
//                btn_chat_voice.setVisibility(View.VISIBLE);
//                btn_chat_keyboard.setVisibility(View.GONE);
//                btn_chat_send.setVisibility(View.GONE);
//                return false;
//            }
//        });
//
//        // 重发按钮的点击事件
//        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend, new MessageChatAdapter.onInternalClickListener() {
//
//            @Override
//            public void OnClickListener(View parentV, View v, Integer position, Object values) {
//                // 重发消息
//                showResendDialog(parentV, v, values);
//            }
//        });
    }

    /**
     * 显示重发按钮 showResendDialog
     *
     * @param @param recent
     * @return void
     * @throws
     * @Title: showResendDialog
     * @Description: TODO
     */
    public void showResendDialog(final View parentV, View v, final Object values) {
        AlertDialogUtils.show(context, "重新发送", "确定重发该消息", "确定", "取消", new AlertDialogUtils.OkCallBack() {
            @Override
            public void onOkClick(DialogInterface dialog, int which) {
//                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE || ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
//                    // 图片和语音类型的采用
//                    resendFileMsg(parentV, values);
//                } else {
//                    resendTextMsg(parentV, values);
//                }
            }
        }, null);
    }

    /**
     * 重发文本消息
     */
    private void resendTextMsg(final View parentV, final Object values) {
//        BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(targetUser, (BmobMsg) values, new PushListener() {
//            @Override
//            public void onSuccess() {
//                L.i("发送成功");
//                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
//                parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
//                parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
//                parentV.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
//                ((TextView) parentV.findViewById(R.id.tv_send_status)).setText("已发送");
//            }
//
//            @Override
//            public void onFailure(int arg0, String arg1) {
//
//                L.i("发送失败:" + arg1);
//                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
//                parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
//                parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
//                parentV.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
//            }
//        });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 重发图片消息
     *
     * @param @param parentV
     * @param @param values
     * @return void
     * @throws
     * @Title: resendImageMsg
     * @Description: TODO
     */
    private void resendFileMsg(final View parentV, final Object values) {
//        BmobChatManager.getInstance(ChatActivity.this).resendFileMessage(targetUser, (BmobMsg) values, new UploadListener() {
//
//            @Override
//            public void onStart(BmobMsg msg) {
//
//            }
//
//            @Override
//            public void onSuccess() {
//
//                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
//                parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
//                parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
//                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
//                    parentV.findViewById(R.id.tv_send_status).setVisibility(View.GONE);
//                    parentV.findViewById(R.id.tv_voice_length).setVisibility(View.VISIBLE);
//                } else {
//                    parentV.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
//                    ((TextView) parentV.findViewById(R.id.tv_send_status)).setText("已发送");
//                }
//            }
//
//            @Override
//            public void onFailure(int arg0, String arg1) {
//
//                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
//                parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
//                parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
//                parentV.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
//            }
//        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.edit_user_comment:// 点击文本输入框
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                if (layout_more.getVisibility() == android.view.View.VISIBLE) {
                    layout_add.setVisibility(android.view.View.GONE);
                    layout_emo.setVisibility(android.view.View.GONE);
                    layout_more.setVisibility(android.view.View.GONE);
                }
                break;
            case R.id.btn_chat_emo:// 点击笑脸图标
                if (layout_more.getVisibility() == android.view.View.GONE) {
                    showEditState(true);
                } else {
                    if (layout_add.getVisibility() == android.view.View.VISIBLE) {
                        layout_add.setVisibility(android.view.View.GONE);
                        layout_emo.setVisibility(android.view.View.VISIBLE);
                    } else {
                        layout_more.setVisibility(android.view.View.GONE);
                    }
                }

                break;
            case R.id.btn_chat_add:// 添加按钮-显示图片、拍照、位置
                if (layout_more.getVisibility() == android.view.View.GONE) {
                    layout_more.setVisibility(android.view.View.VISIBLE);
                    layout_add.setVisibility(android.view.View.VISIBLE);
                    layout_emo.setVisibility(android.view.View.GONE);
                    hideSoftInputView();
                } else {
                    if (layout_emo.getVisibility() == android.view.View.VISIBLE) {
                        layout_emo.setVisibility(android.view.View.GONE);
                        layout_add.setVisibility(android.view.View.VISIBLE);
                    } else {
                        layout_more.setVisibility(android.view.View.GONE);
                    }
                }

                break;
            case R.id.btn_chat_voice:// 语音按钮
                edit_user_comment.setVisibility(android.view.View.GONE);
                layout_more.setVisibility(android.view.View.GONE);
                btn_chat_voice.setVisibility(android.view.View.GONE);
                btn_chat_keyboard.setVisibility(android.view.View.VISIBLE);
                btn_speak.setVisibility(android.view.View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
                showEditState(false);
                break;
            case R.id.btn_chat_send:// 发送文本
                final String msg = edit_user_comment.getText().toString();
                if (msg.equals("")) {
                    T.show(context, "请输入发送消息!");
                    return;
                }
                sendTextMessage(msg);
//                // 组装BmobMessage对象
//                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
//                // 默认发送完成，将数据保存到本地消息表和最近会话表中
//                manager.sendTextMessage(targetUser, message);
                // 刷新界面
//                refreshMessage(message);
                break;
            case R.id.tv_camera:// 拍照
                selectImageFromCamera();
                break;
            case R.id.tv_picture:// 图片
                selectImageFromLocal();
                break;
            case R.id.tv_location:// 位置
                selectLocationFromMap();
                break;
            default:
                break;
        }
    }

    /**
     * 发送文本消息
     *
     * @param msg
     */
    private void sendTextMessage(String msg) {
        ChatEntity chatMsg = new ChatEntity();
        chatMsg.setContent(msg);
        chatMsg.setIsSend(true);
        chatMsg.setType(Message.CONTENT_TYPE.TEXT);
        refreshMessage(chatMsg);

        //发送UDP
        sendMessage(msg, Message.CONTENT_TYPE.TEXT);
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case IPMSGConst.NO_SEND_TXT: //接收到文本消息

                        break;

                    case IPMSGConst.AN_SEND_TXT:
                        //消除progress

                        break;

                    case IPMSGConst.NO_SEND_IMAGE: //客户端发送图片的请求

                        break;

                    case IPMSGConst.AN_SEND_IMAGE: //

                        break;
                }
            }
        };
    }

    /**
     * 发送文本消息
     *
     * @param content
     * @param type
     */
    public void sendMessage(String content, Message.CONTENT_TYPE type) {
        String nowtime = DateUtils.getNowtime();

        IPMSGProtocol command = new IPMSGProtocol();
        command.targetIP = mChatUser.getIpaddress();
        command.senderIP = WifiUtils.getLocalIPAddress();
        command.packetNo = new Date().getTime() + "";

        switch (type) {
            case TEXT:
//                UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, mChatUser.getIpaddress(), msg);
                command.commandNo = IPMSGConst.NO_SEND_TXT;
                command.addObject = new Message("", nowtime, content, type);
                break;
            case IMAGE:
//              UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_REQUEST_IMAGE_DATA, mChatUser.getIpaddress());
                command.commandNo = IPMSGConst.NO_SEND_IMAGE;
                command.addObject = new Message("", nowtime, content, type);
                break;
            case VOICE:
//              UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_REQUEST_VOICE_DATA, mChatUser.getIpaddress());
                command.commandNo = IPMSGConst.NO_SEND_VOICE;
                command.addObject = new Message("", nowtime, content, type);
                break;
            case FILE:
//                Message fileMsg = msg.clone();
//                fileMsg.setMsgContent(FileUtils.getNameByPath(msg.getMsgContent()));
//                UDPMessageListener.sendUDPdata(IPMSGConst.IPMSG_SENDMSG, mChatUser.getIpaddress(), fileMsg);
                break;
        }
//        mDBOperate.addChattingInfo(mID, mSenderID, nowtime, content, type);

        UDPMessageListener.sendUDPdata(command);
    }


    /**
     * 启动地图
     */
    private void selectLocationFromMap() {
//        Intent intent = new Intent(this, LocationActivity.class);
//        intent.putExtra("type", "select");
//        startActivityForResult(intent, Constants.REQUESTCODE_TAKE_LOCATION);
    }

    private String localCameraPath = "";// 拍照后得到的图片地址

    /**
     * 启动相机拍照 startCamera
     */
    public void selectImageFromCamera() {
//        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File dir = new File(Constants.PICTURE_PATH);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
//        localCameraPath = file.getPath();
//        Uri imageUri = Uri.fromFile(file);
//        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        startActivityForResult(openCameraIntent, Constants.REQUESTCODE_TAKE_CAMERA);
    }

    /**
     * 选择图片
     *
     * @param
     * @return void
     * @throws
     * @Title: selectImage
     * @Description: TODO
     */
    public void selectImageFromLocal() {

        startActivityForResult(new Intent(context, AlbumActivity.class), ConfigIntent.REQUEST_PICK_IMAGE);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ConfigIntent.REQUEST_PICK_IMAGE:// 当取到值的时候才上传path路径下的图片到服务器

                    List<String> images = data.getStringArrayListExtra(AlbumActivity.INTENT_SELECTED_PICTURE);
                    for (String s : images) {
                        L.i("本地图片的地址：" + s);
                        sendImageMessage(s);
                    }
                    break;
            }
        }
    }

    /**
     * 发送位置信息
     *
     * @param @param address
     * @param @param latitude
     * @param @param longtitude
     * @return void
     * @throws
     * @Title: sendLocationMessage
     * @Description: TODO
     */
    private void sendLocationMessage(String address, double latitude, double longtitude) {
//        if (layout_more.getVisibility() == View.VISIBLE) {
//            layout_more.setVisibility(View.GONE);
//            layout_add.setVisibility(View.GONE);
//            layout_emo.setVisibility(View.GONE);
//        }
//        // 组装BmobMessage对象
//        BmobMsg message = BmobMsg.createLocationSendMsg(this, targetId, address, latitude, longtitude);
//        // 默认发送完成，将数据保存到本地消息表和最近会话表中
//        manager.sendTextMessage(targetUser, message);
//        // 刷新界面
//        refreshMessage(message);
    }

    /**
     * 默认先上传本地图片，之后才显示出来 sendImageMessage
     *
     * @param @param localPath
     * @return void
     * @throws
     * @Title: sendImageMessage
     * @Description: TODO
     */
    private void sendImageMessage(String local) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        ChatEntity chatMsg = new ChatEntity();
        chatMsg.setContent(local);
        chatMsg.setIsSend(true);
        chatMsg.setTime(System.currentTimeMillis());
        chatMsg.setType(Message.CONTENT_TYPE.IMAGE);
        refreshMessage(chatMsg);
        //发送UDP
        sendMessage(local, Message.CONTENT_TYPE.IMAGE);
    }

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     *
     * @param @param isEmo: 用于区分文字和表情
     * @return void
     * @throws
     * @Title: showEditState
     * @Description: TODO
     */
    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(android.view.View.VISIBLE);
        btn_chat_keyboard.setVisibility(android.view.View.GONE);
        btn_chat_voice.setVisibility(android.view.View.VISIBLE);
        btn_speak.setVisibility(android.view.View.GONE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(android.view.View.VISIBLE);
            layout_more.setVisibility(android.view.View.VISIBLE);
            layout_emo.setVisibility(android.view.View.VISIBLE);
            layout_add.setVisibility(android.view.View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(android.view.View.GONE);
            showSoftInputView();
        }
    }

    // 显示软键盘
    public void showSoftInputView() {
        if (((Activity) context).getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (((Activity) context).getCurrentFocus() != null)
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_user_comment, 0);
        }
    }
//
//    @Override
//    protected void onResume() {
//
//        super.onResume();
//        // 新消息到达，重新刷新界面
//        initOrRefresh();
//        MyMessageReceiver.ehList.add(this);// 监听推送的消息
//        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
//        BmobNotifyManager.getInstance(this).cancelNotify();
//        BmobDB.create(this).resetUnread(targetId);
//        // 清空消息未读数-这个要在刷新之后
//        MyMessageReceiver.mNewNum = 0;
//    }

//    @Override
//    protected void onPause() {
//
//        super.onPause();
//        MyMessageReceiver.ehList.remove(this);// 监听推送的消息
//        // 停止录音
//        if (recordManager.isRecording()) {
//            recordManager.cancelRecording();
//            layout_record.setVisibility(View.GONE);
//        }
//        // 停止播放录音
//        if (NewRecordPlayClickListener.isPlaying && NewRecordPlayClickListener.currentPlayListener != null) {
//            NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
//        }

    //    }
    public static final int NEW_MESSAGE = 0x001;// 收到消息

    NewBroadcastReceiver receiver;

    private void initNewMessageBroadCast() {
//        // 注册接收消息广播
//        receiver = new NewBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
//        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
//        intentFilter.setPriority(5);
//        registerReceiver(receiver, intentFilter);
    }

    /**
     * 新消息广播接收者
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String from = intent.getStringExtra("fromId");
//            String msgId = intent.getStringExtra("msgId");
//            String msgTime = intent.getStringExtra("msgTime");
//            // 收到这个广播的时候，message已经在消息表中，可直接获取
//            BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
//            if (!from.equals(targetId)) // 如果不是当前正在聊天对象的消息，不处理
//                return;
//            // 添加到当前页面
//            mAdapter.add(msg);
//            // 定位
//            mRecyclerView.setSelection(mAdapter.getCount() - 1);
//            // 取消当前聊天对象的未读标示
//            BmobDB.create(ChatActivity.this).resetUnread(targetId);
//            // 记得把广播给终结掉
//            abortBroadcast();
        }
    }

    /**
     * 刷新界面
     *
     * @param @param message
     * @return void
     * @throws
     * @Title: refreshMessage
     * @Description: TODO
     */
    private void refreshMessage(ChatEntity msg) {
        // 更新界面
        list.add(msg);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        edit_user_comment.setText("");
    }

//
//    public void onRefresh() {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                MsgPagerNum++;
//                int total = BmobDB.create(ChatActivity.this).queryChatTotalCount(targetId);
//               L.i("记录总数：" + total);
//                int currents = mAdapter.getCount();
//                if (total <= currents) {
//                    T.show(context, "聊天记录加载完了哦!");
//                } else {
//                    List<BmobMsg> msgList = initMsgData();
//                    mAdapter.setList(msgList);
//                    mRecyclerView.setSelection(mAdapter.getCount() - currents - 1);
//                }
//                mRecyclerView.stopRefresh();
//            }
//        }, 1000);
//    }


//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (layout_more.getVisibility() == View.VISIBLE) {
//                layout_more.setVisibility(View.GONE);
//                return false;
//            } else {
//                return super.onKeyDown(keyCode, event);
//            }
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        hideSoftInputView();
//        try {
//            unregisterReceiver(receiver);
//        } catch (Exception e) {
//        }
//
//    }

    /**
     * 隐藏软键盘 hideSoftInputView
     *
     * @param
     * @return void
     * @throws
     * @Title: hideSoftInputView
     * @Description: TODO
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (((Activity) context).getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (((Activity) context).getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public class MessageReveiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int type = intent.getIntExtra(ConfigIntent.EXTRA_NEW_MSG_TYPE,ConfigIntent.NEW_MSG_TYPE_TXT);//默认是TXT
            String content = intent.getExtras().getString(ConfigIntent.EXTRA_NEW_MSG_CONTENT);

            ChatEntity chatMsg = new ChatEntity();
            chatMsg.setIsSend(false);
            chatMsg.setContent(content);
            chatMsg.setTime(System.currentTimeMillis());
            switch (type){
                case ConfigIntent.NEW_MSG_TYPE_TXT:
                    chatMsg.setType(Message.CONTENT_TYPE.TEXT);
                    break;
                case ConfigIntent.NEW_MSG_TYPE_IMAGE:
                    chatMsg.setType(Message.CONTENT_TYPE.IMAGE);
                    break;
                case ConfigIntent.NEW_MSG_TYPE_VOICE:
                    chatMsg.setType(Message.CONTENT_TYPE.VOICE);
                    break;
            }
            refreshMessage(chatMsg);
        }
    }

}
