package com.cmax.bodysheild.base.presenter;


import com.cmax.bodysheild.base.view.IView;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public interface IPresenter <V extends IView>{
    void attachView(V view);

    void detachView();
}
