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
import com.cnnet.otc.health.views.MyLineChartView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 血糖
 * @author Administrator
 *
 */
public class BloodGlucoseData implements MyCommData {

	private final String TAG = "BloodGlucoseData";
	private Hashtable<GluTestCode,RecordItem> glu_datas=null;
	private GluTestCode cur_test_code=null;

	public String getTestcodeTitle()
	{
		return  (cur_test_code==null)? "":cur_test_code.getTitle();
	}

	public  List<GluTestCode> getUntested7PointTestCode()
	{
		List<GluTestCode> test_codes=GluTestCode.get7PointTestCodes();
		List<GluTestCode> untested_list=new ArrayList<GluTestCode>();
		for(GluTestCode test_code:test_codes)
		{
			if(getGlu_datas().get(test_code)==null)
			{
				untested_list.add(test_code);
			}

		}
		return untested_list;
	}



	public String[] getUntested7PointTitles()
	{
		List<GluTestCode> test_codes=getUntested7PointTestCode();
		String [] test_code_strs=new String[test_codes.size()];
		for(int i=0;i<test_codes.size();i++)
		{
			test_code_strs[i]=test_codes.get(i).getTitle();
		}
		return test_code_strs;
	}

	public void deleteTestValue(RecordItem itm)
	{
		GluTestCode testCode=GluTestCode.find(itm.getTestCode());
		getGlu_datas().remove(testCode);
		SysApp.getMyDBManager().deleteRecordItemByType(itm);
	}

	/**
	 * 连机命令
	 */
	private final String BT_ONLINE_COMMAND = "FE6A755A55AABBCC";
	/**
	 * 请滴血
	 */
	private final String BT_PLEASE_BLEEDING = "FE6A755A55BBBBCC";
	/**
	 * 倒数时间显示
	 */
	private final String BT_COUNTDOWN_TIME_DISPLAY = "FE6A755A55";
	/**
	 * 显示结果
	 */
	private final String BT_DISPLAY_RESULT = "FE6A755A";

	/**
	 * 血糖数据字段
	 */
	public static final String DATA_BLOOD_GLUCOSE = "GLU";

	/**
	 * 测试血糖时是否将试纸插入卡槽:默认为false
	 */
	private boolean inserted = false;;

	public int todoDisconnected_failed, todoDisconnected ;

	private long nativeRecordId;
	private String mUniqueKey = null;
	private Context context;

	class RetVal
	{
		private boolean bool_val;
		public RetVal(boolean isTrue)
		{
			setBool_val(isTrue);
		}

		public boolean isBool_val() {
			return bool_val;
		}

		public void setBool_val(boolean bool_val) {
			this.bool_val = bool_val;
		}
	}

	private boolean doSave(float val)
	{
		nativeRecordId=new Date().getTime();
		SysApp.getMyDBManager().addWaitForInspector(nativeRecordId,mUniqueKey,mUniqueKey,mUniqueKey);
		RecordItem itm;

		if(!getGlu_datas().containsKey(cur_test_code))
		{
			itm=new RecordItem();
			getGlu_datas().put(cur_test_code,itm);
		}
		else
		{
			itm= getGlu_datas().get(cur_test_code);
		}
		itm.setValue1(val);
		itm.setiNativeRecordId(nativeRecordId);
		itm.setiType(DATA_BLOOD_GLUCOSE);
		itm.setSource( DBHelper.RI_SOURCE_DEVICE);
		itm.setiConcluison(SysApp.btDevice.getAddress());
		itm.setDeviceType( SysApp.check_type.ordinal());
		itm.setTestCode(cur_test_code.getTest_code());
		this.setCur_test_code(null);
		SysApp.getMyDBManager().addRecordItem(itm);
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
		return true;
	}

	private boolean saveRecord(float val)
	{
		if(cur_test_code==null)
			return false;
		boolean isOK= doSave(val);
		if(isOK)
		{
			cur_test_code=null;
		}
		return isOK;
	}

	/**
	 * 血糖争对的图表对象
	 */
	private MyLineChartView myLineChartView;
	

	public BloodGlucoseData(Context ctx, MyLineChartView myLineChartView, long nativeRecordId,String  mUniqueKey  ){
		this.myLineChartView = myLineChartView;
		this.context = ctx;
		this.nativeRecordId = nativeRecordId;
		this.mUniqueKey = mUniqueKey;
	}

