package com.cnnet.otc.health.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17.
 */
public class BleScanner {
    static BleScanner scanner = null;

    static public BleScanner getInstance(Context ctx)
    {
        if(scanner==null)
        {
            scanner=new BleScanner(ctx);

        }
        return scanner;
    }



    private LeScanCallbackExt mCallerScanCallback=null;

    boolean mScanning = false;
    public boolean isScanning()
    {
        return mScanning;
    }

    private List<BluetoothDevice> devices=null;
    BluetoothAdapter mBluetoothAdapter=null;

    private BleScanner(Context ctx)
    {
        BluetoothManager mBluetoothManager = (BluetoothManager)ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    public boolean scan(int interval_ms) {
        if(mScanning)
            return true;
        if(mBluetoothAdapter==null)
            return false;
        getDevices().clear();
        Handler mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop_scan();
                }
        }, interval_ms);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        return true;
    }

    public void stop_scan()
    {
        mScanning = false;
        if(mBluetoothAdapter!=null)
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        if(getmCallerScanCallback() !=null)
        {
            getmCallerScanCallback().on_scan_finish();
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device == null)
                return;
            for (BluetoothDevice dev : getDevices()) {
                if (dev.getAddress().equals(device.getAddress())) {
                    return;
                }
            }
            devices.add(device);
            if(getmCallerScanCallback() !=null)
            {
                getmCallerScanCallback().onLeScan(device,rssi,scanRecord);
            }
        }
    };


    public List<BluetoothDevice> getDevices() {
        if(devices==null)
            devices=new ArrayList<BluetoothDevice>();
        return devices;
    }

    public LeScanCallbackExt getmCallerScanCallback() {
        return mCallerScanCallback;
    }

    public void setmCallerScanCallback(LeScanCallbackExt mCallerScanCallback) {
        this.mCallerScanCallback = mCallerScanCallback;
    }
}
