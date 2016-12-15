package com.cmax.bodysheild.base;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cmax.bodysheild.bean.BaseTabPageInfo;


/**
 * Created by Administrator on 2016/7/12 0012.
 */
public abstract class BaseTabPage {
    protected BaseTabPageInfo info;

    protected  String category;
    protected ViewGroup rootView;
    protected Activity mActivity;

    protected boolean loaded;
    public BaseTabPage(Activity activity, String category) {
        mActivity = activity;
        this.category=category;
        initView();
        initData();
        initEvent();
    }

    public BaseTabPage(BaseTabPageInfo info) {
        mActivity = info.activity;
        this.category=info.category;
         this.info=info;
        initView();
        initData();
        initEvent();
    }
    public BaseTabPage(Activity activity) {
        mActivity = activity;
        initView();
        initData();
        initEvent();
    }

    public BaseTabPage() {
        initView();
        initData();
        initEvent();
    }

    protected abstract void initView();
    protected abstract void initData() ;
    protected abstract void initEvent();
    public ViewGroup getRootView(){

        return rootView;
    }



    public abstract void firstLoad();
    public boolean isTopTab(){
        return  false;
    }

    public void onDestory(){

    }

    public ListView getListView() {

        return null;
    }
}
