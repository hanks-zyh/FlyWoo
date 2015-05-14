package com.zjk.wifiproject;

import android.app.Application;
import android.app.Service;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zjk.wifiproject.entity.FileState;
import com.zjk.wifiproject.util.FileUtils;
import com.zjk.wifiproject.util.L;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseApplication extends Application {

    public static boolean isDebugmode = true;
    private boolean isPrintLog = true;

    /** 静音、震动默认开关 **/
    private static boolean isSlient = false;
    private static boolean isVIBRATE = true;

    /** 新消息提醒 **/
    private static int notiSoundPoolID;
    private static SoundPool notiMediaplayer;
    private static Vibrator notiVibrator;

    /** 缓存 **/
    private Map<String, SoftReference<Bitmap>> mAvatarCache;

    public static HashMap<String, FileState> sendFileStates;
    public static HashMap<String, FileState> recieveFileStates;

    /** 本地图像、缩略图、声音、文件存储路径 **/
    public static String IMAG_PATH;
    public static String THUMBNAIL_PATH;
    public static String VOICE_PATH;
    public static String VEDIO_PATH;
    public static String APK_PATH;
    public static String MUSIC_PATH;
    public static String FILE_PATH;
    public static String SAVE_PATH;
    public static String CAMERA_IMAGE_PATH;

    /** mEmoticons 表情 **/
    public static Map<String, Integer> mEmoticonsId;
    public static List<String> mEmoticons;
    public static List<String> mEmoticons_Zem;


    private static BaseApplication instance;




    private static  boolean isClient = true;

    /**
     * <p>
     * 获取BaseApplication实例
     * <p>
     * 单例模式，返回唯一实例
     * 
     * @return instance
     */
    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
        }
        sendFileStates = new HashMap<String, FileState>();
        recieveFileStates = new HashMap<String, FileState>();
        mAvatarCache = new HashMap<String, SoftReference<Bitmap>>();
        // ActivitiesManager.init(getApplicationContext()); // 初始化活动管理器
        // L.setLogStatus(isPrintLog); // 设置是否显示日志

        //初始化Fresco库
        Fresco.initialize(this);

        initEmoticons();
        initNotification();
        initFolder();
    }

    private void initEmoticons() {
        mEmoticonsId = new HashMap<String, Integer>();
        mEmoticons = new ArrayList<String>();
        mEmoticons_Zem = new ArrayList<String>();

        // 预载表情
        for (int i = 1; i < 64; i++) {
            String emoticonsName = "[zem" + i + "]";
            int emoticonsId = getResources().getIdentifier("zem" + i, "drawable", getPackageName());
            mEmoticons.add(emoticonsName);
            mEmoticons_Zem.add(emoticonsName);
            mEmoticonsId.put(emoticonsName, emoticonsId);
        }
    }

    private void initNotification() {
        notiMediaplayer = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
        // notiSoundPoolID = notiMediaplayer.load(this, R.raw.crystalring, 1);
        notiVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        L.e("BaseApplication", "onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        L.e("BaseApplication", "onTerminate");
    }

    // 函数创建文件存储目录
    private void initFolder() {
        if (null == IMAG_PATH) {
            SAVE_PATH = FileUtils.getSDPath();// 获取SD卡的根目录路径,如果不存在就返回Null
            if (null == SAVE_PATH) {
                SAVE_PATH = instance.getFilesDir().toString();// 获取内置存储区目录
            }
            SAVE_PATH += File.separator + "WifiProject";
            IMAG_PATH = SAVE_PATH + File.separator + "image";
            THUMBNAIL_PATH = SAVE_PATH + File.separator + "thumbnail";
            VOICE_PATH = SAVE_PATH + File.separator + "voice";
            FILE_PATH = SAVE_PATH + File.separator + "file";
            VEDIO_PATH = SAVE_PATH + File.separator + "vedio";
            APK_PATH = SAVE_PATH + File.separator + "apk";
            MUSIC_PATH = SAVE_PATH + File.separator + "music";
            CAMERA_IMAGE_PATH = IMAG_PATH + File.separator;
            if (!FileUtils.isFileExists(IMAG_PATH))
                FileUtils.createDirFile(BaseApplication.IMAG_PATH);
            if (!FileUtils.isFileExists(THUMBNAIL_PATH))
                FileUtils.createDirFile(BaseApplication.THUMBNAIL_PATH);
            if (!FileUtils.isFileExists(VOICE_PATH))
                FileUtils.createDirFile(BaseApplication.VOICE_PATH);
            if (!FileUtils.isFileExists(VEDIO_PATH))
                FileUtils.createDirFile(BaseApplication.VEDIO_PATH);
            if (!FileUtils.isFileExists(APK_PATH))
                FileUtils.createDirFile(BaseApplication.APK_PATH);
            if (!FileUtils.isFileExists(MUSIC_PATH))
                FileUtils.createDirFile(BaseApplication.MUSIC_PATH);
            if (!FileUtils.isFileExists(FILE_PATH))
                FileUtils.createDirFile(BaseApplication.FILE_PATH);

        }
    }


    /* 设置声音提醒 */
    public static boolean getSoundFlag() {
        return !isSlient;
    }

    public static void setSoundFlag(boolean pIsSlient) {
        isSlient = pIsSlient;
    }

    /* 设置震动提醒 */
    public static boolean getVibrateFlag() {
        return isVIBRATE;
    }

    public static void setVibrateFlag(boolean pIsvibrate) {
        isVIBRATE = pIsvibrate;
    }

    /**
     * 新消息提醒 - 声音提醒、振动提醒
     */
    public static void playNotification() {
        if (!isSlient) {
            notiMediaplayer.play(notiSoundPoolID, 1, 1, 0, 0, 1);
        }
        if (isVIBRATE) {
            notiVibrator.vibrate(200);
        }

    }

    public boolean isClient() {
            return isClient;
    }
    public void setIsClient(boolean isClient) {
            this.isClient = isClient;
    }
}
