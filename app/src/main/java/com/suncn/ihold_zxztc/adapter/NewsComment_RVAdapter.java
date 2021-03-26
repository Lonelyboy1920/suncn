package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.hot.NewsDetailActivity;
import com.suncn.ihold_zxztc.bean.ReplyBean;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ShowAllTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 首页新闻、主题议政 详情评论的adapter
 */
public class NewsComment_RVAdapter extends BaseQuickAdapter<ReplyBean, NewsComment_RVAdapter.ViewHolder> {
    private Context context;
    private NewsDetailActivity activity;
    private boolean isCanReply; // 是否可回复
    private int intUserRole;

    public NewsComment_RVAdapter(ArrayList<ReplyBean> data, Context context) {
        super(R.layout.item_exlv_comment_group);
        this.context = context;
        activity = (NewsDetailActivity) context;
        intUserRole = GISharedPreUtil.getInt(context, "intUserRole");
    }

    public void setCanReply(boolean canReply) {
        isCanReply = canReply;
    }

    @Override
    protected void convert(@NotNull NewsComment_RVAdapter.ViewHolder viewHolder, ReplyBean info) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                GIImageUtil.loadImg(context, viewHolder.header_ImageView, Utils.formatFileUrl(context, info.getStrReplyUserPhoto()), 1);
                // 1点赞 0未点赞
                if ("1".equals(info.getLoginUserPraiseState())) {
                    viewHolder.zan_TextView.setText(context.getResources().getString(R.string.font_zaned));
                    viewHolder.zan_TextView.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                    viewHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                } else {
                    viewHolder.zan_TextView.setText(context.getResources().getString(R.string.font_zan));
                    viewHolder.zan_TextView.setTextColor(context.getResources().getColor(R.color.font_source));
                    viewHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.font_source));
                }
                viewHolder.tvZanCount.setText(info.getIntPraiseCount() + "");
                viewHolder.name_TextView.setText(info.getStrReplyUserName());
                viewHolder.date_TextView.setText(info.getStrReplyDate());
                // 评论是否可删除（1-可删除；0-不可删除）| 机关端可以删除所有评论，接口处理strHideState返回1
                if ("1".equals(info.getStrHideState())) {
                    viewHolder.comment_DeleteTextView.setVisibility(View.VISIBLE);
                    viewHolder.comment_DeleteTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.OnPartClick(info, view, -1, getItemPosition(info));
                        }
                    });
                } else {
                    viewHolder.comment_DeleteTextView.setVisibility(View.GONE);
                }
                viewHolder.llZan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ("1".equals(info.getLoginUserPraiseState())) {
                            return;
                        }
                        activity.OnPartClick(info, view, -1, getItemPosition(info));
                        viewHolder.zan_TextView.startAnimation(AnimationUtils.loadAnimation(
                                context, R.anim.anim_dianzan));
                    }
                });
                if (info.isShowAllLines()) {
                    viewHolder.content_TextView.setMaxShowLines(0);
                } else {
                    viewHolder.content_TextView.setMaxShowLines(5);
                }
                if ("1".equals(info.getStrChoiceState())) {
                    viewHolder.select_TextView.setVisibility(View.VISIBLE);
                    viewHolder.content_TextView.setMyText("         " + info.getStrReplyContent());
                } else {
                    viewHolder.select_TextView.setVisibility(View.GONE);
                    viewHolder.content_TextView.setMyText(info.getStrReplyContent());
                }
                viewHolder.content_TextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        info.setShowAllLines(true);
                        viewHolder.content_TextView.setMaxShowLines(0);
                        if ("1".equals(info.getStrChoiceState())) {
                            viewHolder.content_TextView.setMyText("         " + info.getStrReplyContent());
                        } else {
                            viewHolder.content_TextView.setMyText(info.getStrReplyContent());
                        }
                    }
                });
                viewHolder.reply_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.OnPartClick(info, view, -1, getItemPosition(info));
                    }
                });

                ArrayList<ReplyBean> childList = info.getChildList();
                if (childList == null) {
                    childList = new ArrayList<>();
                }
                if (getItemPosition(info) == getItemCount() - 1 || childList.size() > 0) {
                    viewHolder.line_View.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.line_View.setVisibility(View.VISIBLE);
                }
                NewsComment_Child_RVAdapter adapter = new NewsComment_Child_RVAdapter(context, getItemPosition(info), info.isShowAllChild());
                info.setAdapter(adapter);
                if (childList.size() > 0) {
                    viewHolder.recyclerView.setVisibility(View.VISIBLE);
                    viewHolder.recyclerView.setAdapter(adapter);
                    adapter.setList(childList);
                } else {
                    viewHolder.recyclerView.setVisibility(View.GONE);
                }
                break;
            case 1:
                break;
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView name_TextView, date_TextView;
        private ShowAllTextView content_TextView;
        private TextView zan_TextView;
        private LinearLayout llZan;
        private LinearLayout reply_Layout;
        private TextView tvZanCount;
        private TextView comment_DeleteTextView;//评论删除
        private TextView select_TextView;//精选
        private View line_View;
        private RoundImageView header_ImageView;
        private RecyclerView recyclerView;

        public ViewHolder(View view) {
            super(view);
            header_ImageView = view.findViewById(R.id.comment_item_logo);
            content_TextView = view.findViewById(R.id.comment_item_content);
            name_TextView = (TextView) view.findViewById(R.id.comment_item_userName);
            comment_DeleteTextView = (TextView) view.findViewById(R.id.comment_item_delete);
            date_TextView = (TextView) view.findViewById(R.id.comment_item_time);
            zan_TextView = (TextView) view.findViewById(R.id.comment_item_like);
            select_TextView = (TextView) view.findViewById(R.id.tv_select);
            llZan = (LinearLayout) view.findViewById(R.id.ll_zan);
            tvZanCount = (TextView) view.findViewById(R.id.tv_zan_count);
            line_View = view.findViewById(R.id.view_line);
            reply_Layout = view.findViewById(R.id.ll_reply);
            if (isCanReply)
                reply_Layout.setVisibility(View.VISIBLE);
            else
                reply_Layout.setVisibility(View.GONE);
            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context)); //给ERV添加布局管理器
            recyclerView.setNestedScrollingEnabled(false);
        }

    }
}
