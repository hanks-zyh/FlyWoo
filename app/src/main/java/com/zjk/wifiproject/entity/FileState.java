package com.zjk.wifiproject.entity;

/**
 * Created by Administrator on 2015/4/23.
 */
public class FileState {
    public long fileSize = 0;
    public long currentSize = 0;
    public String fileName = null;
    public int percent = 0;
    public Message.CONTENT_TYPE type = Message.CONTENT_TYPE.TEXT;

    public FileState() {
    }

    public FileState(String fileFullPath) {
        this.fileName = fileFullPath;
    }

    public FileState(String fileFullPath, Message.CONTENT_TYPE type) {
        this(fileFullPath);
        this.type = type;
    }

    public FileState(long fileSize, long currentSize, String fileName) {
        this.fileSize = fileSize;
        this.currentSize = currentSize;
        this.fileName = fileName;
    }

    public FileState(long fileSize, long currentSize, String fileName, Message.CONTENT_TYPE type) {
        this(fileSize, currentSize, fileName);
        this.type = type;
    }
}
