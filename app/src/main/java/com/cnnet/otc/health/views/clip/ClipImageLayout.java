package com.cnnet.otc.health.views.clip;



import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.cnnet.otc.health.comm.CommConst;

public class ClipImageLayout extends RelativeLayout
{

	private ClipZoomImageView mZoomImageView;
	private ClipImageBorderView mClipImageView;

	private int mImgHeight = CommConst.HEAD_IMG_W;
	private int mImgWidth = CommConst.HEAD_IMG_W;
	public ClipImageLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		mZoomImageView = new ClipZoomImageView(context);
		mClipImageView = new ClipImageBorderView(context);

		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		
		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);

		
		
		mImgHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mImgHeight, getResources().getDisplayMetrics());
		mImgWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mImgWidth, getResources().getDisplayMetrics());
	}

	/**
	 * 对外公布设置边距的方法,单位为dp
	 *
	 */
	public void setImgWH(int w,int h)
	{
		this.mImgWidth = w;
		this.mImgHeight = h;
	}
	/**
	 * 裁切图片
	 * 
	 * @return
	 */
	public Bitmap clip()
	{
		return mZoomImageView.clip();
	}

	public void setBitmap(Bitmap bitmap) {
		mZoomImageView.setImageBitmap(bitmap);
	}

}
