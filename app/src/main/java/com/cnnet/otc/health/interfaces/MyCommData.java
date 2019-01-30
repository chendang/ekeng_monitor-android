package com.cnnet.otc.health.interfaces;

import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.data.MyData;

import java.util.List;

/**
 * Created by SZ512 on 2016/1/16.
 */
public interface MyCommData extends MyData {

    List<RecordItem>[] getRecordList(String mUniqueKey);

    List<RecordItem> getRecordAllList(String mUniqueKey);

    String[] getInsName();

    String[] getInsUnit();
    /**
     * 值的类型：1整形，2浮点型
     */
    int[] getInsRange();

    void refreshRealTime();

    boolean refreshData();

    int getdisconnected_failed();
}
