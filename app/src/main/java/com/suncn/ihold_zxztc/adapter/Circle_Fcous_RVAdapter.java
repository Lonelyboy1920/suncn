package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.circle.Circle_FocusListActivity;
import com.suncn.ihold_zxztc.bean.CircleListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

public class Circle_Fcous_RVAdapter extends BaseQuickAdapter<CircleListBean.RecommendBean, Circle_Fcous_RVAdapter.ViewHolder> {
    private Context context;
    private String type = "0";//0表示已关注，1表示未关注

    public Circle_Fcous_RVAdapter(Context context) {
        super(R.layout.item_rv_circle_fcous);
        this.context = context;
    }


    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, CircleListBean.RecommendBean objInfo) {
        GIImageUtil.loadImg(context, viewHolder.ivImg, Utils.formatFileUrl(context, objInfo.getStrPhotoPath()), 1);
        viewHolder.tvName.setText(objInfo.getStrUserName());
        viewHolder.tvDuty.setText(objInfo.getStrDuty());
        viewHolder.tvSector.setText(objInfo.getStrSector());
        if ("0".equals(type)) {
            viewHolder.ivFollow.setImageDrawable(context.getResources().getDrawable(R.mipmap.notice_icon));
            viewHolder.tvFollow.setText(context.getString(R.string.string_followed));
            viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.font_content));
        } else {
            viewHolder.ivFollow.setImageDrawable(context.getResources().getDrawable(R.mipmap.not_notice_icon));
            viewHolder.tvFollow.setText(context.getString(R.string.string_follow));
            viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.zxta_state_red));
        }
        viewHolder.llFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("1".equals(objInfo.getStrLoginUseState())) {
                    GIToastUtil.showMessage(context, context.getString(R.string.string_cannot_focus_on_yourself));
                    return;
                }
                ((Circle_FocusListActivity) context).followUser(objInfo.getStrUserId(), objInfo.getStrUserName());
                if (viewHolder.tvFollow.getText().equals(context.getString(R.string.string_followed))) {
                    GIToastUtil.showMessage(context, context.getString(R.string.have_alerday_cancle_followed) + objInfo.getStrUserName());
                    viewHolder.ivFollow.setImageDrawable(context.getResources().getDrawable(R.mipmap.not_notice_icon));
                    viewHolder.tvFollow.setText(context.getString(R.string.string_follow));
                    viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.zxta_state_red));
                } else {
                    GIToastUtil.showMessage(context, context.getString(R.string.string_have_alerday_followed) + objInfo.getStrUserName());
                    viewHolder.ivFollow.setImageDrawable(context.getResources().getDrawable(R.mipmap.notice_icon));
                    viewHolder.tvFollow.setText(context.getString(R.string.string_followed));
                    viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.font_content));
                }
            }
        });
    }

    public class ViewHolder extends BaseViewHolder {
        private RoundImageView ivImg;
        private TextView tvName;
        private TextView tvSector;
        private TextView tvDuty;
        private LinearLayout llFollow;
        private ImageView ivFollow;
        private TextView tvFollow;


        public ViewHolder(View itemView) {
            super(itemView);
            ivImg =  itemView.findViewById(R.id.iv_img);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSector = itemView.findViewById(R.id.tv_sector);
            tvDuty =  itemView.findViewById(R.id.tv_duty);
            llFollow =  itemView.findViewById(R.id.ll_follow);
            ivFollow =  itemView.findViewById(R.id.iv_follow);
            tvFollow =  itemView.findViewById(R.id.tv_follow);

        }

    }
}
