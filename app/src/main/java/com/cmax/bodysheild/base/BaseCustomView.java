
package com.cmax.bodysheild.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by hss01248 on 11/4/2015.
 */
public  abstract class BaseCustomView extends FrameLayout {
    protected ViewGroup rootView;

    public BaseCustomView(Context context) {
        this(context, null);
    }

    public BaseCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        this.addView(rootView);
        initData(context, attrs);
        initEvent(context);
    }

    protected abstract void initEvent(Context context);

    protected abstract void initData(Context context, AttributeSet attrs);


    protected abstract void initView();



}
