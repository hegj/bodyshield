package com.cmax.bodysheild.chart;

import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hegj on 2015/10/29.
 *
 * 温度图表x轴格式
 */
public class TemperatureChartXAxisValueFormatter implements XAxisValueFormatter {

    private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat FORMAT2 = new SimpleDateFormat("HH:mm");
    private static final String S = "20151029";

    @Override
    public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
        String result = "";
        long f = Long.parseLong(original);
        try {
            Date date1 = FORMAT1.parse(S);
            long t = date1.getTime() + (f * 1000L);
            Date date2 = new Date(t);
            result = FORMAT2.format(date2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }



}
