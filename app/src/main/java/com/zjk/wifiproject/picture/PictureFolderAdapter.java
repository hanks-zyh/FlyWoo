package com.zjk.wifiproject.picture;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zjk.wifiproject.BaseApplication;
import com.zjk.wifiproject.config.ConfigBroadcast;
import com.zjk.wifiproject.entity.FileState;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.main.MainActivity;
import com.zjk.wifiproject.presenters.BasePresenterAdapter;
import com.zjk.wifiproject.view.TouchCheckBox;

import java.util.List;

public class PictureFolderAdapter extends BasePresenterAdapter<PictureFolderEntity, PictureFolderAdapterVu> {

    public PictureFolderAdapter(Context context, List<PictureFolderEntity> list) {
        super(context, list);
    }

    @Override
    protected void onBindItemVu(int position) {
        final PictureFolderEntity item = list.get(position);
        vu.setFolderName(item.getName());
        vu.setPictureConunt(item.images.size());
        vu.setChecked(false);
        if(item.images.size()>0){
            vu.setImage(item.images.get(0).getAbsolutePath());
        }
        vu.setOnCheckedChangeListener(new TouchCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                if (isChecked) {

                    for(PictureEntity picture : item.images){
                        FileState fs = new FileState(picture.getAbsolutePath());
                        fs.type = Message.CONTENT_TYPE.IMAGE;
                        BaseApplication.sendFileStates.put(picture.getAbsolutePath(), fs);
                    }
                } else {
                    for(PictureEntity picture : item.images){
                        BaseApplication.sendFileStates.remove(picture.getAbsolutePath());
                    }
                }
                if(buttonView.getContext() instanceof MainActivity){
                    buttonView.getContext().sendBroadcast(new Intent(ConfigBroadcast.ACTION_UPDATE_BOTTOM));
                }
            }
        });
    }

    @Override
    protected Class<PictureFolderAdapterVu> getVuClass() {
        return PictureFolderAdapterVu.class;
    }


}
