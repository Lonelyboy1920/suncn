package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.BaseMeetBean;
import org.jetbrains.annotations.NotNull;

/**
 * 会议列表
 */
public class BaseMeet_Main_RVAdapter extends BaseQuickAdapter<BaseMeetBean.BaseMeet, BaseMeet_Main_RVAdapter.ViewHolder> {
    private Context context;
    private String intType;

    public BaseMeet_Main_RVAdapter(Context context, String intType) {
        super(R.layout.item_rv_base_meet);
        this.context = context;
        this.intType = intType;
    }

    @Override
    protected void convert(@NotNull BaseMeet_Main_RVAdapter.ViewHolder baseViewHolder, BaseMeetBean.BaseMeet objInfo) {
        baseViewHolder.tvTitle.setText(objInfo.getStrName());
        baseViewHolder.tvDate.setText(objInfo.getStrStartDate() + "至" + objInfo.getStrEndDate());
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvState;
        private TextView tv_user;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvState = itemView.findViewById(R.id.tv_state);
            tv_user = itemView.findViewById(R.id.tv_user);
        }

    }
}
