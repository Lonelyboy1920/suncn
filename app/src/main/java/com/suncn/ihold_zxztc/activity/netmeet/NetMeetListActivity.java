package com.suncn.ihold_zxztc.activity.netmeet;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GITextView;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.BaseMeet_Main_RVAdapter;
import com.suncn.ihold_zxztc.adapter.TabViewPagerAdapter;
import com.suncn.ihold_zxztc.bean.BaseMeetBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.suncn.ihold_zxztc.view.MyTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class NetMeetListActivity extends BaseActivity {
    @BindView(id = R.id.tabLayout)
    private MyTabLayout tabLayout;//顶部MyTabLayout
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    @BindView(id = R.id.et_top_search)//标题搜索框
    private ClearEditText etSearchTop;
    private String strType = "1";
    private List<BaseMeet_Main_RVAdapter> adapters;
    private ArrayList<Boolean> isNotLoads;
    private ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    private ArrayList<View> myViews;
    private int[] intCurPages;
    private int currentIndex = 0;

    private boolean isUpdateItem = false;
    private boolean isAddItem = false;
    private int intClickPostion;
    private ArrayList<String> titles = new ArrayList<>();
    private String strBaseConsultUserType;
    private String strKeyValue = "";
    private String strIsMeetDateOver = "1";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0: // 进入详情操作后刷新数据
                    isUpdateItem = true;
                    getList(false);
                    break;
                case 1://提交
                    isAddItem = true;
                    if (currentIndex != 0) { // 发布完成后判断当前是否选中第一个tab
                        isNotLoads.set(0, false); // 不是则重置第一个tab，让其数据刷新
                    } else {
                        getList(false); // 是则直接刷新数据
                    }
                    break;
            }
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_meetact_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNotLoads != null) {
            for (int i = 0; i < isNotLoads.size(); i++) {
                isNotLoads.set(i, false);
            }
        }
        if (DefineUtil.isNeedRefresh) {
            getList(false);
            DefineUtil.isNeedRefresh = false;
        }
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        strBaseConsultUserType = GISharedPreUtil.getString(activity, "strBaseConsultUserType");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sessionBeans = (ArrayList<SessionListBean.SessionBean>) bundle.getSerializable("sessionBeans");
            setHeadTitle(bundle.getString("headTitle"));
        }
        findViewById(R.id.ll_session).setVisibility(View.GONE);
        findViewById(R.id.ll_top_search).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_search).setVisibility(View.GONE);
        setHeadTitle("我的会议");
        goto_Button.setText("发起会议");
        goto_Button.setTextSize(16);
        goto_Button.setVisibility(View.VISIBLE);
        titles.add("未开始");
        titles.add("进行中");
        titles.add("已结束");
        initTab();

    }

    @Override
    public void setListener() {
        super.setListener();
        etSearchTop.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    strKeyValue = etSearchTop.getText().toString();
                    if (!GIStringUtil.isNotBlank(strKeyValue)) {
                        GIUtil.closeSoftInput(activity);
                    }
                    getList(false);
                    return true;
                }
                return false;
            }
        });

        etSearchTop.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getList(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                Bundle bundle = new Bundle();
                showActivity(activity, MeetAddActivity.class, bundle);
                break;
        }
    }


    /**
     * 初始化tab栏
     */
    private void initTab() {
        myViews = new ArrayList<>();
        adapters = new ArrayList<>();
        isNotLoads = new ArrayList<>();
        intCurPages = new int[titles.size()];
        for (int i = 0; i < titles.size(); i++) {
            intCurPages[i] = 1;
            isNotLoads.add(false);
            initAddView(i);
        }
        TabViewPagerAdapter viewPagerAdapter = new TabViewPagerAdapter(myViews, titles);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        strType = titles.get(0);
        getList(false);
        //设置tab的item布局样式
        for (int i = 0; i < titles.size(); i++) {
            MyTabLayout.Tab tab = tabLayout.getTabAt(i);
            View inflate = View.inflate(this, R.layout.view_tab_meet, null);
            GITextView textView = inflate.findViewById(R.id.tv_tab);
            textView.setText(titles.get(i));
            textView.setTextColor(getResources().getColor(R.color.font_title));
            //设置第一条tab的选中样式
            if (i == 0) {
                setSelectedTabTextView(textView);
            }
            tab.setCustomView(inflate);
        }
        //tabLayout.setTitle(titles);
        /* 必须执行以下操作，否则最后一个tab默认样式会有问题 */
        tabLayout.getTabAt(tabLayout.getTabCount() - 1).select();
        tabLayout.getTabAt(0).select();
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
        viewPager.setCurrentItem(currentIndex);
    }

    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
            View view = tab.getCustomView();
            setSelectedTabTextView(view.findViewById(R.id.tv_tab));
            currentIndex = tab.getPosition();
            strIsMeetDateOver = tab.getPosition() + 1 + "";
            if (!isNotLoads.get(currentIndex) || GIStringUtil.isNotBlank(strKeyValue)) {
                intCurPages[currentIndex] = 1;
                getList(true);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            ((TextView) view.findViewById(R.id.tv_tab)).setTextColor(getResources().getColor(R.color.font_title));
            Drawable drawable = getResources().getDrawable(R.drawable.shape_tab_bline_select);/// 这一步必须要做,否则不会显示.
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
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(R.color.view_head_bg));
        Drawable drawable = getResources().getDrawable(R.drawable.shape_tab_bline_select);/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, GIDensityUtil.dip2px(activity, 5), drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null, null, null, drawable);
    }

    /**
     * 添加视图
     */
    private void initAddView(final int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_viewpager_add_view, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        Utils.initEasyRecyclerView(activity, recyclerView, true, false, R.color.line_bg_white, 0.5f, 10);
        final BaseMeet_Main_RVAdapter adapter = new BaseMeet_Main_RVAdapter(this, titles.get(position));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                intCurPages[position]++;
                getList(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                intCurPages[position] = 1;
                getList(false);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("strId", ((BaseMeetBean.BaseMeet) adapter.getData().get(position)).getStrId());
                bundle.putBoolean("isCanJoin", !strIsMeetDateOver.equals("3"));
                showActivity(activity, NetMeetDetailActivity.class, bundle);
            }
        });
        myViews.add(view);
        adapters.add(adapter);
    }


    /**
     * 获取列表
     */
    private void getList(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        textParamMap.put("strKeyValue", strKeyValue);
        textParamMap.put("strState", strIsMeetDateOver);
        doRequestNormal(ApiManager.getInstance().MyStartMeetingListServlet(textParamMap), 0);
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        try {
            switch (sign) {
                case 0:
                    prgDialog.closePrgDialog();
                    BaseMeet_Main_RVAdapter adapter = adapters.get(currentIndex);
                    SmartRefreshLayout refreshLayout = myViews.get(currentIndex).findViewById(R.id.refreshLayout);
                    RecyclerView recyclerView = myViews.get(currentIndex).findViewById(R.id.recyclerView);
                    BaseMeetBean proposalListBean = (BaseMeetBean) data;
                    Utils.setLoadMoreViewState(proposalListBean.getIntAllCount(), intCurPages[currentIndex] * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, adapter);
                    if (intCurPages[currentIndex] == 1) {
                        adapter.setList(proposalListBean.getObjList());
                    } else {
                        adapter.addData(proposalListBean.getObjList());
                    }
                    if (isNotLoads != null && isNotLoads.size() > 0) {
                        isNotLoads.set(currentIndex, true);
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
}
