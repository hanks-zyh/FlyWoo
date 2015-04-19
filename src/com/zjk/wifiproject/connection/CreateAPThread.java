package com.zjk.wifiproject.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class CreateAPThread implements Runnable {

    @Override
    public void run() {
        try {
            final int MAXLEN = 100;
            byte[] buffer = new byte[MAXLEN];// 字符数组初始化
            // byte[] buffer=new String().getBytes();//初始化字节数组
            DatagramSocket ds = new DatagramSocket(2345);// 接收端初始化Socket的时候一般需要绑定一个本地端口
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            // 注意DS和DP的构造方法分别有两种，一种是参数里面有地址信息，一种是无地址信息，比如
            // DatagramSocket 接收端需要端口信息，来绑定一个本地端口，以方便发送端制定特定的端口
            // 而DatagramPacket得接收端不需要地址信息，而发送端则需要地址信息，这里需要形象记忆，才能不搞混
            ds.receive(dp);
            int len = dp.getLength();
            System.out.println(len + " bytes received.\n");
            String s = new String(dp.getData(), 0, len);// 字节流转化为字符串的构造方法
            System.out.println(dp.getAddress() + "at port" + dp.getPort() + " says:" + s);

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
