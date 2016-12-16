package com.cmax.bodysheild.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmax.bodysheild.R;
import com.cmax.bodysheild.listeners.BelowMenuPopupWindowListener;
import com.cmax.bodysheild.util.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/16 0016.
 */

public class ChoosePopupWindow {
    @Bind(R.id.tv_menu_one)
    Button tvMenuOne;
    @Bind(R.id.tv_menu_fifth)
    TextView tvMenuFifth;
    @Bind(R.id.tv_menu_four_left)
    TextView tvMenuFourLeft;
    @Bind(R.id.tv_menu_four_right)
    TextView tvMenuFourRight;
    @Bind(R.id.rl_four)
    RelativeLayout rlFour;
    @Bind(R.id.tv_menu_third_left)
    public  TextView tvMenuThirdLeft;
    @Bind(R.id.tv_menu_third_right)
    TextView tvMenuThirdRight;
    @Bind(R.id.rl_third)
    RelativeLayout rlThird;
    @Bind(R.id.tv_menu_two_left)
  public   TextView tvMenuTwoLeft;
    @Bind(R.id.tv_menu_two_right)
    TextView tvMenuTwoRight;
    @Bind(R.id.rl_two)
    RelativeLayout rlTwo;
    @Bind(R.id.ll_menu_fifth)
    LinearLayout llMenuFifth;
    private Activity activity;
    private PopupWindow popupWin;
    private BelowMenuPopupWindowListener belowMenuPopupWindowListener;
    private RelativeLayout popupView;

    public ChoosePopupWindow(Activity activity) {
        this.activity = activity;


    }


    private void initView() {
        popupView = (RelativeLayout) View.inflate(UIUtils.getContext(), R.layout.popupwindow_below_menu, null);
        ButterKnife.bind(this, popupView);
        popupWin = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWin.setOutsideTouchable(true);
        popupWin.setFocusable(true);
        popupWin.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tvMenuOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                belowMenuPopupWindowListener.menuItemClickByType(BelowMenuPopupWindowListener.TYPE_1);
            }
        });
        rlThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                belowMenuPopupWindowListener.menuItemClickByType(BelowMenuPopupWindowListener.TYPE_3);
            }
        });
        rlTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                belowMenuPopupWindowListener.menuItemClickByType(BelowMenuPopupWindowListener.TYPE_2);
            }
        });
        if (!popupWin.isShowing()) {
            popupWin.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }
        initEvent();
    }

    private void initEvent() {

    }

    public void setBelowMenuPopupWindowListener(BelowMenuPopupWindowListener listener) {
        this.belowMenuPopupWindowListener = listener;
    }

    public void showMenuPopupwindow() {
        if (popupWin != null) {
            if (!popupWin.isShowing()) {
                popupWin.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }
            return;
        }
        initView();
    }

    public void setMenuLeftText( String twoText, String third,String fourText) {

        tvMenuTwoLeft.setText(twoText);
        tvMenuThirdLeft.setText(third);
        tvMenuFourLeft.setText(fourText);
        llMenuFifth.setVisibility(View.GONE);
        tvMenuOne.setVisibility(View.GONE);
    }

    public void setMenuLeftText(String oneText, String twoText) {
        tvMenuOne.setText(oneText);
        tvMenuTwoLeft.setText(twoText);
        tvMenuThirdLeft.setVisibility(View.GONE);
        tvMenuFourLeft.setVisibility(View.GONE);
    }

    public void setMenuTextGravity() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tvMenuTwoLeft.setLayoutParams(params);
        tvMenuThirdLeft.setLayoutParams(params);
        tvMenuFourLeft.setLayoutParams(params);
        rlTwo.setBackgroundResource(R.drawable.selector_choose_sex);
        rlThird.setBackgroundResource(R.drawable.selector_choose_sex);

    }

    public void setMenuRightText(String twoText, String third, String four) {
        tvMenuTwoRight.setText(twoText);
        tvMenuThirdRight.setText(third);
        tvMenuFourRight.setText(four);
    }

    public void dismiss() {
        if (popupWin != null) {
            if (isShowing()) {
                popupWin.dismiss();
            }
        }
    }

    public boolean isShowing() {
        return popupWin.isShowing();
    }


}
