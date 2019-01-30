package com.cnnet.otc.health.events;


import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.comm.CommConst;

import java.util.List;

public class MembersObtainedEvent {
	private int resultCode ;
    private String err_msg;
	private List<Member> members = null;

	public static MembersObtainedEvent createDataObtainEvt(List<Member> members)
	{
		MembersObtainedEvent evt=new MembersObtainedEvent();
		evt.members=members;
		evt.resultCode=CommConst.FLAG_MEMBERS_OBTAINED;
		return evt;
	}

	public static MembersObtainedEvent createDownloadingErrEvt(String err_msg)
	{
		MembersObtainedEvent evt=new MembersObtainedEvent();
		evt.err_msg=err_msg;
		evt.resultCode=CommConst.FLAG_MEMBERS_ERR;
		return evt;
	}

	public static MembersObtainedEvent createMembersReadyEvt()
	{
		MembersObtainedEvent evt=new MembersObtainedEvent();
		evt.resultCode=CommConst.FLAG_MEMBERS_READY;
		return evt;
	}

	private MembersObtainedEvent(){

	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public String getErr_msg() {
		return err_msg;
	}

	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}
}
