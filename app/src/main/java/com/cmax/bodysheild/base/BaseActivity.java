package com.cmax.bodysheild.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

import com.cmax.bodysheild.base.presenter.BasePresenter;
import com.cmax.bodysheild.base.view.IView;
import com.cmax.bodysheild.inject.component.ActivityComponent;
import com.cmax.bodysheild.util.Constant;

import butterknife.ButterKnife;

/**
 * Created by allen on 15/9/27.
 */
public  abstract  class BaseActivity   <T extends BasePresenter> extends FragmentActivity implements IView {


    // @Inject
    protected  T basePresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        if (layoutId<=0){
            throw new NullPointerException("Please set Layout  ");
        }
        basePresenter = setBasePresenter();
        setContentView(layoutId);
        // ActivityComponent activityComponent = DaggerActivityComponent.builder().appComponent(AppComponent.Instance.getAppComponent()).build();
        //setActivityComponent(activityComponent);
        initView(savedInstanceState);
        if (basePresenter !=null) {
            basePresenter.attachView(this);
        }/*else {
            throw new NullPointerException("Please set basePresenter parameters among initView method");
        }*/
        ButterKnife.bind(this);
        initData(savedInstanceState);
        initEvent(savedInstanceState);
    }

    protected   T setBasePresenter(){
        return  null;
    }

    protected void initEvent(Bundle savedInstanceState) {

    }

    protected void initData(Bundle savedInstanceState) {

    }

    protected   void initView(Bundle savedInstanceState){

    }

    protected abstract int  getLayoutId() ;
    protected    void setActivityComponent(ActivityComponent activityComponent){

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (basePresenter !=null) basePresenter.detachView();
        this.unregisterReceiver(this.finishAppReceiver);
        isRegister = false;
    }

    private long lastClickTime = 0L;
    private boolean isRegister = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(!isRegister){
            // 在当前的activity中注册广播
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.EXIT);
            this.registerReceiver(this.finishAppReceiver, filter);
            isRegister = true;
        }

    }



    /**
     * 关闭Activity的广播，放在自定义的基类中，让其他的Activity继承这个Activity就行
     */
    protected BroadcastReceiver finishAppReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 600) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }
}
