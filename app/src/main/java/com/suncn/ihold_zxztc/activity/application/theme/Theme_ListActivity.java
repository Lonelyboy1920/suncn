package com.suncn.ihold_zxztc.activity.application.theme;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
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
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.hot.NewsDetailActivity;
import com.suncn.ihold_zxztc.adapter.Theme_RVAdapter;
import com.suncn.ihold_zxztc.bean.ThemeListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 更多主题议政列表Activity
 */
public class Theme_ListActivity extends BaseActivity {
    @BindView(id = R.id.ll_title) //标题
    private LinearLayout llTitle;
    @BindView(id = R.id.ll_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    private int curPage = 1;
    private String strKeyValue = ""; // 搜索关键字
    private Theme_RVAdapter adapter;
    private String headTitle;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_recyclerview_nopadding);
    }

    @Override
    public void initData() {
        super.initData();
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getResources().getString(R.string.font_search));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            headTitle = bundle.getString("headTitle");
            setHeadTitle(headTitle);
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getThemeList(true);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        recyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        adapter = new Theme_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getThemeList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage = curPage + 1;
                getThemeList(false);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ThemeListBean.ThemeBean themeBean = (ThemeListBean.ThemeBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strNewsId", themeBean.getStrId());
                bundle.putString("headTitle", headTitle + "详情");
                bundle.putString("strUrl", Utils.formatFileUrl(activity, themeBean.getStrUrl()));
//                bundle.putString("strDateState", themeBean.getStrDateState());
                showActivity(activity, NewsDetailActivity.class, bundle);
            }
        });
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
        etSearch.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                curPage = 1;
                getThemeList(false);
            }
        });
    }

    /**
     * 获取列表
     */
    private void getThemeList(boolean b) {
        if (b)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "1");
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        textParamMap.put("strKeyValue", strKeyValue);
        doRequestNormal(ApiManager.getInstance().getThemeList(textParamMap), 0);
    }

    private void doLogic(Object object, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 2:

                break;
            case 0:
                prgDialog.closePrgDialog();
                try {
                    ThemeListBean result = (ThemeListBean) object;
                    ArrayList<ThemeListBean.ThemeBean> objList = result.getSubjectList();
                    Utils.setLoadMoreViewState(result.getIntAllCount(),curPage*DefineUtil.GLOBAL_PAGESIZE,strKeyValue,refreshLayout,adapter);
                    if (curPage==1){
                        adapter.setList(objList);
                    }else {
                        adapter.addData(objList);
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
