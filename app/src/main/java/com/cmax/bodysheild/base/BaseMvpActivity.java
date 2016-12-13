package com.cmax.bodysheild.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.cmax.bodysheild.base.presenter.BasePresenter;
import com.cmax.bodysheild.base.view.IView;
import com.cmax.bodysheild.inject.component.ActivityComponent;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public abstract class BaseMvpActivity   <T extends BasePresenter> extends FragmentActivity implements IView {
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
        }else {
            throw new NullPointerException("Please set basePresenter parameters among initView method");
        }
        ButterKnife.bind(this);
        initData(savedInstanceState);
        initEvent(savedInstanceState);
    }

    protected abstract T setBasePresenter();

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
        if (basePresenter !=null) basePresenter.detachView();
    }
}
