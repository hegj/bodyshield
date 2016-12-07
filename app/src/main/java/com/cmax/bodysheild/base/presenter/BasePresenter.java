package com.cmax.bodysheild.base.presenter;


import com.cmax.bodysheild.base.view.IView;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class BasePresenter<T extends IView> implements IPresenter<T> {
    private T view;
    protected CompositeSubscription compositeSubscription;

    @Override
    public void attachView(T view) {
        this.view =view;
    }

    @Override
    public void detachView() {
        view=null;
        unSubscribe();
    }
    public T getView() {
        return view;
    }
    protected void unSubscribe() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    protected void addSubscribe(Subscription s) {
        if (this.compositeSubscription == null) {
            this.compositeSubscription = new CompositeSubscription();
        }
        this.compositeSubscription.add(s);
    }
}
