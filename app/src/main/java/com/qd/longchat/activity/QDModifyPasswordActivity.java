package com.qd.longchat.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.qd.longchat.R;
import com.qd.longchat.util.QDUtil;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/25 上午9:51
 */
public class QDModifyPasswordActivity extends QDBaseActivity {

    @BindView(R2.id.view_modify_title)
    View viewTile;
    @BindView(R2.id.et_modify_old_pwd)
    EditText etOldPwd;
    @BindView(R2.id.et_modify_new_pwd)
    EditText etNewPwd;
    @BindView(R2.id.et_modify_confirm_pwd)
    EditText etConfirmPwd;

    @BindString(R2.string.title_sure)
    String strSure;
    @BindString(R2.string.modify_pwd_title)
    String strTitle;
    @BindString(R2.string.modify_pwd_empty_old)
    String strEmptyOld;
    @BindString(R2.string.modify_pwd_empty_new)
    String strEmptyNew;
    @BindString(R2.string.modify_pwd_empty_confirm)
    String strEmptyConfirm;
    @BindString(R2.string.modify_pwd_not_contain_chinese)
    String strNotContainChinese;
    @BindString(R2.string.modify_pwd_not_right)
    String strNotRight;
    @BindString(R2.string.modify_pwd_error_old)
    String strErrorOld;
    @BindString(R2.string.modify_pwd_success)
    String strSuccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ButterKnife.bind(this);

        initTitleView(viewTile);
        tvTitleName.setText(strTitle);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText(strSure);

        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = etOldPwd.getText().toString();
                String newPwd = etNewPwd.getText().toString();
                String confirmPwd = etConfirmPwd.getText().toString();

                if (TextUtils.isEmpty(oldPwd)) {
                    QDUtil.showToastCenter(context, strEmptyOld);
                    return;
                }
                if (TextUtils.isEmpty(newPwd)) {
                    QDUtil.showToastCenter(context, strEmptyNew);
                    return;
                }
                if (TextUtils.isEmpty(confirmPwd)) {
                    QDUtil.showToastCenter(context, strEmptyConfirm);
                    return;
                }
                if (QDUtil.isChinese(newPwd)) {
                    QDUtil.showToastCenter(context, strNotContainChinese);
                    return;
                }
                if (!newPwd.equals(confirmPwd)) {
                    QDUtil.showToastCenter(context, strNotRight);
                    return;
                }
                QDClient.getInstance().modifyPwd(oldPwd, newPwd, new QDResultCallBack() {
                    @Override
                    public void onError(String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                QDUtil.showToastCenter(context, strErrorOld);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(Object o) {
                    }
                });
            }
        });
    }
}
