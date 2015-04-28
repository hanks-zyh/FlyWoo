package com.zjk.wifiproject.app;

import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.connection.CreateConnectionActivity;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.util.A;

public class AppVu implements Vu {

    private View view;
    private GridView gridView;
    private AppGridAdapter adapter;
    private View layout_bottom;
    private List<AppEntity> selectedList = new ArrayList<AppEntity>();
    private List<AppEntity> list;
    private TextView tv_local;
    private TextView tv_select_size;
    private boolean showAnim = false;
    private Context context;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_app, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        tv_local = (TextView) view.findViewById(R.id.tv_local);
        tv_select_size = (TextView) view.findViewById(R.id.tv_select_size);
        layout_bottom = view.findViewById(R.id.layout_bottom);
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
                showAnim = false;
                boolean isChecked = vu.toggleChecked();
                if (isChecked) {
                    getSelectedList().add(list.get(position));
                    if (getSelectedList().size() == 1)
                        showAnim = true;
                } else {
                    getSelectedList().remove(list.get(position));
                }
                if (getSelectedList().size() > 0) {
                    showBottomLayout();
                } else {
                    hideBottomLayout();
                }
            }
        });
    }

    public void setData(Context context, List<AppEntity> list) {
        this.context = context;
        this.list = list;
        tv_local.setText("本地应用（" + list.size() + "）");
        this.adapter = new AppGridAdapter(this, context, list);
        gridView.setAdapter(adapter);
        setListener();
    }

    private void setListener() {
        view.findViewById(R.id.ib_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBottomLayout();
            }
        });
        tv_select_size.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                A.goOtherActivity(context, CreateConnectionActivity.class);
            }
        });
    }

    private void showBottomLayout() {
        if (showAnim) {
            // 防止出现缝隙
            final int height = layout_bottom.getHeight() - 2;
            ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(300);
            va.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    layout_bottom.setTranslationY(-height * value);
                }
            });
            va.start();
        }
        tv_select_size.setText("传输（" + getSelectedList().size() + "）");
        layout_bottom.setVisibility(View.VISIBLE);
    }

    private void hideBottomLayout() {
        // 防止出现缝隙
        final int height = layout_bottom.getHeight() - 2;
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(300);
        va.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                layout_bottom.setTranslationY(height * value - height);
            }
        });
        va.start();
        getSelectedList().clear();
        adapter.notifyDataSetChanged();
    }

    public List<AppEntity> getSelectedList() {
        return selectedList;
    }

}
