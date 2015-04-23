package com.zjk.wifiproject.music;

import java.util.List;

import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.util.FileUtils;

public class MusicFragment extends BasePresenterFragment<MusicVu> {

    @Override
    protected void onBindVu() {

        List<MusicEntity> list = FileUtils.getMusicList(context);
        vu.setDate(list);
    }

    @Override
    protected Class<MusicVu> getVuClass() {
        return MusicVu.class;
    }

}
