package com.zjk.wifiproject.entity;

import com.zjk.wifiproject.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
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
    protected int childernSize ;


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

    public void setChildernSize(int i) {
        this.childernSize = i;
    }

    public int getChildernSize() {
        return childernSize;
    }
}
