package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GITextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Proposal_Audit_RVAdapter;
import com.suncn.ihold_zxztc.bean.ProposalTrackListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.suncn.ihold_zxztc.view.SpinerSessionPopWindow;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 提案审核列表Activity
 */
public class Proposal_AuditListActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.ll_title) //标题
    private LinearLayout llTitle;
    @BindView(id = R.id.ll_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.btn_spinner, click = true)
    private GITextView spinner_Button; // 届次选择按钮
    @BindView(id = R.id.ll_search_attend)
    private LinearLayout llSearchAttend;
    @BindView(id = R.id.ll_top_search)//标题的搜索布局
    private LinearLayout llSearchTop;
    @BindView(id = R.id.tv_search, click = true)
    private TextView search_TextView;
    @BindView(id = R.id.et_top_search)//标题搜索框
    private ClearEditText etSearchTop;
    @BindView(id = R.id.tv_cancel_1, click = true)
    private TextView tvCancelTop;
    @BindView(id = R.id.tv_session)
    private TextView session_TextView;//界次TextView
    @BindView(id = R.id.ll_session, click = true)
    private LinearLayout session_LinearLayout;//界次LinearLayout
    private SpinerSessionPopWindow<String> mSpinerPopWindow;
    public ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    private String strSessionId = "";
    private ArrayList<String> objList = new ArrayList<>();//出席缺席集合
    private Proposal_Audit_RVAdapter adapter;
    private int curPage = 1;
    private int intClickPostion;
    private String strKeyValue = "";
    private boolean checkIsFinishCheck = false;
    private String strCheckState = "";//1 表示初审  =2 表示复审
    private ArrayList<String> mDataList = new ArrayList<>();
    private int mIntType = 1;
    private ArrayList<String> yearArray = new ArrayList<>();
    private String strYear = "";//年份
    public ArrayList<SessionListBean.YearInfo> yearList; // 年份集合

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            switch (requestCode) {
                case 0:
                    if (bundle != null) {
                        checkIsFinishCheck = bundle.getBoolean("isRemove", false);
                    }
                    if (checkIsFinishCheck) {
                        adapter.remove(intClickPostion);
                    } else {
                        getAuditList(1, true);
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_recyclerview_nopadding);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sessionBeans = (ArrayList<SessionListBean.SessionBean>) bundle.getSerializable("sessionBeans");
            yearList = (ArrayList<SessionListBean.YearInfo>) bundle.getSerializable("yearList");
            session_TextView.setText(sessionBeans.get(0).getStrSessionName());
            strSessionId = sessionBeans.get(0).getStrSessionId();
            mSpinerPopWindow = new SpinerSessionPopWindow<String>(activity, sessionBeans, itemClickListener);
            try {
                strSessionId = sessionBeans.get(0).getStrSessionId();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setHeadTitle(bundle.getString("headTitle"));
        }
        llSearchAttend.setVisibility(View.VISIBLE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getAuditList(1, true);
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
            case R.id.btn_spinner:
                mSpinerPopWindow.setWidth(GIDensityUtil.dip2px(activity, 100));
                mSpinerPopWindow.showAsDropDown(spinner_Button);
                break;
            case R.id.tv_search:
                llSearchTop.setVisibility(View.VISIBLE);
                search_TextView.setVisibility(View.GONE);
                tvCancelTop.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_session:
                mSpinerPopWindow.setWidth((int) getResources().getDimension(R.dimen.sp_100_dp));
                mSpinerPopWindow.setHeight(session_TextView.getHeight() * yearList.size());
                mSpinerPopWindow.showAsDropDown(session_TextView);
                break;
            case R.id.tv_cancel_1:
                llSearchTop.setVisibility(View.INVISIBLE);
                search_TextView.setVisibility(View.VISIBLE);
                tvCancelTop.setVisibility(View.GONE);
                GIUtil.closeSoftInput(activity);
                if (GIStringUtil.isNotBlank(etSearchTop.getText().toString())) {
                    etSearchTop.setText("");
                    curPage = 1;
                    getAuditList(1, false);
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
            curPage = 1;
            getAuditList(1, false);
        }
    };

    @Override
    public void setListener() {
        super.setListener();
        etSearchTop.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getAuditList(1, false);
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, true, false, R.color.line_bg_white, 0.5f, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getAuditList(1, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getAuditList(curPage + 1, false);
            }
        });
        adapter = new Proposal_Audit_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                intClickPostion = position;
                ProposalTrackListBean.ProposalTrack proposalBean = (ProposalTrackListBean.ProposalTrack) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("headTitle", getString(R.string.string_proposal_detail));
                bundle.putString("strCaseId", proposalBean.getStrCaseMotionId());
                bundle.putString("strId", proposalBean.getStrId());
                if (2 == proposalBean.getIntState()) {
                    bundle.putString("gotoValue", getString(R.string.string_review));
                }
                bundle.putBoolean("isWorker", true);
                showActivity(activity, Proposal_DetailActivity.class, bundle, 0);
            }
        });

    }


    /**
     * 获取提案审核列表
     */
    private void getAuditList(int curPage, boolean b) {
        this.curPage = curPage;
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("strSessionId", strSessionId + "");
        textParamMap.put("strKeyValue", strKeyValue);
        if (ProjectNameUtil.isJMSZX(activity)) {
            textParamMap.put("strCheckState", strCheckState);//江门政协需要加初审复审的筛选
        }
        if (ProjectNameUtil.isCZSZX(activity)) {
            textParamMap.put("intType", mIntType + "");
        }
        doRequestNormal(ApiManager.getInstance().getProposalAuditlist(textParamMap), 0);
    }

    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        try {
            switch (what) {
                case 0:
                    ProposalTrackListBean proposalTrackListBean = (ProposalTrackListBean) object;
                    // 必须在获取完数据后刷新LoadMore设置，否则分页无法生效
                    Utils.setLoadMoreViewState(proposalTrackListBean.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, adapter);
                    if (curPage == 1) {
                        adapter.setList(proposalTrackListBean.getObjList());
                    } else {
                        adapter.addData(proposalTrackListBean.getObjList());
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getResources().getString(R.string.data_error));
        }
    }
}
