package com.zjk.wifiproject.picture;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.view.TouchCheckBox;

public class PictureFolderAdapterVu implements Vu {

    private View view;

    private SimpleDraweeView     mImage;
    private TextView      mPictureCount;
    private TouchCheckBox mSelect;
    private TextView      mFolderName;

    private Context context;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
        view = inflater.inflate(R.layout.item_picture_folder, container, false);
        bindViews();
    }

    private void bindViews() {
        mImage = (SimpleDraweeView) view.findViewById(R.id.image);
        mPictureCount = (TextView) view.findViewById(R.id.pictureCount);
        mSelect = (TouchCheckBox) view.findViewById(R.id.select);
        mFolderName = (TextView) view.findViewById(R.id.folderName);
    }

    public void setChecked(boolean isSelect) {
        mSelect.setChecked(isSelect);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setImage(String path) {
//        Picasso.with(context).load(new File(path)).centerCrop().resize(200, 200).into(mImage);
        mImage.setImageURI(Uri.parse("file://"+path));
    }

    public void setOnCheckedChangeListener (TouchCheckBox.OnCheckedChangeListener listener){
        mSelect.setOnCheckedChangeListener(listener);
    }

    public void setFolderName(String name) {
        mFolderName.setText(name);
    }

    public void setPictureConunt(int count) {
        mPictureCount.setText("(" + count + ")");
    }

}
