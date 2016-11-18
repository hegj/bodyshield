package com.cmax.bodysheild.activity.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.chart.TemperatureChartXAxisValueFormatter;
import com.cmax.bodysheild.chart.TemperatureChartYAxisValueFormatter;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;


public class TemperatureChartFragment extends Fragment implements OnChartValueSelectedListener {
    private static final String TAG = TemperatureChartFragment.class.getSimpleName();
    private LineChart chart;
    private Typeface tf = Typeface.DEFAULT;
    private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
    private static final SimpleDateFormat minutesFormat = new SimpleDateFormat("mm", Locale.getDefault());
    private Handler handler  = new Handler();
    private boolean isRegister = false;
    private int start = 0;
    private int end = 300;
    private static final int visibleRange = 180;

//    private static final  floa

//    public TemperatureChartFragment() {
//        // Required empty public constructor
//    }
public static TemperatureInfoImgFragment newInstance() {
    TemperatureInfoImgFragment fragment = new TemperatureInfoImgFragment();
    return fragment;
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_temperature_chart, container, false);
        ButterKnife.bind(this, view);
        chart = (LineChart)view.findViewById(R.id.temperatureChart);
        initChart();
        handler.post(myRunnable);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        chart.clear();
//        initChart();
        initYAxis();
//        handler.post(myRunnable);
//        if(!isRegister){
//            final IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(BluetoothService.ACTION_REALTIME_TEMPRETURE);
//            getActivity().registerReceiver(notificationReceiver, intentFilter);
//            isRegister = true;
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        handler.removeCallbacks(myRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 初始化图表
     */
    private  void initChart(){

        chart.clear();
        chart.setOnChartValueSelectedListener(this);

        chart.setDescription("");       //描述
        chart.setNoDataTextDescription("");    //没有数据时在图表上显示的文字
        chart.setTouchEnabled(true);    //是否可以点击
        chart.setDragEnabled(false);    // 是否可以拖拽
        chart.setScaleEnabled(false);  // 是否可以缩放 x和y轴, 默认是true
        chart.setDrawGridBackground(false); //是否设置表格的背景
        chart.setPinchZoom(true);   //设置x轴和y轴能否同时缩放。默认是否
//        chart.setBackgroundColor(ColorTemplate.getHoloBlue()); //设置背景颜色
        chart.setDrawBorders(true);
        chart.setBorderWidth(2f);
        chart.setBorderColor(Color.WHITE);
        chart.setHighlightEnabled(true);
        chart.setHighlightPerDragEnabled(true); //是否可以拖动高亮线
        chart.setDrawMarkerViews(true);
        /*初始化x轴数据*/
        chart.setData(initLineData());
        chart.setVisibleXRangeMaximum(visibleRange);  //一个界面显示多少个点，其他点可以通过滑动看到（X轴）
//        chart.setVisibleXRangeMinimum(3);  //一个界面最少显示多少个点，放大后最多 放大到 剩多少 个点
        initLegend();
        initXAxis();
//        initYAxis();

    }

    /**
     * 初始化图例
     */
    private void initLegend(){
        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();   // 设置坐标线描述 的样式

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(tf);
        l.setTextColor(Color.WHITE);
        l.setEnabled(false);    //设置图例不显示

//        l.setEnabled(true);
//        l.setTextColor(Color.rgb(255, 255, 255));// 坐标线描述文字的颜色
//        l.setTextSize(15);// 坐标线描述文字的大小，单位dp
////      l.setTypeface(mTf);// 坐标线描述文字的字体
//
//        l.setWordWrapEnabled(true); //坐标线描述 是否 不允许出边界
//        l.setMaxSizePercent(0.95f);   //坐标线描述 占据的大小x%  默认0.95 即95%
//
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);//位置 RIGHT_OF_CHART, RIGHT_OF_CHART_CENTER,
//        // RIGHT_OF_CHART_INSIDE, BELOW_CHART_LEFT, BELOW_CHART_RIGHT, BELOW_CHART_CENTER or PIECHART_CENTER (PieChart only), ... and more
//        l.setForm(Legend.LegendForm.CIRCLE);// 样式SQUARE, CIRCLE or LINE.
//        l.setFormSize(10f);// 大小 单位dp
//
//        l.setXEntrySpace(20f);// 条目的水平间距
//        l.setYEntrySpace(20f);// 条目的垂直间距
//        l.setFormToTextSpace(20f);//Sets the space between the legend-label and the corresponding legend-form.


    }

