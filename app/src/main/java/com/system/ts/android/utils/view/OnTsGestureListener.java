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
     * @param startPosition 数据起点
     * @param endPosition 数据终点
     */
    void onChartSlowSlide(int direction, int startPosition, int endPosition);
    /**
     * 长按后滑动
     * @param position 当前位置
     */
    void onChartSlideLongClick(int position);
}
