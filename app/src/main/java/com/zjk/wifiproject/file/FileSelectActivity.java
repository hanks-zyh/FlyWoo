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
package com.zjk.wifiproject.file;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.BaseApplication;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.base.BaseActivity;
import com.zjk.wifiproject.entity.FileState;
import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.util.AlertDialogUtils;
import com.zjk.wifiproject.util.FileUtils;
import com.zjk.wifiproject.util.T;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/13.
 */
public class FileSelectActivity extends BaseActivity{

    private TextView mPath;
    private ImageButton mUp;
    private ListView mListView;


    private FileAdapter adapter;
    private List<WFile> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        bindViews();
        initListView();
        String currentPath  = Environment.getExternalStorageDirectory().getAbsolutePath();
        setPath(currentPath);
    }

    private void bindViews() {
        mPath = (TextView)  findViewById(R.id.path);
        mUp = (ImageButton)  findViewById(R.id.up);
        mListView = (ListView) findViewById(R.id.listView);
    }

    private void initListView() {
        adapter  = new FileAdapter(context,list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new FileItemClickListener());
        mListView.setOnItemLongClickListener(new SendDirctoryListener());
        mUp.setOnClickListener(new UpButtonClickListener());
    }

    private void setPath(String currentPath) {
        mPath.setText(currentPath);
        if(adapter!=null){
            adapter.setData(FileUtils.getCurrentFileList(currentPath));
        }
    }


    private class FileItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            WFile item = list.get(position);
            if(item.isDirectory()) {
                String path = item.getAbsolutePath();
                Logger.i(path);
                setPath(path);
            }
        }
    }

    private class UpButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                File currentDir = new File(mPath.getText().toString());
                Logger.i(currentDir.getParent());
                setPath(currentDir.getParent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ok(View view){
        setResult(RESULT_OK);
        Logger.i("选中了"+BaseApplication.sendFileStates.keySet().size()+"个文件");
        T.show(context, "选中了" + BaseApplication.sendFileStates.keySet().size() + "个文件");
        onBackPressed();
    }

    private class SendDirctoryListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialogUtils.show(context,"传输","确定传输该文件夹所以内容吗？","确定","取消", new AlertDialogUtils.OkCallBack() {
                @Override
                public void onOkClick(DialogInterface dialog, int which) {

                    WFile item = list.get(position);
                    Logger.i("传输文件夹："+ item.getAbsolutePath());
                    FileState fs = new FileState(item.getAbsolutePath());
                    BaseApplication.sendFileStates.put(item.getAbsolutePath(),fs);
                    onBackPressed();
                }
            },null);
            return true;
        }
    }
}
