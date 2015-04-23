package com.zjk.wifiproject.vedio;

import com.zjk.wifiproject.entity.WFile;

public class VedioEntity extends WFile {

    private int id;
    private String data;
    private String displayName;
    private long size;
    private long duration;

    @Override
    public String toString() {
        return "VedioEntity [id=" + id + ", data=" + data + ", displayName=" + displayName + ", size=" + size
                + ", duration=" + duration + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
