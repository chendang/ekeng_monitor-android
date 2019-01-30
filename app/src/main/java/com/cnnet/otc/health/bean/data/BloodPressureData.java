package com.cnnet.otc.health.bean.data;

import android.content.Context;
import android.util.Log;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.db.DBHelper;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.tasks.UploadAllNewInfoTask;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;

import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author Administrator
 * 血压
 */
public class BloodPressureData implements MyCommData {

	/**
	 * 显示结果
	 */
	private final String BT_DISPLAY_RESULT = "FE6A735A";
	/**
	 * 血压：收缩压字段
	 */
	public static final String DATA_BP_HIGHT = "BPHigh";
	/**
	 * 血压：舒张压字段
	 */
	public static final String DATA_BP_LOW = "BPLow";
	/**
	 * 血压：脉压差字段
	 */
	public static final String DATA_BP_PULSE = "BPC";
	/**
	 * 血压争对的图表对象
	 */
	//private MyLineChartView myLineChartView;

	public static final String DATA_BP_PR = "BPPR";

	/**
	 * 值的类型：1整形，2浮点型
	 */
	private final int VALUE_RANGE = 1;

	private Context ctx;

	private long nativeRecordId;
	private String mUniqueKey = null;
	public BloodPressureData(Context ctx, long nativeRecordId,String mUniqueKey){
		this.nativeRecordId = nativeRecordId;
		this.ctx = ctx;
		this.mUniqueKey=mUniqueKey;
	}
	


	@Override
	public void todo(byte[] read) {
		String hexString = StringUtil.byteToHexString(read);//将数据进行Hex运算后得到的结果字符串
		Log.e("BloodPressureData", "value = " + hexString);
		//6A 73 5A 00 70 42 42 FE
		if (hexString.startsWith(BT_DISPLAY_RESULT)) {
			byte[] temp = new byte[2];
			temp[0] = read[4];
			temp[1] = read[5];
			int highPressure = (Integer.parseInt(StringUtil.byteToHexString(temp), 16)) & 0x7fff;
			temp = new byte[1];
			temp[0] = read[6];
			int lowPressure = (Integer.parseInt(StringUtil.byteToHexString(temp), 16));
			temp = new byte[1];
			temp[0] = read[7];
			int heartRate = (Integer.parseInt(StringUtil.byteToHexString(temp), 16));
			String data = ctx
					.getString(R.string.YOUR_SYSTOLIC)
					+ highPressure
					+ "mmHg,"
					+ ctx
					.getString(R.string.YOUR_DIASTOLIC)
					+ lowPressure
					+ "mmHg,"
					+ ctx.getString(R.string.YOUR_HEART)
					+ heartRate
					+ ctx.getString(R.string.TIMES)
					+ ctx.getString(R.string.PERIOD);
			System.out.println("血压数据：" + data);
			nativeRecordId=new Date().getTime();
			SysApp.getMyDBManager().addWaitForInspector(nativeRecordId,mUniqueKey,mUniqueKey,mUniqueKey);
			SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_BP_HIGHT, highPressure, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
			SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_BP_LOW, lowPressure, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
			SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_BP_PULSE, highPressure-lowPressure, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
			SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_BP_PR, heartRate, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
			UploadAllNewInfoTask.submitOneRecordInfo(ctx,mUniqueKey, nativeRecordId,
					new SubmitServerListener() {
						@Override
						public void onResult(int result) {
							DialogUtil.cancelDialog();
							if (result == 0) { //success
							} else if(result == -2){
								// ToastUtil.TextToast(ctx.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
							} else {
								// ToastUtil.TextToast(ctx.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
							}
						}
					});
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, data));
			/*if (DisplayView.fa != null) {
				DisplayView.fa.save();
				DisplayView.fa.hander.sendEmptyMessage(0);
				BtNormalManager.inserted = true;
			}*/
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
		}
	}

	@Override
	public void todoConnected() {

	}

	@Override
	public void todoDisconnected() {

	}

	@Override
	public void todoConnecting() {

	}

	@Override
	public void todoDisconnected_failed() {

	}

	@Override
	public List<RecordItem>[] getRecordList(String mUniqueKey) {
		List<RecordItem>[] lists = new List[4];
		lists[0] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_BP_HIGHT);
		lists[1] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_BP_LOW);
		lists[2] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_BP_PULSE);
		lists[3] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_BP_PR);
		return lists;
	}

	@Override
	public List<RecordItem> getRecordAllList(String mUniqueKey) {
		return SysApp.getMyDBManager().getRecordAllInfoByType(mUniqueKey, DATA_BP_HIGHT, DATA_BP_LOW,DATA_BP_PULSE,DATA_BP_PR);
	}

	@Override
	public String[] getInsName() {
		return new String[]{"收缩压\n\r(mmHg)", "舒张压\n\r(mmHg)","脉压差\n\r(mmHg)","心率\n\r(bpm)"};
	}

	@Override
	public String[] getInsUnit() {
		return new String[]{"","","",""};
	}

	@Override
	public int[] getInsRange() {
		return new int[]{1, 1, 1,1};
	}

	@Override
	public void refreshRealTime() {

	}

	@Override
	public boolean refreshData() {

		return false;
	}

	@Override
	public int getdisconnected_failed() {
		return 0;
	}
}
