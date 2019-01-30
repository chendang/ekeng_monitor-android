package com.cnnet.otc.health.views.adapter;

import com.cnnet.otc.health.bean.RecordItem;

import java.util.EventObject;

/**
 * Created by Administrator on 2016/10/6.
 */
public class GluDeleteEvent extends EventObject {
    private RecordItem item;
    public GluDeleteEvent(Object source,RecordItem item)
    {
        super(source);
        this.setItem(item);
    }

    public RecordItem getItem() {
        return item;
    }

    public void setItem(RecordItem item) {
        this.item = item;
    }
}
