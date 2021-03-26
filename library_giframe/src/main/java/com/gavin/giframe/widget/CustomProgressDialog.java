package com.gavin.giframe.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gavin.giframe.R;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.FadingCircle;

import skin.support.content.res.SkinCompatResources;

/**
 * 加载对话框
 */
public class CustomProgressDialog extends Dialog {
    //    private AnimationDrawable animationDrawable;
//    private ImageView icon_imageView;
    private Activity activity;
    private TextView msg_TextView;
    ProgressBar progressBar;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        activity = (Activity) context;
        this.setContentView(R.layout.view_progressdialog);
//        icon_imageView = (ImageView) findViewById(R.id.iv_icon);
        progressBar = findViewById(R.id.progress);
        msg_TextView = findViewById(R.id.tv_msg);

        FadingCircle doubleBounce = new FadingCircle();
        doubleBounce.setBounds(0, 0, 300, 300);
        doubleBounce.setColor(SkinCompatResources.getColor(activity,R.color.view_head_bg));
        progressBar.setIndeterminateDrawable(doubleBounce);
    }

    /**
     * 加载对话框
     */
    public void showLoadDialog() {
        showPrgDialog("载入中，请稍后…", true);
    }

    /**
     * 提交对话框
     */
    public void showSubmitDialog() {
        showPrgDialog("提交数据中，请稍后…", false);
    }

    /**
     * 检查更新对话框
     */
    public void showUpdateDialog() {
        showPrgDialog("检查更新中，请稍后…", false);
    }

    /**
     * 显示对话框
     */
    private void showPrgDialog(String msg, boolean cancelable) {
        msg_TextView.setText(msg);
        this.setCancelable(cancelable); // 是否可取消对话框（true可以取消）
        if (!activity.isFinishing()){
            this.show();
        }
    }

    /**
     * 关闭对话框
     */
    public void closePrgDialog() {
        this.cancel();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        icon_imageView.setBackgroundResource(R.drawable.progress_round);
//        animationDrawable = (AnimationDrawable) icon_imageView.getBackground();
//        animationDrawable.start();
//    }
}
