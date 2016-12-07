package com.cmax.bodysheild.inject.component;

import android.support.annotation.NonNull;


import com.cmax.bodysheild.inject.module.AppModule;

import dagger.Component;

/**
 * Created by Administrator on 2016/11/25 0025.
 */
@Component(modules = AppModule.class)
public interface AppComponent {

    class Instance{
    private  static     AppComponent mComponent;
        public  static void init(@NonNull AppComponent appComponent){
             mComponent=appComponent;
        }
        public  static  AppComponent getAppComponent(){
            return mComponent;
        }
    }
}
