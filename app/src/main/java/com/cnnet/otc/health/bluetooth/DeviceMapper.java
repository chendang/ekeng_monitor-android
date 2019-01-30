package com.cnnet.otc.health.bluetooth;

import com.cnnet.otc.health.comm.CheckType;

import java.util.Hashtable;

/**
 * Created by Administrator on 2018/5/4.
 */
public class DeviceMapper {
    private CheckType chk_typ;
    private String    dev_name_prefix;
    private String    disp_name;

    public DeviceMapper(CheckType chk_typ,String dev_name_prefix,String disp_name)
    {
        this.chk_typ=chk_typ;
        this.dev_name_prefix=dev_name_prefix;
        this.disp_name=disp_name;
    }

    public CheckType getChk_typ() {
        return chk_typ;
    }

    public void setChk_typ(CheckType chk_typ) {
        this.chk_typ = chk_typ;
    }

    public String getDev_name_prefix() {
        return dev_name_prefix;
    }

    public void setDev_name_prefix(String dev_name_prefix) {
        this.dev_name_prefix = dev_name_prefix;
    }

    public String getDisp_name() {
        return disp_name;
    }

    public void setDisp_name(String disp_name) {
        this.disp_name = disp_name;
    }
}
