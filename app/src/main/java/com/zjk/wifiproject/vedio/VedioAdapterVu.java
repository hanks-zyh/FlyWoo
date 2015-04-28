package com.zjk.wifiproject.vedio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.util.L;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class VedioAdapterVu implements Vu {

    private View view;
    private ImageView mVedio;
    private TextView mDuration;
    private TextView mVedioSize;
    private TextView mSelect;
    private TextView mVedioName;
    private Context context;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
        view = inflater.inflate(R.layout.item_vedio, container, false);
        bindViews();
    }

    private void bindViews() {
        mVedio = (ImageView) view.findViewById(R.id.vedio);
        mDuration = (TextView) view.findViewById(R.id.duration);
        mVedioSize = (TextView) view.findViewById(R.id.vedioSize);
        mSelect = (TextView) view.findViewById(R.id.select);
        mVedioName = (TextView) view.findViewById(R.id.vedioName);
    }

    @Override
    public View getView() {
        return view;
    }

    /**
     * 设置视频缩略图
     * 
     * @version 1.0
     * @param path
     */
    public void setVedioThrmuil(String path) {
        try {
            Bitmap bm = getVideoThumbnail(path);
            L.d("缩略图："+bm.getWidth() +"，"+bm.getHeight());
            ByteArrayOutputStream baos = null ;
            try{
                baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 3, baos);

            }finally{
                try {
                    if(baos != null)
                        baos.close() ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mVedio.setImageBitmap(bm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVedioName(String name) {
        mVedioName.setText(name);
    }

    public void setVedioSize(long size) {
        mVedioSize.setText(Formatter.formatFileSize(context, size));
    }

    public void setVedioDuration(long duration) {
        mDuration.setText(formatTime((int) (duration / 1000)));
    }

    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(0,0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 把int格式化时间
     */
    public String formatTime(int time) {
        // 245995
        // time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }
}
