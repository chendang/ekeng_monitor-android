package com.foxchen.ekengmonitor.bean;

import com.lifesense.lssleepanalyze_ndk.LSSleepStatusData;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/6.
 */

public class PedometerSleepAnalyzeData {
    public long getSleepTimeUTC() {
        return sleepTimeUTC;
    }

    public void setSleepTimeUTC(long sleepTimeUTC) {
        this.sleepTimeUTC = sleepTimeUTC;
    }

    public long getGetupTimeUTC() {
        return getupTimeUTC;
    }

    public void setGetupTimeUTC(long getupTimeUTC) {
        this.getupTimeUTC = getupTimeUTC;
    }

    public long getDeepSleepTime() {
        return deepSleepTime;
    }

    public void setDeepSleepTime(long deepSleepTime) {
        this.deepSleepTime = deepSleepTime;
    }

    public long getLightSleepTime() {
        return lightSleepTime;
    }

    public void setLightSleepTime(long lightSleepTime) {
        this.lightSleepTime = lightSleepTime;
    }

    public long getAwakeSleepTime() {
        return awakeSleepTime;
    }

    public void setAwakeSleepTime(long awakeSleepTime) {
        this.awakeSleepTime = awakeSleepTime;
    }

    public long getAwakeCount() {
        return awakeCount;
    }

    public void setAwakeCount(long awakeCount) {
        this.awakeCount = awakeCount;
    }

    public ArrayList<LSSleepStatusData> getSleepStatus() {
        return sleepStatus;
    }

    public void setSleepStatus(ArrayList<LSSleepStatusData> sleepStatus) {
        this.sleepStatus = sleepStatus;
    }

    public long sleepTimeUTC;
    public long getupTimeUTC;
    public long deepSleepTime;
    public long lightSleepTime;
    public long awakeSleepTime;
    public long awakeCount;
    public ArrayList<LSSleepStatusData> sleepStatus;
}
