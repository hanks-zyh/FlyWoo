package com.zjk.wifiproject.socket.tcp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.zjk.wifiproject.util.FileUtils;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.T;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2015/5/8.
 */
public class TcpReceiveThread implements Runnable{

    private final Context mContext;



    public TcpReceiveThread(Context context, Handler mHandler) {
        mContext = context;
    }

    @Override
    public void run() {
        try {

            // 创建监听端口
            ServerSocket servsock = new ServerSocket(TcpClient.TCP_SERVER_RECEIVE_PORT);

            while (true) {

                showToast("监听:" + TcpClient.TCP_SERVER_RECEIVE_PORT);

                Socket sock = servsock.accept(); // 阻塞线程，直到监听建立了连接

                showToast("建立连接");
                L.i("Accepted connection : " + sock);


                //获取文件流
                InputStream is = sock.getInputStream();

                //目标文件位置
                File myFile = new File(FileUtils.getSDPath() + "/22.jpg");


                if (myFile.exists()) {
                    showToast("文件22已存在,删除");
                    myFile.delete();
                }

                FileOutputStream fos = new FileOutputStream(myFile);
                byte[] mybytearray = new byte[TcpClient.READ_BUFFER_SIZE];
                int len = 0;

                while((len = is.read(mybytearray))!=-1){
                    L.e("接收字节："+len);
                    fos.write(mybytearray,0,len);
                }


                fos.flush();
                sock.close();
                fos.close();
                is.close();

                showToast("文件接收完成：" + myFile.length() + ",路径：" + myFile.getAbsolutePath());

            }
        } catch (Exception e) {
            L.i("e: " + e);
            showToast("接收文件异常");
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
