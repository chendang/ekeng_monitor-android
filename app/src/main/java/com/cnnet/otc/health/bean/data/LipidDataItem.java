package com.cnnet.otc.health.bean.data;

import com.cnnet.otc.health.comm.CommConst;

/**
 * Created by Administrator on 2016/8/11.
 */

public class LipidDataItem
{
    public static float NONE_VALUE=-1f;
    public static float STRANGE_VALUE=999f;


    private String typeStr;
    float val=NONE_VALUE;
    private String unitStr;
    private String descr="";
    private String title="";
    private String tag="";

    public LipidDataItem(String tag,String title,String typStr,String the_unitStr)
    {
        this.setTag(tag);
        this.setTitle(title);
        this.setTypeStr(typStr);
        this.setUnitStr(the_unitStr);
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String the_descr) {
        this.descr=the_descr;
    }

    public String getUnitStr() {
        return unitStr;
    }

    public void setUnitStr(String unitStr) {
        this.unitStr = unitStr;
    }

    public String getModifier() {
        if(descr!=null)
        {
            if(descr.equals(CommConst.VALUE_SMALLER))
            {
                return "<";
            }
            else if(descr.equals(CommConst.VALUE_GREATER))
            {
                return ">";
            }
        }
        return"";
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
