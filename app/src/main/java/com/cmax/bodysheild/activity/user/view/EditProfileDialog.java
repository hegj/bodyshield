package com.cmax.bodysheild.activity.user.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmax.bodysheild.R;

/**
 * Created by Administrator on 2017/1/12 0012.
 */
public class EditProfileDialog extends Dialog {
    public EditProfileDialog(Context context) {
        this(context, R.style.DialogStyle);
    }

    public EditProfileDialog(Context context, int themeResId) {
        super(context, themeResId);
        // setContentView(R.layout.dialog_edit_profile);
    }
    public static final int TYPE1 = 1;
    public static final int TYPE2 = 2;
    public static final int TYPE3 = 3;
    public static final int TYPE4 = 4;
    // 写一些方法
    public static class Builder {

        private Activity context;
        private String title;
        private String textMessage="";
        private DialogInterface.OnClickListener positiveListener;
        private DialogInterface.OnClickListener negativeListener;
        private String prositiveText;
        private String negativeText;
        private View contentView;
        private boolean canceledOnTouchOutside = false;
        private String edTextMessage;
        private TextInputEditText edMessage;
        private String edTextHint;
        private int dialogType;

        public Builder setContext(Activity context) {
            this.context = context;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTextMessage(String message) {
            this.textMessage = message;
            return this;
        }

        public Builder setEdTextMessage(String message) {
            this.edTextMessage = message;
            return this;
        }

        public Builder setEdTextHint(String hint) {
            this.edTextHint = hint;
            return this;
        }

        public Builder setPositiveButton(String prositive, DialogInterface.OnClickListener listener) {
            this.prositiveText = prositive;
            this.positiveListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negative, DialogInterface.OnClickListener listener) {
            this.negativeText = negative;
            this.negativeListener = listener;
            return this;
        }

        public Builder setContentView(View contentView) {//设置dialog的主view
            this.contentView = contentView;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean b) {
            this.canceledOnTouchOutside = b;
            return this;
        }
        public Builder setDialogType(int type){
            this.dialogType=type;
            return this;
        }

        // 构建整个dialog
        public EditProfileDialog create() {
            View view = View.inflate(context, R.layout.dialog_edit_profile, null);
            final EditProfileDialog editProfileDialog = new EditProfileDialog(context);
            editProfileDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            editProfileDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (!TextUtils.isEmpty(title)) {
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                tvTitle.setText(title);
            }
            TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
            edMessage = (TextInputEditText) view.findViewById(R.id.et_message);
            if (dialogType==TYPE1){
                edMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(textMessage);
            }else if(dialogType==TYPE2){
                tvMessage.setText(textMessage);
                tvMessage.setVisibility(View.VISIBLE);
                edMessage.setVisibility(View.GONE);
            }else if (dialogType==TYPE3){
                edMessage.setVisibility(View.VISIBLE);
                tvMessage.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(edTextHint)){
                    edMessage.setText(edTextHint);
                }
            }else  if (dialogType==TYPE4){
                // 自定义布局
                LinearLayout llMessage = (LinearLayout) view.findViewById(R.id.ll_message);
                llMessage.removeAllViews();
                if (view != null) llMessage.addView(contentView);
            }else {
                edMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(textMessage);
            }

            TextView tvPositive = (TextView) view.findViewById(R.id.tv_positive);
            if (TextUtils.isEmpty(prositiveText)){
                tvPositive.setVisibility(View.GONE);
            }else {
                tvPositive.setText(prositiveText);
                tvPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveListener.onClick(editProfileDialog,BUTTON_POSITIVE);
                    }
                });
            }
            TextView tvNegative = (TextView) view.findViewById(R.id.tv_negative);
            if (TextUtils.isEmpty(negativeText)){
                tvNegative.setVisibility(View.GONE);
            }else {
                tvNegative.setText(negativeText);
                tvNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeListener.onClick(editProfileDialog,BUTTON_NEGATIVE);
                    }
                });
            }
            editProfileDialog.setContentView(view);
            return editProfileDialog;
        }
    }

}
