package com.cmax.bodysheild.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmax.bodysheild.base.presenter.IPresenter;
import com.cmax.bodysheild.base.view.IView;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/15 0015.
 */

public abstract class BaseFragment <T extends IPresenter> extends Fragment implements IView{
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    private FragmentActivity mActivity;
    protected  T basePresenter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            boolean state = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if (state){
             fragmentTransaction.show(this);
            }else {
                fragmentTransaction.hide(this);
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN,isHidden());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutId()<=0){
            throw  new  NullPointerException("Please set LayoutId");
        }
        mActivity = getActivity();
        View rootView = View.inflate(mActivity, getLayoutId(), null);
        basePresenter = setBasePresenter();
        initView(savedInstanceState);
        if (basePresenter !=null) {
            basePresenter.attachView(this);
        }/*else {
            throw new NullPointerException("Please set basePresenter parameters among initView method");
        }*/
        ButterKnife.bind(this,rootView);
        initData(savedInstanceState);
        initEvent(savedInstanceState);
        return rootView;

    }
    protected   abstract void initData(Bundle savedInstanceState)  ;
    protected abstract void initView(Bundle savedInstanceState);
    protected abstract void initEvent(Bundle savedInstanceState);
    private T setBasePresenter() {
        return null;
    }

    protected abstract int  getLayoutId() ;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (basePresenter !=null) {
            basePresenter.detachView();
        }
    }
}
