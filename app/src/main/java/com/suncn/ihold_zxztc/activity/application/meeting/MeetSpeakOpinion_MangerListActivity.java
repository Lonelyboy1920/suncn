package com.suncn.ihold_zxztc.activity.application.meeting;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.MangerMeetSpeakListAdapter;
import com.suncn.ihold_zxztc.adapter.ViewPagerAdapter;
import com.suncn.ihold_zxztc.bean.MeetingSpeakListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.suncn.ihold_zxztc.view.MyTabLayout;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import skin.support.content.res.SkinCompatResources;

/**
 * 社情民意、会议发言（机关）
 */
public class MeetSpeakOpinion_MangerListActivity extends BaseActivity {
    @BindView(id = R.id.tv_session)
    private TextView session_TextView;//界次TextView
    @BindView(id = R.id.ll_session, click = true)
    private LinearLayout session_LinearLayout;//界次LinearLayout
    @BindView(id = R.id.tabLayout)
    private MyTabLayout tabLayout;//顶部MyTabLayout
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    @BindView(id = R.id.ll_top_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_top_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel_1, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.tv_search, click = true)
    private TextView search_TextView;
    private List<MangerMeetSpeakListAdapter> adapters;
    private ArrayList<Boolean> isNotLoads;
    private ArrayList<SessionListBean.YearInfo> yearList; // 年份集合
    private ArrayList<View> myViews;
    private int[] intCurPages;
    private int currentIndex = 0;
    private List<String> menuList;
    private int intKind;
    private String strKeyValue = "";
    private String headTitle;
    private ArrayList<String> yearArray = new ArrayList<>();

    private String strYear;//年份
    private SpinerPopWindow<String> mSpinerPopWindow;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                case 1:
                    getMeetingList(true);
                    break;
                case 2:
                    getMeetingList(false);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_global_tab_list);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            intKind = bundle.getInt("intKind", 0);
            yearList = (ArrayList<SessionListBean.YearInfo>) bundle.getSerializable("yearList");
            session_TextView.setText(yearList.get(0).getStrYear());
            strYear = yearList.get(0).getStrYear();
