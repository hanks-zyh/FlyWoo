package com.zjk.wifiproject.config;

/**
 * Intent相关常量
 * Created by Administrator on 2015/5/4.
 */
public class ConfigIntent {

    /**
     * 展示创建,加入按钮
     */
    public static final int REQUEST_SHOW_CREATE = 0x01;

    /**
     * 展示正在创建
     */
    public static final int REQUEST_SHOW_CREATING = 0x02;

    /**
     * 模糊后图片的存储地址
     */
    public static final String EXTRA_BLUR_PATH = "blur_path";


    public static final String EXTRA_SENDER_IP = "sender_ip";

    public static final String EXTRA_CHAT_USER = "chat_user";

    public static final String EXTRA_NEW_MSG_CONTENT = "new_msg_content";
    public static final String EXTRA_NEW_MSG_TYPE = "new_msg_type";
    public static final int NEW_MSG_TYPE_TXT   = 0x401;
    public static final int NEW_MSG_TYPE_IMAGE = 0x402;
    public static final int NEW_MSG_TYPE_VOICE = 0x403;
    public static final int NEW_MSG_TYPE_FILE  = 0x404;


    /**
     * 获取图片
     */
    public static final int REQUEST_PICK_IMAGE = 0x000300;

    /**
     * 获取文件
     */
    public static final int REQUEST_PICK_FILE = 0x000301;
}