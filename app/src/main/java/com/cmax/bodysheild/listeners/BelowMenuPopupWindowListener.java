package com.cmax.bodysheild.listeners;

/**
 * Created by Administrator on 2016/9/22 0022.
 */
public interface BelowMenuPopupWindowListener {
     // type 从下往上 标记item
    int TYPE_1=1;
    int TYPE_2=2;
    int TYPE_3=3;
      void menuItemClickByType(int type);
}
