package com.zjk.wifiproject.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.main.MainActivity;
import com.zjk.wifiproject.main.SendFileListener;
import com.zjk.wifiproject.presenters.Vu;

import java.util.List;

public class AppVu implements Vu {

    private View view;
    private GridView gridView;
    public AppGridAdapter adapter;

    private List<AppEntity> list;
    private TextView tv_local;
    private Context context;
    private SendFileListener sendFileListener;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
        view = inflater.inflate(R.layout.vu_app, container, false);
        sendFileListener = ((MainActivity)context).vu;
        bindViews();
    }

    private void bindViews() {
        gridView = (GridView) view.findViewById(R.id.gridview);
        tv_local = (TextView) view.findViewById(R.id.tv_local);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setOnItemClickListener() {
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppItemVu vu = (AppItemVu) view.getTag();
                boolean isChecked = vu.toggleChecked();
                if(isChecked){
                    //添加一个文件
                    sendFileListener.addSendFile(list.get(position));
                }else{
                    //移除一个文件
                    sendFileListener.removeSendFile(list.get(position));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void setData(Context context, List<AppEntity> list) {
        this.context = context;
        this.list = list;
        tv_local.setText("本地应用（" + list.size() + "）");
        this.adapter = new AppGridAdapter(this, context, list);
        gridView.setAdapter(adapter);
    }
}
