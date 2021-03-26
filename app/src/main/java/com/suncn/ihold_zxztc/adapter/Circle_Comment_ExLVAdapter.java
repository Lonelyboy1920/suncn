package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.circle.Circle_DetailActivity;
import com.suncn.ihold_zxztc.bean.ReplyBean;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ShowAllTextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Moos
 * E-mail: moosphon@gmail.com
 * Date:  18/4/20.
 * Desc: 评论与回复列表的适配器
 */
public class Circle_Comment_ExLVAdapter extends BaseExpandableListAdapter {
    private ArrayList<ReplyBean> commentBeanList;
    private Context context;
    private Set<Integer> showMoreCommentPosition = new HashSet<>();

    public Circle_Comment_ExLVAdapter(Context context, ArrayList<ReplyBean> commentBeanList) {
        this.context = context;
        this.commentBeanList = commentBeanList;
    }

    public void addCommentBean(ArrayList<ReplyBean> commentBeanList) {
        if (this.commentBeanList == null) {
            this.commentBeanList = commentBeanList;
        } else {
            for (ReplyBean commentDetail : commentBeanList) {
                this.commentBeanList.add(commentDetail);
            }
        }
    }

    @Override
    public int getGroupCount() {
        if (commentBeanList != null) {
            return commentBeanList.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        if (commentBeanList.get(i).getChildList() == null) {
            return 0;
        } else {

            return commentBeanList.get(i).getChildList().size();
        }
    }

    @Override
    public Object getGroup(int i) {
        return commentBeanList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return commentBeanList.get(i).getChildList().get(i1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
        final GroupHolder groupHolder;
        final ReplyBean info = commentBeanList.get(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_comment_group, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (groupPosition == getGroupCount() - 1 || getChildrenCount(groupPosition) > 0) {
            groupHolder.viewLine.setVisibility(View.INVISIBLE);
        } else {
            groupHolder.viewLine.setVisibility(View.VISIBLE);
        }
        GIImageUtil.loadImg(context, groupHolder.ivHeader, Utils.formatFileUrl(context, info.getStrReplyUserPhoto()), 1);
        groupHolder.tvZanCount.setText(info.getIntPraiseCount() + "");
        if ("1".equals(info.getLoginUserPraiseState())) {
            groupHolder.iv_like.setText(context.getResources().getString(R.string.font_zaned));
            groupHolder.iv_like.setTextColor(context.getResources().getColor(R.color.line_has_zan));
            groupHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.line_has_zan));
        } else {
            groupHolder.iv_like.setText(context.getResources().getString(R.string.font_zan));
            groupHolder.iv_like.setTextColor(context.getResources().getColor(R.color.font_source));
            groupHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.font_source));

        }
        if ("1".equals(info.getStrHasDel())) {
            groupHolder.comment_item_delete.setVisibility(View.VISIBLE);
        } else {
            groupHolder.comment_item_delete.setVisibility(View.GONE);
        }
        groupHolder.tv_name.setText(info.getStrReplyUserName());
        if (info.isShowAllLines()) {
            groupHolder.tv_content.setMaxShowLines(0);
        } else {
            groupHolder.tv_content.setMaxShowLines(5);
        }
        groupHolder.tv_content.setMyText(info.getStrReplyContent());
        groupHolder.tv_content.setId(groupPosition);
        groupHolder.tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = v.getId();
                final ReplyBean objInfo = commentBeanList.get(groupPosition);
                objInfo.setShowAllLines(true);
                groupHolder.tv_content.setMaxShowLines(0);
                groupHolder.tv_content.setMyText(objInfo.getStrReplyContent());
                //notifyItemChanged(getAdapterPosition());
            }
        });
        groupHolder.tv_time.setText(info.getStrReplyDate());
        groupHolder.llZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("1".equals(info.getLoginUserPraiseState())) {
                    return;
                }
                groupHolder.iv_like.startAnimation(AnimationUtils.loadAnimation(
                        context, R.anim.anim_dianzan));
                ((Circle_DetailActivity) context).setOnClick(info, v, 0);
                groupHolder.iv_like.setText(context.getResources().getString(R.string.font_zaned));
                groupHolder.iv_like.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                groupHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                info.setIntPraiseCount(info.getIntPraiseCount() + 1);
                info.setLoginUserPraiseState("1");
                groupHolder.tvZanCount.setText(info.getIntPraiseCount() + "");
            }
        });
        groupHolder.comment_item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAnimatorSet mBasIn = new BounceTopEnter();
                BaseAnimatorSet mBasOut = new BounceTopEnter();
                try {
                    mBasIn = BounceEnter.class.newInstance();
                    mBasOut = ZoomOutExit.class.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final MaterialDialog dialog = new MaterialDialog(context);
                dialog.title(Utils.getMyString(R.string.string_tips)).content(Utils.getMyString(R.string.string_are_you_sure_you_want_to_delete_this_comment)).btnText(Utils.getMyString(R.string.string_cancle), Utils.getMyString(R.string.string_determine)).showAnim(mBasIn).dismissAnim(mBasOut).show();
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        }, new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                ((Circle_DetailActivity) context).setOnClick(info, v, 0);
                                commentBeanList.remove(groupPosition);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }
                );
            }
        });
        groupHolder.llReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Circle_DetailActivity) context).setOnClick(info, v, groupPosition);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;
        final ReplyBean info = (ReplyBean) commentBeanList.get(groupPosition).getChildList().get(childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_comment_child, viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        if (childPosition == getChildrenCount(groupPosition) - 1) {
            if (groupPosition == getGroupCount() - 1) {
                childHolder.viewLine.setVisibility(View.INVISIBLE);
            } else {
                childHolder.viewLine.setVisibility(View.VISIBLE);
            }
        } else {
            childHolder.viewLine.setVisibility(View.GONE);
        }
        GIImageUtil.loadImg(context, childHolder.ivHeader, Utils.formatFileUrl(context, info.getStrReplyUserPhoto()), 1);
        if ("1".equals(info.getStrHasDel())) {
            childHolder.comment_item_delete.setVisibility(View.VISIBLE);
        } else {
            childHolder.comment_item_delete.setVisibility(View.GONE);
        }
        if (!showMoreCommentPosition.contains(groupPosition) && childPosition > 2) {
            childHolder.llContent.setVisibility(View.GONE);
            childHolder.llBottom.setVisibility(View.GONE);
        } else {
            childHolder.tvZanCount.setText(info.getIntPraiseCount() + "");
            if ("1".equals(info.getLoginUserPraiseState())) {
                childHolder.iv_like.setText(context.getResources().getString(R.string.font_zaned));
                childHolder.iv_like.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                childHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.line_has_zan));
            } else {
                childHolder.iv_like.setText(context.getResources().getString(R.string.font_zan));
                childHolder.iv_like.setTextColor(context.getResources().getColor(R.color.font_source));
                childHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.font_source));
            }
            childHolder.tvDate.setText(info.getStrReplyDate());
            if (childPosition < 2 || showMoreCommentPosition.contains(groupPosition)) {
                String replyUser = ((ReplyBean) commentBeanList.get(groupPosition).getChildList().get(childPosition)).getStrReplyUserName();
                childHolder.llContent.setVisibility(View.VISIBLE);
                childHolder.llBottom.setVisibility(View.GONE);
                if (GIStringUtil.isNotBlank(replyUser) && GIStringUtil.isNotBlank(info.getStrRepliedUserId())) {
                    SpannableString spannableString = new SpannableString(replyUser + "回复" + info.getStrRepliedUserName());
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#999999"));
                    spannableString.setSpan(colorSpan, replyUser.length(), replyUser.length()+2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    childHolder.tv_name.setText(spannableString);
                } else if (GIStringUtil.isNotBlank(replyUser)) {
                    childHolder.tv_name.setText(replyUser);
                } else {
                    childHolder.tv_name.setText("");
                }
                if (info.isShowAllLines()) {
                    childHolder.tv_content.setMaxShowLines(0);
                } else {
                    childHolder.tv_content.setMaxShowLines(5);
                }
                childHolder.tv_content.setMyText(((ReplyBean) commentBeanList.get(groupPosition).getChildList().get(childPosition)).getStrReplyContent());
                childHolder.tv_content.setTag(R.id.tag_gpos, groupPosition);
                childHolder.tv_content.setTag(R.id.tag_cpos, childPosition);
                childHolder.tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int gpos = (int) view.getTag(R.id.tag_gpos);
                        int cpos = (int) view.getTag(R.id.tag_cpos);
                        final ReplyBean info = (ReplyBean) commentBeanList.get(gpos).getChildList().get(cpos);
                        info.setShowAllLines(true);
                        childHolder.tv_content.setMaxShowLines(0);
                        childHolder.tv_content.setMyText(info.getStrReplyContent());
                        //notifyItemChanged(getAdapterPosition());
                    }
                });
            } else {
                childHolder.llContent.setVisibility(View.GONE);
                childHolder.llBottom.setVisibility(View.VISIBLE);
                childHolder.tvMore.setText("查看全部" + commentBeanList.get(groupPosition).getChildList().size() + "条回复>");
                childHolder.llBottom.setId(groupPosition);
                childHolder.llBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMoreCommentPosition.add(v.getId());
                        notifyDataSetChanged();
                    }
                });
            }
            childHolder.comment_item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseAnimatorSet mBasIn = new BounceTopEnter();
                    BaseAnimatorSet mBasOut = new BounceTopEnter();
                    try {
                        mBasIn = BounceEnter.class.newInstance();
                        mBasOut = ZoomOutExit.class.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final MaterialDialog dialog = new MaterialDialog(context);
                    dialog.title(Utils.getMyString(R.string.string_tips)).content(Utils.getMyString(R.string.string_are_you_sure_you_want_to_delete_this_comment)).btnText(Utils.getMyString(R.string.string_cancle), Utils.getMyString(R.string.string_determine)).showAnim(mBasIn).dismissAnim(mBasOut).show();
                    dialog.setOnBtnClickL(
                            new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {
                                    dialog.dismiss();
                                }
                            }, new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {
                                    ((Circle_DetailActivity) context).setOnClick(info, v, 0);
                                    commentBeanList.get(groupPosition).getChildList().remove(childPosition);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }
                    );
                }
            });
            childHolder.llZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("1".equals(info.getLoginUserPraiseState())) {
                        return;
                    }
                    childHolder.iv_like.startAnimation(AnimationUtils.loadAnimation(
                            context, R.anim.anim_dianzan));
                    ((Circle_DetailActivity) context).setOnClick(info, v, 0);
                    childHolder.iv_like.setText(context.getResources().getString(R.string.font_zaned));
                    childHolder.iv_like.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                    childHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                    info.setIntPraiseCount(info.getIntPraiseCount() + 1);
                    info.setLoginUserPraiseState("1");
                    childHolder.tvZanCount.setText(info.getIntPraiseCount() + "");
                }
            });
            childHolder.ll_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Circle_DetailActivity) context).setOnClick(info, v, groupPosition);
                }
            });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder {
        private TextView tv_name, tv_time;
        private ShowAllTextView tv_content;
        private TextView iv_like;
        private LinearLayout llZan;
        private LinearLayout llReplay;
        private TextView tvZanCount;
        private View viewLine;
        private RoundImageView ivHeader;
        private GITextView comment_item_delete;

        public GroupHolder(View view) {
            ivHeader = view.findViewById(R.id.comment_item_logo);
            tv_content = (ShowAllTextView) view.findViewById(R.id.comment_item_content);
            tv_name = (TextView) view.findViewById(R.id.comment_item_userName);
            tv_time = (TextView) view.findViewById(R.id.comment_item_time);
            iv_like = (TextView) view.findViewById(R.id.comment_item_like);
            llZan = (LinearLayout) view.findViewById(R.id.ll_zan);
            llReplay = (LinearLayout) view.findViewById(R.id.ll_reply);
            tvZanCount = (TextView) view.findViewById(R.id.tv_zan_count);
            viewLine = view.findViewById(R.id.view_line);
            comment_item_delete = view.findViewById(R.id.comment_item_delete);
        }
    }

    private class ChildHolder {
        private TextView tv_name;
        private ShowAllTextView tv_content;
        private RelativeLayout llContent;
        private TextView tvMore;
        private LinearLayout llBottom;
        private TextView iv_like;
        private LinearLayout llZan;
        private TextView tvZanCount;
        private TextView tvDate;
        private View viewLine;
        private RoundImageView ivHeader;
        private GITextView comment_item_delete;
        private LinearLayout ll_reply;

        public ChildHolder(View view) {
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
            comment_item_delete = view.findViewById(R.id.comment_item_delete);
            ll_reply = view.findViewById(R.id.ll_reply_child);
        }
    }


    /**
     * by moos on 2018/04/20
     * func:评论成功后插入一条数据
     *
     * @param commentDetailBean 新的评论数据
     */
    public void addTheCommentData(ReplyBean commentDetailBean) {
        if (commentDetailBean != null) {
            commentBeanList.add(0, commentDetailBean);
            notifyDataSetChanged();
        } else {
            throw new IllegalArgumentException("评论数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:回复成功后插入一条数据
     *
     * @param replyDetailBean 新的回复数据
     */
    public void addTheReplyData(ReplyBean replyDetailBean, int groupPosition) {
        if (replyDetailBean != null) {
            if (commentBeanList.get(groupPosition).getChildList() != null) {
                commentBeanList.get(groupPosition).getChildList().add(0, replyDetailBean);
            } else {
                ArrayList<ReplyBean> replyList = new ArrayList<>();
                replyList.add(replyDetailBean);
                commentBeanList.get(groupPosition).setChildList(replyList);
            }
            notifyDataSetChanged();
        } else {
            throw new IllegalArgumentException("回复数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:添加和展示所有回复
     *
     * @param replyBeanList 所有回复数据
     * @param groupPosition 当前的评论
     */
    private void addReplyList(ArrayList<ReplyBean> replyBeanList, int groupPosition) {
        if (commentBeanList.get(groupPosition).getChildList() != null) {
            commentBeanList.get(groupPosition).getChildList().clear();
            commentBeanList.get(groupPosition).getChildList().addAll(replyBeanList);
        } else {
            commentBeanList.get(groupPosition).setChildList(replyBeanList);
        }
        notifyDataSetChanged();
    }
}
