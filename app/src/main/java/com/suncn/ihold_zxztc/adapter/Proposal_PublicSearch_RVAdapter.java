package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalListBean;

import org.jetbrains.annotations.NotNull;

/**
 * 公开提案查询Adapter
 */
public class Proposal_PublicSearch_RVAdapter extends BaseQuickAdapter<ProposalListBean.ProposalBean, Proposal_PublicSearch_RVAdapter.ViewHolder> {
    private Context context;

    public Proposal_PublicSearch_RVAdapter(Context context) {
        super(R.layout.item_rv_proposal_publicsearch);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ProposalListBean.ProposalBean data) {
        viewHolder.date_TextView.setText(data.getPubDate());
        viewHolder.content_TextView.setText(data.getStrTitle());
        viewHolder.name_TextView.setText(data.getStrSourceName());
        if (GIStringUtil.isNotBlank(data.getStrMainUnit())) {
            viewHolder.unit_TextView.setVisibility(View.VISIBLE);
            viewHolder.unit_TextView.setText(context.getString(R.string.string_undertaker) + "  " + data.getStrMainUnit());
        } else {
            viewHolder.unit_TextView.setVisibility(View.GONE);
        }
        if (GIStringUtil.isNotBlank(data.getStrHandlerFinishLevel())) {
            viewHolder.solution_TextView.setVisibility(View.VISIBLE);
            viewHolder.solution_TextView.setText(context.getString(R.string.string_degree_of_resolution) + data.getStrHandlerFinishLevel());
        } else {
            viewHolder.solution_TextView.setVisibility(View.GONE);
        }
        viewHolder.finishDate_TextView.setText(data.getDtHandlerFinishDate());
        int textColor = context.getResources().getColor(R.color.zxta_state_grey);
        String state = data.getStrFlowState(); //  办理状态（已上报、已立案、办理中、待反馈、已反馈（不满意）、已办结）
        if (GIStringUtil.isNotBlank(state)) {
            switch (state) {
                case "办理中":
                    textColor = context.getResources().getColor(R.color.zxta_state_green);
                    break;
                case "待反馈":
                    textColor = context.getResources().getColor(R.color.zxta_state_orange);
                    break;
                case "不予立案":
                    textColor = context.getResources().getColor(R.color.zxta_state_orange);
                    break;
                case "已反馈（不满意）":
                case "已上报":
                case "已立案":
                case "已办结":
                default:
                    break;
            }
        }
        viewHolder.state_TextView.setText(state);
        viewHolder.state_TextView.setTextColor(textColor);
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView unit_TextView;
        private TextView solution_TextView;
        private GITextView date_TextView;
        private TextView content_TextView;
        private GITextView name_TextView;
        private TextView state_TextView;
        private TextView finishDate_TextView;

        public ViewHolder(View convertView) {
            super(convertView);
            date_TextView = convertView.findViewById(R.id.tv_date);
            content_TextView = convertView.findViewById(R.id.tv_content);
            name_TextView = convertView.findViewById(R.id.tv_name);
            state_TextView = convertView.findViewById(R.id.tv_state);
            unit_TextView = convertView.findViewById(R.id.tv_unit);
            solution_TextView = convertView.findViewById(R.id.tv_solution);
            finishDate_TextView = convertView.findViewById(R.id.tv_finish_date);
        }

    }
}
