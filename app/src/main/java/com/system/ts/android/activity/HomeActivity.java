package com.system.ts.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.system.ts.android.R;
import com.system.ts.android.bean.TkDetailsBean;
import com.system.ts.android.http.HttpApi;
import com.system.ts.android.utils.MyVolumeFormatter;
import com.system.ts.android.utils.MyYAxisValueFormatter;
import com.system.ts.android.utils.SharedPreferencesUtils;
import com.system.ts.android.utils.TkCodeUtils;
import com.system.ts.android.utils.ToastUtils;
import com.system.ts.android.utils.view.OnTsGestureListener;
import com.system.ts.android.utils.view.TsCandleStickChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final int X_ANIMATE_PERIOD = 500;//X轴动画的时间间隔
    @Bind(R.id.tv_tk_title)
    TextView tvTkTitle;
    @Bind(R.id.tv_tk_code)
    TextView tvTkCode;
    @Bind(R.id.tv_close_price)
    TextView tvClosePrice;
    @Bind(R.id.tv_cur_time)
    TextView tvCurTime;
    @Bind(R.id.tv_max_price)
    TextView tvMaxPrice;
    @Bind(R.id.tv_min_price)
    TextView tvMinPrice;
    @Bind(R.id.tv_open_price)
    TextView tvOpenPrice;
    @Bind(R.id.tv_tk_range)
    TextView tvTkRange;
    @Bind(R.id.tv_total_volume)
    TextView tvTotalVolume;
    @Bind(R.id.tv_total_money)
    TextView tvTotalMoney;
    @Bind(R.id.btn_search)
    ImageView btnSearch;
    @Bind(R.id.tv_frist_time)
    TextView tvFristTime;
    @Bind(R.id.tv_last_time)
    TextView tvLastTime;
    @Bind(R.id.btn_bar_min)
    ImageView btnBarMin;
    @Bind(R.id.btn_bar_max)
    ImageView btnBarMax;
    @Bind(R.id.ts_candler_chart)
    TsCandleStickChart mChart;

    private CandleDataSet candleDataSet;
    private List<TkDetailsBean> tkData = new ArrayList<>();
    private List<CandleEntry> yVals = new ArrayList<>();
    private List<String> xVals = new ArrayList<>();

    private boolean isFristLoad = true;//是否第一次加载
    private boolean loadError = false;//当前状态是否为加载失败
    private boolean isLoading = false;//是否正在加载
    private int currentShowCount = 80;//当前展示的数据量
    private int currentStartIndex = 0;//当前数据的起点
    private int currentEndIndex = 0;//当前数据的终点

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initView();
        String tkCode = SharedPreferencesUtils.getCurrentTkCode();
        loadStickData(tkCode, true);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //基本设置
        mChart.setNoDataTextDescription("加载中...");//如果没有数据的时候，会显示这个
        mChart.setDrawGridBackground(false);//是否显示表格颜色
        mChart.setBackgroundColor(Color.BLACK);//设置背景
        mChart.setGridBackgroundColor(Color.BLACK);//设置表格背景色
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);//是否可以拖拽
        mChart.setScaleEnabled(true);//是否可以缩放
        mChart.setPinchZoom(false);
        mChart.setScaleYEnabled(false);
        mChart.setScaleXEnabled(false);
        mChart.animateX(X_ANIMATE_PERIOD); //立即执行的动画,x轴
        //设置x轴
        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);
        //设置y轴(左边)
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setLabelCount(5, false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setGridColor(ContextCompat.getColor(HomeActivity.this, R.color.gray_overlay));
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setValueFormatter(new MyYAxisValueFormatter());
        //设置y轴(右边)
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        //图标设置
        Legend l = mChart.getLegend();//设置比例图标示
        l.setEnabled(false);
        //设置监听器
        mChart.setOnTsGestureListener(new OnTsGestureListener() {
            @Override
            public void onChartSingleTapped() { //单次点击

            }

            @Override
            public void onChartDoubleTapped() { //多次点击
                if (loadError) {
                    String tkCode = SharedPreferencesUtils.getCurrentTkCode();
                    loadStickData(tkCode, true);
                }
            }

            @Override
            public void onChartFastSlide(int direction) { //快速滑动
                String code = TkCodeUtils.getNextCode(SharedPreferencesUtils.getCurrentTkCode(), direction);
                if (!TextUtils.isEmpty(code)) {
                    loadStickData(code, true);
                }
            }

            @Override
            public void onChartSlowSlide(int direction) { //匀速滑动
                onTranslateUI(direction);
            }

            @Override
            public void onChartSlideLongClick(int position) { //长按后滑动
                updateTopView(position);
            }
        });
    }

    /**
     * 刷新顶部View
     */
    private void updateTopView(int index) {
        if (index >= tkData.size()) {
            return;
        }
        TkDetailsBean bean = tkData.get(currentStartIndex + index);
        tvCurTime.setText(bean.getCur_timer());
        tvTotalMoney.setText(MyVolumeFormatter.moneyFormatter(bean.getCur_total_money()));
        tvTotalVolume.setText(MyVolumeFormatter.volumeFormatter(bean.getCur_total_volume()));
        tvClosePrice.setText(bean.getCur_close_price());
        tvOpenPrice.setText(bean.getCur_open_price());
        tvMaxPrice.setText(bean.getCur_max_price());
        tvMinPrice.setText(bean.getCur_min_price());
        tvTkRange.setText(bean.getCur_price_range() + "%");
        //更新字段的颜色值
        double lastClosePrice = Double.parseDouble(bean.getCur_close_price()) * (1 - Double.parseDouble(bean.getCur_price_range()) / 100);
        if (Double.parseDouble(bean.getCur_price_range()) >= 0) {
            tvTkRange.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.red_overlay));
        } else {
            tvTkRange.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.green_overlay));
        }
        if (Double.parseDouble(bean.getCur_close_price()) >= lastClosePrice) {
            tvClosePrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.red_overlay));
        } else {
            tvClosePrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.green_overlay));
        }
        if (Double.parseDouble(bean.getCur_open_price()) >= lastClosePrice) {
            tvOpenPrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.red_overlay));
        } else {
            tvOpenPrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.green_overlay));
        }
        if (Double.parseDouble(bean.getCur_max_price()) >= lastClosePrice) {
            tvMaxPrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.red_overlay));
        } else {
            tvMaxPrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.green_overlay));
        }
        if (Double.parseDouble(bean.getCur_min_price()) >= lastClosePrice) {
            tvMinPrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.red_overlay));
        } else {
            tvMinPrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.green_overlay));
        }
    }

    /**
     * 刷新底部View
     */
    private void updateBottomView() {
        if (currentStartIndex >= tkData.size() || currentEndIndex >= tkData.size()) {
            return;
        }
        tvFristTime.setText(tkData.get(currentStartIndex).getCur_timer());
        tvLastTime.setText(tkData.get(currentEndIndex).getCur_timer());
    }

    /**
     * 清空顶部View(刷新页面前调用)
     */
    private void clearTopView() {
        tvTkTitle.setText("--");
        tvTkCode.setText("--");
        tvCurTime.setText("--");
        tvTotalMoney.setText("--");
        tvTotalVolume.setText("--");
        tvClosePrice.setText("--");
        tvOpenPrice.setText("--");
        tvMaxPrice.setText("--");
        tvMinPrice.setText("--");
        tvTkRange.setText("--");
        tvTkRange.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.white_overlay));
        tvClosePrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.white_overlay));
        tvOpenPrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.white_overlay));
        tvMaxPrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.white_overlay));
        tvMinPrice.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.white_overlay));
        if (xVals != null && yVals != null) {
            xVals.clear();
            yVals.clear();
            mChart.highlightValues(null);//撤消所有高亮显示
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    /**
     * 加载数据
     *
     * @param tkCode 证券代码
     * @param reLoad 是否可以再次加载
     */
    private void loadStickData(final String tkCode, final boolean reLoad) {
        if (isLoading) {
            return;
        }
        clearTopView();//加载前清空顶部栏
        tvTkTitle.setText(TkCodeUtils.getNamebyCode(tkCode));
        tvTkCode.setText(tkCode);
        OkGo.get(HttpApi.BASE_URL).tag(this)
                .params("code", tkCode)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            Log.d(TAG, s);
                            JSONObject result = new JSONObject(s);
                            if (result.getInt("code") == 200) {
                                loadError = false;
                                if (tkData != null) {
                                    tkData.clear();
                                }
                                String data = result.getString("data");
                                JSONArray array = new JSONArray(data);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject item = (JSONObject) array.opt(array.length() - 1 - i);
                                    //解析基础数据
                                    TkDetailsBean bean = new TkDetailsBean(
                                            item.getString("cur_min_price"), item.getString("cur_close_price"),
                                            item.getString("cur_timer"), item.getString("cur_price_range"),
                                            item.getString("cur_max_price"), item.getString("cur_total_money"),
                                            item.getString("cur_total_volume"), item.getString("cur_open_price")
                                    );
                                    tkData.add(bean);
                                }
                                currentEndIndex = tkData.size() - 1;
                                updateDataIndex(0);
                                //解析绘图需要的数据
                                float[] yAxisArray = updateDrawData();
                                //每次都要重新设置坐标轴的极值
                                YAxis leftAxis = mChart.getAxisLeft();
                                leftAxis.setAxisMaxValue(yAxisArray[0]);
                                leftAxis.setAxisMinValue(yAxisArray[1]);
                                if (isFristLoad) {
                                    isFristLoad = false;
                                    candleDataSet = new CandleDataSet(yVals, "");
                                    candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                    candleDataSet.setShadowColor(Color.DKGRAY);//影线颜色
                                    candleDataSet.setShadowColorSameAsCandle(true);//影线颜色与实体一致
                                    candleDataSet.setShadowWidth(0.7f);//影线
                                    candleDataSet.setDecreasingColor(ContextCompat.getColor(HomeActivity.this, R.color.blue_overlay));//下跌的颜色
                                    candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);//红涨，实体
                                    candleDataSet.setIncreasingColor(Color.RED);//上涨的颜色
                                    candleDataSet.setIncreasingPaintStyle(Paint.Style.STROKE);//绿跌，空心
                                    candleDataSet.setNeutralColor(Color.RED);//当天价格不涨不跌（一字线）颜色
                                    candleDataSet.setHighlightLineWidth(0.5f);//选中蜡烛时的线宽
                                    candleDataSet.setDrawValues(false);//在图表中的元素上面是否显示数值
                                    candleDataSet.setHighLightColor(ContextCompat.getColor(HomeActivity.this, R.color.y_page_bg));//高亮的颜色
                                    CandleData candleData = new CandleData(xVals, candleDataSet);
                                    mChart.setData(candleData);
                                } else {
                                    mChart.highlightValues(null);//撤消所有高亮显示
                                    mChart.notifyDataSetChanged();
                                    mChart.invalidate();
                                }
                                updateTopView(currentEndIndex - currentStartIndex);
                                updateBottomView();
                                //缓存当前展示的证券代码
                                SharedPreferencesUtils.setCurrentTkCode(tkCode);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (reLoad) {
                            loadStickData(tkCode, false);//首次加载失败时再次加载
                        } else {
                            loadError = true;
                            SharedPreferencesUtils.setCurrentTkCode(tkCode);
                            ToastUtils.makeToast(HomeActivity.this, "加载失败，请检查网络");
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        isLoading = true;
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        isLoading = false;
                    }
                });
    }

    /**
     * 页面伸缩
     *
     * @param flags 伸长或者缩短的标志
     */
    private void onScaleUI(int flags) {
        switch (flags) {
            case 1://页面拉伸
                if (currentShowCount < 100) {
                    currentShowCount += 20;
                } else {
                    return;
                }
                break;
            case -1://页面压缩
                if (currentShowCount > 20) {
                    currentShowCount -= 20;
                } else {
                    return;
                }
                break;
        }
        //解析绘图需要的数据
        float[] yAxisArray = updateDrawData();
        //每次都要重新设置坐标轴的极值
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaxValue(yAxisArray[0]);
        leftAxis.setAxisMinValue(yAxisArray[1]);
        //刷新底部View
        updateBottomView();
        //刷新顶部View
        updateTopView(currentEndIndex - currentStartIndex);
        mChart.highlightValues(null);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    /**
     * 页面滑动
     */
    private void onTranslateUI(int direction) {
        if (updateDataIndex(direction)) {
            //解析绘图需要的数据
            float[] yAxisArray = updateDrawData();
            //每次都要重新设置坐标轴的极值
            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setAxisMaxValue(yAxisArray[0]);
            leftAxis.setAxisMinValue(yAxisArray[1]);
            //刷新底部View
            updateBottomView();
            //刷新顶部View
            updateTopView(currentEndIndex - currentStartIndex);
            mChart.highlightValues(null);
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    /**
     * 更新数据的起点和终点
     */
    private boolean updateDataIndex(int direction) {
        if (direction < 0 && currentEndIndex >= tkData.size() - 1) {//页面不能向左滑动
            return false;
        }
        if (direction > 0 && currentEndIndex <= currentShowCount) {//页面不能向右滑动
            return false;
        }
        currentEndIndex -= direction;
        if (currentShowCount < currentEndIndex) {
            currentStartIndex = currentEndIndex - currentShowCount;
        } else {
            int length = tkData.size() - 1;
            currentEndIndex = currentShowCount > length ? length : currentShowCount;
        }
        return true;
    }

    /**
     * 更新绘图所需要的数据(按照展示数量)
     * @return 当前数据的最大值和最小值
     */
    private float[] updateDrawData() {
        if (xVals != null && yVals != null) {
            xVals.clear();
            yVals.clear();
        }
        float yAxisMax = Float.MIN_VALUE;
        float yAxisMin = Float.MAX_VALUE;
        for (int i = currentStartIndex; i <= currentEndIndex; i++) {
            TkDetailsBean bean = tkData.get(i);
            float open = Float.parseFloat(bean.getCur_open_price());
            float close = Float.parseFloat(bean.getCur_close_price());
            float shadowH = Float.parseFloat(bean.getCur_max_price());
            float shadowL = Float.parseFloat(bean.getCur_min_price());
            CandleEntry ce = new CandleEntry(i - currentStartIndex, shadowH, shadowL, open, close);
            yVals.add(ce);
            xVals.add("" + (i - currentStartIndex));
            if (yAxisMax < shadowH) {
                yAxisMax = shadowH;
            }
            if (yAxisMin > shadowL) {
                yAxisMin = shadowL;
            }
        }
        return new float[]{yAxisMax, yAxisMin};
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            switch (requestCode) {
                case 1:
                    //处理搜索返回的结果
                    String tkCode = data.getStringExtra("code");
                    loadStickData(tkCode, true);
                    break;
            }
        }
    }

    @OnClick({R.id.btn_bar_min, R.id.btn_bar_max, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bar_min://页面压缩
                onScaleUI(-1);
                break;
            case R.id.btn_bar_max://页面拉伸
                onScaleUI(1);
                break;
            case R.id.btn_search://跳转到搜索页面
                startActivityForResult(new Intent(this, SearchTkActivity.class), 1);
                break;
        }
    }
}
