package com.qd.longchat.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/7 上午10:15
 */
public class QDDateUtil {

    public final static String MSG_FORMAT1 = "HH:mm";
    public final static String MSG_FORMAT2 = "MM月dd日 HH:mm";
    public final static String MSG_FORMAT3 = "yyyy年MM月dd日 HH:mm";
    public final static String MSG_FORMAT4 = "yyyy-MM-dd HH:mm:ss";
    public final static String MSG_FORMAT5 = "yyyy/MM/dd";
    public final static String MSG_FORMAT6 = "HH:mm:ss";
    public final static String CONVERSATION_FORMAT = "MM月dd日";
    public final static String FILE_FORMAT = "yyyy年MM月dd日";
    public final static String CLOUD_FORMAT = "yyyy/MM/dd HH:mm";
    public final static String ACC_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FOR_MEETING = "yyyy-MM-dd HH:mm";
    public static final String DATE_FOR_ORDER = "MM-dd HH:mm";



    /**
     * 获取消息时间
     * @param time 毫秒
     * @return
     */
    public static String getMsgTime(long time) {
        Calendar timeCal = Calendar.getInstance();
        Date timeDate = new Date(time);
        timeCal.setTime(timeDate);

        Date currentDate = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        int timeYear = timeCal.get(Calendar.YEAR);
        int currentYear = currentCal.get(Calendar.YEAR);
        int timeDay = timeCal.get(Calendar.DAY_OF_YEAR);
        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);
        return getDateFormat(MSG_FORMAT1).format(timeDate);

    }
    /**
     * 获取消息时间
     * @param time 毫秒
     * @return
     */
    public static String getMeetTime(long time) {
        Calendar timeCal = Calendar.getInstance();
        Date timeDate = new Date(time);
        timeCal.setTime(timeDate);

        Date currentDate = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        int timeYear = timeCal.get(Calendar.YEAR);
        int currentYear = currentCal.get(Calendar.YEAR);
        int timeDay = timeCal.get(Calendar.DAY_OF_YEAR);
        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);
        return getDateFormat(DATE_FOR_MEETING).format(timeDate);

    }
    /**
     * 获取会话时间
     * @param time 毫秒
     * @return
     */
    public static String getConversationTime(long time) {
        Calendar timeCal = Calendar.getInstance();
        Date timeDate = new Date(time);
        timeCal.setTime(timeDate);

        Date currentDate = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        int timeYear = timeCal.get(Calendar.YEAR);
        int currentYear = currentCal.get(Calendar.YEAR);
        int timeDay = timeCal.get(Calendar.DAY_OF_YEAR);
        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);

        if (timeYear == currentYear) {
            if (timeDay == currentDay) {
                return getDateFormat(MSG_FORMAT1).format(timeDate);
            }
            if (currentDay - timeDay == 1) {
                return "昨天";
            }
            return getDateFormat(CONVERSATION_FORMAT).format(timeDate);
        } else {
            return getDateFormat(DATE_FOR_MEETING).format(timeDate);
        }
    }


    public static boolean isToday(long time) {
        Calendar timeCal = Calendar.getInstance();
        Date timeDate = new Date(time);
        timeCal.setTime(timeDate);

        Date currentDate = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        int timeYear = timeCal.get(Calendar.YEAR);
        int currentYear = currentCal.get(Calendar.YEAR);
        int timeDay = timeCal.get(Calendar.DAY_OF_YEAR);
        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);

        if (timeYear == currentYear && timeDay == currentDay) {
            return true;
        }
        return false;
    }

    public static boolean isYesterday(long time) {
        Calendar timeCal = Calendar.getInstance();
        Date timeDate = new Date(time);
        timeCal.setTime(timeDate);

        Date currentDate = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        int timeYear = timeCal.get(Calendar.YEAR);
        int currentYear = currentCal.get(Calendar.YEAR);
        int timeDay = timeCal.get(Calendar.DAY_OF_YEAR);
        int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);

        if (timeYear == currentYear && currentDay - timeDay == 1) {
            return true;
        }
        return false;
    }

    public static String dateToString(Date date, String format) {
        return getDateFormat(format).format(date);
    }

    private static SimpleDateFormat getDateFormat(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    public static String longToString(long time, String format) {
        Date date = new Date(time);
        return dateToString(date, format);
    }
}
