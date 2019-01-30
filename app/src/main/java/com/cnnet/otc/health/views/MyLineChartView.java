package com.cnnet.otc.health.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bluetooth.MyMarkerView;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.util.DateUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SZ512 on 2016/1/4.
 */
public class MyLineChartView extends LinearLayout {

    private final String TAG = "MyLineChartView";

    private Context ctx;

    /**
     * 判断当前单位是否是mmol/L单位
     */
    private boolean isMmol = true;

    private int lineColors[];

    private TextView tvTitle;
    private TextView tvInfo;
    private TextView tvLastValue;
    private TextView tvLastTime;

    private LinearLayout barCharLinear = null;  //实施数据父容器
    private BarChart barChart = null;  //实时数据刷新View
    ArrayList<BarEntry> yBarVals1 = null;
    private int barIndex = 0; //real下标
    private final int REAL_DATA_LENGTH = 200; //当前real长度

    private int redCount;   //颜色值
    private int alphaFlag = 0;  //透明度

    private int[] LIN_MAX = new int[]{35, 350, 50, 100, 50, 250, 700};

    /**
     * 线性绘图
     */
    private LineChart mChart;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     */
    public MyLineChartView(Context context, OnChartValueSelectedListener listener) {
        super(context);
        this.ctx = context;
        initColor();
        setupLayoutResource();
        initView();
        initLineView(listener);
    }

    private void initColor() {
        lineColors = new int[]{Color.WHITE, Color.BLUE, Color.GREEN, Color.MAGENTA, 0xFF97FFFF};
    }

    /**
     * Sets the layout resource for a custom MarkerView.
     */
    private void setupLayoutResource() {

        View inflated = LayoutInflater.from(getContext()).inflate(R.layout.v_my_line_chart, this);

        inflated.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        // measure(getWidth(), getHeight());
        inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
    }

    private void initView() {
        barCharLinear = (LinearLayout) findViewById(R.id.real_time_chart_linear);
        tvTitle = (TextView) findViewById(R.id.tv_my_line_title);
        tvInfo = (TextView) findViewById(R.id.tv_my_line_info);
        tvLastValue = (TextView) findViewById(R.id.tv_my_line_last_value);
        tvLastTime = (TextView) findViewById(R.id.tv_my_line_last_time);
    }

    /**
     * 初始化线性
     */
    private void initLineView(OnChartValueSelectedListener listener) {
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(listener);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        // set an alternative background color
        mChart.setBackgroundColor(Color.TRANSPARENT);
        //mChart.setBackgroundResource(setBackgroundResource);
        mChart.getAxisRight().setEnabled(false);

        mChart.animateX(1500);

        Typeface tf = Typeface.createFromAsset(ctx.getAssets(), "OpenSans-Regular.ttf");

        setLegend(tf);  //设置图表呈现方式
        addMarkView();   //设置点击点时，其中的值的显示层
        setChartXAxis(tf);   //设置X轴的属性
        setChartYAxis(tf);  //设置Y轴属性
        //addLimitLineForY(tf, 170f, "170mmHg", R.color.color_limit_line);   //添加上限（或下限）线

    }

    /**
     * 设置图例的属性，设置图表呈现方式（线性，柱状）
     * @param tf 图例的字体
     */
    private void setLegend(Typeface tf) {
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        l.setYOffset(11f);
    }

    /**
     * 设置x轴属性
     * @param tf  字体
     */
    private void setChartXAxis(Typeface tf) {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setAxisLineColor(getResources().getColor(R.color.color_line_xy));
    }

    /**
     * 设置Y轴的属性
     * @param tf  Y轴上文字字体
     */
    private void setChartYAxis(Typeface tf) {
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setTypeface(tf);
        //leftAxis.addLimitLine(ll1);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(LIN_MAX[SysApp.check_type.ordinal()]);
        leftAxis.setAxisMinValue(0);
        //leftAxis.setDrawAxisLine(true);
        //leftAxis.setDrawGridLines(true);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawLimitLinesBehindData(true);   //设置添加的上限（或下限）线是否显示在值的下面
        leftAxis.setAxisLineColor(getResources().getColor(R.color.color_line_xy));
        leftAxis.setGridColor(getResources().getColor(R.color.color_line_xy));
    }

