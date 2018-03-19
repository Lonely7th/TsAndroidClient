这篇文章主要介绍如何使用MPAndroidChart库实现K线面板的相关功能，我们会着重介绍以下几个方面：

**1.绘制K线图和展示股票数据**

**2.处理用户手势操作**

**3.拉伸和压缩K线图**

**最终的效果图：**

![图1](http://upload-images.jianshu.io/upload_images/9225319-72948e24ff8a3644?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**源码下载地址：[https://github.com/Lonely7th/TsAndroidClient](https://github.com/Lonely7th/TsAndroidClient)**

# 绘制K线图和展示股票数据

绘制K线图需要用到真实的股票数据，网上有很多免费的接口可以使用，我们也可以自己编写一个股票数据接口 [Python实现股票数据接口](https://www.jianshu.com/p/5bb04e7c4e5e)

#### 1.将MPAndroidChart集成到项目中

在project根目录的build.gradle添加中央库地址：

```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
在项目build.gradle中添加相关依赖:
```
dependencies {
    compile 'com.github.PhilJay:MPAndroidChart:v2.2.4'
}
```
#### 2.在布局文件中添加CandleStickChart
```
<com.github.mikephil.charting.charts.CandleStickChart
        android:id="@+id/candler_chart"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@+id/top_line"
        android:layout_above="@+id/rl_bottom_view"/>
```
```
mChart = findViewById(R.id.candler_chart);
```
#### 3.基本参数设置
设置提示文字
```
mChart.setNoDataTextDescription("加载中...");//如果没有数据的时候，会显示这个
```

设置背景颜色
```
mChart.setDrawGridBackground(false);//是否显示表格颜色
mChart.setBackgroundColor(Color.BLACK);//设置背景
mChart.setGridBackgroundColor(Color.BLACK);//设置表格背景色
```

设置坐标轴，坐标轴分为x轴、左y轴和右y轴，可以分别设置，这里我们取消x轴和右y轴，只设置左y轴的属性。
```
//设置x轴
XAxis xAxis = mChart.getXAxis();
xAxis.setEnabled(false);
//设置y轴(左边)
YAxis leftAxis = mChart.getAxisLeft();
leftAxis.setEnabled(true);
leftAxis.setLabelCount(5, false);
leftAxis.setDrawGridLines(true);//绘制网格线
leftAxis.setDrawAxisLine(false);
leftAxis.setGridColor(ContextCompat.getColor(MainActivity.this, R.color.gray_overlay));//设置网格线的颜色
leftAxis.setTextColor(Color.WHITE);//坐标轴文字颜色
leftAxis.setValueFormatter(new MyYAxisValueFormatter());//坐标轴文字格式
//设置y轴(右边)
YAxis rightAxis = mChart.getAxisRight();
rightAxis.setEnabled(false);
```

设置高亮
```
mChart.setHighlightPerDragEnabled(false);//直接拖动屏幕时不显示高亮
mChart.setHighlightPerTapEnabled(false);//点击屏幕时不显示高亮
```

设置监听（在第二个章节中我们会详细介绍如何使用这些监听实现各种手势操作，这里不再赘述）
```
mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                if (isLongPressed) {
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });
```

```
mChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
               
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
```
#### 4.获取数据/添加数据
这里我们通过  [http://47.95.243.173/tkdata?code=000001](http://47.95.243.173/tkdata?code=000001)接口获取数据，接口的实现过程请点击  [Python实现股票数据接口](https://www.jianshu.com/p/5bb04e7c4e5e)
```
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
                            ToastUtils.makeToast(MainActivity.this, "加载失败，请检查网络");
                        }
                    }
