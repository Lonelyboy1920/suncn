package com.qd.longchat.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.util.QDDateUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liuli on 2015/11/27.
 * <p/>
 * updated by skynight on 2016/08/07
 */
public class QDTimeSelector {
    public interface ResultHandler {
        void handle(String time);
    }

    public enum FORMAT {
        DATE_TIME("yyyy-MM-dd HH:mm"),

        DATE("yyyy-MM-dd"),

        TIME("HH:mm");

        public String value;

        FORMAT(String value) {
            this.value = value;
        }
    }

    public final static String START_DATE = "1900-01-01 00:00";
    public final static String END_DATE = "2099-12-31 23:59";

    private ResultHandler handler;
    private Context context;
    private DecimalFormat numFormat = new DecimalFormat("#00");
    private FORMAT format;

    private Dialog selectorDialog;
    private QDPickerView year_pv;
    private QDPickerView month_pv;
    private QDPickerView day_pv;
    private QDPickerView hour_pv;
    private QDPickerView minute_pv;

    private final int MAX_MINUTE = 59;
    private final int MAX_HOUR = 23;
    private final static int MIN_MINUTE = 0;
    private final static int MIN_HOUR = 0;
    private final int MAX_MONTH = 12;

    private ArrayList<String> year, month, day, hour, minute;
    private int startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute;
    private boolean spanYear, spanMon, spanDay, spanHour, spanMin;
    private Calendar selectedCalender = Calendar.getInstance();
    private final static long ANIMATOR_DELAY = 200L;
    private final static long CHANGE_DELAY = 90L;
    private boolean isInit = false;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private Date userDate;

    /**
     * @param context       上下文
     * @param resultHandler 选取时间后回调
     * @param format        时间格式
     */
    public QDTimeSelector(Context context, FORMAT format, ResultHandler resultHandler, Date start, Date end) {
        this.context = context;
        this.handler = resultHandler;
        this.format = format;
        initScreen(context);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        endCalendar.setTime(end);
        initDialog();
        initView();
    }

    public void show() {
        initParameter();
        initTimer();
        addListener();
        selectorDialog.show();
    }

    private void initDialog() {
        if (selectorDialog == null) {
            selectorDialog = new Dialog(context, R.style.time_dialog);
            selectorDialog.setCancelable(true);
            selectorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            selectorDialog.setContentView(R.layout.im_date_time_selector);
            Window window = selectorDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = width;
            window.setAttributes(lp);
        }
    }

