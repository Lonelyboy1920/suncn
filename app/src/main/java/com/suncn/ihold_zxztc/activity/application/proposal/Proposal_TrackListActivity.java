package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GITextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Proposal_Track_RVAdapter;
import com.suncn.ihold_zxztc.adapter.ViewPagerAdapter;
import com.suncn.ihold_zxztc.bean.ProposalTrackListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyTabLayout;
import com.suncn.ihold_zxztc.view.SpinerSessionPopWindow;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import skin.support.content.res.SkinCompatResources;

/**
 * 提案追踪
 * 提案办理
 */
public class Proposal_TrackListActivity extends BaseActivity {
    @BindView(id = R.id.ll_session, click = true)
    private LinearLayout session_LinearLayout;//界次LinearLayout
    @BindView(id = R.id.tv_session)
    private TextView session_TextView;//界次TextView
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
    private String strType = "1";//待签收1，办理中2，已办结3
    private List<Proposal_Track_RVAdapter> adapters;
    private ArrayList<Boolean> isNotLoads;
    public ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    private ArrayList<View> myViews;
    private int[] intCurPages;
    private ViewPagerAdapter viewPagerAdapter;
    private int currentIndex = 0;
    private List<String> objList;
    private String strKeyValue = "";
    private String strMenuType = "";//25提案办理
    private String strSessionId = "";
    private SpinerSessionPopWindow<String> mSpinerPopWindow;
    private int intClickPostion;
    private boolean isUpdateItem = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            isUpdateItem = true;
            switch (requestCode) {
                case 0:
                case 1:
                    getMeetingList(true);
                    break;
                case 2:
                    getMeetingList(false);
                    break;
                case 3://提案办理
                    adapters.get(currentIndex).remove(intClickPostion);
                    if (0 == adapters.get(currentIndex).getItemCount()) {
                        adapters.get(currentIndex).setList(new ArrayList<>());
                        View view = myViews.get(currentIndex);
                        RecyclerView easyRecyclerView = view.findViewById(R.id.recyclerView);
                        adapters.get(currentIndex).setEmptyView(R.layout.view_erv_empty); // 设置无数据时的view
                    }
                    isNotLoads.set(currentIndex + 1, false);
                    break;
                default:
                    break;
            }
        } else if (resultCode == -2) {//延期申请返回
            isUpdateItem = true;
            getMeetingList(false);
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
            strMenuType = bundle.getString("strMenuType");
            sessionBeans = (ArrayList<SessionListBean.SessionBean>) bundle.getSerializable("sessionBeans");
            session_TextView.setText(sessionBeans.get(0).getStrSessionName());
            strSessionId = sessionBeans.get(0).getStrSessionId();
            mSpinerPopWindow = new SpinerSessionPopWindow<>(activity, sessionBeans, itemClickListener);

            setHeadTitle(bundle.getString("headTitle"));
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        objList = new ArrayList<>();
        if ("25".equals(strMenuType)) {
            objList.add(getString(R.string.string_to_be_signed));
        } else {
            objList.add(getString(R.string.string_unsigned));
        }
        objList.add(getString(R.string.string_in_process));
        objList.add(getString(R.string.string_done));
        myViews = new ArrayList<View>();
        adapters = new ArrayList<>();
        isNotLoads = new ArrayList<>();
        initTab();
    }

    /**
     * 获取提案列表
     */
    private void getMeetingList(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        textParamMap.put("strSessionId", strSessionId);
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        textParamMap.put("strType", strType);
        textParamMap.put("strKeyValue", strKeyValue);
        if ("25".equals(strMenuType)) {
            doRequestNormal(ApiManager.getInstance().getProposaldeallist(textParamMap), 0);
        } else {
            doRequestNormal(ApiManager.getInstance().getProposaltracklist(textParamMap), 0);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_session:
                mSpinerPopWindow.setWidth((int) getResources().getDimension(R.dimen.sp_100_dp));
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

    /**
     * popupwindow显示的ListView的item点击事件(年份)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            session_TextView.setText(sessionBeans.get(position).getStrSessionName());
            strSessionId = sessionBeans.get(position).getStrSessionId();
            for (int i = 0; i < objList.size(); i++) {
                isNotLoads.set(i, false);
                intCurPages[i] = 1;
            }
            getMeetingList(true);
        }
    };

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
     * 初始化tab栏
     */
    private void initTab() {
        //currentIndex = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(metrics.widthPixels / objList.size(), ViewGroup.LayoutParams.MATCH_PARENT);
        intCurPages = new int[objList.size()];
        for (int i = 0; i < objList.size(); i++) {
            intCurPages[i] = 1;
            isNotLoads.add(false);
            initAddView(i);
        }

        viewPagerAdapter = new ViewPagerAdapter(myViews, objList);
        viewPager.setAdapter(viewPagerAdapter);
        //viewPager.setCurrentItem(currentIndex);
        //viewPager.addOnPageChangeListener(pageListener);
        tabLayout.setupWithViewPager(viewPager);
        getMeetingList(true);

        //设置tab的item布局样式
        for (int i = 0; i < objList.size(); i++) {
            MyTabLayout.Tab tab = tabLayout.getTabAt(i);
            View inflate = View.inflate(this, R.layout.view_tab_meet, null);
            GITextView textView = inflate.findViewById(R.id.tv_tab);
            textView.setText(objList.get(i));
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
            if (currentIndex == 0) {
                strType = "1";
            } else if (currentIndex == 1) {
                strType = "2";
            } else {
                strType = "3";
            }
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
        //textView.setIncludeFontPadding(true);
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
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        //View viewHead = LayoutInflater.from(activity).inflate(R.layout.view_head_banner, null);
        Proposal_Track_RVAdapter adapter = new Proposal_Track_RVAdapter(this, currentIndex);
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
                intCurPages[currentIndex] = intCurPages[currentIndex] + 1;
                getMeetingList(false);
            }
        });
        adapters.add(adapter);
        myViews.add(view);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                intClickPostion = position;
                ProposalTrackListBean.ProposalTrack proposalBean = (ProposalTrackListBean.ProposalTrack) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strId", proposalBean.getStrId());
                bundle.putString("headTitle", getString(R.string.string_proposal_detail));
                bundle.putString("strCaseId", proposalBean.getStrCaseMotionId());
                bundle.putBoolean("isWorker", true);

                //strState
                //0(且intRecUnitType为1时，为提醒交办，intRecUnitType为2时，为提醒签收),
                //1（且strDelayState为1，为延期审核,strDelayState 0为催办）
                if ("25".equals(strMenuType)) {
                    bundle.putString("strHandlerTypeName", proposalBean.getStrHandlerTypeName());
                    bundle.putString("strHandlerLimitDate", proposalBean.getStrHandlerLimitDate());
                    if ("0".equals(proposalBean.getStrState())) {
                        bundle.putString("gotoValue", getString(R.string.string_sign_for));
                    } else if ("1".equals(proposalBean.getStrState())) {
                        if ("1".equals(proposalBean.getStrDelayState())) {
                            bundle.putString("gotoValue", getString(R.string.string_handle));
                        } else if ("0".equals(proposalBean.getStrDelayState())) {
                            bundle.putString("gotoValue", getString(R.string.string_operation));
                        }
                    }
                } else {
                    if ("0".equals(proposalBean.getStrState())) {
                        if (1 == proposalBean.getIntRecUnitType()) {
                            bundle.putString("gotoValue", getString(R.string.string_remind_to_handle));
                        } else if (2 == proposalBean.getIntRecUnitType()) {
                            bundle.putString("gotoValue", getString(R.string.string_reminder_sign_in));
                        }
                    } else if ("1".equals(proposalBean.getStrState())) {
                        if ("1".equals(proposalBean.getStrDelayState())) {
                            bundle.putString("gotoValue", getString(R.string.string_delayed_audit));
                        } else if ("0".equals(proposalBean.getStrDelayState())) {
                            bundle.putString("gotoValue", getString(R.string.string_urge));
                        }
                    }
                    if ("1".equals(proposalBean.getStrHandlerState())) {
                        bundle.putString("gotoValue", getString(R.string.string_urging_members_to_give_feedback));
                    }
                    if ("0".equals(proposalBean.getStrHasSecondHandler()) && "2".equals(proposalBean.getStrHandlerState()) && 1 == GISharedPreUtil.getInt(activity, "intUserRole")) {
                        bundle.putString("gotoValue", getString(R.string.string_handle_again));
                    }
                }
                //bundle.putString("gotoValue", "操作");
                if ("25".equals(strMenuType)) {
                    showActivity(activity, Proposal_DetailActivity.class, bundle, 3);
                } else {
                    showActivity(activity, Proposal_DetailActivity.class, bundle, 0);
                }
            }
        });

    }


    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    Proposal_Track_RVAdapter adapter = adapters.get(currentIndex);
                    SmartRefreshLayout refreshLayout = myViews.get(currentIndex).findViewById(R.id.refreshLayout);
                    ProposalTrackListBean meetingListBean = (ProposalTrackListBean) data;
                    List<ProposalTrackListBean.ProposalTrack> proposalBeans = meetingListBean.getObjList();
                    Utils.setLoadMoreViewState(meetingListBean.getIntAllCount(), intCurPages[currentIndex] * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, adapter);
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
