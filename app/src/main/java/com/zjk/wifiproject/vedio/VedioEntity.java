package com.zjk.wifiproject.vedio;

import com.zjk.wifiproject.entity.WFile;

public class VedioEntity extends WFile {

    private int id;
    private String displayName;
    private long duration;

    public VedioEntity(String path) {
        super(path);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
