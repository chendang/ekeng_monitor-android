package com.cnnet.otc.health.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.foxchen.ekengmonitor.R;

public class MyGridView extends GridView {
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        View localView1 = getChildAt(0);
        if(localView1 != null) {
            int column = getWidth() / localView1.getWidth();//计算出一共有多少列，假设有3列
            int childCount = getChildCount();//子view的总数
            System.out.println("子view的总数childCount==" + childCount);
            Paint localPaint;//画笔
            localPaint = new Paint();
            localPaint.setStyle(Paint.Style.STROKE);
            localPaint.setColor(getContext().getResources().getColor(R.color.grid_view_item_line));//设置画笔的颜色
            for (int i = 0; i < childCount; i++) {//遍历子view
                View cellView = getChildAt(i);//获取子view
                //画底部横线
                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                //画左边竖线
                canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getLeft(), cellView.getBottom(), localPaint);
                if (i < column) {//第一行
                    //画子view顶部横线
                    canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getRight(), cellView.getTop(), localPaint);
                }
                if ((i + 1) % column == 0 || ((i + 1) == childCount)) {//最后一列或者是最后一项
                    canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                }
            }
        }
    }


}
