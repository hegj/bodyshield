package com.cmax.bodysheild.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.base.BaseActivity;
import com.cmax.bodysheild.base.view.IStateView;
import com.cmax.bodysheild.bean.HistoryData;
import com.cmax.bodysheild.bean.ble.BLEDevice;
import com.cmax.bodysheild.bean.ble.MemoryRecord;
import com.cmax.bodysheild.bean.ble.MemoryStatus;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.bean.cache.DeviceUser;
import com.cmax.bodysheild.bean.cache.User;
import com.cmax.bodysheild.bluetooth.BLEService;
import com.cmax.bodysheild.bluetooth.BluetoothManage;
import com.cmax.bodysheild.bluetooth.BluetoothService;
import com.cmax.bodysheild.bluetooth.command.temperature.ContinuousDataCommand;
import com.cmax.bodysheild.bluetooth.command.temperature.DataRequestCommand;
import com.cmax.bodysheild.bluetooth.response.temperature.MemoryRecordResponse;
import com.cmax.bodysheild.bluetooth.response.temperature.MemoryStatusResponse;
import com.cmax.bodysheild.chart.TemperatureChartXAxisValueFormatter;
import com.cmax.bodysheild.chart.TemperatureChartYAxisValueFormatter;
import com.cmax.bodysheild.dao.DBManager;
import com.cmax.bodysheild.http.HttpMethods;
import com.cmax.bodysheild.http.RxJavaHttpHelper;
import com.cmax.bodysheild.http.rxschedulers.RxSchedulersHelper;
import com.cmax.bodysheild.http.rxsubscriber.ProgressSubscriber;
import com.cmax.bodysheild.util.CommonUtil;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.DialogUtils;
import com.cmax.bodysheild.util.PortraitUtil;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.cmax.bodysheild.util.ToastUtils;
import com.cmax.bodysheild.util.UIUtils;
import com.cmax.bodysheild.widget.CircleImageView;
import com.cmax.bodysheild.widget.LoadingMask;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class TemperatureHistoryInfoActivity extends BaseActivity implements View.OnTouchListener,OnChartValueSelectedListener ,IStateView {

    public final static String EXTRA_DEVICE = "com.cmax.bodysheild.Temperature.EXTRA_DEVICE";
    private final static String TAG = TemperatureHistoryInfoActivity.class.getSimpleName();
    @Bind(R.id.showDate)
    TextView  et_showDate;
    @Bind(R.id.showTime)
    TextView  showTime;
    @Bind(R.id.userName)
    TextView  tv_userName;
    @Bind(R.id.temperature)
    TextView  tv_temperature;
    @Bind(R.id.synchronizedBtn)
    ImageView synchronizedBtn;
    @Bind(R.id.userImageBtn_history)
    CircleImageView userImageBtn;

    private LineChart chart;
    private              Typeface         tf          = Typeface.DEFAULT;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat FORMAT1     = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private static final SimpleDateFormat FORMAT2     = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final DecimalFormat df = new DecimalFormat("#.00");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00");
    private BLEDevice device;
    private DBManager dbManager;
    private BLEService                   bleService;
    private ServiceConnection            serviceConnection;
    private BluetoothService.LocalBinder bleBinder;
    private boolean isBind     = false;
    private boolean isRegister = false;
    private LineData xData;
//    private  int recordIndex = 1;
    private  int recordsCount = 0;
    private  String currentUserName = null;
    private long timeFlag = 1L;
    private LoadingMask mask;
    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
//            if (MemoryRecordResponse.ACTION_MEMORY_RECORD.equals(action)) {
//                MemoryRecord record = intent.getParcelableExtra(MemoryRecordResponse.EXTRA_MEMORY_RECORD);
//                if(record != null){
//                    String address = intent.getStringExtra(BluetoothService.EXTRA_ADDRESS);
//                    if (device.getAddress().equals(address)) {
//                        int recordIndex = record.getRecordIndex();
//                        if(record.getTimestamp() > timeFlag && recordIndex > 1) {
//                            Log.i(TAG, "缓存时间：" + timeFlag + "--记录时间： " + record.getTimestamp());
//                            HistoryData historyData = new HistoryData();
//                            historyData.setDeviceAddress(address);
//                            historyData.setTimestamp(record.getTimestamp());
//                            historyData.setUserId(currentUserName);
//                            historyData.setValue(record.getTempreature());
//                            dbManager.addHistory(historyData);
//                            if(recordsCount == recordIndex){
//                                SharedPreferencesUtil.setLongValue(Constant.KEY_TIME_FLAG,record.getTimestamp());
//                            }
//                            DataRequestCommand command = new DataRequestCommand(device.getAddress(), DataRequestCommand.ReqeustType.RecordWithIndex);
//                            command.setRecordIndex(recordIndex-1);
//                            bleService.executeCommand(command);
//                            Log.i(TAG, "发送的index：" + (recordIndex-1) + "####接收的index：" + record.getRecordIndex());
//
//                        }else{
//                            timeFlag =  SharedPreferencesUtil.getLongValue(Constant.KEY_TIME_FLAG, record.getTimestamp());
//                            initChartData(FORMAT1.format(new Date()));
//                            if(mask != null){
//                                mask.hide();
//                            }
//                            Log.i(TAG,"同步历史数据完成！");
//                        }
//                    }
//                }
//            }


//            if (MemoryStatusResponse.ACTION_MEMORY_STATUS.equals(action)) {
//                String address = intent.getStringExtra(BluetoothService.EXTRA_ADDRESS);
//                if (device.getAddress().equals(address)){
//                    MemoryStatus status = MemoryStatusResponse.getResponseResult(intent);
//                    if (status != null) {
//                        recordsCount = status.getRecordsCount();
//                        Log.i(TAG,"历史记录总数："+recordsCount);
//                        if(recordsCount > 0){
//
//
//                            DataRequestCommand command = new DataRequestCommand(device.getAddress(), DataRequestCommand.ReqeustType.RecordWithIndex);
//                            command.setRecordIndex(recordsCount);
//                            bleService.executeCommand(command);
//                        }else {
//                            if(mask != null){
//                                mask.hide();
//                            }
//                            ContinuousDataCommand continuousDataCommand = new ContinuousDataCommand(device.getAddress(), ContinuousDataCommand.ReqeustType.StartModel);
//                            bleService.executeCommand(continuousDataCommand);
//                        }
//                    }
//                }
//            }

            if (BluetoothManage.ACTION_GATT_DISCONNECTED.equals(action)) {
                //设备断开
                if(mask != null){
                    mask.hide();
                }
            }
        }
    };
    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_temperature_history_info;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        chart = (LineChart) findViewById(R.id.historyTemperatureChart);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        et_showDate.setOnTouchListener(this);
        et_showDate.setText(DATE_FORMAT.format(new Date()));
        showTime.setText(FORMAT2.format(new Date()));
        Bundle extras = getIntent().getExtras();
        device = extras.getParcelable(TemperatureInfoActivity.EXTRA_DEVICE);
        setCurrentUser();

        dbManager = new DBManager(this);
        initLineData();
        initChart();
    }

    @Override
    protected void initEvent(Bundle savedInstanceState) {
        super.initEvent(savedInstanceState);

        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                bleBinder = (BluetoothService.LocalBinder) service;
                bleService = bleBinder.getBLEService();
            }
        };

        bindService(new Intent(TemperatureHistoryInfoActivity.this, BluetoothService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        isBind = true;

        if (!isRegister) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MemoryRecordResponse.ACTION_MEMORY_RECORD);
            intentFilter.addAction(MemoryStatusResponse.ACTION_MEMORY_STATUS);
            registerReceiver(notificationReceiver, intentFilter);
            isRegister = true;
        }
        timeFlag = SharedPreferencesUtil.getLongValue(Constant.KEY_TIME_FLAG,1L);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentUser();
