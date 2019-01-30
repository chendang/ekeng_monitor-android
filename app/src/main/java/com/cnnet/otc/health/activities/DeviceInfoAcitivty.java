package com.cnnet.otc.health.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;

import java.util.List;

/**
 * Created by SZ512 on 2016/1/26.
 */
public class DeviceInfoAcitivty extends BaseActivity {

    private TextView title = null;
    private TextView deviceDelete = null;
    private ListView deviceList = null;
    private DeviceAdapter adapter;
    private int[] icons = null;
    private int deviceTypeValue = -1;
    private boolean isDeleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        initDeviceList();
    }

    private void initDeviceList() {
        deviceDelete = (TextView) findViewById(R.id.device_delete);
        String deviceInfo = getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE_NAME);
        deviceTypeValue = getIntent().getIntExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE, -1);
        title = (TextView) findViewById(R.id.device_info_title);
        title.setText(String.format(getString(R.string.device_info_title), deviceInfo));
        icons = new int[]{
                R.drawable.btn_xt_selector, R.drawable.btn_bp_selector, R.drawable.btn_tp_selector,
                R.drawable.btn_xy_selector, R.drawable.btn_xz_selector, R.drawable.btn_tz_selector,
                R.drawable.btn_ns_selector, 0};
        deviceList = (ListView) findViewById(R.id.device_list);
        adapter = new DeviceAdapter(this, SysApp.getMyDBManager().getConnectedBtList(deviceTypeValue));
        deviceList.setAdapter(adapter);
        deviceDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDeleted) {
                    deviceDelete.setText(R.string.DELETE);
                    isDeleted = false;
                } else {
                    isDeleted = true;
                    deviceDelete.setText(R.string.COMPLETE);
                }
                adapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndSendBack();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAndSendBack();
        }
        return super.onKeyUp(keyCode, event);
    }

    private void finishAndSendBack() {
        Intent intent = new Intent();
        intent.putExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_IS_DELETED, true);
        setResult(CommConst.INTENT_REQUEST_DEVICE_INFO, intent);
        this.finish();
    }

    class DeviceAdapter extends BaseAdapter {
        private LayoutInflater inflater = null;
        private List<MyBlueToothDevice> devices = null;

        public DeviceAdapter(Context ctx, List<MyBlueToothDevice> devices) {
            inflater = LayoutInflater.from(ctx);
            this.devices = devices;
        }

        @Override
        public int getCount() {
            if(devices != null) {
                return devices.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final MyBlueToothDevice device = devices.get(position);
            ViewHandler handler = null;
            if(view == null) {
                view = inflater.inflate(R.layout.device_info_list_item, null);
                handler = new ViewHandler();
                handler.icon = (ImageView) view.findViewById(R.id.device_item_image);
                handler.title = (TextView) view.findViewById(R.id.device_item_address);
                handler.info = (TextView) view.findViewById(R.id.device_item_name);
                handler.delete = (ImageView) view.findViewById(R.id.device_item_edit);
                view.setTag(handler);
            } else {
                handler = (ViewHandler) view.getTag();
            }
            handler.icon.setImageResource(icons[deviceTypeValue]);
            handler.title.setText(device.getAddress());
            handler.info.setText(device.getName());
            if(isDeleted) {
                handler.delete.setVisibility(View.VISIBLE);
                handler.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SysApp.getMyDBManager().deleteBTDeviceInfo(device.getAddress(), deviceTypeValue);
                        devices.remove(position);
                        notifyDataSetChanged();

                    }
                });
            } else {
                handler.delete.setVisibility(View.GONE);
            }
            return view;
        }

        class ViewHandler {
            ImageView icon;
            TextView title;
            TextView info;
            ImageView delete;
        }
    }
}
