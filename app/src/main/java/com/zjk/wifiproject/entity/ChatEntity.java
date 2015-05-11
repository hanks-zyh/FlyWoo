package com.zjk.wifiproject.entity;

/**
 * 聊天的实体类
 * Created by Administrator on 2015/5/7.
 */
public class ChatEntity {

    public ChatEntity(String content) {
        this.content = content;
    }

    private Message.CONTENT_TYPE type;
    private String content;
    private long time;
    private WFile file;
    /**
     * 是否是自己发出的
     */
    private boolean isSend;

    public Message.CONTENT_TYPE getType() {
        return type;
    }

    public void setType(Message.CONTENT_TYPE type) {
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

    public boolean isSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }
}
