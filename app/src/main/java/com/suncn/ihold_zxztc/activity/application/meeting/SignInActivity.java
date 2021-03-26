package com.suncn.ihold_zxztc.activity.application.meeting;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.IosAttendBean;
import com.suncn.ihold_zxztc.service.LocationService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * 签到界面
 */
public class SignInActivity extends BaseActivity {
    @BindView(id = R.id.ll_succed)
    private LinearLayout succed_LinearLayout;//定位成功
    @BindView(id = R.id.ll_failed)
    private LinearLayout failed_LinearLayout;//定位失败
    @BindView(id = R.id.btn_get_postion, click = true)
    private Button getPostion_Button;//重新定位
    @BindView(id = R.id.tv_icon)
    private TextView icon_TextView;
    @BindView(id = R.id.tv_title)
    private TextView title_TextView;
    @BindView(id = R.id.tv_address)
    private TextView address11_TextView;//地点TextView
    @BindView(id = R.id.tv_time)
    private TextView time_TextView;//时间TextView
    @BindView(id = R.id.ll_signIn, click = true)
    private LinearLayout signIn_Layout;//签到LinearLayout
    @BindView(id = R.id.tv_mtDate)
    private TextView mtDate_TextView;//会议(活动)时间TextView
    @BindView(id = R.id.tv_mtName)
    private TextView mtName_TextView;//会议(活动)名称TextView

