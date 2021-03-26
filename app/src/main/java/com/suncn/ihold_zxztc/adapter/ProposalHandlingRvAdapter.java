package com.suncn.ihold_zxztc.adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.bean.ObjTransactBean;
import com.suncn.ihold_zxztc.view.ShowAllSpan;
import com.suncn.ihold_zxztc.view.ShowAllTextView;

import org.jetbrains.annotations.NotNull;

/**
 * @author :Sea
 * Date :2020-6-11 18:23
 * PackageName:com.suncn.ihold_zxztc.adapter
 * Desc:
 */
public class ProposalHandlingRvAdapter extends BaseQuickAdapter<ObjTransactBean, ProposalHandlingRvAdapter.ViewHolder> {
    private BaseFragment fragment;
    private boolean isShowMax = false;

    public ProposalHandlingRvAdapter(BaseFragment fragment) {
        super(R.layout.item_rv_proposal_handling);
        this.fragment = fragment;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, ObjTransactBean transactBean) {
        baseViewHolder.itemTvOrganizerTag.setText(transactBean.getStrAttendType() + "单位");
        baseViewHolder.itemTvOrganizer.setText(transactBean.getStrAttendUnitName());
//        baseViewHolder.itemTvHandlingResult.setText(transactBean.getStrFeedBackResultState());
//        baseViewHolder.itemTvOverallEvaluation.setText(transactBean.getStrFeedBackTotalState());
        baseViewHolder.tvItemRecDate.setText(transactBean.getStrRecDate());
        baseViewHolder.tvItemFinishDate.setText(transactBean.getStrFinishDate());
        baseViewHolder.tvItemReplyType.setText(transactBean.getStrReplyType());
        String strReSolveLevel = transactBean.getStrResolveLevel();
        if (GIStringUtil.isNotBlank(strReSolveLevel)) {
            baseViewHolder.llItemResolveLevel.setVisibility(View.VISIBLE);
            baseViewHolder.tvItemResolveLevel.setText(strReSolveLevel);  //如果为空改行不显示
        } else {
            baseViewHolder.llItemResolveLevel.setVisibility(View.GONE);
        }
        baseViewHolder.itemTvSpecificReply.setVisibility(View.VISIBLE);
        if (!isShowMax) {
            baseViewHolder.itemTvSpecificReply.setMaxShowLines(2);
        }
        baseViewHolder.itemTvSpecificReply.setMyText(transactBean.getStrReplyContent());
        baseViewHolder.itemTvSpecificReply.setOnAllSpanClickListener(widget -> {
            baseViewHolder.itemTvSpecificReply.setMaxShowLines(0);
            isShowMax = true;
            notifyDataSetChanged();
        });
        baseViewHolder.tvItemAttendState.setText(transactBean.getStrAttendState());
//        int textColor;
//        if (fragment.getString(R.string.string_filed).equals(strState)
//                || fragment.getString(R.string.string_jointly).equals(strState)
//                || fragment.getString(R.string.string_seconded).equals(strState)
//                || fragment.getString(R.string.string_in_process).equals(strState)
//                || fragment.getString(R.string.string_agree_to_second).equals(strState)) {//绿色
//            textColor = fragment.getResources().getColor(R.color.zxta_state_green);
//        } else if (fragment.getString(R.string.string_not_filed).equals(strState)
//                || fragment.getString(R.string.string_disagree_filed).equals(strState)) {
//            textColor = fragment.getResources().getColor(R.color.red);
//        } else {
//            textColor = fragment.getResources().getColor(R.color.font_source);
//        }

    }

    public class ViewHolder extends BaseViewHolder {
        private TextView itemTvOrganizerTag;
        private TextView itemTvOrganizer;
        //        private TextView itemTvHandlingResult;
//        private TextView itemTvOverallEvaluation;
        private ShowAllTextView itemTvSpecificReply;
        private TextView tvItemRecDate;
        private TextView tvItemFinishDate;
        private TextView tvItemReplyType;
        private TextView tvItemResolveLevel;
        private LinearLayout llItemResolveLevel;
        private TextView tvItemAttendState;


        public ViewHolder(View convertView) {
            super(convertView);
            itemTvOrganizerTag = convertView.findViewById(R.id.itemTvOrganizerTag);
            itemTvOrganizer = convertView.findViewById(R.id.itemTvOrganizer);
//            itemTvHandlingResult = convertView.findViewById(R.id.itemTvHandlingResult);
//            itemTvOverallEvaluation = convertView.findViewById(R.id.itemTvOverallEvaluation);
            itemTvSpecificReply = convertView.findViewById(R.id.itemTvSpecificReply);
            tvItemRecDate = convertView.findViewById(R.id.tvItemRecDate);
            tvItemFinishDate = convertView.findViewById(R.id.tvItemFinishDate);
            tvItemReplyType = convertView.findViewById(R.id.tvItemReplyType);
            tvItemResolveLevel = convertView.findViewById(R.id.tvItemResolveLevel);
            llItemResolveLevel = convertView.findViewById(R.id.llItemResolveLevel);
            tvItemAttendState = convertView.findViewById(R.id.tvItemAttendState);

        }
    }
}