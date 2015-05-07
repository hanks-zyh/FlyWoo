package com.zjk.wifiproject.entity;

import com.zjk.wifiproject.chat.ChatEntityType;

/**
 * 聊天的实体类
 * Created by Administrator on 2015/5/7.
 */
public class ChatEntity{

    public ChatEntity(String content) {
        this.content = content;
    }

    private ChatEntityType type;
    private String content;
    private long  time;
    private WFile file;
    /**
     * 是否是自己发出的
     */
    private boolean isOut;

    public ChatEntityType getType() {
        return type;
    }

    public void setType(ChatEntityType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public WFile getFile() {
        return file;
    }

    public void setFile(WFile file) {
        this.file = file;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setIsOut(boolean isOut) {
        this.isOut = isOut;
    }
}
