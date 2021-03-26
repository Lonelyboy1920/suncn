package com.suncn.ihold_zxztc.activity.application.meeting;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.suncn.ihold_zxztc.BaseInterface;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.MeetAct_Main_RVAdapter;
import com.suncn.ihold_zxztc.bean.MeetingActListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.HashMap;

/**
 * 次会列表Activity
 */
public class Meet_ChildListActivity extends BaseActivity implements BaseInterface {
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

    private MeetAct_Main_RVAdapter adapter;
    private int curPage = 1;
    private String strKeyValue = "";
    private String headTitle; // 标题名称
    private int sign;
    private String strId;//会议id 获取次会用

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                case 1:
                    getChildList(1, true);
                    break;
                case 2:
                    getChildList(1, false);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //刷新
        if (DefineUtil.isNeedRefresh) {
            getChildList(1, true);
            DefineUtil.isNeedRefresh = false;
        }
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
        goto_Button.setVisibility(View.GONE);
        goto_Button.setText(getResources().getString(R.string.font_search));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            headTitle = bundle.getString("headTitle");
            sign = bundle.getInt("sign");
            setHeadTitle(headTitle);
            strId = bundle.getString("strId");
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getChildList(1, true);
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
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    strKeyValue = etSearch.getText().toString();
                    if (!GIStringUtil.isNotBlank(strKeyValue)) {
                        GIUtil.closeSoftInput(activity);
                    }
                    getChildList(1, false);
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
                getChildList(1, false);
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        recyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getChildList(1, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getChildList(curPage + 1, false);
            }
        });
        adapter =  new MeetAct_Main_RVAdapter(R.layout.item_rv_meetact_child, activity,DefineUtil.zhxx);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                String strId = "";
                Bundle bundle = new Bundle();
                MeetingActListBean.ObjMeetingActBean myActivitiesBean = (MeetingActListBean.ObjMeetingActBean) adapter.getItem(position);
                strId = myActivitiesBean.getStrId();
                bundle.putString("strId", strId);
                bundle.putInt("sign", sign);
                bundle.putBoolean("onlyShowNormalInfo", false);
                bundle.putString("headTitle", getString(R.string.string_meeting_detail));
                bundle.putBoolean("isFromList", true);
                bundle.putBoolean("isManager", true);
                showActivity(activity, MeetAct_DetailActivity.class, bundle, 2);
            }
        });
    }

    /**
     * 获取次会列表
     */
    private void getChildList(int curPage, boolean b) {
        this.curPage = curPage;
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("strKeyValue", strKeyValue);
        textParamMap.put("strId", strId);
        if (sign == DefineUtil.hygl) {
            doRequestNormal(ApiManager.getInstance().getMangerChildMeetingList(textParamMap), 0);
        } else {
            doRequestNormal(ApiManager.getInstance().getChildMeetingList(textParamMap), 0);
        }
    }

    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        try {
            switch (what) {
                case 0:
                    MeetingActListBean meetingActListBean = (MeetingActListBean) object;
                    if (curPage == 1) {
                        adapter.setList(meetingActListBean.getObjList());
                    } else {
                        adapter.addData(meetingActListBean.getObjList());
                    }
                    Utils.setLoadMoreViewState(meetingActListBean.getIntAllCount(),curPage*DefineUtil.GLOBAL_PAGESIZE,strKeyValue,refreshLayout,adapter);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getResources().getString(R.string.data_error));
        }
    }

    @Override
    public void setOnClick(Object object, View view, int type) {
        MeetingActListBean.ObjMeetingActBean myActivitiesBean = (MeetingActListBean.ObjMeetingActBean) object;
        Bundle bundle;
        switch (view.getId()) {
            case R.id.tv_state:
                if (myActivitiesBean.getStrState().equals("3")) {//报名
                    bundle = new Bundle();
                    if (type == DefineUtil.wdhd) {
                        bundle.putString("headTitle", getString(R.string.string_event_registration));
                    } else {
                        bundle.putString("headTitle", getString(R.string.string_meeting_registration));
                    }
                    bundle.putInt("sign", type);
                    bundle.putString("strName", myActivitiesBean.getStrName());
                    bundle.putString("strStartDate", myActivitiesBean.getStrStartDate());
                    bundle.putString("strEndDate", myActivitiesBean.getStrEndDate());
                    bundle.putString("strPlace", myActivitiesBean.getStrPlace());
                    bundle.putString("strId", myActivitiesBean.getStrId());
                    showActivity(activity, MeetAct_SignUpActivity.class, bundle, 2);
                } else {//签到
                    String strSignWay = GIStringUtil.nullToEmpty(myActivitiesBean.getStrSignWay());
                    if (strSignWay.equals("0")) {//二维码签到
                        bundle = new Bundle();
                        bundle.putString("strId", myActivitiesBean.getStrId());//活动id
                        bundle.putString("strType", "2");//我的次会类型0
                        showActivity(activity, QRCodeSignInActivity.class, bundle, 2);
                    } else if (strSignWay.equals("1")) {//定位签到
                        String strWeiDu = ""; //纬度
                        String strJingDu = ""; // 经度
                        String strMtTime = "";
                        String strMtName = "";
                        String strMtId = "";
                        String strLocationSignDistance = "";
                        strMtId = myActivitiesBean.getStrId();
                        strJingDu = myActivitiesBean.getStrLongitude();
                        strWeiDu = myActivitiesBean.getStrLatitude();
                        strMtName = myActivitiesBean.getStrName();
                        strMtTime = myActivitiesBean.getStrStartDate();
                        strLocationSignDistance = myActivitiesBean.getStrLocationSignDistance();
                        bundle = new Bundle();
                        bundle.putString("strMtId", strMtId);
                        bundle.putString("strLocationSignDistance", strLocationSignDistance);
                        bundle.putString("strWeiDu", strWeiDu);
                        bundle.putString("strJingDu", strJingDu);
                        bundle.putString("strMtTime", strMtTime);
                        bundle.putString("strMtName", strMtName);
                        bundle.putString("strType", "2");
                        bundle.putString("headTitle", ((TextView) view).getText().toString());
                        showActivity(activity, SignInActivity.class, bundle, 2);
                    } else if (strSignWay.equals("2")) {
                        String strWeiDu = ""; //纬度
                        String strJingDu = ""; // 经度
                        String strMtTime = "";
                        String strMtName = "";
                        String strMtId = "";
                        String strLocationSignDistance = "";
                        strMtId = myActivitiesBean.getStrId();
                        strJingDu = myActivitiesBean.getStrLongitude();
                        strWeiDu = myActivitiesBean.getStrLatitude();
                        strMtName = myActivitiesBean.getStrName();
                        strMtTime = myActivitiesBean.getStrStartDate();
                        strLocationSignDistance = myActivitiesBean.getStrLocationSignDistance();
                        bundle = new Bundle();
                        bundle.putString("strMtId", strMtId);
                        bundle.putString("strLocationSignDistance", strLocationSignDistance);
                        bundle.putString("strWeiDu", strWeiDu);
                        bundle.putString("strJingDu", strJingDu);
                        bundle.putString("strMtTime", strMtTime);
                        bundle.putString("strMtName", strMtName);
                        bundle.putString("strType", "2");
                        bundle.putString("strSignWay", strSignWay);
                        bundle.putString("headTitle", ((TextView) view).getText().toString());
                        showActivity(activity, SignInActivity.class, bundle, 2);
                    }
                }
                break;
            case R.id.ll_child_detail://次会详情
                bundle = new Bundle();
                bundle.putString("headTitle", getString(R.string.string_second_meeting_info));
                bundle.putBoolean("isChildMt", true);
                bundle.putInt("sign", DefineUtil.zhxx);
                bundle.putString("strId", myActivitiesBean.getStrId());
                showActivity(activity, Meet_ChildListActivity.class, bundle);
                break;
        }
    }
}
