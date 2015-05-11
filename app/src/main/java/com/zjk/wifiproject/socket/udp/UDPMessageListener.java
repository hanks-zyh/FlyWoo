package com.zjk.wifiproject.socket.udp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.BaseApplication;
import com.zjk.wifiproject.chat.ChatActivity;
import com.zjk.wifiproject.config.ConfigBroadcast;
import com.zjk.wifiproject.config.ConfigIntent;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.socket.tcp.TcpService;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.ImageUtils;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.T;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 监听消息的线程
 * Created by Administrator on 2015/4/26.
 */
public class UDPMessageListener implements Runnable {

    private static final int POOL_SIZE = 5; //单个cpu线程个数
    private static final int BUFFERLENGTH = 1024; //缓存大小
    private static final String TAG = "UDPMessage";

    private static String BROADCASTIP;
    /**
     * 单例模式
     */
    private static UDPMessageListener udpMessageListener;
    private final ExecutorService exector;
    private Context context;
    private boolean isThreadRunning;
    private DatagramSocket udpSocket;
    private byte[] sendBuffer = new byte[BUFFERLENGTH];
    private byte[] receiveBuffer = new byte[BUFFERLENGTH];
    private DatagramPacket receiveDataPacket;
    private Thread receiveUDPThread;
    private DatagramPacket sendDatagramPacket;

    private UDPMessageListener(Context context) {

        this.context = context;
        BROADCASTIP = "255.255.255.255";

        int cpuNumber = Runtime.getRuntime().availableProcessors();
        exector = Executors.newFixedThreadPool(cpuNumber * POOL_SIZE); //根据CPU数目初始化线程池

    }

    /**
     * 返回UDPMessageListener的实例
     *
     * @param context
     * @return
     */
    public static UDPMessageListener getInstance(Context context) {
        if (udpMessageListener == null) {
            udpMessageListener = new UDPMessageListener(context);
        }
        return udpMessageListener;
    }


