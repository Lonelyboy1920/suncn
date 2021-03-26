package com.suncn.ihold_zxztc.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.goyourfly.multi_picture.MultiPictureView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.activity.circle.CirclePersonPageActivity;
import com.suncn.ihold_zxztc.activity.circle.Circle_MainFragment;
import com.suncn.ihold_zxztc.activity.global.ShowBigImgActivity;
import com.suncn.ihold_zxztc.activity.global.ShowVideoActivity;
import com.suncn.ihold_zxztc.bean.CircleListBean;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ShowAllTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 圈子主界面列表的adapter
 */
public class Circle_Main_RVAdapter extends BaseQuickAdapter<CircleListBean.DynamicBean, Circle_Main_RVAdapter.ViewHolder> {
    private Activity context;
    private BaseFragment fragment;
    private boolean isShowNotic = true;

    public Circle_Main_RVAdapter(BaseFragment fragment) {
        super(R.layout.item_rv_circle_main);
        this.fragment = fragment;
        this.context = fragment.getActivity();
    }

    public Circle_Main_RVAdapter(Activity context) {
        super(R.layout.item_rv_circle_main);
        this.context = context;
    }

    public boolean isShowNotic() {
        return isShowNotic;
    }

    public void setShowNotic(boolean showNotic) {
        isShowNotic = showNotic;
    }


