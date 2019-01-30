package com.cnnet.otc.health.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.activities.DeviceInfoAcitivty;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SZ512 on 2016/1/9.
 */
public class DeviceFragment extends Fragment {

    private final String TAG = "DeviceFragment";
    private Context ctx;
    private View root;
    private ListView deivceLists;
    // 图片封装为一个数组
    private List<Map<String, Object>> datas = null;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ctx = activity;
        Log.d(TAG, "AAAAAAAAAA____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AAAAAAAAAA____onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AAAAAAAAAA____onCreateView");
        if(root == null) {
            root = inflater.inflate(R.layout.fragment_devices, container, false);
            deivceLists = (ListView) root.findViewById(R.id.device_type_list);
        }
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "AAAAAAAAAA____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "AAAAAAAAAA____onResume");
        setListViewData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "AAAAAAAAAA____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "AAAAAAAAAA____onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AAAAAAAAAA____onDestroy");
    }

    private void setListViewData() {
        initDeviceInfo();
        SimpleAdapter simpleAdapter = new SimpleAdapter(ctx, datas, R.layout.device_type_list_item,
                new String[]{"image", "title", "count"}, new int[]{R.id.device_item_image,
                R.id.device_item_title, R.id.device_item_count});
        deivceLists.setAdapter(simpleAdapter);
        deivceLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int count = (int) datas.get(position).get("count");
                if(count > 0) {
                    Intent intent = new Intent(ctx, DeviceInfoAcitivty.class);
                    intent.putExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE_NAME, (String)(datas.get(position).get("title")));
                    intent.putExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE, position);
                    startActivity(intent);
                } else {
                    ToastUtil.TextToast(ctx, R.string.device_list_is_empty, 2000);
                }
            }
        });
    }

    /**
     * 初始化GridView中的素材
     */
    private void initDeviceInfo() {
        datas = new ArrayList<>();
        Map<Integer, Integer> deviceCounts = SysApp.getMyDBManager().getDeviceCountByType();
        Map<String, Object> map = new HashMap<>();
        map.put("image", R.mipmap.btn_xt_normal);
        map.put("title", ctx.getString(R.string.device_meter_blood_glucose));
        if(deviceCounts.containsKey(CheckType.BLOOD_GLUCOSE.ordinal())) {
            map.put("count", deviceCounts.get(CheckType.BLOOD_GLUCOSE.ordinal()));
        } else {
            map.put("count", 0);
        }
        datas.add(map);
        map = new HashMap<>();
        map.put("image", R.mipmap.btn_bp_normal);
        map.put("title", ctx.getString(R.string.device_sphygmomanometers));
        if(deviceCounts.containsKey(CheckType.BLOOD_PRESSURE.ordinal())) {
            map.put("count", deviceCounts.get(CheckType.BLOOD_PRESSURE.ordinal()));
        } else {
            map.put("count", 0);
        }
        datas.add(map);
        map = new HashMap<>();
        map.put("image", R.mipmap.btn_tp_normal);
        map.put("title", ctx.getString(R.string.device_thermometer));
        if(deviceCounts.containsKey(CheckType.THERMOMETER.ordinal())) {
            map.put("count", deviceCounts.get(CheckType.THERMOMETER.ordinal()));
        } else {
            map.put("count", 0);
        }
        datas.add(map);
        map = new HashMap<>();
        map.put("image", R.mipmap.btn_xy_normal);
        map.put("title", ctx.getString(R.string.device_pulse_oximter));
        if(deviceCounts.containsKey(CheckType.OXIMETRY.ordinal())) {
            map.put("count", deviceCounts.get(CheckType.OXIMETRY.ordinal()));
        } else {
            map.put("count", 0);
        }
        datas.add(map);
        map = new HashMap<>();
        map.put("image", R.mipmap.btn_xz_normal);
        map.put("title", ctx.getString(R.string.device_lipid_instrument));
        if(deviceCounts.containsKey(CheckType.LIPID.ordinal())) {
            map.put("count", deviceCounts.get(CheckType.LIPID.ordinal()));
        } else {
            map.put("count", 0);
        }
        datas.add(map);
        map = new HashMap<>();
        map.put("image", R.mipmap.btn_tz_normal);
        map.put("title", ctx.getString(R.string.device_weighing_scale));
        if(deviceCounts.containsKey(CheckType.WEIGHT.ordinal())) {
            map.put("count", deviceCounts.get(CheckType.WEIGHT.ordinal()));
        } else {
            map.put("count", 0);
        }
        datas.add(map);
        map = new HashMap<>();
        map.put("image", R.mipmap.btn_ns_normal);
        map.put("title", ctx.getString(R.string.device_uric_acid_analyzer));
        if(deviceCounts.containsKey(CheckType.URIC_ACID.ordinal())) {
            map.put("count", deviceCounts.get(CheckType.URIC_ACID.ordinal()));
        } else {
            map.put("count", 0);
        }
        datas.add(map);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CommConst.INTENT_REQUEST_DEVICE_INFO) {
            boolean isDeleted = data.getBooleanExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_IS_DELETED, false);
            if(isDeleted) { //刷新设备
                setListViewData();
            }
        }
    }
}