    @Override
    public void run() {

        while (isThreadRunning) {

            try {
                udpSocket.receive(receiveDataPacket);
            } catch (IOException e) {
                e.printStackTrace();
                //处理异常
                receiveDataPacket = null;
                stopUDPSocketThread();
                if (udpSocket != null) {
                    udpSocket.close();
                    udpSocket = null;
                }
                Logger.e("UDP数据包接受失败，停止线程");
                break;
            }

            if (receiveDataPacket.getLength() == 0) {
                Logger.e("无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            String UDPListenResStr = "";
            try {
                UDPListenResStr = new String(receiveBuffer, 0, receiveDataPacket.getLength(), IPMSGConst.IPMSG_CHARSET);
                Logger.i("接收到数据：" + UDPListenResStr);
                final String finalUDPListenResStr = UDPListenResStr;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        T.show(context, "接收到数据：" + finalUDPListenResStr);
                    }
                });

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Logger.e("系统不支持GBK编码");
            }

            IPMSGProtocol ipmsgRes = new IPMSGProtocol(UDPListenResStr);
            int commandNo = ipmsgRes.getCommandNo(); // 获取命令字
            String senderIMEI = ipmsgRes.getSenderIMEI();
            String senderIp = receiveDataPacket.getAddress().getHostAddress();

            //转TCP接收
            showToast("处理命令");
            processMessage(commandNo, ipmsgRes, senderIMEI, senderIp);

            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receiveDataPacket != null) {
                receiveDataPacket.setLength(BUFFERLENGTH);
            }

        }

        receiveDataPacket = null;
        if (udpSocket != null) {
            udpSocket.close();
            udpSocket = null;
        }
        receiveUDPThread = null;
    }

    private void showToast(final String msg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.show(context, msg);
            }
        });
    }

    /**
     * 建立Socket连接
     */
    public void connectUDPSocket() {

        try {
            //绑定端口
            if (udpSocket == null) {
                udpSocket = new DatagramSocket(IPMSGConst.PORT);
                Logger.i("connectUDPSocket()绑定端口成功");
            }

            //创建数据接受包
            if (receiveDataPacket == null) {
                receiveDataPacket = new DatagramPacket(receiveBuffer, BUFFERLENGTH);
            }

            startUDPSocketThread();

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void startUDPSocketThread() {
        if (receiveUDPThread == null) {
            receiveUDPThread = new Thread(this);
            receiveUDPThread.start();
        }
        isThreadRunning = true;
        Logger.i("启动线程");
    }

    public void stopUDPSocketThread() {
        isThreadRunning = false;
        if (receiveUDPThread != null) {
            receiveUDPThread.interrupt();
        }
        receiveUDPThread = null;
        udpMessageListener = null; //置空，消除静态变量引用
        Logger.i("停止线程");
    }


    /**
     * 发送UDP数据包
     *
     * @param commandNo 消息命令
     * @param targetIP  目标ID
     */
    public void sendUDPdata(int commandNo, String targetIP) {
        sendUDPdata(commandNo, targetIP, null);
    }

    /**
     * @param addData   附加数据
     * @param commandNo
     * @param targetIP
     */
    public void sendUDPdata(int commandNo, String targetIP, Object addData) {
        IPMSGProtocol ipmsgProtocol = null;

        String imei = ""; //

        if (addData == null) {
            ipmsgProtocol = new IPMSGProtocol(imei, commandNo);
        } else if (addData instanceof WFile) {
            ipmsgProtocol = new IPMSGProtocol(imei, commandNo, (WFile) addData);
        } else if (addData instanceof String) {
            ipmsgProtocol = new IPMSGProtocol(imei, commandNo, (String) addData);
        }
        sendUDPdata(ipmsgProtocol, targetIP);
    }

    public void sendUDPdata(final IPMSGProtocol ipmsgProtocol, final String targetIP) {
        exector.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    L.d("targetIP:"+targetIP);
                    InetAddress targetAddr = InetAddress.getByName(targetIP);
                    sendBuffer = ipmsgProtocol.getProtocolJSON().getBytes(IPMSGConst.IPMSG_CHARSET);
                    sendDatagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length, targetAddr, IPMSGConst.PORT);
                    //绑定端口
                    if (udpSocket == null) {
                        udpSocket = new DatagramSocket(IPMSGConst.PORT);
                        Logger.i("connectUDPSocket()绑定端口成功");
                    }
                    udpSocket.send(sendDatagramPacket);
                    Logger.i("数据UDP发送成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("数据UDP发送失败");
                }
            }
        });
    }


    public void processMessage(int commandNo, IPMSGProtocol ipmsgRes, String senderIMEI,
                               String senderIp) {
        TcpService tcpService;
        switch (commandNo) {

            // 客户端连接成功
            case IPMSGConst.CONNECT_SUCCESS: {
                L.i(TAG, "客户端连接成功");
//                addUser(ipmsgRes);
//                sendUDPdata(IPMSGConst.IPMSG_ANSENTRY, receiveDatagramPacket.getAddress(),
//                        mLocalUser);
                showToast("客户端连接成功");
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra(ConfigIntent.EXTRA_SENDER_IP,senderIp);
                A.goOtherActivityFinish(context, intent);

            }

            // 发送过来文本消息
            case IPMSGConst.IPMSG_SEND_TXT: {
                L.i(TAG, "接收到发来的文本消息");
//                addUser(ipmsgRes);
//                sendUDPdata(IPMSGConst.IPMSG_ANSENTRY, receiveDatagramPacket.getAddress(),
//                        mLocalUser);
                Intent intent = new Intent(ConfigBroadcast.ACTION_NEW_MSG);
                intent.putExtra("msg",ipmsgRes.getProtocolJSON());
                context.sendBroadcast(intent);

            }
            break;  case IPMSGConst.IPMSG_BR_ENTRY: {
                L.i(TAG, "收到上线通知");
//                addUser(ipmsgRes);
//                sendUDPdata(IPMSGConst.IPMSG_ANSENTRY, receiveDatagramPacket.getAddress(),
//                        mLocalUser);
                L.i(TAG, "成功发送上线应答");
            }
            break;

            // 收到上线应答，更新在线用户列表
            case IPMSGConst.IPMSG_ANSENTRY: {
                L.i(TAG, "收到上线应答");
//                addUser(ipmsgRes);
            }
            break;

            // 收到下线广播
            case IPMSGConst.IPMSG_BR_EXIT: {
//                removeOnlineUser(senderIMEI, 1);
                L.i(TAG, "成功删除imei为" + senderIMEI + "的用户");
            }
            break;

            case IPMSGConst.IPMSG_REQUEST_IMAGE_DATA:
                L.i(TAG, "收到IMAGE发送请求");

                tcpService = TcpService.getInstance(context);
                tcpService.setSavePath(BaseApplication.IMAG_PATH);
                tcpService.startReceive();
                sendUDPdata(IPMSGConst.IPMSG_CONFIRM_IMAGE_DATA, senderIp);
                break;

            case IPMSGConst.IPMSG_REQUEST_VOICE_DATA:
                L.i(TAG, "收到VOICE发送请求");

                tcpService = TcpService.getInstance(context);
                tcpService.setSavePath(BaseApplication.VOICE_PATH);
                tcpService.startReceive();
                sendUDPdata(IPMSGConst.IPMSG_CONFIRM_VOICE_DATA, senderIp);
                break;

            case IPMSGConst.IPMSG_SENDMSG: {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        T.show(context, "收到MSG信息");
                    }
                });
                L.i(TAG, "收到MSG消息");
                Message msg = (Message) ipmsgRes.getAddObject();

                switch (msg.getContentType()) {
                    case TEXT:
                        sendUDPdata(IPMSGConst.IPMSG_RECVMSG, senderIp, ipmsgRes.getPacketNo());
                        break;

                    case IMAGE:
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                T.show(context, "收到图片信息"  );
                            }
                        });
                        L.i(TAG, "收到图片信息");
                        msg.setMsgContent(BaseApplication.IMAG_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        String THUMBNAIL_PATH = BaseApplication.THUMBNAIL_PATH + File.separator
                                + msg.getSenderIMEI();

                        L.d(TAG, "缩略图文件夹路径:" + THUMBNAIL_PATH);
                        L.d(TAG, "图片文件路径:" + msg.getMsgContent());

                        ImageUtils.createThumbnail(context, msg.getMsgContent(), THUMBNAIL_PATH
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
                        tcpService = TcpService.getInstance(context);
                        tcpService.setSavePath(BaseApplication.FILE_PATH);
                        tcpService.startReceive();
                        sendUDPdata(IPMSGConst.IPMSG_CONFIRM_FILE_DATA, senderIp);
                        msg.setMsgContent(BaseApplication.FILE_PATH + File.separator
                                + msg.getSenderIMEI() + File.separator + msg.getMsgContent());
                        L.d(TAG, "文件路径:" + msg.getMsgContent());
                        break;
                }

//                // 加入数据库
//                mDBOperate.addChattingInfo(senderIMEI, SessionUtils.getIMEI(), msg.getSendTime(),
//                        msg.getMsgContent(), msg.getContentType());

                // 加入未读消息列表
                android.os.Message pMessage = new android.os.Message();
                pMessage.what = commandNo;
                pMessage.obj = msg;

//                ChatActivity v = ActivitiesManager.getChatActivity();
//                if (v == null) {
//                    addUnReadPeople(getOnlineUser(senderIMEI)); // 添加到未读用户列表
//                    for (int i = 0; i < mListenerList.size(); i++) {
//                        android.os.Message pMsg = new android.os.Message();
//                        pMsg.what = IPMSGConst.IPMSG_RECVMSG;
//                        mListenerList.get(i).processMessage(pMsg);
//                    }
//                }
//                else {
//                    v.processMessage(pMessage);
//                }
//
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
//                    v.processMessage(pMessage);
//                }

                break;

        } // End of switch
    }

}
