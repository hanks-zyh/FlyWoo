package com.zjk.wifiproject.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.ScanResult;
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
import com.zjk.wifiproject.util.PixelUtil;
import com.zjk.wifiproject.util.T;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2015/5/5.
 */
public class SearchView extends RelativeLayout {
    private ImageView bgView, scanView;
    private Context context;
    private List<Point> margins;
    private OnAvatarClickListener listener;

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
        initArray();
        this.context = context;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        bgView = new ImageView(context);
        bgView.setImageResource(R.drawable.search_radar);
        bgView.setLayoutParams(params);

        scanView = new ImageView(context);
        scanView.setImageResource(R.drawable.search_radar_green);
        scanView.setLayoutParams(params);

        addView(bgView);
        addView(scanView);

        scanView.setVisibility(GONE);
    }

    public void setOnAvatarClickListener(OnAvatarClickListener listener) {
        //调用者的listener
        this.listener = listener;
    }

    /**
     * 初始化点的位置
     */
    private void initArray() {
        int w = PixelUtil.dp2px(60);
        int h = PixelUtil.dp2px(84);

        margins = new ArrayList<>();
        margins.add(new Point(0, 0));
        margins.add(new Point(0, h));
        margins.add(new Point(0, h * 2));
        margins.add(new Point(w, 0));
        margins.add(new Point(w, h));
        margins.add(new Point(w, h * 2));
        margins.add(new Point(w * 2, 0));
        margins.add(new Point(w * 2, h));
        margins.add(new Point(w * 2, h * 2));
        margins.add(new Point(w * 3, 0));
        margins.add(new Point(w * 3, h));
        margins.add(new Point(w * 3, h * 2));
    }

    /**
     * 扫描动画
     *
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

    /**
     * 添加热点头像
     * @param wifi
     */
    public void addApView(ScanResult wifi) {
        if (margins.size() == 0) {
            T.show(context, "达到最大数目");
            return;
        }
        //隐藏扫描
        scanView.setVisibility(GONE);

        //创建view
        View view = View.inflate(context, R.layout.layout_new_ap, null);
        String name = wifi.SSID.substring(6);
        ((TextView) view.findViewById(R.id.apName)).setText(name);
        view.setTag(wifi);//将头像对应的wifi附加到上面（绑定），这样可以由头像得到wifi
        addView(view);

        //生成随机位置 marginLeft，marginTop
        Random random = new Random();
        Point p = margins.get(random.nextInt(margins.size()));
        RelativeLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.setMargins(p.x, p.y, 0, 0); //设置上随机位置
        view.setLayoutParams(params);
        margins.remove(p); //移除p

        view.setScaleX(0.5f);
        view.setScaleY(0.5f);
        view.animate().scaleX(1f).scaleY(1f).setDuration(400).setInterpolator(new OvershootInterpolator()).start();

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用者的listener，（在setOnAvatarClickListener里已经初始化）
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
    }

    /**
     * 判断当前点是否可以添加
     *
     * @param p
     * @return
     */
    private boolean canPlace(Point p, int viewWidth, int viewHeight) {
        for (Point m : margins) {
            if (Math.abs(m.x - p.x) < viewWidth && Math.abs(m.y - p.y) < viewHeight) {
                return false;
            }
        }
        margins.add(p);
        return true;
    }

    public void clearApView() {
        int count = getChildCount();
        for (int i = 2; i < count; i++) {
            View child = getChildAt(i);
            removeView(child);
        }
    }

    /**
     * 只剩下一个头像
     * @param v
     */
    public void clearApViewButOne(View v) {
        int count = getChildCount();
        for (int i = 2; i < count; i++) {
            View child = getChildAt(i);
            if(!v.equals(child)) {
                removeView(child);
            }
        }
    }

    public void hideBackground() {
        bgView.animate().alpha(0).setDuration(300).start();
    }

    public void setTextColor(View view,int color) {
        ((TextView) view.findViewById(R.id.apName)).setTextColor(color);
        ((TextView) view.findViewById(R.id.apName)).setTextSize(14);
    }


    public interface OnAvatarClickListener {
        public void onClick(View v);
    }
}
