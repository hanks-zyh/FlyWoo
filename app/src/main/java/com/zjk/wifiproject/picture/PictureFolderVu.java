package com.zjk.wifiproject.picture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.bus.BusProvider;
import com.zjk.wifiproject.presenters.Vu;

import java.util.List;

public class PictureFolderVu implements Vu {

    private View view;
    private Context context;
    private ListView mListView;
    public PictureFolderAdapter adapter;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_picture, container, false);
        context = inflater.getContext();
        bindView();
    }

    private void bindView() {
        mListView = (ListView) view.findViewById(R.id.listView);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setDate(final List<PictureFolderEntity> list) {
        adapter = new PictureFolderAdapter(context, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.i("setOnItemClickListener:"+position);
                BusProvider.getInstance().post(new ShowImageListEvent(list.get(position).images));
            }
        });
    }

    public void onResume() {
        BusProvider.getInstance().register(this);
    }


    public void onPause() {
        BusProvider.getInstance().unregister(this);
    }


    public void setListDate(List<PictureEntity> list) {
        Logger.i("setListDate:");
        mListView.setAdapter(new PictureAdapter(context, list));
    }
}
