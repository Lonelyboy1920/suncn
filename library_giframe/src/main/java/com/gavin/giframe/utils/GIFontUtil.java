package com.gavin.giframe.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Description: 自定义APP字体
 * Created by: Administrator
 * 2019-3-28
 */
public class GIFontUtil {
    // fongUrl是自定义字体分类的名称
    private static String fongUrl = "SourceHanSerifCN-Regular.otf"; // 思源宋体
    private static String blokUrl = "SourceHanSerifCN-SemiBold.otf"; // 思源宋体粗体
    private static String iconfont = "iconfont.ttf";

    /**
     * 设置字体
     */
    public static Typeface setFont(Context context, String type) {
        //给它设置你传入的自定义字体文件，再返回回来
        //Typeface是字体，这里我们创建一个对象
        Typeface tf;
        // 不可用switch
        if ("3".equals(type)) { // 粗体
            tf = Typeface.createFromAsset(context.getAssets(), blokUrl);
        } else if ("2".equals(type)) { // 正常
            tf = Typeface.createFromAsset(context.getAssets(), fongUrl);
        } else { // 字体图片库
            tf = Typeface.createFromAsset(context.getAssets(), iconfont);
        }
        return tf;
    }

    /**
     * 设置默认字体
     */
    public static void setDefaultFont(Context context) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(), fongUrl);
        replaceFont(regular);
    }

    /**
     * 利用反射替换系统默认字体
     * 将MONOSPACE字体替换成我们自定义的字体
     */
    private static void replaceFont(final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField("MONOSPACE");
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
