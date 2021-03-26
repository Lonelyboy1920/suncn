package com.suncn.ihold_zxztc.activity.my;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;

import java.util.HashMap;

/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity {
    @BindView(id = R.id.et_option)
    private GIEditText option_EditText;//意见EditText
    @BindView(id = R.id.et_tel)
    private EditText tel_EditText;//手机号码EditText
    @BindView(id = R.id.btn_submit, click = true)
    private Button submit_Button;//提交Button
    @BindView(id = R.id.tv_mike, click = true)
    private TextView mike_TextView; // 语音识别按钮

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("意见反馈");
        option_EditText.setTextView(findViewById(R.id.tv_count));
        tel_EditText.setText(GISharedPreUtil.getString(activity, "strMobile"));
        tel_EditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
    }

    /**
     * 请求结果
     */
    private void doLogic(Object object, int sign) {
        prgDialog.closePrgDialog();
        String msg = "";
        switch (sign) {
            case 0:
                try {
                    msg = "反馈成功";
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (GIStringUtil.isNotEmpty(msg))
            showToast(msg);
    }


    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_feedback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                checkInput();
                break;
            case R.id.tv_mike:
                MikeEditTextUtil util = new MikeEditTextUtil(activity, option_EditText);
                util.viewShow();
                //doMike();
                break;
            default:
                break;
        }
    }

    /**
     * 提交反馈
     */
    private void checkInput() {
        String option = option_EditText.getText().toString().trim();
        String tel = tel_EditText.getText().toString().trim();
        if (GIStringUtil.isEmpty(option)) {
            showToast("请输入反馈内容");
        } else if (GIStringUtil.isEmpty(tel)) {
            showToast("请输入联系方式");
        } else {
            doFeedBack(option, tel);
        }
    }

    /**
     * 意见反馈
     */
    private void doFeedBack(String option, String tel) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strContent", option);
        textParamMap.put("strInformation", tel);
        doRequestNormal(ApiManager.getInstance().feedBack(textParamMap), 0);
    }
}
