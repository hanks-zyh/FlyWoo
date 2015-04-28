package com.zjk.wifiproject.app;

import android.graphics.drawable.Drawable;

import com.zjk.wifiproject.entity.WFile;

/**
 * APK文件
 */
public class AppEntity extends WFile{

    /**
     * APK包名
     */
    private String packageName;
    /**
     * APK版本
     */
    private String versionName;
    /**
     * APK版本号
     */
    private int versionCode;
    /**
     * APK uid
     */
    private int uid;
    /**
     * 缓存数据大小
     */
    private long cacheSize;
    /**
     * 应用数据大小
     */
    private long dataSize;
    /**
     * 图标
     */
    private Drawable icon;
    private boolean checked;
    private boolean visible;
    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public AppEntity(String path) {
        super(path);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
