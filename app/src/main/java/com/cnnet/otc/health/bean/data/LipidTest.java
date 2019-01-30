package com.cnnet.otc.health.bean.data;

import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.util.StringUtil;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Administrator on 2016/8/11.
 */
public class LipidTest
{
    private String test_descr="";

    /**
     * 总胆固醇数据字段
     */
    public static final String DATA_CHOLESTEROL = "CHOL";
    /**
     * 甘油三酯数据字段
     */
    public static final String DATA_TRIGLYCERIDES = "TG";
    /**
     * 高密度脂蛋白数据字段
     */
    public static final String DATA_HDL = "HDL";
    /**
     * 低密度脂蛋白数据字段
     */
    public static final String DATA_LDL = "LDL";

    private List<LipidDataItem> lipid_data_items=null;

    List<LipidDataItem> create_lipid_test_items()
    {
        List<LipidDataItem> test_items=new ArrayList<LipidDataItem>();
        test_items.add(new LipidDataItem("CHOL","总胆固醇",DATA_CHOLESTEROL,"mmol/L"));
        test_items.add(new LipidDataItem("HDL CHOL","高密度脂蛋白",DATA_HDL,"mmol/L"));
        test_items.add(new LipidDataItem("TRIG","甘油三脂",DATA_TRIGLYCERIDES,"mmol/L"));
        test_items.add(new LipidDataItem("CALC LDL","低密度脂蛋白",DATA_LDL,"mmol/L"));
        return test_items;
    }

    public boolean set_test_type(String test_typ)
    {
        if(test_typ.equals("P"))
        {
            setTest_descr("血脂四项");
            lipid_data_items=create_lipid_test_items();
            return true;
        }
        return false;
    }

    public String getTest_descr() {
        return test_descr;
    }

    public void setTest_descr(String test_descr) {
        this.test_descr = test_descr;
    }

    public void parse_value(String typStr,String valStr)
    {
        LipidDataItem itm= null;
        List<LipidDataItem> the_itms=getLipid_data_items();
        for(int i=the_itms.size()-1;i>=0;i--)
        {
            LipidDataItem the_itm=the_itms.get(i);
            if(typStr.startsWith(the_itm.getTag()))
            {
                itm=the_itm;
                break;
            }
        }
        if(itm!=null)
        {
            if(!valStr.endsWith(itm.getUnitStr()))
            {
                itm.val=LipidDataItem.STRANGE_VALUE;
                itm.setDescr(CommConst.VALUE_STRANGE);
                return;
            }
            float val = Float.parseFloat(StringUtil.getFirstFloatStr(valStr));
            itm.val=StringUtil.getBigDecimal(2, val);
            if (valStr.startsWith(">"))
                itm.setDescr(CommConst.VALUE_GREATER);
            else if (valStr.startsWith("<"))
                itm.setDescr(CommConst.VALUE_SMALLER);

        }
    }


    public List<LipidDataItem> getLipid_data_items() {
        return lipid_data_items;
    }
}
