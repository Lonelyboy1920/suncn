package com.qd.longchat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.SkinAppCompatDelegateImpl;

import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDLoginOutCallBack;
import com.longchat.base.manager.listener.QDLoginOutCallBackManager;
import com.longchat.base.util.QDCmdCode;
import com.qd.longchat.R;
import com.qd.longchat.application.QDApplication;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDWaitingDialog;

import java.util.Date;

import skin.support.content.res.SkinCompatResources;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/16 下午6:44
 */

public class QDBaseActivity extends AppCompatActivity implements QDLoginOutCallBack {

    protected Context context;
    protected Activity activity;

    protected TextView tvTitleBack;
    protected TextView tvTitleName;
    protected TextView tvTitleRight;
    protected TextView tvTitleClose;
    protected TextView tvTitleSunname;
    protected TextView tvTitleFun;

    protected RelativeLayout rlLayout;
    protected TextView tvSearch;

    private QDWaitingDialog waitingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        checkAppStatus();
        Window window = getWindow();
        activity=this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;

       // QDLoginOutCallBackManager.getInstance().addCallBack(this);
    }

//    private void checkAppStatus() {
//        if(QDAppStatusManager.getInstance().getAppStatus() == QDAppStatusManager.AppStatusConstant.APP_FORCE_KILLED) {
//            //该应用已被回收，执行相关操作（下面有详解）
//            Intent intent = new Intent(this, QDSplashActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent intent = new Intent();
//        intent.setAction("com.qd.longchat.notify");
//        sendBroadcast(intent);
    }

    protected void initTitleView(View view) {
        tvTitleBack = view.findViewById(R.id.tv_title_back);
        tvTitleName = view.findViewById(R.id.tv_title_name);
        tvTitleRight = view.findViewById(R.id.tv_title_right);
        tvTitleClose = view.findViewById(R.id.tv_title_close);
        tvTitleSunname = view.findViewById(R.id.tv_title_subname);
        tvTitleFun = view.findViewById(R.id.tv_title_fun);

        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvTitleBack.setVisibility(View.VISIBLE);
        tvTitleClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void initSearchView(View view) {
        rlLayout = view.findViewById(R.id.rl_search_layout);
        tvSearch = view.findViewById(R.id.tv_search_hint);
    }

    protected QDWaitingDialog getWarningDailog() {
        if (waitingDialog == null) {
            waitingDialog = new QDWaitingDialog(this);
        }
        return waitingDialog;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDLoginOutCallBackManager.getInstance().removeLast();
    }

    @Override
    public void onLoginOut() {
        if (((QDApplication) getApplication()).getRefCount() == 0) {
            return;
        }
       // loginOut();
    }

    @Override
    public void onLoginOutFailed(int errorCode, String errorMsg) {
        QDUtil.showToast(context, context.getResources().getString(R.string.login_out_error) + errorMsg);

    }

    @Override
    public void onForceOut(int code) {
        if (code == QDCmdCode.CMD_LOUT) {
            QDUtil.showToast(context, context.getResources().getString(R.string.your_account_was_kicked_offline_by_admin));
        } else if (code == QDCmdCode.CMD_LBOUT) {
            QDUtil.showToast(context, context.getResources().getString(R.string.your_account_was_kicked_offline_reload));
        } else {
            String time = QDDateUtil.dateToString(new Date(), QDDateUtil.MSG_FORMAT1);
            QDUtil.showToast(context, context.getResources().getString(R.string.your_account) + time + context.getResources().getString(R.string.landing_on_another_device));
        }
       // loginOut();
    }

    @Override
    public void onModifyPwd() {
        QDUtil.showToast(context, context.getResources().getString(R.string.pwd_modify_reload));
        QDClient.getInstance().loginOut();
    }

//    private void loginOut() {
//        QDLanderInfo.getInstance().loginOut();
//        QDBitmapUtil.getInstance().logOut();
//        Intent intent = new Intent(context, QDLoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//        ((Activity) context).finish();
//    }

    /**
     * 隐藏输入法
     */
    protected void HideSoftInput() {
        if (getCurrentFocus() != null) {
            IBinder token = getCurrentFocus().getWindowToken();
            if (token != null) {
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    protected void setStatusBar(boolean useStatusBarDarkColor, boolean useThemeStatusBarColor) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (useThemeStatusBarColor) {
            getWindow().setStatusBarColor(SkinCompatResources.getColor(activity,R.color.view_head_bg));
        } else {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarDarkColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return SkinAppCompatDelegateImpl.get(this, this);
    }
}
