package com.zjk.wifiproject.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ConnectAPThread implements Runnable {

    @Override
    public void run() {
        try {
            InetAddress receiveHost = InetAddress.getByName("192.168.43.1");// 类静态方法

            DatagramSocket theSocket = new DatagramSocket();
            String message = "Hello world!";
            byte[] data = message.getBytes();
            // data = theLine.getBytes();
            DatagramPacket thePacket = new DatagramPacket(data, data.length, receiveHost, 2345);
            theSocket.send(thePacket);

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
