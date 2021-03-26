package com.suncn.ihold_zxztc.activity.application.meeting;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GITextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.MeetSpeak_Main_RVAdapter;
import com.suncn.ihold_zxztc.bean.MeetSpeakListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 会议发言Activity
 */
public class MeetSpeak_MainActivity extends BaseActivity {
    @BindView(id = R.id.btn_count)
    private GITextView count_Button; // 右边操作按钮角标数字
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
    private MeetSpeak_Main_RVAdapter adapter;
    private int curPage = 1;
    private String strKeyValue = "";
    private String headTitle = "";
    private ArrayList<String> yearArray = new ArrayList<>();
    private String strYear = "";//年份
    private SpinerPopWindow<String> mSpinerPopWindow;
    public ArrayList<SessionListBean.YearInfo> yearList; // 年份集合

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_recyclerview_nopadding);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isRefreshSpeakNotice = GISharedPreUtil.getBoolean(activity, "isRefreshSpeakNotice", false);
        if (isRefreshSpeakNotice) {
            getMeetSpeakList(1, false);
            GISharedPreUtil.setValue(activity, "isRefreshSpeakNotice", false);
        }
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            headTitle = bundle.getString("headTitle");
            yearList = (ArrayList<SessionListBean.YearInfo>) bundle.getSerializable("yearList");
            session_TextView.setText(yearList.get(0).getStrYear());
            strYear = yearList.get(0).getStrYear();
            for (SessionListBean.YearInfo yearInfo : yearList) {
                yearArray.add(yearInfo.getStrYear());
            }
            mSpinerPopWindow = new SpinerPopWindow<String>(activity, yearArray, itemClickListener);
            setHeadTitle(headTitle);
        }
        llSearchAttend.setVisibility(View.VISIBLE);
        recyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setTextSize(24);
        goto_Button.setTextColor(getResources().getColor(R.color.view_head_bg));
        goto_Button.setText(getResources().getString(R.string.font_notice));

        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getMeetSpeakList(1, true);
    }

    /**
     * popupwindow显示的ListView的item点击事件(年份)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            session_TextView.setText(yearArray.get(position));
            strYear = yearArray.get(position);
            getMeetSpeakList(1, false);
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                showActivity(activity, MeetSpeak_NoticeListActivity.class);
                break;
            case R.id.tv_search:
                llSearchTop.setVisibility(View.VISIBLE);
                search_TextView.setVisibility(View.GONE);
                tvCancelTop.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_session:
                mSpinerPopWindow.setWidth(session_LinearLayout.getWidth()*2);
                mSpinerPopWindow.setHeight(session_TextView.getHeight() * yearList.size());
                mSpinerPopWindow.showAsDropDown(session_TextView);
                break;
            case R.id.tv_cancel_1:
                llSearchTop.setVisibility(View.INVISIBLE);
                search_TextView.setVisibility(View.VISIBLE);
                tvCancelTop.setVisibility(View.GONE);
                if (GIStringUtil.isNotBlank(etSearchTop.getText().toString())) {
                    etSearchTop.setText("");
                    getMeetSpeakList(1, false);
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
        etSearchTop.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (GIStringUtil.isBlank(strKeyValue)) {
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
        adapter = new MeetSpeak_Main_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                MeetSpeakListBean.MeetSpeakBean speakBean = (MeetSpeakListBean.MeetSpeakBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("headTitle", headTitle);
                bundle.putString("strUrl", speakBean.getStrUrl());
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
        textParamMap.put("strYear", strYear);
        doRequestNormal(ApiManager.getInstance().getMeetSpeakList(textParamMap), 0);
    }

    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        try {
            switch (what) {
                case 0:
                    MeetSpeakListBean meetSpeakListBean = (MeetSpeakListBean) object;
                    if (0 < meetSpeakListBean.getIntNotReadCount()) {
                        count_Button.setVisibility(View.VISIBLE);
                        count_Button.setText(meetSpeakListBean.getIntNotReadCount() + "");
                    } else {
                        count_Button.setVisibility(View.INVISIBLE);
                    }
                    Utils.setLoadMoreViewState(meetSpeakListBean.getIntAllCount(), curPage, strKeyValue, refreshLayout, adapter);
                    if (curPage == 1) {
                        adapter.setList(meetSpeakListBean.getObjList());
                    } else {
                        adapter.addData(meetSpeakListBean.getObjList());
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getResources().getString(R.string.data_error));
        }
    }
}
