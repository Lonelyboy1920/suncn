package com.suncn.ihold_zxztc.activity.my;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.hot.NewsDetailActivity;
import com.suncn.ihold_zxztc.adapter.My_Collection_RVAdapter;
import com.suncn.ihold_zxztc.bean.CollectionListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 我的收藏Activity
 */
public class CollectionListActivity extends BaseActivity {
    @BindView(id = R.id.ll_main)
    private LinearLayout llMain;
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
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;


    private My_Collection_RVAdapter adapter;
    private int curPage = 1;
    private String strKeyValue = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            getCollectionList(1, false);
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_recyclerview);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("我的收藏");
        llMain.setBackgroundColor(getResources().getColor(R.color.main_bg));
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getResources().getString(R.string.font_search));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getCollectionList(1, true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
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
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    strKeyValue = etSearch.getText().toString();
                    if (!GIStringUtil.isNotBlank(strKeyValue)) {
                        GIUtil.closeSoftInput(activity);
                    }
                    getCollectionList(1, false);
                    return true;
                }
                return false;
            }
        });

        etSearch.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getCollectionList(1, false);
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        adapter = new My_Collection_RVAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Bundle bundle = new Bundle();
                CollectionListBean.CollectionList collectionList = (CollectionListBean.CollectionList) adapter.getItem(position);
                bundle.putString("strUrl", collectionList.getStrUrl());
                bundle.putString("strNewsId", collectionList.getStrId());
                bundle.putSerializable("objShare", collectionList.getObjShare());
//                bundle.putString("strDateState", adapter.getItem(position).getStrDateState());
                if ("1".equals(collectionList.getStrSourceType())) {
                    bundle.putBoolean("isNews", true);
                }
                showActivity(activity, NewsDetailActivity.class, bundle, 0);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getCollectionList(1, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new com.scwang.smartrefresh.layout.listener.OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getCollectionList(curPage + 1, false);
            }
        });
    }

    /**
     * 动态控制loadMore事件
     */
    private void setRecyclerViewLoadMore(int allCount) {
        if (curPage == 1 || allCount != -1) {
            /* 有搜索功能时需添加如下代码 */
            if (GIStringUtil.isNotBlank(strKeyValue)) {
                adapter.setEmptyView(R.layout.view_erv_empty_search); // 设置无数据时的view（搜索）
            } else {
                adapter.setEmptyView(R.layout.view_erv_empty); // 设置无数据时的view
            }
            if (allCount > curPage * DefineUtil.GLOBAL_PAGESIZE) {
                refreshLayout.setNoMoreData(false);
            } else {
                refreshLayout.setNoMoreData(true);
            }
        }
    }

    /**
     * 获取收藏列表
     */
    private void getCollectionList(int curPage, boolean b) {
        this.curPage = curPage;
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("strKeyValue", strKeyValue);
        doRequestNormal(ApiManager.getInstance().getCollectionList(textParamMap), 0);
    }

    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        try {
            switch (what) {
                case 0:
                    CollectionListBean collectionListBean = (CollectionListBean) object;
                    // 必须在获取完数据后刷新LoadMore设置，否则分页无法生效
                    setRecyclerViewLoadMore(collectionListBean.getIntAllCount());
                    if (curPage == 1) {
                        adapter.setList(collectionListBean.getObjList());
                    } else {
                        adapter.addData(collectionListBean.getObjList());
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getResources().getString(R.string.data_error));
        }
    }
}
