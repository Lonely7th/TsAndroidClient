package com.system.ts.android.utils.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.system.ts.android.bean.TkDetailsBean;
import com.system.ts.android.utils.MyTimeUtils;
import com.system.ts.android.utils.SharedPreferencesUtils;
import com.system.ts.android.utils.TkCodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Time ： 2018/3/13 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class TsCandleStickChart extends CandleStickChart {
    private static final int FLIP_PERIOD = 200;//翻页事件的时间间隔
    private static final int SLIDE_PERIOD = 300;//滚动事件的时间间隔
    private static final int X_ANIMATE_PERIOD = 500;//X轴动画的时间间隔
    private static final int FLIP_DISTANCE = 3600;//翻页事件的最小速率
    private static final int INSERTDATAPIXELS = 20;//滑动距离与数据量的比例

    private OnTsGestureListener onTsGestureListener;

    private boolean isLongPressed = false;//是否处于长按状态
    private float currentDownindex = 0.0f;//当前点击的坐标

    private List<TkDetailsBean> tkData = new ArrayList<>();

    public TsCandleStickChart(Context context) {
        super(context);
        initListener();
    }

    public TsCandleStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initListener();
    }

    public TsCandleStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initListener();
    }

    /**
     * 数据更新
     */
    public void notifyTsDataSetChanged(List<TkDetailsBean> tkData){
        if(this.tkData != null){
            this.tkData.clear();
            this.tkData.addAll(tkData);
        }
        notifyDataSetChanged();
    }

    /**
     * 初始化监听
     */
    protected void initListener(){
        setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        currentDownindex = motionEvent.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(!isLongPressed){
                            if(MyTimeUtils.getDistanceTime(motionEvent.getDownTime(), motionEvent.getEventTime()) > SLIDE_PERIOD){
//                                onTranslateUI(motionEvent.getRawX());
                            }
                        }
                        break;
                }
                return false;
            }
        });
        setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                if (me.getFlags() == 0) {
                    isLongPressed = false;
                    setHighlightPerDragEnabled(false);
                }
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                //为了避免滑动事件与高亮时间冲突，高亮事件只在长按后显示，优先消费滑动事件
                if (me.getFlags() == 0) {
                    isLongPressed = true;
                    setHighlightPerDragEnabled(true);
                    highlightValue(getHighlightByTouchPoint(me.getX(), me.getY()).getXIndex(),0);
                }
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                if (!isLongPressed) {
                    if(MyTimeUtils.getDistanceTime(me2.getDownTime(), me2.getEventTime()) < FLIP_PERIOD){
                        if (velocityX > FLIP_DISTANCE) {
                            String code = TkCodeUtils.getNextCode(SharedPreferencesUtils.getCurrentTkCode(), -1);
                            if (!TextUtils.isEmpty(code)) {
//                                loadStickData(code, true);
                            }
                        } else if (velocityX < -FLIP_DISTANCE) {
                            String code = TkCodeUtils.getNextCode(SharedPreferencesUtils.getCurrentTkCode(), 1);
                            if (!TextUtils.isEmpty(code)) {
//                                loadStickData(code, true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
    }

}
