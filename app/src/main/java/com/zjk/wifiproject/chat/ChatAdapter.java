package com.zjk.wifiproject.chat;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.entity.ChatEntity;
import com.zjk.wifiproject.util.DateUtils;

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
//            case MUSIC:
//                type = msg.isSend() ? TYPE_SEND_MUSIC : TYPE_RECEIVER_MUSIC;
//                break;
//            case VEDIO:
//                type = msg.isSend() ? TYPE_SEND_VEDIO : TYPE_RECEIVER_VEDIO;
//                break;
//            case APK:
//                type = msg.isSend() ? TYPE_SEND_APK : TYPE_RECEIVER_APK;
//                break;
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

    public class TextViewHolder extends BaseViewHolder {

        public TextView tv_message;
        private TextView tv_time;

        public TextViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindViews(View itemView) {
            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }

        @Override
        public void update(int position) {
            ChatEntity item = list.get(position);
            tv_message.setText(item.getContent());
            tv_time.setText(DateUtils.getBetweentime(item.getTime()));
        }
    }

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
            tv_time.setText(DateUtils.getBetweentime(item.getTime()));
        }
    }

    private class VoiceViewHolder extends BaseViewHolder {
        private TextView tv_time,tv_voice_length;
        public VoiceViewHolder(View v) {
            super(v);
        }

        @Override
        protected void bindViews(View itemView) {

            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_voice_length = (TextView) itemView.findViewById(R.id.tv_voice_length);
        }

        @Override
        public void update(int position) {
            ChatEntity item = list.get(position);
            tv_time.setText(DateUtils.getBetweentime(item.getTime()));
            tv_voice_length.setText("20s");
        }
    }
}
