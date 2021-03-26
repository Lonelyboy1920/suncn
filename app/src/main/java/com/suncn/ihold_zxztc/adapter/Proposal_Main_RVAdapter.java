package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
 * 提案列表adapter
 */
public class Proposal_Main_RVAdapter extends BaseQuickAdapter<ProposalListBean.ProposalBean, Proposal_Main_RVAdapter.ViewHolder> {
    private Context context;
    private int intType;

    public Proposal_Main_RVAdapter(Context context, int intType) {
        super(R.layout.item_rv_proposal_main);
        this.context = context;
        this.intType = intType;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, ProposalListBean.ProposalBean objInfo) {
        //            title_TextView.setText(objInfo.getStrTitle());
        if (intType == 0) {
            baseViewHolder.date_TextView.setText(objInfo.getStrReportDate());
        } else {
            baseViewHolder.date_TextView.setText(objInfo.getStrSourceName() + " " + objInfo.getStrReportDate());
        }
        String strTitle = objInfo.getStrTitle();
        Drawable drawable = null;
        if ("1".equals(objInfo.getStrIsGood()) && "1".equals(objInfo.getStrIsKey())) {
            drawable = context.getResources().getDrawable(R.mipmap.icon_good_key);
        } else if ("1".equals(objInfo.getStrIsGood())) {
            drawable = context.getResources().getDrawable(R.mipmap.icon_good);
        } else if ("1".equals(objInfo.getStrIsKey())) {
            drawable = context.getResources().getDrawable(R.mipmap.icon_key);
        } else {
            drawable = null;
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            baseViewHolder.title_TextView.setCompoundDrawables(drawable, null, null, null);
        }else {
            baseViewHolder.title_TextView.setCompoundDrawables(null, null, null, null);
        }
        baseViewHolder.title_TextView.setText(strTitle);
        String strState = GIStringUtil.isNotBlank(objInfo.getStrFlowState()) ? objInfo.getStrFlowState() : GIStringUtil.isNotBlank(objInfo.getStrAttendName()) ? objInfo.getStrAttendName() : "";
        int textColor;
        if (context.getString(R.string.string_filed).equals(strState)
                || context.getString(R.string.string_jointly).equals(strState)
                || context.getString(R.string.string_seconded).equals(strState)
                || context.getString(R.string.string_in_process).equals(strState)
                || context.getString(R.string.string_agree_to_second).equals(strState)) {//绿色
            textColor = context.getResources().getColor(R.color.zxta_state_green);
        } else if (context.getString(R.string.string_not_filed).equals(strState)
                || context.getString(R.string.string_disagree_filed).equals(strState)) {
            textColor = context.getResources().getColor(R.color.red);
        } else {
            textColor = context.getResources().getColor(R.color.font_source);
        }
        baseViewHolder.state_TextView.setTextColor(textColor);
        baseViewHolder.state_TextView.setText(strState);
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;
        private GITextView date_TextView;
        private GITextView state_TextView;
        private GITextView focus_TextView;
        private GITextView good_TextView;//优秀

        public ViewHolder(View itemView) {
            super(itemView);
            title_TextView = (TextView) itemView.findViewById(R.id.tv_title);
            date_TextView = (GITextView) itemView.findViewById(R.id.tv_date);
            state_TextView = (GITextView) itemView.findViewById(R.id.tv_state);
            focus_TextView = (GITextView) itemView.findViewById(R.id.tv_focus);
            good_TextView = (GITextView) itemView.findViewById(R.id.tv_good);
        }

    }
}
