package com.suncn.ihold_zxztc.activity.duty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.MemberDuty_Rank_LVAdapter;
import com.suncn.ihold_zxztc.bean.MemRecordRankListBean;
import com.suncn.ihold_zxztc.bean.MemRecordScoreBean;
import com.suncn.ihold_zxztc.bean.MemRecordScoreBean.ScoreRankListBean.RankListBean;
import com.suncn.ihold_zxztc.rxhttp.RxDisposeManager;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;
import java.util.List;

import skin.support.content.res.SkinCompatResources;

/**
 * 履职排名列表Activity
 */
public class MemberDuty_RankListActivity extends BaseActivity {
    @BindView(id = R.id.ll_menu)
    private LinearLayout menu_Layout; // 履职排行类型
    @BindView(id = R.id.view_line)
    private View line_View; // TAB红色分割线
    @BindView(id = R.id.lv_rank)
    private RecyclerView zrcListView; // 履职排行ListView
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;

    private MemberDuty_Rank_LVAdapter adapter;
    private int curPage = 1;
    private List<MemRecordScoreBean.ScoreRankListBean> scoreRankList;
    private String strRankType;// 排行类型
    private String strYear;
    private String strUserId;
    private int intAllCount;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_memberduty_rank_list);
        isShowBackBtn = true;
    }

    @Override
    public void initData() {
        setHeadTitle(getString(R.string.string_performance_ranking));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strUserId = bundle.getString("strUserId");
            strYear = bundle.getString("strYear");
            scoreRankList = (List<MemRecordScoreBean.ScoreRankListBean>) bundle.getSerializable("scoreRankList");
            initMenu();
        }
        setRecyclerViewStyle();
        adapter = new MemberDuty_Rank_LVAdapter(activity, 1);
        zrcListView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curPage = curPage + 1;
                getListData(false);
            }
        });

        super.initData();
    }

    /**
     * 设置RecyclerView的排序方向和边距
     */
    private void setRecyclerViewStyle() {
        LinearLayoutManager layoutManagerBottom = new LinearLayoutManager(activity);
        layoutManagerBottom.setOrientation(LinearLayoutManager.VERTICAL);
        //完成layoutManager设置
        zrcListView.setLayoutManager(layoutManagerBottom);

    }

    /**
     * 刷新列表
     */
    private void refreshList(boolean isTrue) {
        curPage = 1;
        getListData(isTrue);
    }

    /**
     * 委员履职排行列表
     */
    private void getListData(boolean isTrue) {
        RxDisposeManager.get().cancelAll();
        if (isTrue)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strYear", strYear); // 年份，默认当前年
        if (GIStringUtil.isNotBlank(strUserId))
            textParamMap.put("strUserId", strUserId); // 委员用户名（非委员登录时必输）
        textParamMap.put("intCurPage", curPage + ""); // 当前页数（默认值为1）
        textParamMap.put("intPageSize", "20"); // 每页记录数（ 默认值为10）
        textParamMap.put("strRankType", strRankType); // 排行类型
        doRequestNormal(ApiManager.getInstance().getMemRecordRankList(textParamMap), 0);
    }

    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        prgDialog.closePrgDialog();
        switch (what) {
            case 0:
                try {
                    MemRecordRankListBean memRecordRankListBean = (MemRecordRankListBean) obj;
                    List<RankListBean> rankListBeans = memRecordRankListBean.getObjList();
                    intAllCount = memRecordRankListBean.getIntAllCount();
                    Utils.setLoadMoreViewState(memRecordRankListBean.getIntAllCount(), 20 * curPage, "", refreshLayout, adapter);
                    if (curPage == 1) {
                        adapter.setList(rankListBeans);
                    } else {
                        adapter.addData(rankListBeans);
                    }
                } catch (Exception e) {
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

    /**
     * 初始化履职排行界面
     */
    private void initMenu() {
        if (scoreRankList != null && scoreRankList.size() > 0) {
            MemRecordScoreBean.ScoreRankListBean scoreRankListBean;
            for (int i = 0; i < scoreRankList.size(); i++) {
                scoreRankListBean = scoreRankList.get(i);
                final TextView textView = new TextView(activity);
                textView.setText(scoreRankListBean.getStrRankName());
                textView.setTextColor(getResources().getColor(R.color.font_title));
                textView.setTextSize(16);
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, GIDensityUtil.dip2px(activity, 40));
                params.gravity = Gravity.CENTER;
                params.weight = 1;
                textView.setId(i);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        int count = menu_Layout.getChildCount();
                        for (int j = 0; j < count; j++) {
                            ((TextView) menu_Layout.getChildAt(j)).setTextColor(getResources().getColor(R.color.font_title));
                        }
                        ((TextView) view).setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                        int pos = view.getId();
                        MemRecordScoreBean.ScoreRankListBean scoreRankListBean = scoreRankList.get(pos);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(view.getWidth(), GIDensityUtil.dip2px(activity, 2));
                        params.leftMargin = view.getLeft();
                        line_View.setLayoutParams(params);
                        strRankType = scoreRankListBean.getStrRankType();
                        refreshList(true);
                    }
                });
                if (i == 0) {
                    textView.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                    strRankType = scoreRankListBean.getStrRankType();
                    refreshList(true);
                }
                menu_Layout.addView(textView, params);
            }
        }
    }
}
