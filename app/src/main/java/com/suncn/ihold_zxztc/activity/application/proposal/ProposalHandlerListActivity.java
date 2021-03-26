package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Proposal_Track_RVAdapter;
import com.suncn.ihold_zxztc.bean.ProposalTrackListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.SpinerSessionPopWindow;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 提案交办列表
 */
public class ProposalHandlerListActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.tv_session)
    private TextView session_TextView;//界次TextView
    @BindView(id = R.id.ll_session, click = true)
    private LinearLayout session_LinearLayout;//界次LinearLayout
    @BindView(id = R.id.ll_top_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_top_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel_1, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.tv_search, click = true)
    private TextView search_TextView;
    private String strType = "1";//待签收1，办理中2，已办结3
    private Proposal_Track_RVAdapter adapter;
    public ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    private String strKeyValue = "";
    private String strSessionId = "";
    private SpinerSessionPopWindow<String> mSpinerPopWindow;
    private int intClickPostion;
    private int curPage = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    adapter.remove(intClickPostion);
                default:
                    break;
            }
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_handler_list);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sessionBeans = (ArrayList<SessionListBean.SessionBean>) bundle.getSerializable("sessionBeans");

            session_TextView.setText(sessionBeans.get(0).getStrSessionName());
            strSessionId = sessionBeans.get(0).getStrSessionId();
            mSpinerPopWindow = new SpinerSessionPopWindow<>(activity, sessionBeans, itemClickListener);

            setHeadTitle(bundle.getString("headTitle"));
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getMeetingList(true);
    }

    /**
     * 获取提案列表
     */
    private void getMeetingList(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        textParamMap.put("strSessionId", strSessionId);
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("strType", "4");
        textParamMap.put("strKeyValue", strKeyValue);
        doRequestNormal(ApiManager.getInstance().getProposalHandlerList(textParamMap), 0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_session:
                mSpinerPopWindow.setWidth(GIDensityUtil.dip2px(activity, 100));
                mSpinerPopWindow.showAsDropDown(session_TextView);
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
                    curPage = 1;
                    getMeetingList(true);
                }
                break;
        }
    }

    /**
     * popupwindow显示的ListView的item点击事件(年份)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            session_TextView.setText(sessionBeans.get(position).getStrSessionName());
            strSessionId = sessionBeans.get(position).getStrSessionId();
            getMeetingList(true);
        }
    };

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
                    getMeetingList(false);
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
                getMeetingList(false);
            }
        });
    }


    /**
     * 添加视图
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        adapter = new Proposal_Track_RVAdapter(this, 1);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getMeetingList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage = curPage + 1;
                getMeetingList(false);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                intClickPostion = position;
                ProposalTrackListBean.ProposalTrack proposalBean = (ProposalTrackListBean.ProposalTrack) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strId", proposalBean.getStrCaseMotionId());
                bundle.putString("headTitle", "提案详情");
                bundle.putString("strCaseId", proposalBean.getStrCaseMotionId());
                bundle.putBoolean("isWorker", true);
                bundle.putBoolean("isUnit", true);
                bundle.putString("gotoValue", "交办");
                showActivity(activity, Proposal_DetailActivity.class, bundle, 0);
            }
        });
    }


    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    ProposalTrackListBean proposalTrackListBean = (ProposalTrackListBean) data;
                    List<ProposalTrackListBean.ProposalTrack> proposalBeans = proposalTrackListBean.getObjList();
                    Utils.setLoadMoreViewState(proposalTrackListBean.getIntAllCount(),curPage*DefineUtil.GLOBAL_PAGESIZE,strKeyValue,refreshLayout,adapter);
                    if (curPage==1){
                        adapter.setList(proposalBeans);
                    }else {
                        adapter.addData(proposalBeans);
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
