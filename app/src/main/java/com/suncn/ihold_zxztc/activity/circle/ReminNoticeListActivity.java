package com.suncn.ihold_zxztc.activity.circle;

import com.gavin.giframe.ui.BindView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.CircleRemindListAdapter;
import com.suncn.ihold_zxztc.bean.MsgRemindListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 圈子的提醒列表
 */
public class ReminNoticeListActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    private CircleRemindListAdapter adapter;
    private int curPage = 1;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_recyclerview_mainbg);
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
        setHeadTitle("消息提醒");
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 10f, 10);
        adapter = new CircleRemindListAdapter(activity);
        recyclerView.setAdapter(adapter);
        getList(true);
    }

    private void getList(boolean b) {
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        doRequestNormal(ApiManager.getInstance().DynamicNoticeServlet(textParamMap), 0);
    }


    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        switch (what) {
            case 0:
                MsgRemindListBean msgRemindListBean = (MsgRemindListBean) object;
                Utils.setLoadMoreViewState(msgRemindListBean.getIntAllCount(),curPage*DefineUtil.GLOBAL_PAGESIZE,
                        "",refreshLayout,adapter);
                if (curPage == 1) {
                    adapter.setList(msgRemindListBean.getObjList());
                } else {
                    adapter.addData(msgRemindListBean.getObjList());
                }
                break;
            case 1:
                DefineUtil.isNeedRefresh = true;
                adapter.notifyDataSetChanged();
                break;
        }

    }

    /**
     * 关注
     */
    public void followUser(String strPubUserId, String strPubUserName) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strPubUserName", strPubUserName);
        textParamMap.put("strPubUserId", strPubUserId);
        doRequestNormal(ApiManager.getInstance().addDynamicNotice(textParamMap), 1);
    }


    @Override
    public void setListener() {
        super.setListener();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage = curPage + 1;
                getList(false);
            }
        });
    }
}
