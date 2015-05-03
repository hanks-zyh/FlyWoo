package com.zjk.wifiproject.main;

import com.zjk.wifiproject.entity.WFile;

/**
 * addsendFile和removeSendFile
 * Created by Administrator on 2015/5/3.
 */
public interface SendFileListener {
    public void addSendFile(WFile file);
    public void removeSendFile(WFile file);
}
