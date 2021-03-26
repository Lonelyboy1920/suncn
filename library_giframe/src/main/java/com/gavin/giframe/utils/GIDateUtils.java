package com.gavin.giframe.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期、时间相关工具类
 */
public class GIDateUtils {
    /**
     * 返回unix时间戳 (1970年至今的秒数)
     */
    public static long getUnixStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 得到昨天的日期
     */
    public static String getYestoryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    /**
     * 得到今天的日期
     */
    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 得到今天的日期和时间
     */
    public static String getTodayTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 时间戳转化为时间格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String timeStampToStr(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(timeStamp * 1000);
    }

    /**
     * 得到日期（yyyy-MM-dd）
     *
     * @param timeStamp 时间戳
     */
    public static String formatDate(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(timeStamp * 1000);
    }

    /**
     * 得到时间  HH:mm:ss
     *
     * @param timeStamp 时间戳
     */
    public static String getTime(long timeStamp) {
        String time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(timeStamp * 1000);
        String[] split = date.split("\\s");
        if (split.length > 1) {
            time = split[1];
        }
        return time;
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
     *
     * @param timeStamp
     */
    public static String convertTimeToFormat(long timeStamp) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - timeStamp;

        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月前";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年前";
        } else {
            return "刚刚";
        }
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，(多少分钟)
     *
     * @param timeStamp
     */
    public static String timeStampToFormat(long timeStamp) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - timeStamp;
        return time / 60 + "";
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(strDate, pos);
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     */
    public static String dateToStr(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(dateDate);
    }

    /**
     * 获取年份数组
     *
     * @param count 表示需要从当前年份往前一共取多少年
     */
    public static ArrayList<String> getYearStringArray(int count) {
        ArrayList<String> yearString = new ArrayList<>();
        //取筛选的年份
        Date date = new Date();
        for (int i = 0; i < count; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            yearString.add(sdf.format(yearAddNum(date, -i)));
        }
        return yearString;
    }

    public static Date yearAddNum(Date time, Integer num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.YEAR, num);
        Date newTime = calendar.getTime();
        return newTime;
    }
}
