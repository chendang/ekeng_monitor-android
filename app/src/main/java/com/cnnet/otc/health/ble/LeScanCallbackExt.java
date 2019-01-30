package com.cnnet.otc.health.ble;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by Administrator on 2017/5/17.
 */
public interface LeScanCallbackExt extends BluetoothAdapter.LeScanCallback{
    void on_scan_finish();
}
