/**
 * 
 */
package com.cmax.bodysheild.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmax.bodysheild.R;


public class LoadingMask {

	Dialog	dialog;
	Context	context;
	LinearLayout	layout;
	boolean cancelable;
	public LoadingMask(Context context) {
		this.context = context;
		cancelable = true;
		init();
	}

	public LoadingMask(Context context, boolean cancelable) {
		this.context = context;
		this.cancelable = cancelable;
		init();
	}
	
	@SuppressLint("InflateParams")
	private void init() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_mask, null);
		layout = (LinearLayout) v
				.findViewById(R.id.dialog_view);

		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context,
				R.anim.animation);
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);

		dialog = new Dialog(context, R.style.mask_dialog);
		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
	}

	public void show() {
		dialog.show();
		Log.i("LoadingMask","显示遮罩层！");
	}

	public void hide() {
		dialog.hide();
		Log.i("LoadingMask", "隐藏遮罩层！");
	}

	public void close() {
		dialog.dismiss();
	}
}
