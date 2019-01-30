package com.cnnet.otc.health.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.bean.MemberRecord;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.RoleUser;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.interfaces.IUser;
import com.cnnet.otc.health.util.DESUtil;
import com.cnnet.otc.health.util.DateUtil;
import com.cnnet.otc.health.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SZ512 on 2016/1/5.
 */
public class MyDBManager {

    private final String TAG = "MyDBManager";

    private final String DB_NAME = "otcccc.db";// 数据库名字
    private final int DB_VERSION = 1;// 数据库版本
    //private final String PASSWORD = "OTC_7!9%45";

    private Context ctx;
    private DBHelper dbHelper;  //数据库工具
    private SQLiteDatabase mDB;

    public static MyDBManager getInstance(Context ctx) {
        return new MyDBManager(ctx);
    }

    public MyDBManager(Context ctx) {
        this.ctx = ctx;
        openOrCreateDatabase();
    }

    private void openOrCreateDatabase() {
        this.dbHelper = DBHelper.getInstance(ctx, DB_NAME, DB_VERSION);
        mDB = dbHelper.getWritableDatabase();
        Log.e(TAG, "db.getPath() = " + mDB.getPath());
    }

    public void closeDB() {
        if(mDB != null && mDB.isOpen()) {
            mDB.close();
            mDB = null;
        }
    }

    public void destory() {
        closeDB();
        dbHelper.close();
        dbHelper = null;
    }

    /**
     * 插入检查信息
     * @param nativeId  本地检查信息
     * @param iType     检查类型
     * @param value     检查结果值
     * @param sourceType  来源类型：1设备 ，2手动输入
     * @param deviceInfo  蓝牙设备信息
     * @param deviceType  蓝牙设备类型（血糖仪，血氧仪等）
     * @return
     */
    public boolean addRecordItem(long nativeId, String iType, float value, short sourceType, String deviceInfo, int deviceType) {


        // mDB.delete(DBHelper.TB_MEMBER_RECORD_ITEM, DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + nativeId +" AND " + DBHelper.RI_COL_TYPE + "=?",
        //         new String[]{iType});
        ContentValues values = new ContentValues();
        values.put(DBHelper.RI_COL_NATIVE_RECORD_ID, nativeId);
        values.put(DBHelper.RI_COL_TYPE, iType);
        values.put(DBHelper.RI_COL_VALUE1, value);
        values.put(DBHelper.RI_COL_SOURCE, sourceType);
        values.put(DBHelper.RI_COL_CONCLUSION, deviceInfo);
        values.put(DBHelper.RI_COL_DEVICE_TYPE, deviceType);
        long result = mDB.insert(DBHelper.TB_MEMBER_RECORD_ITEM, null, values);

        if (result > 0) {
            return true;
        }

        return false;
    }
    public void clearDBForServerSwitch()
    {
        String sql="DELETE FROM tb_member_record_item WHERE iNativeId in (SELECT r.id FROM tb_member_record AS r INNER JOIN tb_members AS m ON r.memberNativeId=m.id WHERE m.mUniqueKey<>'')";
        mDB.execSQL(sql);
        sql="DELETE FROM tb_member_record WHERE memberNativeId IN (SELECT id FROM tb_members WHERE mUniqueKey<>'')";
        mDB.execSQL(sql);
        sql="DELETE FROM tb_members WHERE mUniqueKey<>''";
        mDB.execSQL(sql);
        sql="DELETE FROM tb_data_version";
        mDB.execSQL(sql);
    }