//        chart.clear();
        initYAxis();
        if (!isBind){
            bindService(new Intent(TemperatureHistoryInfoActivity.this, BluetoothService.class), serviceConnection, Context.BIND_AUTO_CREATE);
            isBind = true;
        }
        if (!isRegister) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MemoryRecordResponse.ACTION_MEMORY_RECORD);
            intentFilter.addAction(MemoryStatusResponse.ACTION_MEMORY_STATUS);
            registerReceiver(notificationReceiver, intentFilter);
            isRegister = true;
        }
        initChartData(FORMAT1.format(new Date()));
    }

    /**
     * 设置当前的用户
     */
    protected void setCurrentUser(){
        List<DeviceUser> deviceUsers = SharedPreferencesUtil.getList(Constant.DEVICE_USER_LIST, DeviceUser.class);
        boolean flag = false;
        for (DeviceUser deviceuser:deviceUsers) {
            if (deviceuser.getAddress().equalsIgnoreCase(device.getAddress())){
                currentUserName = deviceuser.getUserId();
                tv_userName.setText(deviceuser.getName());
                List<User> users = SharedPreferencesUtil.getList(Constant.USER_LIST,
                        User.class);
                for (User user:users) {
                    if (user.getId().equals(currentUserName)){
                        String userImage = user.getImage();
                        Bitmap bm = PortraitUtil.getBitmap(this, userImage);
                        userImageBtn.setImageBitmap(bm);
                        break;
                    }
                }
                flag = true;
                break;
            }
        }
        if(!flag){
            currentUserName = "";
            tv_userName.setText("");
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (isRegister) {
            unregisterReceiver(notificationReceiver);
            isRegister = false;
        }
        if(isBind) {
            unbindService(serviceConnection);
            isBind = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegister) {
            unregisterReceiver(notificationReceiver);
            isRegister = false;
        }
        if(isBind) {
            unbindService(serviceConnection);
            isBind = false;
        }
        if(dbManager != null){
            dbManager.close();
            dbManager = null;
        }

        if(chart != null){
            chart.clear();
            chart = null;
        }
        if (mask != null){
            mask.hide();
            mask.close();
            mask = null;
        }
    }

    @OnClick(R.id.backBtn)
    void backClick(){
//        ContinuousDataCommand continuousDataCommand = new ContinuousDataCommand(device.getAddress(), ContinuousDataCommand.ReqeustType.StartModel);
//        bleService.executeCommand(continuousDataCommand);
        finish();
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    String str = String.format("%d-%02d-%02d", year, month + 1, day);
                    String date = String.format("%d%02d%02d", year, month + 1, day);
                    et_showDate.setText(str);
                    initChartData(date);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH) ).show();

        }
        return true;
    }

    @OnTouch(R.id.showTime)
    public boolean timeTextViewTouch(View v, MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());


            new TimePickerDialog(TemperatureHistoryInfoActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hour, int minute) {
                            try {
                                String str = DECIMAL_FORMAT.format(hour) +":"+DECIMAL_FORMAT.format(minute)+":"+"00";
                                int xIndex = hour*60*60+minute*60;
                                showTime.setText(str);
                                chart.moveViewToX(xIndex);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), true).show();
        }
        return true;
    }


    @OnClick(R.id.settingBtn_history)
    public void setting(){
        Intent intent = new Intent(this,SettingActivity.class);
        intent.putExtra(TemperatureInfoActivity.EXTRA_DEVICE, device);
        startActivity(intent);
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
        chart.setDragEnabled(true);    // 是否可以拖拽
        chart.setScaleEnabled(false);  // 是否可以缩放 x和y轴, 默认是true
        chart.setScaleXEnabled(false);
        chart.setDrawGridBackground(false); //是否设置表格的背景
        chart.setPinchZoom(true);   //设置x轴和y轴能否同时缩放。默认是否
//        chart.setBackgroundColor(ColorTemplate.getHoloBlue()); //设置背景颜色
        chart.setDrawBorders(true);
        chart.setBorderWidth(2f);
        chart.setBorderColor(Color.WHITE);
        chart.setHighlightEnabled(true);
        chart.setHighlightPerDragEnabled(true); //是否可以拖动高亮线
        chart.setDrawMarkerViews(true);

        chart.setData(xData);
        chart.setVisibleXRangeMaximum(600);  //一个界面显示多少个点，其他点可以通过滑动看到（X轴）
        chart.setVisibleXRangeMinimum(60);  //一个界面最少显示多少个点，放大后最多 放大到 剩多少 个点
        initLegend();
        initXAxis();
//        initYAxis();
//        initChartData(FORMAT1.format(new Date()));

    }

    /**
     * 预初始化x轴刻度
     */
    private void initLineData(){
        xData = new LineData();
        xData.setValueTextColor(Color.WHITE);
        for(int i = 0; i<86399;i++){
            xData.addXValue(""+i);
        }
        xData.setHighlightEnabled(true);
    }

    /**
     * 初始化图例
     */
    private void initLegend(){
        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();   // 设置坐标线描述 的样式

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(tf);
//        l.setTextColor(Color.WHITE);
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
        upperLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upperLimit.setTextSize(4f);
        upperLimit.setTypeface(tf);
        upperLimit.setLineColor(Color.RED);
        /*下面的界限线*/
        LimitLine lowerLimit = new LimitLine(lowFever, getString(R.string.low_fever));
        lowerLimit.setLineWidth(1f);
        lowerLimit.enableDashedLine(20f, 20f, 0f);
        lowerLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
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
        int xIndex = e.getXIndex();
//        String time = FORMAT2.format(new Date(xIndex*1000));
        int hours = (xIndex % (60 * 60 * 24)) / ( 60 * 60);
        int minutes = (xIndex % (60 * 60)) / 60;
        int seconds = xIndex % 60;
        tv_temperature.setText(CommonUtil.getTemperature(e.getVal()));
        showTime.setText(DECIMAL_FORMAT.format(hours)+":"+DECIMAL_FORMAT.format(minutes)+":"+DECIMAL_FORMAT.format(seconds));
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
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
        set.setHighLightColor(Color.RED);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

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
            chart.notifyDataSetChanged();   //不加这句会造成数组越界
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
        Date currentDate = new Date(timestamp);
        String dateStr = FORMAT1.format(currentDate);
        int index = 0;
        try {
            Date today = FORMAT1.parse(dateStr);
            long tem = timestamp - today.getTime();
            index = (int)tem/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return index;
    }

    private void initChartData(String date){
        List<Temperature> temperatureList = dbManager.getHistory(date,device.getAddress(),currentUserName);
        chart.clearValues();
        for(Temperature temperature : temperatureList){
            setPoint(temperature);
        }
        chart.moveViewToX(parseIndex(new Date().getTime()) - 50);
//        chart.notifyDataSetChanged();
//        chart.invalidate();
    }
    @OnClick(R.id.synchronizedBtn)
    void synchronizedBtn(View v){
        long lastTimestamp = SharedPreferencesUtil.getLongValue(Constant.KEY_TEMPERTURE_RECORD_TIME + device.getAddress() +UIUtils.getUserId(), 0);
        HttpMethods.getInstance().apiService.downloadTemperature(device.getAddress(), UIUtils.getUserId()+"",lastTimestamp+"")
                .compose(RxJavaHttpHelper.<List<HistoryData>>handleResult())
                .compose( RxSchedulersHelper.<List<HistoryData>>applyIoTransformer())
                .subscribe(new ProgressSubscriber<List<HistoryData>>(this) {
            @Override
            public void _onError(String message) {
                ToastUtils.showFailToast(message);
            }

            @Override
            public void _onNext(List<HistoryData> data) {
                for (int i =0 ; i<data.size();i++){
                    HistoryData historyData = data.get(i);
                  /*  if (historyData.getUserId().equals(historyData.getUserId()) && historyData.getDeviceAddress() .equalsIgnoreCase( device.getAddress())){

                    }*/
                    dbManager.addHistory(historyData);
                }
                //TODO
                if (data.size()>0) {
                    SharedPreferencesUtil.setLongValue(Constant.KEY_TEMPERTURE_RECORD_TIME + device.getAddress() +UIUtils.getUserId(), data.get(0).getTimestamp());
                    initChartData(FORMAT1.format(new Date()));
                }
            }

            @Override
            public void _onCompleted() {


            }
        });
    }

    @Override
    public void showProgressDialog() {
        if (progressDialog==null)
            progressDialog = DialogUtils.showProgressDialog(this,UIUtils.getString(R.string.loading));
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog!=null &&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
