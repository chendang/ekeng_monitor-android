package com.cnnet.otc.health.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SZ512 on 2016/1/5.
 */
public class DateUtil {

    public static String getDateStr(Date time, String todayStr) {
        if(time != null) {
            String date = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            Calendar curCal = Calendar.getInstance();
            curCal.setTimeInMillis(System.currentTimeMillis());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            date =  " " + (hour<10?("0"+hour):hour) + ":" + (minute<10?("0"+minute):minute);
            if(calendar.get(Calendar.YEAR) == curCal.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH) == curCal.get(Calendar.MONTH)
                    && calendar.get(calendar.DAY_OF_MONTH) == curCal.get(Calendar.DAY_OF_MONTH)) {
                date = todayStr + date;
            } else {
				int month = calendar.get(Calendar.MONTH) + 1;
				int day = calendar.get(calendar.DAY_OF_MONTH);
                date = (calendar.get(Calendar.YEAR) % 100) + "/" + (month<10?("0"+month):month) +"/"
                        + (day<10?("0"+day):day) + date;
            }
            return date;
        }
        return null;
    }

    /**
     * 根据时间戳获取当前日期字符串yyyy-MM-dd HH:mm:ss
     * @param time
     * @return
     */
    public static String getDateByTimeLong(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
    /**
     * 根据日期格式获取日期字符串yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String getDateStrByDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
    /**
     * 获取信息yyyy-MM-dd HH:mm:ss
     * @param dateStr
     * @return
     */
    public static Date getDateByStr(String dateStr) {
        if(dateStr != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return format.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
        //return format.format(date);
    }

    /**
     * 获取信息yyyy-MM-dd
     * @param dateStr
     * @return
     */
    public static Date getOnlyDateByStr(String dateStr) {
        if(dateStr != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                return format.parse(dateStr);
            } catch (ParseException e) {
                try{
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                    return format.parse(dateStr);
                }catch (ParseException e1){
                    e1.printStackTrace();
                }

            }
        }
        return null;
        //return format.format(date);
    }

    /**
     * 根据出生日期获取年龄
     * @param birthDay
     * @return
     * @throws Exception
     */
    public static int getAgeByBirthDay(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            }else{
                age--;
            }
        }
        System.out.println("age:"+age);
        return age;
    }

    /**
     * 根据生日字符串获取年龄
     * @param birthDayStr
     * @return
     */
    public static int getAgeByBirthDayStr(String birthDayStr) {
        Date birthDay = getOnlyDateByStr(birthDayStr);
        if(birthDay != null) {
            try {
                return getAgeByBirthDay(birthDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

}
