package com.suncn.ihold_zxztc.activity.application.proposal;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
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
import com.suncn.ihold_zxztc.adapter.Proposal_PublicSearch_RVAdapter;
import com.suncn.ihold_zxztc.bean.ProposalListBean;
import com.suncn.ihold_zxztc.bean.ProposalTypeListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;

import skin.support.content.res.SkinCompatResources;

/**
 * 公开提案查询
 */
public class Proposal_PublicSearchActivity extends BaseActivity {
    @BindView(id = R.id.tv_msg)
    private TextView msg_TextView;//筛选条数结果TextView
    @BindView(id = R.id.rl_session, click = true)
    private RelativeLayout session_RelativeLayout;//界次选择RelativeLayout
    @BindView(id = R.id.tv_session)
    private TextView session_TextView;//界次TextView
    @BindView(id = R.id.rl_type, click = true)
    private RelativeLayout type_RelativeLayout;//类型RelativeLayout
    @BindView(id = R.id.tv_type)
    private TextView type_TextView;//类型TextView
    @BindView(id = R.id.rl_state, click = true)
    private RelativeLayout state_RelativeLayout;//提案状态RelativeLayout
    @BindView(id = R.id.tv_state)
    private TextView state_TextView;//提案状态TextView
    @BindView(id = R.id.et_searchInfo)
    private ClearEditText search_EditText;//搜索
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;//列表
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.tv_session_expend)
    private TextView session_expend_TextView;//界次展开图标TextView
    @BindView(id = R.id.tv_type_expend)
    private TextView type_expend_TextView;//类型展开图标TextView
    @BindView(id = R.id.tv_state_expend)
    private TextView state_expend_TextView;//状态展开图标TextView
    private String sessionCode;
    private String headTitle; // 标题名称
    private int zxtaType; // 提案菜单类型
    private int intUserRole; // 用户类型（0-委员；1-工作人员；2-承办单位）
    private ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    private String strKeyValue = ""; // 搜索关键字
    private boolean isSearch;
    private int curPage = 1;
    private String[] sessionArray;//届次数组
    private String sessionId;
    private String[] stateArray = new String[]{"所有状态", "未办理", "办理中", "已办结"};
    private ArrayList<ProposalTypeListBean.ProposalType> zxtaTypeList;//提案类别
    private String[] typeArray;//提案类型数组
    private String typeId;
    private boolean isChooseType = false;
    private int searchCount;
    private Proposal_PublicSearch_RVAdapter adapter;
    private boolean isFirst = false;
    private ArrayList<ProposalListBean.ProposalBean> zxtaBeans;
    private String strUseCbxt;//是否启用承办系统 0显示1不显示(审核时、待交办时有交办信息的根据分发类型显示是否有承办系统的)

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_publicsearch);
    }

    @Override
    public void initData() {
        super.initData();
        strUseCbxt = GISharedPreUtil.getString(activity, "strUseCbxt");

        // msg_TextView.setText(searchCount);
        goto_Button.setVisibility(View.INVISIBLE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        //state_TextView.setText(stateArray[0]);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            headTitle = bundle.getString("headTitle");
            setHeadTitle(headTitle);
            zxtaType = bundle.getInt("zxtaType");
            intUserRole = bundle.getInt("intUserRole");
            sessionBeans = (ArrayList<SessionListBean.SessionBean>) bundle.getSerializable("sessionBeans");
            if (sessionBeans != null && sessionBeans.size() > 0) {
                sessionArray = new String[sessionBeans.size()];
                for (int i = 0; i < sessionBeans.size(); i++) {
                    sessionArray[i] = sessionBeans.get(i).getStrSessionName();
                    if (session_TextView.getText().toString().trim().equals(sessionArray[i])) {
                    }
                }
                session_TextView.setText(sessionArray[0]);
                sessionId = sessionBeans.get(0).getStrSessionId();
            }
            initRecyclerView();
            getListData(true, true);
            if (strUseCbxt.equals("1") && strFlowCaseDistType.equals("0")) {//办理状态不显示的条件
                state_RelativeLayout.setVisibility(View.GONE);
            } else {
                state_RelativeLayout.setVisibility(View.VISIBLE);
            }
            // msg_TextView.setText(searchCount + "");
        }

        stateArray= getResources().getStringArray(R.array.handling_status_array);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getListData(false, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage++;
                getListData(false, false);
            }
        });
        adapter = new Proposal_PublicSearch_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ProposalListBean.ProposalBean proposalBean = (ProposalListBean.ProposalBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strId", proposalBean.getStrId());
                bundle.putString("headTitle", "提案详情");
                bundle.putString("strCaseId", proposalBean.getStrCaseMotionId());
                bundle.putString("strId", proposalBean.getStrId());
                bundle.putString("strAttend", proposalBean.getIntAttend() + "");
                bundle.putString("strFlowState", proposalBean.getStrFlowState());
                showActivity(activity, Proposal_DetailActivity.class, bundle, 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_session:
                showChooseDialog(0, sessionArray);
                break;
            case R.id.rl_type:
                curPage = 1;
                getTypeList();
                break;
            case R.id.rl_state:
                showChooseDialog(2, stateArray);
                break;
            default:
                break;
        }
    }

    /**
     * 提案类别列表
     */
    private void getTypeList() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strCaseMotionSessionId", sessionId); // 届次信息
        doRequestNormal(ApiManager.getInstance().getProposalTypeList(textParamMap), 1);
    }

    /**
     * 选择对话框
     */
    private void showChooseDialog(final int i, final String[] arr) {
        final NormalListDialog normalListDialog = new NormalListDialog(activity, arr);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSearch = true;
                if (i == 0) {
                    session_TextView.setText(arr[position]);
                    sessionId = sessionBeans.get(position).getStrSessionId();
                }
                if (i == 1) {
                    isChooseType = true;
                    type_TextView.setText(arr[position]);
                    for (int i = 0; i < zxtaTypeList.size(); i++) {
                        if (arr[position].equals(zxtaTypeList.get(i).getStrCaseTypeName())) {
                            typeId = zxtaTypeList.get(i).getStrCaseTypeId();
                        }
                    }

                }
                if (i == 2) {
                    state_TextView.setText(arr[position]);
                }
                normalListDialog.dismiss();
                curPage = 1;
                getListData(true, false);
            }
        });
        if (i == 0) {
            normalListDialog.title(getString(R.string.string_please_select_the_session));
        }
        if (i == 1) {
            normalListDialog.title(getString(R.string.string_please_select_proposal_category));
        }
        if (i == 2) {
            normalListDialog.title(getString(R.string.string_please_select_processing_status));
        }
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        normalListDialog.titleTextColor(Color.WHITE);
        normalListDialog.show();
    }

    @Override
    public void setListener() {
        super.setListener();
        search_EditText.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getListData(false, false);
            }
        });
    }

    /**
     * 公开提案查询列表
     */
    private void getListData(boolean isTrue, boolean isFirst) {
        this.isFirst = isFirst;
        if (isTrue)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strKeyValue", strKeyValue); // 查询关键字
        textParamMap.put("strCaseMotionSessionId", sessionId); // 届次信息id
        if (isChooseType) {
            textParamMap.put("strCaseTypeId", typeId); // 类型id
        } else {
            textParamMap.put("strCaseTypeId", "-1"); // 类型id
        }
        textParamMap.put("strState", state_TextView.getText().toString().trim()); // 状态
        textParamMap.put("intCurPage", curPage + ""); // 当前页数（默认值为1）
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + ""); // 每页记录数（ 默认值为10）
        doRequestNormal(ApiManager.getInstance().getPublicProposalList(textParamMap), 0);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        switch (what) {
            case 1:
                prgDialog.closePrgDialog();
                try {
                    ProposalTypeListBean zxtaTypeListBean = (ProposalTypeListBean) obj;
                    zxtaTypeList = zxtaTypeListBean.getObjList();
                    if (zxtaTypeList != null && zxtaTypeList.size() > 0) {
                        typeArray = new String[zxtaTypeList.size()];
                        for (int i = 0; i < zxtaTypeList.size(); i++) {
                            typeArray[i] = zxtaTypeList.get(i).getStrCaseTypeName();
                        }
                    }
                    showChooseDialog(1, typeArray);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 0:
                prgDialog.closePrgDialog();
                try {
                    final ProposalListBean zxtaListBean = (ProposalListBean) obj;
                    searchCount = zxtaListBean.getIntAllCount();
                    zxtaBeans = new ArrayList<>();
                    zxtaBeans = zxtaListBean.getObjList();
                    if (curPage == 1) {
                        adapter.setList(zxtaBeans);
                    } else {
                        adapter.addData(zxtaBeans);
                    }
                    Utils.setLoadMoreViewState(zxtaListBean.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, adapter);
                    msg_TextView.setVisibility(View.VISIBLE);
                    msg_TextView.setText(GIUtil.showHtmlInfo(getString(R.string.string_alerady_searched_for_you) + "<font color=\"#ef4b39\">" + searchCount + "</font>" + getString(R.string.string_qualified_proposal)));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            msg_TextView.setVisibility(View.GONE);
                        }
                    }, 3000L);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }

        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

}
