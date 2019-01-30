package com.cnnet.otc.health.bluetooth;

import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.SysApp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Administrator on 2018/5/4.
 */
public class DeviceMapperManager {

    Hashtable<CheckType,List<DeviceMapper>> mappers=new Hashtable<CheckType,List<DeviceMapper>>();

    static DeviceMapperManager manager=null;

    public static DeviceMapperManager getInstance()
    {
        if(manager==null)
        {
            manager=new DeviceMapperManager();
        }
        return manager;
    }

    DeviceMapperManager()
    {
        load_default();
    }

    void add_mapper(DeviceMapper mapper)
    {
        List<DeviceMapper> mapper_grp=null;
        if(mappers.containsKey(mapper.getChk_typ()))
        {
            mapper_grp=mappers.get(mapper.getChk_typ());
        }
        else
        {
            mapper_grp=new ArrayList<DeviceMapper>();
            this.mappers.put(mapper.getChk_typ(),mapper_grp);
        }
        for(DeviceMapper the_mapper:mapper_grp)
        {
            if(the_mapper.getDev_name_prefix().equals(mapper.getDev_name_prefix()))
            {
                mapper_grp.remove(the_mapper);
                break;
            }
        }
        mapper_grp.add(mapper);
    }

    void load_default()
    {
        add_mapper(new DeviceMapper(CheckType.BLOOD_GLUCOSE,"BGM-3.0","稳和血糖仪"));
        add_mapper(new DeviceMapper(CheckType.BLOOD_GLUCOSE,"OTC-GLU","稳和血糖仪"));
        add_mapper(new DeviceMapper(CheckType.BLOOD_GLUCOSE,"0TC-GLU","稳和血糖仪"));
        add_mapper(new DeviceMapper(CheckType.WEIGHT,"Electronic Scale","稳和体重秤"));
        add_mapper(new DeviceMapper(CheckType.THERMOMETER,"IRT-3.0","稳和体温计"));
        add_mapper(new DeviceMapper(CheckType.URIC_ACID,"BeneCheck","BeneCheck尿酸仪"));
        add_mapper(new DeviceMapper(CheckType.BLOOD_PRESSURE,"BPM-3.0","稳和血压计"));
        add_mapper(new DeviceMapper(CheckType.OXIMETRY,"Medical","稳和血氧仪"));
        add_mapper(new DeviceMapper(CheckType.LIPID,"CardioChek","CardioChek血脂仪"));

    }

    public MyBlueToothDevice filter(MyBlueToothDevice dev)
    {
        if(dev.getName()==null)
            return null;
        List<DeviceMapper> mapper_grp=mappers.get(SysApp.check_type);
        if(mapper_grp==null)
        {
            return null;
        }
        for(DeviceMapper mapper:mapper_grp)
        {
            if(dev.getName().startsWith(mapper.getDev_name_prefix()))
            {
                dev.setDevice_name(mapper.getDisp_name());
                return dev;
            }
        }
        return null;
    }
}
