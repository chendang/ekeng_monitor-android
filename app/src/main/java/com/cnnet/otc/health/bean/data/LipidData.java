package com.cnnet.otc.health.bean.data;

import android.content.Context;
import android.util.Log;

import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.events.BleEvent;
import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.tasks.UploadAllNewInfoTask;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.views.MyLineChartView;

import com.cnnet.otc.health.db.DBHelper;

import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 血脂
 * Created by SZ512 on 2016/1/6.
 */

public class LipidData implements MyCommData {

    private final String TAG = "LipidData";

    /**
     * 图表对象
     */
    private MyLineChartView myLineChartView;

    private Context context;

    private long nativeRecordId;
    private String mUniqueKey = null;
    private String allDatas = null;
    private int dateLength = 0;

    private final int HEAD_LENGTH = 6;
    private final String HEAD_DIGIT = "0A4E0A";
    private final int END_LENGTH = 10;
    private final String END_DIGIT = "220A0A4E0A";

    LipidTest my_test=new LipidTest();
    public LipidData(Context context, MyLineChartView myLineChartView, long nativeRecordId,String mUniqueKey) {
        this.context = context;
        this.myLineChartView = myLineChartView;
        this.nativeRecordId = nativeRecordId;
        this.mUniqueKey=mUniqueKey;
    }