//            yearArray = GIDateUtils.getYearStringArray(yearList.size());
            for (SessionListBean.YearInfo yearInfo : yearList) {
                yearArray.add(yearInfo.getStrYear());
            }
            mSpinerPopWindow = new SpinerPopWindow<String>(activity, yearArray, itemClickListener);
            headTitle = bundle.getString("headTitle");
            setHeadTitle(headTitle);
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        menuList = new ArrayList<>();
        if (GISharedPreUtil.getInt(activity, "intUserRole") == 1) {
            if (intKind == DefineUtil.sqmy) {
                menuList.add("1|" + getString(R.string.string_pending_review));
            }
            if (intKind == DefineUtil.hyfy) {
                menuList.add("1|" + getString(R.string.string_pending_review));
            }
            menuList.add("2|" + getString(R.string.string_pending_adopted));
            menuList.add("3|" + getString(R.string.string_pending_not_adopted));
        } else {
            goto_Button.setVisibility(View.VISIBLE);
            goto_Button.setText("撰写");
            goto_Button.refreshFontType(activity, "4");
            menuList.add("1|个人提交");
            menuList.add("2|联名提交");
            menuList.add("3|公开刊物");
        }
        initTab();
    }

    /**
     * 获取会议发言列表
     */
    private void getMeetingList(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", menuList.get(currentIndex).split("\\|")[0]);
        textParamMap.put("strYear", strYear);
        textParamMap.put("strKeyValue", strKeyValue);
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        if (intKind == DefineUtil.hyfy) {//会议发言（1待审发言，2 采用 3 不采用）
            doRequestNormal(ApiManager.getInstance().getMangerMeetSpeakList(textParamMap), 0);
        } else {//社情民意（1 待审核 2 采用 3 不采用）
            if (GISharedPreUtil.getInt(activity, "intUserRole") == 1) {
                doRequestNormal(ApiManager.getInstance().getMangerOpinionsList(textParamMap), 0);
            } else {
                doRequestNormal(ApiManager.getInstance().MyInfoListByTypeServlet(textParamMap), 0);
            }

        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_session:
                mSpinerPopWindow.setWidth(session_LinearLayout.getWidth() * 2);
                mSpinerPopWindow.showAsDropDown(session_TextView);
                break;
            case R.id.tv_search:
                llSearch.setVisibility(View.VISIBLE);
                search_TextView.setVisibility(View.GONE);
                tvCancel.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel_1:
                llSearch.setVisibility(View.INVISIBLE);
                search_TextView.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.GONE);
                if (GIStringUtil.isNotBlank(etSearch.getText().toString())) {
                    etSearch.setText("");
                    intCurPages[currentIndex] = 1;
                    getMeetingList(true);
                }
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        etSearch.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getMeetingList(false);
            }
        });
    }

    /**
     * popupwindow显示的ListView的item点击事件(年份)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            session_TextView.setText(yearArray.get(position));
            strYear = yearArray.get(position);
            for (int i = 0; i < menuList.size(); i++) {
                isNotLoads.set(i, false);
                intCurPages[i] = 1;
            }
            getMeetingList(true);
        }
    };

    /**
     * 初始化tab栏
     */
    private void initTab() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        myViews = new ArrayList<View>();
        adapters = new ArrayList<>();
        isNotLoads = new ArrayList<>();
        intCurPages = new int[menuList.size()];
        for (int i = 0; i < menuList.size(); i++) {
            intCurPages[i] = 1;
            isNotLoads.add(false);
            initAddView(i);
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(myViews, menuList);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        getMeetingList(true);
        //设置tab的item布局样式
        for (int i = 0; i < menuList.size(); i++) {
            MyTabLayout.Tab tab = tabLayout.getTabAt(i);
            View inflate = View.inflate(this, R.layout.view_tab_meet, null);
            GITextView textView = inflate.findViewById(R.id.tv_tab);
            textView.setText(menuList.get(i).split("\\|")[1]);
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
            if (!isNotLoads.get(currentIndex) || GIStringUtil.isNotBlank(strKeyValue)) {
                getMeetingList(true);
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
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_meetingact_add_view, null);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        final SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        MangerMeetSpeakListAdapter adapter = new MangerMeetSpeakListAdapter(this, currentIndex);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                intCurPages[position] = 1;
                getMeetingList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                intCurPages[position]++;
                getMeetingList(false);
            }
        });
        adapters.add(adapter);
        myViews.add(view);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                MeetingSpeakListBean.MeetingSpeakBean meetingSpeakBean = (MeetingSpeakListBean.MeetingSpeakBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("headTitle", headTitle);
                bundle.putString("strId", meetingSpeakBean.getStrId());
                bundle.putString("strType", meetingSpeakBean.getStrCheckState());
                bundle.putString("strUrl", meetingSpeakBean.getStrUrl());
                bundle.putInt("intKind", intKind);
                if (intKind == DefineUtil.sqmy) {
                    bundle.putBoolean("isSocialOpinions", true);
                }

                if (ProjectNameUtil.isCZSZX(activity)) {
                    if (intKind == DefineUtil.hyfy) {
                        bundle.putString("intToMsz", meetingSpeakBean.getIntToMsz());
                        bundle.putString("intToZx", meetingSpeakBean.getIntToZx());
                        bundle.putString("strTypeHYFY", meetingSpeakBean.getStrType());
                    } else if (intKind == DefineUtil.sqmy) {
                        bundle.putString("intToZx", meetingSpeakBean.getIntToZx());
                        bundle.putString("intPass", meetingSpeakBean.getIntPass());
                    }
                    bundle.putString("strCheckType", meetingSpeakBean.getStrCheckType());
                }
                showActivity(activity, WebViewActivity.class, bundle, 0);
            }
        });

    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    MangerMeetSpeakListAdapter adapter = adapters.get(currentIndex);
                    SmartRefreshLayout refreshLayout = myViews.get(currentIndex).findViewById(R.id.refreshLayout);
                    MeetingSpeakListBean meetingListBean = (MeetingSpeakListBean) data;
                    List<MeetingSpeakListBean.MeetingSpeakBean> proposalBeans = meetingListBean.getObjList();
                    Utils.setLoadMoreViewState(meetingListBean.getIntAllCount(), intCurPages[currentIndex] * DefineUtil.GLOBAL_PAGESIZE
                            , strKeyValue, refreshLayout, adapter);
                    if (intCurPages[currentIndex] == 1) {
                        adapter.setList(proposalBeans);
                    } else {
                        adapter.addData(proposalBeans);
                    }
                    if (isNotLoads != null && isNotLoads.size() > 0) {
                        isNotLoads.set(currentIndex, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }
}
