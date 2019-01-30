package com.cnnet.otc.health.views.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.data.GluTestCode;
import com.cnnet.otc.health.util.DateUtil;
import com.cnnet.otc.health.util.DialogUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/6.
 */
public class DetectGluListAdapter  extends BaseAdapter {

    private Collection listeners=null;
    private List<RecordItem> record_items;
    private Context context;
    LayoutInflater inflater;

    public DetectGluListAdapter(Context ctx,List<RecordItem> itms)
    {
        this.record_items=itms;
        this.context=ctx;
        this.inflater=LayoutInflater.from(context);
    }

    public void addDeleteListener(GluDeleteEventListener listener)
    {
        if(listeners==null)
        {
            listeners=new HashSet();
        }
        listeners.add(listener);
    }

    public void removeDeleteListener(GluDeleteEventListener listener)
    {
        if(listeners==null)
        {
            return;
        }
        listeners.remove(listener);
    }

    private void notifyDelete(GluDeleteEvent evt)
    {
        Iterator iter=listeners.iterator();
        while(iter.hasNext())
        {
            GluDeleteEventListener listener=(GluDeleteEventListener)(iter.next());
            listener.OnCheckItemDelete(evt);
        }
    }

    @Override
    public int getCount() {
        if(record_items != null) {
            return record_items.size() + 1;
        }
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    String getTimeStr(Date date)
    {
        SimpleDateFormat fmt;
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        Calendar curCal=Calendar.getInstance();
        if(cal.get(Calendar.YEAR)==curCal.get(Calendar.YEAR)&&
                cal.get(Calendar.MONTH)==curCal.get(Calendar.MONTH)&&
                cal.get(Calendar.DAY_OF_MONTH)==curCal.get(Calendar.DAY_OF_MONTH))
        {
            fmt = new SimpleDateFormat("HH:mm");
        }
        else
        {
            fmt=new SimpleDateFormat("yy/MM/dd HH:mm");
        }
        return  fmt.format(date);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewCache view_cache = null;
        if(view == null) {
            view = inflater.inflate(R.layout.glu_record_info_list_item, null);
            view_cache = new ViewCache();
            view_cache.txtRecordTime = (TextView) view.findViewById(R.id.glu_record_item_time);
            view_cache.txtGluValue = (TextView) view.findViewById(R.id.glu_record_item_value);
            view_cache.txtCheckItemName = (TextView) view.findViewById(R.id.glu_record_item_title);
            view_cache.btnDel = (ImageView) view.findViewById(R.id.glu_record_item_delete);
            view.setTag(view_cache);
        } else {
            view_cache = (ViewCache) view.getTag();
        }
        if(position <= 0) {
            view_cache.txtRecordTime.setText(R.string.TIME);
            view_cache.txtGluValue.setText("血糖\n\r(mmol/L)");
            view_cache.txtCheckItemName.setText("项目");
            view_cache.btnDel.setVisibility(View.INVISIBLE);
            view_cache.btnDel.setOnClickListener(null);
        } else {
            final int index = position - 1;
            final RecordItem item = this.record_items.get(index);
            view_cache.txtRecordTime.setText(DateUtil.getDateStr(item.getCreateTime(), context.getString(R.string.today)));
            view_cache.txtGluValue.setText(String.valueOf(item.getValue1()));
            GluTestCode testCode=GluTestCode.find(item.getTestCode());
            String testName=(testCode==null)? "":testCode.getTitle();
            view_cache.txtCheckItemName.setText(testName);
            if (item.getState() < 2) {
                view_cache.btnDel.setVisibility(View.VISIBLE);
                view_cache.btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.Confirm(context, R.string.dialog_alert_title, R.string.dialog_delete_record_item,
                                R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        notifyDelete(new GluDeleteEvent(this,item));
                                        record_items.remove(item);
                                        notifyDataSetChanged();
                                    }
                                }, R.string.cancel, null);
                    }
                });
            } else {
                view_cache.btnDel.setOnClickListener(null);
                view_cache.btnDel.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    class ViewCache {
        TextView txtRecordTime;
        TextView txtGluValue;
        TextView txtCheckItemName;
        ImageView btnDel;
    }
}