    /**
     * 插入检查信息
     * @param itm  本地检查信息
     * @return
     */
    public boolean addRecordItem(RecordItem itm) {
        /*if(nativeId <= 0) {
            nativeId = addWaitForInspector(mUniqueKey, checkPersion);
        }*/
        if(itm.getiNativeRecordId() > 0) {
            mDB.delete(DBHelper.TB_MEMBER_RECORD_ITEM, DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + itm.getiNativeRecordId() +" AND " + DBHelper.RI_COL_TYPE + "=? AND " + DBHelper.RI_COL_TESTCODE + "=?",
                    new String[]{itm.getiType(),itm.getTestCode()});
            ContentValues values = new ContentValues();
            values.put(DBHelper.RI_COL_NATIVE_RECORD_ID, itm.getiNativeRecordId());
            values.put(DBHelper.RI_COL_TYPE, itm.getiType());
            values.put(DBHelper.RI_COL_VALUE1, itm.getValue1());
            values.put(DBHelper.RI_COL_SOURCE, itm.getSource());
            values.put(DBHelper.RI_COL_CONCLUSION, itm.getiConcluison());
            values.put(DBHelper.RI_COL_DEVICE_TYPE, itm.getDeviceType());
            values.put(DBHelper.RI_COL_TESTCODE,itm.getTestCode());
            long result = mDB.insert(DBHelper.TB_MEMBER_RECORD_ITEM, null, values);
            if(result > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 插入检查信息
     * @param nativeId  本地检查信息
     * @param iType     检查类型
     * @param value     检查结果值
     * @param descr     检查结果描述信息
     * @param sourceType  来源类型：1设备 ，2手动输入
     * @param deviceInfo  蓝牙设备信息
     * @param deviceType  蓝牙设备类型（血糖仪，血氧仪等）
     * @return
     */
    public boolean addRecordItemWithDescr(long nativeId, String iType, float value, String descr,short sourceType, String deviceInfo, int deviceType) {
        /*if(nativeId <= 0) {
            nativeId = addWaitForInspector(mUniqueKey, checkPersion);
        }*/
        if (nativeId > 0) {
            mDB.delete(DBHelper.TB_MEMBER_RECORD_ITEM, DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + nativeId + " AND " + DBHelper.RI_COL_TYPE + "=?",
                    new String[]{iType});
            ContentValues values = new ContentValues();
            values.put(DBHelper.RI_COL_NATIVE_RECORD_ID, nativeId);
            values.put(DBHelper.RI_COL_TYPE, iType);
            values.put(DBHelper.RI_COL_VALUE1, value);
            values.put(DBHelper.RI_COL_DESC1, descr);
            values.put(DBHelper.RI_COL_SOURCE, sourceType);
            values.put(DBHelper.RI_COL_CONCLUSION, deviceInfo);
            values.put(DBHelper.RI_COL_DEVICE_TYPE, deviceType);
            long result = mDB.insert(DBHelper.TB_MEMBER_RECORD_ITEM, null, values);
            if (result > 0) {
                return true;
            }
        }
        return false;
    }

    public void deleteMember(Member member)
    {
        String del_item_sql="DELETE FROM tb_member_record_item WHERE iNativeId in (SELECT id FROM tb_member_record WHERE memberNativeId="+member.getId()+")";
        mDB.execSQL(del_item_sql);
        String del_rec_sql="DELETE FROM tb_member_record WHERE memberNativeId="+member.getId();
        mDB.execSQL(del_rec_sql);
        String del_member_sql="DELETE FROM tb_members WHERE id="+member.getId();
        mDB.execSQL(del_member_sql);
    }

    /**
     * 添加一条检查记录信息，并返回本地id
     * @param mUniqueKey 会员ID
     * @param checkPersion 检查人：会员自己为空
     * @param addUniqueKey   当前登录人员Key；会员账号登录时，这个值传值为0
     * @return
     */
    public long addWaitForInspector(long memberNativeId, String mUniqueKey, String checkPersion, String addUniqueKey) {
        if(!StringUtil.isEmpty(mUniqueKey)) {
            /*ContentValues values = new ContentValues();
            values.put(DBHelper.R_COL_MEMBER_UNIQUEKEY, mUniqueKey);
            values.put(DBHelper.R_COL_RECORD_PERSION, checkPersion == null ? "" : checkPersion);
            values.put(DBHelper.R_COL_STATE, 0);
            values.put(DBHelper.R_COL_ADD_UNIQUEKEY, addUniqueKey);
            long result = mDB.insert(DBHelper.TB_MEMBER_RECORD, null, values);
            if(result > 0) {
                //Cursor cur = mDB.rawQuery("last_insert_rowid()", null);
                //cur.moveToFirst();
               // long id = cur.getLong(1);
                //cur.close();
                return result;
            }*/

            String sql = "INSERT INTO " + DBHelper.TB_MEMBER_RECORD + "(" +DBHelper.R_COL_RECORD_ID + "," + DBHelper.R_COL_NATIVE_MEMBER_ID + "," + DBHelper.R_COL_MEMBER_UNIQUEKEY
                    + "," + DBHelper.R_COL_RECORD_PERSION + "," + DBHelper.R_COL_ADD_UNIQUEKEY + "," + DBHelper.R_COL_STATE
                    + ") SELECT ?,?,?,?,?,0 WHERE NOT EXISTS (SELECT * FROM " + DBHelper.TB_MEMBER_RECORD + " WHERE "
                    + DBHelper.R_COL_MEMBER_UNIQUEKEY +  "=? AND " + DBHelper.R_COL_RECORD_ID + "=? AND " + DBHelper.R_COL_STATE + "==0 AND " + DBHelper.R_COL_ADD_UNIQUEKEY + "=?)"  ;
            mDB.execSQL(sql, new Object[]{memberNativeId, mUniqueKey,mUniqueKey, (checkPersion == null ? "" : checkPersion), addUniqueKey, mUniqueKey,memberNativeId,addUniqueKey});
        }
        return 0;
    }

    /**
     * 批量添加检查人员
     * @param lists
     * @param checkPersion
     * @param addUniqueKey
     * @return
     */
    public long addWaitFroInsList(List<Member> lists, String checkPersion, String addUniqueKey) {
        boolean openTrans = false;
        long count = 0;
        if(lists != null) {
            /*if(lists.size() >= 50) {
                openTrans = true;
            }*/
            if(openTrans) {
                //开启事务
                mDB.beginTransaction();
            }
            try{
                for(Member member : lists) {
                    //批量处理操作
                    if(member.isSuperClinic()) {
                        saveMemberInfo(member);
                    }
                    addWaitForInspector(member.getId(), member.getUniqueKey(), checkPersion, addUniqueKey);
                }
                if(openTrans) {
                    //设置事务标志为成功，当结束事务时就会提交事务
                    mDB.setTransactionSuccessful();
                }
            }catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(openTrans) {
                    //结束事务
                    mDB.endTransaction();
                }
            }
        }
        return count;
    }

    /**
     * 将查询出来的会员信息转换为Member对象
     * @param c
     * @return
     */
    private Member getMemberByCursor(Cursor c) {
        Member item = new Member();
        item.setId(c.getInt(0));
        item.setUniqueKey(c.getString(1));
        item.setFaUniqueKey(c.getString(2));
        item.setmHeadPath(c.getString(3));
        item.setmNativeHeadPath(c.getString(4));
        item.setName(c.getString(5));
        item.setSex(c.getString(6));
        item.setmIdNumber(c.getString(7));
        item.setmTel(c.getString(8));
        item.setmPhone(c.getString(9));
        item.setmAddress(c.getString(10));
        item.setmBrithday(c.getString(11));
        item.setmAnamnesis(c.getString(12));
        item.setmSSN(c.getString(13));
        item.setmCreatePersion(c.getString(14));
        item.setmCreateTime(c.getLong(15));
        item.setmUpdateTime(c.getLong(16));
        item.setVersion(c.getLong(17));
        item.setState(c.getInt(18));
        item.setAddUniqueKey(c.getString(19));
        item.setWithDoctor(c.getString(20));
        item.setmNamePinyin(c.getString(21));
        return item;
    }

    /**
     * 从待检会员中移除
     * @param   member 要从待检会员中移除的会员
     * @return
     */
    public void removeFromWaitInspectorMemberList(Member member)
    {
        if(member!=null) {

            String sql ="DELETE FROM "+DBHelper.TB_MEMBER_RECORD_ITEM + " WHERE "+DBHelper.RI_COL_NATIVE_RECORD_ID+"="+member.getNative_record_id();
            mDB.execSQL(sql);
            sql ="DELETE FROM "+DBHelper.TB_MEMBER_RECORD + " WHERE "+DBHelper.ID+"="+member.getNative_record_id();
            mDB.execSQL(sql);

        }
    }

    /**
     * 根据当前登录UniqueKey，查询当前账号下的待检会员列表
     * @param uniqueKey  登录人员Key
     * @return
     */
    public List<Member> getWaitInspectorMemberList(String uniqueKey) {
        if(StringUtil.isNotEmpty(uniqueKey)) {
            String sql = "SELECT m.*,r.id AS native_record_id FROM " + DBHelper.TB_MEMBER_RECORD + " AS r LEFT JOIN "
                    + DBHelper.TB_MEMBERS + " AS m ON r." + DBHelper.R_COL_NATIVE_MEMBER_ID + "=m." + DBHelper.ID
                    + " WHERE r." + DBHelper.R_COL_ADD_UNIQUEKEY + "='" + uniqueKey + "' AND r." + DBHelper.R_COL_STATE + "="
                    + CommConst.STATE_RECORD_WAIT + " ORDER BY r.id ASC";
            Log.d(TAG, "sql = " + sql);
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size = 0;
                List<Member> members = null;
                if((size = c.getCount()) > 0){
                    Log.d(TAG, "getWaitInspectorMemberList >>> size = " + size);
                    //c.moveToFirst();
                    members = new ArrayList<>();
                    while (c.moveToNext()) {
                        Member item = new Member();
                        item.setId(c.getInt(0));
                        item.setUniqueKey(c.getString(1));
                        item.setFaUniqueKey(c.getString(2));
                        item.setmHeadPath(c.getString(3));
                        item.setmNativeHeadPath(c.getString(4));
                        item.setName(c.getString(5));
                        item.setSex(c.getString(6));
                        item.setmIdNumber(c.getString(7));
                        item.setmTel(c.getString(8));
                        item.setmPhone(c.getString(9));
                        item.setmAddress(c.getString(10));
                        item.setmBrithday(c.getString(11));
                        item.setmAnamnesis(c.getString(12));
                        item.setmSSN(c.getString(13));
                        item.setmCreatePersion(c.getString(14));
                        item.setmCreateTime(c.getLong(15));
                        item.setmUpdateTime(c.getLong(16));
                        item.setVersion(c.getLong(17));
                        item.setState(c.getInt(18));
                        item.setAddUniqueKey(c.getString(19));
                        item.setWithDoctor(c.getString(20));
                        item.setNative_record_id(c.getLong(22));
                        members.add(item);
                    }
                }
                c.close();
                return members;
            }
        }
        return null;
    }

    /**
     * 根据当前登录UniqueKey和待检记录ID，查询待检会员信息
     * @param uniqueKey  登录人员Key
     * @return
     */
    public Member getWaitInspectorMemberInfo(String uniqueKey, long nativeReocrdId) {
        if(nativeReocrdId > 0 && StringUtil.isNotEmpty(uniqueKey)) {
            String sql = "SELECT m.*,r.id AS native_record_id FROM " + DBHelper.TB_MEMBER_RECORD + " AS r LEFT JOIN "
                    + DBHelper.TB_MEMBERS + " AS m ON r." + DBHelper.R_COL_NATIVE_MEMBER_ID + "=m." + DBHelper.ID
                    + " WHERE r." + DBHelper.R_COL_ADD_UNIQUEKEY + "='" + uniqueKey + "' AND r." + DBHelper.R_COL_STATE + "="
                    + CommConst.STATE_RECORD_WAIT + " AND r." + DBHelper.ID + "=" + nativeReocrdId + " ORDER BY r.id ASC";
            Log.d(TAG, "sql = " + sql);
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size;
                Member member = null;
                if((size = c.getCount()) > 0){
                    Log.d(TAG, "getWaitInspectorMemberInfo >>> size = " + size);
                    c.moveToFirst();
                    member = new Member();
                    member.setId(c.getInt(0));
                    member.setUniqueKey(c.getString(1));
                    member.setFaUniqueKey(c.getString(2));
                    member.setmHeadPath(c.getString(3));
                    member.setmNativeHeadPath(c.getString(4));
                    member.setName(c.getString(5));
                    member.setSex(c.getString(6));
                    member.setmIdNumber(c.getString(7));
                    member.setmTel(c.getString(8));
                    member.setmPhone(c.getString(9));
                    member.setmAddress(c.getString(10));
                    member.setmBrithday(c.getString(11));
                    member.setmAnamnesis(c.getString(12));
                    member.setmSSN(c.getString(13));
                    member.setmCreatePersion(c.getString(14));
                    member.setmCreateTime(c.getLong(15));
                    member.setmUpdateTime(c.getLong(16));
                    member.setVersion(c.getLong(17));
                    member.setState(c.getInt(18));
                    member.setAddUniqueKey(c.getString(19));
                    member.setWithDoctor(c.getString(20));
                    member.setNative_record_id(c.getLong(22));
                }
                c.close();
                return member;
            }
        }
        return null;
    }

