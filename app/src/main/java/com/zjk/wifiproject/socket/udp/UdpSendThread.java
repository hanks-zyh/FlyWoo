package com.zjk.wifiproject.socket.udp;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.util.T;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Administrator on 2015/5/8.
 */
public class UdpSendThread {

    private static final int TIMEOUT = 5000;  //设置接收数据的超时时间
    private static final int MAXNUM = 5;      //设置重发数据的最多次数

    private static final int UDP_PORT = 3000;
    private static final int BUFFER_SIZE = 1024;

    private String targetIP = "0.0.0.0";
    private Context mContext;
    private Handler mHandler;

    private static UdpSendThread udpSendThread;

    //创建单例模式
    public UdpSendThread(Context mContext, Handler mHandler, String ip) {
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.targetIP = ip;
    }

//    public static UdpSendThread getInstance(Context mContext, Handler mHandler, String ip){
//        if(udpSendThread ==null){
//            udpSendThread = new UdpSendThread(mContext,mHandler,ip);
//        }
//        return udpSendThread;
//    }
//

    public void send(final String str_send) {
        try {

//            //目的IP和端口
//            final DatagramSocket ds = new DatagramSocket(UDP_PORT); //这里不是服务器的端口
//            ds.setBroadcast(true);

              //使用流的防止解决中午乱码
//            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//            DataOutputStream dataStream = new DataOutputStream(ostream);
//            dataStream.writeUTF(str_send);
//            dataStream.close();

//            byte[] data = ostream.toByteArray();

            final DatagramSocket socket = new DatagramSocket(UDP_PORT+2);
            socket.setBroadcast(true);
            final  DatagramPacket packet = new DatagramPacket(str_send.getBytes(), str_send.length(),
                    getBroadcastAddress(), UDP_PORT);
//            socket.send(packet);


            //定义用来发送数据的DatagramPacket实例
//            final DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), getBroadcastAddress(), UDP_PORT); //将数据发送到服武器的ip和端口
            new Thread() {
                @Override
                public void run() {
                    //数据发向本地3000端口 //发送数据
                    try {
                        socket.send(packet);
                        Logger.i("发送UDP:" + str_send);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        socket.close();
                    }

                }
            }.start();
            T.show(mContext, "发送：" + str_send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi =(WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }
}