```
获取到数据后我们创建一个CandleDataSet对象，CandleDataSet需要传入一个List<CandleEntry>类型的参数，这个参数就是待展示的股票数据
```
CandleEntry ce = new CandleEntry(i-start_index, shadowH, shadowL, open, close);
yVals.add(ce);
```
```
candleDataSet = new CandleDataSet(yVals, "");
candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
candleDataSet.setShadowColor(Color.DKGRAY);//影线颜色
candleDataSet.setShadowColorSameAsCandle(true);//影线颜色与实体一致
candleDataSet.setShadowWidth(0.7f);//影线candleDataSet.setDecreasingColor(ContextCompat.getColor(MainActivity.this, R.color.blue_overlay));//下跌的颜色
candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);//红涨，实体
candleDataSet.setIncreasingColor(Color.RED);//上涨的颜色
candleDataSet.setIncreasingPaintStyle(Paint.Style.STROKE);//绿跌，空心
candleDataSet.setNeutralColor(Color.RED);//当天价格不涨不跌（一字线）颜色
candleDataSet.setHighlightLineWidth(0.5f);//选中蜡烛时的线宽 candleDataSet.setDrawValues(false);//在图表中的元素上面是否显示数值
candleDataSet.setHighLightColor(ContextCompat.getColor(MainActivity.this, R.color.y_page_bg));//高亮的颜色
CandleData candleData = new CandleData(xVals, candleDataSet);
```
最后我们将candleDataSet绑定到CandleStickChart
```
mChart.setData(candleData);
```
到此为止，我们已经使用MPAndroidChart完成了一个静态的K线面板，下个章节我们将介绍如何为[K线面板添加手势操作](https://www.jianshu.com/p/692f577f99b4)

******

上个章节我们使用MPAndroidChart完成了一个静态的K线面板，这个章节我们将介绍如何在K线面板中处理用户的手势操作。
## 处理用户的手势操作

**在K线面板中，需要处理的手势操作主要包括：**

**1.单次点击（加载失败时重新加载）**

**2.匀速滑动（K线图左右滚动）**

**3.快速滑动（前后切换股票）**

**4.长按后滑动（高亮显示被选中的数据）**

如果你对上述手势操作不是太了解，可以下载apk体验一下  [apk下载地址](https://www.pgyer.com/Z8lM)

### 1.设计思路
写代码之前我们首先来谈一下设计思路，上述的手势操作并不复杂，但是结合到一起容易引起重叠，造成不好的用户体验。所以这次我们的设计思路是：
>首先判断长按事件，在长按状态下被选中数据高亮显示；
>在非长按状态下，通过判断滑动速率和滑动时间区分快速滑动和匀速滑动；
>在匀速滑动状态下，每滑动一个单位都重新获取手指的位置。
### 2.继承CandleStickChart类
开始编写代码，我们首先继承CandleStickChart类，CandleStickChart是MPAndroidChart提供的用于展示K线图的控件，在上个章节实现静态K线图的时候我们有用到：
```
public class TsCandleStickChart extends CandleStickChart {
    public TsCandleStickChart(Context context) {
        super(context);
    }

    public TsCandleStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TsCandleStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
```
创建OnTsGestureListener接口：
```
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
```
### 3.长按后滑动
按照最初的构思，我们先区分将长按事件跟非长按事件，这里我们复写setOnChartGestureListener，在onChartLongPressed中处理长按事件，其中变量isLongPressed用来记录当前是否处于长按状态： 
```
@Override
public void onChartLongPressed(MotionEvent me) {
    //为了避免滑动事件与高亮时间冲突，高亮事件只在长按后显示，优先消费滑动事件
    if (me.getFlags() == 0) {
        isLongPressed = true;
        setHighlightPerDragEnabled(true);
        highlightValue(getHighlightByTouchPoint(me.getX(), me.getY()).getXIndex(),0);
    }
}
```
接着我们复写setOnChartValueSelectedListener来获取当前被选中的数据，当前处于长按状态时，将被选中的数据高亮显示：
```
setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (isLongPressed && onTsGestureListener != null) {
            onTsGestureListener.onChartSlideLongClick(e.getXIndex());
        }
    }
});
```
### 4.快速滑动
在非长按状态下，我们用滑动时间和滑动速率来区分快速滑动和匀速滑动：
```
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
```
### 5.匀速滑动
这里使用setOnTouchListener实现匀速滑动，对于onTouchListener想必大家都比较熟悉，手指按下时记录点击的位置：
```
case MotionEvent.ACTION_DOWN:
    currentDownindex = motionEvent.getRawX();
    break;
