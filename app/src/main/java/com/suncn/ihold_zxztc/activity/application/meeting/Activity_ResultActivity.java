package com.suncn.ihold_zxztc.activity.application.meeting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MotionEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.ActivityResult_RVAdapter;
import com.suncn.ihold_zxztc.bean.ActivityResultListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.bean.CollectionListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

/**
 * 活动成果列表
 */
public class Activity_ResultActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    private String strId;
    private int curPage = 1;
    private ActivityResult_RVAdapter adapter;
    private boolean isEmpty;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEmpty && event.getAction() == MotionEvent.ACTION_DOWN) {
            getListData(true);
        }
        return super.onTouchEvent(event);
    }

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
                doLogic(data, sign);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            strId = bundle.getString("strId");
        }
        setHeadTitle("活动成果");
        initRecyclerView();
        getListData(true);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getListData(true);
            }
        });
        refreshLayout.setEnableLoadMore(true);
        adapter = new ActivityResult_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Bundle bundle = new Bundle();
                CollectionListBean.CollectionList activityResult = (CollectionListBean.CollectionList) adapter.getItem(position);
                bundle.putString("strUrl", activityResult.getStrUrl());
                showActivity(activity, WebViewActivity.class, bundle);
            }
        });
    }


    private void doLogic(Object object, int sign) {
        prgDialog.closePrgDialog();
        try {
            switch (sign) {
                case 0:
                    CollectionListBean collectionListBean = (CollectionListBean) object;
                    adapter.setList(collectionListBean.getObjList());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getResources().getString(R.string.data_error));
        }
    }

    private void getListData(boolean isShow) {
        if (isShow) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().getActResultList(textParamMap), 0);
    }
}
