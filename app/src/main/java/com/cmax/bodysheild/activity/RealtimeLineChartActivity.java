package com.cmax.bodysheild.activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.bean.ble.Temperature;
import com.cmax.bodysheild.bluetooth.BluetoothService;
import com.cmax.bodysheild.bluetooth.response.temperature.PresentDataResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RealtimeLineChartActivity extends FragmentActivity  {


    private LineChart chart;
    private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private Handler handler  = new Handler();
    private boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);   //设置窗体全屏
        setContentView(R.layout.activity_realtime_linechart);
        chart = (LineChart)findViewById(R.id.temperatureChart);
        handler.post(myRunnable);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isRegister){
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PresentDataResponse.ACTION_REALTIME_TEMPRETURE);
            registerReceiver(notificationReceiver, intentFilter);
            isRegister = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isRegister){
            unregisterReceiver(notificationReceiver);
            isRegister = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*控制图表x轴随时间移动的线程*/
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            chart.moveViewToX(parseIndex(new Date().getTime())-540);
            handler.postDelayed(myRunnable, 2000);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.realtime, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionAdd: {
                addEntry();
                break;
            }
            case R.id.actionClear: {
//                chart.clearValues();
                Toast.makeText(this, "Chart cleared!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.actionFeedMultiple: {
//               feedMultiple();
                break;
            }
        }
        return true;
    }

    public void addEntry() {

        Intent intent = new Intent(PresentDataResponse.ACTION_REALTIME_TEMPRETURE);


        float y = (float) (Math.random() * 8)+34;
        Temperature temperature = new Temperature(new Date().getTime(),y,new byte[]{});
        intent.putExtra(PresentDataResponse.EXTRA_PRESENT_DATA, temperature);
        sendBroadcast(intent);
//            setPoint(new Date().getTime(),y);
    }

    /**
     * 创建数据集
     * @return
     */
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.LTGRAY);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.LTGRAY);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(true);
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
    private void setPoint(Temperature temperature){
        long timestamp = temperature.getTimestamp();
        float y = temperature.getValue();
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
            index = (int)tem/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return index;
    }

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (PresentDataResponse.ACTION_REALTIME_TEMPRETURE.equals(action)) {

                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE);

                Temperature receiverTemperature = PresentDataResponse.getResponseResult(intent);
                setPoint(receiverTemperature);
            }
        }
    };
}
