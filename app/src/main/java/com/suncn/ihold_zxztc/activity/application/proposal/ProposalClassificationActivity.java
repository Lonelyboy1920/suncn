package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ExpandableListView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.ProposalClassification_ExLVAdapter;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.ProposalClassificationListBean;
import com.suncn.ihold_zxztc.ApiManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 提案分类
 */
public class ProposalClassificationActivity extends BaseActivity {
    @BindView(id = R.id.exListview)
    private ExpandableListView exListView;//提案分类ExpandableListView
    private ProposalClassification_ExLVAdapter adapter;
    private String strCaseTypeId;
    private String strClassification;
    private String strId;
    private boolean isBack;
    private String strCaseMotionSessionId;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_global_exlistview);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("提案分类");
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText("确定");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        goto_Button.refreshFontType(activity, "2");
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strCaseMotionSessionId = bundle.getString("strCaseMotionSessionId");
            strId = bundle.getString("strId");
            isBack = bundle.getBoolean("isBack", false);
            strCaseTypeId = bundle.getString("strCaseTypeId");
        }
        getClassificationInfo();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                if (isBack) {
                    strClassification = adapter.getStrCaseTypeName();
                    strCaseTypeId = adapter.getStrCaseTypeId();
                    Bundle bundle = new Bundle();
                    bundle.putString("strClassification", strClassification);
                    bundle.putString("strCaseTypeId", strCaseTypeId);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (adapter != null) {
                        strCaseTypeId = adapter.getStrCaseTypeId();
                        if (GIStringUtil.isNotBlank(strCaseTypeId)) {
                            doReceivePro();
                            GILogUtil.i("选择的结果是" + strCaseTypeId);
                        } else {
                            showToast("请选择提案分类");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 提案接收
     */
    private void doReceivePro() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        textParamMap.put("strCaseTypeId", strCaseTypeId);
        //doRequestNormal(HttpCommonUtil.MotionPreLiAnServlet, BaseGlobal.class, 1);
    }

    /**
     * 获取提案分类
     */
    private void getClassificationInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        //textParamMap.put("strYear", "2018");
        textParamMap.put("strCaseMotionSessionId", strCaseMotionSessionId);
        doRequestNormal(ApiManager.getInstance().getClassificationList(textParamMap), 0);
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
            case 1:
                prgDialog.closePrgDialog();
                try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                    toastMessage = "接收成功！";
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 0:
                prgDialog.closePrgDialog();
                try {
                    ProposalClassificationListBean proposalManagementList = (ProposalClassificationListBean) obj;
                    ArrayList<ProposalClassificationListBean.GroupProposalClassification> childProposalClassifications = proposalManagementList.getObjList();
                    exListView.setVisibility(View.VISIBLE);
                    if (adapter == null) {
                        adapter = new ProposalClassification_ExLVAdapter(activity, childProposalClassifications);
                        if (GIStringUtil.isNotBlank(strCaseTypeId)) {
                            adapter.setStrCaseTypeId(strCaseTypeId);
                        }
                        adapter.setProposalClassifications(childProposalClassifications);
                        exListView.setAdapter(adapter);
                    } else {
                        adapter.setProposalClassifications(childProposalClassifications);
                        adapter.notifyDataSetChanged();
                    }
                        /*exListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                            @Override
                            public void onGroupExpand(int groupPosition) {
                                for (int i = 0; i < adapter.getGroupCount(); i++) {
                                    //if (i != groupPosition) {
                                        exListView.expandGroup(i);
                                   // }
                                }
                            }
                        });*/
                    for (int i = 0; i < childProposalClassifications.size(); i++) {
                        for (int j = 0; j < childProposalClassifications.get(i).getCaseTopic_list().size(); j++) {
                            if (childProposalClassifications.get(i).getCaseTopic_list().get(j).getStrCaseTypeId().equals(strCaseTypeId)) {
                                exListView.expandGroup(i);
                                break;
                            }
                            for (int k = 0; k < childProposalClassifications.get(i).getCaseTopic_list().get(j).getCaseTopic_list().size(); k++) {
                                if (childProposalClassifications.get(i).getCaseTopic_list().get(j).getCaseTopic_list().get(k).getStrCaseTypeId().equals(strCaseTypeId))
                                    exListView.expandGroup(i);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            GIToastUtil.showMessage(activity, toastMessage);
    }

    @Override
    public void setListener() {
        super.setListener();
        //exListView.setOnChildClickListener(onChildClickListener);
//        expandableListview.setOnGroupExpandListener(onGroupExpandListener);
        //exListView.setOnGroupClickListener(onGroupClickListener);
    }

    ExpandableListView.OnGroupClickListener onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return false;
        }
    };

    ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            return false;
        }
    };

}
