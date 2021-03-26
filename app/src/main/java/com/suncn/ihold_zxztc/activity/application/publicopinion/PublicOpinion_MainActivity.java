package com.suncn.ihold_zxztc.activity.application.publicopinion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.PublicOpinion_MainTop_RVAdapter;
import com.suncn.ihold_zxztc.adapter.PublicOpinion_Main_RVAdapter;
import com.suncn.ihold_zxztc.bean.PublicOpinionListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 社情民意Activity
 */
public class PublicOpinion_MainActivity extends BaseActivity {
    @BindView(id = R.id.tv_session)
    private TextView session_TextView;//界次TextView
    @BindView(id = R.id.ll_session, click = true)
    private LinearLayout session_LinearLayout;//界次LinearLayout
    @BindView(id = R.id.refreshlayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.recyclerView_top)
    private RecyclerView recyclerViewTop;
    @BindView(id = R.id.tv_more, click = true)
    private TextView tvMore;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.ll_title) //标题
    private LinearLayout llTitle;
    @BindView(id = R.id.ll_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.ll_top_search)//标题的搜索布局
    private LinearLayout llSearchTop;
    @BindView(id = R.id.et_top_search)//标题搜索框
    private ClearEditText etSearchTop;
    @BindView(id = R.id.tv_cancel_1, click = true)
    private TextView tvCancelTop;
    @BindView(id = R.id.tv_search, click = true)
    private TextView search_TextView;
    @BindView(id = R.id.ll_public_opinion)
    private LinearLayout llPublicOpinion;
    private PublicOpinion_MainTop_RVAdapter topListAdapter;
    private PublicOpinion_Main_RVAdapter adapter;
    private int curPage = 1;
    private String strKeyValue = "";
    List<PublicOpinionListBean.PubListBean> pubListBeans;
    List<PublicOpinionListBean.ObjListBean> objListBeans;
    private ArrayList<String> yearArray = new ArrayList<>();
    private String strYear = "";//年份
    private SpinerPopWindow<String> mSpinerPopWindow;
    public ArrayList<SessionListBean.YearInfo> yearList; // 年份集合


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) { // 新增后刷新列表数据
            getListData(1, false);
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_publicopinion_main);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_social_opinion));
        //荔湾区需要添加年份筛选操作
        if (ProjectNameUtil.isLWQZX(activity)) {
            findViewById(R.id.ll_search_attend).setVisibility(View.VISIBLE);
        } else {
            goto_Button.setText(getString(R.string.font_search));
            goto_Button.setVisibility(View.VISIBLE);
        }

        if (AppConfigUtil.isExistUserByCppccSession(activity)) {
            two_Button.setVisibility(View.VISIBLE);
            two_Button.setText(getResources().getString(R.string.font_add));
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            yearList = (ArrayList<SessionListBean.YearInfo>) bundle.getSerializable("yearList");
            session_TextView.setText(yearList.get(0).getStrYear());
            strYear = yearList.get(0).getStrYear();
//            yearArray = GIDateUtils.getYearStringArray(yearList.size());
            for (SessionListBean.YearInfo yearInfo : yearList) {
                yearArray.add(yearInfo.getStrYear());
            }
            mSpinerPopWindow = new SpinerPopWindow<String>(activity, yearArray, itemClickListener);
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getListData(1, true);
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
            getListData(0, true);
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_two:
                showActivity(activity, PublicOpinion_AddActivity.class, 0);
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
                }
                break;
            case R.id.tv_more:
                Bundle bundle = new Bundle();
                bundle.putSerializable("yearList", yearList);
                showActivity(activity, PublicOpinion_MoreActivity.class, bundle);
                break;
            case R.id.ll_session:
                mSpinerPopWindow.setWidth(GIDensityUtil.dip2px(activity, session_LinearLayout.getWidth()));
                mSpinerPopWindow.setHeight(session_TextView.getHeight() * yearList.size());
                mSpinerPopWindow.showAsDropDown(session_TextView);
                break;
            case R.id.tv_search:
                llSearchTop.setVisibility(View.VISIBLE);
                search_TextView.setVisibility(View.GONE);
                tvCancelTop.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel_1:
                llSearchTop.setVisibility(View.INVISIBLE);
                search_TextView.setVisibility(View.VISIBLE);
                tvCancelTop.setVisibility(View.GONE);
                if (GIStringUtil.isNotBlank(etSearchTop.getText().toString())) {
                    etSearchTop.setText("");
                    getListData(1, true);
                }
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getListData(1, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getListData(++curPage, false);
            }
        });
        etSearch.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getListData(1, false);
            }
        });
        etSearchTop.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    strKeyValue = etSearch.getText().toString();
                    if (!GIStringUtil.isNotBlank(strKeyValue)) {
                        GIUtil.closeSoftInput(activity);
                    }
                    getListData(1, false);
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
                getListData(1, false);
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0.5f, 10);
        adapter = new PublicOpinion_Main_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                PublicOpinionListBean.ObjListBean pubListBean = (PublicOpinionListBean.ObjListBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strUrl", pubListBean.getStrUrl());
                bundle.putString("headTitle", getString(R.string.string_social_opinion));
                //bundle.putString("strNewsId", adapter.getItem(position).getStrId());
                showActivity(activity, WebViewActivity.class, bundle);
            }
        });
        //完成layoutManager设置
        recyclerViewTop.setLayoutManager(new GridLayoutManager(activity, 3));
        topListAdapter = new PublicOpinion_MainTop_RVAdapter(activity);
        recyclerViewTop.setAdapter(topListAdapter);
        topListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                PublicOpinionListBean.PubListBean pubListBean = (PublicOpinionListBean.PubListBean) topListAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strUrl", pubListBean.getStrUrl());
                bundle.putString("headTitle", getString(R.string.string_public_publication));
                //bundle.putString("strNewsId", adapter.getItem(position).getStrId());
                showActivity(activity, WebViewActivity.class, bundle);
            }
        });
    }

    /**
     * 获取社情民意列表
     */
    private void getListData(int curPage, boolean b) {
        if (GIStringUtil.isNotBlank(strKeyValue)) {
            llPublicOpinion.setVisibility(View.GONE);
        } else {
            llPublicOpinion.setVisibility(View.VISIBLE);
        }
        this.curPage = curPage;
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("strKeyValue", strKeyValue);
        textParamMap.put("strYear", strYear);
        doRequestNormal(ApiManager.getInstance().getPubublicOpinionList(textParamMap), 0);
    }

    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        try {
            switch (what) {
                case 0:
                    PublicOpinionListBean publicOpinionListBean = (PublicOpinionListBean) object;
                    objListBeans = publicOpinionListBean.getObjList();
                    if (curPage == 1) {
                        adapter.setList(objListBeans);
                    } else {
                        adapter.addData(objListBeans);
                    }
                    Utils.setLoadMoreViewState(publicOpinionListBean.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, adapter);
                    pubListBeans = publicOpinionListBean.getPubList();
                    topListAdapter.setList(pubListBeans);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getResources().getString(R.string.data_error));
        }
    }
}
