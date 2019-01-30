package com.cnnet.otc.health.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.UUID;

/**
 * Created by Administrator on 2017/5/17.
 */
@SuppressLint({"NewApi"})
public class SimpleBleSerialClient {

        SimpleBleSerialClientCfg cfg=null;

        private BluetoothGatt mBluetoothGatt;
        BluetoothGattCharacteristic RxChar=null;
        BluetoothGattCharacteristic TxChar=null;
        private Context mContext;
        BluetoothGattCallback mBluetoothGattCallback;
        boolean client_is_closed=false;
        private int mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
        static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

        public SimpleBleSerialClient(Context context,SimpleBleSerialClientCfg cfg) {
            this.mContext = context;
            this.cfg=cfg;
            //setBtCallBack(ble_callback);
        }



        public void setBtCallBack(BluetoothGattCallback callback) {
            this.mBluetoothGattCallback = callback;
        }


        public boolean connectBtDevice() {
            if(client_is_closed)
                return false;
            BluetoothManager mBluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
            if(mBluetoothAdapter != null) {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(cfg.getDev_addr());
                if(device == null) {
                    Log.w("connectBtDevice", "Device address is not valid.");
                    return false;
                } else {
                    this.mBluetoothGatt = device.connectGatt(this.mContext, true, new MyBluetoothGattCallback());
                    this.mConnectionState =BluetoothProfile.STATE_CONNECTING ;
                    return true;
                }
            } else {
                Log.w("connectBtDevice", "BluetoothAdapter not initialized.");
                return false;
            }
        }

    public boolean connectBtDevice_Direct() {
        if(client_is_closed)
            return false;
        BluetoothManager mBluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter != null) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(cfg.getDev_addr());
            if(device == null) {
                Log.w("connectBtDevice", "Device address is not valid.");
                return false;
            } else {
                this.mBluetoothGatt = device.connectGatt(this.mContext, false, new MyBluetoothGattCallback());
                this.mConnectionState =BluetoothProfile.STATE_CONNECTING ;
                return true;
            }
        } else {
            Log.w("connectBtDevice", "BluetoothAdapter not initialized.");
            return false;
        }
    }

        public int getConnectState() {
            return this.mConnectionState;
        }

        public boolean isConnected() {
            return this.mConnectionState ==BluetoothProfile.STATE_CONNECTED;
        }

        void disconnectBtDevice() {
            //stop();
            if(this.mBluetoothGatt != null) {
                this.mBluetoothGatt.disconnect();
                this.mConnectionState = BluetoothProfile.STATE_DISCONNECTING;
            }
        }

        void closeBtConection() {
            //stop();
            if(this.mBluetoothGatt != null) {
                this.mBluetoothGatt.close();
                this.mBluetoothGatt = null;
                this.mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
            }
        }

        public void exitBt() {
            client_is_closed=true;
            if(this.isConnected())
                this.disconnectBtDevice();
            else
            {
                closeBtConection();
            }
        }

        boolean setup_connection()
        {
            if(mBluetoothGatt==null||!this.isConnected())
                return false;
            BluetoothGattService serialService = this.mBluetoothGatt.getService(cfg.getSerial_service_uuid());
            if(serialService == null) {
                Log.e("SimpleBleSerialClient", "The required serial service not found!");
                return false;
            }
            UUID rx_char_uuid=cfg.getSerial_rx_char_uuid();
            if(rx_char_uuid!=null) {
                RxChar=serialService.getCharacteristic(cfg.getSerial_rx_char_uuid());
                if (RxChar == null) {
                    Log.e("SimpleBleSerialClient", "The reqired write interface is not supported!");
                    return false;
                }
            }
            UUID tx_char_uuid=cfg.getSerial_tx_char_uuid();
            if(tx_char_uuid!=null)
            {
                TxChar=serialService.getCharacteristic(tx_char_uuid);
                if (TxChar == null) {
                    Log.e("SimpleBleSerialClient", "The reqired read interface is not supported!");
                    return false;
                }
                this.mBluetoothGatt.setCharacteristicNotification(TxChar, true);
                BluetoothGattDescriptor descriptor = TxChar.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                this.mBluetoothGatt.writeDescriptor(descriptor);
            }
            return true;
        }

        public boolean send(byte[] msg) {
            if(!isConnected())
            {
                return false;
            }
            try {
                if (RxChar != null) {
                    RxChar.setValue(msg);
                    return this.mBluetoothGatt.writeCharacteristic(RxChar);
                }
                return true;
            }
            catch(Exception e)
            {
                System.out.println(e.getLocalizedMessage());
                return false;
            }
        }

    @SuppressLint({"NewApi"})
    public class MyBluetoothGattCallback extends BluetoothGattCallback {

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if(mBluetoothGattCallback!=null)
            {
                mBluetoothGattCallback.onCharacteristicChanged(gatt,characteristic);
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(mBluetoothGattCallback!=null)
            {
                mBluetoothGattCallback.onCharacteristicRead(gatt,characteristic,status);
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(mBluetoothGattCallback!=null)
            {
                mBluetoothGattCallback.onCharacteristicWrite(gatt,characteristic,status);
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("SimpleBleSerialClient", "Service discoved!");
            if(mBluetoothGattCallback!=null)
            {
                boolean bSuccess=setup_connection();
                if(bSuccess) {
                    mBluetoothGattCallback.onServicesDiscovered(gatt, status);
                }
                else
                {
                    exitBt();
                }
            }
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            mConnectionState= newState;
            if((status==BluetoothGatt.GATT_SUCCESS)&&newState==BluetoothProfile.STATE_CONNECTED)
            {
                mBluetoothGatt.discoverServices();
            }
            else if(newState==BluetoothProfile.STATE_DISCONNECTED)
            {
                    closeBtConection();
            }
            else
            {
                Log.d("SimpleBleSerialClient", "state="+newState);
            }
            if(mBluetoothGattCallback!=null)
            {
                mBluetoothGattCallback.onConnectionStateChange(gatt, status, newState);
            }
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

        }
    }
}
