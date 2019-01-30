package com.cnnet.otc.health.comm;

public enum CheckType {
	BLOOD_GLUCOSE("血糖仪"),   //血糖仪
	BLOOD_PRESSURE("血压计"),   //血压计
	THERMOMETER("体温计"),       //体温计
	OXIMETRY("血氧仪"),          //血氧饱和度
	LIPID("血脂仪"),            //血脂
	WEIGHT("体脂称"),    //体重
	URIC_ACID("尿酸仪"),   //尿酸
	NONE(""),
	LS_WIFI_BLOOD_PRESSURE(""),//乐心wifi 血压计
	LS_WIFI_WEIGHT("");
	private String title="";
	CheckType(String str)
	{
		this.title=str;
	}
	//乐心wifi 体重秤
	public String getTitle() {
		return title;
	}
}
