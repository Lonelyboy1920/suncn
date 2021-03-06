package com.suncn.ihold_zxztc.activity.global;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.BaseInterface;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_DetailActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_SignUpActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_DetailActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_FeedbackActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.hot.NewsDetailActivity;
import com.suncn.ihold_zxztc.adapter.Message_Notice_RVAdapter;
import com.suncn.ihold_zxztc.adapter.Message_Remind_RVAdapter;
import com.suncn.ihold_zxztc.adapter.MyViewPagerAdapter;
import com.suncn.ihold_zxztc.adapter.OverSearch_RVAdapter;
import com.suncn.ihold_zxztc.adapter.SearchHistoryAdapter;
import com.suncn.ihold_zxztc.bean.MessageRemindListBean;
import com.suncn.ihold_zxztc.bean.NewsColumnListBean;
import com.suncn.ihold_zxztc.bean.NoticeAnnouncementSearchListBean;
import com.suncn.ihold_zxztc.bean.ObjAnnouncementBean;
import com.suncn.ihold_zxztc.bean.OverSearchBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.DividerDecoration;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * ????????????/??????????????????/??????????????????
 */
public class OverSearchListActivity extends BaseActivity implements BaseInterface {
    @BindView(id = R.id.tabLayout)
    private TabLayout tabLayout;//??????MyTabLayout
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    @BindView(id = R.id.ll_title)
    private LinearLayout llTitle;
    @BindView(id = R.id.ll_search)
    private LinearLayout llSearch;
    @BindView(id = R.id.et_search)
    private ClearEditText etSearch;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.tv_empty)
    private ImageView empty_ImageView;
    private int currentIndex = 0;
    private ArrayList<View> myViews;
    private ArrayList<OverSearch_RVAdapter> adapters;
    private ArrayList<Message_Remind_RVAdapter> messageAdapters;
    private ArrayList<Message_Notice_RVAdapter> noticeAdapters;
    private ArrayList<Integer> intCurPages;
    private List<NewsColumnListBean.NewsColumnBean> tabList = new ArrayList<>();
    private String strKeyValue = "";
    private ArrayList<String> serveList;
    private SearchHistoryAdapter historyAdapter;
    private int AllCount;
    private int searchType = 0;//0-?????????????????????1-???????????????2-????????????
    private String spName; // ?????????????????????sp??????

    public static final String ACTION_OPEN_SEARCH = "com.suncn.ihold_zxztc.activity.global.action.DYNAMIC_OPEN";

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_oversearch_list);
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
            searchType = bundle.getInt("searchType");
        }
        llTitle.setVisibility(View.GONE);
        llSearch.setVisibility(View.VISIBLE);
        getColumn();
        if (0 == searchType)
            setSearchHistory();
    }

    @Override
    public void setListener() {
        super.setListener();
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    empty_ImageView.setVisibility(View.GONE);
                    intCurPages.set(currentIndex, 1);
                    strKeyValue = etSearch.getText().toString();
                    getEtSearchChange();
                    return true;
                }
                return false;
            }
        });
        etSearch.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                empty_ImageView.setVisibility(View.GONE);
                intCurPages.set(currentIndex, 1);
                strKeyValue = keyword;
                getEtSearchChange();
            }
        });
    }

    /**
     * ??????????????????
     */
    private void getColumn() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        switch (searchType) {
            case 0: // ????????????
                spName = "searchHistory";
                if (!GISharedPreUtil.getBoolean(this, "isHasLogin", false)) {
                    textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
                } else {
                    textParamMap.put("strSid", GISharedPreUtil.getString(activity, "strSid"));
                }
                doRequestNormal(ApiManager.getInstance().getSearchColumn(textParamMap), 1);
                break;
            case 1: // ????????????
                spName = "messageRemindHistory";
                doRequestNormal(ApiManager.getInstance().getRemindSearchColumn(textParamMap), 1);
                break;
            case 2: // ????????????
                spName = "noticeSearchHistory";
                doRequestNormal(ApiManager.getInstance().getNoticeSearchColumn(textParamMap), 1);
                break;
        }

    }

    /**
     * ?????????????????????????????????
     */
    private void getEtSearchChange() {
        if (GIStringUtil.isBlank(strKeyValue)) {
            if (serveList == null || serveList.size() == 0) {
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
            }
            if (0 == searchType) {
                historyAdapter.setList(serveList);
            }
            viewPager.setVisibility(View.GONE);
            GIUtil.closeSoftInput(activity);
            if (searchType == 2 || searchType == 1) {
                getListData(false);
            }
        } else {
            getListData(false);
            recyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * ????????????????????????
     */
    private void setSearchHistory() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0.5f, 10);
        String searchHistory = GISharedPreUtil.getString(activity, spName).trim();
        historyAdapter = new SearchHistoryAdapter(activity, searchType);
        if (GIStringUtil.isNotBlank(searchHistory)) {
            String[] History = searchHistory.split(",");
            serveList = new ArrayList<>(Arrays.asList(History));
        } else {
            serveList = new ArrayList<>();
        }
        if (serveList.size() > 10) {
            serveList = new ArrayList<>(serveList.subList(0, 10));
        }
        recyclerView.setVisibility(View.VISIBLE);
        historyAdapter.addData(serveList);
        historyAdapter.setHeaderView(getHeadView());
        recyclerView.setAdapter(historyAdapter);
        historyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                strKeyValue = historyAdapter.getItem(position);
                etSearch.setText(strKeyValue);
                prgDialog.showLoadDialog();
            }
        });

        if (serveList != null && serveList.size() > 0) {
            empty_ImageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            empty_ImageView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    private View getHeadView() {
        View headerView = getLayoutInflater().inflate(R.layout.view_head_searchhistory, recyclerView, false);
        TextView tvClear = headerView.findViewById(R.id.tv_clear);
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
        return headerView;

    }

    /**
     * ??????????????????
     */
    private void getListData(boolean b) {
        if (b)
            prgDialog.showLoadDialog();
        if (GIStringUtil.isNotBlank(strKeyValue) && 0 == searchType) {
            setSettingData(strKeyValue, 0);
        }
        viewPager.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        textParamMap = new HashMap<>();
        textParamMap.put("strKeyValue", strKeyValue);
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages.get(currentIndex) + "");
        textParamMap.put("strSorceType", tabList.get(currentIndex).getStrType());
        switch (searchType) {
            case 0: // ????????????
                if (!GISharedPreUtil.getBoolean(this, "isHasLogin", false)) {
                    textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
                } else {
                    textParamMap.put("strSid", GISharedPreUtil.getString(activity, "strSid"));
                }
                doRequestNormal(ApiManager.getInstance().getOverSearchList(textParamMap), 0);
                break;
            case 1: // ????????????
                doRequestNormal(ApiManager.getInstance().getMessageRemindSearchList(textParamMap), 2);
                break;
            case 2: // ????????????
                doRequestNormal(ApiManager.getInstance().getNoticeAnnouncementSearchList(textParamMap), 3);
                break;
        }
    }

    /**
     * ?????????tab???
     */
    private void initTab() {
        currentIndex = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        myViews = new ArrayList<>();
        adapters = new ArrayList<>();
        messageAdapters = new ArrayList<>();
        noticeAdapters = new ArrayList<>();
        intCurPages = new ArrayList<>();
        for (int i = 0; i < tabList.size(); i++) {
            intCurPages.add(1);
            initAddView();
        }
        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(myViews, tabList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(currentIndex);
        viewPager.addOnPageChangeListener(pageListener);
        tabLayout.setupWithViewPager(viewPager);
        if (tabList.size() == 1 || tabList.size() == 0) {
            tabLayout.setVisibility(View.GONE);
        } else {
            tabLayout.setVisibility(View.VISIBLE);
        }
        if ((0 == searchType)) {
            prgDialog.closePrgDialog();
        } else {
            getListData(true);
        }
    }

    ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int position, float offset, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            viewPager.setCurrentItem(position);
            currentIndex = position;
            if (GIStringUtil.isNotBlank(strKeyValue) || searchType == 1 || searchType == 2)
                getListData(true);
        }
    };


    private void doLogic(int what, Object object) {
        SmartRefreshLayout smartRefreshLayout;
        try {
            switch (what) {
                case 1:
                    NewsColumnListBean result = (NewsColumnListBean) object;
                    tabList = result.getObjList();
                    if (tabList != null && tabList.size() > 0) {
                        initTab();
                    } else {
                        prgDialog.closePrgDialog();
                    }
                    break;
                case 0:
                    prgDialog.closePrgDialog();
                    OverSearchBean overSearchBean = (OverSearchBean) object;
                    OverSearch_RVAdapter adapter = adapters.get(currentIndex);
                    smartRefreshLayout = (myViews.get(currentIndex)).findViewById(R.id.refreshLayout);
                    AllCount = overSearchBean.getIntAllCount();
                    if (intCurPages.get(currentIndex) == 1) {
                        TextView overSearch_TextView = adapter.getHeaderLayout().findViewById(R.id.tv_text);
                        overSearch_TextView.setText("???????????????" + AllCount + "?????????");
                        adapter.setList(overSearchBean.getObjList());
                    } else {
                        adapter.addData(overSearchBean.getObjList());
                    }

                    Utils.setLoadMoreViewState(AllCount, intCurPages.get(currentIndex) * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, smartRefreshLayout, adapter);

                    break;
                case 2://????????????
                    prgDialog.closePrgDialog();
                    MessageRemindListBean messageRemindListBean = (MessageRemindListBean) object;
                    Message_Remind_RVAdapter messageRemindListAdapter = messageAdapters.get(currentIndex);
                    AllCount = messageRemindListBean.getIntAllCount();
                    smartRefreshLayout = (myViews.get(currentIndex)).findViewById(R.id.refreshLayout);
                    if (intCurPages.get(currentIndex) == 1) {
                        TextView messageSearch_TextView = messageRemindListAdapter.getHeaderLayout().findViewById(R.id.tv_text);
                        messageSearch_TextView.setText("???????????????" + AllCount + "?????????");
                        messageRemindListAdapter.setList(messageRemindListBean.getObjList());
                    } else {
                        messageRemindListAdapter.addData(messageRemindListBean.getObjList());
                    }
                    Utils.setLoadMoreViewState(AllCount, intCurPages.get(currentIndex) * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, smartRefreshLayout, messageRemindListAdapter);


                    break;
                case 3:
                    prgDialog.closePrgDialog();
                    NoticeAnnouncementSearchListBean noticeListBean = (NoticeAnnouncementSearchListBean) object;
                    Message_Notice_RVAdapter noticeAdapter = noticeAdapters.get(currentIndex);
                    AllCount = noticeListBean.getIntAllCount();
                    smartRefreshLayout = (myViews.get(currentIndex)).findViewById(R.id.refreshLayout);
                    if (intCurPages.get(currentIndex) == 1) {
                        TextView noticeSearch_TextView = noticeAdapter.getHeaderLayout().findViewById(R.id.tv_text);
                        noticeSearch_TextView.setText("???????????????" + AllCount + "?????????");
                        noticeAdapter.setList(noticeListBean.getObjList());
                    } else {
                        noticeAdapter.addData(noticeListBean.getObjList());
                    }
                    Utils.setLoadMoreViewState(AllCount, intCurPages.get(currentIndex) * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, smartRefreshLayout, noticeAdapter);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getResources().getString(R.string.data_error));
        }
    }

    /**
     * ??????viewpage??????
     */
    private void initAddView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_viewpager_add_view, null);
        myViews.add(view);
        ImageView empty_ImageView = view.findViewById(R.id.tv_empty);
        RecyclerView easyRecyclerView = view.findViewById(R.id.recyclerView);
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        easyRecyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
        easyRecyclerView.setLayoutManager(new LinearLayoutManager(activity)); //???ERV?????????????????????
        empty_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intCurPages.set(currentIndex, 1);
                getListData(true);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                intCurPages.set(currentIndex, 1);
                getListData(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                intCurPages.set(currentIndex, intCurPages.get(currentIndex) + 1);
                getListData(false);

            }
        });
        int dividerHeight = 0;
        switch (searchType) {
            case 0: // ????????????
                dividerHeight = 10;
                OverSearch_RVAdapter adapter = new OverSearch_RVAdapter(activity);
                easyRecyclerView.setAdapter(adapter);
                adapter.addHeaderView(getHeadView(easyRecyclerView));
                adapters.add(adapter);
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                        OverSearchBean.OverSearchList objInfo = (OverSearchBean.OverSearchList) adapter.getItem(position);
                        Bundle bundle = new Bundle();
                        if (objInfo.getStrSorceType().equals("01")) {//??????
                            bundle.putString("strId", objInfo.getStrId());
                            showActivity(activity, Proposal_DetailActivity.class, bundle);
                        } else if (objInfo.getStrSorceType().equals("02")) {//????????????
                            bundle.putString("strUrl", objInfo.getStrUrl());
                            bundle.putString("headTitle", "????????????");
                            showActivity(activity, WebViewActivity.class, bundle);
                        } else if (objInfo.getStrSorceType().equals("03")) {//??????
                            bundle.putString("strId", objInfo.getStrId());
                            bundle.putString("headTitle", "????????????");
                            bundle.putInt("sign", DefineUtil.hygl);
                            showActivity(activity, MeetAct_DetailActivity.class, bundle);
                        } else if (objInfo.getStrSorceType().equals("04")) {//??????
                            bundle.putString("strId", objInfo.getStrId());
                            bundle.putBoolean("onlyShowNormalInfo", true);
                            bundle.putInt("sign", DefineUtil.hdgl);
                            bundle.putString("headTitle", "????????????");
                            showActivity(activity, MeetAct_DetailActivity.class, bundle);
                        } else if (objInfo.getStrSorceType().equals("05")) {//????????????
                            bundle.putString("strUrl", objInfo.getStrUrl());
                            bundle.putString("headTitle", "????????????");
                            showActivity(activity, WebViewActivity.class, bundle);
                        } else if (objInfo.getStrSorceType().equals("07")) {//????????????????????????
                            bundle.putString("strUrl", objInfo.getStrUrl());
                            bundle.putString("strNewsId", objInfo.getStrId());
                            showActivity(activity, NewsDetailActivity.class, bundle);
                        } else {
                            bundle.putString("headTitle", "????????????");//??????
                            bundle.putString("strUrl", objInfo.getStrUrl());
                            bundle.putString("strNewsId", objInfo.getStrId());
                            bundle.putBoolean("isNews", true);
                            showActivity(activity, NewsDetailActivity.class, bundle);
                        }
                    }
                });
                break;
            case 1: // ????????????
                dividerHeight = 0;
                easyRecyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
                Message_Remind_RVAdapter messageAdapter = new Message_Remind_RVAdapter(activity);
                easyRecyclerView.setAdapter(messageAdapter);
                messageAdapter.addHeaderView(getHeadView(easyRecyclerView));
                messageAdapters.add(messageAdapter);
                messageAdapter.addChildClickViewIds(R.id.ll_state);
                messageAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                        setOnClick(adapter.getItem(position), view, -1);
                    }
                });
                messageAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                        MessageRemindListBean.MessageRemindBean messageRemindBean = (MessageRemindListBean.MessageRemindBean) messageAdapter.getItem(position);
                        String strAffirType = messageRemindBean.getStrAffirType();
                        if ("0".equals(messageRemindBean.getStrMobileState())) {
                            messageRemindBean.setStrMobileState("1");
                            messageAdapter.notifyDataSetChanged();
                        }
                        dealReadRemind(messageRemindBean.getStrId());
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isFromRemindList", true);
                        switch (strAffirType) {
                            case "01"://??????
                                String strAttend = "";
                                String strFlowState = "";
                                String strButtonJSFunction = messageRemindBean.getStrButtonJSFunction();
                                if ("feedBackHandler".equals(strButtonJSFunction)) {
                                    strFlowState = "?????????";
                                } else if ("showCaseMotionAlly".equals(strButtonJSFunction)) {
                                    strAttend = "2";
                                }
                                bundle.putString("strId", messageRemindBean.getStrAffirId());
                                bundle.putString("headTitle", "????????????");
                                bundle.putString("strAttend", strAttend);
                                bundle.putString("strFlowState", strFlowState);
                                showActivity(activity, Proposal_DetailActivity.class, bundle, 0);
                                break;
                            case "02":// ??????  strButtonJSFunction= showMeet ??????????????????
                            case "03"://  ??????  strButtonJSFunction =showEvent ??????????????????
                                boolean isShowSign = false;//??????????????????
                                if ("showMeet".equals(messageRemindBean.getStrButtonJSFunction()) || "showEvent".equals(messageRemindBean.getStrButtonJSFunction())) {
                                    isShowSign = true;
                                }
                                bundle.putString("strId", messageRemindBean.getStrAffirId());
                                bundle.putBoolean("isShowSign", isShowSign);
                                if (strAffirType.equals("03")) {
                                    bundle.putInt("sign", DefineUtil.wdhd);
                                    bundle.putString("headTitle", "????????????");
                                } else {
                                    bundle.putInt("sign", DefineUtil.wdhy);
                                    bundle.putString("headTitle", "????????????");
                                }
                                bundle.putBoolean("isFromList", false);
                                bundle.putBoolean("onlyShowNormalInfo", true);
                                showActivity(activity, MeetAct_DetailActivity.class, bundle, 2);
                                break;
                            case "04":// ????????????
                            case "05": // ??????
                            case "06":// ????????????
                                bundle.putString("headTitle", "????????????");
                                bundle.putBoolean("isMsgRemind", true);
                                bundle.putString("strUrl", messageRemindBean.getStrUrl());
                                showActivity(activity, WebViewActivity.class, bundle, 4);
                                break;
                        }
                    }
                });
                break;
            case 2: // ????????????
                dividerHeight = 0;
                easyRecyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
                Message_Notice_RVAdapter noticeAdapter = new Message_Notice_RVAdapter(activity);
                easyRecyclerView.setAdapter(noticeAdapter);
                noticeAdapter.addHeaderView(getHeadView(easyRecyclerView));
                noticeAdapters.add(noticeAdapter);
                noticeAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                        ObjAnnouncementBean objInfo = (ObjAnnouncementBean) noticeAdapter.getItem(position);
                        if ("0".equals(objInfo.getStrState())) {
                            objInfo.setStrState("1");
                            noticeAdapter.notifyDataSetChanged();
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("headTitle", objInfo.getStrTopTitle());//objList.get(currentIndex).getStrName());
                        bundle.putString("strUrl", objInfo.getStrUrl());
                        bundle.putBoolean("isPersonInfo", true);
                        showActivity(activity, WebViewActivity.class, bundle);
                    }
                });
                break;
        }
        DividerDecoration itemDecoration = new DividerDecoration(getResources().getColor(R.color.main_bg), GIDensityUtil.dip2px(activity, dividerHeight), 0, 0);//?????? & ?????? & ????????? & ?????????
        itemDecoration.setDrawLastItem(true);//??????????????????item????????????,??????true.
        itemDecoration.setDrawHeaderFooter(false);//????????????????????????Header???Footer??????,??????false.
        easyRecyclerView.addItemDecoration(itemDecoration);
    }


    private View getHeadView(RecyclerView recyclerView) {
        View headerView = getLayoutInflater().inflate(R.layout.view_head_searchresult, recyclerView, false);
        TextView textView = headerView.findViewById(R.id.tv_text);
        return headerView;
    }

    /**
     * ????????????
     * type=0,???????????????1????????????
     */
    public void setSettingData(String name, int type) {
        if (serveList != null && serveList.size() > 0) {
            for (int i = 0; i < serveList.size(); i++) {
                String sv = serveList.get(i);
                if (sv.equals(name)) { // ???????????????????????????
                    serveList.remove(i);
                    break;
                }
            }
            if (type == 0) {
                serveList.add(0, name);
            }
        }
        if (serveList != null && serveList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < serveList.size(); i++) {
                sb.append(serveList.get(i) + ",");
            }
            GISharedPreUtil.setValue(activity, spName, sb.toString());
        } else {
            if (type == 0) {
                serveList = new ArrayList<>();
                serveList.add(name);
                GISharedPreUtil.setValue(activity, spName, name + ",");
            } else {
                GISharedPreUtil.setValue(activity, spName, "");
            }
        }
        if (type == 1) {
            if (serveList == null || serveList.size() == 0) {
                empty_ImageView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                empty_ImageView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            historyAdapter.setList(serveList);
            historyAdapter.notifyDataSetChanged();
        }
    }

    /**
     * ?????????
     */
    private void showConfirmDialog() {
        String title = "?????????????????????????????????";
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title("??????").content(title).btnText("??????", "??????").showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        GISharedPreUtil.setValue(activity, spName, "");
                        serveList = new ArrayList<>();
                        //zrcListViewUtil.emptyDataListView(zrcListView, "");
                        if (historyAdapter != null) {
                            historyAdapter.setList(new ArrayList<String>());
                            historyAdapter.notifyDataSetChanged();
                        }
                        empty_ImageView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
        );
    }

    /**
     * ??????????????????
     */
    private void dealReadRemind(String strId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().dealReadRemind(textParamMap), 1);
    }

    @Override
    public void setOnClick(Object object, View view, int type) {
        MessageRemindListBean.MessageRemindBean messageRemindBean = (MessageRemindListBean.MessageRemindBean) object;
        dealReadRemind(messageRemindBean.getStrId());
        Bundle bundle = new Bundle();
        type = Integer.parseInt(messageRemindBean.getStrAffirType());
        if (view.getId() == R.id.ll_state) {
            switch (type) {//01 ?????? 02 ?????? 03 ?????? 04 ???????????? 05 ?????? 06 ???????????? ?????????Inter.parseInt??????
                case 1:
                    String strAttend = "";
                    bundle.putBoolean("isFromRemind", true);
                    String strButtonJSFunction = messageRemindBean.getStrButtonJSFunction();
                    if ("feedBackHandler".equals(strButtonJSFunction)) {
                        bundle.putString("strId", messageRemindBean.getStrAffirId());
                        showActivity(activity, Proposal_FeedbackActivity.class, bundle, 0);
                    } else if ("showCaseMotionAlly".equals(strButtonJSFunction)) {
                        strAttend = "2";
                        bundle.putString("strId", messageRemindBean.getStrAffirId());
                        bundle.putString("strAttend", strAttend);
                        showActivity(activity, Proposal_FeedbackActivity.class, bundle, 0);
                    }
                    break;
                case 2:
                case 3:
                    bundle.putBoolean("isFromRemind", true);
                    if (2 == type) {
                        bundle.putString("headTitle", "????????????");
                        bundle.putInt("sign", DefineUtil.wdhy);
                    } else {
                        bundle.putString("headTitle", "????????????");
                        bundle.putInt("sign", DefineUtil.wdhd);
                    }
                    bundle.putString("strName", messageRemindBean.getStrName());
                    bundle.putString("strStartDate", messageRemindBean.getStrStartDate());
                    bundle.putString("strEndDate", messageRemindBean.getStrEndDate());
                    bundle.putString("strPlace", messageRemindBean.getStrPlace());
                    bundle.putString("strId", messageRemindBean.getStrAffirId());
                    boolean isShowSign = false;//??????????????????
                    if ("showMeet".equals(messageRemindBean.getStrButtonJSFunction()) || "showEvent".equals(messageRemindBean.getStrButtonJSFunction())) {
                        isShowSign = true;
                    }
                    if (isShowSign)
                        showActivity(activity, MeetAct_SignUpActivity.class, bundle, 2);
                    break;
                default:
                    break;

            }
        }

    }
}
