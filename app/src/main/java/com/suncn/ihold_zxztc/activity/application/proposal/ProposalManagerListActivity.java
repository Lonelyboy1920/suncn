package com.suncn.ihold_zxztc.activity.application.proposal;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.widget.GITextView;
import com.google.android.material.tabs.TabLayout;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.view.MyTabLayout;

import java.util.ArrayList;
import java.util.List;

import skin.support.content.res.SkinCompatResources;

/**
 * @author :Sea
 * Date :2020-6-12 10:06
 * PackageName:com.suncn.ihold_zxztc.activity.application.proposal
 * Desc: 提案管理
 */
public class ProposalManagerListActivity extends BaseActivity {
    @BindView(id = R.id.tabLayout)
    private MyTabLayout tabLayout;//顶部MyTabLayout
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;

    private List<String> mTabTitles;
    private List<Fragment> mFragments;
    public ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    public ArrayList<SessionListBean.YearInfo> yearList; // 年份集合

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_manager_list);
    }

    @Override
    public void initData() {
        super.initData();
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getResources().getString(R.string.font_search));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setHeadTitle(bundle.getString("headTitle"));
            sessionBeans = (ArrayList<SessionListBean.SessionBean>) bundle.getSerializable("sessionBeans");
            yearList = (ArrayList<SessionListBean.YearInfo>) bundle.getSerializable("yearList");
        }
        mTabTitles = new ArrayList<>();
        mTabTitles.add(getString(R.string.string_proposal_inquiry));
        mTabTitles.add(getString(R.string.string_situation_analysis));

        mFragments = new ArrayList<>();
        mFragments.add(ProposalManagerListFragment.newInstance());
        mFragments.add(new ProposalManagerSituationAnalysisFragment());
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTabTitles.get(position);
            }
        });
        //绑定
        tabLayout.setupWithViewPager(viewPager);
        //设置tab的item布局样式
        for (int i = 0; i < mTabTitles.size(); i++) {
            MyTabLayout.Tab tab = tabLayout.getTabAt(i);
            View inflate = View.inflate(this, R.layout.view_tab_meet, null);
            GITextView textView = inflate.findViewById(R.id.tv_tab);
            textView.setText(mTabTitles.get(i));
            textView.setTextColor(getResources().getColor(R.color.font_title));

            //设置第一条tab的选中样式
            if (i == 0) {
                setSelectedTabTextView(textView);
            }
            tab.setCustomView(inflate);
        }
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
    }

    @Override
    public void setListener() {
        super.setListener();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                Bundle bundle = new Bundle();
                bundle.putSerializable("sessionBeans", sessionBeans);
                bundle.putSerializable("yearList", yearList);
                showActivity(activity, ProposalSearchActivity.class, bundle);
                break;
        }
    }


    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
            View view = tab.getCustomView();
            setSelectedTabTextView(view.findViewById(R.id.tv_tab));
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            ((TextView) view.findViewById(R.id.tv_tab)).setTextColor(getResources().getColor(R.color.font_title));
            Drawable drawable = SkinCompatResources.getDrawable(activity, R.drawable.shape_tab_bline_select);/// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, GIDensityUtil.dip2px(activity, 5), drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ((TextView) view.findViewById(R.id.tv_tab)).setCompoundDrawables(null, null, null, null);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    /**
     * 设置选中的tab效果
     */
    private void setSelectedTabTextView(TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        Drawable drawable = SkinCompatResources.getDrawable(activity, R.drawable.shape_tab_bline_select);/// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, GIDensityUtil.dip2px(activity, 5), drawable.getMinimumWidth() * 2, drawable.getMinimumHeight());
        textView.setCompoundDrawables(null, null, null, drawable);
        textView.setCompoundDrawablePadding(4);
    }
}
