package com.suncn.ihold_zxztc.activity.application.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.adapter.CheckLeaveListAdapter;
import com.suncn.ihold_zxztc.bean.CheckLeaveListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * 请假审核fragment
 */
public class MeetAct_Detail_AuditLeaveFragment extends BaseFragment {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    private static int sign;
    private static String strId;
    private int curPage = 1;
    private CheckLeaveListAdapter adapter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getListData();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getListData();
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_leave_list, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static MeetAct_Detail_AuditLeaveFragment newInstance(int mSign, String mStrId) {
        MeetAct_Detail_AuditLeaveFragment memListFragment = new MeetAct_Detail_AuditLeaveFragment();
        sign = mSign;
        strId = mStrId;
        return memListFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        initRecyclerView();
        getListData();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 1, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getListData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage++;
                getListData();
            }
        });
        adapter = new CheckLeaveListAdapter(activity, sign);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 获取会议活动请假列表
     */
    private void getListData() {
        textParamMap = new HashMap<>();
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        textParamMap.put("strId", strId);
        if (sign == DefineUtil.hygl) {
            doRequestNormal(ApiManager.getInstance().getMeetLeaveList(textParamMap), 1);
        } else {
            doRequestNormal(ApiManager.getInstance().getActLeaveList(textParamMap), 1);

        }
    }

    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        switch (what) {
            case 1:
                CheckLeaveListBean collectionListBean = (CheckLeaveListBean) obj;
                if (curPage == 1) {
                    adapter.setList(collectionListBean.getObjList());
                } else {
                    adapter.addData(collectionListBean.getObjList()); // 数据加载完后底部显示的view
                }
                Utils.setLoadMoreViewState(collectionListBean.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, "", refreshLayout, adapter);
                break;
            default:
                break;
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            GIToastUtil.showMessage(activity, toastMessage);
    }

    /**
     * 更新会议、活动详情参会情况页面
     */
    public void update() {
        getListData();
    }
}
