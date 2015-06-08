package com.zjk.wifiproject.drawer;

import android.os.Build;

import com.zjk.wifiproject.entity.ChatEntity;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.presenters.BasePresenterFragment;
import com.zjk.wifiproject.sql.ChatDao;

import java.util.ArrayList;
import java.util.List;

public class DrawerFragment extends BasePresenterFragment<DrawerVu> {

    @Override
    protected Class<DrawerVu> getVuClass() {
        return DrawerVu.class;
    }

    @Override
    protected void onBindVu() {
        vu.setDeviceName(getPhoneModel());
        List<ChatEntity> list = new ChatDao(context).getAllChat();
        List<ChatEntity> listTmp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getType() == Message.CONTENT_TYPE.TEXT)
                continue;;
            listTmp.add(list.get(i));
        }
        vu.setData(listTmp);
    }

    /**
     * 每次重新看到界面时会调用
     */
    @Override
    public void onResume() {
        super.onResume();
        List<ChatEntity> list = new ChatDao(context).getAllChat();
        List<ChatEntity> listTmp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getType() == Message.CONTENT_TYPE.TEXT)
                continue;;
            listTmp.add(list.get(i));
        }
        vu.setData(listTmp);
        vu.onResume(context);
    }

    //得到手机型号
    public String getPhoneModel() {
        String str1 = Build.BRAND;
        String str2 = Build.MODEL;
        str2 = str1 + "_" + str2;
        return str2;
    }

}
