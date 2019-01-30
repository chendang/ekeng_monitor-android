package com.cnnet.otc.health.ble_middle;


import com.cnnet.otc.health.ble.SimpleBleSerialClientCfg;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.SysApp;

import java.util.UUID;

/**
 * Created by ljs-jj on 2017/5/17.
 */
public class BleCfgFactory {
    public static SimpleBleSerialClientCfg createBleSO2Cfg()
    {
        switch(SysApp.check_type) {
            case OXIMETRY:
            UUID RX_SERVICE_UUID = UUID.fromString("CDEACB80-5235-4C07-8846-93A37EE6B86D");
            UUID RX_CHAR_UUID = UUID.fromString("CDEACB82-5235-4C07-8846-93A37EE6B86D");
            UUID TX_CHAR_UUID = UUID.fromString("CDEACB81-5235-4C07-8846-93A37EE6B86D");
            SimpleBleSerialClientCfg cfg = new SimpleBleSerialClientCfg();
            //cfg.setDev_addr("98:7B:F3:73:7D:9E");
            cfg.setSerial_service_uuid(RX_SERVICE_UUID);
            cfg.setSerial_tx_char_uuid(TX_CHAR_UUID);
            cfg.setSerial_rx_char_uuid(RX_CHAR_UUID);
            cfg.setDev_name("血氧4.0");
            return cfg;
        }
        return null;
    }

    public static SimpleBleSerialClientCfg createBleCardiacLipidCfg() {
        UUID RX_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
        UUID TX_CHAR_UUID = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
        SimpleBleSerialClientCfg cfg = new SimpleBleSerialClientCfg();
        cfg.setSerial_service_uuid(RX_SERVICE_UUID);
        cfg.setSerial_tx_char_uuid(TX_CHAR_UUID);
        cfg.setSerial_rx_char_uuid(null);
        cfg.setDev_name("血脂仪");
        return cfg;
    }

}
