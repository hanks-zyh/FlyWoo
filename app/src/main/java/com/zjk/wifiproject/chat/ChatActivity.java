package com.zjk.wifiproject.chat;

import com.zjk.wifiproject.entity.ChatEntity;
import com.zjk.wifiproject.presenters.BasePresenterActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/6.
 */
public class ChatActivity extends BasePresenterActivity<ChatVu>{
    @Override
    protected void onBindVu() {
        List<ChatEntity> list = new ArrayList<>();
        ChatEntity msg = new ChatEntity("哈哈哈");
        msg.setType(ChatEntityType.STRING);
        msg.setIsSend(true);
        list.add(msg);
        vu.setDate(list);
    }

    @Override
    protected Class<ChatVu> getVuClass() {
        return ChatVu.class;
    }
}
