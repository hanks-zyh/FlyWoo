package com.zjk.wifiproject.picture;

import java.util.List;

import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.util.FileUtils;

public class PictureFragment extends BasePresenterFragment<PictureFolderVu> {

    private List<PictureFolderEntity> list;

    @Override
    protected void onBindVu() {
        list = FileUtils.getPictureFolderList(context);
        vu.setDate(list);
    }

    @Override
    protected Class<PictureFolderVu> getVuClass() {
        return PictureFolderVu.class;
    }

}
