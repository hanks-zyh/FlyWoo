package com.zjk.wifiproject.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2015/5/7.
 */
public abstract  class BaseViewHolder extends RecyclerView.ViewHolder{
    
    public BaseViewHolder(View itemView) {
        super(itemView);
        bindViews(itemView);
    }

    protected abstract void bindViews(View itemView);

    public abstract void update(int position);
}
