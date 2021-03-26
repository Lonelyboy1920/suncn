package com.suncn.ihold_zxztc.activity.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDateUtils;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.google.android.material.appbar.AppBarLayout;
import com.goyourfly.multi_picture.MultiPictureView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.BaseInterface;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.ShowBigImgActivity;
import com.suncn.ihold_zxztc.activity.global.ShowVideoActivity;
import com.suncn.ihold_zxztc.adapter.Circle_Comment_ExLVAdapter;
import com.suncn.ihold_zxztc.bean.CommentDetailBean;
import com.suncn.ihold_zxztc.bean.ReplyBean;
import com.suncn.ihold_zxztc.interfaces.AppbarLayoutStateChange;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ShareUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.dialog.MyDialog;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 圈子详情
 */
public class Circle_DetailActivity extends BaseActivity implements BaseInterface {
    @BindView(id = R.id.tv_duty)
    private TextView duty_TextView;
    @BindView(id = R.id.detail_page_lv_comment)
    private ExpandableListView expandableListView;
    @BindView(id = R.id.detail_page_do_comment, click = true)
    private TextView etComment;
    @BindView(id = R.id.tv_commit, click = true)
    private TextView tvCommit;
    @BindView(id = R.id.mv_pic)
    private MultiPictureView multiPictureView;
    @BindView(id = R.id.detail_page_userLogo, click = true)
    private ImageView ivPersonLogo;
    @BindView(id = R.id.detail_page_userName)
    private TextView tvUserName;
    @BindView(id = R.id.detail_page_title)
    private TextView tvContent;
    @BindView(id = R.id.tv_title)
    private TextView tvTitle;
    @BindView(id = R.id.tv_comment_count)
    private TextView tvCommentCount;
    @BindView(id = R.id.tv_zan_count)
    private TextView tvZanCount;
    @BindView(id = R.id.ll_share, click = true)
    private LinearLayout share_LinearLayout;//分享LinearLayout
    @BindView(id = R.id.tv_share_count, click = true)
    private TextView tvShareCount;
    @BindView(id = R.id.tv_comment_count_two)
    private TextView tvCommentCountTwo;
    @BindView(id = R.id.detail_page_above_container)
    private LinearLayout llTop;
    @BindView(id = R.id.detail_page_focus, click = true)
    private TextView btnNotice;
    @BindView(id = R.id.tv_zan_icon)
    private TextView tvZanIcon;
    @BindView(id = R.id.iv_notice)
    private ImageView ivNotice;
    @BindView(id = R.id.ll_sort, click = true)
    private LinearLayout llSort;
    @BindView(id = R.id.tv_sort_icon)
    private TextView tvSortIcon;
    @BindView(id = R.id.ll_empty)
    private LinearLayout llEmpty;
    @BindView(id = R.id.refreshlayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.ll_zan, click = true)
    private LinearLayout llZan;
    @BindView(id = R.id.layout_appbar)
    private AppBarLayout appBarLayout;
    @BindView(id = R.id.iv_simple_image, click = true)
    private ImageView ivSimpleImage;
    @BindView(id = R.id.tv_play)
    private GITextView tv_play;
    private Circle_Comment_ExLVAdapter adapter;
    private String strId = "";
    private String strParentId = "-1";
    private ArrayList<ReplyBean> commentList;
    private String strSort = "0"; //按时间排序字段
    private CommentDetailBean commentDetailBean;
    private int curPage = 1;
    private int curReplyPosition = -1; //当前回复的位置
    private boolean isZanDy = false; //是否是点赞动态
    private Bundle bundle;
    private String comment = "";//回复评论的内容
    private MyDialog myDialog;
    private String loginUserPraiseState;
    private ArrayList<String> imageList;
    private ArrayList<String> imageListPub;
    private String strSid;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_circle_detail);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DefineUtil.isNeedRefresh) {
            getCommentList(true);
        }
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_dynamic_details));
        bundle = getIntent().getExtras();
        strSid = GISharedPreUtil.getString(activity, "strSid");
        if (bundle != null) {
            strId = bundle.getString("strId");
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getCommentList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage++;
                getCommentList(false);
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppbarLayoutStateChange.AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    setHeadTitle(getString(R.string.string_dynamic_details));
                    if (!goto_Button.getText().toString().equals("\ue6a2"))
                        goto_Button.setVisibility(View.GONE);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    if (commentDetailBean != null) {
                        setHeadTitle(commentDetailBean.getDynamic().getStrPubUserName());
                        if (!commentDetailBean.getDynamic().isNotice()
                                && "1".equals(commentDetailBean.getDynamic().getStrShowInterestButton())) {
                            goto_Button.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    //中间状态
                }
            }
        });

        getCommentList(true);
    }

    /**
     * 初始化评论和回复列表
     */
    private void initExpandableListView(final ArrayList<ReplyBean> commentList) {
        if (commentList != null) {
            adapter = new Circle_Comment_ExLVAdapter(activity, commentList);
            expandableListView.setAdapter(adapter);
            for (int i = 0; i < commentList.size(); i++) {//默认展开所有回复
                expandableListView.expandGroup(i);
            }
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                    return true;
                }
            });
        }
    }

    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        switch (what) {
            case 0:
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                commentDetailBean = (CommentDetailBean) object;
                commentList = commentDetailBean.getObjList();
                loginUserPraiseState = commentDetailBean.getLoginUserPraiseState();
                imageList = new ArrayList<>();
                imageListPub = new ArrayList<>();
                if (commentDetailBean.getDynamic().getPicList() != null) {
                    for (int i = 0; i < commentDetailBean.getDynamic().getPicList().size(); i++) {
                        if (commentDetailBean.getDynamic().getPicList().get(i).getStrFileType().equals("video")) {
                            tv_play.setVisibility(View.VISIBLE);
                            imageList.add(Utils.formatFileUrl(activity, commentDetailBean.getDynamic().getPicList().get(i).getStrVideoImagePath()));
                        } else {
                            imageList.add(Utils.formatFileUrl(activity, commentDetailBean.getDynamic().getPicList().get(i).getStrImagePath()));
                        }
                        imageListPub.add(Utils.formatFileUrl(activity, commentDetailBean.getDynamic().getPicList().get(i).getStrThumbPath()));
                    }
                }
                setTopMassage();
                if (commentDetailBean.getIntAllCount() <= curPage * DefineUtil.GLOBAL_PAGESIZE) {
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }
                if (curPage == 1) {
                    initExpandableListView(commentDetailBean.getObjList());
                } else {
                    //adapter = new Circle_Comment_ExLVAdapter(activity, commentList);
                    adapter.addCommentBean(commentList);
                    adapter.notifyDataSetChanged();
                }
                if ("1".equals(commentDetailBean.getDynamic().getStrShowInterestButton())) {
                    findViewById(R.id.ll_notice).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.ll_notice).setVisibility(View.INVISIBLE);
                }
                if (commentDetailBean.getObjList() == null || commentDetailBean.getObjList().size() == 0) {
                    llEmpty.setVisibility(View.VISIBLE);
                } else {
                    llEmpty.setVisibility(View.GONE);
                }
                break;
            case 1:
                ReplyBean newReplyMassage = (ReplyBean) object;
                ReplyBean commentDetail = new ReplyBean();
                commentDetail.setStrReplyId(newReplyMassage.getStrReplyId());
                commentDetail.setStrReplyUserName(GISharedPreUtil.getString(activity, "strName"));
                commentDetail.setStrReplyContent(comment);
                commentDetail.setStrReplyUserPhoto(newReplyMassage.getStrReplyUserPhoto());
