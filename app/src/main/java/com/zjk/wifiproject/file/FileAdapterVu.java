package com.zjk.wifiproject.file;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.presenters.Vu;

import java.io.File;

/**
 * Created by Administrator on 2015/4/23.
 */
public class FileAdapterVu implements Vu {

    private View view;
    private Context context;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
        view = inflater.inflate(R.layout.item_file, container, false);
        bindViews();
    }
    // Content View Elements

    private ImageView mFileIcon;
    private TextView mFileName;
    private TextView mTimeSize;
    private CheckBox mIsSelect;

    // End Of Content View Elements

    private void bindViews() {

        mFileIcon = (ImageView) view.findViewById(R.id.fileIcon);
        mFileName = (TextView) view.findViewById(R.id.fileName);
        mTimeSize = (TextView) view.findViewById(R.id.timeSize);
        mIsSelect = (CheckBox) view.findViewById(R.id.isSelect);
    }


    @Override
    public View getView() {
        return view;
    }

    public void setFileIcon(WFile file){
        if (file.isDirectory()){
            mFileIcon.setImageResource(R.drawable.ic_folder);
        }else{
            mFileIcon.setImageResource(R.drawable.ic_file);
        }
    }

    public void setFileName(String fileName){
        mFileName.setText(fileName);
    }

    public void setFileSize(WFile file){
        if(file.isDirectory()){
            mTimeSize.setText("("+(file.listFiles() !=null ? file.listFiles().length : 0 )+")");
        }else {
            mTimeSize.setText(Formatter.formatFileSize(context
                    , file.length()));
        }
    }
    public void setFileSelect(boolean isSelected){
        mIsSelect.setChecked(isSelected);
    }



}
