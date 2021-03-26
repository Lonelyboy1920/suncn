package com.gavin.giframe.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;

/**
 * Toast
 *
 * @author gavin
 * @date 2012-6-12 下午01:41:51
 */
public class GIToastUtil {
//    private static Handler handler = new Handler(Looper.getMainLooper());
//    private static Toast toast = null;
//    private static Object synObj = new Object();

    public static void showMessage(final Context act, final String msg) {
        showMessage(act, msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(final String msg) {
        showMessage(null, msg, Toast.LENGTH_SHORT);
    }

    private static void showMessage(final Context act, final String msg, final int len) {
        ToastUtils.cancel();
        new Handler().post(new Runnable() {
            public void run() {
                ToastUtils.show(msg);
            }
        });
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {//9.0以上toast直接用原生的方法即可，并不用setText防止重复的显示的问题
//            Toast toast = Toast.makeText(act, null, len);
//            toast.setText(msg);
//            toast.show();
////            Toast.makeText(act, msg, len).show();
//        } else {
//            new Thread(new Runnable() {
//                public void run() {
//                    handler.post(new Runnable() {
//                        public void run() {
//                            synchronized (synObj) {
//                                if (toast != null) {
//                                    // toast.cancel();
//                                    toast.setText(msg);
//                                    toast.setDuration(len);
//                                } else {
//                                    toast = Toast.makeText(act, msg, len);
//                                }
//                                toast.show();
//                            }
//                        }
//                    });
//                }
//            }).start();
//        }
    }
}
