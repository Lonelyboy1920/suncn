package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalViewBean;

import org.jetbrains.annotations.NotNull;

/**
 * 政协提案详情的领导批示
 */
public class Proposal_Detail_Instruct_RVAdapter extends BaseQuickAdapter<ProposalViewBean.MotionApproval, Proposal_Detail_Instruct_RVAdapter.ViewHolder> {
    private Context context;

    public Proposal_Detail_Instruct_RVAdapter(Context context) {
        super(R.layout.item_rv_proposal_detail_instruct);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ProposalViewBean.MotionApproval joinOpinion) {
        viewHolder.name_TextView.setText(joinOpinion.getStrLeader());
        viewHolder.date_TextView.setText(joinOpinion.getStrCommentDate());
        viewHolder.enter_TextView.setText(joinOpinion.getStrUserName());
        viewHolder.tv_type.setText(joinOpinion.getStrLeaderType());
        if (GIStringUtil.isNotBlank(joinOpinion.getStrComment())) {
            viewHolder.opinion_LinearLayout.setVisibility(View.VISIBLE);
            viewHolder.opinion_TextView.setText(GIUtil.showHtmlInfo(joinOpinion.getStrComment()));
        } else {
            viewHolder.opinion_LinearLayout.setVisibility(View.GONE);
        }
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView name_TextView;//联名人
        private TextView date_TextView;//联名状态
        private TextView opinion_TextView;//联名意见
        private TextView enter_TextView;//录入人
        private LinearLayout opinion_LinearLayout;//联名意见LinearLayout
        private TextView tv_type;//批示类型

        public ViewHolder(View convertView) {
            super(convertView);
            name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            date_TextView = (TextView) convertView.findViewById(R.id.tv_date);
            enter_TextView = (TextView) convertView.findViewById(R.id.tv_enter);
            opinion_LinearLayout = (LinearLayout) convertView.findViewById(R.id.ll_opinion);
            opinion_TextView = (TextView) convertView.findViewById(R.id.tv_opinion);
            tv_type = (TextView) convertView.findViewById(R.id.tv_type);
        }

    }

}
