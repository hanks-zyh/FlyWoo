package com.zjk.wifiproject.presenters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BasePresenterAdapter<E, V extends Vu> extends BaseAdapter {
    protected List<E> list;
    protected Context context;

    public BasePresenterAdapter(Context context, List<E> list) {
        this.context = context;
        this.list = list;
    }

    protected V vu;

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            try {
                vu = getVuClass().newInstance();
                vu.init(inflater, parent);
                convertView = vu.getView();
                convertView.setTag(vu);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            vu = (V) convertView.getTag();
        }
        if (convertView != null) {
            onBindItemVu(position);
        }
        return convertView;
    }

    protected abstract void onBindItemVu(int position);

    protected abstract Class<V> getVuClass();

    @Override
    public int getCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }
}
