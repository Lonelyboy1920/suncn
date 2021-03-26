package com.gavin.giframe.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GIPhoneUtils {
    public static String getDiskCacheDir(Context context, String dirName) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }
        if (cachePath == null) {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                cachePath = cacheDir.getPath();
            }
        }
        return cachePath + File.separator + dirName;
    }

    /**
     * 获取本机语言
     */
    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String languageCode = locale.getLanguage();
        if (TextUtils.isEmpty(languageCode)) {
            languageCode = "";
        }
        return languageCode;
    }

    /**
     * 获取城市编号
     */
    public static String getCountry() {
        Locale locale = Locale.getDefault();
        String countryCode = locale.getCountry();
        if (TextUtils.isEmpty(countryCode)) {
            countryCode = "";
        }
        return countryCode;
    }

    /**
     * 获取该app的versionName
     */
    public static String getAppVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取该app的versionCode
     */
    public static int getAppVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取唯一设备号
     */
    public static String getSerialNumber(Context context) {
        String serial;
        serial = getMD5(getMacFromHardware(context));
        return serial;
    }

    /**
     * MD5加密成32位字符串
     */
    private static String getMD5(String info) {
        try {
            if (info==null){
                return "";
            }
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes(StandardCharsets.UTF_8));
            byte[] encryption = md5.digest();
            StringBuilder strBuf = new StringBuilder();
            for (byte anEncryption : encryption) {
                if (Integer.toHexString(0xff & anEncryption).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & anEncryption));
                } else {
                    strBuf.append(Integer.toHexString(0xff & anEncryption));
                }
            }
            return strBuf.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            return info;
        }
    }

    /**
     * 获取MAC地址
     */
    private static String getMacFromHardware(Context context) {
        String macAddress = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//5.0以下
            macAddress = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            macAddress = getMacAddress();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            macAddress = getMacFromHardware();
        }
        if (macAddress != null) {
            macAddress = macAddress.replaceAll(":", "");
            if (!macAddress.equalsIgnoreCase("020000000000")) {
                return macAddress;
            }
        }
        return null;
    }

    /**
     * 6.0系统以下
     */
    private static String getMacDefault(Context context) {
        String mac = null;
        if (context == null) {
            return mac;
        }
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     */
    private static String getMacAddress() {
        String WifiAddress = null;
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 7.0 系统以上
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isHuawei() {
        return Build.BRAND.contains("huawei") || Build.BRAND.contains("honor");
    }

    public static boolean isXiaomi() {
        return Build.MANUFACTURER.toLowerCase().equals("xiaomi");
    }

    public static boolean isVivo() {
        return Build.BRAND.contains("vivo") || Build.BRAND.contains("bbk");
    }

    public static boolean isOppo() {
        return Build.BRAND.contains("oppo");
    }

    public static boolean isOneplus() {
        return Build.BRAND.contains("oneplus");
    }

    public static boolean isSamsung() {
        return Build.BRAND.contains("samsung");
    }

    /**
     * 判断APP是否已安装
     */
    public static boolean isAppInstalled(Context context, String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationInfo(pkgName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
