package com.qd.longchat.acc.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.manager.QDAccManager;
import com.longchat.base.model.gd.QDAccHistoryModel;
import com.longchat.base.model.gd.QDAccModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.acc.adapter.QDAccHistoryAdapter;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.util.QDIntentKeyUtil;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/26 下午5:12
 */
public class QDAccHistoryActivity extends QDBaseActivity {

    @BindView(R2.id.view_history_title)
    View viewTitle;
    @BindView(R2.id.lv_history_list)
    PullToRefreshListView listView;

    @BindString(R2.string.pull_to_refresh)
    String strPullToRefresh;
    @BindString(R2.string.str_loading)
    String strLoading;
    @BindString(R2.string.release_refresh)
    String strReleaseRefresh;

    private QDAccModel model;
    private QDAccHistoryAdapter adapter;
    private int pageIndex = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_history);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        model = (QDAccModel) getIntent().getSerializableExtra(QDIntentKeyUtil.INTENT_ACC);
        tvTitleName.setText(model.getName());
        adapter = new QDAccHistoryAdapter(context, model);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);

        getData();

        ILoadingLayout endLabels = listView.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel(strPullToRefresh);// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel(strLoading);// 刷新时
        endLabels.setReleaseLabel(strReleaseRefresh);// 下来达到一定距离时，显示的提示

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<SwipeMenuListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
                pageIndex++;
                listView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                        listView.onRefreshComplete();
                    }
                }, 1000);
            }
        });
    }

    private void getData() {
        QDAccManager.getInstance().getAccHistory(model.getId(), pageIndex, new QDResultCallBack<List<QDAccHistoryModel>>() {
            @Override
            public void onError(String errorMsg) {

            }

            @Override
            public void onSuccess(List<QDAccHistoryModel> modelList) {
                if (modelList.size() < 20) {
                    adapter.setEnd(true);
                    listView.setMode(PullToRefreshBase.Mode.DISABLED);
                } else {
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                }
                adapter.addModeList(modelList);
            }
        });
    }
}
