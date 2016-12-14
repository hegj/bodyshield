package com.cmax.bodysheild.bean;

import android.app.Activity;

import com.qxinli.newpack.mytoppack.listview.MyRclViewHolder;
import com.qxinli.newpack.mytoppack.listview.MyViewHolder;

import java.util.Map;

/**
 * Created by Administrator on 2016/7/18 0018.
 */
public class BaseTabPageInfo {

    public Activity activity;
    public String category;
    public Class clazz;
    public String url;
    public String topCategoryId;

    // friends 使用
    public   String uid;
    public   String fansNumber;
    public   String followsNumber;

    // 我的咨询使用
    // 是否是我购买的 0 ,我接受的 1 我购买的

    public int isReceived;
    public MyRclViewHolder rclViewHolder;

    // new audio
    public String tag;

    // more
    public int moreType;
    public Map<String, String> map;
}
