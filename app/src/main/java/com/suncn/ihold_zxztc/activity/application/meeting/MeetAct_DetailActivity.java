package com.suncn.ihold_zxztc.activity.application.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Proposal_Detail_VPAdapter;
import com.suncn.ihold_zxztc.bean.ActivityViewBean;
import com.suncn.ihold_zxztc.bean.MeetingViewBean;
import com.suncn.ihold_zxztc.bean.ObjPersonListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;

import java.util.ArrayList;
import java.util.HashMap;

import skin.support.design.widget.SkinMaterialTabLayout;

/**
 * 我的会议、我的活动详情页面
 */
public class MeetAct_DetailActivity extends BaseActivity {
    @BindView(id = R.id.btn_spinner, click = true)
    private GITextView spinner_Button; // 届次选择按钮
    @BindView(id = R.id.pager)
    private ViewPager pager; // ViewPager
    @BindView(id = R.id.tabLayout)
    private SkinMaterialTabLayout indicator; // 头部选项卡
    @BindView(id = R.id.ll_btn)
    private LinearLayout llBtn;
    @BindView(id = R.id.btn_left, click = true)
    private TextView btnLeft;
    @BindView(id = R.id.btn_right, click = true)
    private TextView btnRight;
    private String strId;
    private int sign;
    private int intUserRole;
    private String mobile;
    private Proposal_Detail_VPAdapter meetingadapter;
    private Proposal_Detail_VPAdapter activityadapter;
    private boolean isUpdate;
    private boolean isFromMsgNotice;
    private boolean isWorker = false;
    public String[] typeArrays;
    private int strTypeCommissioner = 0; // 委员管理展示类别
    private boolean isManager;
    private boolean isFromList = false;
    private boolean isFromRemindList = false;
    public boolean isShowSign = false;
    private boolean onlyShowNormalInfo = false;
    private String intAttend;//出席状态0：请假 1：出席 2：缺席
    private ArrayList<String> objList;
    private SpinerPopWindow<String> mSpinerPopWindow;
    private String strAttendId;//
    private MeetingViewBean meetingViewBean;
    private ActivityViewBean activityViewBean;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0://活动、会议详情、次会参与状态修改
                case 1:
                    isUpdate = true;
                    GISharedPreUtil.setValue(activity, "isRefreshMeetAct", true);
                    getDetail();
                    break;
                case 2://
                    setResult(RESULT_OK);
                    finish();
                    break;
                case 3:

                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (isFromList) {
                    Intent intent = new Intent();
                    intent.putExtra("intAttend", intAttend);
                    if (isUpdate) {
                        setResult(RESULT_OK, intent);
                    } else {
                        setResult(RESULT_OK);
                    }
                } else if (isFromRemindList) {
                    setResult(RESULT_OK);
                }
                break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setListener() {
        super.setListener();
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromList) {
                    Intent intent = new Intent();
                    intent.putExtra("intAttend", intAttend);
                    if (isUpdate) {
                        setResult(RESULT_OK, intent);
                    } else {
                        setResult(RESULT_OK);
                    }
                } else if (isFromRemindList) {
                    setResult(RESULT_OK);
                }
                finish();
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (ProjectNameUtil.isSZSZX(activity) || ProjectNameUtil.isJMSZX(activity)) {
                    if (meetingadapter != null && meetingadapter.getPageTitle(i).toString().contains("出席情况")) {
                        spinner_Button.setVisibility(View.VISIBLE);
                    } else if (activityadapter != null && activityadapter.getPageTitle(i).toString().contains("出席情况")) {
                        spinner_Button.setVisibility(View.VISIBLE);
                    } else {
                        spinner_Button.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_spinner:
                mSpinerPopWindow.setWidth(GIDensityUtil.dip2px(activity, 100));
                mSpinerPopWindow.showAsDropDown(spinner_Button);
                break;
            case R.id.btn_left:
                if (btnLeft.getText().equals("接受邀请")) {
                    receiveMeet();
                } else if (btnLeft.getText().equals("更改意愿")) {
                    showConfirmDialog(1);
                }
                if (btnLeft.getText().equals("撤回请假")) {
                    showConfirmDialog(2);
                }
                break;
            case R.id.btn_right:
                Bundle bundle = new Bundle();
                bundle.putString("strId", strId);
                bundle.putString("strAttendId", strAttendId);
                bundle.putInt("sign", sign);
                showActivity(activity, MeetActApplyLeaveActivity.class, bundle, 0);
                break;
            case R.id.btn_goto:
                String strWeiDu = ""; //纬度
                String strJingDu = ""; // 经度
                String strMtTime = "";
                String strMtName = "";
                String strMtId = "";
                String strLocationSignDistance = "";
                bundle = new Bundle();
                if (sign == DefineUtil.wdhd) {
                    strMtId = activityViewBean.getStrId();
                    strJingDu = activityViewBean.getStrLongitude();
                    strWeiDu = activityViewBean.getStrLatitude();
                    strMtName = activityViewBean.getStrAtName();
                    strMtTime = activityViewBean.getStrStartDate();
                    strLocationSignDistance = activityViewBean.getStrLocationSignDistance();
                    bundle.putString("strMtId", strMtId);
                    bundle.putString("strLocationSignDistance", strLocationSignDistance);
                    bundle.putString("strWeiDu", strWeiDu);
                    bundle.putString("strJingDu", strJingDu);
                    bundle.putString("strMtTime", strMtTime);
                    bundle.putString("strMtName", strMtName);
                    bundle.putString("strType", "1");
                    bundle.putString("strAttendId", strAttendId);
                    bundle.putString("headTitle", "签到");
                } else {
                    strMtId = meetingViewBean.getStrId();
                    strJingDu = meetingViewBean.getStrLongitude();
                    strWeiDu = meetingViewBean.getStrLatitude();
                    strMtName = meetingViewBean.getStrMtName();
                    strMtTime = meetingViewBean.getStrStartDate();
                    strLocationSignDistance = meetingViewBean.getStrLocationSignDistance();
                    bundle.putString("strMtId", strMtId);
                    bundle.putString("strLocationSignDistance", strLocationSignDistance);
                    bundle.putString("strWeiDu", strWeiDu);
                    bundle.putString("strJingDu", strJingDu);
                    bundle.putString("strMtTime", strMtTime);
                    bundle.putString("strMtName", strMtName);
                    bundle.putString("strType", "0");
                    bundle.putString("headTitle", "签到");
                    bundle.putString("strAttendId", strAttendId);
                }
                showActivity(activity, SignInActivity.class, bundle, 2);
                break;
        }
    }

