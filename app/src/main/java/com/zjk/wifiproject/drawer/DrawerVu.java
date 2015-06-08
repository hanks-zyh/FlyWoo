package com.zjk.wifiproject.drawer;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.entity.ChatEntity;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.sql.ChatDao;
import com.zjk.wifiproject.util.AlertDialogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DrawerVu implements Vu {

    private View         view;
    private RecyclerView recyclerView;
    private List<ChatEntity> list = new ArrayList<>();
    private HistoryAdapter adapter;
    private TextView       deviceName;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        //填充布局
        view = inflater.inflate(R.layout.vu_drawer, container, false);
        bindViews();
    }

    public void setData(List<ChatEntity> list) {
        this.list = list;
        //历史记录的list
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new HistoryAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    private void bindViews() {
        deviceName = (TextView) view.findViewById(R.id.deviceName);
    }

    public void setDeviceName(String phoneModel) {
        deviceName.setText(phoneModel);
    }

    @Override
    public View getView() {
        return view;
    }


    public void onResume(Context context) {
       /* List<ChatEntity> tmp = new ChatDao(context).getAllChat();
        if (tmp != null && tmp.size() > 0) {
            list.clear();
            list.addAll(tmp);
            adapter.notifyDataSetChanged();
        }*/
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

        public HistoryAdapter(List<ChatEntity> list) {
        }

        @Override
        public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_chat_history, null);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HistoryViewHolder holder, final int position) {
            holder.update(position);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    AlertDialogUtils.show(v.getContext(), "删除记录", "确定要删除记录吗？", "确定", "取消", new AlertDialogUtils.OkCallBack() {
                        @Override
                        public void onOkClick(DialogInterface dialog, int which) {
                            new ChatDao(v.getContext()).deleteChat(list.get(position));
                            list.remove(position);
                            notifyDataSetChanged();
                        }
                    }, null);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView chatIcon;
        TextView textView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            chatIcon = (ImageView) itemView.findViewById(R.id.chatTypeIcon);
        }

        public void update(int position) {
            File file = new File(list.get(position).getContent());
            Message.CONTENT_TYPE type = list.get(position).getType();

            if (file.exists()) {
                textView.setText(file.getName());
            } else {
                textView.setText(list.get(position).getContent());
            }

            if (type == Message.CONTENT_TYPE.MUSIC) {
                chatIcon.setImageResource(R.drawable.item_music);
            } else if (type == Message.CONTENT_TYPE.VEDIO) {
                chatIcon.setImageResource(R.drawable.item_vedio);
            } else if (type == Message.CONTENT_TYPE.APK) {
                chatIcon.setImageResource(R.drawable.item_apk);
            } else if(type == Message.CONTENT_TYPE.IMAGE) {
                chatIcon.setImageResource(R.drawable.item_picture);
            }
            else {
                chatIcon.setImageResource(R.drawable.item_file);
            }
        }
    }
}

