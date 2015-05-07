package com.zjk.wifiproject.chat;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 录音管理类 负责录音和存储
 * 
 * @version 1.0
 * @author zyh
 */
public class ZRecordManager {
    // 单例模式,私有化构造器
    private ZRecordManager() {
    }

    public static int MAX_RECORD_TIME = 60;
    public static int MIN_RECORD_TIME = 1;
    private static final String VOICE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/voice";

    private MediaRecorder recoder;
    private File file;
    private long startTime;
    private String recordFileName;
    private String recordFilePath;
    private static volatile ZRecordManager sInstance;
    private static Object INSTANCE_LOCK = new Object();
    OnRecordChangeListener listener;
    /**
     * 使用 AtomicBoolean 高效并发处理 “只初始化一次” 的功能要求：
     */
    private AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    /**
     * 线程池
     */
    private ExecutorService executor;

    final Handler handler = new Handler(new MyCallBack());

    /**
     * 使用单例模式创建--双重锁定
     * 
     * @version 1.0
     * @author zyh
     * @param context
     * @return
     */
    public static ZRecordManager getInstance(Context context) {
        if (sInstance == null)
            synchronized (INSTANCE_LOCK) {
                if (sInstance == null)
                    sInstance = new ZRecordManager();
                sInstance.init(context);
            }
        return sInstance;
    }

    /**
     * 初始化
     * 
     * @version 1.0
     * @author zyh
     * @param paramContext
     */
    public void init(Context paramContext) {
        /*
         * -缓存型池子，先查看池中有没有以前建立的线程，如果有，就reuse.如果没有，就建一个新的线程加入池中
         * -缓存型池子通常用于执行一些生存期很短的异步型任务 因此在一些面向连接的daemon型SERVER中用得不多。
         * -能reuse的线程，必须是timeout
         * IDLE内的池中线程，缺省timeout是60s,超过这个IDLE时长，线程实例将被终止及移出池。
         * 注意，放入CachedThreadPool的线程不必担心其结束，超过TIMEOUT不活动，其会自动被终止。
         */
        executor = Executors.newCachedThreadPool();
    }

    /**
     * 设置录音监听器
     * 
     * @version 1.0
     * @author zyh
     * @param onRecordChangeListener
     */
    public void setOnRecordChangeListener(OnRecordChangeListener onRecordChangeListener) {
        listener = onRecordChangeListener;
    }

    /**
     * 开始录音
     * 
     * @version 1.0
     * @author zyh
     * @param chatId
     *            会话Id
     */
    public void startRecording(String chatId) {
        if (recoder == null) {
            recoder = new MediaRecorder();
            recoder.setAudioSource(1);
            recoder.setOutputFormat(3);
            recoder.setAudioEncoder(1);
            recoder.setAudioChannels(1);
            recoder.setAudioEncodingBitRate(12200);
            recoder.setOnErrorListener(new RecorderErrorListener());
        } else {
            recoder.stop();
            recoder.reset();
        }
        recordFileName = System.currentTimeMillis() + ".amr";
        recordFilePath = getRecordFilePath(chatId);
        file = new File(recordFilePath);
        recoder.setOutputFile(file.getAbsolutePath());
        try {
            recoder.prepare();
            recoder.start();
            atomicBoolean.set(true);
            startTime = new Date().getTime();
            executor.execute(new CalculateRunnable(this));
            return;
        } catch (IllegalStateException localIllegalStateException) {
            Log.i("voice", "IllegalStateException thrown while trying to record a greeting");
            atomicBoolean.set(false);
            recoder.release();
            recoder = null;
            return;
        } catch (IOException localIOException) {
            Log.i("voice", "IOException thrown while trying to record a greeting");
            atomicBoolean.set(false);
            recoder.release();
            recoder = null;
        }
    }

    public void cancelRecording() {
        if (recoder == null)
            return;
        recoder.stop();
        recoder.release();
        recoder = null;
        if ((file != null) && (file.exists()) && (!file.isDirectory()))
            file.delete();
        atomicBoolean.set(false);
    }

    /**
     * 停止录音
     * 
     * @version 1.0
     * @author zyh
     * @return 录音时长
     */
    public int stopRecording() {
        if (recoder != null) {
            atomicBoolean.set(false);
            recoder.stop();
            recoder.release();
            recoder = null;
            return (int) (new Date().getTime() - startTime) / 1000;
        }
        return 0;
    }

    public boolean isRecording() {
        return atomicBoolean.get();
    }

    public MediaRecorder getMediaRecorder() {
        return recoder;
    }

    /**
     * 
     * @version 1.0
     * @author zyh
     * @param chatId
     * @return
     */
    public String getRecordFilePath(String chatId) {
        File path = new File(VOICE_DIR);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(VOICE_DIR + File.separator + getRecordFileName());
        try {
            if (!path.exists())
                path.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path.getAbsolutePath();
    }

    public String getRecordFileName() {
        return recordFileName;
    }

    public interface OnRecordChangeListener {
        public void onVolumnChanged(int value);

        public void onTimeChanged(int recordTime, String localPath);
    }

    /**
     * 录音出错监听
     * 
     * @version 1.0
     * @author zyh
     */
    class RecorderErrorListener implements MediaRecorder.OnErrorListener {

        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            String msg = "";
            switch (what) {
                case 1:
                    msg = "MEDIA_RECORDER_ERROR_UNKNOWN";
                    break;
                default:
                    msg = Integer.toString(what);
            }
            Log.i("voice",
                    String.format("MediaRecorder error occured: %s,%d",
                            new Object[] { msg, Integer.valueOf(extra) }));
        }
    }

    /**
     * 音量大小转换,录音时长
     * 
     * @version 1.0
     * @author zyh
     */
    final class CalculateRunnable implements Runnable {
        ZRecordManager recordManager;

        public CalculateRunnable(ZRecordManager recordManager) {
            this.recordManager = recordManager;
        }

        public final void run() {
            for (int i = 0; atomicBoolean.get(); i++) {
                int j = recoder.getMaxAmplitude();
                if ((j = j * 5 / 32768) > 5)
                    j = 5;
                Message localMessage = new Message();
                localMessage.arg1 = j;
                localMessage.arg2 = i;
                localMessage.what = 10;
                recordManager.handler.sendMessage(localMessage);
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException localInterruptedException) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    class MyCallBack implements Handler.Callback {
        public boolean handleMessage(Message msg) {
            if (msg.what == 10) {
                int i = msg.arg1;
                int j = msg.arg2;
                Log.i("zyh", i + "-------" + (j / 10));
                if (listener != null) {
                    listener.onVolumnChanged(i);
                    if (j % 10 == 0)
                        listener.onTimeChanged(j / 10, ZRecordManager.VOICE_DIR);
                }
                return true;
            }
            return false;
        }
    }
}
