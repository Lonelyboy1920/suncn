package com.suncn.ihold_zxztc.activity.application.publicopinion;

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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.adapter.PublicOpinion_More_RVAdapter;
import com.suncn.ihold_zxztc.bean.MorePublicOpinionsListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 更多公开刊物
 */
public class PublicOpinion_MoreActivity extends BaseActivity {
    @BindView(id = R.id.ll_title) //标题
    private LinearLayout llTitle;
    @BindView(id = R.id.ll_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
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
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    private int curPage = 1;
    private String strKeyValue = ""; // 搜索关键字
    private PublicOpinion_More_RVAdapter adapter;
    private String headTitle;
    private ArrayList<String> yearArray = new ArrayList<>();
    private String strYear = "";//年份
    private SpinerPopWindow<String> mSpinerPopWindow;
    public ArrayList<SessionListBean.YearInfo> yearList; // 年份集合

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_recyclerview_nopadding);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_public_publication));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            yearList = (ArrayList<SessionListBean.YearInfo>) bundle.getSerializable("yearList");
            session_TextView.setText(yearList.get(0).getStrYear());
            strYear = yearList.get(0).getStrYear();
//            yearArray = GIDateUtils.getYearStringArray(yearList.size());
            for (SessionListBean.YearInfo yearInfo : yearList) {
                yearArray.add(yearInfo.getStrYear());
            }
            mSpinerPopWindow = new SpinerPopWindow<String>(activity, yearArray, itemClickListener);
        }
        llSearchAttend.setVisibility(View.VISIBLE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getThemeList(true);
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
            curPage = 1;
            getThemeList(false);
        }
    };

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        recyclerView.setBackgroundColor(getResources().getColor(R.color.main_bg));
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                curPage = 1;
                getThemeList(false);
            }
        });
        adapter = new PublicOpinion_More_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                MorePublicOpinionsListBean.MorePublicOpinionBean themeBean = (MorePublicOpinionsListBean.MorePublicOpinionBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("headTitle", "公开刊物");
                bundle.putString("strUrl", Utils.formatFileUrl(activity, themeBean.getStrUrl()));
                showActivity(activity, WebViewActivity.class, bundle);
            }
        });
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
                if (GIStringUtil.isNotBlank(etSearch.getText().toString())) {
                    etSearch.setText("");
                }
                break;
            case R.id.tv_search:
                llSearchTop.setVisibility(View.VISIBLE);
                search_TextView.setVisibility(View.GONE);
                tvCancelTop.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_session:
                mSpinerPopWindow.setWidth(GIDensityUtil.dip2px(activity, session_LinearLayout.getWidth()));
                mSpinerPopWindow.setHeight(session_TextView.getHeight() * yearList.size());
                mSpinerPopWindow.showAsDropDown(session_TextView);
                break;
            case R.id.tv_cancel_1:
                llSearchTop.setVisibility(View.INVISIBLE);
                search_TextView.setVisibility(View.VISIBLE);
                tvCancelTop.setVisibility(View.GONE);
                if (GIStringUtil.isNotBlank(etSearchTop.getText().toString())) {
                    etSearchTop.setText("");
                    curPage = 1;
                    getThemeList(false);
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
                getThemeList(false);
            }
        });
    }

    /**
     * 获取列表
     */
    private void getThemeList(boolean b) {
        if (b)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        textParamMap.put("strKeyValue", strKeyValue);
        textParamMap.put("strYear", strYear);
        doRequestNormal(ApiManager.getInstance().getMorePubublicOpinionList(textParamMap), 0);
    }

    private void doLogic(Object object, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    MorePublicOpinionsListBean result = (MorePublicOpinionsListBean) object;
                    List<MorePublicOpinionsListBean.MorePublicOpinionBean> objList = result.getObjList();
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
            showToast(toastMessage);
    }
}
