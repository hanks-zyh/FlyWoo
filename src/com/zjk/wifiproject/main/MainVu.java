package com.zjk.wifiproject.main;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.presenters.Vu;
import com.zjk.wifiproject.util.L;
import com.zjk.wifiproject.view.tabs.SlidingTabLayout;

/**
 * 主界面的框架
 * 
 * @version 1.0
 * @author zyh
 */
public class MainVu implements Vu {

    private View view;
    private FragmentManager fm;
    private ViewPager mViewPager;
    private SlidingTabLayout mTabs;

    @Override
    public void init(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.vu_main, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setViewPager(List<Fragment> list) {
        mViewPager.setAdapter(new MainPageAdapter(fm, list));
        initSlidingTabLayout();
    }

    private void initSlidingTabLayout() {
        mTabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        // Set custom tab layout
        mTabs.setCustomTabView(R.layout.custom_tab, 0);
        // Center the tabs in the layout
        // mTabs.setDistributeEvenly(true);
        // Customize tab color
        mTabs.setSelectedIndicatorColors(Color.RED);
        mTabs.setViewPager(mViewPager);

    }

    public void setDrawerMenu(FragmentManager fm, Fragment drawerMenu) {
        this.fm = fm;
        if (fm != null) {
            fm.beginTransaction().replace(R.id.left_drawer, drawerMenu).commit();
        } else {
            L.e("FragmentManager is null");
        }
    }

    class MainPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> list;
        private String tabs[] = new String[] { "应用", "音乐", "图片", "视频", "文件" };

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
}
