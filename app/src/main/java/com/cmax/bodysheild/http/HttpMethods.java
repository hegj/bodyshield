package com.cmax.bodysheild.http;

import com.cmax.bodysheild.api.ApiServer;
import com.cmax.bodysheild.api.Url;

import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class HttpMethods {

    public    ApiServer apiService;
    private   Retrofit retrofit;

    private  static    class SingleHttpMethods{
        private static  final HttpMethods INSTANCE=new HttpMethods();
    }
    //获取单例
    public static HttpMethods getInstance() {
        return SingleHttpMethods.INSTANCE;
    }

    private HttpMethods(){
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(12, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl( Url.BASE_URL)
                .client(httpClientBuilder.build())
                .build();
        apiService = retrofit.create(ApiServer.class);
    }

}
