package com.zjk.wifiproject.socket.udp;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.BaseApplication;
import com.zjk.wifiproject.config.ConfigBroadcast;
import com.zjk.wifiproject.config.ConfigIntent;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.entity.Users;
import com.zjk.wifiproject.socket.tcp.TcpClient;
import com.zjk.wifiproject.socket.tcp.TcpService;
import com.zjk.wifiproject.sql.SqlDBOperate;
import com.zjk.wifiproject.util.GsonUtils;
import com.zjk.wifiproject.util.L;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPMessageListener implements Runnable {

    private static final String TAG = "SZU_UDPMessageListener";

    private static final int POOL_SIZE = 5; // 单个CPU线程池大小
    private static final int BUFFERLENGTH = 1024; // 缓冲大小

    private static byte[] sendBuffer = new byte[BUFFERLENGTH];
    private static byte[] receiveBuffer = new byte[BUFFERLENGTH];

    private HashMap<String, String> mLastMsgCache; // 最后一条消息缓存，以IMEI为KEY
    private ArrayList<Users> mUnReadPeopleList; // 未读消息的用户队列
    private HashMap<String, Users> mOnlineUsers; // 在线用户集合，以IMEI为KEY

    private String BROADCASTIP;
    private Thread receiveUDPThread;
    private boolean isThreadRunning;
    private List<OnNewMsgListener> mListenerList;

    private Users mLocalUser; // 本机用户对象
    private SqlDBOperate mDBOperate;

    private static ExecutorService executor;
    private static DatagramSocket UDPSocket;
    private static DatagramPacket sendDatagramPacket;
    private DatagramPacket receiveDatagramPacket;

    private static Context mContext;
    private static UDPMessageListener instance;
    private Handler mHanlder;


    private UDPMessageListener() {
        BROADCASTIP = "255.255.255.255";
        // BROADCASTIP = WifiUtils.getBroadcastAddress();

        mDBOperate = new SqlDBOperate(mContext);
        mListenerList = new ArrayList<OnNewMsgListener>();
        mOnlineUsers = new LinkedHashMap<String, Users>();
        mLastMsgCache = new HashMap<String, String>();
        mUnReadPeopleList = new ArrayList<Users>();

        int cpuNums = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cpuNums * POOL_SIZE); // 根据CPU数目初始化线程池

    }

    /**
     * <p/>
     * 获取UDPSocketThread实例
     * <p/>
     * 单例模式，返回唯一实例
     *
     * @return instance
     */
    public static UDPMessageListener getInstance(Context context) {
        if (instance == null) {
            mContext = context;
            instance = new UDPMessageListener();
        }
        return instance;
    }

    @Override
    public void run() {
        while (isThreadRunning) {

            try {
                //这是一个阻塞的方法
                UDPSocket.receive(receiveDatagramPacket);
            } catch (IOException e) {
                isThreadRunning = false;
                receiveDatagramPacket = null;
                if (UDPSocket != null) {
                    UDPSocket.close();
                    UDPSocket = null;
                }
                receiveUDPThread = null;
                L.e(TAG, "UDP数据包接收失败！线程停止");
                showToast("UDP数据包接收失败！线程停止");
                e.printStackTrace();
                break;
            }

            if (receiveDatagramPacket.getLength() == 0) {
                L.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                showToast("UDP数据包接收失败！线程停止");
                continue;
            }

            String resStr = ""; //接收到的字符串
            try {
                resStr = new String(receiveBuffer, 0, receiveDatagramPacket.getLength(), "gbk");
            } catch (UnsupportedEncodingException e) {
                L.e(TAG, "系统不支持GBK编码");
            }

            //打印一下
         //   showToast(resStr);
            Logger.i("接收到" + resStr);

            String senderIp = receiveDatagramPacket.getAddress().getHostAddress();
            //将json串转成IPMSGProtocol对象
            IPMSGProtocol ipmsgRes = GsonUtils.jsonToBean(resStr, IPMSGProtocol.class);
            processMessage(ipmsgRes, senderIp);

            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receiveDatagramPacket != null) {
                receiveDatagramPacket.setLength(BUFFERLENGTH);
            }

        }//while

        receiveDatagramPacket = null;
        if (UDPSocket != null) {
            UDPSocket.close();
            UDPSocket = null;
        }
        receiveUDPThread = null;

    }

    /**
     * 处理接收到的UDP数据
     *
     * @param ipmsgRes
     * @param senderIp
     */
    public void processMessage(IPMSGProtocol ipmsgRes, String senderIp) {

        int commandNo = ipmsgRes.commandNo;
        Logger.i("处理来自：" + senderIp + "命令：" + commandNo);

        TcpService tcpService = TcpService.getInstance(mContext);
        tcpService.setHandler(mHanlder);

        TcpClient tcpClient = TcpClient.getInstance(mContext);
        tcpClient.setHandler(mHanlder);

        switch (commandNo) {

            /*-------------------服务器------------------------------*/
            case IPMSGConst.NO_CONNECT_SUCCESS: { //接收到客户端连接成功

             //   showToast("收到上线通知");


                //确认指令
                sendUDPdata(getConfirmCommand(IPMSGConst.AN_CONNECT_SUCCESS, ipmsgRes.targetIP, senderIp));

                L.i(TAG, "成功发送上线应答");
            //    showToast("成功发送上线应答");
            }
            break;

            case IPMSGConst.NO_SEND_TXT: { //客户端发来文本消息
                Logger.i("客户端发来文本消息");

                //新消息广播
                Message textMsg = ipmsgRes.addObject;
                Intent intent = new Intent(ConfigBroadcast.ACTION_NEW_MSG);
                intent.putExtra(ConfigIntent.EXTRA_NEW_MSG_TYPE, ConfigIntent.NEW_MSG_TYPE_TXT);
                intent.putExtra(ConfigIntent.EXTRA_NEW_MSG_CONTENT, textMsg.getMsgContent());
                mContext.sendBroadcast(intent);

                sendUDPdata(getConfirmCommand(IPMSGConst.AN_SEND_TXT, ipmsgRes.targetIP, senderIp));
            }
            break;

            case IPMSGConst.NO_SEND_IMAGE: { //客户端发来图片
                Logger.i("客户端发来图片请求");
             //   showToast("客户端发来图片请求");

                tcpService.setSavePath(BaseApplication.IMAG_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_IMAGE, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);
            }
            break;

            case IPMSGConst.NO_SEND_VOICE: { //客户端发来语音
                Logger.i("客户端发来语音请求");
             //   showToast("客户端发来语音请求");

                tcpService.setSavePath(BaseApplication.VOICE_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_VOICE, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);
            }
            break;

            case IPMSGConst.NO_SEND_VEDIO: { //发送视频
                Logger.i("客户端发送视频请求");
            //    showToast("客户端发送视频请求");

                tcpService.setSavePath(BaseApplication.VEDIO_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_VEDIO, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);

                Intent intent = new Intent(ConfigBroadcast.ACTION_NEW_MSG);
                intent.putExtra(ConfigIntent.EXTRA_NEW_MSG_TYPE, ConfigIntent.NEW_MSG_TYPE_VEDIO);
                intent.putExtra(ConfigIntent.EXTRA_NEW_MSG_CONTENT, ipmsgRes.addObject.getMsgContent());
                mContext.sendBroadcast(intent);
            }
            break;
            case IPMSGConst.NO_SEND_MUSIC: { //发送音乐
                Logger.i("客户端发送音乐请求");
              //  showToast("客户端发送音乐请求");
                tcpService.setSavePath(BaseApplication.MUSIC_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_MUSIC, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);

                Intent intent = new Intent(ConfigBroadcast.ACTION_NEW_MSG);
                intent.putExtra(ConfigIntent.EXTRA_NEW_MSG_TYPE, ConfigIntent.NEW_MSG_TYPE_MUSIC);
                intent.putExtra(ConfigIntent.EXTRA_NEW_MSG_CONTENT, ipmsgRes.addObject.getMsgContent());
                mContext.sendBroadcast(intent);
            }
            break;
            case IPMSGConst.NO_SEND_FILE: { //发送文件
                Logger.i("客户端发送文件请求");
             //   showToast("客户端发送文件请求");

                tcpService.setSavePath(BaseApplication.FILE_PATH);
                tcpService.startReceive();

                IPMSGProtocol command = getConfirmCommand(IPMSGConst.AN_SEND_FILE, ipmsgRes.targetIP, senderIp);
                command.addObject = ipmsgRes.addObject;
                sendUDPdata(command);

                Intent intent = new Intent(ConfigBroadcast.ACTION_NEW_MSG);
                intent.putExtra(ConfigIntent.EXTRA_NEW_MSG_TYPE, ConfigIntent.NEW_MSG_TYPE_FILE);
                intent.putExtra(ConfigIntent.EXTRA_NEW_MSG_CONTENT, ipmsgRes.addObject.getMsgContent());
                mContext.sendBroadcast(intent);
            }
            break;

            /*-------------------客户端------------------------------*/
            case IPMSGConst.AN_CONNECT_SUCCESS: { //服务器确认连接成功

            }
            break;

            case IPMSGConst.AN_SEND_TXT: { //服务器确认成功接收文本消息
            }
            break;

            case IPMSGConst.AN_SEND_IMAGE: { //服务器确认成功接收图片
                Message textMsg = ipmsgRes.addObject;
                Logger.d("接收方确认图片请求,发送的文件为" + textMsg.getMsgContent());
            //    showToast("开始发送图片");

                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.IMAGE);
            }
            break;

            case IPMSGConst.AN_SEND_VOICE: { //服务器确认成功接收图片
                Message textMsg = ipmsgRes.addObject;
                Logger.d("接收方确认语音请求,发送的文件为" + textMsg.getMsgContent());
             //   showToast("开始发送语音");
                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.VOICE);
            }
            break;

            case IPMSGConst.AN_SEND_VEDIO: { //服务器确认接收视频
                Message textMsg = ipmsgRes.addObject;
                Logger.d("接收方确认文件请求,发送的文件为" + textMsg.getMsgContent());
             //   showToast("开始发送文件");
                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.VEDIO);
            }
            break;
            case IPMSGConst.AN_SEND_MUSIC: { //服务器确认接收音乐
                Message textMsg = ipmsgRes.addObject;
                Logger.d("接收方确认文件请求,发送的文件为" + textMsg.getMsgContent());
           //     showToast("开始发送文件");
                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.MUSIC);
            }
            break;
            case IPMSGConst.AN_SEND_FILE: { //服务器确认接收文件
                Message textMsg = ipmsgRes.addObject;
                Logger.d("接收方确认文件请求,发送的文件为" + textMsg.getMsgContent());
             //   showToast("开始发送文件");
                tcpClient.startSend();
                tcpClient.sendFile(textMsg.getMsgContent(), senderIp, Message.CONTENT_TYPE.FILE);
            }
            break;

           /* // 收到上线数据包，添加用户，并回送IPMSG_ANSENTRY应答。
            case IPMSGConst.IPMSG_BR_ENTRY: {
                L.i(TAG, "收到上线通知");
                showToast("收到上线通知");
//                addUser(ipmsgRes);
                sendUDPdata(IPMSGConst.IPMSG_ANSENTRY, senderIp,
                        mLocalUser);
                L.i(TAG, "成功发送上线应答");
                showToast("成功发送上线应答");
            }
            break;

            // 收到上线应答，更新在线用户列表
            case IPMSGConst.IPMSG_ANSENTRY: {
                L.i(TAG, "收到上线应答");
                showToast("收到上线应答");
//                addUser(ipmsgRes);

            }
            break;
            // 收到下线广播
            case IPMSGConst.IPMSG_BR_EXIT: {
                removeOnlineUser(senderIMEI, 1);
                L.i(TAG, "成功删除imei为" + senderIMEI + "的用户");
            }
            break;

            case IPMSGConst.IPMSG_REQUEST_IMAGE_DATA:
                L.i(TAG, "收到IMAGE发送请求");

                tcpService = TcpService.getInstance(mContext);
                tcpService.setSavePath(BaseApplication.IMAG_PATH);
                tcpService.startReceive();
                sendUDPdata(IPMSGConst.IPMSG_CONFIRM_IMAGE_DATA, senderIp);
                break;

            case IPMSGConst.IPMSG_REQUEST_VOICE_DATA:
                L.i(TAG, "收到VOICE发送请求");

                tcpService = TcpService.getInstance(mContext);
                tcpService.setSavePath(BaseApplication.VOICE_PATH);
                tcpService.startReceive();
                sendUDPdata(IPMSGConst.IPMSG_CONFIRM_VOICE_DATA, senderIp);
                break;

            case IPMSGConst.IPMSG_SENDMSG: {
                L.i(TAG, "收到MSG消息");
                Message msg = (Message) ipmsgRes.getAddObject();

                switch (msg.getContentType()) {
                    case TEXT:
                        Intent intent = new Intent(ConfigBroadcast.ACTION_NEW_MSG);
                        intent.putExtra("msg", msg.getMsgContent());
                        mContext.sendBroadcast(intent);
                        sendUDPdata(IPMSGConst.IPMSG_RECVMSG, senderIp, ipmsgRes.getPacketNo());
                        break;

                    case IMAGE:
                        L.i(TAG, "收到图片信息");
                        msg.setMsgContent(BaseApplication.IMAG_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        String THUMBNAIL_PATH = BaseApplication.THUMBNAIL_PATH + File.separator
                                + msg.getSenderIMEI();

                        L.d(TAG, "缩略图文件夹路径:" + THUMBNAIL_PATH);
                        L.d(TAG, "图片文件路径:" + msg.getMsgContent());

                        ImageUtils.createThumbnail(mContext, msg.getMsgContent(), THUMBNAIL_PATH
                                + File.separator);
                        break;

                    case VOICE:
                        L.i(TAG, "收到录音信息");
                        msg.setMsgContent(BaseApplication.VOICE_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        L.d(TAG, "文件路径:" + msg.getMsgContent());
                        break;

                    case FILE:
                        L.i(TAG, "收到文件 发送请求");
                        tcpService = TcpService.getInstance(mContext);
                        tcpService.setSavePath(BaseApplication.FILE_PATH);
                        tcpService.startReceive();
                        sendUDPdata(IPMSGConst.IPMSG_CONFIRM_FILE_DATA, senderIp);
                        msg.setMsgContent(BaseApplication.FILE_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        L.d(TAG, "文件路径:" + msg.getMsgContent());
                        break;
                }

                // 加入数据库
//                mDBOperate.addChattingInfo(senderIMEI, SessionUtils.getIMEI(), msg.getSendTime(),
//                        msg.getMsgContent(), msg.getContentType());

                // 加入未读消息列表
                android.os.Message pMessage = new android.os.Message();
                pMessage.what = commandNo;
                pMessage.obj = msg;

//                ChatActivity v = ActivitiesManager.getChatActivity();
//                if (v == null) {
//                    addUnReadPeople(getOnlineUser(senderIMEI)); // 添加到未读用户列表
                showToast("listenerSize=" + mListenerList.size());
                for (int i = 0; i < mListenerList.size(); i++) {
                    android.os.Message pMsg = new android.os.Message();
                    pMsg.what = commandNo;

                    mListenerList.get(i).processMessage(pMsg);
                }
//                }
//                else {
//                    v.processMessage(pMessage);
//                }
//                addLastMsgCache(senderIMEI, msg); // 添加到消息缓存
                BaseApplication.playNotification();
            }
            break;
            default:
                L.i(TAG, "收到命令：" + commandNo);
                android.os.Message pMessage = new android.os.Message();
                pMessage.what = commandNo;

//                ChatActivity v = ActivitiesManager.getChatActivity();
//                if (v != null) {
////                    v.processMessage(pMessage);
//                }

                break;
*/
        } // End of switch
        //回调处理
        callBack(ipmsgRes);
    }

    /**
     * 创建一个新的指令
     *
     * @param commandNo
     * @param senderIp
     * @param targetIP
     */
    private IPMSGProtocol getConfirmCommand(int commandNo, String senderIp, String targetIP) {
        IPMSGProtocol command = new IPMSGProtocol();
        command.commandNo = commandNo;
        command.senderIP = senderIp;
        command.targetIP = targetIP;
        command.packetNo = new Date().getTime() + "";
        return command;
    }

    /**
     * 回调给Listener
     *
     * @param ipmsgRes
     */
    private void callBack(IPMSGProtocol ipmsgRes) {
      //  showToast("listenerSize=" + mListenerList.size());
        for (int i = 0; i < mListenerList.size(); i++) {
            mListenerList.get(i).processMessage(ipmsgRes);
        }
    }

    /**
     * 建立Socket连接 *
     * 绑定监听的端口，初始化一个数据报包用来接收
     */
    public void connectUDPSocket() {
        try {
            // 绑定端口
            if (UDPSocket == null)
                UDPSocket = new DatagramSocket(IPMSGConst.PORT);
            L.i(TAG, "connectUDPSocket() 绑定端口成功");

            // 创建数据接受包
            if (receiveDatagramPacket == null)
                receiveDatagramPacket = new DatagramPacket(receiveBuffer, BUFFERLENGTH);

            startUDPSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始监听线程 *
     */
    public void startUDPSocketThread() {
        if (receiveUDPThread == null) {
            //将本runnable的实现放进thread
            receiveUDPThread = new Thread(this);
            receiveUDPThread.start();   //执行run方法
        }
        isThreadRunning = true;
        L.i(TAG, "startUDPSocketThread() 线程启动成功");
    }

    /**
     * 暂停监听线程 *
     */
    public void stopUDPSocketThread() {
        isThreadRunning = false;
        if (receiveUDPThread != null)
            receiveUDPThread.interrupt();
        receiveUDPThread = null;
        instance = null; // 置空, 消除静态变量引用
        L.i(TAG, "stopUDPSocketThread() 线程停止成功");
    }

    public void addMsgListener(OnNewMsgListener listener) {
        //等会要发送消息的对象列表
        this.mListenerList.add(listener);
    }

    public void removeMsgListener(OnNewMsgListener listener) {
        this.mListenerList.remove(listener);
    }


    /**
     * 发送UDP数据包
     *
     * @param ipmsgProtocol 附加的Json指令
     */
    public static void sendUDPdata(final IPMSGProtocol ipmsgProtocol) {

        final String targetIP = ipmsgProtocol.targetIP;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.i(targetIP);
                    InetAddress targetAddr = InetAddress.getByName(targetIP); // 目的地址
                    sendBuffer = GsonUtils.beanToJson(ipmsgProtocol).getBytes("gbk");
                    sendDatagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length, targetAddr, IPMSGConst.PORT);
                    UDPSocket.send(sendDatagramPacket);
                    L.i(TAG, "sendUDPdata() 数据发送成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e(TAG, "sendUDPdata() 发送UDP数据包失败");
                }

            }
        });

    }


    public Users getOnlineUser(String paramIMEI) {
        return mOnlineUsers.get(paramIMEI);
    }


    public HashMap<String, Users> getOnlineUserMap() {
        return mOnlineUsers;
    }

    /**
     * 新增用户缓存
     *
     * @param paramIMEI 新增记录的对应用户IMEI
     * @param msg       需要缓存的消息对象
     */
    public void addLastMsgCache(String paramIMEI, Message msg) {
        StringBuffer content = new StringBuffer();
        switch (msg.getContentType()) {
            case FILE:
                content.append("<FILE>: ").append(msg.getMsgContent());
                break;
            case IMAGE:
                content.append("<IMAGE>: ").append(msg.getMsgContent());
                break;
            case VOICE:
                content.append("<VOICE>: ").append(msg.getMsgContent());
                break;
            default:
                content.append(msg.getMsgContent());
                break;
        }
        if (msg.getMsgContent().isEmpty()) {
            content.append(" ");
        }
        mLastMsgCache.put(paramIMEI, content.toString());
    }

    /**
     * 获取消息缓存
     *
     * @param paramIMEI 需要获取消息缓存记录的用户IMEI
     * @return
     */
    public String getLastMsgCache(String paramIMEI) {
        return mLastMsgCache.get(paramIMEI);
    }

    /**
     * 移除消息缓存
     *
     * @param paramIMEI 需要清除缓存的用户IMEI
     */
    public void removeLastMsgCache(String paramIMEI) {
        mLastMsgCache.remove(paramIMEI);
    }

    public void clearMsgCache() {
        mLastMsgCache.clear();
    }

    public void clearUnReadMessages() {
        mUnReadPeopleList.clear();
    }

    /**
     * 新增未读消息用户
     * ng
     *
     * @param people
     */
    public void addUnReadPeople(Users people) {
        if (!mUnReadPeopleList.contains(people))
            mUnReadPeopleList.add(people);
    }

    /**
     * 获取未读消息队列
     *
     * @return
     */
    public ArrayList<Users> getUnReadPeopleList() {
        return mUnReadPeopleList;
    }

    /**
     * 获取未读用户数
     *
     * @return
     */
    public int getUnReadPeopleSize() {
        return mUnReadPeopleList.size();
    }

    /**
     * 移除指定未读用户
     *
     * @param people
     */
    public void removeUnReadPeople(Users people) {
        if (mUnReadPeopleList.contains(people))
            mUnReadPeopleList.remove(people);
    }

    public void setHandler(Handler mHandler) {
        this.mHanlder = mHandler;
    }

    /**
     * 新消息处理接口
     */
    public interface OnNewMsgListener {
        void processMessage(IPMSGProtocol pMsg);
    }

    public void showToast(final String s) {
      /*  ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.show(mContext, s);
            }
        });*/
    }
}