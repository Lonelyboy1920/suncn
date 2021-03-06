package com.suncn.ihold_zxztc.activity.hot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.service.GIDownloadService;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.ShowBigImgActivity;
import com.suncn.ihold_zxztc.adapter.NewsComment_Child_RVAdapter;
import com.suncn.ihold_zxztc.adapter.NewsComment_RVAdapter;
import com.suncn.ihold_zxztc.bean.NewsListBean;
import com.suncn.ihold_zxztc.bean.ReplyBean;
import com.suncn.ihold_zxztc.bean.ReplyListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.LoginInterceptor;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.ShareUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.dialog.MyDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * ????????????????????????????????????????????????????????????????????????????????????
 * ??????????????????
 */
public class NewsDetailActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.ll_write_comment)
    private LinearLayout writeComment_LinearLayout;//?????????LinearLayout
    @BindView(id = R.id.et_comment, click = true)
    private TextView comment_EditText;//?????????EditText
    @BindView(id = R.id.rl_top, click = true)
    private RelativeLayout top_Layout;//??????????????????
    @BindView(id = R.id.tv_shou, click = true)
    private GITextView shou_TextView;//??????
    @BindView(id = R.id.tv_zhuan, click = true)
    private GITextView zhuan_TextView;//??????
    @BindView(id = R.id.tv_play, click = true)
    private GITextView play_TextView;
    @BindView(id = R.id.seekBar)
    private SeekBar seekBar;
    @BindView(id = R.id.tv_close, click = true)
    private GITextView close_TextView;
    @BindView(id = R.id.ll_video)
    private RelativeLayout llVideo;
    @BindView(id = R.id.tv_count)
    private TextView tvCount;//????????????
    @BindView(id = R.id.scrollview)
    private NestedScrollView scrollView;

    private LinearLayout llSave;//??????
    private GITextView tvSave;
    private WebView webView; // WebView
    private LinearLayout zan_LinearLayout;//????????????LinearLayout
    private GITextView articleZan_TextView;//??????????????????
    private TextView count_TextView;//???????????????
    private LinearLayout zan_LinearLayout_gz;//????????????LinearLayout
    private GITextView articleZan_TextView_gz;//??????????????????
    private TextView count_TextView_gz;//???????????????
    private LinearLayout empty_Layout;// ??????????????????????????????
    private View headerView;
    private TextView startTime_TextView;

    private MyDialog myDialog;
    private NewsComment_RVAdapter adapter;
    private SpeechSynthesizer mTts; // ??????????????????
    private LinearLayoutManager mManager;
    private ReplyListBean newsReplyListBean;
    private NewsListBean.objShare objShare;

    private String myUrl;
    private String detailUrl;//?????????url
    private String strNewsId;//??????id???????????????
    private String strType;//0????????????2????????????3?????????,4 ?????? 5 ??????
    private boolean isMainDeal;//?????????????????????
    private int curPage = 1;
    private String strReplyContent = "";
    private String strListenContent; // ??????????????????
    private String loginUserPraiseState;//???????????????????????????????????? 0 ??? 1??? ;
    private int intDetailZanCount;//?????????????????????
    private boolean isFristLoad = true;
    private int clickPos; // ???????????????
    private int clickGroupPos; // ?????????????????????????????????
    private String strDateState; // ????????????????????????????????????????????? -1 ????????? 1 ????????? 0????????? ???????????????????????????
    private boolean isNews; // ?????????????????????
    private int intSelectCount = 0;//?????????????????????
    private String strSid;
    private int replyCount = 0;
    public static String strTmpNewsId = "";//?????????????????????id
    private boolean isSetPosion = false;
    private Date startDate;
    private Date endDate;
    private boolean isShowComment;//??????????????????

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_news_detail);
    }

    @Override
    public void onResume() {
        super.onResume();
        startDate = new Date();
    }

    @Override
    public void initData() {
        super.initData();
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener); // ?????????????????????
        setHeadTitle("");
        writeComment_LinearLayout.setVisibility(View.GONE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        Bundle bundle = getIntent().getExtras();
        strSid = GISharedPreUtil.getString(activity, "strSid");
        seekBar.setEnabled(false);
        if (bundle != null) {
            detailUrl = Utils.formatFileUrl(activity, bundle.getString("strUrl"));
            strNewsId = bundle.getString("strNewsId");
            isNews = bundle.getBoolean("isNews");
            mManager = new LinearLayoutManager(activity);
            objShare = (NewsListBean.objShare) bundle.getSerializable("objShare");
            isShowComment = bundle.getBoolean("isShowComment", true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new NewsComment_RVAdapter(new ArrayList<>(), activity);
            adapter.setHeaderView(getHeaderView());
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
            if (isNews) {
                adapter.setCanReply(false);
                zhuan_TextView.setVisibility(View.VISIBLE);
            } else {
                comment_EditText.setHint(R.string.string_i_join_in);
                adapter.setCanReply(true);
                zhuan_TextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_comment: // ?????????
                if (!GISharedPreUtil.getBoolean(this, "isHasLogin", false)) { // ??????????????????????????????????????????????????????????????????????????????????????????
                    Bundle bundle = new Bundle();
                    bundle.putString("headTitle", getString(R.string.string_information_content));//objList.get(currentIndex).getStrName());
                    bundle.putString("strUrl", detailUrl);
                    bundle.putBoolean("isShowReply", true);
                    LoginInterceptor.interceptor(activity, getClass().getSimpleName(), bundle);
                } else {
                    doComment(true, "", null);
                }
                break;
            case R.id.ll_zan: // ???????????????
                if (loginUserPraiseState.equals("0")) {
                    dealInfo("2", true, "");
                    findViewById(R.id.tv_zan).startAnimation(AnimationUtils.loadAnimation(
                            activity, R.anim.anim_dianzan));
                }
                break;
            case R.id.ll_zan_gz: // ???????????????
                if (loginUserPraiseState.equals("0"))
                    dealInfo("2", true, "");
                break;
            case R.id.tv_shou:
            case R.id.ll_save:// ??????
                if (!GISharedPreUtil.getBoolean(this, "isHasLogin", false)) { // ??????????????????????????????????????????????????????????????????????????????????????????
                    Bundle bundle = new Bundle();
                    bundle.putString("headTitle", getString(R.string.string_information_content));//objList.get(currentIndex).getStrName());
                    bundle.putString("strUrl", detailUrl);
                    bundle.putBoolean("isShowReply", true);
                    LoginInterceptor.interceptor(activity, getClass().getSimpleName(), bundle);
                } else {
                    dealInfo("3", true, "");
                }
                break;
            case R.id.tv_zhuan: // ??????
                ShareUtil.showDialog(activity, objShare.getStrTitle(), objShare.getStrContent(), Utils.formatFileUrl(activity, objShare.getStrIMGUrl()), Utils.formatFileUrl(activity, objShare.getStrPageUrl()), getResources().getStringArray(R.array.share_array));
                break;
            case R.id.rl_top: // ?????????????????????
                scrollView.scrollTo(0, webView.getHeight());
                GILogUtil.e("?????????-====", webView.getHeight());
                break;
        }
    }

    /**
     * ?????????/???????????????
     */
    public void OnPartClick(Object obj, View view, int groupPos, int pos) {
        clickGroupPos = groupPos;
        clickPos = pos;
        ReplyBean newsReplyBean = (ReplyBean) obj;
        switch (view.getId()) {
            case R.id.ll_zan: // ??????
                dealInfo("2", false, newsReplyBean.getStrReplyId());
                break;
            case R.id.ll_reply: // ??????
                doComment(false, newsReplyBean.getStrReplyId(), newsReplyBean.getStrReplyUserName());
                break;
            case R.id.comment_item_delete: // ????????????
                BaseAnimatorSet mBasIn = new BounceTopEnter();
                BaseAnimatorSet mBasOut = new BounceTopEnter();
                try {
                    mBasIn = BounceEnter.class.newInstance();
                    mBasOut = ZoomOutExit.class.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final MaterialDialog dialog = new MaterialDialog(activity);
                dialog.title(getString(R.string.string_tips)).content(getString(R.string.string_are_you_sure_you_want_to_delete_this_comment)).btnText(getString(R.string.string_cancle), getString(R.string.string_determine)).showAnim(mBasIn).dismissAnim(mBasOut).show();
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        }, new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dealInfo("5", false, newsReplyBean.getStrReplyId());
                                dialog.dismiss();
                            }
                        }
                );
                break;
        }
    }

    /**
     * ????????????
     */
    private void doComment(boolean isMainDeal, String id, String name) {
        if (isNews || "1".equals(strDateState)) {
            strReplyContent = "";
            myDialog = new MyDialog();
            if (GIStringUtil.isNotBlank(name)) {
                SpannableString s = new SpannableString(getString(R.string.string_reply) + "@" + name);//???????????????????????????????????????
                myDialog.setMyHint(s.toString());
            } else {
                myDialog.setMyHint(comment_EditText.getHint().toString());
            }

            myDialog.setMyText(GISharedPreUtil.getString(activity, strSid + strNewsId + id));
            myDialog.setOnItemClickListener(new MyDialog.onItemClickListener() {
                @Override
                public void onItemClick(View v, EditText inputComment, String s) {
                    if (GIStringUtil.isNotBlank(s)) {
                        inputComment.setEnabled(true);
                        inputComment.setFocusable(true);
                        inputComment.setFocusableInTouchMode(true);
                        inputComment.requestFocus();
                        strReplyContent = s;
                        GISharedPreUtil.setValue(activity, strSid + strNewsId + id, "");
                        dealInfo("0", isMainDeal, id);
                    } else {
                        showToast(getString(R.string.string_please_enter_comments));
                    }
                }
            });
            //????????????????????????????????????????????????????????????????????????????????????????????????
            myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (GIStringUtil.isBlank(strReplyContent)) {
                        GISharedPreUtil.setValue(activity, strSid + strNewsId + id, myDialog.getInputComment().getText().toString());
                    } else {
                        GISharedPreUtil.setValue(activity, strSid + strNewsId + id, "");
                    }
                }
            });
            myDialog.show(getFragmentManager(), "dialog");
        } else if ("0".equals(strDateState)) {
            showToast(getString(R.string.string_this_topic_comment_has_not_started));
        } else if ("-1".equals(strDateState)) {
            showToast(getString(R.string.string_this_topic_comment_has_ended));
        }
    }

    private View getHeaderView() {
        headerView = getLayoutInflater().inflate(R.layout.view_head_theme_detail, recyclerView, false);
        headerView.setVisibility(View.GONE);
        webView = headerView.findViewById(R.id.webview);
        //scrollView = headerView.findViewById(R.id.scrollView);
        webView.loadUrl(detailUrl);

        skeletonScreen = Skeleton.bind(scrollView)
                .load(R.layout.activity_news_detail_skeleton)
//                .shimmer(false)
                .show();
        count_TextView = headerView.findViewById(R.id.tv_count);//????????????
        articleZan_TextView = headerView.findViewById(R.id.tv_zan);//????????????
        zan_LinearLayout = headerView.findViewById(R.id.ll_zan);
        zan_LinearLayout.setOnClickListener(NewsDetailActivity.this);
        count_TextView_gz = headerView.findViewById(R.id.tv_count_gz);//????????????
        articleZan_TextView_gz = headerView.findViewById(R.id.tv_zan_gz);//????????????
        zan_LinearLayout_gz = headerView.findViewById(R.id.ll_zan_gz);
        zan_LinearLayout_gz.setOnClickListener(NewsDetailActivity.this);
        llSave = headerView.findViewById(R.id.ll_save);
        tvSave = headerView.findViewById(R.id.tv_save);
        llSave.setOnClickListener(NewsDetailActivity.this);
        empty_Layout = headerView.findViewById(R.id.ll_empty);
        if (!isShowComment) {
            headerView.findViewById(R.id.ll_btm).setVisibility(View.GONE);
        } else {
            headerView.findViewById(R.id.ll_btm).setVisibility(View.VISIBLE);
        }
        initWebView();
        return headerView;
    }

    /**
     * ????????????
     *
     * @param type       0????????????2????????????3?????????,4 ?????? 5 ??????
     * @param isMainDeal ????????????????????????false??????????????????????????????????????????
     */
    private void dealInfo(String type, boolean isMainDeal, String itemZanId) {
        if (type.equals("0")) {
            prgDialog.showLoadDialog();
        }
        strType = type;
        this.isMainDeal = isMainDeal;
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strNewsId);//??????id
        textParamMap.put("strType", strType);//???????????? 0????????????2????????????3?????????,4 ?????? 5 ??????
        if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
        }
        if (type.equals("0")) {
            try {
                textParamMap.put("strReplyContent", URLEncoder.encode(strReplyContent,"utf-8"));//????????????
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (!isMainDeal) {
            textParamMap.put("strReplyId", itemZanId);//???????????????itemId
        }
        if (isNews)
            doRequestNormal(ApiManager.getInstance().getNewsDeal(textParamMap), 0);
        else
            doRequestNormal(ApiManager.getInstance().dealThemeInfo(textParamMap), 0);
    }

    /**
     * ??????????????????
     */
    private void getListData(boolean isShow) {
        textParamMap = new HashMap<>();
        if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
        }
        textParamMap.put("strId", strNewsId);
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        if (isNews) {
            doRequestNormal(ApiManager.getInstance().getNewsReplyList(textParamMap), 1);
        } else {
            doRequestNormal(ApiManager.getInstance().getReplyList(textParamMap), 1);
        }
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        try {
            switch (sign) {
                case 1:
                    ReplyListBean newsReplyListBean = (ReplyListBean) data;
                    replyCount = newsReplyListBean.getIntAllRecordCount();
                    if (newsReplyListBean.getIntAllRecordCount() > 99) {
                        tvCount.setText("99+");
                        tvCount.setVisibility(View.VISIBLE);
                    } else if (newsReplyListBean.getIntAllRecordCount() > 0) {
                        tvCount.setText(newsReplyListBean.getIntAllRecordCount() + "");
                        tvCount.setVisibility(View.VISIBLE);
                    } else {
                        tvCount.setText("");
                        tvCount.setVisibility(View.GONE);
                    }
                    strDateState = newsReplyListBean.getStrDateState();
                    strListenContent = newsReplyListBean.getStrListenContent();
                    intDetailZanCount = newsReplyListBean.getIntPraiseCount();
                    loginUserPraiseState = newsReplyListBean.getLoginUserPraiseState();
                    initDetailZan();
                    initDetailCollect(newsReplyListBean.getLoginUserCollectState(), false);
                    for (int i = 0; i < newsReplyListBean.getObjList().size(); i++) {
                        if ("1".equals(newsReplyListBean.getObjList().get(i).getStrChoiceState())) {
                            intSelectCount++;
                        } else {
                            break;
                        }
                    }
                    ArrayList<ReplyBean> newsReplyBeans = newsReplyListBean.getObjList();
                    if (curPage == 1) {
                        adapter.setList(newsReplyBeans); // ???????????????????????????????????????view
                        if (newsReplyBeans.size() > 0) {
                            empty_Layout.setVisibility(View.GONE);
                        } else {
                            empty_Layout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        adapter.addData(newsReplyBeans);
                    }
                    if (newsReplyListBean.getIntAllCount() > DefineUtil.GLOBAL_PAGESIZE) {

                    }
                    if (strTmpNewsId.equals(strNewsId) && !isSetPosion) {
                        isSetPosion = true;
                        scrollView.scrollTo(0, GISharedPreUtil.getInt(activity, "position" + strNewsId));
                    }
                    break;
                case 0:
                    prgDialog.closePrgDialog();
                    ReplyBean baseGlobal = (ReplyBean) data;
                    switch (strType) { // 0????????????2????????????3?????????,4 ?????? 5 ??????
                        case "0": // ??????
                            myDialog.dismiss();
                            ReplyBean replyBean = new ReplyBean();
                            replyBean.setStrReplyId(baseGlobal.getStrReplyId());
                            replyBean.setStrReplyUserPhoto(baseGlobal.getStrReplyUserPhoto());
                            replyBean.setStrReplyUserName(GISharedPreUtil.getString(activity, "strName"));
                            replyBean.setStrReplyContent(strReplyContent);
                            replyBean.setStrReplyDate(getString(R.string.string_just));
                            replyBean.setIntPraiseCount(0);
                            replyBean.setLoginUserPraiseState("0");
                            replyBean.setStrHideState("1"); // ???????????????????????????
                            if (isMainDeal) {
                                replyCount++;
                                toastMessage = getString(R.string.string_comment_posted_successfully);
                                empty_Layout.setVisibility(View.GONE);
                                adapter.addData(intSelectCount, replyBean); //  ????????????????????????????????????????????????????????????????????????????????????
                                if (replyCount > 99) {
                                    tvCount.setText("99+");
                                } else {
                                    tvCount.setText(replyCount + "");
                                }
                                adapter.notifyDataSetChanged();// ??????????????????notifyDataSetChanged????????????????????????intGroupPos
                            } else {
                                toastMessage = getString(R.string.string_reply_successful);
                                ReplyBean replyBean1 = (ReplyBean) adapter.getItem(clickPos - 1);
                                NewsComment_Child_RVAdapter adapter1 = (NewsComment_Child_RVAdapter) replyBean1.getAdapter();
                                replyBean1.setShowAllChild(adapter1.isShowAllChild());
                                replyBean1.getChildList().add(0, replyBean);
                                adapter.notifyItemChanged(clickPos);// ??????????????????notifyItemChanged(int)??????????????????
                            }
                            break;
                        case "2": // ??????
                            toastMessage = getString(R.string.string_like_the_success);
                            if (isMainDeal) { // ?????????????????????
                                intDetailZanCount++;
                                loginUserPraiseState = "1";
                                initDetailZan();
                            } else {
                                if (clickGroupPos == -1) {
                                    ReplyBean replyBean1 = (ReplyBean) adapter.getItem(clickPos); // ?????????header???????????????1
                                    replyBean1.setLoginUserPraiseState("1");
                                    replyBean1.setIntPraiseCount(replyBean1.getIntPraiseCount() + 1);
//                                    adapter.update(replyBean1, clickPos);
                                    //???????????????????????????????????????
                                    adapter.setData(clickPos, replyBean1);// ??????????????????notifyItemChanged(int)?????????????????????
                                } else {
                                    ReplyBean replyBean1 = (ReplyBean) adapter.getItem(clickGroupPos - 1); // ?????????header???????????????1
                                    NewsComment_Child_RVAdapter adapter1 = (NewsComment_Child_RVAdapter) replyBean1.getAdapter();
                                    ReplyBean replyBean2 = replyBean1.getChildList().get(clickPos);
                                    replyBean2.setLoginUserPraiseState("1");
                                    replyBean2.setIntPraiseCount(replyBean2.getIntPraiseCount() + 1);
                                    adapter1.setData(clickPos, replyBean2);
//                                    replyBean1.getChildList().set(clickPos, replyBean2);
//                                    adapter.notifyItemChanged(clickGroupPos);// ??????????????????notifyItemChanged(int)?????????????????????
                                }
                            }
                            break;
                        case "3": // ??????
                            initDetailCollect(baseGlobal.getIntCount() + "", true);
                            setResult(RESULT_OK);
                            break;
                        case "5": // ??????
                            if (clickGroupPos == -1) {
                                adapter.remove(clickPos); // ?????????header???????????????1
                                adapter.notifyDataSetChanged();// ??????????????????notifyDataSetChanged????????????????????????intGroupPos
                                replyCount--;
                                if (replyCount > 99) {
                                    tvCount.setText("99+");
                                } else if (replyCount > 0) {
                                    tvCount.setText(replyCount + "");
                                }
                                if (adapter.getItemCount() == 0) {
                                    empty_Layout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                ReplyBean replyBean1 = (ReplyBean) adapter.getItem(clickGroupPos - 1); // ?????????header???????????????1
                                NewsComment_Child_RVAdapter adapter1 = (NewsComment_Child_RVAdapter) replyBean1.getAdapter();
                                replyBean1.setShowAllChild(adapter1.isShowAllChild());
                                replyBean1.getChildList().remove(clickPos);
                                adapter.notifyItemChanged(clickGroupPos);// ??????????????????notifyItemChanged(int)?????????????????????
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getString(R.string.data_error);
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }

    /**
     * ???????????????????????????
     */
    private void initDetailZan() {
        if ("0".equals(loginUserPraiseState)) { // ?????????
            zan_LinearLayout.setBackground(activity.getResources().getDrawable(R.drawable.shape_ll_zan_bg));
            articleZan_TextView.setText("\ue65b");
            count_TextView.setText(intDetailZanCount + "");
            zan_LinearLayout_gz.setBackground(activity.getResources().getDrawable(R.drawable.shape_ll_zan_bg));
            articleZan_TextView_gz.setText("\ue65b");
            count_TextView_gz.setText(intDetailZanCount + "");
        } else { // ?????????
            zan_LinearLayout.setBackground(activity.getResources().getDrawable(R.drawable.shape_ll_zan_bg_hover));
            articleZan_TextView.setText("\ue665");
            count_TextView.setText(intDetailZanCount + "");
            articleZan_TextView.setTextColor(getResources().getColor(R.color.zxta_state_red));
            count_TextView.setTextColor(getResources().getColor(R.color.zxta_state_red));

            zan_LinearLayout_gz.setBackground(activity.getResources().getDrawable(R.drawable.shape_ll_zan_bg_hover));
            articleZan_TextView_gz.setText("\ue665");
            count_TextView_gz.setText(intDetailZanCount + "");
            articleZan_TextView_gz.setTextColor(getResources().getColor(R.color.zxta_state_red));
            count_TextView_gz.setTextColor(getResources().getColor(R.color.zxta_state_red));
        }
    }

    /**
     * ???????????????????????????
     */
    private void initDetailCollect(String loginUserCollectState, boolean isShowToast) {
        if ("0".equals(loginUserCollectState)) { // ?????????
            shou_TextView.setTextColor(getResources().getColor(R.color.font_content));
            shou_TextView.setText("\ue65e");
            tvSave.setTextColor(getResources().getColor(R.color.font_content));
            tvSave.setText("\ue65e");
            if (isShowToast) {
                showToast(getString(R.string.string_favorite_cancelled));
            }
        } else { // ?????????
            shou_TextView.setTextColor(getResources().getColor(R.color.my_shou));
            shou_TextView.setText("\ue666");
            tvSave.setTextColor(getResources().getColor(R.color.my_shou));
            tvSave.setText("\ue666");
            if (isShowToast) {
                showToast(getString(R.string.string_collection_success));
            }
        }
    }


    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);//????????????????????????webview?????????
        settings.setLoadsImagesAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setTextZoom((int) (100 * (GISharedPreUtil.getFloat(this, "FontScale") > 0 ? GISharedPreUtil.getFloat(this, "FontScale") : 1)));
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new JavascriptInterface(), "imagelistener");
    }


    public class JavascriptInterface {

        @android.webkit.JavascriptInterface
        public void openImage(String imgs, String img) {
            String[] strings = imgs.split(",");
            int position = 0;
            Bundle bundle = new Bundle();
            ArrayList imageList = new ArrayList();
            for (int i = 0; i < strings.length; i++) {
                imageList.add(strings[i]);
                if (strings[i].equals(img)) {
                    position = i;
                }
            }
            bundle.putStringArrayList("paths", imageList);
            bundle.putInt("position", position);
            showActivity(activity, ShowBigImgActivity.class, bundle);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { // ?????????????????????????????????
            super.onPageStarted(view, url, favicon);
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public void onPageFinished(WebView view, String url) { // ???????????????????????????
            if (isFristLoad) {
                isFristLoad = false;
                if (!ProjectNameUtil.isGYSZX(activity) && !ProjectNameUtil.isGZSZX(activity)) {
                    writeComment_LinearLayout.setVisibility(View.VISIBLE);
                }
                if (isShowComment) {
                    writeComment_LinearLayout.setVisibility(View.VISIBLE);
                }
                headerView.setVisibility(View.VISIBLE);
                if (isShowComment) {
                    getListData(true);
                }
                skeletonScreen.hide();
            }
            addImageClickListener();
        }

        private void addImageClickListener() {
            // ??????js???????????????????????????????????????img??????????????????onclick???????????????????????????????????????????????????????????????java???????????????url??????
            //if(isShow && objs[i].width>80)????????????????????????,??????????????????,??????????????????,?????????
            webView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "var imgUrl = \"\";" +
                    "var filter = [\"img//EventHead.png\",\"img//kong.png\",\"hdtz//button.png\"];" +
                    "var isShow = true;" +
                    "for(var i=0;i<objs.length;i++){" +
                    "for(var j=0;j<filter.length;j++){" +
                    "if(objs[i].src.indexOf(filter[j])>=0) {" +
                    "isShow = false; break;}}" +
                    "if(isShow && objs[i].width>50){" +
                    "imgUrl += objs[i].src + ',';isShow = true;" +
                    "    objs[i].onclick=function()  " +
                    "    {  "
                    + "        window.imagelistener.openImage(imgUrl,this.src);" +
                    "    }" +
                    "}" +
                    "}" +
                    "})()"
            );
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { // ?????????????????????????????????,7.0???????????????
            url = Utils.formatFileUrl(activity, url);
            myUrl = url;
            GILogUtil.i("shouldOverrideUrlLoading----->" + myUrl);
            if (url.contains("/upload/")) {
                //????????????
                HiPermission.create(activity).checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                    @Override
                    public void onGuarantee(String permisson, int position) { // ??????/?????????
                        String[] urlArr = new String[]{};
                        String strName = "";
                        try {
                            if (myUrl.contains("$")) {
                                urlArr = myUrl.split("\\$");
                                strName = URLDecoder.decode(urlArr[1], "UTF-8");
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String myDownLoadUrl = GIStringUtil.isBlank(urlArr[0]) ? myUrl : urlArr[0];
                        Intent intent = new Intent(activity, GIDownloadService.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("url", Utils.formatFileUrl(activity, myDownLoadUrl));
                        bundle.putString("filename", GIStringUtil.nullToEmpty(strName));
                        bundle.putInt("smallIcon", R.mipmap.ic_launcher);
                        intent.putExtras(bundle);
                        activity.startService(intent);
                    }

                    @Override
                    public void onClose() { // ????????????????????????
                    }

                    @Override
                    public void onFinish() { // ????????????????????????
                    }

                    @Override
                    public void onDeny(String permisson, int position) { // ??????
                    }
                });
            } else if (url.contains("/startRead/")) {
                setSeekBar();
                if (strListenContent.length() > 4000) {
                    speakText(strListenContent.substring(0, 4000));
                } else {
                    speakText(strListenContent);
                }
            } else {
                webView.loadUrl(url);
            }
            return true;
        }
    }

    /**
     * ??????????????????
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showToast(getString(R.string.string_initialization_failed_error_code) + "???" + code);
            }
        }
    };

    /**
     * ???????????????????????????????????????
     */
    private void speakText(String content) {
        Utils.setPlayParam(mTts);  // ????????????
        int code = mTts.startSpeaking(content, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            showToast(getString(R.string.string_speech_synthesis_failed_error_code) + ": " + code);
        }
    }

    /**
     * ?????????????????????
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // ????????????
//            mPercentForBuffering = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // ????????????
//            mPercentForPlaying = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
//
//            SpannableStringBuilder style=new SpannableStringBuilder(texts);
//            Log.e(TAG,"beginPos = "+beginPos +"  endPos = "+endPos);
//            style.setSpan(new BackgroundColorSpan(Color.RED),beginPos,endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ((EditText) findViewById(R.id.tts_text)).setText(style);
            GILogUtil.i("percent======================" + percent);
            seekBar.setProgress(percent);
            startTime_TextView.setText(percent + "");
            // seekBar.setProgress(percent);
            //tvStartTime.setText(percent + "");
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error != null) {
                showToast(error.getPlainDescription(true));
            } else {
                if (strListenContent.length() < 4000) {
                    showToast(getString(R.string.string_play_completed));
                    llVideo.setVisibility(View.GONE);
                } else {
                    speakText(strListenContent.substring(4000));
                }
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isShowComment && GISharedPreUtil.getBoolean(activity, "isHasLogin", false)) {
            endDate = new Date();
            long strDurationDate = (endDate.getTime() - startDate.getTime()) / 1000;
            textParamMap = new HashMap<>();
            textParamMap.put("strId", strNewsId);
            textParamMap.put("strType", "1");
            textParamMap.put("strDurationDate", String.valueOf(strDurationDate));
            doRequestNormal(ApiManager.getInstance().CommonReadRuleServlet(textParamMap), 99);
        }
        strTmpNewsId = strNewsId;
        GISharedPreUtil.setValue(activity, "position" + strNewsId, scrollView.getScrollY());
        GILogUtil.e("???????????????=====", scrollView.getScrollY());
        if (null != mTts) {
            mTts.stopSpeaking();
            mTts.destroy();
        }
    }

    private void setSeekBar() {
        llVideo.setVisibility(View.VISIBLE);
        play_TextView.setText(R.string.font_robot_pause);
        play_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play_TextView.getText().toString().equals(getString(R.string.font_robot_play))) {
                    play_TextView.setText(R.string.font_robot_pause);
                    mTts.resumeSpeaking();
                } else {
                    play_TextView.setText(R.string.font_robot_play);
                    mTts.pauseSpeaking();
                }
            }
        });
        close_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTts.pauseSpeaking();
                llVideo.setVisibility(View.GONE);
            }
        });
    }
}
