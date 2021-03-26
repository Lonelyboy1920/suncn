package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.SimilarProposalListBean;

import org.jetbrains.annotations.NotNull;

/**
 * 相似提案adapter
 */
public class SimilarProposalListAdapter extends BaseQuickAdapter<SimilarProposalListBean.SimilarProposal, SimilarProposalListAdapter.ViewHolder> {
    private Context context;


    public SimilarProposalListAdapter(Context context) {
        super(R.layout.view_lv_item_similar_proposal);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, SimilarProposalListBean.SimilarProposal similarProposal) {
        viewHolder.title_TextView.setText(similarProposal.getStrDestTitle());
        viewHolder.date_TextView.setText(similarProposal.getStrReportDate());
        viewHolder.content_TextView.setText(context.getString(R.string.string_proposer) + "：" + similarProposal.getStrDestSourceName());
        viewHolder.state_TextView.setTextColor(context.getResources().getColor(R.color.font_content));
        viewHolder.state_TextView.setText(context.getString(R.string.string_similarity) + "：" + similarProposal.getStrContentLikePer());
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;
        private TextView content_TextView;
        private TextView date_TextView;
        private TextView state_TextView;

        public ViewHolder(View convertView) {
            super(convertView);
            title_TextView = (TextView) convertView.findViewById(R.id.tv_title);
            date_TextView = (TextView) convertView.findViewById(R.id.tv_date);
            content_TextView = (TextView) convertView.findViewById(R.id.tv_content);
            state_TextView = (TextView) convertView.findViewById(R.id.tv_state);
        }

    }
}
