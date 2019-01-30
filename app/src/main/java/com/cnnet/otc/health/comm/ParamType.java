package com.cnnet.otc.health.comm;

/**
 * Created by Administrator on 2016/10/7.
 */
public enum ParamType {
    HEIGHT(1,"身高",1,"cm"),
    WAIST(2,"腰围",1,"cm"),
    WEIGHT	(3,"体重",2,"kg"),
    BFP	(4,"体脂肪率",2,"%"),
    BMI	(5,"体质指数",2,""),
    SpO2(7,"血氧饱和度",2,"%"),
    TEMP(8,"体温",2,"℃"),
    GLU	(9,"血糖",2,"mmol/L"),
    CHOL(10,"胆固醇",2,"mmol/L"),
    TG(11,"甘油三酯",2,"mmol/L"),
    U_PH(12,"尿PH值",2,""),
    SG(13,"尿比重",2,""),
    UA(14,"尿酸",2,"mmol/L"),
    BPHigh(15,"收缩压",1,"mmHg"),
    BPLow(16,"舒张压",1,"mmHg"),
    BPPR(17,"脉率",1,""),
    SLEEP_TIME	(18,"睡眠时长",2,""),
    DEEP_SLEEP	(19,"深睡眠时长",2,""),
    STEP_NUMBER(22,"步数",1,"步"),
    RUNNING_NUMBER(23,"跑步数",1,"步"),
    BPC(25,"脉压",1,"mmHg"),
    HDL(26,"高密度脂蛋白",2,"mmol/L"),
    LDL(27,"低密度脂蛋白",2,"mmol/L");

    private int id;
    private String title;
    private int range;
    private String unit;

    ParamType(int id,String title, int range,String unit)
    {
        this.setId(id);
        this.setTitle(title);
        this.setRange(range);
        this.setUnit(unit);
    }

    public static ParamType find(String iType)
    {
        for(ParamType param_type:ParamType.values())
        {
            if(param_type.toString() ==iType)
                return param_type;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
