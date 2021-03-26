package com.gavin.giframe.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.text.Html;
import android.text.Spanned;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gavin.giframe.R;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * 通用工具类
 */
public final class GIUtil {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private GIUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     */
    public static void init(@NonNull final Context context) {
        GIUtil.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     */
    public static Context getContext() {
        if (context != null) {
            return context;
        }
        throw new NullPointerException("should be initialized in application");
    }

    /**
     * 关闭输入法
     */
    public static void closeSoftInput(Activity context) {
        IBinder binder = null;
        if (context.getCurrentFocus() != null) {
            binder = context.getCurrentFocus().getWindowToken();
            if (binder != null)
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示输入法
     */
    public static void showSoftInput(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(1000, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * listView兼容scrollview设置
     */
//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//
//        int totalHeight = 0;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//    }

    /**
     * 设置桌面Icon角标
     */
    public static void sendBadgeNumber(Context context, int icon, int number) {
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(context, icon, number);
        } else {
            ShortcutBadger.applyCount(context, number); //for 1.1.4+
        }
    }

    /**
     * 小米手机miui6以上版本需要使用通知发送
     */
    private static void sendToXiaoMi(Context context, int icon, int number) {
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = null;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("您有" + number + "个待处理事项");
            builder.setTicker("您有" + number + "个待处理事项");
            builder.setAutoCancel(true);
            builder.setSmallIcon(icon);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, number);// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);
            field.set(notification, miuiNotification);
            //miui6以上版本需要使用通知发送
            nm.notify(101010, notification);

//            Intent intent = new Intent(activity, BadgeIntentService.class);
//            intent.putExtra("badgeCount", number);
//            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            //miui 6之前的版本
            ShortcutBadger.applyCount(context, number); //for 1.1.4+
        }
    }

    /*
     * 屏蔽安卓9.0系统出现弹窗
     */
    public static void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取渠道名
     *
     * @return 如果没有获取成功，那么返回值为空
     */
    public static String getChannelName(Context context) {
        if (context == null) {
            return null;
        }
        String channelName = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = String.valueOf(applicationInfo.metaData.get("MTA_CHANNEL"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelName;
    }

    /**
     * 展示带样式的文字
     */
    public static Spanned showHtmlInfo(String str) {
        return showHtmlInfo(str, null);
    }

    /**
     * 展示带图片的文字
     */
    public static Spanned showHtmlInfo(String str, TextView textView) {
        if (GIStringUtil.isNotBlank(str)) {
            str = formatStringToHtml(str);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                if (textView == null) {
                    return Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY);
                } else {
                    return Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY, new MyImageGetter(textView), null);
                }
            } else {
                if (textView == null) {
                    return Html.fromHtml(str);
                } else {
                    return Html.fromHtml(str, new MyImageGetter(textView), null);
                }
            }
        }
        return Html.fromHtml("");
    }

    static class MyImageGetter implements Html.ImageGetter {
        private URLDrawable urlDrawable = null;
        private TextView textView;

        MyImageGetter(TextView textView) {
            this.textView = textView;
        }

        @Override
        public Drawable getDrawable(final String source) {
            urlDrawable = new URLDrawable();
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = Glide.with(context)
                                .asBitmap()
                                .load(source)
                                .submit().get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap resource) {
                    if (resource==null){
                        resource=DrawableToBitmap(context.getResources().getDrawable(R.mipmap.img_error));
                    }
                    urlDrawable.bitmap = resource;
                    urlDrawable.setBounds(0, 0, resource.getWidth(), resource.getHeight());
                    textView.setText(textView.getText());//不加这句显示不出来图片，原因不详
                }
            }.execute();
            return urlDrawable;
        }
    }

    static class URLDrawable extends BitmapDrawable {
        public Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }

    public static Bitmap DrawableToBitmap(Drawable drawable) {

        // 获取 drawable 长宽
        int width = drawable.getIntrinsicWidth();
        int heigh = drawable.getIntrinsicHeight();

        drawable.setBounds(0, 0, width, heigh);

        // 获取drawable的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 创建bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, heigh, config);
        // 创建bitmap画布
        Canvas canvas = new Canvas(bitmap);
        // 将drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 解决服务器端保存数据后的转码问题（转成html格式）
     */
    public static String formatStringToHtml(String s) {
        if (s == null)
            return null;
        s = s.replaceAll("，", ",");
        s = s.replaceAll("＼n", "\n");
        s = s.replaceAll("＼r", "\r");
        s = s.replaceAll("\r\n", "<br />");
        s = s.replaceAll("\r", "&nbsp;");
        s = s.replaceAll("\n", "<br />");
        s = s.replaceAll("＜", "<");
        s = s.replaceAll("＞", ">");
        s = s.replaceAll("＂", "\"");
        s = s.replaceAll("＇", "'");
        s = s.replaceAll("％", "%");
        return s;
    }

    /**
     * 获取 Application
     */
    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }
}
