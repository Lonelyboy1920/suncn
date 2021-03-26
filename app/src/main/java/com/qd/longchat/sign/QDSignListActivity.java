package com.qd.longchat.sign;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.manager.QDSignManager;
import com.longchat.base.model.gd.QDSignModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.sign.adapter.QDSignViewPagerAdapter;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/24 下午3:40
 */
public class QDSignListActivity extends QDBaseActivity {

    @BindView(R2.id.view_group_title)
    View viewTitle;
    @BindView(R2.id.tl_group_layout)
    TabLayout tlTabLayout;
    @BindView(R2.id.vp_group_pager)
    ViewPager viewPager;
    @BindView(R2.id.ll_group_parent_layout)
    LinearLayout llPatentLayout;

    @BindString(R2.string.sign_sign)
    String strTitle;
    @BindString(R2.string.sign_self)
    String strSelf;
    @BindString(R2.string.sign_report)
    String strReport;

    private String[] tabTitles;
    private List<QDSignModel.ListBean> mineRecord;
    private List<QDSignModel.ListBean> reportRecord;
    private QDSignViewPagerAdapter adapter;

    private QDResultCallBack<QDSignModel> callBack = new QDResultCallBack<QDSignModel>() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, "获取记录失败：" + errorMsg);
        }

        @Override
        public void onSuccess(QDSignModel qdSignModel) {
            QDSignManager.getInstance().getRecordReport(callBack1);
            mineRecord = qdSignModel.getList();
        }
    };

    private QDResultCallBack<QDSignModel> callBack1 = new QDResultCallBack<QDSignModel>() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, "获取记录失败：" + errorMsg);
        }

        @Override
        public void onSuccess(QDSignModel qdSignModel) {
            reportRecord = qdSignModel.getList();
            adapter.setMineList(mineRecord);
            adapter.setReportList(reportRecord);
            adapter.notifyDataSetChanged();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_list);
        ButterKnife.bind(this);
        int index = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_INDEX, 0);
        llPatentLayout.setBackgroundColor(getResources().getColor(R.color.colorActivityBackground));
        initTitleView(viewTitle);

        tabTitles = new String[]{strSelf, strReport};

        QDSignManager.getInstance().getMineRecord(callBack);

        adapter = new QDSignViewPagerAdapter(context, tabTitles);
        viewPager.setAdapter(adapter);
        tlTabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(index);
    }

}
