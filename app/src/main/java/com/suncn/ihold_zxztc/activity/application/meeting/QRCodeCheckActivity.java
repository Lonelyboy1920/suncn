package com.suncn.ihold_zxztc.activity.application.meeting;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.google.gson.Gson;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.ConfirmLoginActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.QRInfoBean;

import java.util.HashMap;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * 二维码扫描页面
 */
public class QRCodeCheckActivity extends BaseActivity implements QRCodeView.Delegate {
    @BindView(id = R.id.qr_view)
    private ZXingView view_QRCodeView;
    private String strEventInfo;

    public static final String ACTION_OPEN_SCAN_QR_CODE = "com.suncn.ihold_zxztc.activity.application.meeting.action.DYNAMIC_OPEN";


    @Override
    protected void onStart() {
        super.onStart();
        view_QRCodeView.startCamera();
        view_QRCodeView.startSpot();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view_QRCodeView.onDestroy();
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_qrcode_check);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        setHeadTitle(getString(R.string.string_scan_to_check_in));

        HiPermission.create(activity).checkSinglePermission(Manifest.permission.CAMERA, new PermissionCallback() {
            @Override
            public void onClose() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {

            }

            @Override
            public void onGuarantee(String permission, int position) {
                view_QRCodeView.setDelegate(QRCodeCheckActivity.this);
                view_QRCodeView.startSpot();
            }
        });

    }

    private void doLogic(int what, Object object) {
        view_QRCodeView.stopCamera();
        view_QRCodeView.onDestroy();
        showToast(getString(R.string.string_sign_in_successfully));
        finish();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        //vibrate();//震动
        view_QRCodeView.stopSpot();
        GILogUtil.e(result);
        if (GIStringUtil.isNotBlank(result)) {
            strEventInfo = result;
            if (result.contains("EevetSign") || result.contains("MeetSign")) {
                getSignInfo();
            } else if (result.contains("appLogin")) {
                Bundle bundle = new Bundle();
                bundle.putString("CheckCode", result);
                skipActivity(activity, ConfirmLoginActivity.class, bundle);
            } else {
                showToast(getString(R.string.string_the_qr_code_is_temporarily_unable_to_provide_recognition));
            }
        } else {
            showToast(getString(R.string.string_scan_failed_please_retry_again));
            view_QRCodeView.startSpot();
        }
    }

    /**
     * 获取签到信息
     */
    private void getSignInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("msg", strEventInfo);
        doRequestNormal(ApiManager.getInstance().QRCheckServlet(textParamMap), 0);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }
}
