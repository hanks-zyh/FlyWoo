package com.zjk.wifiproject.music;

import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.util.FileUtils;

import java.util.List;

public class MusicFragment extends BasePresenterFragment<MusicVu> {

    @Override
    protected void onBindVu() {

        List<MusicEntity> list = FileUtils.getMusicList(context);
        vu.setData(list);
    }

    @Override
    protected Class<MusicVu> getVuClass() {
        return MusicVu.class;
    }


}
