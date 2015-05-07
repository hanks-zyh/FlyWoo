package com.zjk.wifiproject.chat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.entity.ChatEntity;
import com.zjk.wifiproject.presenters.Vu;

import java.util.List;

/**
 * Created by Administrator on 2015/5/6.
 */
public class ChatVu implements Vu{

    private View view;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChatAdapter mAdapter;
    private Context context;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
        view = inflater.inflate(R.layout.vu_chat, container, false);
        bindViews();
        init();
    }

    private void init() {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);


    }

    private void bindViews() {
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recylerView);
    }


    public void setDate(List<ChatEntity> list){
        Logger.d(""+list.size());
        mAdapter = new ChatAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View getView() {
        return view;
    }
}
