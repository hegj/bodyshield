package com.cmax.bodysheild.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cmax.bodysheild.bean.HistoryData;
import com.cmax.bodysheild.bean.ble.Temperature;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by hegj on 2015/11/5.
 */
public class DBManager {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private static final SimpleDateFormat FORMAT2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private String tag = DBManager.class.getName();

    public DBManager(Context context){
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void addHistory(HistoryData historyData){
        if(!isExistHistory(historyData)){
            String sql = "INSERT INTO history_data values (null,?,?,?,?)";
            db.execSQL(sql,new String[]{historyData.getDeviceAddress(),historyData.getUserId(),String.valueOf(historyData.getTimestamp()),String.valueOf(historyData.getValue())});
            Log.i(tag, "成功添加一条历史数据！");
        }

    }

    public void batchAdd(List<HistoryData> historyDataList){
        db.beginTransaction();
        try{
            for(HistoryData data : historyDataList){
                addHistory(data);
            }
            db.setTransactionSuccessful();
        }finally {
            if(db != null){
                db.endTransaction();
            }
        }
    }

    public List<Temperature> getHistory(String date,String deviceAddress, String userId){
        List<Temperature> result = new ArrayList<Temperature>();
        Cursor c = null;
        try {
            long start = FORMAT1.parse(date).getTime();
            long end = start +86399999;
            String sql = "select * from history_data where deviceaddress='"+ deviceAddress +"' and userid='"+ userId +"' and timestamp BETWEEN " + start +" AND "+end +" order by timestamp ASC";
            c = db.rawQuery(sql,null);
            while (c.moveToNext()) {
                long timestamp = c.getLong(c.getColumnIndex("timestamp"));
                String value = c.getString(c.getColumnIndex("value"));
                float y = Float.parseFloat(value);
                Temperature temperature = new Temperature(timestamp,y,new byte[]{});
                result.add(temperature);
            }
        }catch (Exception e){
            Log.i(tag,e.getMessage());
        }finally {
            if(c != null){
                c.close();
                c = null;
            }
        }
        return result;
    }

//    public void delete(String timestamp){
//        String sql = "DELETE FROM history_data where timestamp >= ?";
//        db.execSQL(sql,new String[]{timestamp});
//    }

    public boolean isExistHistory(HistoryData historyData){
        boolean isExist = false;
        Cursor c = null;
        String sql = "select * from history_data where deviceaddress=? and userid=? and timestamp=?";
        try {
            c = db.rawQuery(sql, new String[]{historyData.getDeviceAddress(), historyData.getUserId(), String.valueOf(historyData.getTimestamp())});
            if(c.moveToNext()){
                isExist = true;
                Log.i(tag,"已存在该历史数据！");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(c != null){
                c.close();
                c = null;
            }
        }
        return isExist;
    }

    public void close(){
        if (db != null){
            db.close();
        }
        if (dbHelper != null){
            dbHelper.close();
        }
    }

}
