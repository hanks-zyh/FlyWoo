package com.zjk.wifiproject.entity;

import java.io.File;
import java.util.List;

/**
 * @fileName WFile.java
 * @package szu.wifichat.android.entity
 * @description 实体基类
 */
public class WFile {
    // 本来想做些什么来,暂时空着吧
    protected String fileName;
    protected String filePath;
    protected long fileSize;
    protected boolean isDirectory;
    protected List<WFile> childern;

    public List<WFile> getChildern() {
        return childern;
    }

    public void setChildern(List<WFile> childern) {
        this.childern = childern;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }
}
