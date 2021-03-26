package com.qd.longchat.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.qd.longchat.R;
import com.qd.longchat.util.QDUtil;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/2/26 上午11:23
 */
public class QDScanCodeLoginActivity extends QDBaseActivity {

    @BindView(R2.id.view_title)
    View viewTitle;
    @BindView(R2.id.btn_login)
    Button btnLogin;
    @BindView(R2.id.tv_cancel_login)
    TextView tvCancel;

    @BindString(R2.string.sweep_login_error)
    String strLoginError;

    private String pwd;
    private String guid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_login);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleBack.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        tvTitleBack.setText(R.string.title_close);
        pwd = getIntent().getStringExtra("password");
        guid = getIntent().getStringExtra("guid");
    }

    @OnClick({R2.id.btn_login, R2.id.tv_cancel_login})
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {
            QDClient.getInstance().loginToPC(pwd, guid, new QDResultCallBack() {
                @Override
                public void onError(String errorMsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            QDUtil.showToast(context, strLoginError);
                        }
                    });
                }

                @Override
                public void onSuccess(Object o) {
                    finish();
                }
            });
        } else {
            finish();
        }
    }
}
