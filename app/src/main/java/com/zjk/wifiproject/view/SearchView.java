package com.zjk.wifiproject.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjk.wifiproject.R;

/**
 * Created by Administrator on 2015/5/5.
 */
public class SearchView extends RelativeLayout {
    private ImageView bgView, scanView;
    private Context context;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        this.context = context;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        bgView = new ImageView(context);
        bgView.setImageResource(R.drawable.search_radar);
        bgView.setLayoutParams(params);

        scanView = new ImageView(context);
        scanView.setImageResource(R.drawable.search_radar_green);
        scanView.setLayoutParams(params);
        scanView.setVisibility(GONE);

        addView(bgView);
        addView(scanView);

    }

    /**
     * 扫描动画
     * @param delay
     */
    public void startSearchAnim(long delay) {
        scanView.setVisibility(VISIBLE);
        ObjectAnimator oa = ObjectAnimator.ofFloat(scanView, "Rotation", 0, 360);
        oa.setDuration(2000).setRepeatCount(Animation.INFINITE);
        oa.setInterpolator(new LinearInterpolator());
        oa.setStartDelay(delay);
        oa.start();
    }


    public void addApView(String name){
        View view = View.inflate(context, R.layout.layout_new_ap, null);
        ((TextView)view.findViewById(R.id.apName)).setText(name);
        scanView.setVisibility(GONE);
        addView(view);
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(400).setInterpolator(new OvershootInterpolator()).start();
    }

    public void clearApView(){
        int count = getChildCount();
        for(int i=2;i<count;i++){
            View child =getChildAt(i);
            removeView(child);
        }
    }
}
