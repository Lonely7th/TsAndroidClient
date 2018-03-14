package com.system.ts.android.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

/**
 * Time ： 2018/3/13 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public class TsCandleStickChart extends CandleStickChart {
    private static final int FLIP_PERIOD = 200;//翻页事件的时间间隔
    private static final int SLIDE_PERIOD = 300;//滚动事件的时间间隔
    private static final int FLIP_DISTANCE = 3600;//翻页事件的最小速率
    private static final int INSERTDATAPIXELS = 20;//滑动距离与数据量的比例

    private OnTsGestureListener onTsGestureListener;

    private boolean isLongPressed = false;//是否处于长按状态
    private float currentDownindex = 0.0f;//当前点击的坐标

    public TsCandleStickChart(Context context) {
        super(context);
    }

    public TsCandleStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TsCandleStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnTsGestureListener(OnTsGestureListener onTsGestureListener){
        this.onTsGestureListener = onTsGestureListener;
    }

    /**
     * 初始化监听
     */
    @Override
    protected void init(){
        super.init();
        setHighlightPerDragEnabled(false);//直接拖动屏幕时不显示高亮
        setHighlightPerTapEnabled(false);//点击屏幕时不显示高亮
        //设置监听
        setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (isLongPressed && onTsGestureListener != null) {
                    onTsGestureListener.onChartSlideLongClick(e.getXIndex());
                }
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
                        if(!isLongPressed && onTsGestureListener != null){
                            if((motionEvent.getEventTime() - motionEvent.getDownTime()) > SLIDE_PERIOD){
                                float rawX = motionEvent.getRawX();
                                int distance = (int)(rawX - currentDownindex);
                                if(Math.abs(distance) > INSERTDATAPIXELS){//每滑过一个单位都要更新用户手势的位置
                                    currentDownindex = rawX;
                                    int num = distance/INSERTDATAPIXELS;
                                    int change = num > 0?1:-1;
                                    onTsGestureListener.onChartSlowSlide(change);
                                }
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
                if(onTsGestureListener != null){
                    onTsGestureListener.onChartDoubleTapped();
                }
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                if(onTsGestureListener != null){
                    onTsGestureListener.onChartSingleTapped();
                }
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                if (!isLongPressed && onTsGestureListener != null) {
                    if((me2.getEventTime() - me2.getDownTime()) < FLIP_PERIOD){
                        if (velocityX > FLIP_DISTANCE) {
                            onTsGestureListener.onChartFastSlide(-1);
                        } else if (velocityX < -FLIP_DISTANCE) {
                            onTsGestureListener.onChartFastSlide(1);
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
