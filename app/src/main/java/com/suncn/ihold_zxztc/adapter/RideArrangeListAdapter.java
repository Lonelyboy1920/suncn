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
import com.suncn.ihold_zxztc.bean.HYRCListBean;
import com.suncn.ihold_zxztc.bean.RideArrangeListBean;

import org.jetbrains.annotations.NotNull;


public class RideArrangeListAdapter extends BaseQuickAdapter<RideArrangeListBean.RideArrange, RideArrangeListAdapter.ViewHolder> {
    private Context context;

    public RideArrangeListAdapter(Context context) {
        super(R.layout.item_ride_arrange);
        this.context = context;
    }


    @Override
    protected void convert(@NotNull ViewHolder viewHolder, RideArrangeListBean.RideArrange objInfo) {
        viewHolder.tvEndPlace.setText(objInfo.getStrMeetingPlace());
        viewHolder.tvStartPlace.setText(objInfo.getStrCarPlace());
        viewHolder.tvMeet.setText(objInfo.getStrName());
        viewHolder.tvTime.setText(objInfo.getStrTime() + " 发车");
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView tvMeet;
        private TextView tvStartPlace;
        private TextView tvTime;
        private TextView tvEndPlace;


        public ViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvMeet = (TextView) itemView.findViewById(R.id.tv_meet_name);
            tvStartPlace = (TextView) itemView.findViewById(R.id.tv_start_place);
            tvEndPlace = (TextView) itemView.findViewById(R.id.tv_end_place);

        }

    }
}