    private String strMtId; // 会议或活动的id
    private LatLng siteLat;
    private String address; // 当前地址
    private int myDistance;
    private DateFormat df;
    private String strType;//类型（0会议，1活动,2.次会）
    private String strLocationSignDistance;
    // 定位相关
    private MyLocationListenner myListener = new MyLocationListenner();
    public LocationService locationService;
    private String strSignWay = "";//2时定位失败跳转到二维码签到
    private boolean isFirstSign = false;
    private String strAttendId;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    setResult(RESULT_OK);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setRootView() {
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService = new LocationService(getApplicationContext());
        setContentView(R.layout.activity_singin);
        isShowBackBtn = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationService.registerListener(myListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(myListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        df = new SimpleDateFormat("HH:mm");
        time_TextView.setText(df.format(new Date()));
        signIn_Layout.setEnabled(false);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setHeadTitle(bundle.getString("headTitle"));
            String strJingDu = bundle.getString("strJingDu");
            String strWeiDu = bundle.getString("strWeiDu");
            mtDate_TextView.setText(bundle.getString("strMtTime"));
            mtName_TextView.setText(bundle.getString("strMtName"));
            strSignWay = bundle.getString("strSignWay");
            strMtId = bundle.getString("strMtId");
            strType = bundle.getString("strType");
            strAttendId=bundle.getString("strAttendId","");
            if ("1".equals(strType)) {
                setHeadTitle(getString(R.string.string_event_check_in));
            } else if ("0".equals(strType) || "2".equals(strType)) {
                setHeadTitle(getString(R.string.string_conference_registration));
            }
            strLocationSignDistance = bundle.getString("strLocationSignDistance");
            GILogUtil.i("strLocationSignDistance----->" + strLocationSignDistance);
            if (GIStringUtil.isNotEmpty(strJingDu) && GIStringUtil.isNotEmpty(strWeiDu)) {
                siteLat = new LatLng(Double.parseDouble(strWeiDu), Double.parseDouble(strJingDu));
            }
        }

        //请求授权
        HiPermission.create(activity).checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, new PermissionCallback() {
            @Override
            public void onGuarantee(String permisson, int position) { // 同意/已授权
                GILogUtil.i("onGuarantee");
                prgDialog.showLoadDialog();
                locationService.start();// 定位SDK
            }

            @Override
            public void onClose() { // 用户关闭权限申请
                GILogUtil.i("onClose");
                prgDialog.closePrgDialog();
            }

            @Override
            public void onFinish() { // 所有权限申请完成
                GILogUtil.i("onFinish");
                prgDialog.closePrgDialog();
            }

            @Override
            public void onDeny(String permisson, int position) { // 拒绝
                GILogUtil.i("onDeny");
                prgDialog.closePrgDialog();
                locationFailed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_postion:
                prgDialog.showLoadDialog();
                locationService.registerListener(myListener);
                locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                locationService.start();// 定位SDK
                break;
            case R.id.ll_signIn:
                doIosAttend();
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    /**
     * 签到
     */
    private void doIosAttend() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strAttendId", strAttendId); // 会议或活动的id
        if ("1".equals(strType)) {
            textParamMap.put("isMeetOrEvent", strType);
        } else {
            textParamMap.put("isMeetOrEvent", "0");
        }
        doRequestNormal(ApiManager.getInstance().dealSign(textParamMap), 0);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    IosAttendBean iosAttendBean = (IosAttendBean) obj;
                    Bundle bundle = new Bundle();
                    bundle.putString("strMtId", strMtId);
                    bundle.putString("address", address);
                    bundle.putString("strQdTime", iosAttendBean.getStrRealSignDate());
                    bundle.putInt("intQdAttend", iosAttendBean.getStrIsLateSign());
                    bundle.putString("headTitle", head_title_TextView.getText().toString());
                    bundle.putString("strAttendId", GIStringUtil.nullToEmpty(iosAttendBean.getStrAttendId()));
                    showActivity(activity, SignInResultActivity.class, bundle, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }

        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            time_TextView.setText(df.format(new Date()));
            if (myDistance > Integer.parseInt(strLocationSignDistance)) {
                title_TextView.setText(R.string.string_not_within_the_scope_of_checkin);
                title_TextView.setTextColor(getResources().getColor(R.color.view_head_bg));
                icon_TextView.setTextColor(getResources().getColor(R.color.view_head_bg));
                signIn_Layout.setBackgroundResource(R.mipmap.btn_signin_bg_hover);
                signIn_Layout.setEnabled(false);
            } else {
                title_TextView.setText(R.string.string_you_are_within_the_scope_of_checkin);
                title_TextView.setTextColor(getResources().getColor(R.color.zxta_state_green));
                icon_TextView.setTextColor(getResources().getColor(R.color.zxta_state_green));
                signIn_Layout.setBackgroundResource(R.mipmap.btn_signin_bg);
                signIn_Layout.setEnabled(true);
            }
            address11_TextView.setVisibility(View.VISIBLE);
            address11_TextView.setText(address);
            super.handleMessage(msg);
        }
    };

    /**
     * 定位SDK监听函数
     */
    private class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                locationFailed();
                return;
            }
            if (!isFirstSign) {
                isFirstSign = true;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                myDistance = (int) DistanceUtil.getDistance(ll, siteLat);
                GILogUtil.i("myDistance----->" + myDistance);
                address = location.getAddrStr();
                GILogUtil.i("myAddress----->" + address);
                handler.sendEmptyMessage(0);
                if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {// GPS定位结果
                    succed_LinearLayout.setVisibility(View.VISIBLE);
                    failed_LinearLayout.setVisibility(View.GONE);
                    prgDialog.closePrgDialog();
                } else {
                    locationFailed();
                }
            }

        }
    }

    /**
     * 定位签到异常时处理方式
     */
    private void locationFailed() {
        prgDialog.closePrgDialog();
        if (GIStringUtil.isNotBlank(strSignWay)) {
            Bundle bundle = new Bundle();
            bundle.putString("strId", strMtId);//活动id
            bundle.putString("strType", strType);//我的会议活动类型0
            showActivity(activity, QRCodeSignInActivity.class, bundle, 2);
            finish();
        } else {
            locationService.unregisterListener(myListener); //注销掉监听
            locationService.stop(); //停止定位服务
            succed_LinearLayout.setVisibility(View.GONE);
            failed_LinearLayout.setVisibility(View.VISIBLE);
        }
    }
}
