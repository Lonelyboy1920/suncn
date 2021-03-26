package com.gavin.giframe.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;

/**
 * 对话框
 *
 * @author gavin
 * @date 2012-6-12 下午01:36:44
 */
public class GIDialogUtil {
    private Context context;
    private ProgressDialog prgDialog;

    private int show;
    private int close;
    private int timeout = 30 * 1000;

    public GIDialogUtil(Context context) {
        this.context = context;
    }

    /**
     * 加载对话框
     */
    public void showLoadDialog() {
        showPrgDialog(null, "载入中，请稍后…", false);
    }

    /**
     * 提交对话框
     */
    public void showSubmitDialog() {
        showPrgDialog(null, "提交数据中，请稍后…", false);
    }

    /**
     * 检查更新对话框
     */
    public void showUpdateDialog() {
        showPrgDialog(null, "检查更新中，请稍后…", false);
    }

    /**
     * 自定义信息对话框
     */
    public void showMsgDialog(String msg) {
        showPrgDialog(null, msg, false);
    }

    /**
     * 显示对话框
     */
    public void showPrgDialog(String title, String message, boolean cancelable) {
        if (prgDialog != null && prgDialog.isShowing()) {
            return;
        }
        prgDialog = ProgressDialog.show(context, title, message);
        prgDialog.setCancelable(true); // 是否可取消对话框（true可以取消）
        prgDialog.setIndeterminate(true); // 是否不明确具体进度（true不明确）
        prgDialog.show();
        prgDialog.isShowing();
        new Thread() {
            public void run() {
                Looper.prepare();
                try {
                    Thread.sleep(timeout);
                    if (show != close) {
                        if (prgDialog.isShowing()) {
                            closePrgDialog();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        show++;
    }

    /**
     * 判断对话框是否已显示
     */
    public boolean isShow() {
        return prgDialog != null && prgDialog.isShowing();
    }

    /**
     * 关闭对话框
     */
    public void closePrgDialog() {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.cancel();
            close++;
        }
    }
}
