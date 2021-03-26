package com.suncn.ihold_zxztc.activity.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GITextView;
import com.luck.picture.lib.PictureSelector;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.BaseInterface;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.MainActivity;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.activity.global.ShowBigImgActivity;
import com.suncn.ihold_zxztc.adapter.Circle_MainHeader_RVAdapter;
import com.suncn.ihold_zxztc.adapter.Circle_Main_RVAdapter;
import com.suncn.ihold_zxztc.bean.BaseTypeBean;
import com.suncn.ihold_zxztc.bean.CircleListBean;
import com.suncn.ihold_zxztc.bean.CircleTagListBean;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.ShareUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.CircleTagPopWindow;
import com.suncn.ihold_zxztc.view.dialog.MyDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import skin.support.content.res.SkinCompatResources;

import static android.app.Activity.RESULT_OK;

/**
 * 圈子主界面
 */
public class Circle_MainFragment extends BaseFragment implements BaseInterface {
    @BindView(id = R.id.view_place)
    private View viewPlace;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.ll_head)
    private RelativeLayout llHead;
    @BindView(id = R.id.tv_tabLeft, click = true)
    private TextView tvTabLeft; // 动态Tab
    @BindView(id = R.id.tv_tabRight, click = true)
    private TextView tvTabRight; // 关注Tab
    @BindView(id = R.id.tv_remind, click = true)
    private GITextView tvRemind; // 消息提醒按钮
    @BindView(id = R.id.tv_count)
    private TextView tvCount;//消息提醒数量
    @BindView(id = R.id.add_dy, click = true)
    private CardView addDy; // 发动态按钮

    private Circle_MainHeader_RVAdapter headerAdapter; // 头部推荐关注/我的关注Adapter
    private Circle_Main_RVAdapter adapter; // 动态列表Adapter
    private int curPage = 1;
    private ArrayList<CircleListBean.RecommendBean> topObjInfo;
    private String strType = "0"; //1表示查询关注的人动态
    private String curCommentDysId = ""; //当前评论的动态id
    private int curReplyPosition = -1; //当前评论的位置，为了将评论数+1
    private MyDialog myDialog;
    private boolean isHeaderFollow; // 是否是头部的关注
    private int intHeaderClickPos; // 推荐关注中点击的关注按钮位置，用于关注成功后静态删除已被关注的人
    private CircleTagPopWindow circleTagPopWindow;//关注的标签下拉框
    private List<CircleTagListBean.CircleTagBean> tagList = new ArrayList<>();//关注的标签列表
    private String strSelectTag = "0000";//选择的标签值
    private String strSelectName = "关注";
    private String strReplyContent = "";//评论内容
    private String strSid;
    private int delectPosition = -1;//删除的位置

    @Override
    public View inflaterView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_circle_main, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            strSelectTag = "0000";
            getDynamicList(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).currentFragment == this) {
            if (DefineUtil.isNeedRefresh) {
                setTabStyle(Integer.parseInt(strType), true, true);
                DefineUtil.isNeedRefresh = false;
            } else {
                setTabStyle(Integer.parseInt(strType), false, false);
                if (strSelectTag.equals("0000")) {
                    tvTabRight.setText(R.string.string_follow);
                } else {
                    if (strSelectName.length() > 4) {
                        String substring = strSelectName.substring(0, 4);
                        tvTabRight.setText(substring + "...");
                    } else {
                        tvTabRight.setText(strSelectName);
                    }
                }
            }
            getNoRedCount();
        }
    }

    @Override
    public void initData() {
        setStatusBar(false);
        viewPlace.setVisibility(View.VISIBLE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        strSid = GISharedPreUtil.getString(activity, "strSid");
        tvRemind.setVisibility(View.VISIBLE);
        initRecyclerView();

        getRecommendList(true);
        getNoRedCount();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_tabLeft: // 动态Tab
                setTabStyle(0, true, false);
                break;
            case R.id.tv_tabRight: // 关注Tab
                setTabStyle(1, true, false);
                break;
            case R.id.add_dy: // 发动态按钮
                showActivity(fragment, Circle_AddDynamicActivity.class);
                break;
            case R.id.tv_remind: // 消息提醒按钮
                showActivity(fragment, ReminNoticeListActivity.class);
                tvCount.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getRecommendList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage++;
                getDynamicList(false);
            }
        });
    }

    @Override
    public void setOnClick(Object object, View view, int type) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.tv_follow: // 关注/取消关注按钮
            case R.id.ll_follow: // 推荐关注里的关注按钮
                if (type == 0) {
                    followUser(((CircleListBean.DynamicBean) object).getStrPubUserId(), ((CircleListBean.DynamicBean) object).getStrPubUserName());
                } else { // 点击了头部推荐关注的关注按钮
                    GIToastUtil.showMessage(activity, getString(R.string.string_have_alerday_followed) + ((CircleListBean.RecommendBean) object).getStrUserName());
                    isHeaderFollow = true;
                    intHeaderClickPos = headerAdapter.getItemPosition((CircleListBean.RecommendBean) object);
                    followUser(((CircleListBean.RecommendBean) object).getStrUserId(), ((CircleListBean.RecommendBean) object).getStrUserName());
                }
                break;
            case R.id.iv_img: // 点头像进个人主页
                bundle.putString("strUserId", type == 1 ? ((CircleListBean.RecommendBean) object).getStrUserId() : ((CircleListBean.DynamicBean) object).getStrPubUserId());
                if (0 == type && "0".equals(((CircleListBean.DynamicBean) object).getStrShowInterestButton())) {
                    bundle.putBoolean("isShowChat", false);
                }
                intent.putExtras(bundle);
                intent.setClass(activity, CirclePersonPageActivity.class);
                startActivity(intent);
                break;
            case R.id.mv_pic: // 点击图片，查看大图
                CircleListBean.DynamicBean dynamicBean = (CircleListBean.DynamicBean) object;
                if (dynamicBean.getPicList().size() == 1 && dynamicBean.getPicList().get(0).getStrFileType().equals("video")) {
                    PictureSelector.create(activity).externalPictureVideo(dynamicBean.getPicList().get(0).getStrThumbPath());
                } else {
                    ArrayList<String> imageList = new ArrayList<>();
                    for (int i = 0; i < dynamicBean.getPicList().size(); i++) {
                        imageList.add(Utils.formatFileUrl(activity, dynamicBean.getPicList().get(i).getStrPrimaryImagePath()));
                    }
                    intent.setClass(activity, ShowBigImgActivity.class);
                    intent.putExtra("paths", imageList);
                    intent.putExtra("position", adapter.getItemPosition(dynamicBean));
                    startActivity(intent);
                }
                break;
            case R.id.ll_comment: // 评论
                if (((CircleListBean.DynamicBean) object).getIntDiscussCount() == 0) {
                    GIUtil.showSoftInput(activity);
                    curReplyPosition = adapter.getItemPosition(((CircleListBean.DynamicBean) object));
                    curCommentDysId = ((CircleListBean.DynamicBean) object).getStrId();
                    strReplyContent = "";
                    myDialog = new MyDialog();
                    myDialog.setMyText(GISharedPreUtil.getString(activity, strSid + curCommentDysId));
                    myDialog.setOnItemClickListener(new MyDialog.onItemClickListener() {
                        @Override
                        public void onItemClick(View v, EditText inputComment, String s) {
                            if (GIStringUtil.isNotBlank(s)) {
                                inputComment.setEnabled(true);
                                inputComment.setFocusable(true);
                                inputComment.setFocusableInTouchMode(true);
                                inputComment.requestFocus();
                                strReplyContent = s;
                                GISharedPreUtil.setValue(activity, strSid + curCommentDysId, "");
                                commitReply(s);
                            } else {
                                showToast(getString(R.string.string_please_enter_comments));
                            }
                        }
                    });
                    //监听弹窗关闭事件，若未发布但是输入内容，记录缓存，发布过清楚缓存
                    myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (GIStringUtil.isBlank(strReplyContent)) {
                                GISharedPreUtil.setValue(activity, strSid + curCommentDysId, myDialog.getInputComment().getText().toString());
                            } else {
                                GISharedPreUtil.setValue(activity, strSid + curCommentDysId, "");
                            }
                        }
                    });
                    myDialog.show(activity.getFragmentManager(), "dialog");
                } else {
                    bundle.putString("strId", ((CircleListBean.DynamicBean) object).getStrId());
                    bundle.putBoolean("isFromComment", true);
                    intent.setClass(activity, Circle_DetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.ll_zan: // 点赞按钮
                commitZan(((CircleListBean.DynamicBean) object).getStrId());
                break;
            case R.id.ll_share: // 分享按钮
                CircleListBean.DynamicBean obj = ((CircleListBean.DynamicBean) object);
                ShareUtil.showDialog(activity, " ", obj.getObjShare().getStrTitle(), Utils.formatFileUrl(activity, obj.getObjShare().getStrIMGUrl()), Utils.formatFileUrl(activity, obj.getObjShare().getStrPageUrl()), new String[]{getString(R.string.string_wechat), getString(R.string.string_friend_circle), getString(R.string.string_qq), getString(R.string.string_weibo)});
                break;
            case R.id.ll_delete:
                deleteDynamic(((CircleListBean.DynamicBean) object).getStrId());
                break;
            default:
                break;
        }
    }

    //标签点击事件
    OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
            strSelectTag = tagList.get(position).getStrCode();
            strSelectName = tagList.get(position).getStrLabelName();
            if (strSelectTag.equals("0000")) {
                tvTabRight.setText(R.string.string_follow);
            } else {
                if (tagList.get(position).getStrLabelName().length() > 4) {
                    String substring = tagList.get(position).getStrLabelName().substring(0, 4);
                    tvTabRight.setText(substring + "...");
                } else {
                    tvTabRight.setText(tagList.get(position).getStrLabelName());
                }
            }
            getRecommendList(false);
            circleTagPopWindow.dismiss();
        }
    };

    /**
     * 获取动态通知未读数量
     */
    private void getNoRedCount() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().GetCircleNotRedNumber(textParamMap), 8);
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
     * 获取推荐关注/我的关注列表数据
     */
    private void getRecommendList(boolean b) {
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getRecommendList(textParamMap), 0);
    }

    /**
     * 获取动态列表
     */
    private void getDynamicList(boolean b) {
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        if (strType.equals("1")) {
            textParamMap.put("strTagCode", strSelectTag);
        }
        textParamMap.put("strType", strType);//1表示查询关注的人动态
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        doRequestNormal(ApiManager.getInstance().getDynamicList(textParamMap), 1);
    }

    /**
     * 执行关注请求
     */
    private void followUser(String strPubUserId, String strPubUserName) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strPubUserName", strPubUserName);
        textParamMap.put("strPubUserId", strPubUserId);
        doRequestNormal(ApiManager.getInstance().addDynamicNotice(textParamMap), 2);
    }

    /**
     * 执行点赞请求
     */
    private void commitZan(String strDynamicId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strDynamicId", strDynamicId);
        textParamMap.put("strType", "2");
        doRequestNormal(ApiManager.getInstance().addZan(textParamMap), 3);
    }

    /**
     * 执行评论请求
     */
    private void commitReply(String strReplyContent) {
        textParamMap = new HashMap<>();
        textParamMap.put("strParentId", "-1");
        textParamMap.put("strDynamicId", curCommentDysId);
        try {
            textParamMap.put("strReplyContent", URLEncoder.encode(strReplyContent, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        doRequestNormal(ApiManager.getInstance().addReply(textParamMap), 4);
    }

    /**
     * 获取已关注标签列表
     */
    private void getFollowTag() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().GetMemberFollowServlet(textParamMap), 5);
    }

    /**
     * 返回数据
     */
    private void doLogic(Object obj, int what) {
        try {
            switch (what) {
                case 0: // 推荐关注/我的关注
                    CircleListBean circleListBean = (CircleListBean) obj;
                    if (strType.equals("1")) {
                        topObjInfo = circleListBean.getMyNoticeList();
                    } else {
                        topObjInfo = circleListBean.getRecommendList();
                    }
                    curPage = 1;
                    getDynamicList(false);
                    break;
                case 1: // 主题列表
                    prgDialog.closePrgDialog();
                    adapter.removeAllHeaderView();
                    //关注tab下面，只有特别关注的时候才显示头部
                    if (topObjInfo != null && topObjInfo.size() > 0 && (strSelectTag.equals("0001") || strType.equals("0"))) {
                        adapter.addHeaderView(getHeadView(recyclerView));
                    }
                    circleListBean = (CircleListBean) obj;
                    if (curPage == 1) {
                        adapter.setList(circleListBean.getDynamicList());
                    } else {
                        adapter.addData(circleListBean.getDynamicList());
                    }
                    Utils.setLoadMoreViewState(circleListBean.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, "", refreshLayout, adapter);
                    break;
                case 2: // 关注
                    prgDialog.closePrgDialog();
                    if (isHeaderFollow || strType.equals("1")) {
                        headerAdapter.removeAt(intHeaderClickPos);
                        if (headerAdapter.getItemCount() == 0) { // 如果无推荐关注数据，需要隐藏推荐关注项
                            adapter.removeAllHeaderView();
                        }
                    }
                    break;
                case 3: // 点赞
                    prgDialog.closePrgDialog();
                    break;
                case 4: // 评论
                    if (curReplyPosition != -1) {
                        adapter.getItem(curReplyPosition).setIntDiscussCount(1);
                        adapter.notifyDataSetChanged();
                    }
                    if (myDialog.isVisible()) {
                        myDialog.dismiss();
                    }
                    break;
                case 5:
                    CircleTagListBean circleTagBean = (CircleTagListBean) obj;
                    tagList = circleTagBean.getObjList();
                    circleTagPopWindow = new CircleTagPopWindow(activity, tagList, itemClickListener);
                    circleTagPopWindow.setSelectCode(strSelectTag);
                    circleTagPopWindow.setWidth(llHead.getWidth());
                    circleTagPopWindow.setHeight(activity.getWindowManager().getDefaultDisplay().getHeight() - llHead.getHeight());
                    circleTagPopWindow.showAsDropDown(llHead);
                    setBackgroundAlpha(0.5f);
                    circleTagPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            Drawable drawable = getResources().getDrawable(R.drawable.shape_tab_line_white);/// 这一步必须要做,否则不会显示.
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            Drawable drawableRight = getResources().getDrawable(R.mipmap.down_icon);/// 这一步必须要做,否则不会显示.
                            drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
                            tvTabRight.setCompoundDrawables(null, null, drawableRight, drawable);
                            setBackgroundAlpha(1.0f);
                        }
                    });
                    break;
                case 7:
                    if (delectPosition != -1) {
                        adapter.getData().remove(delectPosition);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 8:
                    BaseTypeBean typeBean = (BaseTypeBean) obj;
                    if (typeBean.getIntNotReadCount() > 0 && typeBean.getIntNotReadCount() < 100) {
                        tvCount.setText(typeBean.getIntNotReadCount() + "");
                        tvCount.setVisibility(View.VISIBLE);
                    } else if (typeBean.getIntNotReadCount() >= 100) {
                        tvCount.setText("99+");
                        tvCount.setVisibility(View.VISIBLE);
                    } else {
                        tvCount.setText(typeBean.getIntNotReadCount() + "");
                        tvCount.setVisibility(View.GONE);
                    }
                    break;
            }
        } catch (Exception e) {
            showToast(getResources().getString(R.string.data_error));
        }
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        adapter = new Circle_Main_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.view_erv_empty);
        adapter.setHeaderWithEmptyEnable(true); // Header和Empty可同时存在
        adapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                CircleListBean.DynamicBean dynamicBean = (CircleListBean.DynamicBean) adapter.getItem(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("strId", dynamicBean.getStrId());
                bundle.putBoolean("isFromComment", false);
                if ("0".equals(dynamicBean.getStrShowInterestButton())) {
                    bundle.putBoolean("isShowChat", false);
                }
                intent.putExtras(bundle);
                intent.setClass(activity, Circle_DetailActivity.class);
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                String[] strings = new String[]{};
                CircleListBean.DynamicBean dynamicBean = (CircleListBean.DynamicBean) adapter.getItem(position);
                if (dynamicBean.getPicList() != null
                        && dynamicBean.getPicList().size() == 1
                        && dynamicBean.getPicList().get(0).getStrFileType().equals("video")) {
                    strings = new String[]{getString(R.string.string_delete)};
                } else {
                    strings = new String[]{getString(R.string.string_delete), getString(R.string.string_edit)};
                }
                if (GISharedPreUtil.getInt(activity, "intUserRole") == 1
                        && AppConfigUtil.isCanEditCircle(activity)) {
                    new AlertView(null, null, getString(R.string.string_cancle), null,
                            strings,
                            activity, AlertView.Style.ActionSheet, new com.bigkoo.alertview.OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position1) {
                            switch (position1) {
                                case 0:  //删除
                                    delectPosition = position;
                                    showLoginDialog(dynamicBean.getStrId());
                                    break;
                                case 1: //编辑
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("dynamicBean", dynamicBean);
                                    showActivity(fragment, Circle_AddDynamicActivity.class, bundle);
                                    break;
                            }
                        }
                    }).show();
                }
                return true;
            }
        });
    }

    /**
     * 初始化头部布局
     */
    private View getHeadView(RecyclerView recyclerView) {
        View view = getLayoutInflater().inflate(R.layout.head_circle_main, recyclerView, false);
        RecyclerView recyclerViewtop = view.findViewById(R.id.recyclerView_top);
        LinearLayoutManager layoutManagerTop = new LinearLayoutManager(getContext());
        layoutManagerTop.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewtop.setLayoutManager(layoutManagerTop);

        TextView tvMore = view.findViewById(R.id.tv_more);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Type", strType);
                showActivity(fragment, Circle_FocusListActivity.class, bundle, 0);
            }
        });

        TextView tvTitle = view.findViewById(R.id.tv_title);
        if (strType.equals("1")) {
            tvTitle.setText(R.string.string_my_follow);
            headerAdapter = new Circle_MainHeader_RVAdapter(Circle_MainFragment.this, true);
            headerAdapter.addChildClickViewIds(R.id.iv_img);
        } else {
            tvTitle.setText(R.string.string_recommend_follow);
            headerAdapter = new Circle_MainHeader_RVAdapter(Circle_MainFragment.this);
            headerAdapter.addChildClickViewIds(R.id.ll_follow, R.id.iv_img);
        }
        recyclerViewtop.setAdapter(headerAdapter);
        headerAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                setOnClick(adapter.getItem(position), view, 1);
            }
        });
        headerAdapter.setList(topObjInfo);

        if (topObjInfo.size() == 0) {
            view.setVisibility(View.GONE);
        }
        return view;
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
     * 设置头部标题tab按钮选择状态
     */
    private void setTabStyle(int type, boolean isLoad, boolean isRefresh) {
        Drawable drawable = getResources().getDrawable(R.drawable.shape_tab_line_white);/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable drawableRight = getResources().getDrawable(R.mipmap.down_icon);/// 这一步必须要做,否则不会显示.
        drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
        if (type == 0) { // 动态
            strType = "0";
            tvTabLeft.setTextColor(getResources().getColor(R.color.white));
            tvTabRight.setTextColor(SkinCompatResources.getColor(activity, R.color.top_unselect));
            tvTabLeft.setCompoundDrawables(null, null, null, drawable);
            tvTabRight.setCompoundDrawables(null, null, null, null);
            if (isLoad) {
                getRecommendList(false);
            }
        } else {
            if (!strType.equals("1") || !ProjectNameUtil.isIhold(activity) || isRefresh) {//如果是产品的话，需要设置下拉标签
                getRecommendList(true);
            } else {
                if (circleTagPopWindow != null && circleTagPopWindow.isShowing()) {
                    circleTagPopWindow.dismiss();
                } else {
                    if (isLoad) {
                        drawableRight = getResources().getDrawable(R.mipmap.up_icon);/// 这一步必须要做,否则不会显示.
                        drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
                        getFollowTag();
                    }
                }
            }
            strType = "1";
            tvTabLeft.setTextColor(SkinCompatResources.getColor(activity, R.color.top_unselect));
            tvTabRight.setTextColor(getResources().getColor(R.color.white));
            tvTabLeft.setCompoundDrawables(null, null, null, null);
            if (ProjectNameUtil.isIhold(activity)) {
                tvTabRight.setCompoundDrawables(null, null, drawableRight, drawable);
            } else {
                tvTabRight.setCompoundDrawables(null, null, null, drawable);
            }
        }
    }

    private void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }
}