    /**
     * 根据当前登录UniqueKey，查询当前账号下的待上传会员列表
     * @param uniqueKey  登录人员Key
     * @return
     */
    public List<Member> getWaitUploadMemberList(String uniqueKey) {
        if(StringUtil.isNotEmpty(uniqueKey)) {
            String sql = "SELECT * FROM " + DBHelper.TB_MEMBERS + " WHERE " + DBHelper.M_COL_ADD_UNIQUEKEY + "='" + uniqueKey + "' AND " + DBHelper.M_COL_STATE + "=0 ORDER BY id";
            Log.d(TAG, "sql = " + sql);
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size = 0;
                List<Member> members = null;
                if((size = c.getCount()) > 0){
                    Log.d(TAG, "getWaitUploadMemberList >>> size = " + size);
                    //c.moveToFirst();
                    members = new ArrayList<>();
                    while (c.moveToNext()) {
                        members.add(getMemberByCursor(c));
                    }
                }
                c.close();
                return members;
            }
        }
        return null;
    }

    /**
     * 根据会员查询待检记录
     * @param uniqueKey  登录ID
     * @return
     */
    public List<MemberRecord> getWaitUploadRecordList(String uniqueKey) {
        String sql = "";
        if (StringUtil.isNotEmpty(uniqueKey)) {
            sql = "SELECT * FROM " + DBHelper.TB_MEMBER_RECORD + "  WHERE " + DBHelper.R_COL_ADD_UNIQUEKEY
                    + "='" + uniqueKey + "' AND " + DBHelper.R_COL_STATE + "=" + CommConst.STATE_RECORD_NEED_UPLOAD +
                    " ORDER BY id ASC";

        } else {
            //同步本机所有记录
            sql = "SELECT * FROM " + DBHelper.TB_MEMBER_RECORD + "  WHERE " + DBHelper.R_COL_STATE + "=" + CommConst.STATE_RECORD_NEED_UPLOAD +
                    " ORDER BY "+DBHelper.R_COL_RECORD_ID +" ASC";
        }

        Log.d(TAG, "sql = " + sql);
        List<MemberRecord> recordList = null;
        Cursor c = mDB.rawQuery(sql, null);
        if (c != null) {
            int size;

            if ((size = c.getCount()) > 0) {
                recordList = new ArrayList<>();
                Log.d(TAG, "getWaitUploadRecordList >>> size = " + size);
                while (c.moveToNext()) {
                    MemberRecord record = new MemberRecord();
                    record.setId(c.getLong(0));
                    record.setRecordId(c.getLong(1));
                    record.setMemberNativeId(c.getLong(2));
                    record.setmUniqueKey(c.getString(3));
                    record.setRecordPersion(c.getString(4));
                    record.setCreateTime(DateUtil.getDateByStr(c.getString(5)));
                    record.setUpdateTime(DateUtil.getDateByStr(c.getString(6)));
                    record.setRecordType(c.getShort(7));
                    record.setState(c.getShort(8));
                    record.setAdd_uniqueKey(c.getString(9));
                    //record.setCheckedItems(getListByRecordId(record.getId()));
                    recordList.add(record);
                }
            }
            c.close();
        }
        return recordList;
    }


    /**
     * 根据会员查询待检记录
     * @param memberUniqueKey  会员ID
     * @return
     */
    public MemberRecord getWaitInspectorRecord(String memberUniqueKey) {
        if(StringUtil.isNotEmpty(memberUniqueKey)) {
            String sql = "SELECT * FROM " + DBHelper.TB_MEMBER_RECORD + "  WHERE " + DBHelper.R_COL_MEMBER_UNIQUEKEY
                    + "='" + memberUniqueKey + "' AND " + DBHelper.R_COL_STATE + "=" + CommConst.STATE_RECORD_WAIT +
                    " ORDER BY id DESC";
            Log.d(TAG, "sql = " + sql);
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size = 0;
                MemberRecord record = null;
                if((size = c.getCount()) > 0) {
                    Log.d(TAG, "getWaitInspectorMemberList >>> size = " + size);
                    record = new MemberRecord();
                    c.moveToFirst();
                    record.setId(c.getLong(0));
                    record.setRecordId(c.getLong(1));
                    record.setMemberNativeId(c.getLong(2));
                    record.setmUniqueKey(c.getString(3));
                    record.setRecordPersion(c.getString(4));
                    Log.d(TAG, "time --------" + c.getLong(5));
                    record.setCreateTime(DateUtil.getDateByStr(c.getString(5)));
                    record.setUpdateTime(DateUtil.getDateByStr(c.getString(6)));
                    record.setRecordType(c.getShort(7));
                    record.setState(c.getShort(8));
                    record.setAdd_uniqueKey(c.getString(9));
                    //record.setCheckedItems(getListByRecordId(record.getId()));
                }
                c.close();
                return record;
            }
        }
        return null;
    }

    /**
     * 根据会员查询待检记录
     * @param addUniqueKey  会员ID
     * @return
     */
    public MemberRecord getWaitInspectorRecord(String addUniqueKey, long nativeRecordId) {
        if(StringUtil.isNotEmpty(addUniqueKey) && nativeRecordId > 0) {
            String sql = "SELECT * FROM " + DBHelper.TB_MEMBER_RECORD + "  WHERE " + DBHelper.R_COL_NATIVE_MEMBER_ID
                    + "='" + addUniqueKey + "' AND " + DBHelper.R_COL_STATE + "=" + CommConst.STATE_RECORD_WAIT
                    + " AND " + DBHelper.R_COL_RECORD_ID + "=" + nativeRecordId + " ORDER BY "+ DBHelper.R_COL_RECORD_ID +" DESC";
            Log.d(TAG, "sql = " + sql);
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size = 0;
                MemberRecord record = null;
                if((size = c.getCount()) > 0) {
                    Log.d(TAG, "getWaitInspectorMemberList >>> size = " + size);
                    record = new MemberRecord();
                    c.moveToFirst();
                    record.setId(c.getLong(0));
                    record.setRecordId(c.getLong(1));
                    record.setMemberNativeId(c.getLong(2));
                    record.setmUniqueKey(c.getString(3));
                    record.setRecordPersion(c.getString(4));
                    record.setCreateTime(DateUtil.getDateByStr(c.getString(5)));
                    record.setUpdateTime(DateUtil.getDateByStr(c.getString(6)));
                    record.setRecordType(c.getShort(7));
                    record.setState(c.getShort(8));
                    record.setAdd_uniqueKey(c.getString(9));
                    //record.setCheckedItems(getListByRecordId(record.getId()));
                }
                c.close();
                return record;
            }
        }
        return null;
    }

    /**
     * 删除待检人员
     * @param addUniqueKey
     * @param nativeRecordId
     * @return
     */
    public boolean deleteWaitInspectorRecord(String addUniqueKey, long nativeRecordId) {
        if(StringUtil.isNotEmpty(addUniqueKey) && nativeRecordId > 0) {
            int result = mDB.delete(DBHelper.TB_MEMBER_RECORD, (DBHelper.R_COL_ADD_UNIQUEKEY + "='" + addUniqueKey + "' AND " + DBHelper.R_COL_STATE + "=" + CommConst.STATE_RECORD_WAIT
                    + " AND " + DBHelper.ID + "=" + nativeRecordId), null);
            if(result > 0) {
                mDB.delete(DBHelper.TB_MEMBER_RECORD_ITEM, (DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + nativeRecordId), null);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取检查记录各项信息
     * @param nativeRecordId
     * @return
     */
    public Map<Integer, Boolean> getMapByRecordId(long nativeRecordId) {
        if(nativeRecordId > 0) {
            String sql = "SELECT count(*),"+DBHelper.RI_COL_DEVICE_TYPE+" FROM " + DBHelper.TB_MEMBER_RECORD_ITEM + " WHERE " + DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + nativeRecordId
                    /*+ " AND " + DBHelper.RI_COL_SOURCE + "=" + DBHelper.RI_SOURCE_DEVICE*/ + " GROUP BY " + DBHelper.RI_COL_DEVICE_TYPE;
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size = 0;
                Map<Integer, Boolean> maps = null;
                if((size = c.getCount()) > 0) {
                    maps = new HashMap<>();
                    while (c.moveToNext()) {
                        maps.put(c.getInt(1), true);
                    }
                }
                c.close();
                return maps;
            }
        }
        return null;
    }

    /**
     * 获取检查记录各项信息用于存储到远程服务器，目前要求无效数据不上传
     * @param nativeRecordId
     * @return
     */
    public List<RecordItem> getSubmitedListByRecordId(long nativeRecordId) {
        Log.d(TAG, "nativeRecordId : " + nativeRecordId);
        if(nativeRecordId > 0) {
            String sql = "SELECT * FROM " + DBHelper.TB_MEMBER_RECORD_ITEM + " WHERE " + DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + nativeRecordId;
            Log.d(TAG, "getListByRecordId : " + sql);
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size = 0;
                List<RecordItem> list = null;
                if((size = c.getCount()) > 0) {
                    list = new ArrayList<>();
                    while (c.moveToNext()) {
                        RecordItem item = new RecordItem();
                        item.setiId(c.getLong(0));
                        item.setiNativeRecordId(c.getLong(1));
                        item.setiRecordId(c.getLong(2));
                        item.setiType(c.getString(3));
                        item.setValue1(c.getFloat(4));
                        item.setSource(c.getShort(5));
                        item.setiConcluison(c.getString(6));
                        item.setDesc1(c.getString(7));
                        item.setDeviceType(c.getInt(8));
                        item.setCreateTime(DateUtil.getDateByStr(c.getString(9)));
                        item.setTestCode(c.getString(10));
                        if(!item.getDesc1().equals(CommConst.VALUE_STRANGE))
                            list.add(item);
                    }
                }
                c.close();
                return list;
            }
        }
        return null;
    }

    /**
     * 获取检查记录各项信息
     * @param nativeRecordId
     * @return
     */
    public List<RecordItem> getListByRecordId(long nativeRecordId) {
        Log.d(TAG, "nativeRecordId : " + nativeRecordId);
        if(nativeRecordId > 0) {
            String sql = "SELECT * FROM " + DBHelper.TB_MEMBER_RECORD_ITEM + " WHERE " + DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + nativeRecordId;
            Log.d(TAG, "getListByRecordId : " + sql);
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size = 0;
                List<RecordItem> list = null;
                if((size = c.getCount()) > 0) {
                    list = new ArrayList<>();
                    while (c.moveToNext()) {
                        RecordItem item = new RecordItem();
                        item.setiId(c.getLong(0));
                        item.setiNativeRecordId(c.getLong(1));
                        item.setiRecordId(c.getLong(2));
                        item.setiType(c.getString(3));
                        item.setValue1(c.getFloat(4));
                        item.setSource(c.getShort(5));
                        item.setiConcluison(c.getString(6));
                        item.setDesc1(c.getString(7));
                        item.setDeviceType(c.getInt(8));
                        item.setCreateTime(DateUtil.getDateByStr(c.getString(9)));
                        item.setTestCode(c.getString(10));
                        if(!item.getDesc1().equals(CommConst.VALUE_STRANGE))
                            list.add(item);
                    }
                }
                c.close();
                return list;
            }
        }
        return null;
    }

    /**
     * 获取某个检查项信息_ 查出历史用户的输入数据
     * @param mUniqueKey
     * @param iType
     * @return
     */
    public RecordItem getOneRecordItem2(String mUniqueKey, String iType) {
        Log.d(TAG, "nativeRecordId : " + mUniqueKey + ";  iType = " + iType);
        if(StringUtil.isNotEmpty(mUniqueKey) && StringUtil.isNotEmpty(iType)) {
           /* String sql = "SELECT * FROM " + DBHelper.TB_MEMBER_RECORD_ITEM + " WHERE " + DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + nativeRecordId
                    + " AND " + DBHelper.RI_COL_TYPE + "=?";*/
            String sql = "SELECT ri.*,r." + DBHelper.R_COL_CREATE_TIME + " FROM " + DBHelper.TB_MEMBER_RECORD + " AS r LEFT JOIN " + DBHelper.TB_MEMBER_RECORD_ITEM + " AS ri ON r."
                    + DBHelper.R_COL_RECORD_ID + "=ri." + DBHelper.RI_COL_NATIVE_RECORD_ID + " WHERE r." + DBHelper.R_COL_MEMBER_UNIQUEKEY + "=? AND ri." + DBHelper.RI_COL_TYPE + "=? order by r.createTime desc limit 0,1 ";
            Log.d(TAG, "getOneRecordItem : " + sql);
            Cursor c = mDB.rawQuery(sql, new String[]{mUniqueKey, iType});
            if(c != null) {
                RecordItem item = null;
                if(c.getCount() > 0) {
                    c.moveToFirst();
                    item = new RecordItem();
                    item.setiId(c.getLong(0));
                    item.setiNativeRecordId(c.getLong(1));
                    item.setiRecordId(c.getLong(2));
                    item.setiType(c.getString(3));
                    item.setValue1(c.getFloat(4));
                    item.setSource(c.getShort(5));
                    item.setiConcluison(c.getString(6));
                    item.setDesc1(c.getString(7));
                    item.setDeviceType(c.getInt(8));
                    item.setCreateTime(DateUtil.getDateByStr(c.getString(9)));
                    item.setTestCode(c.getString(10));
                }
                c.close();
                //if(!item.getDesc1().equals(CommConst.VALUE_STRANGE))
                    return item;
            }
        }
        return null;
    }
    /**
     * 获取某个检查项信息
     * @param nativeRecordId
     * @param iType
     * @return
     */
    public RecordItem getOneRecordItem(long nativeRecordId, String iType) {
        Log.d(TAG, "nativeRecordId : " + nativeRecordId + ";  iType = " + iType);
        if(nativeRecordId > 0 && StringUtil.isNotEmpty(iType)) {
            String sql = "SELECT * FROM " + DBHelper.TB_MEMBER_RECORD_ITEM + " WHERE " + DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + nativeRecordId
                    + " AND " + DBHelper.RI_COL_TYPE + "=?";
            Log.d(TAG, "getOneRecordItem : " + sql);
            Cursor c = mDB.rawQuery(sql, new String[]{iType});
            if(c != null) {
                RecordItem item = null;
                if(c.getCount() > 0) {
                    c.moveToFirst();
                    item = new RecordItem();
                    item.setiId(c.getLong(0));
                    item.setiNativeRecordId(c.getLong(1));
                    item.setiRecordId(c.getLong(2));
                    item.setiType(c.getString(3));
                    item.setValue1(c.getFloat(4));
                    item.setSource(c.getShort(5));
                    item.setiConcluison(c.getString(6));
                    item.setDesc1(c.getString(7));
                    item.setDeviceType(c.getInt(8));
                    item.setCreateTime(DateUtil.getDateByStr(c.getString(9)));
                    item.setTestCode(c.getString(10));
                }
                c.close();
                //if(!item.getDesc1().equals(CommConst.VALUE_STRANGE))
                return item;
            }
        }
        return null;
    }
    /**
     * 获取检查记录各项信息
     * @param mUniqueKey  会员KEY
     * @param type
     * @return
     */
    public List<RecordItem> getListByReorcdId(String mUniqueKey, String type) {
        if(StringUtil.isNotEmpty(type)) {
            String sql = "SELECT ri.*,r." + DBHelper.R_COL_CREATE_TIME + " FROM " + DBHelper.TB_MEMBER_RECORD + " AS r LEFT JOIN " + DBHelper.TB_MEMBER_RECORD_ITEM + " AS ri ON r."
                    + DBHelper.R_COL_RECORD_ID + "=ri." + DBHelper.RI_COL_NATIVE_RECORD_ID + " WHERE r." + DBHelper.R_COL_MEMBER_UNIQUEKEY + "=? AND ri." + DBHelper.RI_COL_TYPE + "=?  ORDER BY r.createTime  limit 0,1";
            Log.d(TAG, "getListByReorcdId : " + sql);
            Cursor c = mDB.rawQuery(sql, new String[]{mUniqueKey, type});
            if(c != null) {
                int size = 0;
                List<RecordItem> list = null;
                if((size = c.getCount()) > 0) {
                    Log.d(TAG, "Reorcd item size : " + size);
                    list = new ArrayList<>();
                    while (c.moveToNext()) {
                        RecordItem item = new RecordItem();
                        item.setiId(c.getLong(0));
                        item.setiNativeRecordId(c.getLong(1));
                        item.setiRecordId(c.getLong(2));
                        item.setiType(c.getString(3));
                        item.setValue1(c.getFloat(4));
                        item.setSource(c.getShort(5));
                        item.setiConcluison(c.getString(6));
                        item.setDesc1(c.getString(7));
                        item.setDeviceType(c.getInt(8));
                        item.setCreateTime(DateUtil.getDateByStr(c.getString(9)));
                        item.setTestCode(c.getString(10));
                        System.out.println("times---------" + item.getCreateTime().getTime());
                        if(!item.getDesc1().equals(CommConst.VALUE_STRANGE))
                            list.add(item);
                    }
                }
                c.close();
                return list;
            }
        }
        return null;
    }

    /**
     * 根据设备类型获取已经连接的设备列表
     * @param deviceTypeValue
     * @return
     */
    public List<MyBlueToothDevice> getConnectedBtList(int deviceTypeValue) {
        Cursor c = mDB.rawQuery("SELECT * FROM " + DBHelper.TB_DEVICE_BLUETOOTHS
                        + " WHERE " + DBHelper.B_COL_DEVICE_TYPE + "=" + deviceTypeValue + " ORDER BY "
                        + DBHelper.B_COL_CONNECT_TIME + " DESC",
                null);
        if(c!=null) {
            List<MyBlueToothDevice> lists = null;
            if(c.getCount() > 0){
                lists = new ArrayList<>();
                while (c.moveToNext()) {
                    MyBlueToothDevice bean = new MyBlueToothDevice();
                    bean.setId(c.getLong(0));
                    bean.setName(c.getString(1));
                    bean.setAddress(c.getString(2));
                    bean.setDeviceType(c.getInt(3));
                    bean.setConnectTime(c.getLong(4));
                    lists.add(bean);
                }
            }
            c.close();
            return lists;
        }
        return null;
    }

    /**
     * 登录成功后，新增登录或修改登录信息
     * @param user
     * @return
     */
    public boolean addLoginInfo(IUser user) {
        if(user != null) {
            try {
                String enCodeUniqueKey = DESUtil.encrypt(user.getUniqueKey());
                mDB.execSQL("DELETE FROM " + DBHelper.TB_LOGIN_INFO + " WHERE " + DBHelper.L_COL_UNIQUEKEY + "=?", new String[]{enCodeUniqueKey});
                String sql = "insert into " + DBHelper.TB_LOGIN_INFO + "(" + DBHelper.L_COL_UNIQUEKEY + "," + DBHelper.L_COL_SUPER_UNIQUEKEY + ","
                        + DBHelper.L_COL_USERNAME + "," + DBHelper.L_COL_PASSWORD + "," + DBHelper.L_COL_NAME + "," + DBHelper.L_COL_SEX
                        + "," + DBHelper.L_COL_LOGIN_ROLE + ") select ?,?,?,?,?,?,? where not exists(select * from "
                        + DBHelper.TB_LOGIN_INFO + " where " + DBHelper.L_COL_USERNAME + "=? and " +
                        DBHelper.L_COL_PASSWORD + "=? AND " + DBHelper.L_COL_LOGIN_ROLE + "=?)";
                mDB.execSQL(sql, new Object[]{DESUtil.encrypt(user.getUniqueKey()), (DESUtil.encrypt(user.getFaUniqueKey())), user.getUsername(), user.getPassword(), user.getName(),
                    user.getSex(), user.getRole(), user.getUsername(), user.getPassword(), user.getRole()});
                return true;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 离线登录流程
     * @param username 登录信息
     * @param password  登录密码
     * @return
     */
    public IUser checkOfflineLoginByPwd(String username, String password) {
        if(!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password)) {
            String sql = "SELECT * FROM " + DBHelper.TB_LOGIN_INFO + " WHERE " + DBHelper.L_COL_USERNAME + "=? and "
                    + DBHelper.L_COL_PASSWORD + "=?";
            Cursor cursor = mDB.rawQuery(sql, new String[]{username, password});
            if(cursor != null) {
                IUser user = null;
                if(cursor.getCount() > 0){
                    cursor.moveToFirst();
                    user = new RoleUser();
                    user.setId(cursor.getLong(0));
                    user.setUniqueKey(DESUtil.decrypt(cursor.getString(1)));
                    user.setFaUniqueKey(DESUtil.decrypt(cursor.getString(2)));
                    user.setRole(cursor.getInt(3));
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setName(cursor.getString(6));
                    user.setSex(cursor.getString(7));
                }
                cursor.close();
                return user;
            }
        }
        return null;
    }
    //String sql = "insert into " + DBHelper.TB_WAIT_FOR_INSPECTORS + "(" +
    // DBHelper.W_COL_MEMBER_UNIQUEKEY + ",age) select 'zz7zz7zz',25 where not exists(select *
    // from student where name='zz7zz7zz' and age=25)";

    /**
     * 根据数据类型和门诊Key获取该数据的版本号
     * @param superUnqueKey 门诊Key
     * @param type  数据类型
     * @return
     */
    public long getDataVersionByType(String superUnqueKey, short type) {
        if(StringUtil.isNotEmpty(superUnqueKey) && type > 0) {
            String sql = "SELECT * FROM " + DBHelper.TB_DATA_VERSION + " WHERE " + DBHelper.V_COL_UNIQUNKEY
                    + "=? AND " + DBHelper.V_COL_VERSION_TYPE + "=" + type;
            Log.d(TAG, "getVersion : " + sql);
            Cursor cursor = mDB.rawQuery(sql, new String[]{superUnqueKey});
            if(cursor != null) {
                long version = 0;
                if(cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    /*DataVersion dataVersion = new DataVersion();
                    dataVersion.setId(cursor.getInt(0));
                    dataVersion.setvUniqueKey(cursor.getString(1));
                    dataVersion.setVersionType(cursor.getShort(2));
                    dataVersion.setVersion(cursor.getLong(3));*/
                    version = cursor.getLong(3);
                }
                cursor.close();
                return version;
            }
        }
        return 0;
    }

    /**
     * 保存新的版本号，本地不存在时会创建新数据
     * @param superUnqueKey
     * @param type
     * @param version
     * @return
     */
    public boolean saveDataVersion(String superUnqueKey, short type, long version) {
        if(StringUtil.isNotEmpty(superUnqueKey) && type > 0 && version > 0) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.V_COL_VERSION, version);
            String whereClause = DBHelper.V_COL_UNIQUNKEY + "=? AND " + DBHelper.V_COL_VERSION_TYPE + "=" + type;
            int result = mDB.update(DBHelper.TB_DATA_VERSION, values, whereClause, new String[]{superUnqueKey});
            if(result > 0) {
                return true;
            } else {
                values.put(DBHelper.V_COL_UNIQUNKEY, superUnqueKey);
                values.put(DBHelper.V_COL_VERSION_TYPE, type);
                values.put(DBHelper.V_COL_VERSION, version);
                if(mDB.insert(DBHelper.TB_DATA_VERSION, null, values) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existInLocalDB(Member member)
    {
        String sql="SELECT * FROM " + DBHelper.TB_MEMBERS + " WHERE "+DBHelper.M_COL_UNIQUEKEY+"=?";
        Cursor cursor=mDB.rawQuery(sql,new String [] {member.getUniqueKey()});
        if(cursor!=null&&cursor.getCount()>0)
        {
            cursor.moveToFirst();
            member.setId(cursor.getLong(0));
            return true;
        }
        return false;
    }

    /**
     * 保存会员信息，并将state置为1
     * @param member
     * @return
     */
    public boolean saveMemberInfo(Member member) {
        if(member != null) {
            if(!existInLocalDB(member)) {
                String sql = "insert into " + DBHelper.TB_MEMBERS + "(" + DBHelper.M_COL_UNIQUEKEY + "," + DBHelper.M_COL_USER_UNIQUEKY
                        + "," + DBHelper.M_COL_HEADPATH + "," + DBHelper.M_COL_NAME + "," + DBHelper.M_COL_SEX + "," + DBHelper.M_COL_IDNUMBER
                        + "," + DBHelper.M_COL_TEL + "," + DBHelper.M_COL_PHONE + "," + DBHelper.M_COL_ADDRESS + "," + DBHelper.M_COL_BRITHDAY
                        + "," + DBHelper.M_COL_ANAMNESIS + "," + DBHelper.M_COL_SSN + "," + DBHelper.M_COL_CREATE_PERSION + ","
                        + DBHelper.M_COL_CREATE_TIME + "," + DBHelper.M_COL_UPDATE_TIME + "," + DBHelper.M_COL_VERSION + "," + DBHelper.M_COL_STATE
                        + ") VALUES( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
                Log.d(TAG, "saveMemberInfo : " + sql);
                mDB.execSQL(sql, new Object[]{member.getUniqueKey(), member.getFaUniqueKey(), member.getmHeadPath(), member.getName(),
                        member.getSex(), member.getmIdNumber(), member.getmTel(), member.getmPhone(), member.getmAddress(), member.getmBrithday(),
                        member.getmAnamnesis(), member.getmSSN(), member.getmCreatePersion(), member.getmCreateTime(), member.getmUpdateTime(),
                        member.getVersion(), 1});
                sql="select max(id) FROM "+DBHelper.TB_MEMBERS;
                Cursor c=mDB.rawQuery(sql,new String[]{});
                if(c.moveToFirst())
                {
                    long id=c.getLong(0);
                    if(id>0)
                    {
                        member.setId(id);
                        return true;
                    }
                }
                /*if (member.isSuperClinic()) {
                    Cursor cur = mDB.rawQuery("SELECT * FROM " + DBHelper.TB_MEMBERS + " WHERE " + DBHelper.M_COL_UNIQUEKEY + "=?", new String[]{member.getUniqueKey()});
                    cur.moveToFirst();
                    long id = cur.getLong(0);
                    cur.close();
                    member.setId(id);
                }*/
            }
            else
                return true;
        }
        return false;
    }

    /**
     * 根据会员所属门诊，查询当前门诊下的待检会员列表
     * @param superUniqueKey 门诊KEY
     * @param offset  偏移
     * @return
     */
    public List<Member> getClientMemberList(String superUniqueKey, int offset) {
        List<Member> members = new ArrayList<>();
        if(StringUtil.isNotEmpty(superUniqueKey)) {
            String sql="";
            if(SysApp.LOGIN_STATE==CommConst.FLAG_USER_STATE_OFFLINE_LOGIN) {
                sql = "SELECT * FROM " + DBHelper.TB_MEMBERS + " WHERE " + DBHelper.M_COL_USER_UNIQUEKY + "='" + superUniqueKey
                        + "' ORDER BY id DESC LIMIT " + CommConst.DB_DATA_PAGE + " OFFSET " + offset;
            }
            else
            {
                sql = "SELECT * FROM " + DBHelper.TB_MEMBERS + " WHERE " + DBHelper.M_COL_USER_UNIQUEKY + "='" + superUniqueKey
                        + "' AND state!=0 ORDER BY id DESC LIMIT " + CommConst.DB_DATA_PAGE + " OFFSET " + offset;
            }
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size = 0;

                if((size = c.getCount()) > 0) {
                    Log.d(TAG, "getWaitInspectorMemberList >>> size = " + size);
                    //c.moveToFirst();
                    while (c.moveToNext()) {
                        members.add(getMemberByCursor(c));
                    }
                }
                c.close();
            }
            return members;
        }
        return new ArrayList<Member>();
    }

    /**
     * 根据关键字，搜索当前门诊下的会员列表
     * @param superUniqueKey
     * @param nameLike
     * @param mobileLike
     * @param idNumberLike
     * @return
     */
    public List<Member> searchClientMember(String superUniqueKey, String nameLike, String mobileLike, String idNumberLike) {
        if(StringUtil.isNotEmpty(superUniqueKey) && (StringUtil.isNotEmpty(nameLike) || StringUtil.isNotEmpty(mobileLike)
                || StringUtil.isNotEmpty(idNumberLike))) {
            String sql = "SELECT * FROM " + DBHelper.TB_MEMBERS + " WHERE " + DBHelper.M_COL_USER_UNIQUEKY + "='" +
                    superUniqueKey + "' AND (";
            int count = 0;
            if(StringUtil.isNotEmpty(nameLike)) {
                sql += DBHelper.M_COL_NAME + " LIKE '%" + nameLike + "%'";
                count++ ;
            }
            if(StringUtil.isNotEmpty(mobileLike)) {
                if(count > 0) {
                    sql += " AND ";
                }
                sql += DBHelper.M_COL_PHONE + " LIKE '%" + mobileLike + "%'";
                count++ ;
            }
            if(StringUtil.isNotEmpty(idNumberLike)) {
                if(count > 0) {
                    sql += " AND ";
                }
                sql += DBHelper.M_COL_IDNUMBER + " LIKE '%" + idNumberLike + "%'";
            }

            sql += ") ORDER BY id DESC";
            Cursor c = mDB.rawQuery(sql, null);
            if(c != null) {
                int size = 0;
                List<Member> members = null;
                if((size = c.getCount()) > 0) {
                    Log.d(TAG, "getWaitInspectorMemberList >>> size = " + size);
                    //c.moveToFirst();
                    members = new ArrayList<>();
                    while (c.moveToNext()) {
                        members.add(getMemberByCursor(c));
                    }
                }
                c.close();
                return members;
            }
        }
        return null;
    }

    /**
     * 保存连接成功的蓝牙地址
     * @param device
     * @return
     */
    public boolean saveBTDeviceInfo(MyBlueToothDevice device, int deviceType) {
        if(device != null && StringUtil.isNotEmpty(device.getAddress()) && deviceType >= 0) {
            /*String sql = "INSERT INTO " + DBHelper.TB_DEVICE_BLUETOOTHS + "(" + DBHelper.B_COL_MAC + ","  + DBHelper.B_COL_DEVICE_TYPE
                    + "," + DBHelper.B_COL_CONNECT_TIME + ") select ?,?,? WHERE NOT EXISTS(SELECT * FROM " + DBHelper.TB_DEVICE_BLUETOOTHS
                    + " WHERE " + DBHelper.B_COL_MAC ;*/
//            String whereArgs = DBHelper.B_COL_MAC + "=? AND " + DBHelper.B_COL_DEVICE_TYPE + "=" + deviceType;
//             mDB.delete(DBHelper.TB_DEVICE_BLUETOOTHS, whereArgs, new String[]{address});

            deleteBTDeviceInfo(device.getAddress(), deviceType);
            String sql="SELECT * FROM tb_device_bluetooths WHERE device_type="+deviceType+" ORDER BY connect_time DESC";
            Cursor c=mDB.rawQuery(sql,null);
            if(c.getCount()>2)
            {
                c.moveToPosition(1);
                long min_conn_time=c.getLong(c.getColumnIndex("connect_time"));
                sql="DELETE FROM tb_device_bluetooths WHERE connect_time<"+min_conn_time;
                mDB.execSQL(sql);
            }
            ContentValues values = new ContentValues();
            values.put(DBHelper.B_COL_DEIVCE_NAME, device.getName());
            values.put(DBHelper.B_COL_MAC, device.getAddress());
            values.put(DBHelper.B_COL_DEVICE_TYPE, deviceType);
            values.put(DBHelper.B_COL_CONNECT_TIME, new Date().getTime());
            long result = mDB.insert(DBHelper.TB_DEVICE_BLUETOOTHS, null, values);
            if (result > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除对应设备类型的设备
     * @param address
     * @param deviceType
     * @return
     */
    public boolean deleteBTDeviceInfo(String address, int deviceType) {
        if(StringUtil.isNotEmpty(address) && deviceType >= 0) {
            String whereArgs = DBHelper.B_COL_MAC + "=? AND " + DBHelper.B_COL_DEVICE_TYPE + "=" + deviceType;
            int result = mDB.delete(DBHelper.TB_DEVICE_BLUETOOTHS, whereArgs, new String[]{address});
            if(result > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 提交已经完成的信息
     * @param uniquekey
     * @param nativeRecordId
     * @return
     */
    public boolean submitRecordInfo(String uniquekey, long nativeRecordId) {
        if(StringUtil.isNotEmpty(uniquekey) && nativeRecordId > 0) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.R_COL_STATE, CommConst.STATE_RECORD_NEED_UPLOAD);
            int result = mDB.update(DBHelper.TB_MEMBER_RECORD, values, (DBHelper.R_COL_ADD_UNIQUEKEY + "=? AND " + DBHelper.R_COL_RECORD_ID + "=" + nativeRecordId),
                    new String[]{uniquekey});
            if(result > 0)
                return true;
            else
                return false;
        }
        return false;
    }

    /**
     * 提交已经完成的信息
     * @param uniquekey
     * @param nativeRecordId
     * @return
     */
    public boolean submitRecordSuccess(String uniquekey, long nativeRecordId, long recordId, Date createTime) {
        if(StringUtil.isNotEmpty(uniquekey) && nativeRecordId > 0 && createTime != null) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.R_COL_RECORD_ID, recordId);
            values.put(DBHelper.R_COL_STATE, CommConst.STATE_RECORD_SUCCESS);
            String date = DateUtil.getDateByTimeLong(createTime.getTime());
            values.put(DBHelper.R_COL_CREATE_TIME, date);
            values.put(DBHelper.R_COL_UPDATE_TIME, date);
            int result = mDB.update(DBHelper.TB_MEMBER_RECORD, values, (DBHelper.R_COL_ADD_UNIQUEKEY+"=? AND "+DBHelper.R_COL_RECORD_ID+"="+nativeRecordId),
                    new String[]{uniquekey});
            if(result > 0) {
                values = new ContentValues();
                values.put(DBHelper.RI_COL_RECORD_ID, recordId);
                mDB.update(DBHelper.TB_MEMBER_RECORD_ITEM, values, (DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + nativeRecordId),
                        null);
                return true;
            }
        }
        return false;
    }

    /**
     * 新增会员，
     * @param member  会员信息
     * @return
     */
    public boolean addMember(Member member) {
        if(member != null && StringUtil.isNotEmpty(member.getFaUniqueKey())) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.M_COL_USER_UNIQUEKY, member.getFaUniqueKey());
            values.put(DBHelper.M_COL_HEADPATH, member.getmHeadPath());
            values.put(DBHelper.M_COL_NATIVE_HEADPATH, member.getmNativeHeadPath());
            values.put(DBHelper.M_COL_NAME, member.getName());
            values.put(DBHelper.M_COL_SEX, member.getSex());
            values.put(DBHelper.M_COL_IDNUMBER, member.getmIdNumber());
            values.put(DBHelper.M_COL_TEL, member.getmTel());
            values.put(DBHelper.M_COL_PHONE, member.getmPhone());
            values.put(DBHelper.M_COL_ADDRESS, member.getmAddress());
            values.put(DBHelper.M_COL_BRITHDAY, member.getmBrithday());
            values.put(DBHelper.M_COL_ANAMNESIS, member.getmAnamnesis());
            values.put(DBHelper.M_COL_SSN, member.getmSSN());
            values.put(DBHelper.M_COL_PINYIN,"");
            values.put(DBHelper.M_COL_CREATE_PERSION, member.getmCreatePersion());
            member.setmCreateTime(System.currentTimeMillis());
            values.put(DBHelper.M_COL_CREATE_TIME, member.getmCreateTime());
            member.setmUpdateTime(member.getmCreateTime());
            values.put(DBHelper.M_COL_UPDATE_TIME, member.getmUpdateTime());
            values.put(DBHelper.M_COL_STATE, 0);
            values.put(DBHelper.M_COL_ADD_UNIQUEKEY, member.getAddUniqueKey());
            if(StringUtil.isNotEmpty(member.getWithDoctor())) {  //当护士账号登录时，需要指向所属医生
                values.put(DBHelper.M_COL_WITH_DOCTOR, member.getWithDoctor());
            }

            long result = mDB.insert(DBHelper.TB_MEMBERS, null, values);
            String sql="SELECT max(id) FROM "+DBHelper.TB_MEMBERS;
            Cursor c=mDB.rawQuery(sql,new String[]{});
            if(c.getCount()>0)
            {
                c.moveToFirst();
                member.setId(c.getInt(0));
            }

            if (result > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在相同会员信息
     * @param member
     * @return
     */
    public boolean hasSamePhoneMember(Member member) {
        boolean hasSame = false;
        if (member != null && StringUtil.isNotEmpty(member.getFaUniqueKey())) {

            Cursor cursor = mDB.rawQuery("SELECT * FROM " + DBHelper.TB_MEMBERS + " WHERE " + DBHelper.M_COL_PHONE + "=? AND " + DBHelper.M_COL_NAME + "=?", new String[]{member.getmPhone(), member.getName()});
            if (cursor != null && cursor.getCount() > 0) {
                hasSame = true;
            }
            if(cursor != null) {
                cursor.close();
            }
        }
        return hasSame;
    }

    /**
     * 修改本地会员的Key(仅在新增会员直接上传会员调用)
     * @param member
     * @return
     */
    public boolean updateMemberKeyCreatTime(Member member) {
        if(member != null && StringUtil.isNotEmpty(member.getUniqueKey()) && member.getmCreateTime() > 0) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.M_COL_UNIQUEKEY, member.getUniqueKey());
            values.put(DBHelper.M_COL_VERSION, member.getVersion());
            values.put(DBHelper.M_COL_STATE, 1);
            values.put(DBHelper.M_COL_PINYIN, member.getmNamePinyin());
            String whereClause = DBHelper.ID + "=" + member.getId();
            int result = mDB.update(DBHelper.TB_MEMBERS, values, whereClause, null);
            if(result > 0) {
                saveDataVersion(member.getFaUniqueKey(), CommConst.FLAG_USER_ROLE_MEMBER, member.getVersion());
                return true;
            }
        }
        return false;
    }

    /**
     * 修改本地会员的Key，在同步时调用
     * @param member
     * @return
     */
    public boolean updateMemberKeyById(Member member) {
        if(member != null && StringUtil.isNotEmpty(member.getUniqueKey()) && member.getId() > 0) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.M_COL_UNIQUEKEY, member.getUniqueKey());
            values.put(DBHelper.M_COL_VERSION, member.getVersion());
            values.put(DBHelper.M_COL_STATE, 1);
            String whereClause = DBHelper.ID + "=" + member.getId();
            int result = mDB.update(DBHelper.TB_MEMBERS, values, whereClause, null);
            if(result > 0) {
                updateMemberRecordKey(member.getUniqueKey(), member.getId());
                saveDataVersion(member.getFaUniqueKey(), CommConst.FLAG_USER_ROLE_MEMBER, member.getVersion());
                return true;
            }
        }
        return false;
    }

    /**
     * 修改本地会员的Key，在同步时调用
     * @param member
     * @return
     */
    public boolean updateMemberHeadById(Member member) {
        if(member != null && member.getId() > 0) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.M_COL_HEADPATH, member.getmHeadPath());
            String whereClause = DBHelper.ID + "=" + member.getId();
            int result = mDB.update(DBHelper.TB_MEMBERS, values, whereClause, null);
            if(result > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 修改会员对应检查记录中的会员KEY
     * @param memberUniqueKey
     * @param nativeMemberId
     * @return
     */
    private boolean updateMemberRecordKey(String memberUniqueKey, long nativeMemberId) {
        if(StringUtil.isNotEmpty(memberUniqueKey) && nativeMemberId > 0) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.R_COL_MEMBER_UNIQUEKEY, memberUniqueKey);
            String whereClause = DBHelper.R_COL_NATIVE_MEMBER_ID + "=" + nativeMemberId;
            int result = mDB.update(DBHelper.TB_MEMBER_RECORD, values, whereClause, null);
            if(result > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据会员创建时间删除会员
     * @param createTime
     * @return
     */
    public boolean deleteMemberByCreateTime(long createTime) {
        if(createTime > 0) {
            String whereClause = DBHelper.M_COL_CREATE_TIME + "=" + createTime;
            int result = mDB.delete(DBHelper.TB_MEMBERS, whereClause, null);
            if(result > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除角色用户列表
     * @param addUniqueKey
     * @param role 该帐号下的某种角色用户
     * @return
     */
    public boolean deleteRoleUserByAddUniqueKey(String addUniqueKey, int role) {
        if(StringUtil.isNotEmpty(addUniqueKey)) {
            String whereClause = DBHelper.U_COL_ADD_BY_UNIQUEKEY + "=? AND " + DBHelper.U_COL_USER_ROLE + "=" + role;
            mDB.delete(DBHelper.TB_USERS, whereClause, new String[]{addUniqueKey});
            return true;
        }
        return false;
    }

    /**
     * 保存角色信息
     * @param user
     * @return
     */
    public boolean saveRoleUser(RoleUser user) {
        if(user != null && StringUtil.isNotEmpty(user.getAddByUniqueKey())) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.U_COL_UNIQUEKEY, user.getUniqueKey());
            values.put(DBHelper.U_COL_USER_ROLE, user.getRole());
            values.put(DBHelper.U_COL_FA_UNIQUEKEY, user.getFaUniqueKey());
            values.put(DBHelper.U_COL_USERNAME, user.getName());
            values.put(DBHelper.U_COL_SEX, user.getSex());
            values.put(DBHelper.U_COL_BRITHDAY, user.getBrithday());
            values.put(DBHelper.U_COL_JOB_TITLE, user.getJobTitle());
            values.put(DBHelper.U_COL_JOB_POSITION, user.getJobPosition());
            values.put(DBHelper.U_COL_JOB_DEPARTMENT, user.getJobDepartment());
            values.put(DBHelper.U_COL_ADD_BY_UNIQUEKEY, user.getAddByUniqueKey());
            long result = mDB.insert(DBHelper.TB_USERS, null, values);
            if(result > 0) {
                return true;
            }

        }
        return false;
    }

    /**
     * 根据key
     * @param uniqueKey
     * @param role
     * @return
     */
    public List<RoleUser> getRoleUserList(String uniqueKey, int role) {
        if(StringUtil.isNotEmpty(uniqueKey) && role > 0) {
            String sql = "SELECT * FROM " + DBHelper.TB_USERS + " WHERE " + DBHelper.U_COL_ADD_BY_UNIQUEKEY + "=? AND "
                    + DBHelper.U_COL_USER_ROLE + "=" + role;
            Cursor cursor = mDB.rawQuery(sql, new String[]{uniqueKey});
            if(cursor != null) {
                List<RoleUser> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    RoleUser user = new RoleUser();
                    user.setId(cursor.getLong(0));
                    user.setUniqueKey(cursor.getString(1));
                    user.setFaUniqueKey(cursor.getString(2));
                    user.setRole(cursor.getInt(3));
                    user.setName(cursor.getString(4));
                    user.setSex(cursor.getString(5));
                    user.setBrithday(cursor.getString(6));
                    user.setJobTitle(cursor.getString(7));
                    user.setJobPosition(cursor.getString(8));
                    user.setJobDepartment(cursor.getString(9));
                    user.setAddByUniqueKey(uniqueKey);
                    list.add(user);
                }
                return list;
            }

        }
        return null;
    }

    /**
     * 获取每种设备的连接数量
     * @return
     */
    public Map<Integer, Integer> getDeviceCountByType() {
        String sql = "SELECT " + DBHelper.B_COL_DEVICE_TYPE + ",COUNT(" + DBHelper.ID + ") AS device_count FROM " +
                DBHelper.TB_DEVICE_BLUETOOTHS + " GROUP BY " + DBHelper.B_COL_DEVICE_TYPE + " ORDER BY "
                + DBHelper.B_COL_DEVICE_TYPE;
        Cursor cursor = mDB.rawQuery(sql, null);
        Map<Integer, Integer> deviceCounts = new HashMap<>();
        if(cursor != null) {
            while (cursor.moveToNext()) {
                deviceCounts.put(cursor.getInt(0), cursor.getInt(1));
            }
        }
        return deviceCounts;
    }

    /**
     * 获取一个设备所有的检查记录
     * @param uniqueKey
     * @param checkType
     * @return
     */
    public List<RecordItem> getRecordAllInfoByType(String uniqueKey, String... checkType) {
        if(StringUtil.isNotEmpty(uniqueKey) && checkType.length > 0 && checkType.length <= 5) {
            String checkValue = "",  leftJoin = "";
            for(int i = 0; i < checkType.length; i++) {
                String type = checkType[i];
                if(StringUtil.isNotEmpty(type)) {
                    checkValue += ",ri" + i + "." + DBHelper.RI_COL_VALUE1 +",ri" + i + "." + DBHelper.RI_COL_DESC1 + ",ri" + i + "." + DBHelper.RI_COL_RECORD_TIME+",ri" + i + "." + DBHelper.RI_COL_DEVICE_TYPE  ;
                    leftJoin += " LEFT JOIN " + DBHelper.TB_MEMBER_RECORD_ITEM + " AS ri" + i + " ON r." + DBHelper.R_COL_RECORD_ID + "=ri" + i + "." +
                            DBHelper.RI_COL_NATIVE_RECORD_ID + " AND ri"  + i + "." + DBHelper.RI_COL_TYPE + "=?  ";
                }
                else
                {
                    return null;
                }
            }
            String[] params = new String[checkType.length + 1];
            System.arraycopy(checkType, 0, params, 0, checkType.length);
            params[checkType.length] = uniqueKey;
            String sql = "SELECT r.id, r." + DBHelper.R_COL_CREATE_TIME + ",r." + DBHelper.R_COL_STATE + checkValue + " FROM "
                    + DBHelper.TB_MEMBER_RECORD + " AS r " + leftJoin + " WHERE ri0." + DBHelper.RI_COL_DEVICE_TYPE + ">=0 AND r."
                    + DBHelper.R_COL_MEMBER_UNIQUEKEY + "=? GROUP BY r." + DBHelper.R_COL_RECORD_ID+" ORDER BY  r."+DBHelper.R_COL_RECORD_ID + " DESC limit 0,1";
            Log.d(TAG, "sql = " + sql);
            Cursor c = mDB.rawQuery(sql, params);
            if(c != null && c.getCount() > 0) {
                List<RecordItem> lists = new ArrayList<>();
                while (c.moveToNext()) {
                    RecordItem item = new RecordItem();
                    item.setiNativeRecordId(c.getLong(0));
                    long tmp=c.getLong(1);
                    item.setCreateTime(DateUtil.getDateByStr(c.getString(1)));
                    item.setState(c.getInt(2));
                    boolean bFound=false;
                    for(int i=0;i<checkType.length;i++)
                    {
                        int valIdx=i*4+3;
                        String datetimeStr=c.getString(valIdx+2);
                        if(datetimeStr!=null)
                        {
                            bFound=true;
                            item.setCreateTime(DateUtil.getDateByStr(datetimeStr));
                            item.setDeviceType(c.getInt(valIdx+3));
                            float val=c.getFloat(valIdx);
                            String descr=c.getString(valIdx+1);
                            switch(i)
                            {
                                case 0:
                                    item.setValue1(val);
                                    item.setDesc1(descr);
                                    item.setValue1Txt(RecordItem.format(val,checkType[0],descr));
                                    break;
                                case 1:
                                    item.setValue2(val);
                                    item.setDesc2(descr);
                                    item.setValue2Txt(RecordItem.format(val,checkType[1],descr));
                                    break;
                                case 2:
                                    item.setValue3(val);
                                    item.setDesc3(descr);
                                    item.setValue3Txt(RecordItem.format(val,checkType[2],descr));
                                    break;
                                case 3:
                                    item.setValue4(val);
                                    item.setDesc4(descr);
                                    item.setValue4Txt(RecordItem.format(val,checkType[3],descr));
                                    break;
                                case 4:
                                    item.setValue5(val);
                                    item.setDesc5(descr);
                                    item.setValue5Txt(RecordItem.format(val,checkType[4],descr));
                                    break;
                            }
                        }
                    }
                    if(bFound)
                        lists.add(item);
                }
                return lists;
            }
            c.close();
        }
        return null;
    }


    /**
     * 删除检查项记录
     * @param item
     * @return
     */
    public boolean deleteRecordItemByType(RecordItem item) {
        if(item != null && item.getiNativeRecordId() > 0) {
            String whereArgs = DBHelper.RI_COL_NATIVE_RECORD_ID + "=" + item.getiNativeRecordId() + " AND " +
                    //DBHelper.RI_COL_DEVICE_TYPE + "=" + item.getDeviceType()+ " AND "+DBHelper.RI_COL_TESTCODE + "='"+item.getTestCode()+"'";
                    DBHelper.RI_COL_DEVICE_TYPE + "=" + item.getDeviceType();
            int result = mDB.delete(DBHelper.TB_MEMBER_RECORD_ITEM, whereArgs, null);
            if(result > 0) {
                return true;
            }
        }
        return false;
    }
}
