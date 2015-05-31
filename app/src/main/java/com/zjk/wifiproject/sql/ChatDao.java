/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package com.zjk.wifiproject.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.zjk.wifiproject.entity.ChatEntity;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天历史记录
 * Created by Hanks on 2015/5/31.
 */
public class ChatDao {

    private static final String T_NAME = "t_chat";          //数据表名

    private static final String COLUME_ID      = "id";            //id
    private static final String COLUME_CONTENT = "content";       //文件路径
    private static final String COLUME_TIME    = "time";          //时间
    private static final String COLUME_TYPE    = "type";          //类型
    private static final String COLUME_ISSEND  = "isSend";        //接收或者发送的

    private String[] columns = new String[] {
            COLUME_ID, COLUME_CONTENT, COLUME_TIME, COLUME_TYPE, COLUME_ISSEND
    };

    // Database fields
    private SQLiteDatabase database;
    private DBHelper       dbHelper;


    public ChatDao(Context context) {
        dbHelper = new DBHelper(context.getApplicationContext());
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * 创建成功，返回记录的ID
     *
     * @return
     */
    public long create(ChatEntity chatEntity) {
        open();
        ContentValues values = new ContentValues();
        values.put(COLUME_CONTENT, chatEntity.getContent());
        values.put(COLUME_TIME, chatEntity.getTime());
        int type = enumToInt(chatEntity.getType());
        values.put(COLUME_TYPE, type);
        values.put(COLUME_ISSEND, chatEntity.isSend());
        long id = database.insert(T_NAME, null, values);
        close();
        return id;
    }

    private int enumToInt(Message.CONTENT_TYPE type) {
        int result = 0;
        switch (type) {
            case MUSIC:
                result = 1;
                break;
            case VEDIO:
                result = 2;
                break;
            case APK:
                result = 3;
                break;
            case IMAGE:
                result = 4;
                break;
            case VOICE:
                result = 5;
                break;
            case TEXT:
                result = 6;
                break;
        }
        return result;
    }

    private Message.CONTENT_TYPE intToEnum(int type) {
        Message.CONTENT_TYPE result = Message.CONTENT_TYPE.FILE;
        switch (type) {
            case 1:
                result = Message.CONTENT_TYPE.MUSIC;
                break;
            case 2:
                result = Message.CONTENT_TYPE.VEDIO;
                break;
            case 3:
                result = Message.CONTENT_TYPE.APK;
                break;
            case 4:
                result = Message.CONTENT_TYPE.IMAGE;
                break;
            case 5:
                result = Message.CONTENT_TYPE.VOICE;
                break;
            case 6:
                result = Message.CONTENT_TYPE.TEXT;
                break;
        }
        return result;
    }


    /**
     * 删除
     *
     * @return 是否删除成功
     */
    public boolean deleteChat(ChatEntity chatEntity) {
        open();
        long id = chatEntity.getId();
        int affectedRows = database.delete(T_NAME, COLUME_ID + " = ?", new String[] { id + "" });
        close();
        return affectedRows > 0;
    }

    /**
     * 获取所有task
     *
     * @return
     */
    public List<ChatEntity> getAllChat() {
        open();
        List<ChatEntity> taskList = new ArrayList<>();
        Cursor cursor = database.query(T_NAME,
                columns, null, null, null, null, COLUME_TIME);
        while (cursor.moveToNext()) {
            ChatEntity task = cursorToEntity(cursor);
            taskList.add(task);
        }
        // make sure to close the cursor
        cursor.close();
        close();
        L.i("查询到本地的任务个数：" + taskList.size());
        return taskList;
    }

    private ChatEntity cursorToEntity(Cursor cursor) {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setId(cursor.getInt(0));
        chatEntity.setContent(cursor.getString(1));
        chatEntity.setTime(cursor.getLong(2));
        chatEntity.setType(intToEnum(cursor.getInt(3)));
        chatEntity.setIsSend(cursor.getInt(4) > 0);
        return chatEntity;
    }


    /**
     * 获取单个Task
     *
     * @param id
     * @return
     */
    public ChatEntity getTask(int id) {
        open();
        ChatEntity chatEntity = null;
        String sql = "SELECT * FROM " + T_NAME + " WHERE id = (?)";
        Cursor cursor = database.rawQuery(sql, new String[] { id + "" });
        if (cursor.moveToNext()) {
            chatEntity = cursorToEntity(cursor);
        }
        cursor.close();
        close();
        return chatEntity;
    }


    /**
     * 删除所有
     */
    public void deleteAll() {
        open();
        database.delete(T_NAME, null, null);
        close();
    }

    /**
     * 保存多个
     *
     * @param chatEntities
     */
    public void saveAll(List<ChatEntity> chatEntities) {
        for (ChatEntity entity : chatEntities) {
            create(entity);
        }
    }
}
