package com.cmax.bodysheild.widget;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Viewpager 页面切换动画
 */
public class FadeInOutPageTransformer implements ViewPager.PageTransformer {

    @SuppressLint("NewApi")
    @Override
    public void transformPage(View page, float position) {
        if (position < -1) {//页码完全不可见
            page.setAlpha(0);
        } else if (position < 0) {
            page.setAlpha(1 + position);
        } else if (position < 1) {
            page.setAlpha(1 - position);
        } else {
            page.setAlpha(0);
        }
    }
}
