package com.zjk.wifiproject.guide;


import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zjk.wifiproject.R;
import com.zjk.wifiproject.base.BaseActivity;
import com.zjk.wifiproject.base.BaseFragment;
import com.zjk.wifiproject.config.SharedKey;
import com.zjk.wifiproject.main.MainActivity;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.SP;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        init();
    }

    private void init() {
        initViewPager();
    }

    private int currentPositin;
    private int stateChanged;

    private void initViewPager() {

        fragmentList.add(new GuideFragment(0));
        fragmentList.add(new GuideFragment(1));
        fragmentList.add(new GuideFragment(2));
        fragmentList.add(new GuideFragment(3));

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        GuidePageAdapter adapter = new GuidePageAdapter(getFragmentManager());
        mViewPager.setAdapter(adapter);

     //   mViewPager.setPageTransformer(true, new GuidePageTranformer(mViewPager));
    }

    //viewpager的适配器，数据是fragmentlist
    class GuidePageAdapter extends FragmentPagerAdapter {

        public GuidePageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    int[][] images = new int[][] { //
            { R.drawable.guide_1, R.drawable.guide_1_text }, //
            { R.drawable.guide_2, R.drawable.guide_2_text },//
            { R.drawable.guide_3, R.drawable.guide_3_text },//
    };

    int[] colors = new int[] { Color.parseColor("#ED4037"), Color.parseColor("#6DC2DE"),
            Color.parseColor("#00D390") };

    int[] texts = new int[] { R.string.guide_text_1, R.string.guide_text_2, R.string.guide_text_3 };

    //fragment
    class GuideFragment extends BaseFragment {
        private int index;

        public GuideFragment(int index) {
            this.index = index;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_guide, container, false);
            init(v);
            return v;
        }

        //根据index改变四个Fragment的内容
        private void init(View v) {
            if (index < 3) {
                v.findViewById(R.id.top).setBackgroundColor(getResources().getColor(R.color.main_red));
                ((ImageView) v.findViewById(R.id.guide)).setImageResource(images[index][0]);
                ((ImageView) v.findViewById(R.id.image_m)).setImageResource(images[index][1]);
                Logger.d(index + "");
            //    int str = texts[index];
            //    Logger.d(str + "," + getString(R.string.guide_text_1));

            //    ((TextView) v.findViewById(R.id.text)).setText(str);
            } else {
                v.findViewById(R.id.top).setBackgroundColor(getResources().getColor(R.color.main_red));
                ((ImageView) v.findViewById(R.id.guide)).setImageResource(R.drawable.guide_3_text_bg);
                v.findViewById(R.id.image_m).setVisibility(View.GONE);
                TextView tv = ((TextView) v.findViewById(R.id.text));
                tv.setTextSize(20);
                tv.setText(R.string.guide_text1);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SP.put(context, SharedKey.isfirst, false);
                        A.goOtherActivityFinish(context, MainActivity.class);
                    }
                });
            }
        }
    }

}
