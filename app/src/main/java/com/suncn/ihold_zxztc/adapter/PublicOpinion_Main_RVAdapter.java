package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.PublicOpinionListBean;

import org.jetbrains.annotations.NotNull;

/**
 * 社情民意adapter
 */
public class PublicOpinion_Main_RVAdapter extends BaseQuickAdapter<PublicOpinionListBean.ObjListBean, PublicOpinion_Main_RVAdapter.ViewHolder> {
    private Context context;

    public PublicOpinion_Main_RVAdapter(Context context) {
        super(R.layout.item_rv_publicopinion);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, PublicOpinionListBean.ObjListBean objInfo) {
        viewHolder.title_TextView.setText(objInfo.getStrTitle());
        viewHolder.date_TextView.setText(objInfo.getStrPubDate());
        String strState = objInfo.getStrStateName();
        viewHolder.state_TextView.setText(strState);
        int textColor;
        //审核中（黑色）、已通过（绿色）、未通过（红色）、未采编（黑色）、已采编（绿色）
        if (context.getString(R.string.string_under_review).equals(strState)) {
            textColor = context.getResources().getColor(R.color.font_title);
        } else if (context.getString(R.string.string_passed).equals(strState)) {
            textColor = context.getResources().getColor(R.color.zxta_state_green);
        } else if (context.getString(R.string.string_unpass).equals(strState)) {
            textColor = context.getResources().getColor(R.color.red);
        } else if (context.getString(R.string.string_unedited).equals(strState)) {
            textColor = context.getResources().getColor(R.color.font_title);
        } else if (context.getString(R.string.string_edited).equals(strState)) {
            textColor = context.getResources().getColor(R.color.zxta_state_green);
        } else {
            textColor = context.getResources().getColor(R.color.red);
        }
        viewHolder.state_TextView.setTextColor(textColor);
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;
        private TextView date_TextView;
        private TextView state_TextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.tv_meet_name).setVisibility(View.GONE);
            title_TextView = itemView.findViewById(R.id.tv_title);
            date_TextView = itemView.findViewById(R.id.tv_date);
            state_TextView = itemView.findViewById(R.id.tv_state);
        }
    }
}
