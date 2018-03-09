package com.system.ts.android.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class ToastUtils {
    public static void makeToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
