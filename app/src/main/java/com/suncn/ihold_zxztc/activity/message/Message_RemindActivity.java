package com.suncn.ihold_zxztc.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.BaseInterface;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_DetailActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_SignUpActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_DetailActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_FeedbackActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_JoinAffirmActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.OverSearchListActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.activity.netmeet.NetMeetDetailActivity;
import com.suncn.ihold_zxztc.adapter.Message_Remind_RVAdapter;
import com.suncn.ihold_zxztc.bean.MessageRemindListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ??????????????????
 */
public class Message_RemindActivity extends BaseActivity implements BaseInterface {
    @BindView(id = R.id.ll_title) //??????
    private LinearLayout llTitle;
    @BindView(id = R.id.ll_search)//?????????????????????
    private LinearLayout llSearch;
    @BindView(id = R.id.et_search)//???????????????
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    private int curPage = 1;
    private String strKeyValue = ""; // ???????????????
    private Message_Remind_RVAdapter adapter;
    private String headTitle;
    private int intClickPos;
    private int intUserRole;
    private String strAnAnIds = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0://?????? ????????????
                case 2://???????????? ????????????
                    MessageRemindListBean.MessageRemindBean messageRemindBean = (MessageRemindListBean.MessageRemindBean) adapter.getItem(intClickPos);
                    messageRemindBean.setStrMobileState("1");
                    adapter.setData(intClickPos, messageRemindBean);
                    // adapter.remove(intClickPos);
                    break;
                case 4://????????????/??????/??????????????????
                    adapter.remove(intClickPos);
                    break;
                default:
                    break;
            }
        } else if (resultCode == -2) {//??????????????????
            adapter.remove(intClickPos);
        }
    }

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
        setHeadTitle("????????????");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            headTitle = bundle.getString("headTitle");
            strAnAnIds = bundle.getString("strAnAnIds", "");
            setHeadTitle(headTitle);
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        intUserRole = GISharedPreUtil.getInt(activity, "intUserRole");
        recyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
        initRecyclerView();
        getThemeList(true);
    }

    /**
     * ?????????RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0, 0);
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
                curPage++;
                getThemeList(false);
            }
        });
        adapter = new Message_Remind_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.addChildClickViewIds(R.id.ll_state);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                setOnClick(adapter.getItem(position), view, -1);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                intClickPos = position;
                MessageRemindListBean.MessageRemindBean messageRemindBean = (MessageRemindListBean.MessageRemindBean) adapter.getItem(position);
                dealReadRemind(messageRemindBean.getStrId());
                ((MessageRemindListBean.MessageRemindBean) adapter.getItem(position)).setStrMobileState("1");
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFromRemindList", true);
                String strAffirType = messageRemindBean.getStrAffirType();
                switch (strAffirType) {
                    case "01"://??????
                        String strAttend = "";
                        String strFlowState = "";
                        String strButtonJSFunction = messageRemindBean.getStrButtonJSFunction();
                        if ("feedBackHandler".equals(strButtonJSFunction)) {
                            strFlowState = "?????????";
                        } else if ("showCaseMotionAlly".equals(strButtonJSFunction)) {
                            strAttend = "2";
                        }
                        bundle.putString("strId", messageRemindBean.getStrAffirId());
                        bundle.putString("headTitle", "????????????");
                        bundle.putString("strAttend", strAttend);
                        bundle.putString("strFlowState", strFlowState);
                        showActivity(activity, Proposal_DetailActivity.class, bundle, 0);
                        break;
                    case "02":// ??????  strButtonJSFunction= showMeet ??????????????????
                    case "03"://  ??????  strButtonJSFunction =showEvent ??????????????????
                        boolean isShowSign = false;//??????????????????
                        if ("showMeet".equals(messageRemindBean.getStrButtonJSFunction()) || "showEvent".equals(messageRemindBean.getStrButtonJSFunction())) {
                            isShowSign = true;
                        }
                        bundle.putString("strId", messageRemindBean.getStrAffirId());
                        bundle.putBoolean("isShowSign", isShowSign);
                        if (strAffirType.equals("03")) {
                            bundle.putInt("sign", DefineUtil.wdhd);
                            bundle.putString("headTitle", "????????????");
                        } else {
                            bundle.putInt("sign", DefineUtil.wdhy);
                            bundle.putString("headTitle", "????????????");
                        }
                        bundle.putBoolean("isFromList", false);
                        bundle.putBoolean("onlyShowNormalInfo", true);
                        bundle.putString("strAttendId", messageRemindBean.getStrAttendId());
                        showActivity(activity, MeetAct_DetailActivity.class, bundle, 2);
                        break;
                    case "04":// ????????????
                    case "05": // ??????
                    case "06":// ????????????
                        bundle.putString("headTitle", "????????????");
                        bundle.putBoolean("isMsgRemind", true);
                        bundle.putString("strUrl", messageRemindBean.getStrUrl());
                        showActivity(activity, WebViewActivity.class, bundle, 4);
                        break;
                    case "07":
                        bundle.putString("strId",messageRemindBean.getStrAffirId());
                        showActivity(activity, NetMeetDetailActivity.class,bundle);
                        break;
                    default:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto:
                if (intUserRole == 2 || intUserRole == 3
                        || GISharedPreUtil.getString(activity, "strShowLzIconState").equals("0")) {
                    if (llTitle.getVisibility() == View.VISIBLE) {
                        llTitle.setVisibility(View.GONE);
                        llSearch.setVisibility(View.VISIBLE);
                        goto_Button.setVisibility(View.INVISIBLE);
                        tvCancel.setVisibility(View.VISIBLE);
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("searchType", 1);
                    showActivity(activity, OverSearchListActivity.class, bundle);
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
            default:
                break;
        }
        super.onClick(v);
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
     * ????????????
     */
    private void getThemeList(boolean b) {
        if (b)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "1");
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        textParamMap.put("strKeyValue", strKeyValue);
        if (GIStringUtil.isNotBlank(strAnAnIds)) {//????????????????????????
            textParamMap.put("strIds", strAnAnIds);
        }
        doRequestNormal(ApiManager.getInstance().geMessageRemindList(textParamMap), 0);
    }

    /**
     * ??????????????????
     */
    private void dealReadRemind(String strId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().dealReadRemind(textParamMap), 1);
    }


    private void doLogic(Object object, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 1://??????
                break;
            case 2:
                break;
            case 0:
                prgDialog.closePrgDialog();
                try {
                    MessageRemindListBean result = (MessageRemindListBean) object;
                    ArrayList<MessageRemindListBean.MessageRemindBean> objList = result.getObjList();
                    if (curPage == 1) {
                        adapter.setList(objList);
                    } else {
                        adapter.addData(objList);
                    }
                    Utils.setLoadMoreViewState(result.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            prgDialog.closePrgDialog();
        showToast(toastMessage);
    }

    @Override
    public void setOnClick(Object object, View view, int type) {
        MessageRemindListBean.MessageRemindBean messageRemindBean = (MessageRemindListBean.MessageRemindBean) object;
        dealReadRemind(messageRemindBean.getStrId());
        intClickPos = adapter.getItemPosition(messageRemindBean);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromRemind", true);
        type = Integer.parseInt(messageRemindBean.getStrAffirType());
        if (view.getId() == R.id.ll_state) {
            switch (type) {//01 ?????? 02 ?????? 03 ?????? 04 ???????????? 05 ?????? 06 ???????????? ?????????Inter.parseInt??????
                case 1:
                    String strAttend = "";
                    String strButtonJSFunction = messageRemindBean.getStrButtonJSFunction();
                    if ("feedBackHandler".equals(strButtonJSFunction)) {
                        bundle.putString("strId", messageRemindBean.getStrAffirId());
                        showActivity(activity, Proposal_FeedbackActivity.class, bundle, 0);
                    } else if ("showCaseMotionAlly".equals(strButtonJSFunction)) {
                        strAttend = "2";
                        bundle.putString("strId", messageRemindBean.getStrAffirId());
                        bundle.putString("strAttend", strAttend);
                        showActivity(activity, Proposal_JoinAffirmActivity.class, bundle, 0);
                    }
                    break;
                case 2:
                case 3:
                    boolean isShowSign = false;
                    if ("showMeet".equals(messageRemindBean.getStrButtonJSFunction()) || "showEvent".equals(messageRemindBean.getStrButtonJSFunction())) {
                        isShowSign = true;
                    }
                    if (2 == type) {
                        bundle.putString("headTitle", "????????????");
                        bundle.putInt("sign", DefineUtil.wdhy);
                    } else {
                        bundle.putString("headTitle", "????????????");
                        bundle.putInt("sign", DefineUtil.wdhd);
                    }
                    bundle.putString("strName", messageRemindBean.getStrName());
                    bundle.putString("strStartDate", messageRemindBean.getStrStartDate());
                    bundle.putString("strEndDate", messageRemindBean.getStrEndDate());
                    bundle.putString("strPlace", messageRemindBean.getStrPlace());
                    bundle.putString("strId", messageRemindBean.getStrAffirId());
                    bundle.putBoolean("isFromRemindList", true);
                    if (isShowSign)
                        showActivity(activity, MeetAct_SignUpActivity.class, bundle, 2);
                    break;
                case 4:
                    bundle = new Bundle();
                    bundle.putString("strId", messageRemindBean.getStrAffirId());
                    bundle.putInt("sign", DefineUtil.sqmy);
                    bundle.putString("strTitle", "????????????");
                    strButtonJSFunction = messageRemindBean.getStrButtonJSFunction();
                    if ("showInfoAlly".equals(strButtonJSFunction)) {
                        showActivity(activity, Proposal_JoinAffirmActivity.class, bundle, 4);
                    }
                    break;
                default:
                    break;

            }
        }

    }
}
