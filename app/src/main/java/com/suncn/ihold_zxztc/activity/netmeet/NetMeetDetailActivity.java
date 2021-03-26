package com.suncn.ihold_zxztc.activity.netmeet;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.BaseMeetDetailBean;
import com.suncn.ihold_zxztc.hst.FspEvents;
import com.suncn.ihold_zxztc.hst.FspManager;
import com.suncn.ihold_zxztc.hst.FspPreferenceManager;
import com.suncn.ihold_zxztc.hst.MainMeetActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class NetMeetDetailActivity extends BaseActivity {
    @BindView(id = R.id.tv_mc_title)
    private TextView tvMcTitle;
    @BindView(id = R.id.tv_mc_content)
    private TextView tvMcContent;
    @BindView(id = R.id.tv_mc_start_time)
    private TextView tvMcStartTime;
    @BindView(id = R.id.tv_mc_end_time)
    private TextView tvMcEndTime;
    @BindView(id = R.id.tv_mc_member)
    private TextView tvMcMember;
    @BindView(id = R.id.btn_mc_start, click = true)
    private Button btn_mc_start;
    private String strId;
    private BaseMeetDetailBean baseMeetDetailBean;
    private boolean isCanJoin = false;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_meeting_detail);
    }

    @Override
    public void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        setHeadTitle("会议详情");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strId = bundle.getString("strId", "");
            isCanJoin = bundle.getBoolean("isCanJoin", true);
        }
        getDetail();
    }

    private void getDetail() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().VideoMeetingViewServlet(textParamMap), 0);
    }

    private void doLogic(int what, Object object) {
        prgDialog.closePrgDialog();
        switch (what) {
            case 0:
                baseMeetDetailBean = (BaseMeetDetailBean) object;
                tvMcContent.setText(baseMeetDetailBean.getStrContent());
                tvMcTitle.setText(baseMeetDetailBean.getStrName());
                tvMcStartTime.setText(baseMeetDetailBean.getStrStartDate());
                tvMcEndTime.setText(baseMeetDetailBean.getStrEndDate());
                tvMcMember.setText(baseMeetDetailBean.getStrJointMember());
                if (isCanJoin) {
                    btn_mc_start.setVisibility(View.VISIBLE);
                } else {
                    btn_mc_start.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_mc_start:
                prgDialog.showLoadDialog();
                if (GISharedPreUtil.getBoolean(activity, GISharedPreUtil.getString(activity, "strLoginUserId") + "isHstLogin", false)) {
                    FspManager.getInstance().joinGroup(strId);
                } else {
                    joinMeet();
                }
                break;
        }
    }

    private void joinMeet() {
        List<PermissionItem> permissionItems = new ArrayList<>();
        permissionItems.add(new PermissionItem(Manifest.permission.RECORD_AUDIO, "录音", R.drawable.permission_ic_storage));
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "相机", R.drawable.permission_ic_camera));
        HiPermission.create(activity)
                .permissions(permissionItems)
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onFinish() {
                        if (!FspManager.getInstance().init()) {
                            GILogUtil.e("好视通启动失败");
                        } else {
                            GILogUtil.e("好视通启动成功");
                        }
                        FspManager.getInstance().login(GISharedPreUtil.getString(activity, "strLoginUserId"));
                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginResult(FspEvents.LoginResult result) {
        if (result.isSuccess) {
            FspManager.getInstance().joinGroup(strId);
            GISharedPreUtil.setValue(activity, GISharedPreUtil.getString(activity, "strLoginUserId") + "isHstLogin", true);
            GILogUtil.e("好视通登录成功");
        } else {
            prgDialog.closePrgDialog();
            GILogUtil.e("好视通登录失败");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventJoinGroupResult(FspEvents.JoinGroupResult result) {
        prgDialog.closePrgDialog();
        if (result.isSuccess) {
            showToast("加入会议成功");
            Bundle bundle = new Bundle();
            bundle.putString("strName", baseMeetDetailBean.getStrName());
            bundle.putBoolean("isCreate", GISharedPreUtil.getString(activity, "strLoginUserId").equals(baseMeetDetailBean.getStrCreatUserId()));
            bundle.putString("strJointUserId", baseMeetDetailBean.getStrCreatUserId() + "," + baseMeetDetailBean.getStrJointUserId());
            bundle.putString("strId", strId);
            showActivity(activity, MainMeetActivity.class, bundle);
        } else {
            showToast("加入会议失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
