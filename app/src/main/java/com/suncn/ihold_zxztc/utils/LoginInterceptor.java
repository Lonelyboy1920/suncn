package com.suncn.ihold_zxztc.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gavin.giframe.utils.GISharedPreUtil;
import com.suncn.ihold_zxztc.activity.LoginActivity;

/**
 * 判断登陆类，用户处理登录成功后回到之前想进入的界面
 */
public class LoginInterceptor {
    public static final String mINVOKER = "INTERCEPTOR_INVOKER";

    /**
     * 判断处理
     *
     * @param ctx    当前activity的上下文
     * @param target 目标activity的target
     * @param bundle 目标activity所需要的参数
     * @param intent 目标activity
     */
    private static void interceptor(Context ctx, String target, Bundle bundle, Intent intent) {
        if (target != null && target.length() > 0) {
            LoginCarrier invoker = new LoginCarrier(target, bundle);
            if (getLogin(ctx)) {
                invoker.invoke(ctx);
            } else {
                if (intent == null) {
                    intent = new Intent(ctx, LoginActivity.class);
                }
                login(ctx, invoker, intent);
            }
        } else {
            Toast.makeText(ctx, "界面跳转异常", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 登录判断
     *
     * @param ctx    当前activity的上下文
     * @param target 目标activity的target（target需跟AndroidManifest中对应Activity里的action android:name保持一致）
     * @param bundle 目标activity所需要的参数
     */
    public static void interceptor(Context ctx, String target, Bundle bundle) {
        interceptor(ctx, ctx.getApplicationInfo().packageName + "." + target, bundle, null);
    }

    // 这里获取登录状态，具体获取方法看项目具体的判断方法
    private static boolean getLogin(Context cts) {
        return GISharedPreUtil.getBoolean(cts, "isHasLogin", false);
    }

    private static void login(Context context, LoginCarrier invoker, Intent intent) {
        intent.putExtra(mINVOKER, invoker);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
