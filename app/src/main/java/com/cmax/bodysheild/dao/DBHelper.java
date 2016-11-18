package com.cmax.bodysheild.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cmax.bodysheild.util.GlobalConstants;

/**
 * Created by hegj on 2015/11/5.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int Dversion = 1;
    private static final String tag = "DBHelper";

    public DBHelper(Context context){
        super(context, GlobalConstants.DB_NAME, null, Dversion);
    }

    @Override
    //当首次创建数据库时被调用
    public void onCreate(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE IF NOT EXISTS history_data(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "deviceaddress text NOT NULL," +
                "userid text NOT NULL," +
                "timestamp INTEGER," +
                "value text" +
                ")";
        db.execSQL(sql);
        Log.i(tag,"创建数据库成功！");
    }

    @Override
    //当升级数据库时被调用
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i(tag,"数据库升级成功！");
        // TODO 升级数据库时的操作
    }

}
