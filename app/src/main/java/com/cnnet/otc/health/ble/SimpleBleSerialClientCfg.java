package com.cnnet.otc.health.ble;

import java.util.UUID;

/**
 * Created by Administrator on 2017/5/17.
 */
public class SimpleBleSerialClientCfg {
    private UUID serial_service_uuid;
    private UUID serial_rx_char_uuid;
    private UUID serial_tx_char_uuid;
    private String  dev_name;
    private String  dev_addr;

    public UUID getSerial_service_uuid() {
        return serial_service_uuid;
    }

    public void setSerial_service_uuid(UUID serial_service_uuid) {
        this.serial_service_uuid = serial_service_uuid;
    }

    public UUID getSerial_rx_char_uuid() {
        return serial_rx_char_uuid;
    }

    public void setSerial_rx_char_uuid(UUID serial_rx_char_uuid) {
        this.serial_rx_char_uuid = serial_rx_char_uuid;
    }

    public UUID getSerial_tx_char_uuid() {
        return serial_tx_char_uuid;
    }

    public void setSerial_tx_char_uuid(UUID serial_tx_char_uuid) {
        this.serial_tx_char_uuid = serial_tx_char_uuid;
    }

    public String getDev_name() {
        return dev_name;
    }

    public void setDev_name(String dev_name) {
        this.dev_name = dev_name;
    }

    public String getDev_addr() {
        return dev_addr;
    }

    public void setDev_addr(String dev_addr) {
        this.dev_addr = dev_addr;
    }
}
