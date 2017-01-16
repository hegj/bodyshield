package com.cmax.bodysheild.http;

import com.cmax.bodysheild.api.ApiServer;
import com.cmax.bodysheild.api.Url;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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
        httpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl( Url.BASE_URL)
                .client(httpClientBuilder.build())
                .build();
        apiService = retrofit.create(ApiServer.class);
    }
   public   RequestBody buildHeadImageFileBody(int uid, File file){
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uid", uid+"")
               // .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image*//*"), file))
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file))
                .build();
        return  requestBody;
    }
    public     MultipartBody.Part buildHeadImageFileBody( File file){
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return  part;
    }

      public  RequestBody toTextRequestBody(String s){
           return RequestBody.create(MediaType.parse("text/plain"),s);
      }
    public RequestBody toImageRequestBody(File file) {
        return    RequestBody.create(MediaType.parse("image/jpg"), file);
    }
}