```
每当用户滑动的距离达到默认值时，我们会通知接口用户滑动了一个单位。
实际操作中用户滑动的距离可能会超过默认值，超过的情况也统一按照滑动了一个单位进行处理，这样可以使得滑动过程更加平顺：
```
case MotionEvent.ACTION_MOVE:
    if(!isLongPressed && onTsGestureListener != null){
      if((motionEvent.getEventTime() - motionEvent.getDownTime()) > SLIDE_PERIOD){
      float rawX = motionEvent.getRawX();
      int distance = (int)(rawX - currentDownindex);
      if(Math.abs(distance) > INSERTDATAPIXELS){
          currentDownindex = rawX;
          int num = distance/INSERTDATAPIXELS;
          int change = num > 0?1:-1;
          onTsGestureListener.onChartSlowSlide(change);
      }
    }
}
break;
```
编写完TsCandleStickChart类后，我们在布局中使用TsCandleStickChart代替之前的CandleStickChart：
```
<com.system.ts.android.utils.view.TsCandleStickChart
    android:id="@+id/ts_candler_chart"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_below="@+id/top_line"
    android:layout_above="@+id/rl_bottom_view" />
```
最后，在Activity中调用setOnTsGestureListener方法实现上述的手势操作：
```
@Bind(R.id.ts_candler_chart)
TsCandleStickChart mChart;
```
```
mChart.setOnTsGestureListener(new OnTsGestureListener() {
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
```
到此为止，我们已经实现了K线面板中的各种手势操作，下个章节 [MPAndroidChart实现K线面板（三）](https://www.jianshu.com/p/692f577f99b4)
******

这一节我们会介绍K线面板中其它细节的实现方式和可能遇到的问题。

**1.刷新当前展示的数据**

**2.自定义y轴的样式**

**3.拉伸和压缩K线图**

源码下载地址：[https://github.com/Lonely7th/TsAndroidClient](https://github.com/Lonely7th/TsAndroidClient)

# 1.刷新当前展示的数据
上一节我们谈到了如何处理用户的手势操作，处理的结果最终都要体现在K线图中，CandleStickChart已经为我们实现了很多功能，我们只需要更新List<CandleEntry>中的内容就可以达到刷新页面的目的，这里我们定义数据起点、数据终点和数据展示量三个变量，通过这三个变量来更新列表的内容。
刷新数据的起点和终点，每次执行滑动操作时都会调用该方法：
```
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
```
更新数据列表，数据起点、数据终点和数据展示量有变动时会调用该方法：
```
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

```
# 2.自定义y轴的样式
CandleStickChart提供了很多方法用于定义y轴的样式，包括自定义网格线、文字颜色、文字格式等等。
CandleStickChart会自动计算当前数据的最大值和最小值作为绘图的上下边界，但是这个边界并不会随着展示数据的变动而改变，所以这里我们需要每次都重新设置y轴的边界值：
```
float[] yAxisArray = updateDrawData();
YAxis leftAxis = mChart.getAxisLeft();
leftAxis.setAxisMaxValue(yAxisArray[0]);
leftAxis.setAxisMinValue(yAxisArray[1]);
```
CandleStickChart会根据数据的最大值和最小值计算坐标点，这里我们自定义坐标点的文字样式，让这些文字以浮点数展示：
```
leftAxis.setValueFormatter(new MyYAxisValueFormatter());
```
```
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

```
# 3.拉伸和压缩K线图
拉伸和压缩K线图也是K线面板中不可缺少的功能，这里我们通过修改当前的数据展示量来实现拉伸和压缩的效果：
```
switch (view.getId()) {
        case R.id.btn_bar_min://页面压缩
            if (currentShowCount > SCREEN_DATA_MIN) {
                currentShowCount -= SCREEN_DATA_CHANGE;
                onScaleUI();
            }
            break;
        case R.id.btn_bar_max://页面拉伸
            if (currentShowCount < SCREEN_DATA_MAX) {
                currentShowCount += SCREEN_DATA_CHANGE;
                onScaleUI();
            }
            break;
    }
```
```
private void onScaleUI() {
    updateDataIndex(0);
    ...
    mChart.highlightValues(null);
    mChart.notifyDataSetChanged();
    mChart.invalidate();
}
```
缓存当前展示的股票代码，我们将当前展示的股票代码存入缓存，便于下次打开时使用：
```
//缓存当前展示的证券代码
SharedPreferencesUtils.setCurrentTkCode(tkCode);
```
```
public static void setCurrentTkCode(String tkCode){
    SharedPreferences.Editor editor = TsApplication.sf.edit();
    editor.putString("cur_tk_code", tkCode);
    editor.commit();
}
```
如果文章中有描述错误的地方，请与我联系：1003882179@qq.com
