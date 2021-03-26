package com.suncn.ihold_zxztc.activity.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.google.android.material.appbar.AppBarLayout;
import com.longchat.base.QDClient;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.qd.longchat.activity.QDPersonChatActivity;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.BaseInterface;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Circle_Main_RVAdapter;
import com.suncn.ihold_zxztc.bean.CircleListBean;
import com.suncn.ihold_zxztc.bean.PersonPageBean;
import com.suncn.ihold_zxztc.interfaces.AppbarLayoutStateChange;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ShareUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.dialog.MyDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author :Sea
 * Date :2020-3-17 13:30
 * PackageName:com.suncn.ihold_zxztc.activity.circle
 * Desc: 个人圈子
 */
public class CirclePersonPageActivity extends BaseActivity implements BaseInterface {
    @BindView(id = R.id.view_place)
    private View viewPlace;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.btn_two, click = true)
    private GITextView btnNotice;
    @BindView(id = R.id.rl_head)
    private RelativeLayout rlHead;

    @BindView(id = R.id.layout_appbar)
    private AppBarLayout appBarLayout;


    //之前headView 中的内容
    @BindView(id = R.id.tv_myBack, click = true)
    private TextView tv_myBack;
    @BindView(id = R.id.iv_person_head)
    private ImageView ivPersonHead;
    @BindView(id = R.id.tv_notice_state, click = true)
    private TextView tvNoticeState;
    @BindView(id = R.id.tv_chat, click = true)
    private TextView chat_TextView;
    @BindView(id = R.id.tv_name)
    private TextView tvName;
    @BindView(id = R.id.tv_sector)
    private TextView tvSector;
    @BindView(id = R.id.tv_duty)
    private TextView tvDuty;
    @BindView(id = R.id.tv_notice_count)
    private TextView tvNoticeCount;
    @BindView(id = R.id.tv_zan_count)
    private TextView tvZanCount;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;


    private Circle_Main_RVAdapter adapter;
    private PopupWindow popupWindow;
    private PersonPageBean personPageBean;
    private String strUserId = "";
    private int curPage = 1;
    private int scrollY;
    private int curReplyPosition = -1;//当前评论的位置
    private boolean isShowChat = true;
    private View view;

    private MyDialog myDialog;
    private String comment = "";//回复评论的内容
    private String strPubUnitId = "";//企达id


    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar(false, false);
        setContentView(R.layout.activity_new_circle_person_page);
    }

    @Override
    public void initData() {
        super.initData();

        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strUserId = bundle.getString("strUserId");
            isShowChat = bundle.getBoolean("isShowChat", true);
        }
        if (!AppConfigUtil.isUseQDIM(activity)) {
            isShowChat = false;
        }
        btnNotice.setVisibility(View.VISIBLE);
        btnNotice.refreshFontType(activity, "2");
        btnNotice.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_normal));
        if (isShowChat) {
            goto_Button.setVisibility(View.VISIBLE);
            goto_Button.setText(getString(R.string.string_message));
            goto_Button.refreshFontType(activity, "2");
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_normal));
        } else {
            goto_Button.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnNotice.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            btnNotice.setLayoutParams(params); //使layout更新
        }
        viewPlace.setVisibility(View.VISIBLE);
        initRecyclerView();
        getPersonalInfo(true);


    }

    @Override
    public void setListener() {
        super.setListener();
        appBarLayout.addOnOffsetChangedListener(new AppbarLayoutStateChange.AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    rlHead.setVisibility(View.GONE);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    rlHead.setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                }
            }
        });
    }

    @Override
    public void setOnClick(Object object, View view, int type) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.ll_share:
                CircleListBean.DynamicBean obj = ((CircleListBean.DynamicBean) object);
                ShareUtil.showDialog(activity, " ", obj.getObjShare().getStrTitle(), Utils.formatFileUrl(activity, obj.getObjShare().getStrIMGUrl()), Utils.formatFileUrl(activity, obj.getObjShare().getStrPageUrl()), new String[]{getString(R.string.string_wechat), getString(R.string.string_friend_circle), getString(R.string.string_qq), getString(R.string.string_weibo)});

                break;
            case R.id.ll_zan:
                commitZan(((CircleListBean.DynamicBean) object).getStrId());
                break;
            case R.id.iv_img:
                break;
            case R.id.ll_comment:
                if (((CircleListBean.DynamicBean) object).getIntDiscussCount() == 0) {
                    curReplyPosition = adapter.getItemPosition(((CircleListBean.DynamicBean) object));
                    myDialog = new MyDialog();
                    comment = "";
                    myDialog.setMyText(GISharedPreUtil.getString(activity, ((CircleListBean.DynamicBean) object).getStrId() + ((CircleListBean.DynamicBean) object).getStrId()));
                    myDialog.setOnItemClickListener(new MyDialog.onItemClickListener() {
                        @Override
                        public void onItemClick(View v, EditText inputComment, String s) {
                            if (GIStringUtil.isNotBlank(s)) {
                                inputComment.setEnabled(true);
                                inputComment.setFocusable(true);
                                inputComment.setFocusableInTouchMode(true);
                                inputComment.requestFocus();
                                comment = s;
                                GISharedPreUtil.setValue(activity, ((CircleListBean.DynamicBean) object).getStrId() + ((CircleListBean.DynamicBean) object).getStrId(), "");
                                commitReply(((CircleListBean.DynamicBean) object).getStrId(), comment);
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
                                GISharedPreUtil.setValue(activity, ((CircleListBean.DynamicBean) object).getStrId() + ((CircleListBean.DynamicBean) object).getStrId(), myDialog.getInputComment().getText().toString());
                            } else {
                                GISharedPreUtil.setValue(activity, ((CircleListBean.DynamicBean) object).getStrId() + ((CircleListBean.DynamicBean) object).getStrId(), "");
                            }
                        }
                    });
                    myDialog.show(getFragmentManager(), "dialog");
                } else {
                    bundle.putString("strId", ((CircleListBean.DynamicBean) object).getStrId());
                    bundle.putBoolean("isFromComment", true);
                    intent.setClass(activity, Circle_DetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.ll_delete:
                deleteDynamic(((CircleListBean.DynamicBean) object).getStrId());
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_goto:
            case R.id.tv_chat:
                if (!QDClient.getInstance().isOnline()) {
                    showToast(getString(R.string.string_im_login_failed_please_relogin));
                    return;
                }
                if (AppConfigUtil.isUseQDIM(activity)) {
                    QDUser user = QDUserHelper.getUserById(strPubUnitId);
                    if (user == null) {
                        showToast(getString(R.string.string_this_user_not_register_im_and_cannot_chat));
                        return;
                    }
                    intent = new Intent(activity, QDPersonChatActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, strPubUnitId);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_USER, true);
                    startActivity(intent);
                }
                break;
            case R.id.tv_myBack:
                finish();
                break;
            case R.id.tv_notice_state:
                followUser(personPageBean.getMemberHome().getStrUserId(), personPageBean.getMemberHome().getStrUserName());
                break;
            default:
                break;
        }
    }

    /**
     * 删除动态
     */
    private void deleteDynamic(String strDynamicId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strDynamicId);
        doRequestNormal(ApiManager.getInstance().deleteDynamic(textParamMap), 7);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, true, false, R.color.main_bg, 10, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getPersonalInfo(false);
            }
        });

        adapter = new Circle_Main_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setShowNotic(false);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("strId", personPageBean.getDynamicList().get(position).getStrId());
                bundle.putBoolean("isFromComment", false);
                bundle.putBoolean("isFromPersonalPage", true);
                showActivity(activity, Circle_DetailActivity.class, bundle);
            }
        });
    }

    /**
     * 获取个人主页信息
     */
    private void getPersonalInfo(boolean b) {
        if (b)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strUserId", strUserId);
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        doRequestNormal(ApiManager.getInstance().getPersonPage(textParamMap), 0);
    }

    /**
     * 关注/取消关注
     */
    private void followUser(String strPubUserId, String strPubUserName) {
        textParamMap = new HashMap<>();
        textParamMap.put("strPubUserName", strPubUserName);
        textParamMap.put("strPubUserId", strPubUserId);
        doRequestNormal(ApiManager.getInstance().addDynamicNotice(textParamMap), 1);
    }

    /**
     * 点赞
     */
    private void commitZan(String strDynamicId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strDynamicId", strDynamicId);
        textParamMap.put("strType", "2");
        doRequestNormal(ApiManager.getInstance().addZan(textParamMap), 2);
    }

    /**
     * 评论
     */
    private void commitReply(String strDynamicId, String strReplyContent) {
        textParamMap = new HashMap<>();
        textParamMap.put("strParentId", "-1");
        textParamMap.put("strDynamicId", strDynamicId);
        try {
            textParamMap.put("strReplyContent", URLEncoder.encode(strReplyContent, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        doRequestNormal(ApiManager.getInstance().addReply(textParamMap), 3);
    }

    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    personPageBean = (PersonPageBean) obj;
                    strPubUnitId = personPageBean.getStrQDUserId();
                    setHeadTitle(personPageBean.getMemberHome().getStrUserName());
                    setOldAddHeadDate();
                    if (personPageBean.getMemberHome().getLoginUserIsNotice()) {
                        btnNotice.setText(getString(R.string.string_followed));
                    } else {
                        btnNotice.setText("+" + getString(R.string.string_follow));
                    }
                    if ("1".equals(personPageBean.getMemberHome().getStrShowInterestButton())) {
                        btnNotice.setVisibility(View.VISIBLE);
                        if (isShowChat) {
                            goto_Button.setVisibility(View.VISIBLE);
                            goto_Button.setText(getString(R.string.string_message));
                        }
                    } else {
                        btnNotice.setVisibility(View.INVISIBLE);
                        goto_Button.setVisibility(View.INVISIBLE);
                    }
                    Utils.setLoadMoreViewState(personPageBean.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, "", refreshLayout, adapter);
                    adapter.setEmptyView(R.layout.view_empty_dynamic);
                    if (curPage == 1) {
                        adapter.setList(personPageBean.getDynamicList());
                    } else {
                        adapter.addData(personPageBean.getDynamicList());
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    showToast(getResources().getString(R.string.data_error));
                }
                break;
            case 1:
                try {
                    if (btnNotice.getText().equals(getString(R.string.string_followed))) {
                        btnNotice.setText("+" + getString(R.string.string_follow));
                        personPageBean.getMemberHome().setLoginUserIsNotice(false);
                    } else {
                        btnNotice.setText(getString(R.string.string_followed));
                        personPageBean.getMemberHome().setLoginUserIsNotice(true);
                    }

                    int intCount = Integer.parseInt(personPageBean.getMemberHome().getIntNoticedAllCount());
                    if (tvNoticeState.getText().equals(getString(R.string.string_followed))) {
                        tvNoticeState.setText("+" + getString(R.string.string_follow));
                        tvNoticeCount.setText(intCount - 1 + "");
                        personPageBean.getMemberHome().setIntNoticedAllCount(intCount - 1 + "");
                    } else {
                        tvNoticeState.setText(getString(R.string.string_followed));
                        tvNoticeCount.setText(intCount + 1 + "");
                        personPageBean.getMemberHome().setIntNoticedAllCount(intCount + 1 + "");

                    }
                    //adapter.notifyDataSetChanged();
                    DefineUtil.isNeedRefresh = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(getResources().getString(R.string.data_error));
                }
                break;
            case 2:
                break;
            case 3:
                if (curReplyPosition != -1) {
                    adapter.getItem(curReplyPosition).setIntDiscussCount(1);
                    adapter.notifyDataSetChanged();
                }
                curReplyPosition = -1;
                if (myDialog.isVisible()) {
                    myDialog.dismiss();
                }
                break;
            case 7:
                showToast(getString(R.string.string_del_success));
                DefineUtil.isNeedRefresh = true;
                adapter.notifyDataSetChanged();
                break;
        }
    }


    /**
     * 之前顶部是通过RecyclerView AddHead方式添加头部的
     */
    public void setOldAddHeadDate() {
        GIImageUtil.loadImg(activity, ivPersonHead, Utils.formatFileUrl(activity, personPageBean.getMemberHome().getStrPhotoPath()), 1);
        if (personPageBean.getMemberHome().getLoginUserIsNotice()) {
            tvNoticeState.setText(getString(R.string.string_followed));
        } else {
            tvNoticeState.setText("+" + getString(R.string.string_follow));
        }
        if ("1".equals(personPageBean.getMemberHome().getStrShowInterestButton())) {
            tvNoticeState.setVisibility(View.VISIBLE);
        } else {
            tvNoticeState.setVisibility(View.INVISIBLE);
        }
        if (isShowChat) {
            chat_TextView.setVisibility(View.VISIBLE);
        } else {
            chat_TextView.setVisibility(View.GONE);
        }


        tvName.setText(personPageBean.getMemberHome().getStrUserName());
        tvSector.setText(personPageBean.getMemberHome().getStrFaction() + "　" + personPageBean.getMemberHome().getStrSector());
        tvDuty.setText(personPageBean.getMemberHome().getStrDuty());
        tvNoticeCount.setText(personPageBean.getMemberHome().getIntNoticedAllCount());
        tvZanCount.setText(personPageBean.getMemberHome().getIntPraiseAllCount());

    }
}
