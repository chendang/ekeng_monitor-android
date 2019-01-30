package com.cnnet.otc.health.views.clip;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.cnnet.otc.health.comm.CommConst;

public class ClipImageBorderView extends View
{
	
	private int mImgWidth = CommConst.HEAD_IMG_W;//——图片宽;
	private int mImgHeight = CommConst.HEAD_IMG_W;//——图片高;
	/**
	 * 水平方向与View的边距
	 */
	private int mHorizontalPadding =10;
	private int mVerticalPadding =10;
	/**
	 * 边框的宽度 单位dp
	 */
	private int mBorderWidth = 2;

	private Paint mPaint;

	public ClipImageBorderView(Context context)
	{
		this(context, null);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	
		mBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
						.getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// 绘制边框
		mVerticalPadding = (getHeight() -mImgHeight) / 2;
		 mHorizontalPadding= (getWidth() -mImgWidth) / 2;
		mPaint.setColor(Color.parseColor("#FFFFFF"));
		mPaint.setStrokeWidth(mBorderWidth);
		mPaint.setStyle(Style.STROKE);
		
		mImgWidth = mImgWidth < getWidth() ? mImgWidth : getWidth() - 50;
		mImgHeight = mImgWidth;
		mVerticalPadding = (getHeight() -mImgHeight) / 2;
		 mHorizontalPadding= (getWidth() -mImgWidth) / 2;
		 mVerticalPadding = mVerticalPadding  > 0 ? mVerticalPadding:0;
		 mHorizontalPadding = mHorizontalPadding  > 0 ? mHorizontalPadding:0;
		//方形边框
		canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()- mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);
		//圆形边框
//		canvas.drawCircle( getWidth()/2, getHeight()/2, getWidth()/2-mHorizontalPadding, mPaint);

	}

}
