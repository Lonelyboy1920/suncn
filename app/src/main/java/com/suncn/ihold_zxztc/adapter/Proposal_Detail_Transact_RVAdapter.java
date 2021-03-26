package com.suncn.ihold_zxztc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.bean.ObjTransactBean;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_Detail_TransactFragment;

import org.jetbrains.annotations.NotNull;

/**
 * 政协提案详情的办理情况
 */
public class Proposal_Detail_Transact_RVAdapter extends BaseQuickAdapter<ObjTransactBean, Proposal_Detail_Transact_RVAdapter.ViewHolder> {
    private BaseFragment fragment;

    public Proposal_Detail_Transact_RVAdapter(BaseFragment fragment) {
        super(R.layout.item_rv_proposal_detail_transact);
        this.fragment = fragment;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, ObjTransactBean transactBean) {
        String strAttendType = transactBean.getStrAttendType();
        baseViewHolder.type_TextView.setText("[" + strAttendType + "]");
        if (strAttendType.equals(fragment.getString(R.string.string_host))) {
            baseViewHolder.type_TextView.setTextColor(fragment.getResources().getColor(R.color.zxta_state_orange));
        } else {
            baseViewHolder.type_TextView.setTextColor(fragment.getResources().getColor(R.color.zxta_state_grey));
        }
        if (GISharedPreUtil.getInt(fragment.getContext(), "intUserRole") != 0
                && GIStringUtil.isNotBlank(transactBean.getStrQuality())) {
            baseViewHolder.tv_quality.setVisibility(View.VISIBLE);
            baseViewHolder.tv_quality.setText(fragment.getString(R.string.string_proposal_quality) + transactBean.getStrQuality());
        } else {
            baseViewHolder.tv_quality.setVisibility(View.GONE);
        }
        baseViewHolder.name_TextView.setText(transactBean.getStrAttendUnitName());
        String strReSolveLevel = transactBean.getStrReSolveLevel();
        if (GIStringUtil.isBlank(strReSolveLevel)) {
            baseViewHolder.reSolveLevel_TextView.setVisibility(View.GONE);
        } else {
            baseViewHolder.reSolveLevel_TextView.setText(fragment.getString(R.string.string_degree_of_resolution) + strReSolveLevel);
            baseViewHolder.reSolveLevel_TextView.setVisibility(View.VISIBLE);
        }
        String strUnitAttendState = transactBean.getStrAttendState(); // 办理状态
        baseViewHolder.state_TextView.setText(strUnitAttendState);
        if (GIStringUtil.isBlank(strUnitAttendState)) {
            baseViewHolder.state_TextView.setVisibility(View.GONE);
        } else if (strUnitAttendState.contains(fragment.getString(R.string.string_deal_finish)) || strUnitAttendState.contains(fragment.getString(R.string.string_withdraw))) {
            baseViewHolder.state_TextView.setVisibility(View.VISIBLE);
            baseViewHolder.state_TextView.setBackgroundResource(R.drawable.shape_zxta_state_bg_grey);
            baseViewHolder.state_TextView.setTextColor(fragment.getResources().getColor(R.color.zxta_state_grey));
        } else {
            baseViewHolder.state_TextView.setVisibility(View.VISIBLE);
            baseViewHolder.state_TextView.setBackgroundResource(R.drawable.shape_zxtadetail_state_bg_orange);
            baseViewHolder.state_TextView.setTextColor(fragment.getResources().getColor(R.color.zxta_state_orange));
        }
        if (strUnitAttendState.equals(fragment.getString(R.string.string_withdrawed))) {
            baseViewHolder.reply_TextView.setText(fragment.getString(R.string.string_view_reasons_for_withdrawal));
        } else if (strUnitAttendState.equals(fragment.getString(R.string.string_deal_finished))) {
            baseViewHolder.reply_TextView.setText(fragment.getString(R.string.string_view_reply_details));
            baseViewHolder.reply_TextView.setTextColor(fragment.getResources().getColor(R.color.red));
        } else {
            baseViewHolder.reply_TextView.setText("");
        }
        String dtOverDate = transactBean.getStrOverDate(); // 完成时间（格式“yyyy-mm-dd”）
        if (GIStringUtil.isBlank(dtOverDate)) {
            baseViewHolder.date_TextView.setVisibility(View.GONE);
        } else {
            baseViewHolder.date_TextView.setText(dtOverDate);
            baseViewHolder.date_TextView.setVisibility(View.VISIBLE);
        }
        if (transactBean.getIntIsReply() == 1) { // 答复内容标识（0—没有答复内容；1-有答复内容）
            baseViewHolder.reply_TextView.setVisibility(View.VISIBLE);
            //reply_TextView.setTag(position);
            baseViewHolder.reply_TextView.setTextColor(fragment.getResources().getColor(R.color.red));
            baseViewHolder.reply_TextView.setText(fragment.getString(R.string.string_view_reply_content));
            baseViewHolder.reply_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Proposal_Detail_TransactFragment) fragment).setOnClick(transactBean, view, 1);
                }
            });
        } else {
            baseViewHolder.reply_TextView.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView name_TextView;
        private TextView type_TextView;
        private TextView reSolveLevel_TextView;
        private TextView reply_TextView;
        private TextView state_TextView;
        private TextView date_TextView;
        private TextView tv_quality;

        public ViewHolder(View convertView) {
            super(convertView);
            name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            type_TextView = (TextView) convertView.findViewById(R.id.tv_type);
            reSolveLevel_TextView = (TextView) convertView.findViewById(R.id.tv_reSolveLevel);
            reply_TextView = (TextView) convertView.findViewById(R.id.tv_reply);
            state_TextView = (TextView) convertView.findViewById(R.id.tv_state);
            date_TextView = (TextView) convertView.findViewById(R.id.tv_date);
            tv_quality = (TextView) convertView.findViewById(R.id.tv_quality);
        }


    }

}
