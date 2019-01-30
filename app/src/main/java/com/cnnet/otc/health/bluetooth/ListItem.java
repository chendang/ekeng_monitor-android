package com.cnnet.otc.health.bluetooth;

public class ListItem {
	protected String ino;
	protected String date;
	protected String consistency;
	protected boolean isSelected;
	protected int sno;// ��ʾ�����

	public ListItem(String times, String date, String consistency,
			boolean isSelected, int sno) {
		this.ino = times;
		this.date = date;
		this.consistency = consistency;
		this.isSelected = isSelected;
		this.sno = sno;
	}
}