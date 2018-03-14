package com.system.ts.android.utils.view;

/**
 * Time ： 2018/3/13 .
 * Author ： JN Zhang .
 * Description ： .
 * Version : 1.0.0 .
 */
public interface OnTsGestureListener {
    /**
     * 单次点击
     */
    void onChartSingleTapped();
    /**
     * 多次点击
     */
    void onChartDoubleTapped();
    /**
     * 快速滑动
     * @param direction 滑动方向
     */
    void onChartFastSlide(int direction);
    /**
     * 匀速滑动
     * @param direction 滑动方向
     */
    void onChartSlowSlide(int direction);
    /**
     * 长按后滑动
     * @param position 当前位置
     */
    void onChartSlideLongClick(int position);
}
