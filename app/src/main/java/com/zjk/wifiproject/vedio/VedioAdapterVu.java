package com.zjk.wifiproject.vedio;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.view.TouchCheckBox;

import java.util.HashMap;
import java.util.Map;

public class VedioAdapterVu implements Vu {

    private View      view;
    private ImageView mVedio;
    private TextView  mDuration;
    private TextView  mVedioSize;
    private TextView  mSelect;
    private TextView  mVedioName;
    private Context   context;
    private Map<String, Bitmap> vedioThrmuils = new HashMap<>();
    private TouchCheckBox mIsSelect;


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
        mIsSelect = (TouchCheckBox) view.findViewById(R.id.isSelect);
        mVedioName = (TextView) view.findViewById(R.id.vedioName);
    }

    public void setChecked(boolean isSelect) {
        mIsSelect.setChecked(isSelect);
    }

    @Override
    public View getView() {
        return view;
    }

    /**
     * 设置视频缩略图
     *
     * @param path
     * @version 1.0
     */
    public void setVedioThrmuil(String path) {

        try {
            Bitmap bm = vedioThrmuils.get(path);
            if (bm == null) {
                bm = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
    //            L.d("缩略图：" + bm.getWidth() + "，" + bm.getHeight());
                vedioThrmuils.put(path,bm);
            }
            mVedio.setImageBitmap(bm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        try {

            Bitmap bm = getVideoThumbnail(path);
            L.d("缩略图："+bm.getWidth() +"，"+bm.getHeight());
            ByteArrayOutputStream baos = null ;
            try{
                baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 85, out);
                float zoom = (float)Math.sqrt(size * 1024 / (float)out.toByteArray().length);

                Matrix matrix = new Matrix();
                matrix.setScale(zoom, zoom);

                Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

                out.reset();
                result.compress(Bitmap.CompressFormat.JPEG, 85, out);
                while(out.toByteArray().length > size * 1024){
                    System.out.println(out.toByteArray().length);
                    matrix.setScale(0.9f, 0.9f);
                    result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
                    out.reset();
                    result.compress(Bitmap.CompressFormat.JPEG, 85, out);
                }
                mVedio.setImageBitmap(result);

            }finally{
                try {
                    if(baos != null)
                        baos.close() ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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


    public void setOnCheckedChangeListener (TouchCheckBox.OnCheckedChangeListener listener){
        mIsSelect.setOnCheckedChangeListener(listener);
    }



    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(0, 0);
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
