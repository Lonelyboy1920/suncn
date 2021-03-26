package com.suncn.ihold_zxztc.activity.message;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.OverSearchListActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.Message_Notice_RVAdapter;
import com.suncn.ihold_zxztc.bean.NoticeAnnouncementListBean;
import com.suncn.ihold_zxztc.bean.ObjAnnouncementBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 通知公告列表
 */
public class Message_NoticeActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;

    private int curPage = 1;
    private Message_Notice_RVAdapter adapter;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_recyclerview_nopadding);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("通知公告");
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getResources().getString(R.string.font_search));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        recyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
        initRecyclerView();
        getListData(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto:
                Bundle bundle = new Bundle();
                bundle.putInt("searchType", 2);
                showActivity(activity, OverSearchListActivity.class, bundle);
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    /**
     * 委员列表
     */
    private void getListData(boolean isTrue) {
        if (isTrue)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        doRequestNormal(ApiManager.getInstance().getNoticeAnnouncementList(textParamMap), 0);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getListData(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage++;
                getListData(false);
            }
        });

        adapter = new Message_Notice_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter1, @NonNull View view, int position) {
                ObjAnnouncementBean memListBean = (ObjAnnouncementBean) adapter.getItem(position);
                if ("0".equals(memListBean.getStrState())) {
                    memListBean.setStrState("1");
                    adapter.setData(position, memListBean);
                }
                String strShowAddMeetSpeak = "";
                if ("3".equals(memListBean.getStrSorceType()) && 0 == GISharedPreUtil.getInt(activity, "intUserRole")) {
                    if (!ProjectNameUtil.isGZSZX(activity)) {   //贵州政协 屏蔽发布会议发言
                        strShowAddMeetSpeak = "1";
                    }
                }
                String strId = Utils.getShowContent(memListBean.getStrAffirId(), memListBean.getStrId());
                Bundle bundle = new Bundle();
                bundle.putString("strUrl", memListBean.getStrUrl());
                bundle.putString("headTitle", memListBean.getStrTopTitle());
                bundle.putString("strShowAddMeetSpeak", strShowAddMeetSpeak);
                bundle.putString("strMeetSpeakNoticeId", strId);
                bundle.putBoolean("isPersonInfo", true);
                showActivity(activity, WebViewActivity.class, bundle);
            }
        });
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    NoticeAnnouncementListBean result = (NoticeAnnouncementListBean) obj;
                    List<NoticeAnnouncementListBean.AnnouncementListBean> objList = result.getObjList();
                    List<ObjAnnouncementBean> myList = new ArrayList<>();
                    for (int i = 0; i < objList.size(); i++) {
                        objList.get(i).getObjNoticeList().get(0).setStrPubDate(objList.get(i).getStrPubDate());
                        myList.addAll(objList.get(i).getObjNoticeList());
                    }
                    if (curPage == 1) {
                        adapter.setList(myList);
                    } else {
                        adapter.addData(myList);
                    }
                    Utils.setLoadMoreViewState(result.getIntAllCount(), curPage * DefineUtil.GLOBAL_PAGESIZE, "", refreshLayout, adapter);
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
            prgDialog.closePrgDialog();
        }
    }
}
