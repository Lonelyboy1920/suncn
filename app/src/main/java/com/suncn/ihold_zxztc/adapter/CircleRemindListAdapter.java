package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.circle.ReminNoticeListActivity;
import com.suncn.ihold_zxztc.bean.MsgRemindListBean;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ShowAllSpan;
import com.suncn.ihold_zxztc.view.ShowAllTextView;
import com.suncn.ihold_zxztc.view.ShowMoreInfoDialog;

import org.jetbrains.annotations.NotNull;

public class CircleRemindListAdapter extends BaseQuickAdapter<MsgRemindListBean.msgRemindBean, CircleRemindListAdapter.ViewHolder> {
    private Context context;
    private String type = "0";//0表示已关注，1表示未关注

    public CircleRemindListAdapter(Context context) {
        super(R.layout.item_circle_remind);
        this.context = context;
    }


    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, MsgRemindListBean.msgRemindBean objInfo) {
        viewHolder.tvContent.setMyText(GIUtil.showHtmlInfo(objInfo.getStrMsgContent()));
        viewHolder.tvContent.setMaxShowLines(2);
        viewHolder.tvTime.setText(objInfo.getStrDistanceTime());
        viewHolder.tvContent.setOnAllSpanClickListener(new ShowAllSpan.OnAllSpanClickListener() {
            @Override
            public void onClick(View widget) {
                ShowMoreInfoDialog showMoreInfoDialog = new ShowMoreInfoDialog(context, objInfo.getStrMsgContent(), context.getString(R.string.string_reminder_content));
                showMoreInfoDialog.show();
            }
        });
        if (GIStringUtil.isBlank(objInfo.getStrFollowState())) {
            viewHolder.ivImg.setVisibility(View.GONE);
            viewHolder.llBtn.setVisibility(View.GONE);
            if ("0".equals(objInfo.getStrMobileState())) {
                viewHolder.tvIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvIcon.setVisibility(View.GONE);
            }
        } else {
            viewHolder.tvIcon.setVisibility(View.GONE);
            GIImageUtil.loadImg(context, viewHolder.ivImg, Utils.formatFileUrl(context, objInfo.getStrPhotoPath()), 1);
            viewHolder.tvContent.setText(GIUtil.showHtmlInfo(objInfo.getStrMsgContent()));
            viewHolder.ivImg.setVisibility(View.VISIBLE);
            viewHolder.llBtn.setVisibility(View.VISIBLE);
            if ("1".equals(objInfo.getStrFollowState())) {
                viewHolder.ivFollow.setImageDrawable(context.getResources().getDrawable(R.mipmap.notice_icon));
                viewHolder.tvFollow.setText(context.getString(R.string.string_followed));
                viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.font_content));
                viewHolder.llBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                            ((ReminNoticeListActivity) context).followUser(objInfo.getStrUserId(), objInfo.getStrUserName());
//                            objInfo.setStrFollowState("0");
                    }
                });
            } else {
                viewHolder.ivFollow.setImageDrawable(context.getResources().getDrawable(R.mipmap.not_notice_icon));
                viewHolder.tvFollow.setText(context.getString(R.string.string_follow));
                viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.zxta_state_red));
                viewHolder.llBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ReminNoticeListActivity) context).followUser(objInfo.getStrUserId(), objInfo.getStrUserName());
                        objInfo.setStrFollowState("1");
                        ((ReminNoticeListActivity) context).showToast(context.getString(R.string.string_have_alerday_followed) + objInfo.getStrUserName());
                    }
                });

            }
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private RoundImageView ivImg;
        private ShowAllTextView tvContent;
        private LinearLayout llBtn;
        private ImageView ivFollow;
        private TextView tvFollow;
        private TextView tvIcon;
        private TextView tvTime;


        public ViewHolder(View itemView) {
            super(itemView);
            ivImg = (RoundImageView) itemView.findViewById(R.id.iv_img);
            tvContent = (ShowAllTextView) itemView.findViewById(R.id.tv_content);
            llBtn = (LinearLayout) itemView.findViewById(R.id.ll_btn);
            ivFollow = (ImageView) itemView.findViewById(R.id.iv_follow);
            tvFollow = (TextView) itemView.findViewById(R.id.tv_follow);
            tvIcon = (TextView) itemView.findViewById(R.id.tv_icon);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);


        }

    }
}
