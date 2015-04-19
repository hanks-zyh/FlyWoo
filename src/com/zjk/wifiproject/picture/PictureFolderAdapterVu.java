package com.zjk.wifiproject.picture;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

public class PictureFolderAdapterVu implements Vu {

    private View view;

    private ImageView mImage;
    private TextView mPictureCount;
    private TextView mSelect;
    private TextView mFolderName;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.item_picture_folder, container, false);
        bindViews();
    }

    private void bindViews() {
        mImage = (ImageView) view.findViewById(R.id.image);
        mPictureCount = (TextView) view.findViewById(R.id.pictureCount);
        mSelect = (TextView) view.findViewById(R.id.select);
        mFolderName = (TextView) view.findViewById(R.id.folderName);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setImage(String path) {
        mImage.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    public void setFolderName(String name) {
        mFolderName.setText(name);
    }

    public void setPictureConunt(int count) {
        mPictureCount.setText("(" + count + ")");
    }

}
