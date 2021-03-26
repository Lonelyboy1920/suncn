package com.suncn.ihold_zxztc.activity.application.meeting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.SignConfirmBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

/**
 * 扫码确认
 */
public class SignConfirmActivity extends BaseActivity {
    @BindView(id = R.id.iv_person_head)
    private RoundImageView head_ImageView; // 委员头像
    @BindView(id = R.id.tv_user)
    private TextView user_TextView;//用户姓名
    @BindView(id = R.id.tv_duty)
    private TextView duty_TextView;//职务TextView
    @BindView(id = R.id.tv_faction)
    private TextView faction_TextView;//党派TextView
    @BindView(id = R.id.tv_sector)
    private TextView sector_TextView;//界别TextView
    @BindView(id = R.id.tv_name_tag)
    private TextView nameTag_TextView;//会议（活动）名称tag
    @BindView(id = R.id.tv_date_tag)
    private TextView dateTag_TextView;//会议（活动）时间tag
    @BindView(id = R.id.tv_date)
    private TextView date_TextView;//会议（活动）时间
    @BindView(id = R.id.tv_name)
    private TextView name_TextView;//名称TextView
    @BindView(id = R.id.tv_place_tag)
    private TextView placeTag_TextView;//会议（活动）地点tag
    @BindView(id = R.id.tv_place)
    private TextView place_TextView;//地点TextView
    @BindView(id = R.id.btn_confirm, click = true)
    private Button confirm_Button;//确认信息Button
    private String strEventInfo;
    private String strAttendId = "";
    private String strType = "";//0会议，1活动

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_sign_confirm);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_sign_in_confirmation));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strEventInfo = bundle.getString("strEventInfo");
            getSignInfo();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_confirm:
                doSignConfirm();
                break;
        }
    }

    /**
     * 签到确认
     */
    private void doSignConfirm() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strAttendId", strAttendId);
        textParamMap.put("strType", strType);
        doRequestNormal(ApiManager.getInstance().dealQRSignConfirm(textParamMap), 0);
    }

    /**
     * 获取签到信息
     */
    private void getSignInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strQRInfo", strEventInfo);
        doRequestNormal(ApiManager.getInstance().getQRSignInfo(textParamMap), 1);
    }

    /**
     * 请求结果
     *
     * @param what
     * @param obj
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    showToast(getString(R.string.string_sign_in_successfully));
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 1:
                prgDialog.closePrgDialog();
                try {
                    SignConfirmBean signConfirmBean = (SignConfirmBean) obj;
                    strAttendId = signConfirmBean.getStrAttendId();
                    strType = signConfirmBean.getStrType();
                    if ("1".equals(strType)) {
                        nameTag_TextView.setText(getString(R.string.string_activity_name));
                        dateTag_TextView.setText(getString(R.string.string_activity_time));
                        placeTag_TextView.setText(getString(R.string.string_activity_address));
                    } else {
                        nameTag_TextView.setText(getString(R.string.string_meeting_name));
                        dateTag_TextView.setText(R.string.string_meeting_time);
                        placeTag_TextView.setText(getString(R.string.string_meeting_address));
                    }
                    showHeadPhoto(Utils.formatFileUrl(activity, signConfirmBean.getStrPathUrl()), head_ImageView);
                    user_TextView.setText(signConfirmBean.getStrMemberName());
                    duty_TextView.setText(signConfirmBean.getStrDuty());
                    faction_TextView.setText(signConfirmBean.getStrFaction());
                    sector_TextView.setText(signConfirmBean.getStrSector());
                    name_TextView.setText(signConfirmBean.getStrName());
                    date_TextView.setText(signConfirmBean.getStrDate());
                    place_TextView.setText(signConfirmBean.getStrPlace());
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }
}
