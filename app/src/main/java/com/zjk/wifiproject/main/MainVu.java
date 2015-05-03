package com.zjk.wifiproject.main;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjk.wifiproject.BaseApplication;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.app.AppFragment;
import com.zjk.wifiproject.connection.CreateConnectionActivity;
import com.zjk.wifiproject.entity.WFile;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.util.PixelUtil;
import com.zjk.wifiproject.view.tabs.SlidingTabLayout;

import java.util.List;

/**
 * 主界面的框架
 *
 * @author zyh
 * @version 1.0
 */
public class MainVu implements Vu, SendFileListener, View.OnClickListener {

    private View view;
    private FragmentManager fm;
    private ViewPager mViewPager;
    private SlidingTabLayout mTabs;
    private ImageView createButton;

    private View layout_bottom;//底部隐藏布局
    private TextView tv_select_size;//选中的数目

    //创建或者加入的布局
    private View layout_hide;
    private CheckBox mHelpCheckBox;
    private ImageView mHideButton;
    private ImageView mCreate;
    private ImageView mJoin;
    private ImageView mRightImage;
    private ImageView mLeftImage;


    private Context context;
    private BaseApplication application;
    private boolean showAnim = false;
    private List<Fragment> list;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        context = inflater.getContext();
        application = BaseApplication.getInstance();
        view = inflater.inflate(R.layout.vu_main, container, false);
        bindViews();
        setListener();
    }


    /**
     * findView操作
     */
    private void bindViews() {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        createButton = (ImageView) view.findViewById(R.id.createButton);
        tv_select_size = (TextView) view.findViewById(R.id.tv_select_size);


        layout_hide = view.findViewById(R.id.layout_create_or_join);
        layout_bottom = view.findViewById(R.id.layout_bottom);

        mHelpCheckBox = (CheckBox) view.findViewById(R.id.helpCheckBox);
        mHideButton = (ImageView) view.findViewById(R.id.hideButton);
        mCreate = (ImageView) view.findViewById(R.id.create);
        mJoin = (ImageView) view.findViewById(R.id.join);
        mLeftImage = (ImageView) view.findViewById(R.id.leftImage);
        mRightImage = (ImageView) view.findViewById(R.id.rightImage);
    }

    @Override
    public View getView() {
        return view;
    }

    private void setListener() {
        createButton.setOnClickListener(this);
        mHideButton.setOnClickListener(this);
        tv_select_size.setOnClickListener(this);
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        mHelpCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showTwoImageAnimation();
                    mHideButton.setVisibility(View.GONE);
                }else {
                    hideTwoImageAnimation();
                    mHideButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }



    public void setViewPager(List<Fragment> list) {
        this.list = list;
        // 设置Viewpager缓存页数
        mViewPager.setOffscreenPageLimit(list.size());
        mViewPager.setAdapter(new MainPageAdapter(fm, list));
        initSlidingTabLayout();
    }

    /**
     * 设置tab
     */
    private void initSlidingTabLayout() {
        mTabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        mTabs.setCustomTabView(R.layout.custom_tab, 0);        // Set custom tab layout
        // mTabs.setDistributeEvenly(true);  // Center the tabs in the layout
        mTabs.setSelectedIndicatorColors(Color.WHITE);    // Customize tab color
        mTabs.setViewPager(mViewPager);
    }

    /**
     * 侧滑菜单
     *
     * @param fm
     * @param drawerMenu
     */
    public void setDrawerMenu(FragmentManager fm, Fragment drawerMenu) {
        this.fm = fm;
        if (fm != null) {
            fm.beginTransaction().replace(R.id.left_drawer, drawerMenu).commit();
        } else {
            L.e("FragmentManager is null");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createButton:
                layout_hide.setVisibility(View.VISIBLE);
                createButton.setVisibility(View.GONE);
                showTwoButtonAnimation();
                break;
            case R.id.hideButton:
                layout_hide.setVisibility(View.GONE);
                createButton.setVisibility(View.VISIBLE);
                showButtonAnimation();
                break;
            case R.id.ib_close:
                hideBottomLayout();
                break;
            case R.id.tv_select_size:
                A.goOtherActivity(context, CreateConnectionActivity.class);
                break;
        }
    }
    /**
     * 两侧出现小人动画
     */
    private void showTwoImageAnimation() {
        final int dis = PixelUtil.dp2px(160);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(200);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mLeftImage.setTranslationX(value * dis);
                mRightImage.setTranslationX(-value * dis);
            }
        });
        valueAnimator.start();
    }/**
     * 隐藏两侧出现小人动画
     */
    private void hideTwoImageAnimation() {
        final int dis = PixelUtil.dp2px(150);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(200);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mLeftImage.setTranslationX((1-value) * dis);
                mRightImage.setTranslationX((value-1) * dis);
            }
        });
        valueAnimator.start();
    }


    /**
     * 按钮出现动画
     */
    private void showTwoButtonAnimation() {
        final int dis = PixelUtil.dp2px(150);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(200);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mCreate.setTranslationY(-value * dis);
                mCreate.setTranslationX(-value * dis * 0.1f);
                mJoin.setTranslationX(-value * dis);
                mJoin.setTranslationY(-value * dis *0.1f);
                mCreate.setScaleX(0.6f + value);
                mCreate.setScaleY(0.6f + value);
                mJoin.setScaleX(0.6f + value);
                mJoin.setScaleY(0.6f + value);
            }
        });
        valueAnimator.start();

    }

    /**
     * 抖动按钮的动画
     */
    private void showButtonAnimation() {
        createButton.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.shake));
    }

    class MainPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> list;
        private String tabs[] = new String[]{"应用", "音乐", "图片", "视频", "文件"};

        public MainPageAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }

    //------------------------ 隐藏在底部的布局---------------------------------------------
    @Override
    public void addSendFile(WFile sendFile) {
        application.addSendFiles(sendFile);
        if (application.getSendFiles().size() == 1) {
            showAnim = true;
        } else {//如果已经出现了,就不用再展示出现动画了
            showAnim = false;
        }
        handleAnim();
    }

    @Override
    public void removeSendFile(WFile sendFile) {
        application.removeSendFiles(sendFile);
        handleAnim();
    }

    /**
     * 判断当前动画是显示还是关闭
     */
    private void handleAnim() {
        if (application.getSendFiles().size() > 0) {
            showBottomLayout();
        } else {
            hideBottomLayout();
        }
    }

    private void showBottomLayout() {
        if (showAnim) {
            // 防止出现缝隙
            final int height = layout_bottom.getHeight() - 2;
            ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(300);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    layout_bottom.setTranslationY(-height * value);
                }
            });
            va.start();
        }
        tv_select_size.setText("传输（" + BaseApplication.getInstance().getSendFiles().size() + "）");
        layout_bottom.setVisibility(View.VISIBLE);
    }

    private void hideBottomLayout() {
        // 防止出现缝隙
        final int height = layout_bottom.getHeight() - 2;
        ValueAnimator va = ValueAnimator.ofFloat(0, 1).setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                layout_bottom.setTranslationY(height * value - height);
            }
        });
        va.start();
        application.clearSendFiles();
        ((AppFragment) list.get(0)).vu.adapter.notifyDataSetChanged();
    }
}
