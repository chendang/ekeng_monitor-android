package com.cnnet.otc.health.bean.data;

import android.content.Context;
import android.util.Log;

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
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 尿酸
 * Created by SZ512 on 2016/1/22.
 */
public class UricacidData implements MyCommData {

    private final String TAG = "UricacidData";
    /**
     * 图表对象
     */
    private MyLineChartView myLineChartView;

    private Context context;

    private long nativeRecordId;
    /**
     * 有效数据起始位置
     */
    private final String URIC_START = "2450434C";
    /**
     * 结束：错误标示
     */
    private final String URIC_END_ERROR = "D00000070000";

    /**
     * 完整数据长度
     */
    private final int DATA_LENGTH = 20;

    public final String DATA_UA = "UA";  //尿酸

    private String allDatas = null;
    private String mUniqueKey = null;
    private byte[] tempDatas = null;
    public UricacidData(Context context, MyLineChartView myLineChartView, long nativeRecordId,String mUniqueKey) {
        this.context = context;
        this.myLineChartView = myLineChartView;
        this.nativeRecordId = nativeRecordId;
        this.mUniqueKey=mUniqueKey;
    }

    @Override
    public void todo(byte[] bytes) {
        String hexData = StringUtil.byteToHexString(bytes);//将数据进行Hex运算后得到的结果字符串
        if(StringUtil.isNotEmpty(allDatas)) {
            allDatas += hexData;
        } else {
            allDatas = hexData;
        }
        Log.d(TAG, "allDatas str : " + allDatas);
        //50434CEFBFBD0000070000EFBFBD
        //首：24 50 43 4C
        //错误：D0 00 00 07 00 00
        //2450434C010000010000E12450434C51000000090000000F090209113A00A7
        String str_head = "";
        String str_end = "";

        // 24 50 43 4C 01 00 00 01 00 00 EF BF BD
        // 24 50 43 4C 51 00 00 00 09 00 00 00 11 01 16 03 39 3C 00 EF BF BD
        // 24 50 43 4C 01 00 00 01 00 00 EF BF BD
        // 24 50 43 4C 51 00 00 00 09 00 00 00 11 01 16 05 12 3C 00 EF BF BD
        if (allDatas.length() >= 8)  //获取起始头标志
        {
            str_head = allDatas.substring(0, 8);
        }
        if (allDatas.length() >= 20)  //获取尾标志
        {
            str_end = allDatas.substring(8, 12);
        }
        if (str_head == URIC_START && str_end == URIC_END_ERROR) {
            Log.d(TAG, "uric error : " + URIC_END_ERROR);
            EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, "测量错误！"));
            allDatas = null;
            tempDatas = null;
        } else {
            //====================正确数据，开始解析====================
            //<协议解析>
            boolean catchOneData = false;//是否找到一个完整的数据帧,
            byte[] datas = null;
            //1、设置缓存
            if(tempDatas == null) {
                tempDatas = bytes;
            } else {
                byte[] temps = tempDatas;
                tempDatas = new byte[temps.length + bytes.length];
                for(int i = 0; i < tempDatas.length; i++) {
                    if(i < temps.length) {
                        tempDatas[i] = temps[i];
                    } else {
                        tempDatas[i] = bytes[i - temps.length];
                    }
                }
            }
            int index = 0;
            //2.完整性判断
            while (tempDatas != null && (tempDatas.length - index) >= DATA_LENGTH) {
                //2.1 查找数据头
                if (tempDatas[index] == 0x24 && tempDatas[index + 1] == 0x50 &&
                        tempDatas[index + 2] == 0x43 && tempDatas[index + 3] == 0x4C) {
                    if(tempDatas[index + 4] == 0x51) {
                        if (tempDatas.length - index < DATA_LENGTH) {
                            byte[] temps = tempDatas;
                            tempDatas = new byte[tempDatas.length - index];
                            for (int i = 0; i < tempDatas.length; i++) {
                                tempDatas[i] = temps[index + i];
                            }
                            break;//数据不够的时候清除之前数据
                        }

                        datas = new byte[DATA_LENGTH];
                        //复制一条完整数据到具体的数据缓存
                        for (int i = 0; i < DATA_LENGTH; i++) {
                            datas[i] = tempDatas[i + index];
                        }
                        Log.d(TAG, "Hex DATA " + StringUtil.byteToHexString(datas));
                        catchOneData = true;//找到了一个完整的
                        tempDatas = null;//正确分析一条数据，从缓存中移除数据。
                        int value = 0;
                        for(int i = 0; i < datas.length - 1; i++) {
                            value += datas[i];
                        }
                        Log.d(TAG, value + " === " + datas[datas.length - 1]);
                    }
                    index+=4;
                }
                else
                {
                    index++;
                }
            }
            //分析数据
            if(catchOneData) {//如果找到了一个完整的数据帧
                if (datas[4] == 0x51)//UA
                {
                    int hValue = datas[18];
                    int lValue = datas[17];
                    //十进制转十六进制
                    String str_hValue = StringUtil.encode2Hex(hValue);
                    String str_lValue = StringUtil.encode2Hex(lValue);
                    String vv_Value = str_hValue + str_lValue;
                    //十六进制转十进制
                    int int_vv = Integer.parseInt(vv_Value, 16);

                    float vv = (float)(int_vv/(10*16.81f));
                    vv=new BigDecimal(vv).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

                    nativeRecordId=new Date().getTime();
                    SysApp.getMyDBManager().addWaitForInspector(nativeRecordId,mUniqueKey,mUniqueKey,mUniqueKey);
                    SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_UA, vv, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                    Log.d(TAG, "UA value is ..... (" + vv + "mmol/dL)");
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
                    EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, "尿酸结果为：" + vv + "mmol/dL"));

                    EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
                }
                allDatas = null;
                tempDatas = null;
            }
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
        lists[0] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, DATA_UA);
        return lists;
    }

    @Override
    public List<RecordItem> getRecordAllList(String mUniqueKey) {
        return SysApp.getMyDBManager().getRecordAllInfoByType(mUniqueKey,DATA_UA);
    }

    @Override
    public String[] getInsName() {
        return new String[]{"尿酸(mmol/dL)"};
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
