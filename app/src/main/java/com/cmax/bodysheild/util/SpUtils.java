package com.cmax.bodysheild.util;

/**
 * Created by Administrator on 2017/1/18 0018.
 */

public class SPUtils {
    public static   void setTempertureHisoryRecordTime(long time,String address){
        SharedPreferencesUtil.setLongValue(Constant.KEY_TEMPERTURE_RECORD_TIME + address +UIUtils.getUserId(), time);
    }
    public static long getTempertureHisoryRecordTime(String address){
       return SharedPreferencesUtil.getLongValue(Constant.KEY_TEMPERTURE_RECORD_TIME +address +UIUtils.getUserId(), 0);
    }
    public static  void setTempertureToServerRecordTime(long time){
            SharedPreferencesUtil.setLongValue(Constant.KEY_TEMPERTURE_RECORD_NET_TIME , time);
    }  public static  long getTempertureToServerRecordTime(){
          return   SharedPreferencesUtil.getLongValue(Constant.KEY_TEMPERTURE_RECORD_NET_TIME , 0);
    }
}