    @Override
    protected void convert(@NotNull ViewHolder viewHolder, CircleListBean.DynamicBean data) {
        if ("1".equals(data.getStrShowInterestButton())) {
            viewHolder.follow_LinearLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.follow_LinearLayout.setVisibility(View.GONE);
        }
        GIImageUtil.loadImg(context, viewHolder.ivImg, Utils.formatFileUrl(context, data.getStrPhotoPath()), 1);
        viewHolder.tvName.setText(data.getStrPubUserName());
        viewHolder.tvDate.setText(data.getStrPubDate());
        viewHolder.tvZanCount.setText(data.getIntPraiseCount() + "");
        viewHolder.tvCommentCount.setText(data.getIntDiscussCount() + "");
        if (GIStringUtil.isBlank(data.getStrContent())) {
            viewHolder.tvContent.setVisibility(View.GONE);
        } else {
            viewHolder.tvContent.setVisibility(View.VISIBLE);
            viewHolder.tvContent.setMaxShowLines(3);
            viewHolder.tvContent.setMyText(data.getStrContent());
            viewHolder.tvContent.setOnAllSpanClickListener(null);
        }
        viewHolder.tvPosition.setText(data.getStrDuty());
        viewHolder.tvShareCount.setText(data.getIntShareCount() + "");
        if (data.getPicList() != null && data.getPicList().size() > 1) { // 多张图
            ArrayList<String> imageList = new ArrayList<>();
            ArrayList<String> temimgelist = new ArrayList<>();
            for (int i = 0; i < data.getPicList().size(); i++) {
                temimgelist.add(Utils.formatFileUrl(context, data.getPicList().get(i).getStrThumbPath()));
                imageList.add(Utils.formatFileUrl(context, data.getPicList().get(i).getStrImagePath()));
            }
            viewHolder.mvPic.setVisibility(View.VISIBLE);
            viewHolder.iv_simple_image.setVisibility(View.GONE);
            viewHolder.mvPic.setList(viewHolder.transformUri(temimgelist));
            viewHolder.mvPic.setItemClickCallback(new MultiPictureView.ItemClickCallback() {
                @Override
                public void onItemClicked(@NotNull View view, int i, @NotNull ArrayList<Uri> arrayList) {
                    Intent intent = new Intent();
                    intent.setClass(context, ShowBigImgActivity.class);
                    intent.putExtra("paths", imageList);
                    intent.putExtra("position", i);
                    context.startActivity(intent);
                }
            });
        } else if (data.getPicList() != null && data.getPicList().size() == 1) { // 一张图
            viewHolder.iv_simple_image.setVisibility(View.VISIBLE);
            viewHolder.iv_simple_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getPicList().get(0).getStrFileType().equals("video")) {
                        Intent intent = new Intent(context, ShowVideoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("url", data.getPicList().get(0).getStrImagePath());
                        bundle.putString("imgPath", data.getPicList().get(0).getStrVideoImagePath());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    } else {
                        ArrayList<String> imageList = new ArrayList<>();
                        imageList.add(Utils.formatFileUrl(context, data.getPicList().get(0).getStrImagePath()));
                        Intent intent = new Intent();
                        intent.setClass(context, ShowBigImgActivity.class);
                        intent.putExtra("paths", imageList);
                        intent.putExtra("position", 0);
                        context.startActivity(intent);
                    }
                }
            });
            viewHolder.mvPic.setVisibility(View.GONE);
            if ("video".equals(data.getPicList().get(0).getStrFileType())) {
                viewHolder.tv_play.setVisibility(View.VISIBLE);
                GIImageUtil.loadImgNoTrans(context, viewHolder.iv_simple_image, Utils.formatFileUrl(context, data.getPicList().get(0).getStrVideoImagePath()));
            } else {
                viewHolder.tv_play.setVisibility(View.GONE);
                GIImageUtil.loadImgNoTrans(context, viewHolder.iv_simple_image, Utils.formatFileUrl(context, data.getPicList().get(0).getStrThumbPath()));
            }
        } else {
            viewHolder.iv_simple_image.setVisibility(View.GONE);
            viewHolder.mvPic.setVisibility(View.GONE);
        }
        if (isShowNotic) {
            if (data.isNotice()) {
                viewHolder.tvFollow.setText(R.string.string_followed);
                viewHolder.ivNotice.setImageDrawable(context.getResources().getDrawable(R.mipmap.notice_icon));
                viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.font_content));

            } else {
                viewHolder.tvFollow.setText(R.string.string_follow);
                viewHolder.ivNotice.setImageDrawable(context.getResources().getDrawable(R.mipmap.not_notice_icon));
                viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.zxta_state_red));
            }
        } else {
            viewHolder.tvFollow.setVisibility(View.GONE);
            viewHolder.ivNotice.setVisibility(View.GONE);
        }
        if ("1".equals(data.getStrHasDel())) {
            viewHolder.ll_delete.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ll_delete.setVisibility(View.GONE);
        }
        if ("1".equals(data.getLoginUserPraiseState())) {
            viewHolder.tvZanIcon.setText(context.getResources().getString(R.string.font_zaned));
            viewHolder.tvZanIcon.setTextColor(context.getResources().getColor(R.color.line_has_zan));
            viewHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.line_has_zan));
        } else {
            viewHolder.tvZanIcon.setText(context.getResources().getString(R.string.font_zan));
            viewHolder.tvZanIcon.setTextColor(context.getResources().getColor(R.color.font_source));
            viewHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.font_source));
        }
        viewHolder.ll_delete.setOnClickListener(new View.OnClickListener() {
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
                dialog.title(context.getString(R.string.string_tips)).content(context.getString(R.string.string_is_del_this_dynamic)).btnText(context.getString(R.string.string_cancle), context.getString(R.string.string_determine)).showAnim(mBasIn).dismissAnim(mBasOut).show();
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        }, new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                remove(data);
                                viewHolder.setOnClick(data, v, 0);
                                dialog.dismiss();
                            }
                        }
                );

            }
        });
        viewHolder.tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.setOnClick(data, v, 0);
                boolean isNotice = data.isNotice();
                if (isNotice) {
                    data.setNotice(false);
                    viewHolder.tvFollow.setText(R.string.string_follow);
                    GIToastUtil.showMessage(context, context.getString(R.string.have_alerday_cancle_followed) + data.getStrPubUserName());
                    viewHolder.ivNotice.setImageDrawable(context.getResources().getDrawable(R.mipmap.not_notice_icon));
                    viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.zxta_state_red));
                } else {
                    data.setNotice(true);
                    viewHolder.tvFollow.setText(R.string.string_followed);
                    GIToastUtil.showMessage(context, context.getString(R.string.string_have_alerday_followed) + data.getStrPubUserName());
                    viewHolder.ivNotice.setImageDrawable(context.getResources().getDrawable(R.mipmap.notice_icon));
                    viewHolder.tvFollow.setTextColor(context.getResources().getColor(R.color.font_content));
                }

            }
        });
        viewHolder.llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.setOnClick(data, v, 0);
            }
        });
        viewHolder.llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.setOnClick(data, v, 0);
            }
        });
        viewHolder.llZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getLoginUserPraiseState().equals("1")) {
                    return;
                }
                viewHolder.setOnClick(data, v, 0);
                viewHolder.tvZanIcon.startAnimation(AnimationUtils.loadAnimation(
                        context, R.anim.anim_dianzan));
                viewHolder.tvZanIcon.setText(context.getResources().getString(R.string.font_zaned));
                viewHolder.tvZanIcon.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                viewHolder.tvZanCount.setTextColor(context.getResources().getColor(R.color.line_has_zan));
                data.setIntPraiseCount(data.getIntPraiseCount() + 1);
                data.setLoginUserPraiseState("1");
                viewHolder.tvZanCount.setText(data.getIntPraiseCount() + "");
            }
        });
        viewHolder.ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.setOnClick(data, v, 0);
            }
        });
    }


    public class ViewHolder extends BaseViewHolder {
        private RoundImageView ivImg;
        private TextView tvName;
        private TextView tvPosition;
        private ShowAllTextView tvContent;
        private TextView tvCommentCount;
        private TextView tvZanCount;
        private TextView tvShareCount;
        private TextView tvDate;
        private TextView tvFollow;
        private LinearLayout llComment;
        private LinearLayout llZan;
        private LinearLayout llShare;
        private LinearLayout follow_LinearLayout;//关注LinearLayout
        private TextView tvZanIcon;
        private MultiPictureView mvPic;
        private ImageView ivNotice;
        private LinearLayout ll_delete;
        private ImageView iv_simple_image;
        private GITextView tv_play;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImg = (RoundImageView) itemView.findViewById(R.id.iv_img);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvPosition = (TextView) itemView.findViewById(R.id.tv_position);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tv_comment_count);
            tvZanCount = (TextView) itemView.findViewById(R.id.tv_zan_count);
            tvShareCount = (TextView) itemView.findViewById(R.id.tv_share_count);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvFollow = (TextView) itemView.findViewById(R.id.tv_follow);
            llComment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
            follow_LinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_follow);
            llZan = (LinearLayout) itemView.findViewById(R.id.ll_zan);
            llShare = (LinearLayout) itemView.findViewById(R.id.ll_share);
            tvZanIcon = (TextView) itemView.findViewById(R.id.tv_zan_icon);
            mvPic = (MultiPictureView) itemView.findViewById(R.id.mv_pic);
            ivNotice = (ImageView) itemView.findViewById(R.id.iv_notice);
            ll_delete = (LinearLayout) itemView.findViewById(R.id.ll_delete);
            iv_simple_image = (ImageView) itemView.findViewById(R.id.iv_simple_image);
            tv_play = (GITextView) itemView.findViewById(R.id.tv_play);

//            ll_video = (LinearLayout) itemView.findViewById(R.id.ll_video);
//            video_player = (MyVideoPlayer) itemView.findViewById(R.id.video_player);
        }

        private void setOnClick(Object data, View v, int Type) {
            if (fragment != null) {
                ((Circle_MainFragment) fragment).setOnClick(data, v, 0);
            } else {
//                ((Circle_PersonalPageActivity) context).setOnClick(data, v, 0);
                ((CirclePersonPageActivity) context).setOnClick(data, v, 0);
            }

        }

        private List<Uri> transformUri(List<String> list) {
            List<Uri> imgList = new LinkedList<>();
            for (String url : list) {
                try {
                    Uri uri = Uri.parse(Utils.formatFileUrl(context, url));
                    imgList.add(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return imgList;
        }
    }
}
