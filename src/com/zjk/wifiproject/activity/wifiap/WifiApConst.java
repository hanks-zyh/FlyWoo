package com.zjk.wifiproject.activity.wifiap;

/**
 * WifiAp常量
 * 
 * @author Administrator
 *
 */
public class WifiApConst {
    // Wifi状态 粗略
    public static final int CLOSE = 0x001;
    public static final int SEARCH = 0x002;
    public static final int CREATE = 0x003;
    public static final int NOTHING = 0x004;

    // Wifi状态 详细
    public static final int WiFiConnectError = 0; // Wifi连接错误
    public static final int ApScanResult = 1; // 搜索到wifi返回结果
    public static final int WiFiConnectSuccess = 2; // 连接上wifi热点
    public static final int ApCreateApSuccess = 3; // 创建热点成功
    public static final int ApUserChanged = 4; // 用户上线人数更新命令(待定)
    public static final int NetworkChanged = 5; // 连接或断开wifi
    public static final int ApConnectting = 6; // 连接热点中

    // WifiAP 参数
    public static final String PACKAGE_NAME = "com.zjk.wifiproject.activity";
    public static final String FIRST_OPEN_KEY = "version";
    public static final String WIFI_AP_HEADER = "ZChat_";
    public static final String WIFI_AP_PASSWORD = "wifichat123";
}
