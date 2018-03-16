package com.system.ts.android.utils;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Time ： 2018/3/7 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class MyYAxisValueFormatter implements YAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter () {
        mFormat = new DecimalFormat("###,###,##0.00");
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value);
    }
}
