package com.zjk.wifiproject.music;

import com.zjk.wifiproject.entity.WFile;

public class MusicEntity extends WFile {

    private String title;// 音乐名
    private long duration; // 音乐的总时间
    private String artist; // 艺术家
    private int id; // id号
    private String displayName;// 音乐文件名
    private String data;// 音乐文件的路径

    @Override
    public String toString() {
        return "MusicEntity [title=" + title + ", duration=" + duration + ", artist=" + artist + ", id=" + id
                + ", displayName=" + displayName + ", data=" + data + "]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
