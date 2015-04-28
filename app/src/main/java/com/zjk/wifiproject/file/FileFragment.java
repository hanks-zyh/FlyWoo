package com.zjk.wifiproject.file;

import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.util.FileUtils;
import com.zjk.wifiproject.util.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFragment extends BasePresenterFragment<FileVu> {

    private ArrayList<WFile> list = new ArrayList<>();

    @Override
    protected void onBindVu() {
        vu.setData(list);
        String currentPath  = Environment.getExternalStorageDirectory().getAbsolutePath();
        vu.setCurrentPath(currentPath);
        Logger.i(currentPath);
        vu.setOnItemClickListener(new FileItemClickListener());
        vu.setUpClickListener(new UpButtonClickListener());
    }

    @Override
    protected Class<FileVu> getVuClass() {
        return FileVu.class;
    }


    private class FileItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            WFile item = list.get(position);
            if(item.isDirectory()) {
                String path = item.getAbsolutePath();
                Logger.i(path);
                vu.setCurrentPath(path);
            }
        }
    }

    private class UpButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            try {
                File currentDir = new File(vu.getCurrentPath());
                Logger.i(currentDir.getParent());
                vu.setCurrentPath(currentDir.getParent());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
