package com.suncn.ihold_zxztc.activity;

import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import java.util.HashMap;


public class ConfirmLoginActivity extends BaseActivity {
    @BindView(id = R.id.btn_submit, click = true)
    private Button btnSubmit;
    @BindView(id = R.id.tv_no, click = true)
    private TextView tvCancel;
    private String checkCode;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_confirm_login);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new BaseActivity.requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                prgDialog.closePrgDialog();
                finish();
            }
        };
        initView();
    }

    public void initView() {
        checkCode = getIntent().getStringExtra("CheckCode");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_submit:
                confirmLogin();
                break;
            case R.id.tv_no:
                finish();
                break;
        }
    }

    private void confirmLogin() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        String sessionId = checkCode.split("sessionId=",checkCode.length())[1];
        GILogUtil.e(sessionId);
        textParamMap.put("sessionId", sessionId);
        doRequestNormal(ApiManager.getInstance().loginByAppSweepCode(textParamMap), 0);


    }
}
