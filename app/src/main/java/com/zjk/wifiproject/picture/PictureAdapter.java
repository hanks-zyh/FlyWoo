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
package com.zjk.wifiproject.picture;

import android.content.Context;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.entity.Message;
import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.file.OnFileCheckListener;
import com.zjk.wifiproject.view.TouchCheckBox;

import java.util.List;

/**
 * Created by Administrator on 2015/5/15.
 */
public class PictureAdapter extends BaseAdapter {
    private final List<PictureEntity> list;
    private final Context context;

    public PictureAdapter(Context context, List<PictureEntity> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_picture_folder, null);
        }

        SimpleDraweeView image = (SimpleDraweeView) convertView.findViewById(R.id.image);
        TextView pictureCount = (TextView) convertView.findViewById(R.id.pictureCount);
        TextView folderName = (TextView) convertView.findViewById(R.id.folderName);
        TouchCheckBox select = (TouchCheckBox) convertView.findViewById(R.id.select);

        image.setImageURI(Uri.parse("file://" + list.get(position).getAbsolutePath()));
        folderName.setText(list.get(position).getName());
        pictureCount.setText(Formatter.formatFileSize(context,list.get(position).length()));
        WFile wfile = new WFile(list.get(position).getAbsolutePath());
        Message.CONTENT_TYPE type = Message.CONTENT_TYPE.IMAGE;
        select.setOnCheckedChangeListener(new OnFileCheckListener(wfile,type));
        return convertView;
    }
}
