package com.zjk.wifiproject.util;

import android.content.Context;
import android.content.res.Resources;

import com.zjk.wifiproject.BaseApplication;

/**
 * 尺寸转换
 * 
 * @version 1.0
 * @author zyh
 */
public class PixelUtil {

    /**
     * The context.
     */
    private static Context mContext = BaseApplication.getInstance();

    /**
     * dpת px.
     * 
     * @param value
     *            the value
     * @return the int
     */
    public static int dp2px(float value) {
        final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    /**
     * dpת px.
     * 
     * @param value
     *            the value
     * @param context
     *            the context
     * @return the int
     */
    public static int dp2px(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);
    }

    /**
     * pxתdp.
     * 
     * @param value
     *            the value
     * @return the int
     */
    public static int px2dp(float value) {
        final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }

    /**
     * pxתdp.
     * 
     * @param value
     *            the value
     * @param context
     *            the context
     * @return the int
     */
    public static int px2dp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }

    /**
     * spתpx.
     * 
     * @param value
     *            the value
     * @return the int
     */
    public static int sp2px(float value) {
        Resources r;
        if (mContext == null) {
            r = Resources.getSystem();
        } else {
            r = mContext.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }

    /**
     * spתpx.
     * 
     * @param value
     *            the value
     * @param context
     *            the context
     * @return the int
     */
    public static int sp2px(float value, Context context) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }

    /**
     * pxתsp.
     * 
     * @param value
     *            the value
     * @return the int
     */
    public static int px2sp(float value) {
        final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }

    /**
     * pxתsp.
     * 
     * @param value
     *            the value
     * @param context
     *            the context
     * @return the int
     */
    public static int px2sp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }

}
