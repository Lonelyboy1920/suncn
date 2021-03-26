package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalTrackListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 提案审核adapter
 */
public class Proposal_Audit_RVAdapter extends BaseQuickAdapter<ProposalTrackListBean.ProposalTrack, Proposal_Audit_RVAdapter.ViewHolder> {
    private Context context;

    public Proposal_Audit_RVAdapter(Context context) {
        super(R.layout.item_rv_proposal_main);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ProposalTrackListBean.ProposalTrack objInfo) {
        viewHolder.state_TextView.setText(objInfo.getStrStateName());
        String strNo = Utils.getShowContent(objInfo.getStrFromNo(), objInfo.getStrCaseNo());
        viewHolder.title_TextView.setText(GIUtil.showHtmlInfo("<font color=#ef4b39>" + strNo + "</font>" + " " + objInfo.getStrTitle()));
        viewHolder.name_TextView.setText(objInfo.getStrSourceName());
        viewHolder.date_TextView.setText(objInfo.getStrReportDate());
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;//案由
        private TextView name_TextView;//提案人
        private TextView date_TextView;//日期
        private TextView state_TextView;//状态

        public ViewHolder(View itemView) {
            super(itemView);
            title_TextView = (TextView) itemView.findViewById(R.id.tv_title);
            name_TextView = (TextView) itemView.findViewById(R.id.tv_name);
            name_TextView.setVisibility(View.VISIBLE);
            date_TextView = (TextView) itemView.findViewById(R.id.tv_date);
            state_TextView = (TextView) itemView.findViewById(R.id.tv_state);
        }

    }
}
