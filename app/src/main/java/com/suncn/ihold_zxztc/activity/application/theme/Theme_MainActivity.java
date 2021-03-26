package com.suncn.ihold_zxztc.activity.application.theme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.hot.NewsDetailActivity;
import com.suncn.ihold_zxztc.adapter.Theme_Hot_RVAdapter;
import com.suncn.ihold_zxztc.adapter.Theme_RVAdapter;
import com.suncn.ihold_zxztc.bean.ThemeListBean;
import com.suncn.ihold_zxztc.utils.DividerDecoration;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 主题议政
 */
public class Theme_MainActivity extends BaseActivity {
    @BindView(id = R.id.toprecyclerView)
    private RecyclerView topRecyclerView;
    @BindView(id = R.id.tv_more, click = true)
    private TextView more_TextView;//推荐更多
    private Theme_Hot_RVAdapter hotAdapter;//热门
    private Theme_RVAdapter adapter;
    private String strHeadTitle;
    private ArrayList<ThemeListBean.ThemeBean> objList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) { // 从热门主题进入的回来后每次都进行刷新操作，无需做resultCode == RESULT_OK判断
            getThemeList(false);
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_theme_list);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strHeadTitle = bundle.getString("headTitle");
            setHeadTitle(strHeadTitle);
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        adapter = new Theme_RVAdapter(activity);
        initRecyclerView();
        getThemeList(true);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        DividerDecoration itemDecoration = new DividerDecoration(getResources().getColor(R.color.line_bg_white), GIDensityUtil.dip2px(activity, 0), 0, 0);//颜色 & 高度 & 左边距 & 右边距
        itemDecoration.setDrawLastItem(false);//设置最后一个item有分割线,默认true.
        itemDecoration.setDrawHeaderFooter(false);//设置分割线是否对Header和Footer有效,默认false.
        topRecyclerView.addItemDecoration(itemDecoration);
        topRecyclerView.setLayoutManager(new LinearLayoutManager(activity)); //给ERV添加布局管理器
        /*topRecyclerView.setEmptyView(R.layout.view_erv_empty); // 设置无数据时的view
        topRecyclerView.setErrorView(R.layout.view_erv_error); // 设置加载异常时的view*/
        hotAdapter = new Theme_Hot_RVAdapter(this);
        topRecyclerView.setAdapter(hotAdapter);
        hotAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ThemeListBean.ThemeBean themeBean = (ThemeListBean.ThemeBean) hotAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strNewsId", themeBean.getStrId());
                bundle.putString("headTitle", strHeadTitle + getString(R.string.string_detail));
                bundle.putString("strUrl", Utils.formatFileUrl(activity, themeBean.getStrUrl()));
                if ("1".equals(themeBean.getStrViewPermissions())) {
                    showActivity(activity, NewsDetailActivity.class, bundle, 0);
                } else {
                    showToast(getString(R.string.string_you_have_no_permission_to_look_this_theme));
                }
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
        doRequestNormal(ApiManager.getInstance().getThemeList(textParamMap), 0);
    }

    private void doLogic(Object object, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    ThemeListBean result = (ThemeListBean) object;
                    ArrayList<ThemeListBean.ThemeBean> hotList = result.getHotSubjectList();
                    objList = result.getSubjectList();
                    hotAdapter.setList(hotList);
                    hotAdapter.removeAllFooterView();
                    hotAdapter.addFooterView(getFootView());
                    // }
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

    private View getFootView() {
        View footView = getLayoutInflater().inflate(R.layout.view_footer_theme, topRecyclerView, false);
        LinearLayout tjzt_LinearLayout = footView.findViewById(R.id.ll_tjzt);
        RecyclerView recyclerView = footView.findViewById(R.id.recyclerView);
        more_TextView = footView.findViewById(R.id.tv_more);
        more_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("headTitle", strHeadTitle);
                showActivity(activity, Theme_ListActivity.class, bundle);
            }
        });
        int padding5 = GIDensityUtil.dip2px(activity, 5);
        DividerDecoration itemDecoration = new DividerDecoration(getResources().getColor(R.color.line_bg_white), GIDensityUtil.dip2px(activity, 0.5f), padding5, padding5);//颜色 & 高度 & 左边距 & 右边距
        itemDecoration.setDrawLastItem(false);//设置最后一个item有分割线,默认true.
        itemDecoration.setDrawHeaderFooter(false);//设置分割线是否对Header和Footer有效,默认false.
        recyclerView.addItemDecoration(itemDecoration);
        //完成layoutManager设置
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        adapter.setList(objList);
        if (objList != null && objList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ThemeListBean.ThemeBean themeBean = (ThemeListBean.ThemeBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString("strNewsId", themeBean.getStrId());
                bundle.putString("headTitle", strHeadTitle + "详情");
                bundle.putString("strUrl", Utils.formatFileUrl(activity, themeBean.getStrUrl()));
//                    bundle.putString("strDateState", themeBean.getStrDateState());
                showActivity(activity, NewsDetailActivity.class, bundle);
            }
        });
        return footView;
    }

}
