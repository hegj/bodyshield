package com.cmax.bodysheild.http.rxsubscriber;


import android.os.SystemClock;

import com.cmax.bodysheild.base.view.IStateView;
import com.cmax.bodysheild.http.rxexception.DefaultErrorBundle;
import com.cmax.bodysheild.http.rxexception.ErrorManager;

import rx.Subscriber;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public abstract class ProgressSubscriber<T> extends Subscriber<T> {

    private IStateView iStateView;
    private long dialogStartTime;

    public ProgressSubscriber(IStateView iStateView) {
        this.iStateView = iStateView;
    }

    @Override
    public void onCompleted() {
     /*   if (System.currentTimeMillis()-dialogStartTime<1000){
            try {
                Thread.sleep(1000-(System.currentTimeMillis()-dialogStartTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {

            }

        }*/
        _onCompleted();
        iStateView.hideProgressDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        dialogStartTime =System.currentTimeMillis();
        iStateView.showProgressDialog();
        _onStart();
    }

    @Override
    public void onError(Throwable e) {
        iStateView.hideProgressDialog();
        _onError(ErrorManager.handleError(new DefaultErrorBundle((Exception) e)));

    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    public void _onStart() {

    }

    public abstract void _onError(String message);

    public abstract void _onNext(T t);

    public abstract void _onCompleted();
}
