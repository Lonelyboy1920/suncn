package com.suncn.ihold_zxztc.activity.application.meeting;

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
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.MeetSpeak_Notice_RVAdapter;
import com.suncn.ihold_zxztc.bean.MeetSpeakNoticeListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * 会议发言征集通知Activity
 */
public class MeetSpeak_NoticeListActivity extends BaseActivity {
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

    private MeetSpeak_Notice_RVAdapter adapter;
    private int curPage = 1;
    private String strKeyValue = "";
    private String headTitle = "";

    @Override
    public void onResume() {
        super.onResume();
        boolean isRefreshSpeakNotice = GISharedPreUtil.getBoolean(activity, "isRefreshSpeakNotice", false);
        if (isRefreshSpeakNotice) {
            getMeetSpeakList(1, false);
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
        setHeadTitle("征集通知");
        recyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getResources().getString(R.string.font_search));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getMeetSpeakList(1, true);
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
                getMeetSpeakList(1, false);
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getMeetSpeakList(1, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getMeetSpeakList(curPage + 1, false);
            }
        });
        adapter = new MeetSpeak_Notice_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                MeetSpeakNoticeListBean.MeetSpeakNoticeBean speakBean = (MeetSpeakNoticeListBean.MeetSpeakNoticeBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("headTitle", "征集通知");
                bundle.putString("strUrl", speakBean.getStrUrl());
                bundle.putString("strMeetSpeakNoticeId", speakBean.getStrMeetSpeakNoticeId());
                bundle.putString("strShowAddMeetSpeak", "1");
                showActivity(activity, WebViewActivity.class, bundle);
            }
        });
    }

    /**
     * 获取发言列表
     */
    private void getMeetSpeakList(int curPage, boolean b) {
        this.curPage = curPage;
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("strKeyValue", strKeyValue);
        doRequestNormal(ApiManager.getInstance().getMeetSpeakNoticeList(textParamMap), 0);
    }

    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        try {
            switch (what) {
                case 0:
                    MeetSpeakNoticeListBean meetSpeanNoticeList = (MeetSpeakNoticeListBean) object;
                    Utils.setLoadMoreViewState(meetSpeanNoticeList.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, adapter);
                    if (curPage == 1) {
                        adapter.setList(meetSpeanNoticeList.getObjList());
                    } else {
                        adapter.addData(meetSpeanNoticeList.getObjList());
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getResources().getString(R.string.data_error));
        }
    }
}
