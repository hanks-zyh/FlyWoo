package com.zjk.wifiproject.chat;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.entity.ChatEntity;
import com.zjk.wifiproject.util.DateUtils;
import com.zjk.wifiproject.view.emoj.EmoticonsTextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 聊天的adapter
 * Created by Administrator on 2015/5/7.
 */
public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    // 8种Item的类型
    // 文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    // 图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    // 音乐
    private final int TYPE_SEND_MUSIC = 4;
    private final int TYPE_RECEIVER_MUSIC = 5;
    // 语音
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;
    //视频
    private final int TYPE_SEND_VEDIO = 8;
    private final int TYPE_RECEIVER_VEDIO = 9;
    //APK
    private final int TYPE_SEND_APK = 10;
    private final int TYPE_RECEIVER_APK = 11;
    //文件
    private final int TYPE_SEND_FILE = 12;
    private final int TYPE_RECEIVER_FILE = 13;


    private List<ChatEntity> list;
    private MediaPlayer mMediaPlayer;
    private boolean isPlay = false;
    private AnimationDrawable anim;

    ChatAdapter(List<ChatEntity> list) {
        this.list = list;
    }

    /**
     * 根据type创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getViewHolder(parent, viewType);
    }

    /**
     * 创建对应的ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    private BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        BaseViewHolder vh = null;
        switch (viewType) {
            //文本
            case TYPE_SEND_TXT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_sent_message, parent, false);
                vh = new TextViewHolder(v);
                break;

            case TYPE_RECEIVER_TXT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_received_message, parent, false);
                vh = new TextViewHolder(v);
                break;
            //图片
            case TYPE_SEND_IMAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_sent_image, parent, false);
                vh = new ImageViewHolder(v);
                break;
            case TYPE_RECEIVER_IMAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_received_image, parent, false);
                vh = new ImageViewHolder(v);
                break;

            //语音
            case TYPE_SEND_VOICE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_sent_voice, parent, false);
                vh = new VoiceViewHolder(v);
                break;
            case TYPE_RECEIVER_VOICE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_received_voice, parent, false);
                vh = new VoiceViewHolder(v);
                break;

            //文件
            case TYPE_SEND_FILE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_send_file, parent, false);
                vh = new FileViewHolder(v);
                break;
            case TYPE_RECEIVER_FILE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_received_file, parent, false);
                vh = new FileViewHolder(v);
                break;

            //视频
            case TYPE_SEND_VEDIO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_send_file, parent, false);
                vh = new FileViewHolder(v);
                break;
            case TYPE_RECEIVER_VEDIO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_received_file, parent, false);
                vh = new FileViewHolder(v);
                break;

            //音乐
            case TYPE_SEND_MUSIC:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_send_file, parent, false);
                vh = new FileViewHolder(v);
                break;
            case TYPE_RECEIVER_MUSIC:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_received_file, parent, false);
                vh = new FileViewHolder(v);
                break;
            //APK
            case TYPE_SEND_APK:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_send_file, parent, false);
                vh = new FileViewHolder(v);
                break;
            case TYPE_RECEIVER_APK:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_received_file, parent, false);
                vh = new FileViewHolder(v);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.update(position);
    }

    /**
     * 返回的item的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        ChatEntity msg = list.get(position);
        int type = TYPE_SEND_TXT;
        switch (msg.getType()) {
            case TEXT:
                type = msg.isSend() ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
                break;
            case IMAGE:
                type = msg.isSend() ? TYPE_SEND_IMAGE : TYPE_RECEIVER_IMAGE;
                break;
            case VOICE:
                type = msg.isSend() ? TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
                break;
            case VEDIO:
                type = msg.isSend() ? TYPE_SEND_VEDIO : TYPE_RECEIVER_VEDIO;
                break;
            case MUSIC:
                type = msg.isSend() ? TYPE_SEND_MUSIC : TYPE_RECEIVER_MUSIC;
                break;
            case APK:
                type = msg.isSend() ? TYPE_SEND_APK : TYPE_RECEIVER_APK;
                break;
            case FILE:
                type = msg.isSend() ? TYPE_SEND_FILE : TYPE_RECEIVER_FILE;
                break;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    /**
     * 文本消息的ViewHolder
     */
    public class TextViewHolder extends BaseViewHolder {

        public EmoticonsTextView tv_message;
        private TextView tv_time;

        public TextViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindViews(View itemView) {
            tv_message = (EmoticonsTextView) itemView.findViewById(R.id.tv_message);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }

        @Override
        public void update(int position) {
            ChatEntity item = list.get(position);
            tv_message.setText(item.getContent());
            tv_time.setText(DateUtils.formatDate(tv_time.getContext(),item.getTime()));
        }
    }
    /**
     * 图片的ViewHolder
     */
    private class ImageViewHolder extends BaseViewHolder {
        private SimpleDraweeView iv_picture;
        private TextView tv_time;

        public ImageViewHolder(View v) {
            super(v);
        }

        @Override
        protected void bindViews(View itemView) {
            iv_picture = (SimpleDraweeView) itemView.findViewById(R.id.iv_picture);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }

        @Override
        public void update(int position) {
            ChatEntity item = list.get(position);
            iv_picture.setImageURI(Uri.parse("file://" + item.getContent()));
            tv_time.setText(DateUtils.formatDate(tv_time.getContext(),item.getTime()));
        }
    }
    /**
     * 语音消息的ViewHolder
     */
    private class VoiceViewHolder extends BaseViewHolder {
        private TextView tv_time, tv_voice_length;
        private View view;

        public VoiceViewHolder(View v) {
            super(v);
            this.view = v;
        }

        @Override
        protected void bindViews(View itemView) {
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_voice_length = (TextView) itemView.findViewById(R.id.tv_voice_length);
        }

        @Override
        public void update(final int position) {
            ChatEntity item = list.get(position);
            tv_time.setText(DateUtils.formatDate(tv_time.getContext(),item.getTime()));
            tv_voice_length.setText(getVoiceLength(view, item.getContent()) + "s");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnClickVoice(view, position);
                }
            });

        }
    }

    private int getVoiceLength(View view,String path){
        MediaPlayer mp = MediaPlayer.create(view.getContext(), Uri.parse("file://"+path));
        int duration = mp.getDuration()/1000;
        mp.release();
        return duration;
    }

    /**
     * 播放录音
     *
     * @param view
     * @param position
     */
    private void OnClickVoice(View view, int position) {

        // 播放录音
        final ImageView iv_voice = (ImageView) view
                .findViewById(R.id.iv_voice);
        final ChatEntity item = list.get(position);
        if (!isPlay) {
            mMediaPlayer = new MediaPlayer();
            String filePath = item.getContent();
            try {
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                startRecordAnimation(item, iv_voice);
                isPlay = true;
                mMediaPlayer.start();
                // 设置播放结束时监听
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (isPlay) {
                            stopRecordAnimation(item, iv_voice);
                            isPlay = false;
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                isPlay = false;
            } else {
                isPlay = false;
                mMediaPlayer.release();
            }
            stopRecordAnimation(item, iv_voice);
        }

    }

    /**
     * 开启播放动画
     *
     * @param iv_voice @return void
     * @throws
     * @Title: startRecordAnimation
     */
    private void startRecordAnimation(ChatEntity item, ImageView iv_voice) {
        if (item.isSend()) {
            iv_voice.setImageResource(R.drawable.anim_chat_voice_right);
        } else {
            iv_voice.setImageResource(R.drawable.anim_chat_voice_left);
        }
        anim = (AnimationDrawable) iv_voice.getDrawable();
        anim.start();
    }

    /**
     * 停止播放动画
     *
     * @param
     * @param item
     * @param iv_voice @return void
     * @throws
     * @Title: stopRecordAnimation
     * @Description: TODO
     */
    private void stopRecordAnimation(ChatEntity item, ImageView iv_voice) {
        if (item.isSend()) {
            iv_voice.setImageResource(R.drawable.voice_left3);
        } else {
            iv_voice.setImageResource(R.drawable.voice_right3);
        }
        if (anim != null) {
            anim.stop();
        }
    }


    /**
     * 文件ViewHolder
     */
    private class FileViewHolder extends BaseViewHolder {
        private TextView tv_time,fileName,fileSize;
        private ProgressBar progress;
        private ImageView fileTpye;

        @Override
        protected void bindViews(View itemView) {
            fileTpye = (ImageView) itemView.findViewById(R.id.fileTpye);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
            fileSize = (TextView) itemView.findViewById(R.id.fileSize);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
        }

        @Override
        public void update(final int position) {
            ChatEntity item = list.get(position);
            tv_time.setText(DateUtils.formatDate(tv_time.getContext(),item.getTime()));
            progress.setProgress(item.getPercent());
            File f = new File(item.getContent());
            if(f.exists()) {
                fileSize.setText(Formatter.formatFileSize(fileSize.getContext(),f.length()));
                fileName.setText(f.getName());
            }else {
                Logger.e("文件不存在:"+item.getContent());
            }

            switch (item.getType()){
                case FILE:
                    fileTpye.setImageResource(R.drawable.item_file);
                    break;
                case MUSIC:
                    fileTpye.setImageResource(R.drawable.item_music);
                    break;
                case VEDIO:
                    fileTpye.setImageResource(R.drawable.item_vedio);
                    break;
                case APK:
                    fileTpye.setImageResource(R.drawable.item_apk);
                    break;
            }
        }

        public FileViewHolder(View v) {
            super(v);
        }
    }

    /**
     * 文件ViewHolder
     */
    private class VedioViewHolder extends BaseViewHolder {
        private TextView tv_time,fileName,fileSize;
        private ProgressBar progress;

        @Override
        protected void bindViews(View itemView) {
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
            fileSize = (TextView) itemView.findViewById(R.id.fileSize);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
        }

        @Override
        public void update(final int position) {
            ChatEntity item = list.get(position);
            tv_time.setText(DateUtils.formatDate(tv_time.getContext(),item.getTime()));
            progress.setProgress(item.getPercent());
            File f = new File(item.getContent());
            if(f.exists()) {
                fileSize.setText(Formatter.formatFileSize(fileSize.getContext(),f.length()));
                fileName.setText(f.getName());
            }else {
                Logger.e("文件不存在:"+item.getContent());
            }
        }
        public VedioViewHolder(View v) {
            super(v);
        }
    }    /**
     * 文件ViewHolder
     */
    private class MusicViewHolder extends BaseViewHolder {
        private TextView tv_time,fileName,fileSize;
        private ProgressBar progress;

        @Override
        protected void bindViews(View itemView) {
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
            fileSize = (TextView) itemView.findViewById(R.id.fileSize);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
        }

        @Override
        public void update(final int position) {
            ChatEntity item = list.get(position);
            tv_time.setText(DateUtils.formatDate(tv_time.getContext(),item.getTime()));
            progress.setProgress(item.getPercent());
            File f = new File(item.getContent());
            if(f.exists()) {
                fileSize.setText(Formatter.formatFileSize(fileSize.getContext(),f.length()));
                fileName.setText(f.getName());
            }else {
                Logger.e("文件不存在:"+item.getContent());
            }
        }
        public MusicViewHolder(View v) {
            super(v);
        }
    }    /**
     * 文件ViewHolder
     */
    private class ApkViewHolder extends BaseViewHolder {
        private TextView tv_time,fileName,fileSize;
        private ProgressBar progress;

        @Override
        protected void bindViews(View itemView) {
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
            fileSize = (TextView) itemView.findViewById(R.id.fileSize);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
        }

        @Override
        public void update(final int position) {
            ChatEntity item = list.get(position);
            tv_time.setText(DateUtils.formatDate(tv_time.getContext(),item.getTime()));
            progress.setProgress(item.getPercent());
            File f = new File(item.getContent());
            if(f.exists()) {
                fileSize.setText(Formatter.formatFileSize(fileSize.getContext(),f.length()));
                fileName.setText(f.getName());
            }else {
                Logger.e("文件不存在:"+item.getContent());
            }
        }
        public ApkViewHolder(View v) {
            super(v);
        }
    }
}
