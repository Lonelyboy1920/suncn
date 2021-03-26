package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.ProposalTabViewPagerAdapter;
import com.suncn.ihold_zxztc.adapter.Proposal_Main_RVAdapter;
import com.suncn.ihold_zxztc.bean.ProposalListBean;
import com.suncn.ihold_zxztc.bean.ProposalTabListBean;
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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * 政协提案列表
 */
public class Proposal_MainActivity extends BaseActivity {
    @BindView(id = R.id.tabLayout)
    private MyTabLayout tabLayout;//顶部MyTabLayout
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    @BindView(id = R.id.addProposal, click = true)
    private LinearLayout addProposal;
    @BindView(id = R.id.ll_top_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_top_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel_1, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.tv_search, click = true)
    private TextView search_TextView;
    @BindView(id = R.id.tv_session)
    private TextView session_TextView;//界次TextView
    @BindView(id = R.id.ll_session, click = true)
    private LinearLayout session_LinearLayout;//界次LinearLayout


    private String strType = "1";
    private List<Proposal_Main_RVAdapter> adapters;
    private ArrayList<Boolean> isNotLoads;
    private ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    private List<ProposalTabListBean.ProposalTab> objList;
    private ArrayList<View> myViews;
    private int[] intCurPages;
    private int currentIndex = 0;

