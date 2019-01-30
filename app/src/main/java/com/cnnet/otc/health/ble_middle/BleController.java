package com.cnnet.otc.health.ble_middle;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.bean.data.OximetryData;
import com.cnnet.otc.health.ble.SimpleBleSerialClient;
import com.cnnet.otc.health.ble.SimpleBleSerialClientCfg;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.db.MyDBManager;
import com.cnnet.otc.health.events.BleEvent;
import com.cnnet.otc.health.interfaces.MyCommData;
//import com.cnnet.otc.health.util.LogFile;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by ljs-jj on 2017/5/17.
 */
public class BleController {
    SimpleBleSerialClient ble_client;
    private SimpleBleSerialClientCfg cfg;
    MyBlueToothDevice currentBT=null;
    private List<MyBlueToothDevice> bt_list=null;
    int cur_idx=-1;
    private Context mContext=null;

    Handler handler=new Handler();
    Runnable r_conn_monitor =null;

    private MyCommData data=null;

    public void run_controller(final int interval_ms)
    {
        if(r_conn_monitor ==null) {
            r_conn_monitor = new Runnable() {
                @Override
                public void run() {
                    if (ble_client == null || ((ble_client != null)&& !ble_client.isConnected())) {
                        restart_client();
                    }
                    handler.postDelayed(this, interval_ms);
                }
            };
        }
        handler.post(r_conn_monitor);
    }

    public void addBleDeviceToList(MyBlueToothDevice dev)
    {
        int the_idx=-1;
        for(int i=0;i<getBt_list().size();i++)
        {
            if(bt_list.get(i).getAddress().equals(dev.getAddress()))
            {
                the_idx=i;
                break;
            }
        }
        if(the_idx>0)
        {
            bt_list.remove(the_idx);
        }
        bt_list.add(0,dev);
        this.cur_idx=-1;

    }

    public void reset_controller()
    {
        handler.removeCallbacks(r_conn_monitor);
        if(ble_client!=null)
        {
            ble_client.exitBt();
        }
        mContext=null;
        data=null;
        this.cfg=null;
    }

    /**
     * 获取解析数据Data类
     * @return
     */
    public MyCommData getData() {
        return data;
    }

    public void setData(MyCommData the_data)
    {
        data= the_data;
    }

    public void pause_controller()
    {
        handler.removeCallbacks(r_conn_monitor);
        if(ble_client!=null)
        {
            ble_client.exitBt();
        }
        ble_client=null;
        /*if(log_file!=null)
        {
            log_file.finish();
        }*/
    }

    int find_next_bt_idx()
    {
        if(getBt_list().size()<=0)
        {
            return -1;
        }
        int idx=(cur_idx==-1)? 0:cur_idx;
        cur_idx++;
        if(cur_idx==getBt_list().size())
        {
            cur_idx=0;
        }
        return idx;
    }

    public boolean isConnected()
    {
        if(ble_client==null)
            return false;
        return ble_client.isConnected();
    }

    public int getConnStatus()
    {
        if(ble_client==null)
            return BluetoothProfile.STATE_DISCONNECTED;
        return ble_client.getConnectState();
    }

    void restart_client()
    {
        if(getmContext()==null)
            return;
        if(ble_client!=null)
        {
            ble_client.exitBt();
        }
        int idx=find_next_bt_idx();
        if(idx>=0) {
            this.currentBT=getBt_list().get(idx);
            //SimpleBleSerialClientCfg cfg = BleCfgFactory.createBleSO2Cfg();
            if(cfg==null)
                return;
            cfg.setDev_addr(currentBT.getAddress());

            ble_client = new SimpleBleSerialClient(getmContext(), cfg);
            ble_client.setBtCallBack(new TempBluetoothGattCallback());
            ble_client.connectBtDevice_Direct();
        }
    }

    static BleController ble_controller;
    public static BleController getInstance()
    {
        if(ble_controller==null)
        {
            ble_controller=new BleController();
        }
        return ble_controller;
    }

     BleController()
    {

    }

    public boolean isBleStoreEmpty()
    {
        return getBt_list().size()<=0;
    }

    public List<MyBlueToothDevice> getBt_list() {
        if(bt_list==null)
        {
            bt_list= MyDBManager.getInstance(getmContext()).getConnectedBtList(SysApp.check_type.ordinal());
            bt_list=(bt_list==null)? new ArrayList<MyBlueToothDevice>():bt_list;
        }
        return bt_list;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        reset_controller();
        this.mContext = mContext;
        /*if(log_file==null)
        {
            log_file=new LogFile(mContext,"/sdcard/DCIM/data.log");
        }*/
    }

    public void setCfg(SimpleBleSerialClientCfg cfg) {
        this.cfg = cfg;
    }


    class TempBluetoothGattCallback extends BluetoothGattCallback {

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] msg_byts=characteristic.getValue();
            /*StringBuilder stringBuilder = new StringBuilder(msg_byts.length);
            for(byte byteChar : msg_byts)
                stringBuilder.append(String.format("%02X ", byteChar));
            final String val=stringBuilder.toString();
            Log.i("CONN",val);*/
            if(getData()!=null) {
                /*if(log_file!=null)
                {
                    log_file.write(msg_byts);
                }*/
                getData().todo(msg_byts);
            }
        }
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {}
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {}
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            //long m_conn_end=System.currentTimeMillis();
            SysApp.btDevice = currentBT;
            EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_SCUESS, getmContext().getString(R.string.Stateconnected)));
            SysApp.getMyDBManager().saveBTDeviceInfo(currentBT, SysApp.check_type.ordinal());
            if(getData()!=null)
                getData().todoConnected();
            Log.i("CONN","已连接");
            /*if(ble_client!=null)
            {
                ble_client.listening_data(100);
            }*/
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status,final int newState) {
                
                    if(newState== BluetoothProfile.STATE_CONNECTING)
                        Log.i("CONN","正在连接...");
                    else if(newState== BluetoothProfile.STATE_CONNECTED) {

                    }
                    else if(newState== BluetoothProfile.STATE_DISCONNECTING)
                        Log.i("CONN","正在断开...");
                    else {
                        EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_DISCONNECTED, ""));
                        Log.i("CONN", "已断开...");
                    }
        }
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {}
    }
}
