package com.zjk.wifiproject.picture;

import java.util.ArrayList;
import java.util.List;

import com.zjk.wifiproject.entity.Entity;

public class PictureFolderEntity extends Entity {
    /**
     * 图片的文件夹路径
     */
    private String dir;

    /**
     * 第一张图片的路径
     */
    private String firstImagePath;

    /**
     * 文件夹的名称
     */
    private String name;

    public List<PictureEntity> images = new ArrayList<PictureEntity>();

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.name = this.dir.substring(lastIndexOf);
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getName() {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        return name;
    }
}
