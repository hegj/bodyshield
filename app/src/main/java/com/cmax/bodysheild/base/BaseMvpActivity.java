package com.cmax.bodysheild.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.cmax.bodysheild.base.presenter.BasePresenter;
import com.cmax.bodysheild.base.view.IView;
import com.cmax.bodysheild.inject.component.ActivityComponent;
import com.cmax.bodysheild.inject.component.AppComponent;
import com.cmax.bodysheild.inject.component.DaggerActivityComponent;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public abstract class BaseMvpActivity   <T extends BasePresenter> extends FragmentActivity implements IView {
    @Inject
    protected  T presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        if (layoutId<=0){
            throw new NullPointerException("Please set Layout  ");
        }
        ActivityComponent activityComponent = DaggerActivityComponent.builder().appComponent(AppComponent.Instance.getAppComponent()).build();
        setActivityComponent(activityComponent);
        setContentView(layoutId);
        ButterKnife.bind(this);
        initView(savedInstanceState);
        if (presenter!=null)
            presenter.attachView(this);
        initData(savedInstanceState);
        initEvent(savedInstanceState);
    }

    protected void initEvent(Bundle savedInstanceState) {

    }

    protected void initData(Bundle savedInstanceState) {

    }

    protected   void initView(Bundle savedInstanceState){

    }

    protected abstract int  getLayoutId() ;
    protected  abstract void setActivityComponent(ActivityComponent activityComponent);
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter!=null) presenter.detachView();
    }
}
