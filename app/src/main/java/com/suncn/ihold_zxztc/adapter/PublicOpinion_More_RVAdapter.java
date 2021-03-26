package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MorePublicOpinionsListBean;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 更多刊物adapter
 */
public class PublicOpinion_More_RVAdapter extends BaseQuickAdapter<MorePublicOpinionsListBean.MorePublicOpinionBean, PublicOpinion_More_RVAdapter.ViewHolder> {
    private Context context;

    public PublicOpinion_More_RVAdapter(Context context) {
        super(R.layout.item_rv_publicopinion);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, MorePublicOpinionsListBean.MorePublicOpinionBean objInfo) {
        baseViewHolder.title_TextView.setText(objInfo.getStrTitle());
        baseViewHolder.date_TextView.setText(objInfo.getStrPubDate());
        baseViewHolder.state_TextView.setTextColor(context.getResources().getColor(R.color.font_source));
        String strState = objInfo.getStrType() + " " + objInfo.getStrIssue();
        baseViewHolder.state_TextView.setText(strState);
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;
        private TextView date_TextView;
        private TextView state_TextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.tv_meet_name).setVisibility(View.GONE);
            title_TextView = (TextView) itemView.findViewById(R.id.tv_title);
            date_TextView = (TextView) itemView.findViewById(R.id.tv_date);
            state_TextView = (TextView) itemView.findViewById(R.id.tv_state);
        }

    }
}
