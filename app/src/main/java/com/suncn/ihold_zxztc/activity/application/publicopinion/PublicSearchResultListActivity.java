package com.suncn.ihold_zxztc.activity.application.publicopinion;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.PublicOpinionListAdapter;
import com.suncn.ihold_zxztc.adapter.PublicSearch_tabAdapter;
import com.suncn.ihold_zxztc.bean.MeetingSpeakListBean;
import com.suncn.ihold_zxztc.bean.TabBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PublicSearchResultListActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    private int curPage = 1;
    private PublicOpinionListAdapter adapter;
    private String strYear;
    private String strSourceName;//反映人名称
    private String strLeaderType;//批示类型
    private String strReporterType;//反映人类型
    private String strTitle;
    private String strContent;
    private String strState;
    private String strPubType;//刊物类型
    private MeetingSpeakListBean meetingSpeakListBean;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_recyclerview);
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
        setHeadTitle("查询结果");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strYear = bundle.getString("strYear", "");
            strSourceName = bundle.getString("strSourceName", "");
            strLeaderType = bundle.getString("strLeaderType", "");
            strReporterType = bundle.getString("strReporterType", "");
            strTitle = bundle.getString("strTitle", "");
            strContent = bundle.getString("strContent", "");
            strState = bundle.getString("strState", "");
            strPubType = bundle.getString("strPubType", "");
        }
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        adapter = new PublicOpinionListAdapter(this, 0);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getList();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage++;
                getList();
            }
        });
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
        adapter.setEmptyView(R.layout.view_erv_empty_search);
        getList();
    }

    /**
     * 初始化头部布局
     */
    private View getHeadView(RecyclerView recyclerView) {
        View view = getLayoutInflater().inflate(R.layout.view_search_count, recyclerView, false);
        TextView textView = view.findViewById(R.id.tv_count);
        textView.setText(meetingSpeakListBean.getIntAllCount() + "");
        return view;
    }

    private void doLogic(int what, Object object) {
        prgDialog.closePrgDialog();
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
        switch (what) {
            case 0:
                meetingSpeakListBean = (MeetingSpeakListBean) object;
                if (curPage == 1) {
                    adapter.setList(meetingSpeakListBean.getObjList());
                } else {
                    adapter.addData(meetingSpeakListBean.getObjList());
                }
                if (meetingSpeakListBean.getIntAllCount() > curPage * DefineUtil.GLOBAL_PAGESIZE) {
                    refreshLayout.setNoMoreData(false);
                } else {
                    refreshLayout.setNoMoreData(true);
                }
                if (meetingSpeakListBean.getIntAllCount() == 0) {
                    refreshLayout.setEnableLoadMore(false);
                } else {
                    refreshLayout.setEnableLoadMore(true);
                }
                adapter.setHeaderView(getHeadView(recyclerView));
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void getList() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "1");
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("strYear", strYear);
        textParamMap.put("strSourceName", strSourceName);
        textParamMap.put("strLeaderType", strLeaderType);
        textParamMap.put("strReporterType", strReporterType);
        textParamMap.put("strTitle", strTitle);
        textParamMap.put("strContent", strContent);
        textParamMap.put("strState", strState);
        textParamMap.put("strPubType", strPubType);
        doRequestNormal(ApiManager.getInstance().InfoListByTypeServlet(textParamMap), 0);
    }
}