    /**
     * 新增显示点的值的层
     */
    private void addMarkView() {
        MyMarkerView mv = new MyMarkerView(ctx, R.layout.custom_marker_view);
        mChart.setMarkerView(mv);
    }

    /**
     * 在Y轴上添加限制线
     * @param tf  字体
     * @param lineStr  线的说明
     * @param colorResId  线的颜色
     */
    public MyLineChartView addLimitLineForY(Typeface tf, float value, String lineStr, int colorResId) {
        LimitLine ll1 = new LimitLine(value, lineStr);
        ll1.setLineWidth(0.5f);
        ll1.enableDashedLine(15f, 5f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        int color = getResources().getColor(colorResId);
        ll1.setLineColor(color);
        ll1.setTextColor(color);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);
        mChart.getAxisLeft().addLimitLine(ll1);
        return this;
    }

    /**
     * 添加需要显示的数据
     * @param valuelists
     * @return
     */
    public MyLineChartView addData(List<RecordItem>[] valuelists, String[] insNames) {
        if(valuelists != null && valuelists.length > 0) {
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            //设置X轴上显示的数据信息
            ArrayList<String> xVals = new ArrayList<String>();
            int size = 0;
            if(valuelists[0] != null) {
                size = valuelists[0].size();
                for (int i = 0; i < size; i++) {
                    RecordItem item = valuelists[0].get(i);
                    xVals.add(DateUtil.getDateStr(item.getCreateTime(), ctx.getString(R.string.today)));
                }
            }
            // 设置每个点的值
            for (int i = 0; i < valuelists.length; i++) {
                if(valuelists[i] != null && valuelists[i].size() > 0) {
                    Log.d(TAG, "valuelists['" + i + "']----------------------- ");

                    LineDataSet set = getLineDataSet(valuelists[i], insNames[i], lineColors[i], Color.RED, size);
                    dataSets.add(set);
                }
            }
            //将X轴和Y轴的值进行关联
            LineData data = new LineData(xVals, dataSets);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);
            // set data
            mChart.setData(data);
        }
        return this;
    }

    /**
     * 设置一条线上点的值
     * @param list
     * @param lineColor
     * @param foucsColor
     * @return
     */
    private LineDataSet getLineDataSet(List<RecordItem> list, String insName, int lineColor, int foucsColor, int length) {
        if(list != null) {
            ArrayList<Entry> yVals2 = new ArrayList<Entry>();
            for (int i = 0; i < length; i++) {
                RecordItem item = list.get(i);
                Log.d(TAG, "item.getValue1('" + i + "') = " + item.getValue1());
                yVals2.add(new Entry(item.getValue1(), i));
            }

            // create a dataset and give it a type
            LineDataSet dataSet = new LineDataSet(yVals2, insName);
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setColor(lineColor);
            dataSet.setCircleColor(lineColor);
            dataSet.setLineWidth(1f);
            dataSet.setCircleSize(2.5f);
            dataSet.setFillAlpha(65);
            dataSet.setFillColor(foucsColor);
            dataSet.setDrawCircleHole(false);
            dataSet.setDrawValues(false);  //设置是否显示点对应的值
            dataSet.setHighLightColor(Color.rgb(244, 117, 117));
            return dataSet;
        }
        return null;
    }

    public void invalidate() {
        if(mChart != null) {
            mChart.invalidate();
        }
    }

    public void resetBarChart()
    {
        if(barChart!=null)
        {
            barCharLinear.removeView(barChart);
        }
        barChart=null;
        initBarView();
    }

    public void refreshRealTimeByMP(byte[] datas) {
        initBarView();
        if(datas != null&&datas.length>0) {
            for (int i = datas.length - 1; i >= 0; i--) {
                if(barIndex >= REAL_DATA_LENGTH){
                    barIndex=0;
                }
                int value = datas[i] & 0xFF;
                // System.out.println(" barIndex == " + barIndex + ";  value= " + value);
                BarEntry entity = yBarVals1.get(barIndex);
                entity.setVal(value);
                barIndex++;
            }
            barChart.notifyDataSetChanged();
            barChart.invalidate();
        }



    }

    /**
     * 初始化实时数据刷新view
     */
    private void initBarView() {
        if(barChart == null) {
            barChart = new BarChart(ctx);
            barChart.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            barCharLinear.addView(barChart);
            barCharLinear.setVisibility(View.VISIBLE);
            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(false);
            barChart.setDescription("");
            barChart.setDragEnabled(false);// 是否可以拖拽
            barChart.setScaleEnabled(false);// 是否可以缩放
            barChart.setTouchEnabled(false); // 设置是否可以触摸

            // if more than 200 entries are displayed in the chart, no values will be
            // drawn
            barChart.setMaxVisibleValueCount(REAL_DATA_LENGTH);

            // scaling can now only be done on x- and y-axis separately
            barChart.setPinchZoom(false);

            barChart.setDrawGridBackground(false);
            // mChart.setDrawYLabels(false);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGridColor(Color.YELLOW);
            xAxis.setAxisLineWidth(0.5f);
            xAxis.setSpaceBetweenLabels(1);
            xAxis.setTextSize(0);
            xAxis.setDrawLabels(false);


            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setLabelCount(25, false);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMaxValue(150);
            leftAxis.setAxisMinValue(0);
            leftAxis.setGridColor(0x7DCCCCCC);
            leftAxis.setTextSize(0);
            leftAxis.setAxisLineWidth(0.5f);
            leftAxis.setDrawGridLines(false);
            leftAxis.setDrawLabels(false);

            barChart.getAxisRight().setEnabled(false);

            barChart.getLegend().setEnabled(false);

            ArrayList<String> xBarVals = new ArrayList<String>();
            for (int i = 1; i <= REAL_DATA_LENGTH; i++) {
                xBarVals.add(String.valueOf(i));
            }
            yBarVals1 = new ArrayList<BarEntry>();
            for (int i = 0; i < REAL_DATA_LENGTH; i++) {
                yBarVals1.add(new BarEntry(0, i));
            }

            BarDataSet set1 = new BarDataSet(yBarVals1, "realtime");
            //set1.setBarSpacePercent(35f);
            //set1.setColor(Color.YELLOW);
            set1.setColor(Color.argb(0x80,0xFF,0xFF,0x0));
            //set1.setBarSpacePercent(1);
            set1.setBarSpacePercent(0f);
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(xBarVals, dataSets);
            data.setValueTextSize(5f);
            barChart.setData(data);
        }
    }

    /**
     * 设置Title的字体颜色
     */
    public void setTitleTextColor() {
        tvTitle.setTextColor(Color.rgb(255, redCount, redCount));
        if (alphaFlag == 0) {
            if (redCount <= 245) {
                redCount += 10;
            } else {
                alphaFlag = 1;
            }
        } else {
            if (redCount >= 10) {
                redCount -= 10;
            } else {
                alphaFlag = 0;
            }
        }
    }

    public boolean isMmol() {
        return isMmol;
    }

    public void setIsMmol(boolean isMmol) {
        this.isMmol = isMmol;
    }

    public String getTitle() {
        return tvTitle.getText().toString();
    }

    public void setTitleText(String title) {
        this.tvTitle.setText(title);
    }

    public String getInfoText() {
        return tvInfo.getText().toString();
    }

    public void setInfoText(String info) {
        this.tvInfo.setText(info);
    }

    public String getLastValue() {
        return tvLastValue.getText().toString();
    }

    public void setLastValue(String lastValue) {
        this.tvLastValue.setText(lastValue);
    }

    public String getLastTime() {
        return tvLastTime.getText().toString();
    }

    public void setLastTime(String lastTime) {
        this.tvLastTime.setText(lastTime);
    }

    public void setBaseInfo(String title, String info, String lastValue, Timestamp lastTime) {
        this.tvTitle.setText(title);
        this.tvInfo.setText(info);
        this.tvLastValue.setText(lastValue);
        String date = DateUtil.getDateStr(lastTime, ctx.getString(R.string.today));
        this.tvLastTime.setText(date);
    }

    public TextView getTitleView() {
        return tvTitle;
    }
}
