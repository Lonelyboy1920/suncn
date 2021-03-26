package com.qd.longchat.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;


/**
 * 等待对话框
 * <p>
 * Created by xxw on 2016/9/21.
 */

public class QDWaitingDialog {

    private ProgressDialog dialog;
    private Handler handler;
    private Context context;

    public QDWaitingDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请稍等...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        handler = new Handler();
        this.context = context;
    }

    /**
     * 设置提示语
     *
     * @param tip
     */
    public void setTip(String tip) {
        if (TextUtils.isEmpty(tip)) {
            return;
        }
        dialog.setMessage(tip);
    }

    /**
     * 显示等待对话框
     */
    public void show() {
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置点击返回键是否关闭dialog
     * @param b
     */
    public void setCancelable(boolean b) {
        try {
            dialog.setCancelable(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消失等待对话框
     */
    public void dismiss() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    public void setOnCancelListener(final DialogInterface.OnCancelListener listener) {
        dialog.setOnCancelListener(listener);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listener.onCancel(dialog);
            }
        });
    }
}
