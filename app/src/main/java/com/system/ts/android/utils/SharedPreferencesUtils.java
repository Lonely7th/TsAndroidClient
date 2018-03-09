package com.system.ts.android.utils;

import android.content.SharedPreferences;

import com.system.ts.android.TsApplication;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class SharedPreferencesUtils {

    public static String getCurrentTkCode(){
        return TsApplication.sf.getString("cur_tk_code", "000001");
    }

    public static void setCurrentTkCode(String tkCode){
        SharedPreferences.Editor editor = TsApplication.sf.edit();
        editor.putString("cur_tk_code", tkCode);
        editor.commit();
    }

}
