package com.cnnet.otc.health.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.comm.SysApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SZ512 on 2016/1/18.
 */
public class MyDeviceGridAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater inflater = null;
    // 图片封装为一个数组
    private int[] icon = null;
    private String[] iconName = null;
    private List<Map<String, Object>> deiveDatas = null;
    private Map<Integer, Boolean> nowItems = null;

    public MyDeviceGridAdapter(Context ctx) {
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        initGridMeta();
        deiveDatas = getData();
    }

    @Override
    public int getCount() {
        if(deiveDatas != null) {
            return deiveDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return deiveDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.my_grid_view_item, null);
            viewHolder.ivDevice = (ImageView) view.findViewById(R.id.iv_item);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_item);
            viewHolder.ivState = (ImageView) view.findViewById(R.id.iv_state);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if(icon[position] != 0) {
            viewHolder.ivDevice.setImageResource(icon[position]);
            viewHolder.tvName.setText(iconName[position]);
            if (nowItems != null) {
                if (nowItems.containsKey(position)) {
                    viewHolder.ivState.setImageResource(R.drawable.ic_device_ins_success);
                    return view;
                }
            }
            viewHolder.ivState.setImageResource(R.drawable.ic_device_ins_wait);
        } else {
            viewHolder.ivDevice.setVisibility(View.INVISIBLE);
            viewHolder.tvName.setVisibility(View.INVISIBLE);
            viewHolder.ivState.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    class ViewHolder {
        ImageView ivDevice;
        TextView tvName;
        ImageView ivState;
    }

    /**
     * 初始化GridView中的素材
     */
    private void initGridMeta() {
        icon = new int[]{
                R.drawable.btn_xt_selector, R.drawable.btn_bp_selector, R.drawable.btn_tp_selector,
                R.drawable.btn_xy_selector, R.drawable.btn_xz_selector, R.drawable.btn_tz_selector,
                R.drawable.btn_ns_selector, 0};
        iconName= new String[]{
                ctx.getString(R.string.device_meter_blood_glucose), ctx.getString(R.string.device_sphygmomanometers),
                ctx.getString(R.string.device_thermometer),ctx.getString(R.string.device_pulse_oximter),
                ctx.getString(R.string.device_lipid_instrument), ctx.getString(R.string.device_weighing_scale),
                ctx.getString(R.string.device_uric_acid_analyzer),"" };
    }

    /**
     * 设置当前已经检查过的设备
     * @param navtiveRecordId
     */
    public void setNowRecordedDevice(long navtiveRecordId) {
        if(navtiveRecordId > 0) {
            nowItems = SysApp.getMyDBManager().getMapByRecordId(navtiveRecordId);
        } else {
            nowItems = null;
        }
        notifyDataSetChanged();
    }

    /**
     * 获取GridView中将要显示的Data
     * @return
     */
    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }
}
