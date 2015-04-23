package com.zjk.wifiproject.file;

import android.content.Context;

import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.presenters.BasePresenterAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/4/23.
 */
public class FileAdapter extends BasePresenterAdapter<WFile,FileAdapterVu>{

    public FileAdapter(Context context, List<WFile> list) {
        super(context, list);
    }

    @Override
    protected void onBindItemVu(int position) {
        WFile item = list.get(position);
        vu.setFileIcon(item);
        vu.setFileName(item.getFileName());
        vu.setFileSize(item.getFileSize());
    }

    @Override
    protected Class<FileAdapterVu> getVuClass() {
        return FileAdapterVu.class;
    }
}