//                commentDetail.setStrReplyDate(GIDateUtils.getTodayTime());
                commentDetail.setStrReplyDate(getString(R.string.string_just));
                commentDetail.setIntPraiseCount(0);
                commentDetail.setLoginUserPraiseState("0");
                commentDetail.setStrHasDel("1");
                commentDetail.setStrReplyParentId(newReplyMassage.getStrReplyParentId());
                commentDetail.setStrRepliedUserId(newReplyMassage.getStrRepliedUserId());
                commentDetail.setStrRepliedUserName(newReplyMassage.getStrRepliedUserName());
                commentDetail.setStrReplyUserName(newReplyMassage.getStrReplyUserName());
                commentDetail.setStrReplyUserId(newReplyMassage.getStrReplyUserId());
                //往评论和回复里面手动加数据，避免每次刷新以及解决定位问题
                if (strParentId.equals("-1")) {
                    if (adapter == null) {
                        adapter = new Circle_Comment_ExLVAdapter(activity, new ArrayList<>());
                        getCommentList(false);
                    } else {
                        adapter.addTheCommentData(commentDetail);
                        if (adapter.getGroupCount() > 1) {
                            expandableListView.expandGroup(adapter.getGroupCount() - 1);
                        }
                    }
                } else {
                    adapter.addTheReplyData(commentDetail, curReplyPosition);
                }
                commentDetailBean.getDynamic().setIntDiscussCount(commentDetailBean.getDynamic().getIntDiscussCount() + 1);
                tvCommentCount.setText(commentDetailBean.getDynamic().getIntDiscussCount() + "");
                tvCommentCountTwo.setText(getString(R.string.string_comment) + "(" + commentDetailBean.getDynamic().getIntDiscussCount() + ")");
                //评论成功之后再初始化一下设置
                SpannableString s = new SpannableString(getString(R.string.string_friendly_commen_is_the_starting_point_of_communication));//这里输入自己想要的提示文字
                etComment.setHint(s);
                etComment.setText("");
                strParentId = "-1";
                curReplyPosition = -1;
                DefineUtil.isNeedRefresh = true;
                if (myDialog.isVisible()) {
                    myDialog.dismiss();
                }
                if (adapter.getGroupCount() == 0) {
                    llEmpty.setVisibility(View.VISIBLE);
                } else {
                    llEmpty.setVisibility(View.GONE);
                }
                break;
            case 2:
                if (btnNotice.getText().equals(getString(R.string.string_followed))) {
                    showToast(getString(R.string.have_alerday_cancle_followed) + commentDetailBean.getDynamic().getStrPubUserName());
                    goto_Button.setText("\ue657" + getString(R.string.string_follow));
                    btnNotice.setTextColor(getResources().getColor(R.color.zxta_state_red));
                    btnNotice.setText(getString(R.string.string_follow));
                    ivNotice.setImageDrawable(getResources().getDrawable(R.mipmap.not_notice_icon));
                } else {
                    showToast(getString(R.string.string_have_alerday_followed) + commentDetailBean.getDynamic().getStrPubUserName());
                    goto_Button.setText(getString(R.string.string_followed));
                    btnNotice.setTextColor(getResources().getColor(R.color.font_content));
                    btnNotice.setText(getString(R.string.string_followed));
                    ivNotice.setImageDrawable(getResources().getDrawable(R.mipmap.notice_icon));
                }
                DefineUtil.isNeedRefresh = true;
            case 3:
                if (isZanDy) {
                    tvZanCount.setTextColor(getResources().getColor(R.color.line_has_zan));
                    tvZanIcon.setText(activity.getResources().getString(R.string.font_zaned));
                    tvZanIcon.setTextColor(getResources().getColor(R.color.line_has_zan));
                    tvZanCount.setText(commentDetailBean.getDynamic().getIntPraiseCount() + 1 + "");
                    isZanDy = false;
                    DefineUtil.isNeedRefresh = true;
                }
                break;
            case 4:
                showToast(getString(R.string.string_del_success));
                commentDetailBean.getDynamic().setIntDiscussCount(commentDetailBean.getDynamic().getIntDiscussCount() - 1);
                tvCommentCount.setText(commentDetailBean.getDynamic().getIntDiscussCount() + "");
                tvCommentCountTwo.setText(getString(R.string.string_comment) + "(" + commentDetailBean.getDynamic().getIntDiscussCount() + ")");
                if (adapter.getGroupCount() == 0) {
                    llEmpty.setVisibility(View.VISIBLE);
                } else {
                    llEmpty.setVisibility(View.GONE);
                }
                break;
            case 7:
                finish();
                DefineUtil.isNeedRefresh = true;
                break;
        }
    }

    private void setTopMassage() {
        if (GISharedPreUtil.getInt(activity, "intUserRole") == 1
                && AppConfigUtil.isCanEditCircle(activity)
                || "0".equals(commentDetailBean.getDynamic().getStrShowInterestButton())) {
            goto_Button.setText("\ue6a2");
            goto_Button.setVisibility(View.VISIBLE);
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            goto_Button.setText("\ue657" + getString(R.string.string_follow));
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        GIImageUtil.loadImg(this, ivPersonLogo, Utils.formatFileUrl(this, commentDetailBean.getDynamic().getStrPhotoPath()), 1);
        //设置定位到评论
        if (bundle.getBoolean("isFromComment", false)) {
            appBarLayout.setExpanded(false);
        }
        tvUserName.setText(commentDetailBean.getDynamic().getStrPubUserName());
        String content = commentDetailBean.getDynamic().getStrContent();
        if (GIStringUtil.isNotBlank(content)) {
            tvContent.setText(content);
        } else {
            tvContent.setVisibility(View.GONE);
        }
        duty_TextView.setText(commentDetailBean.getDynamic().getStrDuty()+" "+commentDetailBean.getDynamic().getStrPubDate());
        tvCommentCount.setText(commentDetailBean.getDynamic().getIntDiscussCount() + "");
        tvCommentCountTwo.setText(getString(R.string.string_comment) + "(" + commentDetailBean.getDynamic().getIntDiscussCount() + ")");
        tvZanCount.setText(commentDetailBean.getDynamic().getIntPraiseCount() + "");
        tvShareCount.setText(commentDetailBean.getDynamic().getIntShareCount() + "");
        if (imageList.size() == 1) {
            ivSimpleImage.setVisibility(View.VISIBLE);
            GIImageUtil.loadImgNoTrans(activity, ivSimpleImage, imageList.get(0));
            multiPictureView.setVisibility(View.GONE);
            ivSimpleImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (commentDetailBean.getDynamic().getPicList().get(0).getStrFileType().equals("video")) {
                        Intent intent = new Intent(activity, ShowVideoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("url", commentDetailBean.getDynamic().getPicList().get(0).getStrImagePath());
                        bundle.putString("imgPath", commentDetailBean.getDynamic().getPicList().get(0).getStrVideoImagePath());
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(activity, ShowBigImgActivity.class);
                        intent.putExtra("paths", imageList);
                        intent.putExtra("position", 1);
                        startActivity(intent);
                    }

                }
            });
        } else {
            ivSimpleImage.setVisibility(View.GONE);
            multiPictureView.setVisibility(View.VISIBLE);
            multiPictureView.setList(transformUri(imageListPub));
        }
        if (commentDetailBean.getDynamic().getLoginUserPraiseState().equals("1")) {
            tvZanCount.setTextColor(getResources().getColor(R.color.line_has_zan));
            tvZanIcon.setText(activity.getResources().getString(R.string.font_zaned));
            tvZanIcon.setTextColor(getResources().getColor(R.color.line_has_zan));
        }
        if (commentDetailBean.getDynamic().isNotice()) {
            btnNotice.setTextColor(getResources().getColor(R.color.font_content));
            btnNotice.setText(getString(R.string.string_followed));
            ivNotice.setImageDrawable(getResources().getDrawable(R.mipmap.notice_icon));
        } else {
            btnNotice.setTextColor(getResources().getColor(R.color.zxta_state_red));
            btnNotice.setText(getString(R.string.string_follow));
            ivNotice.setImageDrawable(getResources().getDrawable(R.mipmap.not_notice_icon));
        }
        if (imageList.size() == 0 || imageList == null) {
            multiPictureView.setVisibility(View.GONE);
            ivSimpleImage.setVisibility(View.GONE);
        }

        multiPictureView.setItemClickCallback(new MultiPictureView.ItemClickCallback() {
            @Override
            public void onItemClicked(@NotNull View view, int i, @NotNull ArrayList<Uri> arrayList) {
                Intent intent = new Intent();
                intent.setClass(activity, ShowBigImgActivity.class);
                intent.putExtra("paths", imageList);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Uri> transformUri(List<String> list) {
        List<Uri> imgList = new LinkedList<>();
        for (String url : list) {
            try {
                Uri uri = Uri.parse(Utils.formatFileUrl(this, url));
                imgList.add(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imgList;
    }

    /**
     * 关注/取消关注
     */
    private void followUser() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strPubUserId", commentDetailBean.getDynamic().getStrPubUserId());
        textParamMap.put("strPubUserName", commentDetailBean.getDynamic().getStrPubUserName());
        doRequestNormal(ApiManager.getInstance().addDynamicNotice(textParamMap), 2);
    }

    /**
     * 获取评论列表
     */
    private void getCommentList(boolean b) {
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        textParamMap.put("strSort", strSort);
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        doRequestNormal(ApiManager.getInstance().getCommentList(textParamMap), 0);
    }

    /**
     * 添加评论
     */
    private void commitReply(String strDynamicId, String strReplyContent, String strRepliedUserId, String strRepliedUserName) {
        textParamMap = new HashMap<>();
        textParamMap.put("strParentId", strParentId);
        textParamMap.put("strDynamicId", strDynamicId);
        textParamMap.put("strRepliedUserId", strRepliedUserId);
        textParamMap.put("strRepliedUserName", strRepliedUserName);
        try {
            textParamMap.put("strReplyContent", URLEncoder.encode(strReplyContent, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        doRequestNormal(ApiManager.getInstance().addReply(textParamMap), 1);
    }

    /**
     * 点赞
     */
    private void commitZan(String strDynamicId, String strDynamicReplyId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strDynamicId", strDynamicId);
        textParamMap.put("strType", "2");
        textParamMap.put("strDynamicReplyId", strDynamicReplyId);
        doRequestNormal(ApiManager.getInstance().addZan(textParamMap), 3);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_share:
                ShareUtil.showDialog(activity, " ", commentDetailBean.getDynamic().getObjShare().getStrTitle(), Utils.formatFileUrl(activity, commentDetailBean.getDynamic().getObjShare().getStrIMGUrl()), Utils.formatFileUrl(activity, commentDetailBean.getDynamic().getObjShare().getStrPageUrl()), getResources().getStringArray(R.array.share_array));
                break;
            case R.id.detail_page_do_comment:
                //showPopupCommnet();
                myDialog = new MyDialog();
                strParentId = "-1";
                comment = "";
                myDialog.setMyText(GISharedPreUtil.getString(activity, strSid + strId));
                myDialog.setOnItemClickListener(new MyDialog.onItemClickListener() {
                    @Override
                    public void onItemClick(View v, EditText inputComment, String s) {
                        if (GIStringUtil.isNotBlank(s)) {
                            inputComment.setEnabled(true);
                            inputComment.setFocusable(true);
                            inputComment.setFocusableInTouchMode(true);
                            inputComment.requestFocus();
                            comment = s;
                            GISharedPreUtil.setValue(activity, strSid + strId, "");
                            commitReply(strId, comment, "", "");
                        } else {
                            showToast(getString(R.string.string_please_enter_comments));
                        }
                    }
                });
                //监听弹窗关闭事件，若未发布但是输入内容，记录缓存，发布过清楚缓存
                myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (GIStringUtil.isBlank(comment)) {
                            GISharedPreUtil.setValue(activity, strSid + strId, myDialog.getInputComment().getText().toString());
                        } else {
                            GISharedPreUtil.setValue(activity, strSid + strId, "");
                        }
                    }
                });
                myDialog.show(getFragmentManager(), "dialog");
                break;
            case R.id.btn_goto:
                if (goto_Button.getText().toString().equals("\ue6a2")) {
                    String[] strings = new String[]{};
                    if (commentDetailBean.getDynamic().getPicList() != null
                            && commentDetailBean.getDynamic().getPicList().size() == 1
                            && commentDetailBean.getDynamic().getPicList().get(0).getStrFileType().equals("video")) {
                        strings = new String[]{getString(R.string.string_delete)};
                    } else {
                        strings = new String[]{getString(R.string.string_delete), getString(R.string.string_edit)};
                    }
                    new AlertView(null, null, getString(R.string.string_cancle), null,
                            //new String[]{"相册", "拍照", "扫描"},
                            strings,
                            activity, AlertView.Style.ActionSheet, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            switch (position) {
                                case 0:  //删除
                                    showLoginDialog(strId);
                                    break;
                                case 1: //编辑
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("dynamicBean", commentDetailBean.getDynamic());
                                    showActivity(activity, Circle_AddDynamicActivity.class, bundle);
                                    break;
                            }


                        }
                    }).show();
                } else {
                    followUser();
                }
                break;
            case R.id.detail_page_focus:
                followUser();
                break;
            case R.id.ll_sort:
                if (strSort.equals("0")) {
                    tvSortIcon.setText(getResources().getString(R.string.font_sort_up));
                    strSort = "1";
                } else {
                    tvSortIcon.setText(getResources().getString(R.string.font_sort_down));
                    strSort = "0";
                }
                getCommentList(true);
                break;
            case R.id.ll_zan:
                if ("0".equals(loginUserPraiseState)) {
                    isZanDy = true;
                    tvZanIcon.startAnimation(AnimationUtils.loadAnimation(
                            activity, R.anim.anim_dianzan));
                    commitZan(strId, "");
                }
                break;
            case R.id.detail_page_userLogo:
                if (bundle.getBoolean("isFromPersonalPage", false)) {
                    finish();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("strUserId", commentDetailBean.getDynamic().getStrPubUserId());
                    if ("0".equals(commentDetailBean.getDynamic().getStrShowInterestButton())) {
                        bundle.putBoolean("isShowChat", false);
                    }
//                    showActivity(activity, Circle_PersonalPageActivity.class, bundle);
                    showActivity(activity, CirclePersonPageActivity.class, bundle);
                }
                break;
        }
    }

    /**
     * 删除对话框
     */
    private void showLoginDialog(String id) {
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.string_tips)).content(getString(R.string.string_is_del_this_dynamic)).btnText(getString(R.string.string_cancle), getString(R.string.string_determine)).showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        //dialog.dismiss();
                        dialog.superDismiss();
                        deleteDynamic(id);

                    }
                }
        );
    }

    /**
     * 删除动态
     */
    private void deleteDynamic(String strDynamicId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strDynamicId);
        doRequestNormal(ApiManager.getInstance().deleteDynamic(textParamMap), 7);
    }

    @Override
    public void setOnClick(Object object, View view, int groupposition) {
        switch (view.getId()) {
            case R.id.ll_zan:
                commitZan("", ((ReplyBean) object).getStrReplyId());
                break;
            case R.id.ll_reply:
                SpannableString s = new SpannableString(getString(R.string.string_reply) + "@" + commentList.get(groupposition).getStrReplyUserName());//这里输入自己想要的提示文字
                curReplyPosition = groupposition;
                strParentId = commentList.get(groupposition).getStrReplyId();
                myDialog = new MyDialog();
                myDialog.setMyHint(s.toString());
                myDialog.setMyText(GISharedPreUtil.getString(activity, strSid + strId + strParentId));
                myDialog.setOnItemClickListener(new MyDialog.onItemClickListener() {
                    @Override
                    public void onItemClick(View v, EditText inputComment, String s) {
                        if (GIStringUtil.isNotBlank(s)) {
                            inputComment.setEnabled(true);
                            inputComment.setFocusable(true);
                            inputComment.setFocusableInTouchMode(true);
                            inputComment.requestFocus();
                            comment = s;
                            GISharedPreUtil.setValue(activity, strSid + strId + strParentId, "");
                            commitReply(strId, comment, "", "");
                        } else {
                            showToast(getString(R.string.string_please_enter_comments));
                        }
                    }
                });
                //监听弹窗关闭事件，若未发布但是输入内容，记录缓存，发布过清楚缓存
                myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (GIStringUtil.isBlank(comment)) {
                            GISharedPreUtil.setValue(activity, strSid + strId + strParentId, myDialog.getInputComment().getText().toString());
                        } else {
                            GISharedPreUtil.setValue(activity, strSid + strId + strParentId, "");
                        }
                    }
                });
                myDialog.show(getFragmentManager(), "dialog");
                break;
            case R.id.ll_reply_child:
                ReplyBean replyBean = ((ReplyBean) object);
                SpannableString s1 = new SpannableString(getString(R.string.string_reply) + "@" + replyBean.getStrReplyUserName());//这里输入自己想要的提示文字
                curReplyPosition = groupposition;
                strParentId = replyBean.getStrReplyParentId();
                myDialog = new MyDialog();
                myDialog.setMyHint(s1.toString());
                myDialog.setMyText(GISharedPreUtil.getString(activity, strSid + strId + strParentId));
                myDialog.setOnItemClickListener(new MyDialog.onItemClickListener() {
                    @Override
                    public void onItemClick(View v, EditText inputComment, String s) {
                        if (GIStringUtil.isNotBlank(s)) {
                            inputComment.setEnabled(true);
                            inputComment.setFocusable(true);
                            inputComment.setFocusableInTouchMode(true);
                            inputComment.requestFocus();
                            comment = s;
                            GISharedPreUtil.setValue(activity, strSid + strId + strParentId, "");
                            commitReply(strId, comment, replyBean.getStrReplyUserId(), replyBean.getStrReplyUserName());
                        } else {
                            showToast(getString(R.string.string_please_enter_comments));
                        }
                    }
                });
                //监听弹窗关闭事件，若未发布但是输入内容，记录缓存，发布过清楚缓存
                myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (GIStringUtil.isBlank(comment)) {
                            GISharedPreUtil.setValue(activity, strSid + strId + strParentId, myDialog.getInputComment().getText().toString());
                        } else {
                            GISharedPreUtil.setValue(activity, strSid + strId + strParentId, "");
                        }
                    }
                });
                myDialog.show(getFragmentManager(), "dialog");
                break;
            case R.id.comment_item_delete:
                deletReply(((ReplyBean) object).getStrReplyId());
                break;
        }
    }

    /**
     * 删除
     */
    private void deletReply(String strDynamicId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strDynamicId);
        doRequestNormal(ApiManager.getInstance().DeleteReplyServlet(textParamMap), 4);
    }

}
