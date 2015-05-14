/*
 * Created by Hanks
 * Copyright (c) 2015 NaShangBan. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zjk.wifiproject.vedio;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.BaseApplication;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.base.BaseActivity;
import com.zjk.wifiproject.util.FileUtils;
import com.zjk.wifiproject.util.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/13.
 */
public class ApkSelectActivity extends BaseActivity {

    private ListView mListView;


    private VedioAdapter adapter;
    private List<VedioEntity> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_select);
        BaseApplication.sendFileStates.clear();
        bindViews();
        initListView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDatas();
            }
        }, 500);
    }

    private void bindViews() {
        mListView = (ListView) findViewById(R.id.listView);
    }

    private void initListView() {
        adapter = new VedioAdapter(context, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new FileItemClickListener());
    }

    private void getDatas() {
        list.addAll(FileUtils.getVedioList(context));
        adapter.notifyDataSetChanged();
    }


    private class FileItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    public void ok(View view) {
        setResult(RESULT_OK);
        Logger.i("选中了" + BaseApplication.sendFileStates.keySet().size() + "个文件");
        T.show(context, "选中了" + BaseApplication.sendFileStates.keySet().size() + "个文件");
        onBackPressed();
    }

    public void back(View view) {
        onBackPressed();
    }

}
