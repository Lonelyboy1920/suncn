package com.suncn.ihold_zxztc.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_Detail_TransactFragment;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.bean.ObjFeedBackListBean;
import com.suncn.ihold_zxztc.bean.ObjTransactBean;
import com.suncn.ihold_zxztc.view.ShowAllSpan;
import com.suncn.ihold_zxztc.view.ShowAllTextView;

import org.jetbrains.annotations.NotNull;

/**
 * @author :Sea
 * Date :2020-6-10 17:44
 * PackageName:com.suncn.ihold_zxztc.adapter
 * Desc:  反馈情况Rv Adapter
 */
public class ProposalHandlingFeedbackRvAdapter extends BaseQuickAdapter<ObjFeedBackListBean, ProposalHandlingFeedbackRvAdapter.ViewHolder> {
    private BaseFragment fragment;
    private boolean isShowMax = false;

    public ProposalHandlingFeedbackRvAdapter(BaseFragment fragment) {
        super(R.layout.item_rv_proposal_handling_feedback);
        this.fragment = fragment;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, ObjFeedBackListBean transactBean) {
        baseViewHolder.itemTvOrganizerTag.setText(transactBean.getStrAttendType() + "单位");
        baseViewHolder.itemTvOrganizer.setText(transactBean.getStrRecUnitName());
        baseViewHolder.itemTvHandlingResult.setText(transactBean.getStrFeedBackResultState());
        baseViewHolder.itemTvOverallEvaluation.setText(transactBean.getStrFeedBackTotalState());
        baseViewHolder.itemTvHandlingAttitude.setText(transactBean.getStrFeedBackAttitudeState());


        baseViewHolder.itemTvSpecificReply.setVisibility(View.VISIBLE);
        if (!isShowMax) {
            baseViewHolder.itemTvSpecificReply.setMaxShowLines(2);
        }
        baseViewHolder.itemTvSpecificReply.setMyText(transactBean.getStrFeedBackSpecificIdea());
        baseViewHolder.itemTvSpecificReply.setOnAllSpanClickListener(new ShowAllSpan.OnAllSpanClickListener() {
            @Override
            public void onClick(View widget) {
                baseViewHolder.itemTvSpecificReply.setMaxShowLines(0);
                isShowMax = true;
                notifyDataSetChanged();
            }
        });

    }

    public class ViewHolder extends BaseViewHolder {
        private TextView itemTvOrganizerTag;
        private TextView itemTvOrganizer;
        private TextView itemTvHandlingResult;
        private TextView itemTvOverallEvaluation;
        private ShowAllTextView itemTvSpecificReply;
        private TextView itemTvHandlingAttitude;


        public ViewHolder(View convertView) {
            super(convertView);
            itemTvOrganizerTag = convertView.findViewById(R.id.itemTvOrganizerTag);
            itemTvOrganizer = convertView.findViewById(R.id.itemTvOrganizer);
            itemTvHandlingResult = convertView.findViewById(R.id.itemTvHandlingResult);
            itemTvOverallEvaluation = convertView.findViewById(R.id.itemTvOverallEvaluation);
            itemTvSpecificReply = convertView.findViewById(R.id.itemTvSpecificReply);
            itemTvHandlingAttitude = convertView.findViewById(R.id.itemTvHandlingAttitude);

        }
    }
}
