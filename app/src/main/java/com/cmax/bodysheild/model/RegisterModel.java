package com.cmax.bodysheild.model;

import android.widget.EditText;

import com.cmax.bodysheild.base.bean.BaseRequestData;
import com.cmax.bodysheild.bean.UserProfileInfo;
import com.cmax.bodysheild.http.HttpMethods;
import com.cmax.bodysheild.http.RxJavaHttpHelper;
import com.cmax.bodysheild.http.rxschedulers.RxSchedulersHelper;

import java.util.Map;

import rx.Observable;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class RegisterModel {
    public Observable<UserProfileInfo> register(Map<String,String>map){
        return HttpMethods.getInstance().apiService.register(map)
                .compose(RxJavaHttpHelper.<UserProfileInfo>handleResult())
                .compose( RxSchedulersHelper.<UserProfileInfo>applyIoTransformer());
    }
    public Observable<BaseRequestData>isRegister(String userName){
        return HttpMethods.getInstance().apiService.isUserRegister(userName)
             //  .compose(RxJavaHttpHelper.<UserProfileInfo>handleResult())
                .compose( RxSchedulersHelper.<BaseRequestData>applyIoTransformer());
    }
}
