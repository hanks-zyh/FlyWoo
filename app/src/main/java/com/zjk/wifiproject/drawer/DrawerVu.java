package com.zjk.wifiproject.drawer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.entity.ChatEntity;
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

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_drawer, container, false);
        TextView deviceName = (TextView) view.findViewById(R.id.deviceName);
        deviceName.setText(getPhoneModel());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        adapter = new HistoryAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View getView() {
        return view;
    }


    public String getPhoneModel() {
        String str1 = Build.BRAND;
        String str2 = Build.MODEL;
        str2 = str1 + "_" + str2;
        return str2;
    }

    public void onResume(Context context) {
        List<ChatEntity> tmp = new ChatDao(context).getAllChat();
        if (tmp != null && tmp.size() > 0) {
            list.clear();
            list.addAll(tmp);
            adapter.notifyDataSetChanged();
        }
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
        TextView textView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        public void update(int position) {
            File file = new File(list.get(position).getContent());
            if (file.exists()) {
                textView.setText(file.getName());
            } else {
                textView.setText(list.get(position).getContent());
            }
        }
    }
}
