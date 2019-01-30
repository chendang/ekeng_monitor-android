package com.cnnet.otc.health.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ToastUtil {
	public static int LENGTH_LONG = Toast.LENGTH_LONG;
	private static Toast toast = null;

	/**
	 * 带图片消息提示
	 * 
	 * @param context
	 * @param ImageResourceId
	 * @param text
	 * @param duration
	 */
	public static void ImageToast(Context context, int ImageResourceId,
			CharSequence text, int duration) {
		// 创建一个Toast提示消息
		toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		// 设置Toast提示消息在屏幕上的位置
		toast.setGravity(Gravity.CENTER, 0, 0);
		// 获取Toast提示消息里原有的View
		View toastView = toast.getView();
		// 创建一个ImageView
		ImageView img = new ImageView(context);
		img.setImageResource(ImageResourceId);
		// 创建一个LineLayout容器
		LinearLayout ll = new LinearLayout(context);
		// 向LinearLayout中添加ImageView和Toast原有的View
		ll.addView(img);
		ll.addView(toastView);
		// 将LineLayout容器设置为toast的View
		toast.setView(ll);
		// 显示消息
		/*if(!AppCheckUtil.isAppOnBackground()){
		}*/
		toast.show();
	}

	/**
	 * 普通文本消息提示
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void TextToast(Context context, CharSequence text,
			int duration) {
		if(toast != null) {
			toast.cancel();
			toast = null;
		}
		// 创建一个Toast提示消息
		if(toast == null){
			toast = Toast.makeText(context, text, duration);
		}
		
		// 显示消息
		toast.show();
	}

	/**
	 * 普通文本消息提示
	 * 
	 * @param context
	 * @param ResourceId
	 * @param duration
	 */
	public static Toast TextToast(Context context, int ResourceId, int duration) {
		if(toast != null) {
			toast.cancel();
			toast = null;
		}
		// 创建一个Toast提示消息
		if(toast == null){
			toast = Toast.makeText(context, ResourceId, duration);
		}
		
		// 设置Toast提示消息在屏幕上的位置
		// toast.setGravity(Gravity.CENTER, 0, 0);
		// 显示消息

		toast.show();
	
		return toast;
	}
}