	/**
	 * 将从蓝牙获取到的字节数组传递进来，进行数据解析
	 * @param bytes   蓝牙传递过来的数据
	 */
	@Override
	public void todo(byte[] bytes) {
		String hexString = StringUtil.byteToHexString(bytes);//将数据进行Hex运算后得到的结果字符串
		if (hexString.equals(BT_ONLINE_COMMAND)) {
			String text = myLineChartView.getTitle();
			if (text.equals(context.getString(R.string.CONNECT_GLUCOSE))) {
				// // 插入蓝牙MAC地址
				// DisplayView.fa.hander.sendEmptyMessage(2);
				EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_SAVE_BLUETOOTH_ADDRESS, null));
				Log.d(TAG, "插入成功");
			}
			if (inserted == false) {
				//DisplayView.fa.hander.sendEmptyMessage(5);
				EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
						context.getString(R.string.INSERT_TEST_PAPER)));
			} else if (inserted == true) {
				//DisplayView.fa.hander.sendEmptyMessage(5);
				EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
						context.getString(R.string.DRIP_BLOOD)));
			}
		} else if (hexString.equals(BT_PLEASE_BLEEDING)) {
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
					context.getString(R.string.DRIP_BLOOD)));
			inserted = true;
		} else if (hexString.startsWith(BT_COUNTDOWN_TIME_DISPLAY)) {
			/*myLineChartView.setTitleText(context.getString(R.string.WAIT)
					+ (int) bytes[5]
					+ context.getString(R.string.SECOND));*/
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
					(context.getString(R.string.WAIT)+ (int) bytes[5] + context.getString(R.string.SECOND))));
		} else if (hexString.startsWith(BT_DISPLAY_RESULT)) {
			byte[] temp = new byte[2];
			inserted = false;
			temp[0] = bytes[4];
			temp[1] = bytes[5];
			int consistency = (Integer.parseInt(StringUtil.byteToHexString(temp), 16));
			String data = null;
			float values = 0;
			if (myLineChartView.isMmol()) {
				float consistencyMmolpl = consistency / 18.0f;
				int scale = 1;// 设置位数
				int roundingMode = 4;// 表示四舍五入
				BigDecimal bd = new BigDecimal(consistencyMmolpl);
				bd = bd.setScale(scale, roundingMode);
				consistencyMmolpl = bd.floatValue();
				values = consistencyMmolpl;
				data = consistencyMmolpl + "mmol/L";
			} else {
				data = consistency + "mg/dl";
			}
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, data));
			if(saveRecord(values)) {

				//EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_SAVE_RECORD_ITEM, context.getString(R.string.CHECK_TYPE_INFO_BLOOD_GLUCOSE)));
				//DisplayView.fa.hander.sendEmptyMessage(5);

			/*if (DisplayView.fa != null) {
				DisplayView.fa.save();
				DisplayView.fa.hander.sendEmptyMessage(0);
			}*/
				EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
			}
		} else {

		}
	}

	@Override
	public void todoConnected() {
//		todoConnected=2;
		Log.d(TAG, "todoConnected");
	}

	@Override
	public void todoDisconnected() {
		todoDisconnected=0;
		Log.d(TAG, "todoDisconnected");
	}

	@Override
	public void todoConnecting() {
//		todoConnected=1;
		Log.d(TAG, "todoConnecting");
	}

	@Override
	public void todoDisconnected_failed() {
		todoDisconnected_failed=3;
		Log.d(TAG, "todoDisconnected_failed");
	}

	@Override
	public List<RecordItem>[] getRecordList(String mUniqueKey) {
		List<RecordItem>[] lists = new List[1];
		List<RecordItem> item_list=SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_BLOOD_GLUCOSE);
		if(item_list!=null)
			update_glu_datas(item_list);
		else
		{
			update_glu_datas(new ArrayList<RecordItem>() );
		}
		lists[0] = item_list;
		return lists;
	}

	private void update_glu_datas(List<RecordItem> item_list)
	{
		getGlu_datas().clear();
		for(RecordItem itm:item_list)
		{
			if(itm.getiNativeRecordId()==this.nativeRecordId)
			{
				getGlu_datas().put(GluTestCode.find(itm.getTestCode()),itm);
			}
		}
		/*if(getGlu_datas().values().size()==0)
		{
			this.setCur_test_code(GluTestCode.GLU_EMPTY);
		}*/
	}

	@Override
	public List<RecordItem> getRecordAllList(String mUniqueKey) {
		List<RecordItem> item_list=SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_BLOOD_GLUCOSE);
		if(item_list==null)
		{
			item_list=new ArrayList<RecordItem>();
		}
		for(RecordItem item:item_list)
		{
			GluTestCode test_code=GluTestCode.find(item.getTestCode());
			item.setValue2Txt(test_code.getTitle());
		}
		return item_list;
	}

	@Override
	public String[] getInsName() {
		return new String[]{"血糖\n\r(mmol/L)","检查项目"};
	}

	@Override
	public String[] getInsUnit() {
		return new String[]{"",""};
	}

	@Override
	public int[] getInsRange() {
		return new int[]{2,2};
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
		return todoDisconnected_failed;
	}

	public GluTestCode getCur_test_code() {
		return cur_test_code;
	}

	public void setCur_test_code(GluTestCode test_code) {
		this.cur_test_code = test_code;
	}

	public Hashtable<GluTestCode, RecordItem> getGlu_datas() {
		if(glu_datas==null)
		{
			glu_datas=new Hashtable<GluTestCode, RecordItem>();
		}
		return glu_datas;
	}

}
