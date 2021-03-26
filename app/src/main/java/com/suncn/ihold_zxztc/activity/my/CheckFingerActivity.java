package com.suncn.ihold_zxztc.activity.my;

import android.content.DialogInterface;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.FingerprintUtil;

/**
 * 指纹识别验证页面
 */
public class CheckFingerActivity extends BaseActivity {
    @BindView(id = R.id.tv_username)
    private TextView username_TextView;//用户名
    @BindView(id = R.id.tv_fingerprint, click = true)
    private TextView fingerprint_TextView; // 指纹识别TextView
    private AlertDialog dialog;

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("身份验证");
        username_TextView.setText(GISharedPreUtil.getString(activity, "strName"));
        initFingerprint();
        setResult(RESULT_OK);
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_check_finger);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_fingerprint:
                initFingerprint();
                break;
            default:
                break;
        }
    }

    private void initFingerprint() {
        FingerprintUtil.callFingerPrint(activity, new FingerprintUtil.OnCallBackListenr() {
            private View view;
            private TextView finger_TextView;
            private TextView zwsb_TextView;
            private TextView title_TextView;

            @Override
            public void onSupportFailed() { // 当前设备不支持指纹
                GISharedPreUtil.setValue(activity, "isSupportFingerprint", false);
                GILogUtil.i("当前设备不支持指纹");
            }

            @Override
            public void onInsecurity() { // 当前设备未处于安全保护中
                showToast("请先录入指纹");
            }

            @Override
            public void onEnrollFailed() { //未设置指纹
                showToast("请先录入指纹");
                GILogUtil.i("未设置指纹");
            }

            @Override
            public void onAuthenticationStart() { // 准备识别
                GILogUtil.i("准备识别");
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckFingerActivity.this);
                view = LayoutInflater.from(CheckFingerActivity.this).inflate(R.layout.dialog_finger, null);
                title_TextView = view.findViewById(R.id.tv_title);
                title_TextView.setText(getString(R.string.app_name) + "验证");
                finger_TextView = view.findViewById(R.id.tv_finger);
                zwsb_TextView = view.findViewById(R.id.tv_zwsb);
                builder.setView(view);
                builder.setCancelable(false);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FingerprintUtil.cancel();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) { // 识别出现错误
                showToast(errString.toString());
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                showToast("指纹验证失败，请重试！");
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                GILogUtil.i("帮助");
                showToast(helpString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                if (dialog.isShowing()) {
                    zwsb_TextView.setTextColor(getResources().getColor(R.color.view_head_bg));
                    GISharedPreUtil.setValue(activity, "isOpenFingerLogin_" + GISharedPreUtil.getString(activity, "strUserId"), true);
                    showToast("指纹登录功能已开启！");
                    dialog.dismiss();
                    finish();
                }
            }
        });
    }
}
