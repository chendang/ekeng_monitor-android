package com.cnnet.otc.health.views.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.util.DateUtil;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.bean.data.LipidDataItem;

import java.util.List;

/**
 * Created by SZ512 on 2016/1/26.
 */
public class DetectRecordListAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater inflater;
    private List<RecordItem> lists;
    private MyCommData myData;

    public DetectRecordListAdapter(Context ctx, List<RecordItem> lists, MyCommData myData) {
        this.ctx = ctx;
        this.lists = lists;
        this.myData = myData;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        if(lists != null) {
            return lists.size() + 1;
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

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHandler handler = null;
        if(view == null) {
            view = inflater.inflate(R.layout.record_info_list_item, null);
            handler = new ViewHandler();
            handler.recordTime = (TextView) view.findViewById(R.id.record_item_time);
            handler.value1 = (TextView) view.findViewById(R.id.record_item_value1);
            handler.value2 = (TextView) view.findViewById(R.id.record_item_value2);
            handler.value3 = (TextView) view.findViewById(R.id.record_item_value3);
            handler.value4 = (TextView) view.findViewById(R.id.record_item_value4);
            handler.value5 = (TextView) view.findViewById(R.id.record_item_value5);
            handler.delete = (ImageView) view.findViewById(R.id.record_item_delete);
            view.setTag(handler);
        } else {
            handler = (ViewHandler) view.getTag();
        }
        int length = myData.getInsUnit().length;
        if(position <= 0) {
            handler.recordTime.setText(R.string.TIME);
            if (length > 0) {
                handler.value1.setVisibility(View.VISIBLE);
                handler.value1.setText(myData.getInsName()[0]);
                if (length > 1) {
                    handler.value2.setVisibility(View.VISIBLE);
                    handler.value2.setText(myData.getInsName()[1]);
                    if (length > 2) {
                        handler.value3.setVisibility(View.VISIBLE);
                        handler.value3.setText(myData.getInsName()[2]);
                        if (length > 3) {
                            handler.value4.setVisibility(View.VISIBLE);
                            handler.value4.setText(myData.getInsName()[3]);
                            if (length >4) {
                                handler.value5.setVisibility(View.VISIBLE);
                                handler.value5.setText(myData.getInsName()[4]);
                            }
                        }
                    }
                }
            }
            handler.delete.setVisibility(View.INVISIBLE);
            handler.delete.setOnClickListener(null);
        } else {
            final int index = position - 1;
            final RecordItem item = lists.get(index);
            handler.recordTime.setText(DateUtil.getDateStr(item.getCreateTime(), ctx.getString(R.string.today)));
            if (length > 0) {
                handler.value1.setVisibility(View.VISIBLE);
                handler.value1.setText(item.getValue1Txt());
                if (length > 1) {
                    handler.value2.setVisibility(View.VISIBLE);
                    handler.value2.setText(item.getValue2Txt());
                    if (length > 2) {
                        handler.value3.setVisibility(View.VISIBLE);
                        handler.value3.setText(item.getValue3Txt());
                        if (length > 3) {
                            handler.value4.setVisibility(View.VISIBLE);
                            handler.value4.setText(item.getValue4Txt());
                            if (length >4) {
                                handler.value5.setVisibility(View.VISIBLE);
                                handler.value5.setText(item.getValue5Txt());
                            }
                        }
                    }
                }
            }
            if (item.getState() < 2) {
                handler.delete.setVisibility(View.VISIBLE);
                handler.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.Confirm(ctx, R.string.dialog_alert_title, R.string.dialog_delete_record_item,
                                R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (SysApp.getMyDBManager().deleteRecordItemByType(item)) {
                                            lists.remove(index);
                                        }
                                        notifyDataSetChanged();
                                    }
                                }, R.string.cancel, null);
//                        if (SysApp.getMyDBManager().deleteRecordItemByType(item)) {
//                            lists.remove(index);
//                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                handler.delete.setOnClickListener(null);
                handler.delete.setVisibility(View.INVISIBLE);
            }
        }
        if(length < 5) {
            handler.value5.setVisibility(View.GONE);
            if (length < 4) {
                handler.value4.setVisibility(View.GONE);
                if (length < 3) {
                    handler.value3.setVisibility(View.GONE);
                    if (length < 2) {
                        handler.value2.setVisibility(View.GONE);
                        if (length < 1) {
                            handler.value1.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        return view;
    }



    class ViewHandler {
        TextView recordTime;
        TextView value1;
        TextView value2;
        TextView value3;
        TextView value4;
        TextView value5;
        ImageView delete;
    }
}
