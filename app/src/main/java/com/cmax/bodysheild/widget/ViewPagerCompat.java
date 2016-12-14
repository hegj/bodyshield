package com.cmax.bodysheild.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/26 0026.
 */
public class ViewPagerCompat extends ViewPager {
    private boolean mViewTouchMode = false;
    private List data;
    private int position;
    private float down_x;
    private boolean isSlideShow;
    private Map<Integer ,Integer > map;
    private int currentPage;
    private int normalHeight;

    public void setMeasure(boolean measure) {
        isMeasure = measure;
    }

    private boolean isMeasure=false;
    //mViewTouchMode表示ViewPager是否全权控制滑动事件，默认为false，即不控制 private boolean mViewTouchMode = false;

    public ViewPagerCompat(Context context, AttributeSet attrs) {

        super(context, attrs);
        map=new HashMap();
    }
    // 直接控制
    public void setViewTouchMode(boolean b) {

        if (b && !isFakeDragging()) {

//全权控制滑动事件

            beginFakeDrag();

        } else if (!b && isFakeDragging()) {

//终止控制滑动事件

            endFakeDrag();

        }

        mViewTouchMode = b;

    }
    // 需要条件控制
    public void setViewTouchMode(boolean b,List list, int position,boolean isSlideShow) {



        mViewTouchMode = b;

        this.position = position;

        Logger.d(isSlideShow+"isslide");
    }
    /**
     * 在mViewTouchMode为true的时候，ViewPager不拦截点击事件，点击事件将由子View处理
     */

    @Override

    public boolean onInterceptTouchEvent(MotionEvent event) {

        if (mViewTouchMode && data!=null&&data.size()-1==position) {
               switch (event.getAction()){
                   case MotionEvent.ACTION_DOWN:
                       down_x = event.getX();
                       break;
                   case MotionEvent.ACTION_MOVE:
                       float move_x = event.getX();
                       if (move_x<down_x){
                           // 左
                         //  beginFakeDrag();
                           return false ;
                           //return  super.onInterceptTouchEvent(event);
                       }else {
                           if (isSlideShow){
                               return false ;
                           }

                        //   endFakeDrag();
                           return true;
                       }

                   case MotionEvent.ACTION_UP:
                       break;
               }
            return super.onInterceptTouchEvent(event);
        }
        if (mViewTouchMode)
            return  false;

        return super.onInterceptTouchEvent(event);

    }

    @Override

    public boolean onTouchEvent(MotionEvent ev) {

        try {

            return super.onTouchEvent(ev);

        } catch (Exception e) {

            return false;

        }

    }

    /**
     * 在mViewTouchMode为true或者滑动方向不是左右的时候，ViewPager将放弃控制点击事件，
     * 这样做有利于在ViewPager中加入ListView等可以滑动的控件，否则两者之间的滑动将会有冲突
     */
    @Override
    public boolean arrowScroll(int direction) {
        if (mViewTouchMode) return false;
        if (direction != FOCUS_LEFT && direction != FOCUS_RIGHT) return false;
        return super.arrowScroll(direction);
    }

    public void setViewTouchSource(List pageList) {
        this.data = pageList;

    }

    public void setViewPageTouchIsSlide(boolean isSlideShow) {
        this.isSlideShow=isSlideShow;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isMeasure) {
            int height = 0;
            if (map.size() > 0 && map.containsKey(currentPage)) {
                height = map.get(currentPage);
            }else {
                height=normalHeight;
            }
            //得到ViewPager的MeasureSpec，使用固定值和MeasureSpec.EXACTLY，
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 在切换tab的时候，重置ViewPager的高度
     * @param current
     */
    public void resetHeight(int current){
        this.currentPage=current;
        MarginLayoutParams params= (MarginLayoutParams) getLayoutParams();
        if(map.size()>0 && map.containsKey(currentPage)){
            if(params==null){
                params=new MarginLayoutParams(LayoutParams.MATCH_PARENT,map.get(current));
            }else {
                params.height=map.get(current);
            }
            setLayoutParams(params);
        }
    }
    public  void setNormalHeight(int height){
        normalHeight=height;
    }
    /**
     * 获取、存储每一个tab的高度，在需要的时候显示存储的高度
     * @param current  tab的position
     * @param height   当前tab的高度
     */
    public void addHeight(int current,int height){
        map.put(current,height);
    }
    public Map<Integer ,Integer > getMap(){
        return map;
    }
}