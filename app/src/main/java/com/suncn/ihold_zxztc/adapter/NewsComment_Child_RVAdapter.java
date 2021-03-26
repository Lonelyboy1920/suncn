package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.hot.NewsDetailActivity;
import com.suncn.ihold_zxztc.bean.ReplyBean;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ShowAllTextView;

import org.jetbrains.annotations.NotNull;

/**
 * 首页新闻详情评论的回复adapter
 */
public class NewsComment_Child_RVAdapter extends BaseQuickAdapter<ReplyBean, NewsComment_Child_RVAdapter.ViewHolder> {
    private Context context;
    private NewsDetailActivity activity;
    private int intUserRole;
    private int intGroupPos;
    private boolean isShowAllChild; // 是否显示所有子评论

    NewsComment_Child_RVAdapter(Context context, int intGroupPos, boolean isShowAllChild) {
        super(R.layout.item_exlv_comment_child);
        this.context = context;
        activity = (NewsDetailActivity) context;
        this.intGroupPos = intGroupPos;
        this.isShowAllChild = isShowAllChild;
        intUserRole = GISharedPreUtil.getInt(context, "intUserRole");
    }

    public boolean isShowAllChild() {
        return isShowAllChild;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ReplyBean info) {
        if (getItemPosition(info) == getItemCount() - 1) {
            viewHolder.viewLine.setVisibility(View.VISIBLE);
        } else {
            viewHolder.viewLine.setVisibility(View.GONE);
        }
        GIImageUtil.loadImg(context,  viewHolder.ivHeader, Utils.formatFileUrl(context, info.getStrReplyUserPhoto()), 1);

        if (!isShowAllChild && getItemPosition(info) > 2) {
            viewHolder.llContent.setVisibility(View.GONE);
            viewHolder.llBottom.setVisibility(View.GONE);
        } else {
            viewHolder.  tvZanCount.setText(info.getIntPraiseCount() + "");
            if ("1".equals(info.getLoginUserPraiseState())) {
                viewHolder. iv_like.setText(context.getResources().getString(R.string.font_zaned));
                viewHolder.iv_like.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                viewHolder. tvZanCount.setTextColor(context.getResources().getColor(R.color.line_has_zan));
            } else {
                viewHolder.iv_like.setText(context.getResources().getString(R.string.font_zan));
                viewHolder.iv_like.setTextColor(context.getResources().getColor(R.color.font_source));
                viewHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.font_source));
            }
            viewHolder. tvDate.setText(info.getStrReplyDate());
            if (isShowAllChild || getItemPosition(info) < 2) {
                String replyUser = info.getStrReplyUserName();
                viewHolder. llContent.setVisibility(View.VISIBLE);
                viewHolder. llBottom.setVisibility(View.GONE);
                viewHolder.tv_name.setText(replyUser);
                if (info.isShowAllLines()) {
                    viewHolder. tv_content.setMaxShowLines(0);
                } else {
                    viewHolder. tv_content.setMaxShowLines(5);
                }
                viewHolder. tv_content.setMyText(info.getStrReplyContent());
                viewHolder.tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        info.setShowAllLines(true);
                        viewHolder.tv_content.setMaxShowLines(0);
                        viewHolder.tv_content.setMyText(info.getStrReplyContent());
                    }
                });
            } else {
                viewHolder. llContent.setVisibility(View.GONE);
                viewHolder. llBottom.setVisibility(View.VISIBLE);
                viewHolder. tvMore.setText("查看全部" + getItemCount() + "条回复>");
                viewHolder. llBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isShowAllChild = true;
                        notifyDataSetChanged();
                    }
                });
            }
            // 评论是否可删除（1-可删除；0-不可删除）| 机关端可以删除所有评论，接口处理strHideState返回1
            if ("1".equals(info.getStrHideState())) {
                viewHolder.comment_DeleteTextView.setVisibility(View.VISIBLE);
                viewHolder.comment_DeleteTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.OnPartClick(info, view, intGroupPos,getItemPosition(info));
                    }
                });
            } else {
                viewHolder.comment_DeleteTextView.setVisibility(View.GONE);
            }
            viewHolder. llZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("1".equals(info.getLoginUserPraiseState())) {
                        return;
                    }
                    activity.OnPartClick(info, view, intGroupPos, getItemPosition(info));
                }
            });
        }
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView tv_name;
        private ShowAllTextView tv_content;
        private RelativeLayout llContent;
        private TextView tvMore;
        private TextView comment_DeleteTextView;//评论删除
        private LinearLayout llBottom;
        private TextView iv_like;
        private LinearLayout llZan;
        private TextView tvZanCount;
        private TextView tvDate;
        private View viewLine;
        private RoundImageView ivHeader;

        public ViewHolder(View view) {
            super(view);
            comment_DeleteTextView = (TextView) view.findViewById(R.id.comment_item_delete);
            ivHeader = view.findViewById(R.id.iv_header);
            viewLine = view.findViewById(R.id.view_line);
            tv_name = (TextView) view.findViewById(R.id.reply_item_user);
            tv_content = (ShowAllTextView) view.findViewById(R.id.reply_item_content);
            llContent = view.findViewById(R.id.ll_content);
            tvMore = (TextView) view.findViewById(R.id.ll_more);
            llBottom = view.findViewById(R.id.ll_bottom);
            iv_like = (TextView) view.findViewById(R.id.comment_item_like);
            llZan = (LinearLayout) view.findViewById(R.id.ll_zan);
            tvZanCount = (TextView) view.findViewById(R.id.tv_zan_count);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
        }

    }
}
