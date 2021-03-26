package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.activity.hot.HotMainFragment;
import com.suncn.ihold_zxztc.activity.hot.UnitDetailActivty;
import com.suncn.ihold_zxztc.bean.UnitListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class UnitListMain_RVAdapter extends BaseQuickAdapter<UnitListBean.UnitList, UnitListMain_RVAdapter.ViewHolder> {
    private Context context;
    private BaseFragment fragment;

    public UnitListMain_RVAdapter(BaseFragment fragment) {
        super(R.layout.item_rv_news_unit_item);
        this.context = fragment.getContext();
        this.fragment = fragment;
    }


    @Override
    protected void convert(@NotNull ViewHolder viewHolder, UnitListBean.UnitList objInfo) {
        GIImageUtil.loadImg(context, viewHolder.ivImg, Utils.formatFileUrl(context, objInfo.getStrUnit_IconPathUrl()), context.getResources().getDrawable(R.mipmap.unit_head_default));
        viewHolder.tvName.setText(objInfo.getStrUnit_name());
        if (objInfo.getIntCount().equals("0")) {
            viewHolder.tvFollow.setText(context.getString(R.string.string_subscription));
            viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.view_head_bg));
            viewHolder.tvFollow.setBackground(context.getResources().getDrawable(R.drawable.shape_et_comment_bg_red));
        } else {
            viewHolder.tvFollow.setText(context.getString(R.string.string_subscribed));
            viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.font_content));
            viewHolder.tvFollow.setBackground(context.getResources().getDrawable(R.drawable.shape_et_comment_bg));
        }
        viewHolder.tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objInfo.getIntCount().equals("0")) {
                    viewHolder.tvFollow.setText(context.getString(R.string.string_subscribed));
                    viewHolder.tvFollow.setBackground(context.getResources().getDrawable(R.drawable.shape_et_comment_bg));
                    objInfo.setIntCount("1");
                    viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.font_content));
                } else {
                    objInfo.setIntCount("0");
                    viewHolder.tvFollow.setText(context.getString(R.string.string_subscription));
                    viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.view_head_bg));
                    viewHolder.tvFollow.setBackground(context.getResources().getDrawable(R.drawable.shape_et_comment_bg_red));
                }
                ((HotMainFragment) fragment).doSubscribeNews(objInfo.getStrUnit_id());
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("strRootUnitId", objInfo.getStrUnit_id());
                bundle.putString("strState", objInfo.getIntCount());
                bundle.putString("strPhotoUrl", objInfo.getStrUnit_IconPathUrl());
                bundle.putString("strUnitName", objInfo.getStrUnit_name());
                bundle.putString("strBgUrl", objInfo.getStrUnit_BackGroundPathUrl());
                intent.setClass(context, UnitDetailActivty.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends BaseViewHolder {
        private RoundImageView ivImg;
        private TextView tvName;
        private TextView tvFollow;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_img);
            tvName = itemView.findViewById(R.id.tv_name);
            tvFollow = itemView.findViewById(R.id.tv_follow);
        }

    }
}
