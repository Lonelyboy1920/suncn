package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Proposal_Detail_VPAdapter;
import com.suncn.ihold_zxztc.adapter.SessionCode_LVAdapter;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.ProposalDealListBean;
import com.suncn.ihold_zxztc.bean.ProposalViewBean;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;

import java.util.ArrayList;
import java.util.HashMap;

import skin.support.design.widget.SkinMaterialTabLayout;

/**
 * 政协提案详情界面
 */
public class Proposal_DetailActivity extends BaseActivity {
    @BindView(id = R.id.tv_title)
    private TextView title_TextView;//提案标题
    @BindView(id = R.id.tv_caseNo)
    private TextView caseNo_TextView;//届次案号
    @BindView(id = R.id.tv_date)
    private TextView date_TextView;
    @BindView(id = R.id.pager)
    private ViewPager pager; // ViewPager
    @BindView(id = R.id.tabLayout)
    private SkinMaterialTabLayout indicator; // 头部选项卡

    private String strId; // 提案主键ID
    private String strCaseId; // 提案办理ID
    private String strAttend; // 是否同意提名 ReferCaseReportServlet接口返回
    private int zxtaType; // 提案菜单类型
    private SimpleCustomPop mQuickCustomPopup;
    private int intUserRole; // 用户类型（0-委员；1-工作人员；2-承办单位）
    private ProposalViewBean zxtaViewBean;
    private boolean isFromMsgNotice;
    private boolean isWorker;
    private int intState; // 承办系统是否交办
    private boolean isUnit;
    private int operationType;//提案操作类型 1待签收 2办理中 3待交办提案
    private ProposalDealListBean.ProposalDeal proposalDeal;
    private String strHandlerTypeName = "";
    private String strUseCbdwBack;
    private String strHandlerLimitDate;
    private boolean isFromRemindList;//是否来自消息
    private String strFeedback;
    private String strHbFinished;// 会办没有办结时，主办不让办理；（1-会办没有办结）【初次在贵州贵阳项目返回该字段，如果返回1需要弹出提醒对话框】
    private boolean isCanEdit; //是否可以重新编辑
    private String proposalDetailShowTab; //1(显示基本信息、提案内容)   2(办理中、待反馈的，显示基本信息、提案内容、办理情况)   3(已反馈的，显示基本信息、提案内容、办理情况、反馈情况)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (data != null) {//审核回来
                        setResult(RESULT_OK, data);
                    } else {
                        setResult(RESULT_OK);
                    }
                    finish();
                    break;
                default:
                    break;
            }
        } else if (resultCode == -2) {//延期申请
            setResult(-2);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_detail);
    }

    @Override
    public void setListener() {
        super.setListener();
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromRemindList)
                    setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (isFromRemindList)
                    setResult(RESULT_OK);
                finish();
                break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void initData() {
        super.initData();
        strUseCbdwBack = GISharedPreUtil.getString(activity, "strUseCbdwBack");
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (GIStringUtil.isNotBlank(bundle.getString("strHandlerTypeName"))) {
                strHandlerTypeName = bundle.getString("strHandlerTypeName");
            } else {
                strHandlerTypeName = "";
            }
            operationType = bundle.getInt("operationType");
            proposalDeal = (ProposalDealListBean.ProposalDeal) bundle.getSerializable("proposalDeal");
            isWorker = bundle.getBoolean("isWorker", false);
            if (isWorker) {
                String gotoValue = bundle.getString("gotoValue");
                if (GIStringUtil.isNotBlank(gotoValue)) {
                    goto_Button.setVisibility(View.VISIBLE);
                    goto_Button.setText(bundle.getString("gotoValue"));
                } else {
                    goto_Button.setVisibility(View.GONE);
                }
            }
            isFromMsgNotice = bundle.getBoolean("isFromMsgNotice", false);
            isFromRemindList = bundle.getBoolean("isFromRemindList", false);
            if (isFromMsgNotice) {
                String strMsgType = bundle.getString("strMsgType");
                String strStateId = bundle.getString("strStateId");
                sendReadState(strMsgType, strStateId);
            }
            setHeadTitle(getString(R.string.string_proposal_detail));
            intUserRole = GISharedPreUtil.getInt(activity, "intUserRole");
            strId = bundle.getString("strId");
            intState = bundle.getInt("intState");
            isUnit = bundle.getBoolean("isUnit");
            strCaseId = bundle.getString("strCaseId");
            strHandlerLimitDate = bundle.getString("strHandlerLimitDate", "");
            strAttend = bundle.getString("strAttend");
            strFeedback = bundle.getString("strFeedback", "1");
            String strFlowState = bundle.getString("strFlowState"); // 办理状态（已上报、已立案、办理中、待反馈、已反馈（不满意）、已办结）
            zxtaType = bundle.getInt("zxtaType");
            isCanEdit = bundle.getBoolean("isCanEdit", false);
            proposalDetailShowTab = bundle.getString("proposalDetailShowTab", "0");
            if (strAttend != null && strAttend.equals("2")) { // 联名提案
                goto_Button.setText(R.string.string_joint_confirm);
                goto_Button.setVisibility(View.VISIBLE);
            } else if (strAttend != null && strAttend.equals("-1")) { // 联名提案
                goto_Button.setText(R.string.string_second);
                goto_Button.setVisibility(View.VISIBLE);
            } else if (strFlowState != null && strFlowState.equals(getString(R.string.string_wait_feedback))
                    && strFeedback.equals("1") && !ProjectNameUtil.isJMSZX(activity)) {
                goto_Button.setText(R.string.string_member_feedback);
                goto_Button.setVisibility(View.VISIBLE);
            } else if (zxtaType == DefineUtil.zxta_signIn) { // 提案签收
                goto_Button.setText(R.string.string_operation);
                goto_Button.setVisibility(View.VISIBLE);
            } else if (isCanEdit) {
                goto_Button.setText(R.string.string_edit);
                goto_Button.setVisibility(View.VISIBLE);
            }
            goto_Button.refreshFontType(activity, "2");
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            getZxtaDetail();
        }
        mQuickCustomPopup = new SimpleCustomPop(activity);
    }

    /**
     * 修改已读状态
     */
    private void sendReadState(String strMsgType, String strStateId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strMsgType", strMsgType);//消息类型notice,handler
        textParamMap.put("strId", strStateId);//主键  id
        doRequestNormal(ApiManager.getInstance().changeReadState(textParamMap), 2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto:
                operationHandle();
                break;
        }
        super.onClick(v);
    }

    private void operationHandle() {
        String gotoValue = goto_Button.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromRemind", isFromRemindList);
        bundle.putString("strId", strId);
        if (getString(R.string.string_handle).equals(gotoValue)) {
            openActivity(2);
        } else if (getString(R.string.string_sign_for).equals(gotoValue)) {
            showConfirmDialog(6, strId);
        } else if (getString(R.string.string_review).equals(gotoValue)) {
            showActivity(activity, Proposal_AuditActivity.class, bundle, 0);
        } else if (getString(R.string.string_remind_to_handle).equals(gotoValue)) {
            showConfirmDialog(1, strId);
        } else if (getString(R.string.string_urging_members_to_give_feedback).equals(gotoValue)) {
            showConfirmDialog(4, strId);
        } else if (getString(R.string.string_reminder_sign_in).equals(gotoValue)) {
            showConfirmDialog(2, strId);
        } else if (getString(R.string.string_urge).equals(gotoValue)) {
            showConfirmDialog(3, strId);
        } else if (getString(R.string.string_handle_again).equals(gotoValue)) {
            showConfirmDialog(5, strId);
        } else if (getString(R.string.string_second).equals(gotoValue) || getString(R.string.string_joint_confirm).equals(gotoValue)) {//doReportReply(strId);
            bundle.putString("strAttend", strAttend);
            showActivity(activity, Proposal_JoinAffirmActivity.class, bundle, 0);
        } else if (getString(R.string.string_member_feedback).equals(gotoValue)) {
            bundle.putString("strMemReturnAttitude", zxtaViewBean.getStrMemReturnAttitude());
            bundle.putString("strMemReturnAttitudeTxt", zxtaViewBean.getStrMemReturnAttitudeTxt());
            bundle.putString("strMemReturnIdea", zxtaViewBean.getStrMemReturnIdea());
            bundle.putString("strMemReturnTxt", zxtaViewBean.getStrMemReturnTxt());
            bundle.putString("strMemReturnMainTxt", zxtaViewBean.getStrMemReturnMainTxt());
            //TODO  委员反馈
//            showActivity(activity, Proposal_FeedbackActivity.class, bundle, 0);
            showActivity(activity, ProposalMemberFeedbackActivity.class, bundle, 0);
        } else if (getString(R.string.string_delayed_audit).equals(gotoValue)) {
            bundle.putString("strId", strId);
            showActivity(activity, Proposal_DelayAuditActivity.class, bundle, 0);
        } else if (getString(R.string.string_hand_over).equals(gotoValue)) {
            if (isUnit) {
                bundle.putString("strCaseMotionId", strId);
                bundle.putBoolean("isUnit", true);
            }
            showActivity(activity, Proposal_HandleActivity.class, bundle, 0);
        } else if (getString(R.string.string_operation).equals(gotoValue)) {
            mQuickCustomPopup
                    .showAnim(null)
                    .dismissAnim(null)
                    .anchorView(goto_Button)
                    .offset(-5, -10)
                    .dimEnabled(true)
                    .gravity(Gravity.BOTTOM)
                    .show();
        } else if (getString(R.string.string_edit).equals(gotoValue)) {
            showActivity(activity, NewProposalAddActivity.class, bundle, 0);
        }
    }

    /**
     * 发送提醒
     */
    private void sendRemind(String strCaseId) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strCaseId);
        doRequestNormal(ApiManager.getInstance().dealSendRemind(textParamMap), -1);
    }

    /**
     * 不予立案操作接口
     */
    private void proposalRefused(String strId) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().dealSendRemind(textParamMap), -2);
    }

    /**
     * 催办
     */
    private void sendUrge(String strId) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().sednUrge(textParamMap), -1);
    }

    /**
     * 催委员反馈
     */
    private void sendUrgeFeedBack(String strId) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().sendUrgeFeedBack(textParamMap), -1);
    }

    /**
     * 催办
     */
    private void sendTwoHandler(String strCaseId) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strCaseMotionId", strCaseId);
        doRequestNormal(ApiManager.getInstance().sendTwoHandler(textParamMap), -2);
    }

    /**
     * 政协提案详情
     */
    private void getZxtaDetail() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        if (zxtaType == DefineUtil.zxta_signIn || zxtaType == DefineUtil.zxta_lmta) { // 提案签收/联名提案
            textParamMap.put("strId", strCaseId);
        } else {
            textParamMap.put("strId", strId);
        }
        doRequestNormal(ApiManager.getInstance().getProposalViewServlet(textParamMap), 0);
    }

    /**
     * 签收提案接口
     */
    private void doSign(String strId) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId); // 提案主键id
        doRequestNormal(ApiManager.getInstance().dealProposalSign(textParamMap), -2);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        switch (what) {
            case -2:
                prgDialog.closePrgDialog();
                try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                    //toastMessage = "操作成功！";
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case -1:
                prgDialog.closePrgDialog();
                try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 0:
                prgDialog.closePrgDialog();
                try {
                    zxtaViewBean = (ProposalViewBean) obj;
                    strHbFinished = zxtaViewBean.getStrHbFinished();
                    title_TextView.setText(zxtaViewBean.getStrTitle());
                    caseNo_TextView.setText(zxtaViewBean.getStrNo());
                    date_TextView.setText(zxtaViewBean.getDtPubDate());
                    date_TextView.setVisibility(View.GONE);
                    Proposal_Detail_VPAdapter adapter = new Proposal_Detail_VPAdapter(getSupportFragmentManager(), strFlowCaseDistType, activity, "4");
                    ArrayList<String> baseInfoList = new ArrayList<>();
                    baseInfoList.add(getString(R.string.string_proposal_category) + GIStringUtil.nullToEmpty(zxtaViewBean.getStrType()));
                    baseInfoList.add(getString(R.string.string_proponent) + zxtaViewBean.getStrSourceName());
                    baseInfoList.add(getString(R.string.string_job_title) + zxtaViewBean.getStrDuty());
                    baseInfoList.add(getString(R.string.string_contact_number) + zxtaViewBean.getStrMobile());
                    baseInfoList.add(getString(R.string.string_submit_date) + zxtaViewBean.getDtPubDate());
                    baseInfoList.add(getString(R.string.string_proposal_status) + zxtaViewBean.getStrState());
                    if (GIStringUtil.isNotBlank(zxtaViewBean.getStrHandleState())) {
                        baseInfoList.add(getString(R.string.string_processing_status) + zxtaViewBean.getStrHandleState());
                    }

                    String strJoinState = "0".equals(zxtaViewBean.getStrJointly()) ? "否" : "是";
                    baseInfoList.add(getString(R.string.string_joint_proposal) + strJoinState);
                    if ("1".equals(zxtaViewBean.getStrJointly())) {
                        if (intUserRole != 0 || "1".equals(zxtaViewBean.getIsOwner())) {
                            baseInfoList.add(getString(R.string.string_joint_celebrity) + zxtaViewBean.getStrJointlyMem() + "|" + true + "|" + zxtaViewBean.getIntJointlyMem());
                        } else {
                            baseInfoList.add(getString(R.string.string_joint_celebrity) + zxtaViewBean.getStrJointlyMem());

                        }
                    }
                    if (GIStringUtil.isNotBlank(zxtaViewBean.getStrWriterName())) {
                        baseInfoList.add(getString(R.string.string_lead_author_with_line) + zxtaViewBean.getStrWriterName());
                    }
                    if (AppConfigUtil.isUseSupportMotion(activity)) {
                        String strSupportState = "0".equals(zxtaViewBean.getStrSupportMotion()) ? "否" : "是";
                        baseInfoList.add(getString(R.string.string_second_proposal) + strSupportState);
                        if ("1".equals(zxtaViewBean.getStrSupportMotion())) {
                            baseInfoList.add(getString(R.string.string_second_person) + zxtaViewBean.getStrSupportMotionName() + "|" + true + "|" + zxtaViewBean.getIntSupportMotionName());
                        }
                    }
                    baseInfoList.add(getString(R.string.string_key_proposals) + zxtaViewBean.getStrKeyName());
                    baseInfoList.add(getString(R.string.string_proposal_in_the_meeting) + zxtaViewBean.getStrInMeet());
                    if ("1".equals(zxtaViewBean.getStrShowSurvey())) {  //和iOS保持一直 不再判断zxtaViewBean.getStrDiaoyanguocheng是否为空
                        baseInfoList.add(getString(R.string.string_research_process_with_line) + (GIStringUtil.isNotBlank(zxtaViewBean.getStrSurveyProcess()) ? zxtaViewBean.getStrSurveyProcess() : ""));
                    }
                    adapter.setStrId(zxtaViewBean.getStrId());
                    adapter.setBaseInfoList(baseInfoList); // 基本信息数据
                    /* 正文内容数据 */
                    adapter.setReason(zxtaViewBean.getStrReason());
                    adapter.setStrReasonTitle(zxtaViewBean.getStrReasonTitle());
                    if (GIStringUtil.isNotBlank(zxtaViewBean.getStrWay())) {
                        adapter.setWay(zxtaViewBean.getStrWay().replaceAll("<br/>", "\n"));
                        adapter.setStrWayTitle(zxtaViewBean.getStrWayTitle());
                    } else {
                        adapter.setWay("");
                    }
                    //adapter.setWorker(isWorker);
                    adapter.setTransactBeens(zxtaViewBean.getObjDispenseList()); // 办理情况数据

                    adapter.setMemOpinions(zxtaViewBean.getCaseFeedBackObj());//委员反馈
                    //adapter.setHandleIdea(zxtaViewBean.getStrHandleIdea()); // 领导批示审签意见数据
                    adapter.setJoinOpinions(zxtaViewBean.getObjMotionApprovalList()); //领导批示数据
                    adapter.setObjFeedBackListBeanList(zxtaViewBean.getObjFeedBackList());
                    adapter.setStrFeedBackAllState(zxtaViewBean.getStrFeedBackAllState());
                    if (null != zxtaViewBean.getObjDispenseList()
                            && zxtaViewBean.getObjDispenseList().size() > 0
                            && null != zxtaViewBean.getObjFeedBackList()
                            && zxtaViewBean.getObjFeedBackList().size() > 0) {
                        adapter.setProposalDetailShowTab("4");
                    } else if (null != zxtaViewBean.getObjDispenseList()
                            && zxtaViewBean.getObjDispenseList().size() > 0
                            && (null == zxtaViewBean.getObjFeedBackList()
                            || zxtaViewBean.getObjFeedBackList().size() <= 0)) {
                        adapter.setProposalDetailShowTab("2");
                    } else if ((null == zxtaViewBean.getObjDispenseList()
                            || zxtaViewBean.getObjDispenseList().size() <= 0)
                            && null != zxtaViewBean.getObjFeedBackList()
                            && zxtaViewBean.getObjFeedBackList().size() > 0) {
                        adapter.setProposalDetailShowTab("3");
                    } else {
                        adapter.setProposalDetailShowTab("1");
                    }
                    pager.setAdapter(adapter);
//                    indicator.setViewPager(pager);
                    indicator.setupWithViewPager(pager);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 1:
                prgDialog.closePrgDialog();
                try {
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 2:
               /* try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                    if (baseGlobal.getStrRlt().equals("true")) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        toastMessage = baseGlobal.getStrError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }*/
                break;
            default:
                break;
        }

        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    class SimpleCustomPop extends BasePopup<SimpleCustomPop> {
        private ListView listView;
        private String[] valueArray;

        public SimpleCustomPop(Context context) {
            super(context);
//            setCanceledOnTouchOutside(false);
            valueArray = new String[]{getString(R.string.string_handle), getString(R.string.string_apply_for_an_extension)};
        }

        @Override
        public View onCreatePopupView() {
            View inflate = View.inflate(mContext, R.layout.popup_sessioncode_list, null);
            listView = inflate.findViewById(R.id.listview);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(GIDensityUtil.dip2px(activity, 120), LinearLayout.LayoutParams.WRAP_CONTENT);
            listView.setLayoutParams(layoutParams);
            return inflate;
        }

        @Override
        public void setUiBeforShow() {
            listView.setAdapter(new SessionCode_LVAdapter(activity, valueArray));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String value = (String) adapterView.getItemAtPosition(i);
                    //1提案办理申退 2办结 3申退审核 4交办 5提案交办申退
                    if (getString(R.string.string_handle).equals(value)) {
                        openActivity(2);
                    } else if (getString(R.string.string_apply_for_an_extension).equals(value)) {
                        openActivity(6);
                    }
                    dismiss();
                }
            });
        }
    }

    /**
     * 对话框
     */
    public void showConfirmDialog(final int type, final String strId) {
        String title = "";
        if (type == 1) {
            title = getString(R.string.string_confirm_the_need_to_remind);
        } else if (type == 2) {
            title = getString(R.string.string_definitely_need_to_remind_sign);
        } else if (type == 3) {
            title = getString(R.string.string_determine_the_need_to_urge_the_undertaker_to_handle);
        } else if (type == 4) {
            title = getString(R.string.string_determine_the_need_to_call_for_feedback);
        } else if (type == 5) {
            title = getString(R.string.string_are_you_sure_you_need_to_do_it_again);
        } else if (type == 6) {
            title = getString(R.string.string_are_you_sure_you_need_to_sign_the_proposal);
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
                        dialog.superDismiss();
                        if (6 == type) {
                            doSign(strId);
                        } else if (5 == type) {
                            sendTwoHandler(strCaseId);
                        } else if (4 == type) {
                            sendUrgeFeedBack(strId);
                        } else if (3 == type) {
                            sendUrge(strId);
                        } else {
                            sendRemind(strId);
                        }
                    }
                }
        );
    }

    /**
     * activity跳转
     *
     * @param type 1提案办理申退 2办理 3申退审核 4交办 5提案交办申退 6延期申请 7-立案 8-不予立案
     */
    private void openActivity(int type) {
        Bundle bundle = new Bundle();
        Class clazz = null;
        switch (type) {
            case 2: // 提案办理
                if ("1".equals(strHbFinished)) {
                    showConfirmDialog();
                } else {
                    bundle.putString("strId", strId);
                    bundle.putString("strHandlerTypeName", strHandlerTypeName);
                    clazz = Proposal_DealActivity.class;
                }
                break;
            case 6:
                bundle.putString("strId", strId);
                bundle.putString("strReportDate", strHandlerLimitDate);
                clazz = Proposal_DelayApplyActivity.class;
                break;
        }
        if (null != clazz) {
            showActivity(activity, clazz, bundle, 0);
        }
    }

    public void showConfirmDialog() {
        String title = getString(R.string.string_organizer_dialog_tips);
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.string_tips)).content(title).btnText(getString(R.string.string_confirm)).showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.btnNum(1);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }
        );
    }

}
