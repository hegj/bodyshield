package com.cmax.bodysheild.chart;

import com.cmax.bodysheild.util.CommonUtil;
import com.cmax.bodysheild.util.Constant;
import com.cmax.bodysheild.util.SharedPreferencesUtil;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by hegj on 2015/10/29.
 *
 * 温度图表x轴格式
 */
public class TemperatureChartYAxisValueFormatter implements YAxisValueFormatter {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        String temperatureVal = "";
        if(0 == SharedPreferencesUtil.getIntValue(Constant.KEY_UNIT, 0)){
            temperatureVal = (int)value + "℃";
        }else {
            float fahrenheitVal = CommonUtil.centigradeToFahrenheit(value);
            temperatureVal = DECIMAL_FORMAT.format(fahrenheitVal) + "℉";
        }
        return temperatureVal;
    }
}
