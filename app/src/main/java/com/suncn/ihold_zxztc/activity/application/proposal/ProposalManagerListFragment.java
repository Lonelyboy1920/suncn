package com.suncn.ihold_zxztc.activity.application.proposal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.adapter.ProposalManagerHeaderRvAdapter;
import com.suncn.ihold_zxztc.adapter.ProposalManagerRvAdapter;
import com.suncn.ihold_zxztc.bean.ProposalAllListByTypeServletBean;
import com.suncn.ihold_zxztc.bean.ProposalAllTypeServletBean;
import com.suncn.ihold_zxztc.bean.ProposalTrackListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

/**
 * @author :Sea
 * Date :2020-6-12 11:04
 * PackageName:com.suncn.ihold_zxztc.activity.application.proposal
 * Desc:提案审核 Fragment
 */
public class ProposalManagerListFragment extends BaseFragment {
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.rvProposalManagerHeaderView)
    private RecyclerView rvProposalManagerHeaderView;

    private int intCurPages = 1;
    private ProposalManagerRvAdapter proposalManagerRvAdapter;
    private String strType = "1"; //1所有提案 2办理中提案 3已办结提案 4立案提案 5不予立案提案 6重点提案 7优秀提案
    private ProposalManagerHeaderRvAdapter proposalManagerHeaderRvAdapter;


    public static ProposalManagerListFragment newInstance() {
        ProposalManagerListFragment baseInfoFragment = new ProposalManagerListFragment();
        return baseInfoFragment;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_proposal_manager_list, null);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = (data, sign) -> doLogic(data, sign);

        getProposalAllTypeServlet();
        getProposalAllListByTypeServlet(true);
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10f, 0);
        proposalManagerRvAdapter = new ProposalManagerRvAdapter(getContext());
        recyclerView.setAdapter(proposalManagerRvAdapter);
        proposalManagerRvAdapter.setEmptyView(R.layout.view_erv_empty);

        proposalManagerHeaderRvAdapter = new ProposalManagerHeaderRvAdapter(getContext());
        LinearLayoutManager layoutManagerTop = new LinearLayoutManager(activity);
        layoutManagerTop.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvProposalManagerHeaderView.setLayoutManager(layoutManagerTop);
        rvProposalManagerHeaderView.setAdapter(proposalManagerHeaderRvAdapter);

    }

    @Override
    public void setListener() {
        super.setListener();
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            intCurPages = 1;
            strType = "1";
            getProposalAllTypeServlet();
            getProposalAllListByTypeServlet(false);
            rvProposalManagerHeaderView.scrollToPosition(0);
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            intCurPages++;
            getProposalAllListByTypeServlet(false);
        });

        proposalManagerHeaderRvAdapter.setOnItemClickListener((adapter, view, position) -> {
            ProposalAllTypeServletBean.ObjListBean tabBean = (ProposalAllTypeServletBean.ObjListBean) adapter.getItem(position);
            proposalManagerHeaderRvAdapter.setStrSelectId(tabBean.getStrType());
            strType = tabBean.getStrType();
            intCurPages = 1;
            getProposalAllListByTypeServlet(true);
        });

        proposalManagerRvAdapter.setOnItemClickListener((adapter, view, position) -> {
            ProposalAllListByTypeServletBean.ObjListBean proposalBean = (ProposalAllListByTypeServletBean.ObjListBean) adapter.getItem(position);
            Bundle bundle = new Bundle();
            bundle.putString("headTitle", getString(R.string.string_proposal_detail));
            bundle.putString("strId", proposalBean.getStrId());
            bundle.putBoolean("isWorker", true);
            showActivity(fragment, Proposal_DetailActivity.class, bundle, 0);
        });

    }

    /**
     * )机关端的管理列表
     */
    private void getProposalAllListByTypeServlet(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", strType);
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages + "");
        doRequestNormal(ApiManager.getInstance().getProposalAllListByTypeServlet(textParamMap), 0);
    }

    /**
     * 提案列表的类型
     */
    private void getProposalAllTypeServlet() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getProposalAllTypeServlet(textParamMap), 1);
    }

    private void doLogic(Object data, int sign) {
        prgDialog.closePrgDialog();
        String toastMessage = null;
        try {
            switch (sign) {
                case 0:
                    ProposalAllListByTypeServletBean proposalAllListByTypeServletBean = (ProposalAllListByTypeServletBean) data;
                    Utils.setLoadMoreViewState(proposalAllListByTypeServletBean.getIntAllCount(), intCurPages * DefineUtil.GLOBAL_PAGESIZE
                            , "", refreshLayout, proposalManagerRvAdapter);
                    if (1 == intCurPages) {
                        proposalManagerRvAdapter.setList(proposalAllListByTypeServletBean.getObjList());
                    } else {
                        proposalManagerRvAdapter.addData(proposalAllListByTypeServletBean.getObjList());
                    }
                    proposalManagerRvAdapter.setStrType(strType);
                    break;
                case 1:
                    ProposalAllTypeServletBean proposalAllTypeServletBean = (ProposalAllTypeServletBean) data;
                    if (null != proposalAllTypeServletBean.getObjList() && proposalAllTypeServletBean.getObjList().size() > 0) {
                        proposalManagerHeaderRvAdapter.setStrSelectId(proposalAllTypeServletBean.getObjList().get(0).getStrType());
                        proposalManagerHeaderRvAdapter.setList(proposalAllTypeServletBean.getObjList());
                    } else {
                        proposalManagerRvAdapter.setEmptyView(R.layout.view_erv_empty);
                    }
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
