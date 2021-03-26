package com.suncn.ihold_zxztc.activity.application.proposal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gavin.giframe.ui.BindView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.adapter.Proposal_Detail_Instruct_RVAdapter;
import com.suncn.ihold_zxztc.bean.ProposalViewBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 提案详情中的领导批示Fragment
 */
public final class Proposal_Detail_InstructFragment extends BaseFragment {
    @BindView(id = R.id.rl_head)
    private RelativeLayout head_Layout;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;//联名意见EasyRecyclerView
    private Proposal_Detail_Instruct_RVAdapter adapter;
    private static List<ProposalViewBean.MotionApproval> joinOpinions;//领导批示集合

    public static Proposal_Detail_InstructFragment newInstance(List<ProposalViewBean.MotionApproval> joinOpinion) {
        Proposal_Detail_InstructFragment baseInfoFragment = new Proposal_Detail_InstructFragment();
        //handleIdea = mHandleIdea;
        joinOpinions = joinOpinion;
        return baseInfoFragment;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.activity_recyclerview_nopadding, null);
    }

    @Override
    public void initData() {
        super.initData();
        head_Layout.setVisibility(View.GONE);
        recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.main_bg));
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        adapter = new Proposal_Detail_Instruct_RVAdapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.setList(joinOpinions);
        adapter.setEmptyView(R.layout.view_erv_empty);
    }
}
