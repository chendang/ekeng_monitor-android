/**
 * @Title: FetalHeart.java 
 * @Package com.gplus.web.model 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author fish  
 * @date 2014年10月17日 下午1:54:03 
 * @version V1.0   
 */
package com.cnnet.otc.health.bean;

/**
 * @author Administrator
 * 胎心
 */
public class FetalHeart {

	private String recordTime;
	
	private Double fhr;
	
	
	public FetalHeart (String recordtime, double fhr){
		this.recordTime =recordtime;
		this.fhr =fhr;
		
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public Double getFhr() {
		return fhr;
	}

	public void setFhr(Double fhr) {
		this.fhr = fhr;
	}
	
	
	
}
