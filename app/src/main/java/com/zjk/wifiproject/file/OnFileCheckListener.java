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

import android.content.Intent;
import android.widget.CompoundButton;

import com.zjk.wifiproject.BaseApplication;
import com.zjk.wifiproject.config.ConfigBroadcast;
import com.zjk.wifiproject.entity.FileState;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.main.MainActivity;

/**
 * Created by Administrator on 2015/5/14.
 */
public class OnFileCheckListener implements CompoundButton.OnCheckedChangeListener {

    private final WFile file;
    private Message.CONTENT_TYPE type;

    public OnFileCheckListener(WFile file,Message.CONTENT_TYPE type) {
        this.file = file;
        this.type = type;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            FileState fs = new FileState(file.getAbsolutePath());
            fs.type = type;
            BaseApplication.sendFileStates.put(file.getAbsolutePath(), fs);
        } else {
            BaseApplication.sendFileStates.remove(file.getAbsolutePath());
        }
        if(buttonView.getContext() instanceof MainActivity){
            buttonView.getContext().sendBroadcast(new Intent(ConfigBroadcast.ACTION_UPDATE_BOTTOM));
        }
    }
}