package com.zjk.wifiproject.file;

import android.os.Environment;

import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.util.FileUtils;

import java.util.List;

public class FileFragment extends BasePresenterFragment<FileVu> {

    @Override
    protected void onBindVu() {
        String currentPath = Environment.getRootDirectory().getAbsolutePath();
        List<WFile> list     = FileUtils.getCurrentFileList (currentPath);
        vu.setData(list);
        vu.setCurrentPath(currentPath);
    }

    @Override
    protected Class<FileVu> getVuClass() {
        return FileVu.class;
    }

}
