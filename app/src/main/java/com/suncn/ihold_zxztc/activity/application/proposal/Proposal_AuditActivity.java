package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.ChooseListActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Proposal_Audit_ProSign_RVAdapter;
import com.suncn.ihold_zxztc.adapter.Proposal_Audit_Pro_RVAdapter;
import com.suncn.ihold_zxztc.bean.AuditInfoListBean;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.MotionDistTypeServletBean;
import com.suncn.ihold_zxztc.bean.ProposalGoTypeListBean;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.AlwaysMarqueeTextView;
import com.suncn.ihold_zxztc.view.MenuItemEditLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import skin.support.content.res.SkinCompatResources;

/**
 * 待审核页面
 */
public class Proposal_AuditActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;//审核意见EasyRecyclerView
    @BindView(id = R.id.presign_recyclerView)
    private RecyclerView preSign_RecyclerView;//预签收EasyRecyclerView
    @BindView(id = R.id.ll_show)
    private LinearLayout show_LinearLayout;
    @BindView(id = R.id.ll_result)
    private LinearLayout result_LinearLayout;//审核结果LinearLayout
    @BindView(id = R.id.tv_classification,click = true)
    private MenuItemEditLayout classification_TextView;//提案分类TextView
    @BindView(id = R.id.tv_arrow)
    private TextView arrow_TextView;//提案分类箭头
    @BindView(id = R.id.ll_type)
    private LinearLayout type_LinearLayout;//分发类型LinearLayout
    @BindView(id = R.id.ll_system)
    private LinearLayout system_LinearLayout;//承办系统LinearLayout
    @BindView(id = R.id.tv_system, click = true)
    private AlwaysMarqueeTextView system_TextView;//承办系统TextView
    @BindView(id = R.id.ll_host)
    private LinearLayout host_LinearLayout;//主办单位LinearLayout
    @BindView(id = R.id.ll_unit)
    private LinearLayout unit_LinearLayout;//会办单位LinearLayout
    @BindView(id = R.id.rb_pass)
    private RadioButton pass_RadioButton;//立案并分发RadioButton
    @BindView(id = R.id.rb_refuse)
    private RadioButton refuse_RadioButton;//不予立案RadioButton
    @BindView(id = R.id.rb_ismeet)
    private RadioButton isMeet_RadioButton;//是会中提案RadioButton
    @BindView(id = R.id.rb_notmeet)
    private RadioButton notMeet_RadioButton;//不是会中提案RadioButton
    @BindView(id = R.id.rb_agreePub)
    private RadioButton agreePub_RadioButton;//同意公开
    @BindView(id = R.id.rb_refusePub)
    private RadioButton rb_refusePub;//不同意公开
    @BindView(id = R.id.rb_yes)
    private RadioButton yes_RadioButton;//重点提案RadioButton
    @BindView(id = R.id.ll_pass)
    private LinearLayout pass_LinearLayout;//立案分发展示页面
    @BindView(id = R.id.tv_go,click = true)
    private MenuItemEditLayout go_TextView;//提案去向TextView
    @BindView(id = R.id.tv_reason,click = true)
    private MenuItemEditLayout tvReason;//不予立案原因TextView
    @BindView(id = R.id.ll_important)
    private LinearLayout llImportant;//重点提案布局
    @BindView(id = R.id.ll_choose_unit, click = true)
    private LinearLayout chooseUnit_LinearLayout;//选择单位LinearLayout
    @BindView(id = R.id.tv_choose_unit)
    private TextView chooseUnit_TextView;//选择单位TextView
    @BindView(id = R.id.ll_idea)
    private LinearLayout idea_LinearLayout;//具体意见LinearLayout
    @BindView(id = R.id.tv_mike, click = true)
    private TextView mike_TextView; // 语音识别按钮
    @BindView(id = R.id.et_idea)
    private GIEditText idea_EditText;//具体意见EditText
    @BindView(id = R.id.tv_type, click = true)//分发类型TextView
    private TextView type_TextView;
    @BindView(id = R.id.tv_do_type, click = true)//办理类型TextView
    private TextView doType_TextView;
    @BindView(id = R.id.tv_host, click = true)
    private AlwaysMarqueeTextView host_TextView;//主办单位
    @BindView(id = R.id.tv_unit, click = true)
    private AlwaysMarqueeTextView unit_TextView;//会办单位
    @BindView(id = R.id.ll_do_type, click = true)
    private LinearLayout doType_LinearLayout;//办理类型LinearLayout
    @BindView(id = R.id.ll_send)   //滁州市政协 提案分发
    private LinearLayout ll_send;
    @BindView(id = R.id.rgSend)
    private RadioGroup rgSend;
    private String[] menuArray;

    private String type;//分发类型名称
    private String doType;//办理类型
    private String host;//主办单位
    private String unit;//分办单位
    private String system;//承办系统
    private String idea;//意见
    private String go;//提案去向
    private String strCheckResultState = "";//1(立案或立案并分发)、2不予立案)
    private String[] goTypeArray;//提案去向数组
    private String[] goTypeReasonArray;//不予立案数组
    private ArrayList<ProposalGoTypeListBean.ProposalGoType> proposalGoType;
    private String goTypeReasonCode;//不予立案理由编码
    private String goTypeCode = "";//去向编码
    private String strChooseSystem = "";//选择的承办系统
    private String strChooseHost = "";//选择的主办单位
    private String strChooseUnit = "";//选择的会办单位
    private String strUnit = "";//不予立案转有关部门处理的单位
    private String strClassification = "";
    private String strCaseTypeId = "";
    private String strId;//主键id
    private Proposal_Audit_Pro_RVAdapter adapter;
    private Proposal_Audit_ProSign_RVAdapter proSignListAdapter;
    private boolean checkIsFinishCheck = false;//true立案并分发,false:立案
    private String strCaseMotionSessionId;
    private AuditInfoListBean auditInfoListBean;
    private String strSubmitFlow;//0提交至待接收，1提交至待审核 （在机关委审核时用到了）
    private String strFlowType;
    private String mStrSendType;  //提案分发类型

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0://承办系统
                    strChooseSystem = data.getExtras().getString("strChooseUnitId");
                    system_TextView.setText(Utils.showAddress(strChooseSystem));
                    break;
                case 1://主办单位
                    strChooseHost = data.getExtras().getString("strChooseUnitId");
                    host_TextView.setText(Utils.showAddress(strChooseHost));
                    break;
                case 2://会办单位
                    strChooseUnit = data.getExtras().getString("strChooseUnitId");
                    unit_TextView.setText(Utils.showAddress(strChooseUnit));
                    break;
                case 3://分类
                    if (data.getExtras().getString("strClassification") != null) {
                        strClassification = data.getExtras().getString("strClassification");
                        strCaseTypeId = data.getExtras().getString("strCaseTypeId");
                        classification_TextView.setValue(strClassification + "");
                    }
                    break;
                case 4:
                    strUnit = data.getExtras().getString("strChooseUnitId");
                    chooseUnit_TextView.setText(Utils.showAddress(strUnit));
                    break;
                case 5:
                    goTypeReasonCode = data.getExtras().getString("goTypeReasonCode");
                    tvReason.setValue(data.getExtras().getString("strGoTypeReason"));
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_audit);
    }

    @Override
    public void initData() {
        setStatusBar();
        super.initData();
        initRecyclerView();
        initPreSignRecyclerView();
        findViewById(R.id.view_place).setVisibility(View.GONE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        strSubmitFlow = GISharedPreUtil.getString(activity, "strSubmitFlow");
        strFlowType = GISharedPreUtil.getString(activity, "strFlowType");
        if (strFlowCaseDistType.equals("2")) {
            show_LinearLayout.setVisibility(View.VISIBLE);
        } else {
            show_LinearLayout.setVisibility(View.GONE);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setHeadTitle(getString(R.string.string_proposal_review));
            strId = bundle.getString("strId");
            goto_Button.setVisibility(View.VISIBLE);
            goto_Button.setText(getString(R.string.string_submit));
            goto_Button.refreshFontType(activity, "2");
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            getAuditInfo();
        }
        if (AppConfigUtil.isUseCbxt(activity)) {
            if (ProjectNameUtil.isGYSZX(activity) || ProjectNameUtil.isGZSZX(activity)) {
                menuArray = new String[]{getString(R.string.string_undertaking_system)};
            } else {
                menuArray = new String[]{getString(R.string.string_undertaker), getString(R.string.string_undertaking_system)};
            }
        } else {
            menuArray = new String[]{getString(R.string.string_undertaker)};
        }
        if (menuArray.length == 1) {
            type_TextView.setText(menuArray[0]);
            if (menuArray[0].contains(getString(R.string.string_system))) {
                system_LinearLayout.setVisibility(View.VISIBLE);
                host_LinearLayout.setVisibility(View.GONE);
                unit_LinearLayout.setVisibility(View.GONE);
                doType_LinearLayout.setVisibility(View.GONE);
            } else {
                doType_LinearLayout.setVisibility(View.VISIBLE);
                system_LinearLayout.setVisibility(View.GONE);
                host_LinearLayout.setVisibility(View.VISIBLE);
                //unit_LinearLayout.setVisibility(View.VISIBLE);
            }
        }

        if (strSubmitFlow.equals("1")) {
            classification_TextView.setOnClickListener(this);
            arrow_TextView.setVisibility(View.VISIBLE);
        } else {
            arrow_TextView.setVisibility(View.GONE);
        }
        if (ProjectNameUtil.isJMSZX(activity)) {
            llImportant.setVisibility(View.GONE);
        } else {
            llImportant.setVisibility(View.VISIBLE);
        }
        if (ProjectNameUtil.isLWQZX(activity)) {
            idea_EditText.setVisibility(View.GONE);
        } else {
            idea_EditText.setVisibility(View.VISIBLE);
            idea_EditText.setTextView(findViewById(R.id.tv_count));
        }
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        adapter = new Proposal_Audit_Pro_RVAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void initPreSignRecyclerView() {
        Utils.initEasyRecyclerView(activity, preSign_RecyclerView, false, false, R.color.line_bg_white, 0.5f, 0);
        proSignListAdapter = new Proposal_Audit_ProSign_RVAdapter(this);
        preSign_RecyclerView.setLayoutManager(new LinearLayoutManager(activity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        preSign_RecyclerView.setAdapter(proSignListAdapter);
    }

    /**
     * 获取审核信息
     */
    private void getAuditInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().getProposalAuditInfo(textParamMap), -1);
    }

    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        switch (what) {
            case -1:
                prgDialog.closePrgDialog();
                try {
                    auditInfoListBean = (AuditInfoListBean) obj;
                    strCaseMotionSessionId = GIStringUtil.nullToEmpty(auditInfoListBean.getStrCaseMotionSessionId());
                    checkIsFinishCheck = auditInfoListBean.isCheckIsFinishCheck();
                    if (checkIsFinishCheck) {
                        pass_RadioButton.setText(R.string.string_file_and_distribute);
                    } else {
                        pass_RadioButton.setText(R.string.string_put_on_record);
                    }
                    List<AuditInfoListBean.AuditInfo> auditInfo = auditInfoListBean.getCaseCheckList();
                    if (auditInfo != null && auditInfo.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setList(auditInfo);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                    List<AuditInfoListBean.ProSign> proSign = auditInfoListBean.getPreHandlerList();
                    if (proSign != null && proSign.size() > 0) {
                        preSign_RecyclerView.setVisibility(View.VISIBLE);
                        proSignListAdapter.setList(proSign);
                    } else {
                        preSign_RecyclerView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 1:
                prgDialog.closePrgDialog();
                try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                    toastMessage = getString(R.string.string_proposal_review_successful);
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isRemove", checkIsFinishCheck);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 0:
                prgDialog.closePrgDialog();
                try {
                    ProposalGoTypeListBean proposalGoTypeListBean = (ProposalGoTypeListBean) obj;
                    proposalGoType = proposalGoTypeListBean.getObjList();
                    goTypeArray = new String[proposalGoType.size()];
                    for (int i = 0; i < proposalGoType.size(); i++) {
                        goTypeArray[i] = proposalGoType.get(i).getStrName();
                    }
                    showChooseDialog(goTypeArray, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 2:
                prgDialog.closePrgDialog();
                try {
                    ProposalGoTypeListBean proposalGoTypeListBean = (ProposalGoTypeListBean) obj;
                    proposalGoType = proposalGoTypeListBean.getObjList();
                    goTypeReasonArray = new String[proposalGoType.size()];
                    for (int i = 0; i < proposalGoType.size(); i++) {
                        goTypeReasonArray[i] = proposalGoType.get(i).getStrName();
                    }
                    showChooseDialog(goTypeReasonArray, 3);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 3: //滁州市政协 获取提案分发类型

                try {
                    MotionDistTypeServletBean motionDistTypeServletBean = (MotionDistTypeServletBean) obj;
                    List<MotionDistTypeServletBean.ObjListBean> objList = motionDistTypeServletBean.getObjList();
                    rgSend.removeAllViews();
                    rgSend.clearCheck();
                    for (int i = 0; i < objList.size(); i++) {
                        RadioButton radioButton = (RadioButton) LinearLayout.inflate(activity, R.layout.radio_button, null);
                        radioButton.setText(objList.get(i).getStrName());
                        radioButton.setId(i);
                        radioButton.setOnCheckedChangeListener((compoundButton, b) -> {
                            mStrSendType = radioButton.getText().toString().trim();
                        });
                        rgSend.addView(radioButton);
                        rgSend.check(0);  //设置默认选中第一个
                    }
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


    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle;
        MikeEditTextUtil util;
        //是否是会办
        boolean isHB = doType_TextView.getText().toString().trim().equals(getString(R.string.string_will_do)) ? true : false;
        switch (v.getId()) {
            case R.id.tv_mike:
                util = new MikeEditTextUtil(activity, idea_EditText);
                util.viewShow();
                break;
            case R.id.ll_feedback:
                bundle = new Bundle();
                bundle.putString("strId", strId);
                //showActivity(activity, PreFeedBackActivity.class, bundle);
                break;
            case R.id.tv_classification:
                if (arrow_TextView.getVisibility() == View.VISIBLE) {
                    bundle = new Bundle();
                    bundle.putString("strId", strId);
                    bundle.putString("strClassification", strClassification);
                    bundle.putString("strCaseTypeId", strCaseTypeId);
                    bundle.putBoolean("isBack", true);
                    bundle.putString("strCaseMotionSessionId", strCaseMotionSessionId);
                    showActivity(activity, ProposalClassificationActivity.class, bundle, 3);
                }
                break;
            case R.id.tv_system://承办系统
                bundle = new Bundle();
                bundle.putBoolean("isSystem", true);
                bundle.putBoolean("isSingle", true);
                bundle.putString("strChooseUnitId", strChooseSystem);
                bundle.putString("headTitle", getString(R.string.string_undertaking_system));
                showActivity(activity, Choose_UnitActivity.class, bundle, 0);
                break;
            case R.id.tv_host://主办单位
                doType = doType_TextView.getText().toString().trim();
                bundle = new Bundle();
                bundle.putString("strChooseUnitId", strChooseHost);
                bundle.putString("strHasChoose", strChooseUnit);
                bundle.putBoolean("isHB", isHB);
                bundle.putBoolean("isSystem", false);
                if (doType.equals(getString(R.string.string_branch_office))) {
                    bundle.putBoolean("isSingle", false);
                } else {
                    bundle.putBoolean("isSingle", true);
                }
                bundle.putString("headTitle", getString(R.string.string_organizer_main));
                showActivity(activity, Choose_UnitActivity.class, bundle, 1);
                break;
            case R.id.tv_unit://会办单位
                bundle = new Bundle();
                bundle.putBoolean("isHB", isHB);
                bundle.putString("strHasChoose", strChooseHost);
                bundle.putString("strChooseUnitId", strChooseUnit);
                bundle.putBoolean("isSystem", false);
                bundle.putBoolean("isSingle", false);
                bundle.putString("headTitle", getString(R.string.string_organizer));
                showActivity(activity, Choose_UnitActivity.class, bundle, 2);
                break;
            case R.id.tv_go://提案去向
                getGoType();
                break;
            case R.id.ll_reason://不予立案理由
                bundle = new Bundle();
                bundle.putString("goTypeReasonCode", goTypeReasonCode);
                bundle.putString("strTitle", getString(R.string.string_choose_not_to_file_a_case));
                showActivity(activity, ChooseListActivity.class, bundle, 5);
                break;
            case R.id.tv_type://分发类型
                if (ProjectNameUtil.isGYSZX(activity) || ProjectNameUtil.isGZSZX(activity)) {
                    menuArray = new String[]{getString(R.string.string_undertaking_system)};
                } else if (AppConfigUtil.isUseCbdw(activity) && AppConfigUtil.isUseCbxt(activity)) {
                    menuArray = new String[]{getString(R.string.string_undertaker), getString(R.string.string_undertaking_system)};
                } else if (AppConfigUtil.isUseCbdw(activity) && !AppConfigUtil.isUseCbxt(activity)) {
                    menuArray = new String[]{getString(R.string.string_undertaker)};
                } else if (!AppConfigUtil.isUseCbdw(activity) && AppConfigUtil.isUseCbxt(activity)) {
                    menuArray = new String[]{getString(R.string.string_undertaking_system)};
                }
                if (menuArray.length > 1)
                    showChooseDialog(menuArray, 0);
                break;
            case R.id.tv_do_type://办理类型
                menuArray = new String[]{getString(R.string.string_host), getString(R.string.string_will_do), getString(R.string.string_branch_office)};
                showChooseDialog(menuArray, 1);
                break;
            case R.id.btn_goto:
                type = type_TextView.getText().toString().trim();
                doType = doType_TextView.getText().toString().trim();
                host = host_TextView.getText().toString().trim();
                unit = unit_TextView.getText().toString().trim();
                system = system_TextView.getText().toString().trim();
                idea = idea_EditText.getText().toString().trim();
                go = go_TextView.getValue().toString().trim();
                if (GIStringUtil.isBlank(strCheckResultState) && (!ProjectNameUtil.isCZSZX(activity) || result_LinearLayout.isShown())) {
                    showToast(getString(R.string.string_please_select_the_review_result));
                    result_LinearLayout.requestFocus();
                } else if (classification_TextView.isShown() && strSubmitFlow.equals("1") && GIStringUtil.isBlank(classification_TextView.getValue().toString())) {//荔湾区提案分类做必填 滁州市政协提案分类必填
                    showToast(getString(R.string.string_please_select_a_proposal_category));
                    classification_TextView.requestFocus();
                } else if (show_LinearLayout.isShown() && type_LinearLayout.isShown() && GIStringUtil.isBlank(type)) {
                    showToast(getString(R.string.string_please_select_a_distribution_type));
                    type_LinearLayout.requestFocus();
                } else if (show_LinearLayout.isShown() && doType_LinearLayout.isShown() && GIStringUtil.isBlank(doType)) {
                    showToast(getString(R.string.string_please_select_the_type_of_application));
                    doType_LinearLayout.requestFocus();
                } else if (show_LinearLayout.isShown() && host_LinearLayout.isShown() && GIStringUtil.isBlank(host)) {
                    showToast(getString(R.string.string_please_select_the_organizer));
                    host_LinearLayout.requestFocus();
                } else if (show_LinearLayout.isShown() && unit_LinearLayout.isShown() && GIStringUtil.isBlank(unit)) {
                    showToast(getString(R.string.string_please_select_the_organization));
                    host_LinearLayout.requestFocus();
                } else if (show_LinearLayout.isShown() && system_LinearLayout.isShown() && GIStringUtil.isBlank(system)) {
                    showToast(getString(R.string.string_please_select_undertaking_system));
                    system_LinearLayout.requestFocus();
                } else if (go_TextView.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(go)) {
                    showToast(getString(R.string.string_please_select_a_proposal));
                    go_TextView.requestFocus();
                } else if (tvReason.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(goTypeReasonCode)) {
                    showToast(getString(R.string.string_please_choose_not_to_file_a_case));
                    tvReason.requestFocus();
                } else if (chooseUnit_LinearLayout.isShown() && GIStringUtil.isBlank(strUnit)) {
                    showToast(getString(R.string.string_please_select_unit));
                } else if (!ProjectNameUtil.isLWQZX(activity) && idea_LinearLayout.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(idea)) {
                    showToast(getString(R.string.string_please_fill_in_specific_comments));
                    idea_EditText.requestFocus();
                } else if (ProjectNameUtil.isCZSZX(activity) && GIStringUtil.isBlank(mStrSendType) && ll_send.isShown()) {
                    showToast(getString(R.string.string_please_select_a_proposal_distribution_unit));
                } else {
                    doAudit();
                }
                break;
            case R.id.ll_choose_unit:
                bundle = new Bundle();
                bundle.putString("strChooseUnitId", strChooseHost);
                bundle.putString("strHasChoose", strChooseUnit);
                bundle.putBoolean("isHB", isHB);
                bundle.putBoolean("isSystem", false);
                bundle.putBoolean("isSingle", true);
                bundle.putString("headTitle", getString(R.string.string_unit));
                showActivity(activity, Choose_UnitActivity.class, bundle, 4);
                break;
            default:
                break;
        }

    }

    /**
     * 获取提案去向列表
     */
    private void getGoType() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getProposalGotoList(textParamMap), 0);
    }


    /**
     * 提交审核
     */
    private void doAudit() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        textParamMap.put("strCheckResultState", strCheckResultState);
        textParamMap.put("strCheckIdea", GIStringUtil.nullToEmpty(idea));//输入意见
        //textParamMap.put("strType", "1");//1-审核0-详情

        if (strCheckResultState.equals("1") || ProjectNameUtil.isCZSZX(activity)) {
            if (isMeet_RadioButton.isChecked()) {
                textParamMap.put("strInMeet", "1");//会中提案 1会中 0 非会中
            } else {
                textParamMap.put("strInMeet", "0");//会中提案 1会中 0 非会中
            }
            textParamMap.put("strCaseTypeId", strCaseTypeId);//分类id
            if (agreePub_RadioButton.isChecked()) {
                textParamMap.put("intAgreePub", "1");//1同意0不同意公开
            } else {
                textParamMap.put("intAgreePub", "0");
            }
            if (yes_RadioButton.isChecked()) {
                textParamMap.put("strKeyName", "1");//1是重点提案0不是
            } else {
                textParamMap.put("strKeyName", "0");
            }
            if (type.contains(getString(R.string.string_system))) {
                textParamMap.put("intDistType", "1");//1-承办系统，2-承办单位
                textParamMap.put("strCbxtUnit", strChooseSystem);//承办系统
            } else if (type.contains(getString(R.string.string_unit))) {
                textParamMap.put("intDistType", "2");
                if (doType.equals(getString(R.string.string_host))) {
                    textParamMap.put("strHandlerType", "1");//1-主办，2-会办，3-分办
                    textParamMap.put("strMainUnit", strChooseHost);//主办单位
                } else if (doType.equals(getString(R.string.string_will_do))) {
                    textParamMap.put("strHandlerType", "2");//1-主办，2-会办，3-分办
                    textParamMap.put("strMainUnit", strChooseHost);//主办单位
                    textParamMap.put("strMeetUnit", strChooseUnit);//会办单位
                } else {
                    textParamMap.put("strHandlerType", "3");//1-主办，2-会办，3-分办
                    textParamMap.put("strMainUnit", strChooseHost);//主办单位
                }
                // textParamMap.put("strMainUnit", strChooseHost);//主办单位
                //textParamMap.put("strMeetUnit", strChooseUnit);//会办单位
            }
            if (ProjectNameUtil.isCZSZX(activity)) {
                textParamMap.put("strType", "");
            }

        } else {
            textParamMap.put("strCheckResultType", goTypeCode);//不予立案编码
            textParamMap.put("strUnitId", GIStringUtil.nullToEmpty(strUnit));//承办单位
            if (ProjectNameUtil.isJMSZX(activity)) {
                textParamMap.put("strNoLianReason", goTypeReasonCode);
            }

        }

        doRequestNormal(ApiManager.getInstance().dealProposalAudit(textParamMap), 1);
    }

    @Override
    public void setListener() {
        super.setListener();
        refuse_RadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    strCheckResultState = "2";
                    pass_LinearLayout.setVisibility(View.GONE);
                    if (!ProjectNameUtil.isGZSZX(activity)) { // 贵州省政协不需要显示提案去向
                        go_TextView.setVisibility(View.VISIBLE);
                    }
                    if (ProjectNameUtil.isJMSZX(activity)) {
                        tvReason.setVisibility(View.VISIBLE);
                    } else {
                        idea_LinearLayout.setVisibility(View.VISIBLE);
                    }
                    idea_EditText.setVisibility(View.VISIBLE);
                    go_TextView.setValue(auditInfoListBean.getStrCheckResultTypeName());
                    goTypeCode = auditInfoListBean.getStrCheckResultType();
                    classification_TextView.setVisibility(View.GONE);
                    if (ProjectNameUtil.isCZSZX(activity)) {
                        ll_send.setVisibility(View.GONE);
                    }
                }
            }
        });
        pass_RadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    idea_EditText.setVisibility(View.VISIBLE);
                    if (!ProjectNameUtil.isLWQZX(activity) && !ProjectNameUtil.isCZSZX(activity)) {
                        idea_LinearLayout.setVisibility(View.VISIBLE);
                    }
                    boolean isAgreePub = auditInfoListBean.getIntAgreePub() == 1 ? true : false;
                    if (isAgreePub) {
                        agreePub_RadioButton.setChecked(true);
                        rb_refusePub.setChecked(false);
                    } else {
                        agreePub_RadioButton.setChecked(false);
                        rb_refusePub.setChecked(true);
                    }
                    boolean isYes = auditInfoListBean.getStrKeyName().equals("1") ? true : false;
                    boolean isMeet = "1".equals(auditInfoListBean.getStrInMeet()) ? true : false;
                    if (isMeet) {
                        isMeet_RadioButton.setChecked(true);
                        notMeet_RadioButton.setChecked(false);
                    } else {
                        isMeet_RadioButton.setChecked(false);
                        notMeet_RadioButton.setChecked(true);
                    }
                    if (isYes) {
                        yes_RadioButton.setChecked(true);
                    } else {
                        yes_RadioButton.setChecked(false);
                    }
                    //pass_RadioButton.setChecked(true);
                    strCheckResultState = "1";
                    if (ProjectNameUtil.isJMSZX(activity)) {
                        tvReason.setVisibility(View.GONE);
                    }
                    pass_LinearLayout.setVisibility(View.VISIBLE);
                    idea_EditText.setVisibility(View.VISIBLE);
                    go_TextView.setVisibility(View.GONE);
                    if (!ProjectNameUtil.isJMSZX(activity) && !ProjectNameUtil.isCZSZX(activity)) {//分发类型去除（江门的提案办理推送给第三方处理）
                        type_LinearLayout.setVisibility(View.VISIBLE);
                    }
                    classification_TextView.setVisibility(View.VISIBLE);
                    classification_TextView.setValue(auditInfoListBean.getStrCaseTypeName());
                    strClassification = auditInfoListBean.getStrCaseTypeName() + "";
                    strCaseTypeId = auditInfoListBean.getStrCaseTypeId();
                    boolean isSystem = auditInfoListBean.getIntDistType() == 1 ? true : false;
                    if (1 == auditInfoListBean.getIntDistType()) {
                        system_LinearLayout.setVisibility(View.VISIBLE);
                        host_LinearLayout.setVisibility(View.GONE);
                        unit_LinearLayout.setVisibility(View.GONE);
                        doType_LinearLayout.setVisibility(View.GONE);
                        type_TextView.setText(getString(R.string.string_undertaking_system));
                        system_TextView.setText(Utils.showAddress(auditInfoListBean.getStrCbxtUnit()));
                        strChooseSystem = auditInfoListBean.getStrCbxtUnit();
                    } else if (2 == auditInfoListBean.getIntDistType()) {
                        doType_LinearLayout.setVisibility(View.VISIBLE);
                        system_LinearLayout.setVisibility(View.GONE);
                        type_TextView.setText(getString(R.string.string_undertaker));
                        if (auditInfoListBean.getStrHandlerType().equals("1")) {
                            doType_TextView.setText(getString(R.string.string_host));
                        } else if (auditInfoListBean.getStrHandlerType().equals("2")) {
                            doType_TextView.setText(getString(R.string.string_will_do));
                        } else if (auditInfoListBean.getStrHandlerType().equals("3")) {
                            doType_TextView.setText(getString(R.string.string_branch_office));
                        }
                        if (doType_TextView.getText().toString().trim().contains(getString(R.string.string_will_do))) {
                            unit_LinearLayout.setVisibility(View.VISIBLE);
                            host_LinearLayout.setVisibility(View.VISIBLE);
                            unit_TextView.setText(Utils.showAddress(auditInfoListBean.getStrMeetUnit()));
                            strChooseUnit = auditInfoListBean.getStrMeetUnit();
                            host_TextView.setText(Utils.showAddress(auditInfoListBean.getStrMainUnit()));
                            strChooseHost = auditInfoListBean.getStrMainUnit();
                        } else {
                            unit_LinearLayout.setVisibility(View.GONE);
                            host_LinearLayout.setVisibility(View.VISIBLE);
                            host_TextView.setText(Utils.showAddress(auditInfoListBean.getStrMainUnit()));
                            strChooseHost = auditInfoListBean.getStrMainUnit();
                        }
                        // host_LinearLayout.setVisibility(View.VISIBLE);
                        //unit_LinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        if (!ProjectNameUtil.isJMSZX(activity) && !ProjectNameUtil.isCZSZX(activity)) {//分发类型去除（江门的提案办理推送给第三方处理）
                            type_LinearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    if (strFlowCaseDistType.equals("2")) {
                        show_LinearLayout.setVisibility(View.VISIBLE);
                        pass_LinearLayout.setVisibility(View.VISIBLE);
                        /*idea_EditText.setVisibility(View.VISIBLE);
                        idea_LinearLayout.setVisibility(View.VISIBLE);*/
                        go_TextView.setVisibility(View.GONE);
                        if (!ProjectNameUtil.isJMSZX(activity) && !ProjectNameUtil.isCZSZX(activity)) {//分发类型去除（江门的提案办理推送给第三方处理）
                            type_LinearLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        show_LinearLayout.setVisibility(View.GONE);
                        pass_LinearLayout.setVisibility(View.VISIBLE);
                        idea_EditText.setVisibility(View.VISIBLE);
                        /*idea_LinearLayout.setVisibility(View.VISIBLE);
                        go_LinearLayout.setVisibility(View.GONE);*/
                        type_LinearLayout.setVisibility(View.GONE);
                    }
                    if (ProjectNameUtil.isCZSZX(activity)) {
                        ll_send.setVisibility(View.VISIBLE);
                    }
                    if (checkIsFinishCheck) {
                        idea_LinearLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    private void showChooseDialog(final String[] menuArray, final int i) {

        final NormalListDialog normalListDialog = new NormalListDialog(activity, menuArray);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                String state = "";
                if (i == 0) {
                    state = menuArray[position];
                    type_TextView.setText(state);
                    if (state.contains(getString(R.string.string_system))) {
                        system_LinearLayout.setVisibility(View.VISIBLE);
                        host_LinearLayout.setVisibility(View.GONE);
                        unit_LinearLayout.setVisibility(View.GONE);
                        doType_LinearLayout.setVisibility(View.GONE);
                    } else {
                        doType_LinearLayout.setVisibility(View.VISIBLE);
                        if (doType_TextView.getText().toString().trim().contains(getString(R.string.string_will_do))) {
                            unit_LinearLayout.setVisibility(View.VISIBLE);
                            host_LinearLayout.setVisibility(View.VISIBLE);
                        } else {
                            unit_LinearLayout.setVisibility(View.GONE);
                            host_LinearLayout.setVisibility(View.VISIBLE);
                        }
                        if (GIStringUtil.isBlank(doType_TextView.getText().toString())) {
                            doType_TextView.setText(getString(R.string.string_host));
                        }
                        system_LinearLayout.setVisibility(View.GONE);
                        // host_LinearLayout.setVisibility(View.VISIBLE);
                        //unit_LinearLayout.setVisibility(View.VISIBLE);
                    }
                } else if (i == 1) {
                    state = menuArray[position];
                    if (!state.equals(doType_TextView.getText().toString().trim())) {
                        unit_TextView.setText("");
                        host_TextView.setText("");
                        strChooseUnit = "";
                        strChooseHost = "";
                    }
                    if (state.contains(getString(R.string.string_will_do))) {
                        unit_LinearLayout.setVisibility(View.VISIBLE);
                        host_LinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        unit_LinearLayout.setVisibility(View.GONE);
                        host_LinearLayout.setVisibility(View.VISIBLE);
                    }
                    doType_TextView.setText(state);
                    //leaveTypeId = leaveTypeBeans.get(position).getStrAbsentType();
                } else if (i == 2) {
                    state = menuArray[position];
                    go_TextView.setValue(state);
                    goTypeCode = proposalGoType.get(position).getStrCode();
                    if (goTypeCode.equals("5")) {
                        chooseUnit_LinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        chooseUnit_LinearLayout.setVisibility(View.GONE);
                    }
                } else if (i == 3) {
                    state = menuArray[position];
                    tvReason.setValue(state);
                    goTypeReasonCode = proposalGoType.get(position).getStrCode();
                }
                normalListDialog.dismiss();
            }
        });
        if (i == 0) {
            normalListDialog.title(getString(R.string.string_select_distribution_type));
        } else if (i == 1) {
            normalListDialog.title(getString(R.string.string_choose_the_type_of_application));
        } else if (i == 2) {
            normalListDialog.title(getString(R.string.string_choose_a_proposal));
        } else if (i == 3) {
            normalListDialog.title("  " + getString(R.string.string_choose_not_to_file_a_case));
        }
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        normalListDialog.titleTextColor(Color.WHITE);
        normalListDialog.show();
    }

    /**
     * 获取提案分发
     */
    private void getMotionDistTypeServlet() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getMotionDistTypeServlet(textParamMap), 3);
    }
}
