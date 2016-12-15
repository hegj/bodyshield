package com.cmax.bodysheild.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseCustomView;
import com.cmax.bodysheild.base.BaseTabPage;
import com.cmax.bodysheild.bean.TabPageData;
import com.cmax.bodysheild.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class MyPagerSlidingTabStripExtends extends BaseCustomView {
    @Bind(R.id.tabs_iew)
    PagerSlidingTabStripExtends tabsView;
    @Bind(R.id.tab_viewpage)
    ViewPagerCompat tabViewpage;
    @Bind(R.id.rl_tab)
    LinearLayout rlTab;
    @Bind(R.id.tv_filter)
    TextView tvFilter;
    @Bind(R.id.ll_filter)
    LinearLayout llFilter;


    private String[] mTabTitle;
    private List<BaseTabPage> mPageList;
    private PagerAdapter mPagerAdapter;
    private List<Fragment> fragmentPageList;

    public MyPagerSlidingTabStripExtends(Context context) {
        super(context);
    }

    public MyPagerSlidingTabStripExtends(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initEvent(final Context context) {

    }
    public void setFilterOnClickListener(View.OnClickListener l){
        llFilter.setOnClickListener(l);
    }
    public void setLlFilterVisibility(boolean b){
        llFilter.setVisibility(b?VISIBLE:GONE);
    }
    @Override
    protected void initData(Context context, AttributeSet attrs) {
        mTabTitle = new String[10];
        mPageList = new ArrayList<>();

    }

    public void setMeasure(boolean measure) {
        tabViewpage.setMeasure(measure);
    }

    @Override
    protected void initView() {
        rootView = (ViewGroup) View.inflate(UIUtils.getContext(), R.layout.view_my_pagerslidingtabstripextends, null);
        ButterKnife.bind(this, rootView);
    }

    public void setIndicatorColorResource(int res) {
        tabsView.setIndicatorColorResource(res);

    }

    public void setShouldExpand(boolean flag) {
        tabsView.setShouldExpand(flag);
    }

    public void setIndicatorHeight(int height) {
        tabsView.setIndicatorHeight(height);
    }


    public void setTabBackground(int resId) {
        tabsView.setTabBackground(resId);
    }

    public void setTextColor(int resId) {
        tabsView.setTextColorResource(resId);
    }

    public void setViewTouchMode(boolean flag) {
        tabViewpage.setViewTouchMode(flag);
    }

    public void setViewTouchMode(boolean b, int position) {
        tabViewpage.setViewTouchMode(b, null, position, false);
    }

    public void setViewTouchMode(boolean flag, List list, int position, boolean isSlideShow) {
        tabViewpage.setViewTouchMode(flag, list, position, isSlideShow);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        tabsView.setOnPageChangeListener(listener);
    }

    public void setPageAdapterData(TabPageData info) {
        if (info == null) {
            return;
        }
        if (info.pageList == null || info.pageList.size() == 0) {
            return;
        }
        setBaseInfo(info);
        setPageList(info);
        setAdapter();

    }

    public void setFragmentAdapterData(TabPageData info, FragmentActivity activity) {
        if (info == null) return;
        if (info.fragmentList == null || info.fragmentList.size() == 0) return;
        setBaseInfo(info);
        setFragmentPageList(info);
        setFragmentAdapter(activity);

    }

    private void setFragmentPageList(TabPageData info) {
        fragmentPageList = info.fragmentList;
    }

    private void setFragmentAdapter(FragmentActivity activity) {
        PageFragmentAdapter adapter = new PageFragmentAdapter(activity.getSupportFragmentManager());
        tabViewpage.setAdapter(adapter);
        tabsView.setViewPager(tabViewpage);
    }

    private void setBaseInfo(TabPageData info) {

        if (info.tabTitle == null || info.tabTitle.length == 0) {
            return;
        }
        setTabTitle(info.tabTitle, false);
        // 设置切换动画
        setViewPageAnimation(new FadeInOutPageTransformer());
    }

    public void setViewPageAnimation(ViewPager.PageTransformer transformer) {
        tabViewpage.setPageTransformer(true, transformer);
    }

    public void setAdapter() {
        mPagerAdapter = new pagerAdapter();
        tabViewpage.setAdapter(mPagerAdapter);

        tabsView.setViewPager(tabViewpage);
    }

    public void setOffscreenPageLimit(int limit) {
        tabViewpage.setOffscreenPageLimit(limit);
    }

    public void setTabBackgroundResource(int resId) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.height = UIUtils.dp2px(44);
        rlTab.setLayoutParams(layoutParams);
        rlTab.setBackgroundResource(resId);
    }

    public void setTabPaddingLeftRight(int size) {
        tabsView.setTabPaddingLeftRight(size);
    }

    public void setTextSize(int textSizePx) {
        tabsView.setTextSize(textSizePx);
    }

    public void setTabTitle(String[] info, boolean isRefresh) {
        mTabTitle = info;
        if (isRefresh) {
            notifyDataSetChanged();
        }
    }

    public void setCurrentItem(int currentItem) {
        tabViewpage.setCurrentItem(currentItem, true);
    }

    public void setPageList(TabPageData info) {
        mPageList = info.pageList;
    }

    public void setViewTouchSource(List pageList) {
        tabViewpage.setViewTouchSource(pageList);
    }

    public void setViewPageTouchIsSlide(boolean isSlideShow) {
        tabViewpage.setViewPageTouchIsSlide(isSlideShow);
    }

    public ViewPagerCompat getTabViewPage() {

        return tabViewpage;
    }

    public void setViewPageBackgroundResource(int res) {
        tabViewpage.setBackgroundResource(res);
    }




    private class PageFragmentAdapter extends FragmentPagerAdapter {

        public PageFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentPageList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentPageList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitle[position];
        }
    }

    private class pagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mPageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseTabPage page = mPageList.get(position);
            if (!page.isTopTab()) {
                page.firstLoad();//在这里去开子线程加载数据
            }
            View pageView = page.getRootView();
            container.addView(pageView);//这句话不能少
            return pageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitle[position];
        }
    }

    public void setTabSize(int width, int height) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.height = height;
        layoutParams.width = width;
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        tabsView.setLayoutParams(layoutParams);
    }


    public void setDividerColorResource(int resId) {
        tabsView.setDividerColorResource(resId);
    }

    public void notifyDataSetChanged() {
        tabsView.notifyDataSetChanged();
    }
}
