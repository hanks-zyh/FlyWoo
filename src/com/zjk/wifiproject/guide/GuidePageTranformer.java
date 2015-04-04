package com.zjk.wifiproject.guide;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

import com.zjk.wifiproject.util.L;

public class GuidePageTranformer implements PageTransformer {

    int colors[][] = new int[][] { { 255, 0, 0 }, { 0, 255, 0 }, { 0, 0, 255 }, { 255, 255, 0 } };
    private int index = 0;
    private int next = 1;
    int cs[] = new int[] { Color.RED, Color.BLUE, Color.GRAY, Color.YELLOW };

    private ViewPager parent;

    public GuidePageTranformer(ViewPager parent) {
        this.parent = parent;
    }

    // A->B A: 0 ~ -1 B: 1 ~ 0
    //

    @Override
    public void transformPage(View view, float position) {
        // index = (parent.getCurrentItem());
        // next = (index + 1) % cs.length;
        if (position < -1) {

        } else if (position <= 0) { // 页面向右滑动，0 —> - 向左是 -1->0
            // index = next;
            // next = (index + 1) % colors.length;
            L.i("..........position=" + position + ".......index=" + index);
            int currentColor = evaluate(-position, cs[index], cs[next]);
            parent.setBackgroundColor(currentColor);
            if (position == -1) {
                index = (parent.getCurrentItem());
                next = (index + 1) % cs.length;
            }
        } else if (position < 1) {
        } else if (position == 1) {
            // if (position == 0) {
            // index = index - 1 <= 0 ? cs.length - 1 : index - 1;
            // }
        }
    }

    private int evaluate(float fraction, int startValue, int endValue) {
        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24)
                | ((startR + (int) (fraction * (endR - startR))) << 16)
                | ((startG + (int) (fraction * (endG - startG))) << 8)
                | ((startB + (int) (fraction * (endB - startB))));
    }
}
