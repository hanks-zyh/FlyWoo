package com.zjk.wifiproject.socket.udp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.chat.ChatActivity;
import com.zjk.wifiproject.config.ConfigBroadcast;
import com.zjk.wifiproject.config.ConfigIntent;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.T;
import com.zjk.wifiproject.util.WifiUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * UDP发送线程
 * Created by Administrator on 2015/5/8.
 */
public class UdpReceiverThread implements Runnable {


    private static final int UDP_PORT = 3000;
    private static final int BUFFER_SIZE = 1024;
    private boolean isRunning = true;
    private Context mContext;
    private Handler mHandler;

    public UdpReceiverThread(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {
        try {
            //服务端在端口监听
            DatagramSocket ds = new DatagramSocket(UDP_PORT);

            isRunning = true;
            while (isRunning) {


                byte[] buf = new byte[BUFFER_SIZE];
                //接收从客户端发送过来的数据
                DatagramPacket dp_receive = new DatagramPacket(buf, BUFFER_SIZE);

                //服务器端接收来自客户端的数据，阻塞
                Logger.d("udp开始监听："+ WifiUtils.getLocalIPAddress());
                ds.receive(dp_receive);

                //接收到数据
                //解决中文乱码问题
//                DataInputStream istream = new DataInputStream(new ByteArrayInputStream(dp_receive.getData(), dp_receive.getOffset(), dp_receive.getLength()));
//                String msg = istream.readUTF();
                String msg = new String(dp_receive.getData(), 0, dp_receive.getLength());

                final String str_receive = msg +
                        " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();

                Logger.d("接收到的UDP数据：" + str_receive);

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        T.show(mContext, "接收到的UDP数据：" + str_receive);
                    }
                });


                if (!msg.contains("zyh")) {
                    Intent i = new Intent(ConfigBroadcast.ACTION_NEW_MSG);
                    i.putExtra("msg", msg);
                    mContext.sendBroadcast(i);
                } else {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(ConfigIntent.EXTRA_SENDER_IP, dp_receive.getAddress().getHostAddress());
                    A.goOtherActivityFinish(mContext, intent);
                }


//                //数据发动到客户端的3000端口
//                DatagramPacket dp_send= new DatagramPacket(str_send.getBytes(),str_send.length(),dp_receive.getAddress(),9000);
//                ds.send(dp_send);

                //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
                //所以这里要将dp_receive的内部消息长度重新置为1024
//                dp_receive.setLength(BUFFER_SIZE);

            }
            ds.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
