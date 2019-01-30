package com.cnnet.otc.health.bean.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/6.
 */
public enum GluTestCode {
    GLU_EMPTY("M000101","空腹"),
    GLU_2h("M000102","早餐后2h"),
    GLU_BEFORE_LUNCH("M000103","午餐前"),
    GLU_AFTER_LUNCH("M000104","午餐后2h"),
    GLU_BEFORE_MEAL("M000105","晚餐前"),
    GLU_AFTER_MEAL("M000106","晚餐后2h"),
    GLU_BEFORE_SLEEP("M000107","睡前"),
    GLU_MIDNIGHT("M000108","夜间"),
    GLU_RANDOM("M000109","随机");

    private String test_code;
    private String title;

     GluTestCode(String test_code,String title)
    {
        this.test_code=test_code;
        this.title=title;
    }

    public static List<GluTestCode> get7PointTestCodes()
    {
        List<GluTestCode> test_codes=new ArrayList<GluTestCode>();
        test_codes.add(GLU_EMPTY);
        test_codes.add(GLU_2h);
        test_codes.add(GLU_BEFORE_LUNCH);
        test_codes.add(GLU_AFTER_LUNCH);
        test_codes.add(GLU_BEFORE_MEAL);
        test_codes.add(GLU_AFTER_MEAL);
        test_codes.add(GLU_MIDNIGHT);
        return test_codes;
    }

    public static String[] get7PointTestTitles()
    {
        List<GluTestCode> test_codes=get7PointTestCodes();
        String [] test_code_strs=new String[test_codes.size()];
        for(int i=0;i<test_codes.size();i++)
        {
            test_code_strs[i]=test_codes.get(i).getTitle();
        }
        return test_code_strs;
    }

    public static GluTestCode find(String test_code)
    {
        for(GluTestCode testCode:GluTestCode.values())
        {
            if(testCode.getTest_code().equals(test_code))
                return testCode;
        }
        return null;
    }

    @Override
    public String toString()
    {
        return getTitle();
    }

    public String getTest_code() {
        return test_code;
    }


    public String getTitle() {
        return title;
    }
}