    private boolean isUpdateItem = false;
    private boolean isAddItem = false;
    private int intClickPostion;
    private SpinerPopWindow<String> mSpinerPopWindow;
    private ArrayList<String> sessionArray = new ArrayList<>();
    private String mSessionId;
    private String strKeyValue = "";
    private ArrayList<ProposalListBean.ProposalBean> objList1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0: // 进入详情操作后刷新数据
                    isUpdateItem = true;
                    intClickPostion = 0;
                    getProposalList(false);
                    break;
                case 1://提交
                    isAddItem = true;
                    if (currentIndex != 0) { // 发布完成后判断当前是否选中第一个tab
                        isNotLoads.set(0, false); // 不是则重置第一个tab，让其数据刷新
                    } else {
                        getProposalList(false); // 是则直接刷新数据
                    }
                    break;
            }
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_main);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (isNotLoads != null) {
//            for (int i = 0; i < isNotLoads.size(); i++) {
//                isNotLoads.set(i, false);
//            }
//        }
        if (isUpdateItem) {
            getProposalList(false);
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
        goto_Button.setText(getString(R.string.font_search));
        goto_Button.setVisibility(View.GONE);
//        if (AppConfigUtil.isExistUserByCppccSession(activity)) {
//            two_Button.setText("\ue662");
//            two_Button.setVisibility(View.VISIBLE);
//        } else {
//            two_Button.setVisibility(View.GONE);
//        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sessionBeans = (ArrayList<SessionListBean.SessionBean>) bundle.getSerializable("sessionBeans");
            setHeadTitle(bundle.getString("headTitle"));
        }
        getProposalTabList();
        getProposalSessionListServlet();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.addProposal:
//                showActivity(activity, Proposal_AddActivity.class, 1);
                showActivity(activity, NewProposalAddActivity.class, 1);
                break;
            case R.id.btn_goto:
                Bundle bundle = new Bundle();
                bundle.putSerializable("sessionBeans", sessionBeans);
                bundle.putSerializable("headTitle", getString(R.string.string_public_proposal_query));
                showActivity(activity, Proposal_PublicSearchActivity.class, bundle);
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
                    getProposalList(true);
                }
                break;
            case R.id.ll_session:
                mSpinerPopWindow.setWidth((int) getResources().getDimension(R.dimen.sp_100_dp));
                mSpinerPopWindow.showAsDropDown(session_TextView);
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
                getProposalList(false);
            }
        });
    }

    /**
     * 届次
     */
    private void getProposalSessionListServlet() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getProposalSessionListServlet(textParamMap), 2);
    }


    /**
     * 初始化tab栏
     */
    private void initTab() {
        myViews = new ArrayList<>();
        adapters = new ArrayList<>();
        isNotLoads = new ArrayList<>();
        intCurPages = new int[objList.size()];
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < objList.size(); i++) {
            intCurPages[i] = 1;
            isNotLoads.add(false);
            initAddView(i);
            titles.add(objList.get(i).getStrName());
        }
        ProposalTabViewPagerAdapter viewPagerAdapter = new ProposalTabViewPagerAdapter(myViews, objList);
        viewPager.setAdapter(viewPagerAdapter);
        strType = objList.get(currentIndex).getStrType();
        tabLayout.setupWithViewPager(viewPager);
        getProposalList(false);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                strType = objList.get(currentIndex).getStrType();
                getProposalList(false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 添加视图
     */
    private void initAddView(final int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_viewpager_add_view, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        Utils.initEasyRecyclerView(activity, recyclerView, true, false, R.color.line_bg_white, 0.5f, 10);
        final Proposal_Main_RVAdapter adapter = new Proposal_Main_RVAdapter(this, position);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                intCurPages[position] = 1;
                getProposalList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                intCurPages[currentIndex] = intCurPages[currentIndex] + 1;
                getProposalList(false);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                intClickPostion = position;
                ProposalListBean.ProposalBean proposalBean = (ProposalListBean.ProposalBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strId", proposalBean.getStrId());
                if (proposalBean.getIntCanEdit() == 1) {
                    showActivity(activity, NewProposalAddActivity.class, bundle, 0);
                } else {
                    bundle.putString("headTitle", "提案详情");
                    bundle.putString("strCaseId", proposalBean.getStrCaseMotionId());
                    bundle.putString("strId", proposalBean.getStrId());
                    bundle.putString("strAttend", proposalBean.getIntAttend() + "");
                    bundle.putString("strFlowState", proposalBean.getStrFlowState());
                    if (ProjectNameUtil.isGYSZX(activity) || ProjectNameUtil.isGZSZX(activity)) {
                        bundle.putString("strFeedback", proposalBean.getStrFeedback());
                    }
                    if ("1".equals(strType) && "0".equals(proposalBean.getIntState())) {
                        bundle.putBoolean("isCanEdit", true);
                    }
                    String strState = GIStringUtil.isNotBlank(proposalBean.getStrFlowState()) ?
                            proposalBean.getStrFlowState() : GIStringUtil.isNotBlank(proposalBean.getStrAttendName()) ?
                            proposalBean.getStrAttendName() : "";
                    if (getString(R.string.string_filed).equals(strState)
                            || getString(R.string.string_to_be_received).equals(strState)
                            || getString(R.string.string_not_received).equals(strState)
                            || getString(R.string.string_temporary_storage).equals(strState)
                            || getString(R.string.string_not_filed).equals(strState)
                            || getString(R.string.string_under_review).equals(strState)) { //显示基本信息、提案内容
                        bundle.putString("proposalDetailShowTab", "1");
                    } else if (getString(R.string.string_in_process).equals(strState)
                            || getString(R.string.string_wait_feedback).equals(strState)) { //办理中、待反馈的，显示基本信息、提案内容、办理情况
                        bundle.putString("proposalDetailShowTab", "2");
                    } else if (getString(R.string.string_feedbacked).equals(strState)
                            || getString(R.string.string_deal_finished).equals(strState)) { //已反馈的，显示基本信息、提案内容、办理情况、反馈情况
                        bundle.putString("proposalDetailShowTab", "3");
                    }
                    showActivity(activity, Proposal_DetailActivity.class, bundle, 0);
                }
            }
        });
        myViews.add(view);
        adapters.add(adapter);
    }

    /**
     * 提案分类
     */
    private void getProposalTabList() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getProposalTabServlet(textParamMap), 1);
    }

    /**
     * 获取提案列表
     */
    private void getProposalList(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", strType);
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        textParamMap.put("strKeyValue", strKeyValue);
        if (GIStringUtil.isNotBlank(mSessionId)) {
            textParamMap.put("strSessionId", mSessionId);
        }
        doRequestNormal(ApiManager.getInstance().getMyProposalListByTypeServlet(textParamMap), 0);
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        try {
            switch (sign) {
                case 1:
                    ProposalTabListBean result = (ProposalTabListBean) data;
                    objList = result.getObjList();
                    if (objList != null && objList.size() > 0) {
                        initTab();
                    } else {
                        prgDialog.closePrgDialog();
                    }
                    break;
                case 0:
                    prgDialog.closePrgDialog();
                    Proposal_Main_RVAdapter adapter = adapters.get(currentIndex);
                    ProposalListBean proposalListBean = (ProposalListBean) data;
                    View view = myViews.get(currentIndex);
                    SmartRefreshLayout smartRefreshLayout = view.findViewById(R.id.refreshLayout);
                    Utils.setLoadMoreViewState(proposalListBean.getIntAllCount(), intCurPages[currentIndex] * DefineUtil.GLOBAL_PAGESIZE
                            , strKeyValue, smartRefreshLayout, adapter);
                    objList1 = proposalListBean.getObjList();
                    if (intCurPages[currentIndex] == 1) {
                        adapter.setList(proposalListBean.getObjList());
                    } else {
                        adapter.addData(proposalListBean.getObjList());
                    }
                    if (isNotLoads != null && isNotLoads.size() > 0) {
                        isNotLoads.set(currentIndex, true);
                    }
                    break;

                case 2:
                    SessionListBean sessionListBean = (SessionListBean) data;
                    sessionBeans = sessionListBean.getObjList();
                    if (null != sessionBeans && sessionBeans.size() > 0) {
                        mSessionId = sessionBeans.get(0).getStrSessionId();
                        session_TextView.setText(sessionBeans.get(0).getStrSessionName());
                        for (SessionListBean.SessionBean yearInfo : sessionBeans) {
                            sessionArray.add(yearInfo.getStrSessionName());
                        }
                        mSpinerPopWindow = new SpinerPopWindow<String>(activity, sessionArray, itemClickListener);
                    } else {
                        showToast(getString(R.string.string_have_no_data));
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
     * popupwindow显示的ListView的item点击事件(年份)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            session_TextView.setText(sessionArray.get(position));
            mSessionId = sessionBeans.get(position).getStrSessionId();
            getProposalList(true);
        }
    };
}
