package com.suncn.ihold_zxztc.activity.application.proposal;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.adapter.ProposalHandlingFeedbackRvAdapter;
import com.suncn.ihold_zxztc.bean.ObjFeedBackListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.List;

/**
 * @author :Sea
 * Date :2020-6-10 17:16
 * PackageName:com.suncn.ihold_zxztc.activity.application.proposal
 * Desc:提案  反馈情况
 */
public class ProposalHandlingFeedbackFragment extends BaseFragment {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;//办理情况EasyRecyclerView
    @BindView(id = R.id.tvFeedBackAllState)
    private TextView tvFeedBackAllState;
    @BindView(id = R.id.llFeedBackAllState)
    private LinearLayout llFeedBackAllState;
    private static List<ObjFeedBackListBean> transactBeens; // 办理情况集合
    private ProposalHandlingFeedbackRvAdapter adapter;
    private static String mStrFeedBackAllState;

    public static ProposalHandlingFeedbackFragment newInstance(List<ObjFeedBackListBean> mTransactBeens, String strFeedBackAllState) {
        ProposalHandlingFeedbackFragment baseInfoFragment = new ProposalHandlingFeedbackFragment();
        transactBeens = mTransactBeens;
        mStrFeedBackAllState = strFeedBackAllState;
        return baseInfoFragment;
    }


    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_proposal_handling_feedback, null);
    }

    @Override
    public void initData() {
        super.initData();
        Drawable drawableLeftYes;
        if (getString(R.string.string_satisfied).equals(mStrFeedBackAllState)) {
            drawableLeftYes = getResources().getDrawable(R.mipmap.icon_satisfied);
        } else {
            drawableLeftYes = getResources().getDrawable(R.mipmap.icon_unsatisfied);
        }
        if (GIStringUtil.isNotBlank(mStrFeedBackAllState)) {
            llFeedBackAllState.setVisibility(View.VISIBLE);
            tvFeedBackAllState.setCompoundDrawablesWithIntrinsicBounds(drawableLeftYes,
                    null, null, null);
            tvFeedBackAllState.setText(mStrFeedBackAllState);
        } else {
            llFeedBackAllState.setVisibility(View.GONE);
        }
//        recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.main_bg));
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 1f, 0);
        adapter = new ProposalHandlingFeedbackRvAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setList(transactBeens);
        adapter.setEmptyView(R.layout.view_erv_empty);
    }
}
