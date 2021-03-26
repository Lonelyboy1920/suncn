package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalTrackListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 提案追踪列表adapter
 * 提案交办
 */
public class Proposal_Track_RVAdapter extends BaseQuickAdapter<ProposalTrackListBean.ProposalTrack, Proposal_Track_RVAdapter.ViewHolder> {
    private Context context;
    private int intType;

    public Proposal_Track_RVAdapter(Context context, int intType) {
        super(R.layout.item_rv_proposal_track);
        this.context = context;
        this.intType = intType;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ProposalTrackListBean.ProposalTrack objInfo) {
        viewHolder. caseNo_TextView.setText(objInfo.getStrCaseNo());
        if (GIStringUtil.isNotBlank(objInfo.getStrDelayStateName())) {
            viewHolder. state_TextView.setVisibility(View.VISIBLE);
            viewHolder. state_TextView.setText(objInfo.getStrDelayStateName());
        } else {
            viewHolder. state_TextView.setVisibility(View.GONE);
        }
        //通过最简单的方式实现
        if ("1".equals(objInfo.getStrIsGood()) && "0".equals(objInfo.getStrIsKey())) {
            viewHolder.  good_TextView.setVisibility(View.VISIBLE);
            viewHolder. focus_TextView.setVisibility(View.GONE);
            viewHolder. title_TextView.setText("         " + objInfo.getStrTitle());
        } else if ("1".equals(objInfo.getStrIsGood()) && "1".equals(objInfo.getStrIsKey())) {
            viewHolder. good_TextView.setVisibility(View.VISIBLE);
            viewHolder. focus_TextView.setVisibility(View.VISIBLE);
            viewHolder.  title_TextView.setText("                    " + objInfo.getStrTitle());
        } else if ("0".equals(objInfo.getStrIsGood()) && "0".equals(objInfo.getStrIsKey())) {
            viewHolder. good_TextView.setVisibility(View.GONE);
            viewHolder. focus_TextView.setVisibility(View.GONE);
            viewHolder. title_TextView.setText(objInfo.getStrTitle());
        } else if ("0".equals(objInfo.getStrIsGood()) && "1".equals(objInfo.getStrIsKey())) {
            viewHolder. good_TextView.setVisibility(View.GONE);
            viewHolder. focus_TextView.setVisibility(View.VISIBLE);
            viewHolder. title_TextView.setText("         " + objInfo.getStrTitle());
        } else {
            viewHolder. title_TextView.setText(objInfo.getStrTitle());
        }

        String strShowInfo = Utils.getShowContent(objInfo.getStrRecUnitName(), objInfo.getStrHandlerTypeName());
        if (GIStringUtil.isNotBlank(strShowInfo)) {
            viewHolder. unit_TextView.setVisibility(View.VISIBLE);
            viewHolder. unit_TextView.setText(strShowInfo);
        } else {
            viewHolder. unit_TextView.setVisibility(View.GONE);
        }
        viewHolder. name_TextView.setText(objInfo.getStrSourceName());
        viewHolder. date_TextView.setText(objInfo.getStrReportDate());
        if ("1".equals(objInfo.getStrHasSecondHandler())) {
            viewHolder. handlerTwo_TextView.setVisibility(View.VISIBLE);
            viewHolder.  handlerTwo_TextView.setText("二次办理");
        } else {
            viewHolder. handlerTwo_TextView.setVisibility(View.GONE);
        }
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;//案由
        private TextView caseNo_TextView;//案号
        private TextView name_TextView;//提案人
        private TextView unit_TextView;//承办单位
        private TextView date_TextView;//日期
        private TextView state_TextView;//状态
        private TextView good_TextView;//优秀
        private TextView focus_TextView;//重点
        private TextView handlerTwo_TextView;//二次办理

        public ViewHolder(View itemView) {
            super(itemView);
            title_TextView = (TextView) itemView.findViewById(R.id.tv_title);
            good_TextView = (TextView) itemView.findViewById(R.id.tv_good);
            focus_TextView = (TextView) itemView.findViewById(R.id.tv_focus);
            name_TextView = (TextView) itemView.findViewById(R.id.tv_name);
            caseNo_TextView = (TextView) itemView.findViewById(R.id.tv_caseNo);
            unit_TextView = (TextView) itemView.findViewById(R.id.tv_unit);
            date_TextView = (TextView) itemView.findViewById(R.id.tv_date);
            state_TextView = (TextView) itemView.findViewById(R.id.tv_state);
            handlerTwo_TextView = (TextView) itemView.findViewById(R.id.tv_handler_two);
        }

    }
}