    private void initView() {

        year_pv = (QDPickerView) selectorDialog.findViewById(R.id.year_pv);
        month_pv = (QDPickerView) selectorDialog.findViewById(R.id.month_pv);
        day_pv = (QDPickerView) selectorDialog.findViewById(R.id.day_pv);
        hour_pv = (QDPickerView) selectorDialog.findViewById(R.id.hour_pv);
        minute_pv = (QDPickerView) selectorDialog.findViewById(R.id.minute_pv);
        TextView tv_cancel = (TextView) selectorDialog.findViewById(R.id.tv_cancel);
        TextView tv_select = (TextView) selectorDialog.findViewById(R.id.tv_select);

        if (format == FORMAT.DATE) {
            hour_pv.setVisibility(View.GONE);
            minute_pv.setVisibility(View.GONE);
            selectorDialog.findViewById(R.id.tv_hour).setVisibility(View.GONE);
            selectorDialog.findViewById(R.id.tv_minute).setVisibility(View.GONE);
        } else if (format == FORMAT.TIME) {
            year_pv.setVisibility(View.GONE);
            month_pv.setVisibility(View.GONE);
            day_pv.setVisibility(View.GONE);
            selectorDialog.findViewById(R.id.tv_year).setVisibility(View.GONE);
            selectorDialog.findViewById(R.id.tv_month).setVisibility(View.GONE);
            selectorDialog.findViewById(R.id.tv_day).setVisibility(View.GONE);
        }
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorDialog.dismiss();
            }
        });
        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.handle(dateToString(selectedCalender.getTime(), format.value));
                setData(QDDateUtil.dateToString(selectedCalender.getTime(),format.value));
                selectorDialog.dismiss();
            }
        });

    }

    private void initParameter() {
        startYear = startCalendar.get(Calendar.YEAR);
        startMonth = startCalendar.get(Calendar.MONTH) + 1;
        startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        startMinute = startCalendar.get(Calendar.MINUTE);
        endYear = endCalendar.get(Calendar.YEAR);
        endMonth = endCalendar.get(Calendar.MONTH) + 1;
        endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        endMinute = endCalendar.get(Calendar.MINUTE);
        spanYear = startYear != endYear;
        spanMon = (!spanYear) && (startMonth != endMonth);
        spanDay = (!spanMon) && (startDay != endDay);
        spanHour = (!spanDay) && (startHour != endHour);
        spanMin = (!spanHour) && (startMinute != endMinute);
        if (userDate != null){
            selectedCalender.setTime(userDate);}
        else{
            selectedCalender.setTime(new Date());
        }
    }

    private void initTimer() {
        initArrayList();

        if (spanYear) {
            for (int i = startYear; i <= endYear; i++) {
                year.add(String.valueOf(i));
            }
            for (int i = startMonth; i <= MAX_MONTH; i++) {
                month.add(numFormat.format(i));
            }
            for (int i = startDay; i <= startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(numFormat.format(i));
            }
            for (int i = startHour; i <= MAX_HOUR; i++) {
                hour.add(numFormat.format(i));
            }
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(numFormat.format(i));
            }
        } else if (spanMon) {
            year.add(String.valueOf(startYear));
            for (int i = startMonth; i <= endMonth; i++) {
                month.add(numFormat.format(i));
            }
            for (int i = startDay; i <= startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(numFormat.format(i));
            }
            for (int i = startHour; i <= MAX_HOUR; i++) {
                hour.add(numFormat.format(i));
            }
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(numFormat.format(i));
            }
        } else if (spanDay) {
            year.add(String.valueOf(startYear));
            month.add(numFormat.format(startMonth));
            for (int i = startDay; i <= endDay; i++) {
                day.add(numFormat.format(i));
            }
            for (int i = startHour; i <= MAX_HOUR; i++) {
                hour.add(numFormat.format(i));
            }
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(numFormat.format(i));
            }

        } else if (spanHour) {
            year.add(String.valueOf(startYear));
            month.add(numFormat.format(startMonth));
            day.add(numFormat.format(startDay));
            for (int i = startHour; i <= endHour; i++) {
                hour.add(numFormat.format(i));
            }
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(numFormat.format(i));
            }

        } else if (spanMin) {
            year.add(String.valueOf(startYear));
            month.add(numFormat.format(startMonth));
            day.add(numFormat.format(startDay));
            hour.add(numFormat.format(startHour));
            for (int i = startMinute; i <= endMinute; i++) {
                minute.add(numFormat.format(i));
            }
        }

        loadComponent();

    }

    private void initArrayList() {
        if (year == null) year = new ArrayList<>();
        if (month == null) month = new ArrayList<>();
        if (day == null) day = new ArrayList<>();
        if (hour == null) hour = new ArrayList<>();
        if (minute == null) minute = new ArrayList<>();
        year.clear();
        month.clear();
        day.clear();
        hour.clear();
        minute.clear();
    }

    private void addListener() {
        year_pv.setOnSelectListener(new QDPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.YEAR, Integer.parseInt(text));
                monthChange(numFormat.format(selectedCalender.get(Calendar.MONTH) + 1));
            }
        });
        month_pv.setOnSelectListener(new QDPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.MONTH, Integer.parseInt(text) - 1);
                dayChange(numFormat.format(selectedCalender.get(Calendar.DAY_OF_MONTH)));
            }
        });
        day_pv.setOnSelectListener(new QDPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text));
                hourChange(numFormat.format(selectedCalender.get(Calendar.HOUR_OF_DAY)));
            }
        });
        hour_pv.setOnSelectListener(new QDPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(text));
                minuteChange(numFormat.format(selectedCalender.get(Calendar.MINUTE)));
            }
        });
        minute_pv.setOnSelectListener(new QDPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.MINUTE, Integer.parseInt(text));
            }
        });

    }

    private void loadComponent() {
        year_pv.setData(year);
        month_pv.setData(month);
        day_pv.setData(day);
        hour_pv.setData(hour);
        minute_pv.setData(minute);
        isInit = false;
        year_pv.setSelected(numFormat.format(selectedCalender.get(Calendar.YEAR)));
        monthChange(numFormat.format(selectedCalender.get(Calendar.MONTH) + 1));
        dayChange(numFormat.format(selectedCalender.get(Calendar.DAY_OF_MONTH)));
        hourChange(numFormat.format(selectedCalender.get(Calendar.HOUR_OF_DAY)));
        minuteChange(numFormat.format(selectedCalender.get(Calendar.MINUTE)));
        isInit = true;
    }

    private void monthChange(String picked) {
        month.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        if (selectedYear == startYear) {
            for (int i = startMonth; i <= MAX_MONTH; i++) {
                month.add(numFormat.format(i));
            }
        } else if (selectedYear == endYear) {
            for (int i = 1; i <= endMonth; i++) {
                month.add(numFormat.format(i));
            }
        } else {
            for (int i = 1; i <= MAX_MONTH; i++) {
                month.add(numFormat.format(i));
            }
        }
        int index = month.indexOf(picked);
        index = index == -1 ? 0 : index;
        selectedCalender.set(Calendar.MONTH, Integer.parseInt(month.get(index)) - 1);
        month_pv.setData(month);
        month_pv.setSelected(index);
        if (isInit) {
            executeAnimator(month_pv);
            month_pv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dayChange(numFormat.format(selectedCalender.get(Calendar.DAY_OF_MONTH)));
                }
            }, CHANGE_DELAY);
        }

    }

    private void dayChange(String picked) {
        day.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (int i = startDay; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(numFormat.format(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (int i = 1; i <= endDay; i++) {
                day.add(numFormat.format(i));
            }
        } else {
            for (int i = 1; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(numFormat.format(i));
            }
        }
        int index = day.indexOf(picked);
        index = index == -1 ? 0 : index;
        selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(index)));
        day_pv.setData(day);
        day_pv.setSelected(index);
        if (isInit) {
            executeAnimator(day_pv);
            day_pv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hourChange(numFormat.format(selectedCalender.get(Calendar.HOUR_OF_DAY)));
                }
            }, CHANGE_DELAY);
        }
    }

    private void hourChange(String picked) {
        hour.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);

        if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay) {
            for (int i = startHour; i <= MAX_HOUR; i++) {
                hour.add(numFormat.format(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay) {
            for (int i = MIN_HOUR; i <= endHour; i++) {
                hour.add(numFormat.format(i));
            }
        } else {

            for (int i = MIN_HOUR; i <= MAX_HOUR; i++) {
                hour.add(numFormat.format(i));
            }

        }
        int index = hour.indexOf(picked);
        index = index == -1 ? 0 : index;

        selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.get(index)));
        hour_pv.setData(hour);
        hour_pv.setSelected(index);
        if (isInit) {
            executeAnimator(hour_pv);
            hour_pv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    minuteChange(numFormat.format(selectedCalender.get(Calendar.MINUTE)));
                }
            }, CHANGE_DELAY);
        }
    }

    private void minuteChange(String picked) {
        minute.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
        int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);

        if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour) {
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(numFormat.format(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour) {
            for (int i = MIN_MINUTE; i <= endMinute; i++) {
                minute.add(numFormat.format(i));
            }
        } else {
            for (int i = MIN_MINUTE; i <= MAX_MINUTE; i++) {
                minute.add(numFormat.format(i));
            }
        }

        int index = minute.indexOf(picked);
        index = index == -1 ? 0 : index;

        selectedCalender.set(Calendar.MINUTE, Integer.parseInt(minute.get(index)));
        minute_pv.setData(minute);
        minute_pv.setSelected(index);
        if (isInit) {
            executeAnimator(minute_pv);
        }
    }

    private void executeAnimator(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f,
                0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(ANIMATOR_DELAY).start();
    }

    /**
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return 日期
     */

    public static Date StringToDate(String strDate, String pattern) {

        if (isEmpty(strDate)) {
            return null;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
            return df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param date    日期
     * @param pattern 日期格式
     * @return 返回格式化后的字符串
     */

    public static String dateToString(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
            returnValue = df.format(date);
        }
        return returnValue;
    }

    public static int height;
    public static int width;

    private void initScreen(Context context) {
        this.context = context;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
    }

    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || "null".equalsIgnoreCase(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public void setData(String data){
        userDate = StringToDate(data, FORMAT.DATE.value);
    }
}
