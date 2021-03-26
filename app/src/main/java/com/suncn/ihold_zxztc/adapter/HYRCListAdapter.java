package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.bean.HYRCListBean;

import org.jetbrains.annotations.NotNull;


public class HYRCListAdapter extends BaseQuickAdapter<HYRCListBean.HYRCList, HYRCListAdapter.ViewHolder> {
    private Context context;

    public HYRCListAdapter(Context context) {
        super(R.layout.item_hyrc_list);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, HYRCListBean.HYRCList objInfo) {
        viewHolder.tvTime.setText(objInfo.getStrTime());
        viewHolder.tvMeet.setText(objInfo.getStrName());
        viewHolder.tvPlace.setText(objInfo.getStrPlace());
        if (getItemPosition(objInfo) == 0) {
            viewHolder.lineTop.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.lineTop.setVisibility(View.VISIBLE);
        }
        if (getItemPosition(objInfo) == getItemCount() - 1) {
            viewHolder.linebottom.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.linebottom.setVisibility(View.VISIBLE);
        }
        viewHolder.llView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                bundle.putString("strUrl", objInfo.getStrUrl());
                bundle.putString("headTitle", context.getString(R.string.string_meeting_schedule));
                intent.setClass(context, WebViewActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView tvMeet;
        private TextView tvPlace;
        private TextView tvTime;
        private LinearLayout lineTop;
        private LinearLayout linebottom;
        private LinearLayout llView;


        public ViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvMeet = (TextView) itemView.findViewById(R.id.tv_meet);
            tvPlace = (TextView) itemView.findViewById(R.id.tv_place);
            lineTop = (LinearLayout) itemView.findViewById(R.id.line_top);
            linebottom = (LinearLayout) itemView.findViewById(R.id.line_bot);
            llView = (LinearLayout) itemView.findViewById(R.id.ll_view);
        }
    }
}
