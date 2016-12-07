package com.cmax.bodysheild.inject.module;

import android.content.Context;

import dagger.Module;

/**
 * Created by Administrator on 2016/11/25 0025.
 */
@Module
public class AppModule {
    private Context content;

    public AppModule(Context context) {
        this.content =context;
    }
}