    @Override
    public void todo(byte[] bytes) {
        try {
            String hexData = StringUtil.byteToHexString(bytes);//将数据进行Hex运算后得到的结果字符串
            // 0A 4E 0A 00 00 00 00 00
            // 4136302C000000003030303034302C302C340000000000002C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C003030303038302C302C342C312C312C4E2C22200000000000202020202020202020202020202000002020202020202020200000000000000020220A4136302C3030303132302C302C342C312C312C00004E2C222020202020200000000000000020202020202020202020000000000000202020202020202020220A41363000002C3030303136302C302C342C312C312C4E2C222020202020202020202020202020202020202020002020202020220A004136302C000000003030303230302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303234302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C0000000000003030303238302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303332302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303336302C302C34000000002C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303430302C302C342C312C312C4E2C2220202020202020202020202020202020202020202020202020220A4136302C3030303434302C302C342C312C312C4E2C2220202020202020200000000000002020202020202020202020202020202020220A4136302C3030303438302C302C342C312C312C4E2C222020202020202020202020202020202020202020202020
            // 20 20 22 0A 0A 4E 0A 00

            if (StringUtil.isNotEmpty(allDatas)) {
                allDatas += hexData;
                dateLength += bytes.length;
            } else {
                allDatas = hexData;
                dateLength = bytes.length;
            }

//            Log.e(TAG, " -- " + allDatas);
//        Log.d(TAG,"********************************************");
//        Log.d(TAG, "normal -- " + new String(tempDatas));
            int index = 0;
            boolean isPacket = false;
            String checkeResult = null;
            if (dateLength >= HEAD_LENGTH) {
                if (allDatas.startsWith(HEAD_DIGIT)) {
                    isPacket = true;
                }
                if (isPacket && dateLength >= END_LENGTH) {
                    if (allDatas.endsWith(END_DIGIT)) {
                        checkeResult = hexStringToString(allDatas);
                    } else {
                        isPacket = false;
                    }
                } else {
                    isPacket = false;
                }
            }


        /*

                N
                A60,000040,0,4,1,1,N,"??     : 3093537"
                A60,000080,0,4,1,1,N,"??      : 2015 06 17"
                A60,000120,0,4,1,1,N,"??      : 12:35"
                A60,000160,0,4,1,1,N,"??      : ????"
                A60,000200,0,4,1,1,N,"??      : P443"
                A60,000240,0,4,1,1,N,"CHOL    : <  2.59 mmol/L"
                A60,000280,0,4,1,1,N,"HDL CHOL: <  0.39 mmol/L"
                A60,000320,0,4,1,1,N,"TRIG    :  0.65 mmol/L"
                A60,000360,0,4,1,1,N,"CALC LDL: ---- "
                A60,000400,0,4,1,1,N,"TC/HDL  : ---- "
                P1

                N
                A60,000040,0,4,1,1,N,"                         "
                A60,000080,0,4,1,1,N,"                         "
                A60,000120,0,4,1,1,N,"                         "
                A60,000160,0,4,1,1,N,"                         "
                A60,000200,0,4,1,1,N,"                         "
                A60,000240,0,4,1,1,N,"                         "
                A60,000280,0,4,1,1,N,"                         "
                A60,000320,0,4,1,1,N,"                         "
                A60,000360,0,4,1,1,N,"                         "
                A60,000400,0,4,1,1,N,"                         "
                A60,000440,0,4,1,1,N,"                         "
                A60,000480,0,4,1,1,N,"                         "

                N

                 */
            my_test=new LipidTest();
            int cnt=0;
            if (isPacket && checkeResult != null) {
                Log.d(TAG, "allDatas str : " + allDatas);
                Log.d(TAG, "resultStr str : " + checkeResult);
                String results[] = checkeResult.split("\n");
                for (String resultStr : results) {
                    if ((cnt++) < 6)
                        continue;
                    String s = resultStr.trim();
                    String unitStr = "";
                    if (s.startsWith("A60")) {
                        int idx1 = s.indexOf("\"");
                        int idx2 = s.indexOf(":");
                        if (idx1 < 0 || idx2 < 0) {
                            continue;
                        }
                        String typStr = s.substring(idx1 + 1, idx2).trim();
                        String valStr = s.substring(idx2 + 1, s.length() - 1).trim();

                        if (cnt == 7)                                                          //检测类型实际是在第7行，即Index=6， 但由于前面已执行cnt++,因此当cnt==7时解析检查类型一行。
                        {
                            if (!my_test.set_test_type(valStr.trim().substring(0, 1))) {
                                return;
                            } else {
                                continue;
                            }
                        }
                        if (!my_test.getTest_descr().equals("")) {
                            my_test.parse_value(typStr, valStr);
                        }
                    }
                }
                boolean bFound = false;
                String data="";
                for (LipidDataItem itm : my_test.getLipid_data_items()) {
                    if (itm.val != LipidDataItem.NONE_VALUE) {
                        if(!bFound){
                            nativeRecordId=new Date().getTime();
                            SysApp.getMyDBManager().addWaitForInspector(nativeRecordId,mUniqueKey,mUniqueKey,mUniqueKey);
                        }
                        bFound = true;
                        //SysApp.getMyDBManager().addRecordItemWithDescr(nativeRecordId, itm.typeStr, itm.val, itm.getDescr(), DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                        SysApp.getMyDBManager().addRecordItem(nativeRecordId, itm.getTypeStr(), itm.val, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                    }
                    UploadAllNewInfoTask.submitOneRecordInfo(context,mUniqueKey, nativeRecordId,
                            new SubmitServerListener() {
                                @Override
                                public void onResult(int result) {
                                    DialogUtil.cancelDialog();
                                    if (result == 0) { //success
                                    } else if(result == -2){
                                        // ToastUtil.TextToast(context.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
                                    } else {
                                        //  ToastUtil.TextToast(context.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
                                    }
                                }
                            });
                }
                dateLength = 0;
                allDatas = null;
                if (bFound) {
                    EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_UPDATE_STATE, data));
                    EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_EVENT_RESET, ""));
                }
            }
               /*
                //总胆固醇
                String CHOL = "\"CHOL";  //
                final int CHOL_LENGTH = 5;
                String str_dgc = null;
                index = 0; //记录匹配字符的位置
                while ((index = checkeResult.indexOf(CHOL, index)) >= 0) {
                    String sgData = checkeResult.substring(index, index + 30).trim();
                    int int_f = sgData.lastIndexOf(":");
                    int int_f2 = sgData.indexOf("m");
                    str_dgc = sgData.substring(int_f + 1, int_f2).trim();
                    index += CHOL_LENGTH;
                }

                //甘油三酯
                String str_gysz = null;
                String TRIG = "\"TRIG";
                final int TRIG_LENGTH = 5;
                index = 0; //记录匹配字符的位置
                while ((index = checkeResult.indexOf(TRIG, index)) >= 0) {
                    String phData = checkeResult.substring(index, index + 30).trim();
                    int int_f = phData.lastIndexOf(":");
                    int int_f2 = phData.indexOf("m");
                    str_gysz = phData.substring(int_f + 1, int_f2).trim();
                    index += TRIG_LENGTH;
                }
                Log.i(TAG, CHOL + ":  " + str_dgc + "  " + TRIG + ":  " + str_gysz);
                float cholValue = Float.parseFloat(StringUtil.getFirstFloatStr(str_dgc));
                float randomFloat = (float)Math.random() * 0.5f;
                if(str_dgc.startsWith("<")) {
                    cholValue = cholValue - randomFloat;
                } else if(str_dgc.startsWith(">")) {
                    cholValue = cholValue + randomFloat;
                }
                cholValue = StringUtil.getBigDecimal(3, cholValue);
                float trigValue = Float.parseFloat(StringUtil.getFirstFloatStr(str_gysz));
                Log.i(TAG, "two values:  " + cholValue + "  " + trigValue);
                SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_CHOLESTEROL, cholValue, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                SysApp.getMyDBManager().addRecordItem(nativeRecordId, DATA_TRIGLYCERIDES, trigValue, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, "总胆固醇" + str_dgc + ";甘油三酯" + str_gysz));
                dateLength = 0;
                allDatas = null;
                EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
            }*/
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

     String hexStringToString(String hexStr)
    {
        StringBuilder str_builder=new StringBuilder("");
        int cnt=hexStr.length()/2;
        for(int i=0;i<cnt;i++)
        {
            String hex_str=hexStr.substring(i*2,(i+1)*2);
            str_builder.append((char)(Integer.parseInt(hex_str,16)));
        }
        return str_builder.toString();
    }

   /* void update_lipid_db()
    {
        boolean bFound=false;
        String data="";
        if(!my_test.getTest_descr().equals(""))
        {
            for(LipidDataItem itm:my_test.getLipid_data_items())
            {
                if(itm.val!=9999) {
                    bFound=true;
                    if(!itm.getDescr().equals(CommConst.VALUE_STRANGE))
                    {
                        data+=itm.getDescr()+"："+itm.val+ itm.getUnitStr() +"  ";
                    }
                    SysApp.getMyDBManager().addRecordItem(nativeRecordId, itm.getTypeStr(), itm.val, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());

                }
            }
            if(bFound)
                EventBus.getDefault().post(new BleEvent(CommConst.FLAG_BLE_CONNECT_UPDATE_STATE, data));
        }
    }*/

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
        lists[0] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, LipidTest.DATA_CHOLESTEROL);
        lists[1] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, LipidTest.DATA_TRIGLYCERIDES);
        lists[2] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, LipidTest.DATA_HDL);
        lists[3] = SysApp.getMyDBManager().getListByReorcdId(mUniqueKey, LipidTest.DATA_LDL);
        return lists;
    }

    @Override
    public List<RecordItem> getRecordAllList(String mUniqueKey) {
        return SysApp.getMyDBManager().getRecordAllInfoByType(mUniqueKey, LipidTest.DATA_CHOLESTEROL, LipidTest.DATA_TRIGLYCERIDES,LipidTest.DATA_HDL,LipidTest.DATA_LDL);
    }

    @Override
    public String[] getInsName() {
        return new String[]{"总胆固醇\n\r(mmol/L)","甘油三酯\n\r(mmol/L)","高密度脂蛋白\n\r(mmol/L)","低密度脂蛋白\n\r(mmol/L)"};
    }

    @Override
    public String[] getInsUnit() {
        //return new String[]{"mmol/L", "mmol/L","mmol/L","mmol/L"};
        return new String[]{"", "","",""};
    }

    @Override
    public int[] getInsRange() {
        return new int[]{2, 2,2,2};
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
