package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MeetingSpeakListBean;

import org.jetbrains.annotations.NotNull;

/**
 * 社情民意列表
 */
public class PublicOpinionListAdapter extends BaseQuickAdapter<MeetingSpeakListBean.MeetingSpeakBean, PublicOpinionListAdapter.ViewHolder> {
    private Context context;
    private int intType;

    public PublicOpinionListAdapter(Context context, int intType) {
        super(R.layout.view_item_manger_public);
        this.context = context;
        this.intType = intType;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, MeetingSpeakListBean.MeetingSpeakBean objInfo) {
        viewHolder.title_TextView.setText(objInfo.getStrTitle());
        String strState = GIStringUtil.nullToEmpty(objInfo.getStrStateName());
        if (GIStringUtil.isNotBlank(objInfo.getStrFaction()) || GIStringUtil.isNotBlank(objInfo.getStrSector())) {
            viewHolder.tvUserMsg.setVisibility(View.VISIBLE);
            viewHolder.name_TextView.setVisibility(View.GONE);
            viewHolder.tvUserMsg.setText(objInfo.getStrSourceName() + "    " + objInfo.getStrFaction() + "   " + objInfo.getStrSector());
        } else {
            viewHolder.name_TextView.setText(objInfo.getStrSourceName());
            viewHolder.tvUserMsg.setVisibility(View.GONE);
        }
        if (GIStringUtil.isNotBlank(strState)) {
            viewHolder.date_TextView.setVisibility(View.VISIBLE);
            viewHolder.date_TextView.setText(objInfo.getStrReportDate());
            viewHolder.state_TextView.setText(strState);
        } else if (GIStringUtil.isNotBlank(objInfo.getStrIssue())) {
            viewHolder.date_TextView.setVisibility(View.VISIBLE);
            viewHolder.date_TextView.setText(objInfo.getStrReportDate());
            viewHolder.state_TextView.setText(objInfo.getStrIssue());
        } else if (GIStringUtil.isNotBlank(objInfo.getStrAttendName())) {
            viewHolder.date_TextView.setVisibility(View.VISIBLE);
            viewHolder.date_TextView.setText(objInfo.getStrReportDate());
            viewHolder.state_TextView.setText(objInfo.getStrAttendName());
        }
        if (GIStringUtil.isNotBlank(objInfo.getStrType())) {
            viewHolder.tvType.setText(objInfo.getStrType());
            viewHolder.tvType.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvType.setVisibility(View.GONE);
        }
        if ("1".equals(objInfo.getStrLeaderState())) {
            viewHolder.tv_deal.setVisibility(View.VISIBLE);
            viewHolder.tv_deal.setText("已批示");
        } else {
            viewHolder.tv_deal.setVisibility(View.GONE);
        }
        if ("1".equals(objInfo.getStrEditStatus())) {
            viewHolder.tv_receive.setVisibility(View.VISIBLE);
            viewHolder.tv_receive.setText("已采编");
        } else {
            viewHolder.tv_receive.setVisibility(View.GONE);
        }
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;
        private GITextView name_TextView;
        private GITextView date_TextView;
        private GITextView state_TextView;
        private TextView tv_deal;
        private TextView tvType;
        private TextView tvUserMsg;
        private TextView tv_receive;


        public ViewHolder(View itemView) {
            super(itemView);
            title_TextView = (TextView) itemView.findViewById(R.id.tv_title);
            name_TextView = (GITextView) itemView.findViewById(R.id.tv_name);
            date_TextView = (GITextView) itemView.findViewById(R.id.tv_date);
            state_TextView = (GITextView) itemView.findViewById(R.id.tv_state);
            tv_deal = (TextView) itemView.findViewById(R.id.tv_deal);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
            tvUserMsg = (TextView) itemView.findViewById(R.id.tv_user_msg);
            tv_receive=(TextView) itemView.findViewById(R.id.tv_receive);

        }


    }
}
