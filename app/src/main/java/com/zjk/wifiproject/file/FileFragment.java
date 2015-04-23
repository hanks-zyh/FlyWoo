package com.zjk.wifiproject.file;

import com.zjk.wifiproject.presenters.BasePresenterFragment;

public class FileFragment extends BasePresenterFragment<FileVu> {

    @Override
    protected void onBindVu() {

//        list = FileUtils.getCurrentFileList();
//        vu.setData(list);
    }

    @Override
    protected Class<FileVu> getVuClass() {
        return FileVu.class;
    }

}
