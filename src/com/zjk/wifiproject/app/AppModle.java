package com.zjk.wifiproject.app;

import android.graphics.drawable.Drawable;

public class AppModle {

    /**
     * 应用的apk文件路径
     */
    private String apkPath;
    /**
     * 应用包名
     */
    private String packageName;
    /**
     * 应用名称
     */
    private String name;
    /**
     * 应用版本名
     */
    private String versionName;
    /**
     * 应用版本号
     */
    private int versionCode;
    /**
     * 应用uid
     */
    private int uid;
    /**
     * 应用apk安装包大小
     */
    private long apkSize;
    /**
     * 应用缓存大小
     */
    private long cacheSize;
    /**
     * 应用大小
     */
    private long dataSize;
    /**
     * 应用图标
     */
    private Drawable icon;

    private boolean checked;
    private boolean visible;

    @Override
    public String toString() {
        return "AppInfo [apkPath=" + apkPath + ", packageName=" + packageName + ", name=" + name
                + ", versionName=" + versionName + ", versionCode=" + versionCode + ", uid=" + uid
                + ", apkSize=" + apkSize + ", cacheSize=" + cacheSize + ", dataSize=" + dataSize + ", icon="
                + icon + "]";
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
