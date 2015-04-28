package com.zjk.wifiproject.socket;

import android.app.Activity;
import android.content.Context;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.util.T;

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
                    udpSocket = null;
                    udpSocket.close();
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
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        T.show(context, "接收到数据：" + finalUDPListenResStr);
                    }
                });


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Logger.e("系统不支持GBK编码");
            }


        }
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
     * @param commandNo 消息命令
     * @param targetIP 目标ID
     *
     */
    public void sendUDPdata(int commandNo,String targetIP){
        sendUDPdata(commandNo, targetIP, null);
    }

    /**
     * @param addData 附加数据
     * @param commandNo
     * @param targetIP
     */
    public void sendUDPdata(int commandNo, String targetIP, Object addData) {
        IPMSGProtocol ipmsgProtocol = null;

        String imei = ""; //

        if(addData==null){
            ipmsgProtocol = new IPMSGProtocol(imei,commandNo);
        }else if(addData instanceof WFile ){
            ipmsgProtocol = new IPMSGProtocol(imei,commandNo,(WFile)addData);
        }else if(addData instanceof String ){
            ipmsgProtocol = new IPMSGProtocol(imei,commandNo,(String)addData);
        }
        sendUDPdata(ipmsgProtocol,targetIP);
    }

    public void sendUDPdata(final IPMSGProtocol ipmsgProtocol, final String targetIP){

        exector.execute(new Runnable() {
            @Override
            public void run() {
                try {
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
}
