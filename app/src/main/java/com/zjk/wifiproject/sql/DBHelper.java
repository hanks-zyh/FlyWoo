package com.zjk.wifiproject.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int    VERSION = 1;                 //数据库版本
    private static final String DBNAME  = "WifiProject.db";  //数据库名
    private static final String T_NAME  = "t_chat";          //数据表名

    private static final String COLUME_ID      = "id";            //id
    private static final String COLUME_CONTENT = "content";       //文件路径
    private static final String COLUME_TIME    = "time";          //时间
    private static final String COLUME_TYPE    = "type";          //类型
    private static final String COLUME_ISSEND  = "isSend";        //接收或者发送的

    private static final String TEXT_TYPE    = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP    = ",";

    /**
     * 获取数据库中操作表名字
     */
    public String getTableName() {
        return T_NAME;
    }

    /**
     * 构造函数
     * 参数为版本，数据库名
     */
    public DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    /**
     * T_NAME为创建数据表的名字
     * 该函数初始化类的时候会调用，T_NAME为表名,如果表不存在则创建，如果存在则打开
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table " + T_NAME +
                "(" +
                COLUME_ID  + " INTEGER PRIMARY KEY," +
                COLUME_CONTENT + TEXT_TYPE + COMMA_SEP +
                COLUME_TIME + INTEGER_TYPE + COMMA_SEP +
                COLUME_TYPE + INTEGER_TYPE + COMMA_SEP +
                COLUME_ISSEND + INTEGER_TYPE +
                ")";
        db.execSQL(sql);
    }

    /**
     * 该函数当检测到版本数增加时候会自动调用
     * 一般用来数据库表格的改动或者扩展
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
