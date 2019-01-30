package com.cnnet.otc.health.util;

import android.content.Context;

/**
 * Created by SZ512 on 2016/1/18.
 */
public class DensityUtil {
    // int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
    // // 屏幕宽（像素，如 480px
    // int screenHeight =
    // getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如800px)
    // int xDip = DensityUtil.px2dip(SettingActivity.this, (float)
    // (screenWidth * 1.0));
    // int yDip = DensityUtil.px2dip(SettingActivity.this, (float)
    // (screenHeight * 1.0));

    /**
     * 根据手机的分辨率将值为 dp 的单位 转成值为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率值为 px(像素) 的单位 转成值为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float sp2px(Context context, float sp){
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
