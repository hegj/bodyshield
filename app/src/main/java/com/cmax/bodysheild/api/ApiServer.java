package com.cmax.bodysheild.api;

import com.cmax.bodysheild.base.bean.BaseRequestData;
import com.cmax.bodysheild.bean.HistoryData;
import com.cmax.bodysheild.bean.SendMessageInfo;
import com.cmax.bodysheild.bean.UserProfileInfo;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public interface ApiServer {
    @FormUrlEncoded
    @POST( Url.LOGIN)
    Observable<BaseRequestData<UserProfileInfo>> login(@Field("name") String username, @Field("password") String password);
    @FormUrlEncoded
    @POST( Url.REGISTER)
    Observable<BaseRequestData<UserProfileInfo>> register(@FieldMap Map<String,String> map);
    @FormUrlEncoded
    @POST( Url.IS_REGISTER)
    Observable<BaseRequestData> isUserRegister(@Field("name") String username);
    @FormUrlEncoded
    @POST( Url.FEEDBACK)
    Observable<BaseRequestData<Object>> feedBack(@FieldMap Map<String, String>map);
    @Multipart
    @POST()
    Observable<BaseRequestData<String>> updateImage(@retrofit2.http.Url String url, @PartMap Map<String,RequestBody> map);
    @FormUrlEncoded
    @POST( Url.UPLOAD_TEMPERATURE)
    Observable<BaseRequestData<Object>> uploadTemperature(@Field("data") String data);
    @FormUrlEncoded
    @POST( Url.DOWNLOAD_TEMPERATURE)
    Observable<BaseRequestData<List<HistoryData>>> downloadTemperature(  @Field("uid") String uid, @Field("lastTimestamp") String lastTimestamp);
    @FormUrlEncoded
    @POST( Url.THIRD_LOGIN)
    Observable<BaseRequestData<UserProfileInfo>> thirdLogin(@FieldMap Map<String,String> map);
    @POST(Url.SEND_CAPTCHA)
    @FormUrlEncoded
    Observable<BaseRequestData<SendMessageInfo>> sendVerifyCode(@Field("mobile") String mobile);
    @POST(Url.CHANGE_PASSWORD)
    @FormUrlEncoded
    Observable<BaseRequestData<Object>> changePassword(@Field("name")String username,@Field("newPassword") String password);
    @POST(Url.SEND_CAPTCHA_BY_USERNAME)
    @FormUrlEncoded
    Observable<BaseRequestData<SendMessageInfo>> sendVerifyCodeByUserName(@Field("userName")String userName);
}
