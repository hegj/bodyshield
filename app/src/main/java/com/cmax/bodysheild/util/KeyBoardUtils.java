package com.cmax.bodysheild.util;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class KeyBoardUtils {
    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     * <p>
     * <p>
     * 输入框
     * 上下文
     */
    public static void closeKeybord(View view) {
        InputMethodManager imm = (InputMethodManager) UIUtils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


    }

    public static void closeKeybord(Activity activity) {
        InputMethodManager imm = (InputMethodManager) UIUtils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(),  0);
        }
    }

    public static void initEdittext(EditText mEditText, String str) {
        if (!TextUtils.isEmpty(str)) {
            mEditText.setText(str);
            mEditText.setSelection(str.length());
        }
        openKeybord(mEditText, UIUtils.getContext());
    }


    public static boolean isKeyBoardShow(Activity context, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive(editText)) {//因为是在fragment下，所以用了getView()获取view，也可以用findViewById（）来获取父控件
            editText.requestFocus();//强制获取焦点，不然getActivity().getCurrentFocus().getWindowToken()会报错
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            inputMethodManager.restartInput(editText);
            return true;
        }
        return false;

    }

    public static void copyTxt(Context context, String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast("内容为空");
            return;
        }
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        if (cm != null) {
            cm.setText(content);
            ToastUtils.showSuccessToast("已复制到剪贴板");
        } else {
            ToastUtils.showFailToast("内容复制失败，请重试");
        }

    }
}
