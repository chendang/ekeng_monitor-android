package com.cnnet.otc.health.bean;

import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.bean.data.*;
import com.cnnet.otc.health.bean.data.MyData;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Administrator on 2017/4/12.
 */
public class OTCDeviceFilter {
    Hashtable<String,String> device_name_mapper=new  Hashtable<String,String>();
    String filter_str="*";
    String dev_name="";
    void init_device_name_map()
    {
        device_name_mapper.put("BGM-3.0","稳和血糖仪");
        device_name_mapper.put("Electronic Scale","稳和体重秤");
        device_name_mapper.put("IRT-3.0","稳和体温计");
        device_name_mapper.put("BeneCheck","BeneCheck尿酸仪");
        device_name_mapper.put("BPM-3.0","稳和血压计");
        device_name_mapper.put("Medical","稳和血氧仪");
        device_name_mapper.put("CardioChek","CardioChek血脂仪");
        //device_name_mapper.put("Medical","稳和血氧仪");
    }

    public OTCDeviceFilter(MyData data)
    {
        init_device_name_map();
        if(data instanceof BloodGlucoseData)
            filter_str="BGM-3.0";
        else if(data instanceof  BloodPressureData)
            filter_str="BPM-3.0";
        else if(data instanceof  WeightData)
            filter_str="Electronic Scale";
        else if(data instanceof  TemperatureData)
            filter_str="IRT-3.0";
        else if(data instanceof  UricacidData)
            filter_str="BeneCheck";
        else if(data instanceof  OximetryData)
            filter_str="Medical";
        else if(data instanceof  LipidData)
            filter_str="CardioChek";
        dev_name=device_name_mapper.get(filter_str);
    }

    public OTCDeviceFilter(String filter_str)
    {
        init_device_name_map();
        this.filter_str=filter_str;
        dev_name=device_name_mapper.get(filter_str);
    }

    public List<MyBlueToothDevice> filter( List<MyBlueToothDevice> dev_list)
    {
        List<MyBlueToothDevice> filtered_dev_list=new ArrayList<MyBlueToothDevice>();
        for(MyBlueToothDevice dev:dev_list)
        {
            if(dev.getName().startsWith(filter_str))
            {
                dev.setDevice_name(dev_name);
                filtered_dev_list.add(dev);
            }
        }
        return filtered_dev_list;
    }

    public MyBlueToothDevice filter(MyBlueToothDevice dev)
    {
        if(dev.getName()==null)
            return null;
        if(dev.getName().startsWith(filter_str))
        {
            dev.setDevice_name(dev_name);
            return dev;
        }
        return null;
    }
}
