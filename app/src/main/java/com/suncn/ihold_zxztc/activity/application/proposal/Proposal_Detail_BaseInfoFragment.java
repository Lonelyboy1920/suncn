package com.suncn.ihold_zxztc.activity.application.proposal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gavin.giframe.ui.BindView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.suncn.ihold_zxztc.BaseInterface;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.adapter.Proposal_Detail_BaseInfo_RVAdapter;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 提案详情中的基本信息Fragment
 */
public final class Proposal_Detail_BaseInfoFragment extends BaseFragment implements BaseInterface {
    @BindView(id = R.id.rl_head)
    private RelativeLayout head_Layout;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;//基本信息EasyRecyclerView
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;
    private static ArrayList<String> baseInfoList; // 基本信息集合
    private Proposal_Detail_BaseInfo_RVAdapter adapter;
    private static String strId;

    public static Proposal_Detail_BaseInfoFragment newInstance(ArrayList<String> mBaseInfoList, String id) {
        Proposal_Detail_BaseInfoFragment baseInfoFragment = new Proposal_Detail_BaseInfoFragment();
        baseInfoList = mBaseInfoList;
        strId = id;
        return baseInfoFragment;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.activity_recyclerview_nopadding, null);
    }

    @Override
    public void initData() {
        super.initData();
        head_Layout.setVisibility(View.GONE);
        recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.main_bg));
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0.5f, 0);
        adapter = new Proposal_Detail_BaseInfo_RVAdapter(this);
        adapter.setList(baseInfoList);
        refreshLayout.setEnableLoadMore(false);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void setOnClick(Object object, View view, int type) {
        Bundle bundle;
        switch (type) {
            case 0://附议
                bundle = new Bundle();
                bundle.putString("headTitle", getString(R.string.string_second_human));
                bundle.putString("strId", strId);
                bundle.putString("strType", "3");
                showActivity(fragment, Proposal_JoinSupportListActivity.class, bundle);
                break;
            case 1://联名
                bundle = new Bundle();
                bundle.putString("headTitle", getString(R.string.string_associate));
                bundle.putString("strType", "2");
                bundle.putString("strId", strId);
                showActivity(fragment, Proposal_JoinSupportListActivity.class, bundle);
                break;
        }
    }
}
