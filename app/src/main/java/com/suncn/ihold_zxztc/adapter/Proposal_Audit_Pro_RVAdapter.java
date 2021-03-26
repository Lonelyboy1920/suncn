package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.AuditInfoListBean;

import org.jetbrains.annotations.NotNull;

public class Proposal_Audit_Pro_RVAdapter extends BaseQuickAdapter<AuditInfoListBean.AuditInfo, Proposal_Audit_Pro_RVAdapter.ViewHolder> {
    private Context context;

    public Proposal_Audit_Pro_RVAdapter(Context context) {
        super(R.layout.item_rv_proposal_auditinfo);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, AuditInfoListBean.AuditInfo objInfo) {
        baseViewHolder.currentNode_TextView.setText(objInfo.getStrHeadTitle());
        baseViewHolder.name_TextView.setText(objInfo.getStrCheckUserName());
        baseViewHolder.date_TextView.setText(objInfo.getStrCheckDate());
        baseViewHolder.result_TextView.setText(objInfo.getStrFlowStateName());
        baseViewHolder.tv_reason.setText(objInfo.getStrLiAnNoReason());
//            if (objInfo.getStrFlowStateName().equals("不予立案")) {
//                ll_reason.setVisibility(View.VISIBLE);
//            } else {
        baseViewHolder.ll_reason.setVisibility(View.GONE);
//            }
        if (GIStringUtil.isNotBlank(objInfo.getStrUnitName())) {
            baseViewHolder.unit_LinearLayout.setVisibility(View.GONE);
            baseViewHolder.unit_TextView.setText(objInfo.getStrUnitName());
        } else {
            baseViewHolder.unit_LinearLayout.setVisibility(View.GONE);
        }
        baseViewHolder.idea_TextView.setText(objInfo.getStrCheckIdea());
    }

    public class ViewHolder extends BaseViewHolder {
        private LinearLayout unit_LinearLayout;
        private TextView name_TextView;
        private TextView date_TextView;
        private TextView result_TextView;
        private TextView unit_TextView;
        private TextView idea_TextView;
        private TextView currentNode_TextView;
        private LinearLayout ll_reason;
        private TextView tv_reason;

        public ViewHolder(View view) {
            super(view);
            name_TextView = (TextView) view.findViewById(R.id.tv_name);
            date_TextView = (TextView) view.findViewById(R.id.tv_date);
            result_TextView = (TextView) view.findViewById(R.id.tv_result);
            unit_LinearLayout = (LinearLayout) view.findViewById(R.id.ll_unit);
            unit_TextView = (TextView) view.findViewById(R.id.tv_unit);
            idea_TextView = (TextView) view.findViewById(R.id.tv_idea);
            currentNode_TextView = (TextView) view.findViewById(R.id.tv_current_node);
            ll_reason = (LinearLayout) view.findViewById(R.id.ll_reason);
            tv_reason = (TextView) view.findViewById(R.id.tv_reason);
        }

    }
}
