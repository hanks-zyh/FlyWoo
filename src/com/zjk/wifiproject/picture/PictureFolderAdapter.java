package com.zjk.wifiproject.picture;

import java.util.List;

import android.content.Context;

import com.zjk.wifiproject.presenters.BasePresenterAdapter;

public class PictureFolderAdapter extends BasePresenterAdapter<PictureFolderEntity, PictureFolderAdapterVu> {

    public PictureFolderAdapter(Context context, List<PictureFolderEntity> list) {
        super(context, list);
    }

    @Override
    protected void onBindItemVu(int position) {
        PictureFolderEntity item = list.get(position);
        vu.setFolderName(item.getName());
        vu.setPictureConunt(item.images.size());
        vu.setImage(item.getFirstImagePath());
    }

    @Override
    protected Class<PictureFolderAdapterVu> getVuClass() {
        return PictureFolderAdapterVu.class;
    }

}
