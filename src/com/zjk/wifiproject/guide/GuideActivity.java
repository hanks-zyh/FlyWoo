package com.zjk.wifiproject.guide;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjk.wifiproject.R;
import com.zjk.wifiproject.base.BaseActivity;
import com.zjk.wifiproject.base.BaseFragment;
import com.zjk.wifiproject.config.SharedKey;
import com.zjk.wifiproject.main.MainActivity;
import com.zjk.wifiproject.util.A;
import com.zjk.wifiproject.util.SP;

public class GuideActivity extends BaseActivity {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);

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

        mViewPager.setPageTransformer(true, new GuidePageTranformer(mViewPager));
    }

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

        private void init(View v) {
            if (index < 3) {
                v.findViewById(R.id.top).setBackgroundColor(colors[index]);
                ((ImageView) v.findViewById(R.id.guide)).setImageResource(images[index][0]);
                ((ImageView) v.findViewById(R.id.image_m)).setImageResource(images[index][1]);
                ((TextView) v.findViewById(R.id.text)).setText(texts[index]);
            } else {
                v.findViewById(R.id.top).setBackgroundColor(Color.parseColor("#DDE865"));
                ((ImageView) v.findViewById(R.id.guide)).setImageResource(R.drawable.guide_3_text_bg);
                v.findViewById(R.id.image_m).setVisibility(8);
                TextView tv = ((TextView) v.findViewById(R.id.text));
                tv.setTextSize(20);
                tv.setText(R.string.guide_text1);
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // �´β���չʾ�����
                        SP.put(context, SharedKey.isfirst, false);
                        A.goOtherActivityFinish(context, MainActivity.class);
                    }
                });
            }
        }
    }

}
