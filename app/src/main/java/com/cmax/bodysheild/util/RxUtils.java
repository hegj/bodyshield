package com.cmax.bodysheild.util;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class RxUtils {
    public static Observable<Integer> countdown(int time) {
    if (time < 0) time = 0;

    final int countTime = time;
    return Observable.interval(0, 1, TimeUnit.SECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map(new Func1<Long, Integer>() {
                @Override
                public Integer call(Long increaseTime) {
                    return countTime - increaseTime.intValue();
                }
            })
            .take(countTime + 1);

}
}