    /**
     * 对话框
     */
    private void showConfirmDialog(int type) {
        String title;
        if (type == 1) {
            title = getString(R.string.string_are_you_sure_to_change);
        } else {
            title = getString(R.string.string_are_you_sure_to_recall);
        }
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.string_tips)).content(title).btnText(getString(R.string.string_cancle), getString(R.string.string_determine)).showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        if (type == 1) {
                            Bundle bundle = new Bundle();
                            bundle.putString("strId", strId);
                            bundle.putString("strAttendId", strAttendId);
                            bundle.putInt("sign", sign);
                            showActivity(activity, MeetActApplyLeaveActivity.class, bundle, 0);
                            dialog.dismiss();
                        } else {
                            withdrawLeave();
                            dialog.dismiss();
                        }
                    }
                }
        );
    }

    /**
     * 对话框
     */
    private void showConfirmDialog() {
        String title;
        title = getString(R.string.string_receive_invit);
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.string_tips)).content(title).btnText(getString(R.string.string_determine), "").showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(new OnBtnClickL() {
                                  @Override
                                  public void onBtnClick() {
                                      dialog.dismiss();

                                  }
                              }
        );
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_global_viewpager);
    }

    public void setPageTitle(int position, String title) {
        if (meetingadapter != null) {
            meetingadapter.setPageTitle(position, title);
            indicator.setupWithViewPager(pager);
        } else if (activityadapter != null) {
            activityadapter.setPageTitle(position, title);
            indicator.setupWithViewPager(pager);
        }
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

        intUserRole = GISharedPreUtil.getInt(activity, "intUserRole");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFromRemindList = bundle.getBoolean("isFromRemindList", false);
            isFromList = bundle.getBoolean("isFromList", false);
            isShowSign = bundle.getBoolean("isShowSign", false);
            onlyShowNormalInfo = bundle.getBoolean("onlyShowNormalInfo", false);
            isManager = bundle.getBoolean("isManager", false);
            setHeadTitle(bundle.getString("headTitle"));
            strId = bundle.getString("strId");
            strAttendId = bundle.getString("strAttendId", "");
            sign = bundle.getInt("sign");
            isWorker = bundle.getBoolean("isWorker", false);
            if (isWorker) {
                goto_Button.setVisibility(View.VISIBLE);
                typeArrays = new String[]{"平铺展示", "界别", "党派", "专委会"};
            }
            isFromMsgNotice = bundle.getBoolean("isFromMsgNotice", false);
            if (isFromMsgNotice) {
                String strMsgType = bundle.getString("strMsgType");
                String strStateId = bundle.getString("strStateId");
                //sendReadState(strMsgType, strStateId);
            }
            getDetail();
        }

    }

    /**
     * popupwindow显示的ListView的item点击事件(年份)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            spinner_Button.setText(objList.get(position) + " \ue771");
            MeetAct_Detail_AttendPersonFragment fragment = null;
            if (meetingadapter != null) {
                fragment = (MeetAct_Detail_AttendPersonFragment) getSupportFragmentManager().findFragmentByTag(meetingadapter.getTagList().get(1));
            } else if (activityadapter != null) {
                fragment = (MeetAct_Detail_AttendPersonFragment) getSupportFragmentManager().findFragmentByTag(activityadapter.getTagList().get(1));
            }
            if (fragment != null) {
                if (objList.get(position).equals("全部")) {
                    fragment.intAttend = "";
                } else if (objList.get(position).equals("出席")) {
                    fragment.intAttend = "1";
                } else if (objList.get(position).equals("请假")) {
                    fragment.intAttend = "0";
                } else if (objList.get(position).equals("缺席")) {
                    fragment.intAttend = "2";
                }
                fragment.getActJoinMemInfo(false);
            }
        }
    };


    /**
     * 接受邀请
     */
    private void receiveMeet() {
        textParamMap = new HashMap<>();
        textParamMap.put("strAttendId", strAttendId);
        if (sign == DefineUtil.wdhd) {
            doRequestNormal(ApiManager.getInstance().AcceptEventServlet(textParamMap), 2);
        } else {
            doRequestNormal(ApiManager.getInstance().AcceptMeetServlet(textParamMap), 2);
        }

    }

    /**
     * 撤回请假
     */
    private void withdrawLeave() {
        textParamMap = new HashMap<>();
        textParamMap.put("strAttendId", strAttendId);
        if (sign == DefineUtil.wdhd) {
            doRequestNormal(ApiManager.getInstance().RecallEventApplyServlet(textParamMap), 3);
        } else {
            doRequestNormal(ApiManager.getInstance().RecallMeetApplyServlet(textParamMap), 3);
        }
    }


    /**
     * 获取详情
     */
    private void getDetail() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        textParamMap.put("strAttendId", strAttendId);
        if (sign == DefineUtil.wdhy) {
            doRequestNormal(ApiManager.getInstance().getMeetView(textParamMap), 1);
        } else if (sign == DefineUtil.hygl) {
            doRequestNormal(ApiManager.getInstance().MeetViewInfoServlet(textParamMap), 1);
        } else if (sign == DefineUtil.wdhd) {
            doRequestNormal(ApiManager.getInstance().getActView(textParamMap), 0);
        } else if (sign == DefineUtil.hdgl) {
            doRequestNormal(ApiManager.getInstance().getMangerMeetView(textParamMap), 0);
        }
    }


    /**
     * 请求结果
     */
    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        String toastMessage = null;
        switch (what) {
            case 0://活动
                try {
                    activityViewBean = (ActivityViewBean) object;
                    intAttend = activityViewBean.getStrAttendResult();
                    if (GIStringUtil.isNotBlank(activityViewBean.getStrAttendId())) {
                        strAttendId = activityViewBean.getStrAttendId();
                    }
                    ArrayList<ObjPersonListBean> objPersonListBeen = activityViewBean.getObjMem_list();
                    if (activityadapter == null)
                        activityadapter = new Proposal_Detail_VPAdapter(getSupportFragmentManager(), sign, intUserRole, onlyShowNormalInfo, activity);
                    activityadapter.setStrId(activityViewBean.getStrId());
                    activityadapter.setStrAttendId(strAttendId);
                    activityadapter.setActivityViewBean(activityViewBean);
                    activityadapter.setObjPersonListBeen(objPersonListBeen);
                    activityadapter.setManager(onlyShowNormalInfo);
                    if (isUpdate) {
                        activityadapter.update(activityViewBean, null);
                    } else {
                        pager.setAdapter(activityadapter);
//                        indicator.setViewPager(pager);
                        indicator.setupWithViewPager(pager);
                    }
                    if (intUserRole != 1 || !isManager || onlyShowNormalInfo) {
                        indicator.setVisibility(View.GONE);
                    }
                    if ("-1".equals(intAttend) && !"1".equals(activityViewBean.getStrIsEnd())) {
                        if (activityViewBean.getStrAttendWill().equals("-1") || activityViewBean.getStrAttendWill().equals("3")) {
                            llBtn.setVisibility(View.VISIBLE);
                            btnLeft.setText("接受邀请");
                            btnRight.setText("请假");
                        } else if (activityViewBean.getStrAttendWill().equals("0")) {
                            llBtn.setVisibility(View.VISIBLE);
                            btnLeft.setText("撤回请假");
                            btnRight.setVisibility(View.GONE);
                        } else if (activityViewBean.getStrAttendWill().equals("1")) {
                            llBtn.setVisibility(View.VISIBLE);
                            btnLeft.setText("更改意愿");
                            btnRight.setVisibility(View.GONE);
                        }
                    } else {
                        llBtn.setVisibility(View.GONE);
                    }
                    if ("1".equals(activityViewBean.getIsLocationCheck())
                            && !"1".equals(activityViewBean.getStrAttendResult())) {
                        goto_Button.setVisibility(View.VISIBLE);
                        goto_Button.setText("签到");
                        goto_Button.setTextSize(16);
                    } else {
                        goto_Button.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 1://会议
                try {
                    meetingViewBean = (MeetingViewBean) object;
                    meetingViewBean.setStrId(strId);
                    if (GIStringUtil.isNotBlank(meetingViewBean.getStrAttendId())) {
                        strAttendId = meetingViewBean.getStrAttendId();
                    }
                    intAttend = meetingViewBean.getStrAttendResult();
                    ArrayList<MeetingViewBean.ObjMemListBean> objMemListBeen = meetingViewBean.getObjMem_list();
                    if (meetingadapter == null)
                        meetingadapter = new Proposal_Detail_VPAdapter(getSupportFragmentManager(), sign, intUserRole, onlyShowNormalInfo, activity);
                    meetingadapter.setStrId(strId);
                    meetingadapter.setStrAttendId(strAttendId);
                    meetingadapter.setManager(onlyShowNormalInfo);
                    meetingadapter.setMeetingViewBean(meetingViewBean);
                    meetingadapter.setObjMemListBeen(objMemListBeen);
                    if (isUpdate) {
                        meetingadapter.update(null, meetingViewBean);
                    } else {
                        pager.setAdapter(meetingadapter);
                        indicator.setupWithViewPager(pager);
                    }
                    if (intUserRole != 1 || !isManager || onlyShowNormalInfo) {
                        indicator.setVisibility(View.GONE);
                    }
                    if ("-1".equals(intAttend) && !"1".equals(meetingViewBean.getStrIsEnd())) {
                        if (meetingViewBean.getStrAttendWill().equals("-1") || meetingViewBean.getStrAttendWill().equals("3")) {
                            llBtn.setVisibility(View.VISIBLE);
                            btnLeft.setText("接受邀请");
                            btnRight.setText("请假");
                        } else if (meetingViewBean.getStrAttendWill().equals("0")) {
                            llBtn.setVisibility(View.VISIBLE);
                            btnLeft.setText("撤回请假");
                            btnRight.setVisibility(View.GONE);
                        } else if (meetingViewBean.getStrAttendWill().equals("1")) {
                            llBtn.setVisibility(View.VISIBLE);
                            btnLeft.setText("更改意愿");
                            btnRight.setVisibility(View.GONE);
                        }
                    } else {
                        llBtn.setVisibility(View.GONE);
                    }
                    if ("1".equals(meetingViewBean.getIsLocationCheck())
                            && !"1".equals(meetingViewBean.getStrAttendResult())) {
                        goto_Button.setVisibility(View.VISIBLE);
                        goto_Button.setText("签到");
                        goto_Button.setTextSize(16);
                    } else {
                        goto_Button.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 2:
                showConfirmDialog();
                if (btnLeft.getText().equals("接受邀请")) {
                    llBtn.setVisibility(View.VISIBLE);
                    btnLeft.setText("更改意愿");
                    btnRight.setVisibility(View.GONE);
                }
                GISharedPreUtil.setValue(activity, "isRefreshMeetAct", true);
                if (meetingViewBean != null) {
                    meetingViewBean.setStrState("已接受");
                    meetingadapter.update(null, meetingViewBean);
                } else if (activityViewBean != null) {
                    activityViewBean.setStrState("已接受");
                    activityadapter.update(activityViewBean, null);
                }
                break;
            case 3:
                llBtn.setVisibility(View.VISIBLE);
                btnRight.setVisibility(View.VISIBLE);
                btnLeft.setText("接受邀请");
                btnRight.setText("请假");
                if (meetingViewBean != null) {
                    meetingViewBean.setStrState("未响应");
                    meetingadapter.update(null, meetingViewBean);
                } else if (activityViewBean != null) {
                    activityViewBean.setStrState("未响应");
                    activityadapter.update(activityViewBean, null);
                }
                GISharedPreUtil.setValue(activity, "isRefreshMeetAct", true);
                break;
            default:
                break;
        }

        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }

    @Override
    public void onPause() {
        super.onPause();
        isUpdate = false;
    }
}
