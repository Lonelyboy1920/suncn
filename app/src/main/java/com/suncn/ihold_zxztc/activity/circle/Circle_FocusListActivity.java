package com.suncn.ihold_zxztc.activity.circle;

import android.os.Bundle;
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
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Circle_Fcous_RVAdapter;
import com.suncn.ihold_zxztc.bean.CircleListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 我的关注/推荐关注
 */
public class Circle_FocusListActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.et_search)
    private ClearEditText etSearch;
    @BindView(id = R.id.ll_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.ll_title) //标题
    private LinearLayout llTitle;
    @BindView(id = R.id.tv_cancel, click = true)
    private TextView tvCancel;
    private Circle_Fcous_RVAdapter adapter;
    private int curPage = 1;
    private String type = "";
    private String strState = ""; //0表示我的关注，1表示推荐关注
    private String strKeyValue = "";

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_circle_fcous_list);
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
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getResources().getString(R.string.font_search));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("Type");
        }
        if (("1").equals(type)) {
            setHeadTitle(getString(R.string.string_my_follow));
            strState = "0";
        } else {
            setHeadTitle(getString(R.string.string_recommend_follow));
            strState = "1";
        }
        GISharedPreUtil.setValue(activity, "strForceState", type);
        initRecyclerView();
        getRecommendList(true);
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
                getRecommendList(false);
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0.5f, 10);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getRecommendList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage = curPage + 1;
                getRecommendList(false);
            }
        });

        adapter = new Circle_Fcous_RVAdapter(this);
        adapter.setType(strState);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                CircleListBean.RecommendBean recommendBean = (CircleListBean.RecommendBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strUserId", recommendBean.getStrUserId());
//                showActivity(activity, Circle_PersonalPageActivity.class, bundle);
                showActivity(activity, CirclePersonPageActivity.class, bundle);
            }
        });
    }


    /**
     * 获取推荐关注列表
     */
    private void getRecommendList(boolean b) {
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("strState", strState);
        textParamMap.put("strKeyValue", strKeyValue);
        doRequestNormal(ApiManager.getInstance().getNoticeList(textParamMap), 0);
    }

    /**
     * 关注
     */
    public void followUser(String strPubUserId, String strPubUserName) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strPubUserName", strPubUserName);
        textParamMap.put("strPubUserId", strPubUserId);
        doRequestNormal(ApiManager.getInstance().addDynamicNotice(textParamMap), 2);
    }

    private void doLogic(int what, Object object) {
        prgDialog.closePrgDialog();
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        switch (what) {
            case 2:
                DefineUtil.isNeedRefresh = true;
                break;
            case 0:
                try {
                    CircleListBean circleListBean = (CircleListBean) object;
                    Utils.setLoadMoreViewState(circleListBean.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, adapter);
                    if (curPage == 1) {
                        adapter.setList(circleListBean.getUserList());
                    } else {
                        adapter.addData(circleListBean.getUserList());
                    }
                    break;
                } catch (Exception e) {
                    showToast(getResources().getString(R.string.data_error));
                }
        }
    }
}
