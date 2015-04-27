package com.zjk.wifiproject.socket;

/**
 * 相当服务器，用于监听消息，接受文件
 * Created by Administrator on 2015/4/26.
 */
public class SocketServer {


    private MessageListenThread listenThread;

    public static void setInstance(SocketServer instance) {
        SocketServer.instance = instance;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    private boolean isRunning = false;


    //单例模式
    private SocketServer(){}

    private static SocketServer instance;

    public synchronized SocketServer getInstance(){
        if(instance==null){
            instance = new SocketServer();
        }
        return instance;
    }


    private void startListen(){
        setIsRunning(true);
        listenThread = new MessageListenThread();
        listenThread.start();
    }

    private void stopListen(){
        setIsRunning(false);
    }


    /**
     * 监听消息的线程
     */
    class MessageListenThread extends  Thread{
        @Override
        public void run() {
            while (true){

                //控制退出
                if(!isRunning()){
                    break;
                }


            }
        }
    }
}
