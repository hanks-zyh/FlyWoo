package com.zjk.wifiproject.picture;

import java.io.File;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;

public class PictureFolderAdapterVu implements Vu {

    private View view;

    private ImageView mImage;
    private TextView mPictureCount;
    private TextView mSelect;
    private TextView mFolderName;

    private Context context;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
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
        Picasso.with(context).load(new File(path)).resize(100, 100).centerCrop().into(mImage);
    }

    public void setFolderName(String name) {
        mFolderName.setText(name);
    }

    public void setPictureConunt(int count) {
        mPictureCount.setText("(" + count + ")");
    }

}
