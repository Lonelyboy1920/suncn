package com.suncn.ihold_zxztc.activity.application.proposal;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.ProposalSearchResultRvAdapter;
import com.suncn.ihold_zxztc.bean.ProposalAllListByTypeServletBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author :Sea
 * Date :2020-6-15 15:59
 * PackageName:com.suncn.ihold_zxztc.activity.application.proposal
 * Desc:   提案查询列表结果
 */
public class ProposalSearchResultListActivity extends BaseActivity {
    @BindView(id = R.id.tv_msg)
    private TextView msg_TextView;//筛选条数结果TextView
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;

    private int intCurPages = 1;
    private ProposalSearchResultRvAdapter proposalManagerRvAdapter;
    private String strYear;
    private String strSession;
    private String strProposalType;
    private String strSector;
    private String strAffiliatedSpecialCommittee;
    private String strProponentType;
    private String strProposalStatus;
    private String etOrganizer;
    private String etJointVenture;
    private String strContent;
    private String etBranchOffice;
    private String etProponent;
    private String etSubject;
    private String etSerialNumber;
    private String etProposalNo;
    private List<ProposalAllListByTypeServletBean.ObjListBean> objList = new ArrayList<>();


    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_search_result_list);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = (data, sign) -> doLogic(data, sign);
        setHeadTitle(getString(R.string.string_search_result));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strYear = bundle.getString("strYear");
            strSession = bundle.getString("strSession");
            strProposalType = bundle.getString("strProposalType");
            strProposalStatus = bundle.getString("strProposalStatus");
            strProponentType = bundle.getString("strProponentType");
            strSector = bundle.getString("strSector");
            strAffiliatedSpecialCommittee = bundle.getString("strAffiliatedSpecialCommittee");
            etOrganizer = bundle.getString("etOrganizer");
            etJointVenture = bundle.getString("etJointVenture");
            strContent = bundle.getString("strContent");
            etBranchOffice = bundle.getString("etBranchOffice");
            etProponent = bundle.getString("etProponent");
            etSubject = bundle.getString("etSubject");
            etSerialNumber = bundle.getString("etSerialNumber");
            etProposalNo = bundle.getString("etProposalNo");
        }
        getProposalAllListByTypeServlet(true);
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        proposalManagerRvAdapter = new ProposalSearchResultRvAdapter(this);
        recyclerView.setAdapter(proposalManagerRvAdapter);
        proposalManagerRvAdapter.setEmptyView(R.layout.view_erv_empty);
    }

    @Override
    public void setListener() {
        super.setListener();
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            intCurPages = 1;
            getProposalAllListByTypeServlet(false);
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            intCurPages++;
            getProposalAllListByTypeServlet(false);
        });

        proposalManagerRvAdapter.setOnItemClickListener((adapter, view, position) -> {
            ProposalAllListByTypeServletBean.ObjListBean proposalBean = (ProposalAllListByTypeServletBean.ObjListBean) adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putString("headTitle", getString(R.string.string_proposal_detail));
            bundle.putString("strId", proposalBean.getStrId());
            bundle.putBoolean("isWorker", true);
            showActivity(activity, Proposal_DetailActivity.class, bundle, 0);
        });


    }

    /**
     * )机关端的管理列表
     */
    private void getProposalAllListByTypeServlet(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages + "");
        if (GIStringUtil.isNotBlank(strYear)) {
            textParamMap.put("strYear", strYear);
        }
        if (GIStringUtil.isNotBlank(strSession)) {
            textParamMap.put("strSessionId", strSession);
        }
        if (GIStringUtil.isNotBlank(strProposalType)) {
            textParamMap.put("strProposalTypeId", strProposalType);
        }
        if (GIStringUtil.isNotBlank(strProposalStatus)) {
            textParamMap.put("strState", strProposalStatus);
        }
        if (GIStringUtil.isNotBlank(strSector)) {
            textParamMap.put("strSector", strSector);
        }
        if (GIStringUtil.isNotBlank(strAffiliatedSpecialCommittee)) {
            textParamMap.put("strMemType", strAffiliatedSpecialCommittee);
        }
        if (GIStringUtil.isNotBlank(strProponentType)) {
            textParamMap.put("strReporterType", strProponentType);  //提案者类型
        }
        if (GIStringUtil.isNotBlank(etOrganizer)) {
            textParamMap.put("strMainUnitName", etOrganizer);  //主办单位
        }
        if (GIStringUtil.isNotBlank(etJointVenture)) {
            textParamMap.put("strMeetUnitName", etJointVenture);  //会办单位
        }
        if (GIStringUtil.isNotBlank(etBranchOffice)) {
            textParamMap.put("strBranchUnitName", etBranchOffice);  //分办单位
        }
        if (GIStringUtil.isNotBlank(etProponent)) {
            textParamMap.put("strSourceName", etProponent);  //提案者
        }
        if (GIStringUtil.isNotBlank(etSubject)) {
            textParamMap.put("strTitle", etSubject);  //标题
        }
        if (GIStringUtil.isNotBlank(strContent)) {
            textParamMap.put("strContent", strContent);  //内容
        }
        if (GIStringUtil.isNotBlank(etSerialNumber)) {
            textParamMap.put("strFromNo", etSerialNumber);  //流水号
        }
        if (GIStringUtil.isNotBlank(etProposalNo)) {
            textParamMap.put("strNo", etProposalNo);  //提案案号
        }

        doRequestNormal(ApiManager.getInstance().getProposalAllListByTypeServlet(textParamMap), 0);
    }

    private void doLogic(Object data, int sign) {
        prgDialog.closePrgDialog();
        String toastMessage = null;
        try {
            switch (sign) {
                case 0:
                    ProposalAllListByTypeServletBean proposalAllListByTypeServletBean = (ProposalAllListByTypeServletBean) data;
                    Utils.setLoadMoreViewState(proposalAllListByTypeServletBean.getIntAllCount(), intCurPages * DefineUtil.GLOBAL_PAGESIZE
                            , "strKeyValue", refreshLayout, proposalManagerRvAdapter);
                    if (1 == intCurPages) {
                        objList.clear();
                        objList.addAll(proposalAllListByTypeServletBean.getObjList());
                    } else {
                        objList.addAll(proposalAllListByTypeServletBean.getObjList());
                    }
                    proposalManagerRvAdapter.setList(objList);
                    if (proposalAllListByTypeServletBean.getIntAllCount() > 0) {
                        msg_TextView.setVisibility(View.VISIBLE);
                        msg_TextView.setText(GIUtil.showHtmlInfo(getString(R.string.string_alerady_searched_for_you) + "<font color=\"#ef4b39\">" + proposalAllListByTypeServletBean.getIntAllCount() + "</font>" + getString(R.string.string_data_num)));
                    }
                    //胡伟确认 一直显示
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            msg_TextView.setVisibility(View.GONE);
//                        }
//                    }, 3000L);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getString(R.string.data_error);
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }
}