    /**
     * 初始化x轴
     */
    private void initXAxis(){
        XAxis xl = chart.getXAxis();
        xl.setTypeface(tf);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true); //是否显示X坐标轴上的刻度竖线，默认是true
        xl.setGridColor(Color.WHITE);
        xl.setAvoidFirstLastClipping(true);
        xl.setValueFormatter(new TemperatureChartXAxisValueFormatter());
//        xl.setSpaceBetweenLabels(15);
        xl.setLabelsToSkip(59);    //设置坐标相隔多少，参数是int类型
//        xl.resetLabelsToSkip();   //将自动计算坐标相隔多少
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);//把坐标轴放在上下 参数有：TOP, BOTTOM, BOTH_SIDED, TOP_INSIDE or BOTTOM_INSIDE.
        xl.setEnabled(true);
    }

    /**
     * 初始化y轴
     */
    private void initYAxis(){
        float highFever = SharedPreferencesUtil.getFloatValue(Constant.KEY_HIGHT_FEVER,39);
        float lowFever = SharedPreferencesUtil.getFloatValue(Constant.KEY_LOW_FEVER,38);
        /*上面的界限线*/
        LimitLine upperLimit = new LimitLine(highFever,getString(R.string.hight_fever));
        upperLimit.setLineWidth(1f);
        upperLimit.enableDashedLine(20f, 20f, 0f);  //虚线
        upperLimit.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
        upperLimit.setTextSize(4f);
        upperLimit.setTypeface(tf);
        upperLimit.setLineColor(Color.RED);
        /*下面的界限线*/
        LimitLine lowerLimit = new LimitLine(lowFever,getString(R.string.low_fever));
        lowerLimit.setLineWidth(1f);
        lowerLimit.enableDashedLine(20f, 20f, 0f);
        lowerLimit.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        lowerLimit.setTextSize(4f);
        lowerLimit.setTypeface(tf);
        lowerLimit.setLineColor(Color.YELLOW);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tf);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaxValue(42f);  //设置Y轴最大值
        leftAxis.setAxisMinValue(34f);  //设置Y轴最小值
        leftAxis.setStartAtZero(false); //是否从0开始
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.WHITE);
        leftAxis.setLabelCount(8, false);    //显示的标签总数
        leftAxis.setValueFormatter(new TemperatureChartYAxisValueFormatter());
        leftAxis.setSpaceTop(50f);    //Y轴坐标距顶有多少距离，即留白
        leftAxis.setSpaceBottom(50f);    //Y轴坐标距底有多少距离，即留白
        leftAxis.removeAllLimitLines();     //移除所有界限线
        leftAxis.addLimitLine(upperLimit);
        leftAxis.addLimitLine(lowerLimit);
        leftAxis.setDrawLimitLinesBehindData(true); //limit lines are drawn behind data (and not on top)


        YAxis rightAxis = chart.getAxisRight();     //右边的Y轴
        rightAxis.setSpaceTop(50f);    //Y轴坐标距顶有多少距离，即留白
        rightAxis.setSpaceBottom(50f);    //Y轴坐标距底有多少距离，即留白
        rightAxis.setEnabled(false);                //禁用右边的Y轴
        chart.setVisibleYRangeMaximum(8f, YAxis.AxisDependency.LEFT);   //一个界面显示多少个点，其他点可以通过滑动看到（左边的Y轴）
        chart.moveViewToY(34f, YAxis.AxisDependency.LEFT);
    }



    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }



    /*控制图表x轴随时间移动的线程*/
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {

            LineData data = chart.getLineData();
            if(data != null){
                data.addXValue("" + end++);
//            if(end >= 86400){
//
////                data.clearValues();
////                chart.setData(initLineData());
//            }
            }
            chart.moveViewToX(parseIndex(new Date().getTime()) - 170);
            chart.setVisibleXRangeMaximum(visibleRange);
            handler.postDelayed(myRunnable, 1000);
        }
    };




    /**
     * 创建数据集
     * @return
     */
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.rgb(255, 254, 62));
        set.setCircleColor(Color.rgb(255, 254, 62));
        set.setLineWidth(2f);
        set.setCircleSize(3f);
        set.setFillAlpha(65);
        set.setFillColor(Color.rgb(255, 254, 62));
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

//    public void feedMultiple() {
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                for(int i = 0; i < 3000; i++) {
//
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            addEntry();
//                        }
//                    });
//
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }

    /**
     * 往图表中设值
     * @param temperature
     */
    public void setPoint(Temperature temperature){

        long timestamp = temperature.getTimestamp();
        float y = temperature.getValue();
        if(null == chart || y < 34){
            return;
        }
        int index = parseIndex(timestamp);
        LineData data = chart.getData();
        if (data != null) {
            LineDataSet set = data.getDataSetByIndex(0);    //取第一个数据集
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            Entry entry = new Entry(y,index);
            data.addEntry(entry, 0);
            // let the chart know it's data has changed
            chart.notifyDataSetChanged();
//            chart.invalidate();
//            chart.setVisibleYRangeMaximum(30, YAxis.AxisDependency.LEFT); //Sets the size of the area (range on the y-axis) that should be maximum visible at once.
        }

    }

    /**
     * 将时间戳转换成x轴的index
     * @param timestamp
     * @return index of xAxis
     */
    private int parseIndex(long timestamp){
        Date currentDate = new Date();
        String dateStr = FORMAT1.format(currentDate);
        int index = 0;
        try {
            Date today = FORMAT1.parse(dateStr);
            long tem = timestamp - today.getTime();
            index = (int)tem/1000-start;
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Log.i(TAG,"当前刻度："+index);
        return index;
    }

    private LineData initLineData(){
        Date currentTime = new Date();
//        String dateStr = FORMAT1.format(currentTime);
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        data.setHighlightEnabled(false);
        try {

//            SimpleDateFormat secondFormat = new SimpleDateFormat("ss", Locale.getDefault());

            String hourStr = hourFormat.format(currentTime);
            String minutesStr = minutesFormat.format(currentTime);
//            String secondStr = secondFormat.format(currentTime);

            int h = Integer.parseInt(hourStr);
            int m = Integer.parseInt(minutesStr);
//            int s = Integer.parseInt(secondStr);

            start = h*3600 + m*60 - 5*60;
            end = h*3600 + m*60 + 5*60;
            /*初始化x轴数据*/

            for(int i = start; i < end; i++){
                data.addXValue(""+i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
