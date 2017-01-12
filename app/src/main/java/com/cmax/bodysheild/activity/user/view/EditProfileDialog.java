package com.cmax.bodysheild.activity.user.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import com.cmax.bodysheild.R;

/**
 * Created by Administrator on 2017/1/12 0012.
 */
public class EditProfileDialog  extends Dialog{
    public EditProfileDialog(Context context) {
        this(context, R.style.DialogStyle);
    }

    public EditProfileDialog(Context context, int themeResId) {
        super(context, themeResId);
       // setContentView(R.layout.dialog_edit_profile);
    }

    // 写一些方法
     public  static  class Builder{
        private Context contexxt;
        private String title;
        private String message;
        private DialogInterface.OnClickListener positiveListener;
        private DialogInterface.OnClickListener negativeListener;
        private String prositiveText;
        private String negativeText;
        private View contentView;
        private boolean canceledOnTouchOutside=false;

        public  Builder setContext (Context context){
            this.contexxt=context;
            return this;
        }
        public Builder setTitle(String title){
            this.title=title;
            return this;
        }
        public Builder setMessage(String message){
            this.message=message;
            return this;
        }
        public Builder setPositiveButton(String prositive, DialogInterface.OnClickListener listener){
            this.prositiveText=prositive;
            this.positiveListener=listener;
            return  this;
        }
        public  Builder setNegativeButton(String negative,DialogInterface.OnClickListener listener){
            this.negativeText=negative;
            this.negativeListener=listener;
            return  this;
        }
        public Builder setContentView(View contentView) {//设置dialog的主view
            this.contentView = contentView;
            return this;
        }
        public Builder  setCanceledOnTouchOutside(boolean b){
            this.canceledOnTouchOutside=b;
            return  this;
        }
        // 构建整个dialog
        public EditProfileDialog builder(){
            View view = View.inflate(contexxt ,R.layout.dialog_edit_profile,null);
            EditProfileDialog editProfileDialog  = new EditProfileDialog(contexxt);
            editProfileDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            editProfileDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

            return  null;
        }
    }

}
