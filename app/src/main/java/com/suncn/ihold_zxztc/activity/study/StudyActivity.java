package com.suncn.ihold_zxztc.activity.study;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.H5WebViewActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.NewProposalAddActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.ShowBigImgActivity;
import com.suncn.ihold_zxztc.adapter.ProposalTabViewPagerAdapter;
import com.suncn.ihold_zxztc.adapter.StudyListAdapter;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.bean.ProposalTabListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.bean.StudyBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import skin.support.design.widget.SkinMaterialTabLayout;

/**
 * 委员学习列表
 */
public class StudyActivity extends BaseActivity {
    @BindView(id = R.id.tabLayout)
    private SkinMaterialTabLayout tabLayout;//顶部MyTabLayout
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    @BindView(id = R.id.ll_title) //标题
    private LinearLayout llTitle;
    @BindView(id = R.id.ll_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.et_search)//标题搜索框
    private ClearEditText etSearch;
    private String strId = "";
    private List<StudyListAdapter> adapters;
    private ArrayList<Boolean> isNotLoads;
    private ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    private List<ProposalTabListBean.ProposalTab> objList;
    private ArrayList<View> myViews;
    private int[] intCurPages;
    private int currentIndex = 0;

    private boolean isUpdateItem = false;
    private boolean isAddItem = false;
    private int intClickPostion;
    private String strKeyValue = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0: // 进入详情操作后刷新数据
                    isUpdateItem = true;
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
        setContentView(R.layout.activity_study_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNotLoads != null) {
            for (int i = 0; i < isNotLoads.size(); i++) {
                isNotLoads.set(i, false);
            }
        }
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("知情明政");
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        goto_Button.setText(getString(R.string.font_search));
        goto_Button.setVisibility(View.VISIBLE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sessionBeans = (ArrayList<SessionListBean.SessionBean>) bundle.getSerializable("sessionBeans");
            setHeadTitle(bundle.getString("headTitle"));
        }
        getProposalTabList();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_two:
                showActivity(activity, NewProposalAddActivity.class, 1);
                break;
            case R.id.btn_goto:
                if (llTitle.getVisibility() == View.VISIBLE) {
                    llTitle.setVisibility(View.GONE);
                    llSearch.setVisibility(View.VISIBLE);
                    goto_Button.setVisibility(View.INVISIBLE);
                    tvCancel.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_cancel:
                llTitle.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                goto_Button.setVisibility(View.VISIBLE);
                GIUtil.closeSoftInput(activity);
                if (GIStringUtil.isNotBlank(etSearch.getText().toString())) {
                    etSearch.setText("");
                    getProposalList(false);
                }
        }
    }

//    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
//        @Override
//        public void onTabSelected(TabLayout.Tab tab) {
//            View view = tab.getCustomView();
//            setSelectedTabTextView(view.findViewById(R.id.tv_tab));
//            viewPager.setCurrentItem(tab.getPosition());
//            tabLayout.getTabAt(tab.getPosition()).select();
//            currentIndex = tab.getPosition();
//            strId = objList.get(tab.getPosition()).getStrId();
//            if (isNotLoads != null && isNotLoads.size() > 0 && !isNotLoads.get(currentIndex)) {
//                getProposalList(true);
//            }
//        }
//
//        @Override
//        public void onTabUnselected(TabLayout.Tab tab) {
//            View view = tab.getCustomView();
//            ((TextView) view.findViewById(R.id.tv_tab)).setTextColor(getResources().getColor(R.color.font_title));
//            Drawable drawable = getResources().getDrawable(R.drawable.shape_tab_bline_select);/// 这一步必须要做,否则不会显示.
//            drawable.setBounds(0, GIDensityUtil.dip2px(activity, 5), drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            ((TextView) view.findViewById(R.id.tv_tab)).setCompoundDrawables(null, null, null, null);
//        }
//
//        @Override
//        public void onTabReselected(TabLayout.Tab tab) {
//
//        }
//    };

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
                isNotLoads = new ArrayList<>();
                for (int i = 0; i < objList.size(); i++) {
                    intCurPages[i] = 1;
                    isNotLoads.add(false);
                }
                intCurPages[currentIndex] = 1;
                getProposalList(false);
            }
        });
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
        strId = objList.get(currentIndex).getStrId();
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(pageListener);
        getProposalList(false);
        viewPager.setCurrentItem(currentIndex);
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
            strId = objList.get(position).getStrId();
            if (isNotLoads != null && isNotLoads.size() > 0 && !isNotLoads.get(currentIndex)) {
                getProposalList(true);
            }
        }
    };

    /**
     * 添加视图
     */
    private void initAddView(final int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_viewpager_add_view, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView.setBackgroundColor(Color.WHITE);
        Utils.initEasyRecyclerView(activity, recyclerView, true, false, R.color.line_bg_white, 0.5f, 0);
        final StudyListAdapter adapter = new StudyListAdapter(this, position);
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
                StudyBean.study study = (StudyBean.study) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strId", study.getStrId());
                if (study.getStrFileType().equals("4")) {
                    ArrayList<String> imageList = new ArrayList<>();
                    for (ObjFileBean objFile : study.getAffix()) {
                        imageList.add(Utils.formatFileUrl(activity, objFile.getStrFile_url()));
                    }
                    bundle.putStringArrayList("paths", imageList);
                    bundle.putInt("position", 0);
                    showActivity(activity, ShowBigImgActivity.class, bundle);
                } else {
                    bundle.putString("strUrl", study.getStrUrl());
                    bundle.putString("headTitle", "委员学习详情");
                    bundle.putString("strFileType", study.getStrFileType());
                    if ("3".equals(study.getStrFileType())) {
                        showActivity(activity, VideoWebViewActivity.class, bundle);
                    } else {
                        showActivity(activity, H5WebViewActivity.class, bundle);
                    }
                }
            }
        });
        myViews.add(view);
        adapters.add(adapter);
    }

    /**
     * 获取委员学习tab
     */
    private void getProposalTabList() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().LearnRegulationColumnServlet(textParamMap), 1);
    }

    /**
     * 委员学习列表
     */
    private void getProposalList(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        textParamMap.put("strKeyValue", strKeyValue);
        textParamMap.put("strLoginHaiBiAccessToken", GISharedPreUtil.getString(activity, "strLoginHaiBiAccessToken"));
        doRequestNormal(ApiManager.getInstance().LearnMaterialListServlet(textParamMap), 0);
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
                    StudyListAdapter adapter = adapters.get(currentIndex);
                    SmartRefreshLayout smartRefreshLayout = myViews.get(currentIndex).findViewById(R.id.refreshLayout);
                    StudyBean studyBean = (StudyBean) data;
                    if (intCurPages[currentIndex] == 1) {
                        adapter.setList(studyBean.getObjList());
                    } else {
                        adapter.addData(studyBean.getObjList());
                    }
                    if (studyBean.getIntAllCount() > intCurPages[currentIndex] * DefineUtil.GLOBAL_PAGESIZE) {
                        smartRefreshLayout.setNoMoreData(false);
                    } else {
                        smartRefreshLayout.setNoMoreData(true);
                    }
                    if (isUpdateItem && studyBean.getObjList() != null && studyBean.getObjList().size() > 0) {
                        adapter.setData(intClickPostion, studyBean.getObjList().get(intClickPostion));
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
