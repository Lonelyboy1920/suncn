package com.suncn.ihold_zxztc.activity.application.publicopinion;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.proposal.ProposalManagerSituationAnalysisFragment;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.circle.Circle_FocusListActivity;
import com.suncn.ihold_zxztc.activity.circle.Circle_MainFragment;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.Circle_MainHeader_RVAdapter;
import com.suncn.ihold_zxztc.adapter.PublicOpinionListAdapter;
import com.suncn.ihold_zxztc.adapter.PublicSearch_tabAdapter;
import com.suncn.ihold_zxztc.adapter.ViewPagerAdapter;
import com.suncn.ihold_zxztc.bean.MeetingSpeakListBean;
import com.suncn.ihold_zxztc.bean.TabBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import skin.support.content.res.SkinCompatResources;

public class PublicOpinion_ManageListActivity extends BaseActivity {
    @BindView(id = R.id.tabLayout)
    private MyTabLayout tabLayout;
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    private ArrayList<String> tabList;
    private ArrayList<View> myViews;
    private ArrayList<Boolean> isNotLoads = new ArrayList<>();
    private int[] intCurPages;
    private int currentIndex = 0;
    private List<PublicOpinionListAdapter> adapters;
    private ArrayList<TabBean> searchTabList;
    private String strType;
    private int childTabIndex = 0;
    private WebView webView;


    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_global_tab_white_list);
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
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getResources().getString(R.string.font_search));
        setHeadTitle(getResources().getString(R.string.string_social_opinion));
        tabList = new ArrayList<>();
        tabList.add(getResources().getString(R.string.string_search_msg));
        tabList.add(getResources().getString(R.string.string_kanwu));
        tabList.add(getResources().getString(R.string.string_tjfx));
        getTab();
    }

    /**
     * 初始化tab栏
     */
    private void initTab() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        myViews = new ArrayList<View>();
        adapters = new ArrayList<>();
        intCurPages = new int[tabList.size()];
        for (int i = 0; i < tabList.size(); i++) {
            intCurPages[i] = 1;
            isNotLoads.add(false);
            initAddView(i);
        }
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(myViews, tabList);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        getList();
        //设置tab的item布局样式
        for (int i = 0; i < tabList.size(); i++) {
            MyTabLayout.Tab tab = tabLayout.getTabAt(i);
            View inflate = View.inflate(this, R.layout.view_tab_meet, null);
            GITextView textView = inflate.findViewById(R.id.tv_tab);
            textView.setText(tabList.get(i));
            textView.setTextColor(getResources().getColor(R.color.font_title));

            //设置第一条tab的选中样式
            if (i == 0) {
                setSelectedTabTextView(textView);
            }
            tab.setCustomView(inflate);
        }
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
        viewPager.setCurrentItem(0);
    }

    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
            View view = tab.getCustomView();
            setSelectedTabTextView(view.findViewById(R.id.tv_tab));
            currentIndex = tab.getPosition();
            if (currentIndex == 2) {
                webView.reload();
                return;
            }
            if (!isNotLoads.get(currentIndex)) {
                if (currentIndex == 1) {
                    getListKw();
                } else {
                    getList();
                }
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            ((TextView) view.findViewById(R.id.tv_tab)).setTextColor(getResources().getColor(R.color.font_title));
            Drawable drawable = SkinCompatResources.getDrawable(activity, R.drawable.shape_tab_bline_select);/// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, GIDensityUtil.dip2px(activity, 5), drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ((TextView) view.findViewById(R.id.tv_tab)).setCompoundDrawables(null, null, null, null);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    /**
     * 设置选中的tab效果
     */
    private void setSelectedTabTextView(TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        Drawable drawable = SkinCompatResources.getDrawable(activity, R.drawable.shape_tab_bline_select);/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, GIDensityUtil.dip2px(activity, 5), drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null, null, null, drawable);
    }

    /**
     * 添加视图
     */
    private void initAddView(final int position) {
        View view;
        if (position == 2) {
            view = LayoutInflater.from(activity).inflate(R.layout.view_webview, null);
            webView = view.findViewById(R.id.webview);
            initWebView(webView);
            String curskin = GISharedPreUtil.getString(activity, "curskin", "1");
            String url = Utils.formatFileUrl(activity, "res/dist/index.html#/social?strSid=" + GISharedPreUtil.getString(activity, "strSid")) + "&setColor=" + curskin;
            webView.loadUrl(url);
            GILogUtil.e(url);
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.layout_meetingact_add_view, null);
            final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            final SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
            Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
            LinearLayoutManager layoutManagerTop = new LinearLayoutManager(activity);
            layoutManagerTop.setOrientation(LinearLayoutManager.HORIZONTAL);
            PublicOpinionListAdapter adapter = new PublicOpinionListAdapter(this, currentIndex);
            if (position == 0) {
                adapter.setHeaderView(getHeadView(recyclerView));
            }
            recyclerView.setAdapter(adapter);
            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    intCurPages[position] = 1;
                    if (currentIndex == 1) {
                        getListKw();
                    } else {
                        getList();
                    }
                }
            });
            refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    intCurPages[position]++;
                    if (currentIndex == 1) {
                        getListKw();
                    } else {
                        getList();
                    }

                }
            });
            adapters.add(adapter);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    MeetingSpeakListBean.MeetingSpeakBean meetingSpeakBean = (MeetingSpeakListBean.MeetingSpeakBean) adapter.getItem(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("headTitle", "社情民意");
                    bundle.putString("strId", meetingSpeakBean.getStrId());
                    bundle.putString("strType", meetingSpeakBean.getStrCheckState());
                    bundle.putString("strUrl", meetingSpeakBean.getStrUrl());
                    bundle.putBoolean("isSocialOpinions", true);
                    showActivity(activity, WebViewActivity.class, bundle, 0);

                }
            });

        }
        myViews.add(view);
    }

    private void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setAllowUniversalAccessFromFileURLs(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setLongClickable(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    /**
     * 初始化头部布局
     */
    private View getHeadView(RecyclerView recyclerView) {
        View view = getLayoutInflater().inflate(R.layout.item_recycler, recyclerView, false);
        RecyclerView recyclerViewTop = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManagerTop = new LinearLayoutManager(activity);
        layoutManagerTop.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewTop.setLayoutManager(layoutManagerTop);
        PublicSearch_tabAdapter publicSearch_tabAdapter = new PublicSearch_tabAdapter(searchTabList, activity);
        recyclerViewTop.setAdapter(publicSearch_tabAdapter);
        strType = searchTabList.get(0).getStrType();
        publicSearch_tabAdapter.setStrSelectId(strType);
        publicSearch_tabAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                TabBean tabBean = (TabBean) adapter.getItem(position);
                publicSearch_tabAdapter.setStrSelectId(tabBean.getStrType());
                strType = tabBean.getStrType();
                intCurPages[currentIndex] = 1;
                getList();
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                showActivity(activity, PublicSearchActivity.class, getIntent().getExtras());
                break;
        }
    }

    private void getTab() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().InfoTypeServlet(textParamMap), 0);
    }

    private void getList() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", strType);
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        doRequestNormal(ApiManager.getInstance().InfoListByTypeServlet(textParamMap), 1);
    }

    private void getListKw() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "");
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        doRequestNormal(ApiManager.getInstance().InfoListByTypeServlet(textParamMap), 1);
    }

    private void doLogic(int what, Object object) {
        prgDialog.closePrgDialog();
        switch (what) {
            case 0:
                TabBean tabBean = (TabBean) object;
                searchTabList = tabBean.getObjList();
                initTab();
                break;
            case 1:
                PublicOpinionListAdapter adapter = adapters.get(currentIndex);
                SmartRefreshLayout refreshLayout = myViews.get(currentIndex).findViewById(R.id.refreshLayout);
                MeetingSpeakListBean meetingListBean = (MeetingSpeakListBean) object;
                List<MeetingSpeakListBean.MeetingSpeakBean> proposalBeans = meetingListBean.getObjList();
                refreshLayout.finishLoadMore();
                refreshLayout.finishRefresh();
                if (meetingListBean.getIntAllCount() > intCurPages[currentIndex] * DefineUtil.GLOBAL_PAGESIZE) {
                    refreshLayout.setNoMoreData(false);
                } else {
                    refreshLayout.setNoMoreData(true);
                }
                if (meetingListBean.getIntAllCount() == 0) {
                    refreshLayout.setEnableLoadMore(false);
                } else {
                    refreshLayout.setEnableLoadMore(true);
                }
                if (intCurPages[currentIndex] == 1 && meetingListBean.getIntAllCount() == 0) {
                    myViews.get(currentIndex).findViewById(R.id.ll_empty).setVisibility(View.VISIBLE);
                } else {
                    myViews.get(currentIndex).findViewById(R.id.ll_empty).setVisibility(View.GONE);
                }
                if (intCurPages[currentIndex] == 1) {
                    adapter.setList(proposalBeans);
                } else {
                    adapter.addData(proposalBeans);
                }
                if (isNotLoads != null && isNotLoads.size() > 0) {
                    isNotLoads.set(currentIndex, true);
                }
                break;
        }
    }
}
