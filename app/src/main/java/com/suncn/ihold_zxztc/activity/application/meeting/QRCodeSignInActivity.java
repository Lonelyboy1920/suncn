package com.suncn.ihold_zxztc.activity.application.meeting;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.google.gson.Gson;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.QRInfoBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;


/**
 * 二维码签到页面
 */
public class QRCodeSignInActivity extends BaseActivity {
    @BindView(id = R.id.tv_name)
    private TextView name_TextView;//姓名
    @BindView(id = R.id.tv_unit)
    private TextView unit_TextView;//机构
    @BindView(id = R.id.tv_dp)
    private TextView dp_TextView;//党派
    @BindView(id = R.id.tv_jb)
    private TextView jb_TextView;//界别
    @BindView(id = R.id.iv_qr)
    private ImageView qr_ImageView;//二维码ImageView
    private String strId;
    @BindView(id = R.id.iv_person_head)
    private RoundImageView personHead_ImageView;

    public static final String ACTION_OPEN_QR_CODE_SINE = "com.suncn.ihold_zxztc.activity.application.meeting.qrcodesine.action.DYNAMIC_OPEN";

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_qrcode_sign_in);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_duty_idcard));
        DefineUtil.isNeedRefresh=true;
        name_TextView.setText(GISharedPreUtil.getString(activity, "strName"));
        dp_TextView.setText(GISharedPreUtil.getString(activity, "strFaction"));//党派
        jb_TextView.setText(GISharedPreUtil.getString(activity, "strSector"));//界别
        unit_TextView.setText(GISharedPreUtil.getString(activity, "strDuty"));
        showHeadPhoto(GISharedPreUtil.getString(activity, "strPhotoUrl"), personHead_ImageView);
        QRInfoBean infoBean = new QRInfoBean();
        infoBean.setStrUserId(GISharedPreUtil.getString(activity, "strUserId"));
        Bundle bundle = getIntent().getExtras();
        String strType = "";
        if (bundle != null) {
            strId = bundle.getString("strId");
            infoBean.setStrId(strId);
            strType = bundle.getString("strType");
            if (strType.equals("-1")) {//陕西demo strType=-1
                setHeadTitle(getString(R.string.string_member)+getString(R.string.string_duty_idcard));
            } else if (strType.equals("1")) {//活动
                setHeadTitle(getString(R.string.string_event_check_in));
            } else if (strType.equals("0")) {//会议
                setHeadTitle(getString(R.string.string_conference_registration));
            } else if (strType.equals("2")) {
                setHeadTitle(getString(R.string.string_conference_registration));
            }
            if (!strType.equals("-1")) {
                GISharedPreUtil.setValue(activity, "isRefreshMeetAct", true);
            }
        }
        final Gson gson = new Gson();
        final String msg = gson.toJson(infoBean);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                qr_ImageView.setImageBitmap(QRCodeEncoder.syncEncodeQRCode(msg, GIDensityUtil.dip2px(activity, 100), getResources().getColor(R.color.black), getResources().getColor(R.color.white), null));
            }
        });
    }


}
