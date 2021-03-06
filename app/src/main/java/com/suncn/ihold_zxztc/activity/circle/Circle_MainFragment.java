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
 * ???????????????
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
    private TextView tvTabLeft; // ??????Tab
    @BindView(id = R.id.tv_tabRight, click = true)
    private TextView tvTabRight; // ??????Tab
    @BindView(id = R.id.tv_remind, click = true)
    private GITextView tvRemind; // ??????????????????
    @BindView(id = R.id.tv_count)
    private TextView tvCount;//??????????????????
    @BindView(id = R.id.add_dy, click = true)
    private CardView addDy; // ???????????????

    private Circle_MainHeader_RVAdapter headerAdapter; // ??????????????????/????????????Adapter
    private Circle_Main_RVAdapter adapter; // ????????????Adapter
    private int curPage = 1;
    private ArrayList<CircleListBean.RecommendBean> topObjInfo;
    private String strType = "0"; //1??????????????????????????????
    private String curCommentDysId = ""; //?????????????????????id
    private int curReplyPosition = -1; //??????????????????????????????????????????+1
    private MyDialog myDialog;
    private boolean isHeaderFollow; // ????????????????????????
    private int intHeaderClickPos; // ????????????????????????????????????????????????????????????????????????????????????????????????
    private CircleTagPopWindow circleTagPopWindow;//????????????????????????
    private List<CircleTagListBean.CircleTagBean> tagList = new ArrayList<>();//?????????????????????
    private String strSelectTag = "0000";//??????????????????
    private String strSelectName = "??????";
    private String strReplyContent = "";//????????????
    private String strSid;
    private int delectPosition = -1;//???????????????

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
            case R.id.tv_tabLeft: // ??????Tab
                setTabStyle(0, true, false);
                break;
            case R.id.tv_tabRight: // ??????Tab
                setTabStyle(1, true, false);
                break;
            case R.id.add_dy: // ???????????????
                showActivity(fragment, Circle_AddDynamicActivity.class);
                break;
            case R.id.tv_remind: // ??????????????????
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
            case R.id.tv_follow: // ??????/??????????????????
            case R.id.ll_follow: // ??????????????????????????????
                if (type == 0) {
                    followUser(((CircleListBean.DynamicBean) object).getStrPubUserId(), ((CircleListBean.DynamicBean) object).getStrPubUserName());
                } else { // ??????????????????????????????????????????
                    GIToastUtil.showMessage(activity, getString(R.string.string_have_alerday_followed) + ((CircleListBean.RecommendBean) object).getStrUserName());
                    isHeaderFollow = true;
                    intHeaderClickPos = headerAdapter.getItemPosition((CircleListBean.RecommendBean) object);
                    followUser(((CircleListBean.RecommendBean) object).getStrUserId(), ((CircleListBean.RecommendBean) object).getStrUserName());
                }
                break;
            case R.id.iv_img: // ????????????????????????
                bundle.putString("strUserId", type == 1 ? ((CircleListBean.RecommendBean) object).getStrUserId() : ((CircleListBean.DynamicBean) object).getStrPubUserId());
                if (0 == type && "0".equals(((CircleListBean.DynamicBean) object).getStrShowInterestButton())) {
                    bundle.putBoolean("isShowChat", false);
                }
                intent.putExtras(bundle);
                intent.setClass(activity, CirclePersonPageActivity.class);
                startActivity(intent);
                break;
            case R.id.mv_pic: // ???????????????????????????
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
            case R.id.ll_comment: // ??????
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
                    //????????????????????????????????????????????????????????????????????????????????????????????????
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
            case R.id.ll_zan: // ????????????
                commitZan(((CircleListBean.DynamicBean) object).getStrId());
                break;
            case R.id.ll_share: // ????????????
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

    //??????????????????
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
     * ??????????????????????????????
     */
    private void getNoRedCount() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().GetCircleNotRedNumber(textParamMap), 8);
    }

    /**
     * ????????????
     */
    private void deleteDynamic(String strDynamicId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strDynamicId);
        doRequestNormal(ApiManager.getInstance().deleteDynamic(textParamMap), 7);
    }

    /**
     * ??????????????????/????????????????????????
     */
    private void getRecommendList(boolean b) {
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getRecommendList(textParamMap), 0);
    }

    /**
     * ??????????????????
     */
    private void getDynamicList(boolean b) {
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        if (strType.equals("1")) {
            textParamMap.put("strTagCode", strSelectTag);
        }
        textParamMap.put("strType", strType);//1??????????????????????????????
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        doRequestNormal(ApiManager.getInstance().getDynamicList(textParamMap), 1);
    }

    /**
     * ??????????????????
     */
    private void followUser(String strPubUserId, String strPubUserName) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strPubUserName", strPubUserName);
        textParamMap.put("strPubUserId", strPubUserId);
        doRequestNormal(ApiManager.getInstance().addDynamicNotice(textParamMap), 2);
    }

    /**
     * ??????????????????
     */
    private void commitZan(String strDynamicId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strDynamicId", strDynamicId);
        textParamMap.put("strType", "2");
        doRequestNormal(ApiManager.getInstance().addZan(textParamMap), 3);
    }

    /**
     * ??????????????????
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
     * ???????????????????????????
     */
    private void getFollowTag() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().GetMemberFollowServlet(textParamMap), 5);
    }

    /**
     * ????????????
     */
    private void doLogic(Object obj, int what) {
        try {
            switch (what) {
                case 0: // ????????????/????????????
                    CircleListBean circleListBean = (CircleListBean) obj;
                    if (strType.equals("1")) {
                        topObjInfo = circleListBean.getMyNoticeList();
                    } else {
                        topObjInfo = circleListBean.getRecommendList();
                    }
                    curPage = 1;
                    getDynamicList(false);
                    break;
                case 1: // ????????????
                    prgDialog.closePrgDialog();
                    adapter.removeAllHeaderView();
                    //??????tab???????????????????????????????????????????????????
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
                case 2: // ??????
                    prgDialog.closePrgDialog();
                    if (isHeaderFollow || strType.equals("1")) {
                        headerAdapter.removeAt(intHeaderClickPos);
                        if (headerAdapter.getItemCount() == 0) { // ?????????????????????????????????????????????????????????
                            adapter.removeAllHeaderView();
                        }
                    }
                    break;
                case 3: // ??????
                    prgDialog.closePrgDialog();
                    break;
                case 4: // ??????
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
                            Drawable drawable = getResources().getDrawable(R.drawable.shape_tab_line_white);/// ?????????????????????,??????????????????.
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            Drawable drawableRight = getResources().getDrawable(R.mipmap.down_icon);/// ?????????????????????,??????????????????.
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
     * ?????????RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        adapter = new Circle_Main_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.view_erv_empty);
        adapter.setHeaderWithEmptyEnable(true); // Header???Empty???????????????
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
                                case 0:  //??????
                                    delectPosition = position;
                                    showLoginDialog(dynamicBean.getStrId());
                                    break;
                                case 1: //??????
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
     * ?????????????????????
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
     * ???????????????
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
     * ??????????????????tab??????????????????
     */
    private void setTabStyle(int type, boolean isLoad, boolean isRefresh) {
        Drawable drawable = getResources().getDrawable(R.drawable.shape_tab_line_white);/// ?????????????????????,??????????????????.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable drawableRight = getResources().getDrawable(R.mipmap.down_icon);/// ?????????????????????,??????????????????.
        drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(), drawableRight.getMinimumHeight());
        if (type == 0) { // ??????
            strType = "0";
            tvTabLeft.setTextColor(getResources().getColor(R.color.white));
            tvTabRight.setTextColor(SkinCompatResources.getColor(activity, R.color.top_unselect));
            tvTabLeft.setCompoundDrawables(null, null, null, drawable);
            tvTabRight.setCompoundDrawables(null, null, null, null);
            if (isLoad) {
                getRecommendList(false);
            }
        } else {
            if (!strType.equals("1") || !ProjectNameUtil.isIhold(activity) || isRefresh) {//????????????????????????????????????????????????
                getRecommendList(true);
            } else {
                if (circleTagPopWindow != null && circleTagPopWindow.isShowing()) {
                    circleTagPopWindow.dismiss();
                } else {
                    if (isLoad) {
                        drawableRight = getResources().getDrawable(R.mipmap.up_icon);/// ?????????????????????,??????????????????.
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
