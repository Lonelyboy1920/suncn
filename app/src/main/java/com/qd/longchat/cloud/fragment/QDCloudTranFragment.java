package com.qd.longchat.cloud.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.cloud.adapter.QDCloudTranPagerAdapter;
import com.qd.longchat.fragment.QDBaseFragment;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/3 上午10:37
 */
public class QDCloudTranFragment extends QDBaseFragment {

    @BindView(R2.id.view_tran_title)
    View viewTitle;
    @BindView(R2.id.tl_tran_layout)
    TabLayout tbTabLayout;
    @BindView(R2.id.vp_tran_pager)
    ViewPager vpViewPager;

    @BindString(R2.string.cloud_tran)
    String strTitle;
    @BindString(R2.string.cloud_download_list)
    String strDownloadList;
    @BindString(R2.string.cloud_upload_list)
    String strUploadList;

    private String[] tabTitles;
    private QDCloudTranPagerAdapter pagerAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_tran, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);

        tabTitles = new String[]{strDownloadList, strUploadList};
        pagerAdapter = new QDCloudTranPagerAdapter(context, tabTitles);
        vpViewPager.setAdapter(pagerAdapter);
        tbTabLayout.setupWithViewPager(vpViewPager);
    }

    public void update() {
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();
        }
    }
}
