package com.system.ts.android.utils;

import android.util.Log;

import java.text.DecimalFormat;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class MyVolumeFormatter {

    public static String volumeFormatter(String volume){
        DecimalFormat mFormat = new DecimalFormat("###,###,##0.0");
        float num = Float.parseFloat(volume.replace(",","")) / 10000;
        return mFormat.format(num) + "万";
    }

    public static String moneyFormatter(String money){
        DecimalFormat mFormat = new DecimalFormat("###,###,##0.0");
        float num = Float.parseFloat(money.replace(",","")) / 10000;
        return mFormat.format(num) + "亿";
    }

}
