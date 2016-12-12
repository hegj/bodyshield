package com.cmax.bodysheild.util;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmax.bodysheild.R;

/**
 * Created by Administrator on 2016/12/10 0010.
 */

public class ToastUtils {

    private static Toast mToast;

    public ToastUtils() {
    }

    public static void showToast(String text) {
        showToast(UIUtils.getContext(), text, false, UIUtils.getMainThreadHandler());
    }

    public static void showToast(final Context context, final String text, final boolean isLong, Handler mainHandler) {
        if (mainHandler != null) {
            mainHandler.post(new Runnable() {
                public void run() {
                    if (ToastUtils.mToast == null) {
                        ToastUtils.mToast = Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                    } else {
                        ToastUtils.mToast.setText(text);
                        ToastUtils.mToast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                    }

                    ToastUtils.mToast.show();
                }
            });
        }

    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }

    }

    private static void showDebugToast(Context context, String text, boolean isDebugMode, Handler mainHandler) {
        if (isDebugMode) {
            showToast(context, text, false, mainHandler);
        }
    }

    public static void showLongToast(Context context, String text, Handler mainHandler) {
        showToast(context, text, true, mainHandler);
    }

    private static void showImageCneterToast(Context context, String text, int picId, boolean isLong, Handler mainHandler) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) null);
        ImageView image = (ImageView) layout.findViewById(R.id.toast_image);
        image.setImageResource(picId);
        TextView textV = (TextView) layout.findViewById(R.id.toast_text);
        textV.setText(text);
        final Toast toast = new Toast(context);
        toast.setGravity(17, 0, 0);
        toast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setView(layout);
        mainHandler.post(new Runnable() {
            public void run() {
                toast.show();
            }
        });
    }

    public static void showSuccessToast(Context context, String text, Handler mainHandler) {
        showImageCneterToast(context, text, R.mipmap.ic_check_circle_white_36dp, false, mainHandler);
    }
  public static void showSuccessToast (String text ) {
      showSuccessToast(UIUtils.getContext(), text, UIUtils.getMainThreadHandler());
    }

    private static void showFailToast(Context context, String text, Handler mainHandler) {
        showImageCneterToast(context, text, R.mipmap.ic_error_outline_white_36dp, false, mainHandler);
    }

    public static void showFailToast(String text) {
        showFailToast(UIUtils.getContext(), text, UIUtils.getMainThreadHandler());
    }

}
