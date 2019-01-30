package com.cnnet.otc.health.bean.data;

import android.content.Context;
import android.util.Log;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.comm.TempMode;
import com.cnnet.otc.health.comm.TempState;
import com.cnnet.otc.health.db.DBHelper;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.tasks.UploadAllNewInfoTask;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.views.MyLineChartView;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author Administrator
 * 体温
 */
public class TemperatureData implements MyCommData {

	/**
	 * 连机命令
	 */
	private final String ONLINE_COMMAND = "FE6A725A55AABB";
	/**
	 * 请滴血
	 */
	private final String PLEASE_BLEEDING = "FE6A725A55BBBB";
	/**
	 * 倒数时间显示
	 */
	private final String COUNTDOWN_TIME_DISPLAY = "FE6A725A55CCBB";
	/**
	 * 显示结果
	 */
	private final String DISPLAY_RESULT = "FE6A725A";
	/**
	 * 体温：体温字段
	 */
	public static final String DATA_TEMP = "TEMP";

	/**
	 * 图表对象
	 */
	private MyLineChartView myLineChartView;
	private Context context;

	private boolean inserted = false;

	private long nativeRecordId;
	private String mUniqueKey = null;
	public TemperatureData(Context ctx, MyLineChartView myLineChartView, long nativeRecordId,String mUniqueKey){
		this.myLineChartView = myLineChartView;
		this.context = ctx;
		this.nativeRecordId = nativeRecordId;
		this.mUniqueKey=mUniqueKey;

	}

	@Override
	public void todo(byte[] read) {
		String hexString = StringUtil.byteToHexString(read);//将数据进行Hex运算后得到的结果字符串
		String text = myLineChartView.getTitle();
		if (hexString.startsWith(ONLINE_COMMAND)
				&& (!text.startsWith(context.getString(R.string.YOUR))
				&& !text.startsWith(context.getString(R.string.WARNING)))) {
			//DisplayView.text = context.getString(R.string.PRESS_TEST);
			//DisplayView.fa.hander.sendEmptyMessage(5);
			inserted = false;
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
					context.getString(R.string.PRESS_TEST)));
		} else if (hexString.startsWith("FE6A725A55BBBB")) {
			//DisplayView.text = context.getString(R.string.PRESS_TEST);
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
					context.getString(R.string.PRESS_TEST)));
		} else if (hexString.startsWith("FE6A725A55CCBB")) {
			//DisplayView.text = context.getString(R.string.TESTING);
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
					context.getString(R.string.TESTING)));
		} else if (hexString.startsWith("FE6A725A")
				&& !inserted) {
			byte[] temp = new byte[2];
			temp[0] = read[4];
			temp[1] = read[5];
			// FudakangActivity.temperature
			short temperatureUnit = (short) ((read[6] & 0x80) >> 7);
			short temperatureMode = (short) ((read[6] & 0x60) >> 5);
			short temperatureStatus = (short) ((read[6] & 0x1c) >> 2);

			double tempFloat = (Integer.parseInt(StringUtil.byteToHexString(temp), 16)) / 100.0;

			int scale = 1;// 设置位数
			int roundingMode = BigDecimal.ROUND_DOWN;// 表示四舍五入
			BigDecimal bd = new BigDecimal(tempFloat);
			bd = bd.setScale(scale, roundingMode);
			float temperature = bd.floatValue();

			String unitText = "℃";
			/* 单位转换 */
			/*if (temperatureUnit == 1) {  //当定义为华氏度时,数据仍是摄氏度的数值
				temperature = temperature * 9 / 5 + 32;
				unitText = "℉";
				DisplayView.text = context.getString(R.string.YOUR_TEMPERATURE)
						+ temperature + "℉";
			} else {
				unitText = "℃";
			}*/

			/* 模式 */
			String modeText = context.getString(R.string.YOUR_ENVI_TEMPERATURE);
			if (temperatureMode == TempMode.MODE_HUMAN_TEMPERATURE) {
				modeText = context.getString(R.string.YOUR_HUMAN_TEMPERATURE);
			} else if (temperatureMode == TempMode.MODE_OBJECT_TEMPERATURE) {
				modeText = context.getString(R.string.YOUR_OBJECT_TEMPERATURE);
			} else if (temperatureMode == TempMode.MODE_ENVI_TEMPERATURE) {
				modeText = context.getString(R.string.YOUR_ENVI_TEMPERATURE);
			}

			text = modeText + temperature + unitText;

			if (temperatureStatus == TempState.NORMAL) {
				text = modeText + temperature + unitText;
				Log.d(DATA_TEMP, " result : " + text + "   ;   id=" + nativeRecordId);
				nativeRecordId=new Date().getTime();
				SysApp.getMyDBManager().addWaitForInspector(nativeRecordId,mUniqueKey,mUniqueKey,mUniqueKey);
				SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_TEMP, temperature, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());


				UploadAllNewInfoTask.submitOneRecordInfo(context,mUniqueKey, nativeRecordId,
						new SubmitServerListener() {
							@Override
							public void onResult(int result) {
								DialogUtil.cancelDialog();
								if (result == 0) { //success
								} else if(result == -2){
									// ToastUtil.TextToast(context.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
								} else {
									// ToastUtil.TextToast(context.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
								}
							}
						});
			} else if (temperatureStatus == TempState.MEASURING_TEMP_LOW) {
				text = context.getString(R.string.TEMPERATURE_LOW);
			} else if (temperatureStatus == TempState.MEASURING_TEMP_HIGTH) {
				text = context.getString(R.string.TEMPERATURE_HIGTH);
			} else if (temperatureStatus == TempState.ENVIZ_TEMP_HIGTH) {
				text = context.getString(R.string.ENVI_HIGTH);
			} else if (temperatureStatus == TempState.ENVIZ_TEMP_LOW) {
				text = context.getString(R.string.ENVI_LOW);
			} else if (temperatureStatus == TempState.EEPROM) {
				text = context.getString(R.string.EEPROM);
			} else if (temperatureStatus == TempState.SENSOR) {
				text = context.getString(R.string.SENSOR);
			}

			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, text));
			if (context != null) {
				//DisplayView.fa.save();
				//DisplayView.fa.hander.sendEmptyMessage(0);
				EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
			}
			inserted = true;
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
		List<RecordItem>[] lists = new List[1];
		lists[0] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_TEMP);
		return lists;
	}

	@Override
	public List<RecordItem> getRecordAllList(String mUniqueKey) {
		return SysApp.getMyDBManager().getRecordAllInfoByType(mUniqueKey, DATA_TEMP);
	}

	@Override
	public String[] getInsName() {
		return new String[]{"体温(℃)"};
	}

	@Override
	public String[] getInsUnit() {
		return new String[]{""};
	}

	@Override
	public int[] getInsRange() {
		return new int[]{2};
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
