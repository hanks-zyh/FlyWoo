package com.zjk.wifiproject.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.util.FileUtils;

import java.util.List;

public class FileVu implements Vu {

    private View view;
    public FileAdapter adapter;
    private Context context;
    private List<WFile> list;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
        view = inflater.inflate(R.layout.vu_file, container, false);
        bindViews();
    }

    private TextView mPath;
    private ImageButton mUp;
    private ListView mListView;

    // End Of Content View Elements
    private void bindViews() {
        mPath = (TextView) view.findViewById(R.id.path);
        mUp = (ImageButton) view.findViewById(R.id.up);
        mListView = (ListView) view.findViewById(R.id.listView);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setData(List<WFile> list) {
        this.list = list;
        adapter  = new FileAdapter(context,list);
        mListView.setAdapter(adapter);
    }

    public void  setCurrentPath(String currentPath){
        mPath.setText(currentPath);
        if(adapter!=null){
            adapter.setData(FileUtils.getCurrentFileList(currentPath));
        }
    }
    public String getCurrentPath(){
        if(mPath!=null){
            return mPath.getText().toString().trim();
        }
        return null;
    }


    public  void setOnItemClickListener( AdapterView.OnItemClickListener listener){
        Logger.i("setOnItemClick");
        mListView.setOnItemClickListener(listener);
    }


    public void setUpClickListener(View.OnClickListener listener) {
        mUp.setOnClickListener(listener);
    }


}
