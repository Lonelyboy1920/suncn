package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.AuditInfoListBean;

import org.jetbrains.annotations.NotNull;

public class Proposal_Audit_ProSign_RVAdapter extends BaseQuickAdapter<AuditInfoListBean.ProSign, Proposal_Audit_ProSign_RVAdapter.ViewHolder> {
    private Context context;

    public Proposal_Audit_ProSign_RVAdapter(Context context) {
        super(R.layout.item_rv_proposal_auditinfo);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, AuditInfoListBean.ProSign objInfo) {
        if (getItemPosition(objInfo) == 0) {
            viewHolder.currentNode_TextView.setText(context.getString(R.string.string_pre_sign_comments));
        } else {
            viewHolder.currentNode_TextView.setVisibility(View.GONE);
        }
        viewHolder. result_TextView.setText(objInfo.getStrMemFeedBackState());
        viewHolder.unit_TextView.setText(objInfo.getStrRecUnitName());
        viewHolder.idea_TextView.setText(objInfo.getStrBackIdea());
    }


    public class ViewHolder extends BaseViewHolder {
        private LinearLayout unit_LinearLayout;
        private TextView result_TextView;
        private TextView unit_TextView;
        private TextView idea_TextView;
        private TextView currentNode_TextView;

        public ViewHolder(View view) {
            super(view);
            view.findViewById(R.id.ll_check).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.tv_result_label)).setText(context.getString(R.string.string_sign_comments));
            result_TextView = (TextView) view.findViewById(R.id.tv_result);
            unit_LinearLayout = (LinearLayout) view.findViewById(R.id.ll_unit);
            unit_TextView = (TextView) view.findViewById(R.id.tv_unit);
            idea_TextView = (TextView) view.findViewById(R.id.tv_idea);
            currentNode_TextView = (TextView) view.findViewById(R.id.tv_current_node);
        }

    }
}
