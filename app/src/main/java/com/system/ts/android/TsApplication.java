package com.system.ts.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class TsApplication extends Application{
    public static SharedPreferences sf = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sf = PreferenceManager.getDefaultSharedPreferences(this);
        //初始化okgo
        OkGo.getInstance().debug("okgo")
                .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)
                .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)
                .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE);
    }
}
