package com.gavin.giframe.utils;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GISharedPreUtil {
    private static SharedPreferences sp;
    private final static String SharePreferncesName = "SP_SETTING";

    /**
     * @param context
     * @param key
     * @param value
     * @return 是否保存成功
     */
    public static boolean setValue(Context context, String key, Object value) {
        sp = getSharedPreferences(context);
        Editor edit = sp.edit();
        if (value == null) {
            return edit.remove(key).commit();
        } else if (value instanceof String) {
            return edit.putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            return edit.putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Float) {
            return edit.putFloat(key, (Float) value).commit();
        } else if (value instanceof Integer) {
            return edit.putInt(key, (Integer) value).commit();
        } else if (value instanceof Long) {
            return edit.putLong(key, (Long) value).commit();
        } else if (value instanceof Set) {
            new IllegalArgumentException("Value can not be Set object!");
            return false;
        }
        return false;
    }

    public static boolean getBoolean(Context context, String key) {
        sp = getSharedPreferences(context);
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean isDefault) {
        sp = getSharedPreferences(context);
        return sp.getBoolean(key, isDefault);
    }

    public static String getString(Context context, String key) {
        sp = getSharedPreferences(context);
        return sp.getString(key, "");
    }

    public static String getString(Context context, String key, String defValue) {
        sp = getSharedPreferences(context);
        return sp.getString(key, defValue);
    }

    public static Float getFloat(Context context, String key) {
        sp = getSharedPreferences(context);
        return sp.getFloat(key, 0f);
    }

    public static int getInt(Context context, String key) {
        sp = getSharedPreferences(context);
        return sp.getInt(key, 0);
    }

    public static long getLong(Context context, String key) {
        sp = getSharedPreferences(context);
        return sp.getLong(key, 0);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(SharePreferncesName, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * 清除SP_SETTING中存储的数据
     */
    public static void clearData(Context context) {
        sp = getSharedPreferences(context);
        sp.edit().clear().apply();
    }
}
