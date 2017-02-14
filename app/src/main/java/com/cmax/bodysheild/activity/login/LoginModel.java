package com.cmax.bodysheild.activity.login;

import com.cmax.bodysheild.bean.UserProfileInfo;
import com.cmax.bodysheild.http.HttpMethods;
import com.cmax.bodysheild.http.RxJavaHttpHelper;
import com.cmax.bodysheild.http.rxschedulers.RxSchedulersHelper;

import java.util.Map;

import rx.Observable;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class LoginModel {
    public  void getUserProfile(int uid){

    }

    public   Observable<UserProfileInfo>   login(String userName, String password){
        return HttpMethods.getInstance().apiService.login(userName, password)
                        .compose(RxJavaHttpHelper.<UserProfileInfo>handleResult())
                .compose( RxSchedulersHelper.<UserProfileInfo>applyIoTransformer());
    }
    public   Observable<UserProfileInfo>  thirdLogin(Map<String,String> map ){
        return HttpMethods.getInstance().apiService.thirdLogin(map)
                .compose(RxJavaHttpHelper.<UserProfileInfo>handleResult())
                .compose( RxSchedulersHelper.<UserProfileInfo>applyIoTransformer());
    }
}
