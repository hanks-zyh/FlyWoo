package com.zjk.wifiproject.socket.tcp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.zjk.wifiproject.util.FileUtils;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.T;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2015/5/8.
 */
public class TcpSendThread implements Runnable {

    private final String targetIp;
    private final Context mContext;

    public TcpSendThread(Context context, Handler mHandler, String targetIp) {
        this.mContext = context;
        this.targetIp = targetIp;
    }

    @Override
    public void run() {

        OutputStream os = null;      //到服务器的输出流
        FileInputStream fis = null;  //文件读取流
        Socket socket = null;          //连接到服务器的sock

        try {

            long start = System.currentTimeMillis();

            int current = 0;

            // 创建Socket，连接服务端
            socket = new Socket(targetIp, TcpClient.TCP_SERVER_RECEIVE_PORT);

            L.i("Connecting...");
            showToast("连接：" + targetIp + ",端口：" + TcpClient.TCP_SERVER_RECEIVE_PORT);
            // receive file
            byte[] buffer = new byte[TcpClient.READ_BUFFER_SIZE];
            os = socket.getOutputStream();

            File file = new File(FileUtils.getSDPath() + "/b.jpg");

            if (!file.exists()) {
                showToast("b.jpg不存在");
                return;
            }

            fis = new FileInputStream(file);
            int len = 0;
            while ((len = fis.read(buffer, 0, buffer.length)) >= 0) {
                os.write(buffer);
            }

            long end = System.currentTimeMillis();
            L.i("传输耗时" + (end - start));
            showToast("发送完成：" + (end - start));
            os.flush();

        } catch (Exception e) {
            L.e("e: " + e);
            showToast("" + e.getMessage());
        } finally {
            //关闭流
            try {
                os.close();
                fis.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void showToast(final String s) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                T.show(mContext, s);
            }
        });
    }

}
