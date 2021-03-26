package com.suncn.ihold_zxztc.activity.plenarymeeting;

import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gavin.giframe.ui.BindView;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.HYRCListAdapter;
import com.suncn.ihold_zxztc.adapter.MyViewPagerAdapter;
import com.suncn.ihold_zxztc.bean.HYRCListBean;
import com.suncn.ihold_zxztc.bean.NewsColumnListBean;
import com.suncn.ihold_zxztc.bean.TabBean;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 会议日程列表
 */
public class HYRCListActivity extends BaseActivity {
    @BindView(id = R.id.tabLayout)
    private TabLayout tabLayout;
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    private ArrayList<TabBean> tabList = new ArrayList<>();
    private ArrayList<View> myViews;
    private ArrayList<HYRCListAdapter> adapters;
    private MyViewPagerAdapter viewPagerAdapter;
    private String[] typeString;
    private String strDate = "2019-09-09";
    private int currentIndex = 0;
    private ArrayList<Boolean> isNotLoads;

    @Override
    public void setRootView() {
        setStatusBar();
        isShowBackBtn = true;
        setContentView(R.layout.activity_hyrc_list);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        setHeadTitle("会议日程");
        getDateList();
    }

    /**
     * 获取时间
     */
    private void getDateList() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().MeetingScheduleServlet(textParamMap), 0);
    }

    /**
     * 获取日程
     */
    private void getListData() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strDate", strDate);
        doRequestNormal(ApiManager.getInstance().MeetingAgendaServlet(textParamMap), 1);
    }

    private void doLogic(int what, Object object) {
        prgDialog.closePrgDialog();
        try {
            switch (what) {
                case 0:
                    NewsColumnListBean newsColumnListBean = (NewsColumnListBean) object;
                    typeString = new String[newsColumnListBean.getObjList().size()];
                    setTabName(newsColumnListBean.getObjList());
                    initTab();
                    getListData();
                    break;
                case 1:
                    HYRCListBean hyrcListBean = (HYRCListBean) object;
                    View view = myViews.get(currentIndex);
                    RecyclerView easyRecyclerView = view.findViewById(R.id.recyclerView);
                    HYRCListAdapter adapter = adapters.get(currentIndex);
                    if (adapter == null) {
                        adapter = new HYRCListAdapter(activity);
                        easyRecyclerView.setAdapter(adapter);
                    }
                    adapter.setList(hyrcListBean.getObjList());
                    adapter.notifyDataSetChanged();
                    if (isNotLoads != null && isNotLoads.size() > 0)
                        isNotLoads.set(currentIndex, true);
                    break;
            }

        } catch (Exception e) {
            showToast(getResources().getString(R.string.data_error));
        }

    }

    private void setTabName(List<NewsColumnListBean.NewsColumnBean> list) {
        tabList.clear();
        for (int i = 0; i < list.size(); i++) {
            TabBean tabBean = new TabBean();
            tabBean.setName(list.get(i).getStrDateOrder());
            tabBean.setType(list.get(i).getStrFormatDate());
            tabBean.setValue(list.get(i).getStrDate());
            tabList.add(tabBean);
        }
    }

    /**
     * 初始化tab栏
     */
    private void initTab() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        myViews = new ArrayList<>();
        adapters = new ArrayList<>();
        isNotLoads = new ArrayList<>();
        for (int i = 0; i < tabList.size(); i++) {
            currentIndex = 0;
            initAddView();
        }
        strDate = tabList.get(0).getValue();
        viewPagerAdapter = new MyViewPagerAdapter(myViews);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).select();
        //设置tab的item布局样式
        for (int i = 0; i < tabList.size(); i++) {
            MyTabLayout.Tab tab = tabLayout.getTabAt(i);
            View inflate = View.inflate(activity, R.layout.view_tab_hyrc, null);
            TextView tvTitle = inflate.findViewById(R.id.tv_title);
            TextView tvTitle1=inflate.findViewById(R.id.tv_title1);
            LinearLayout llTab = inflate.findViewById(R.id.ll_tab);
            TextView tvIcon = inflate.findViewById(R.id.tv_select_icon);
            tvTitle.setText(tabList.get(i).getName());
            tvTitle1.setText(tabList.get(i).getType());
            tvTitle.setTextColor(getResources().getColor(R.color.font_content));

            //设置第一条tab的选中样式
            if (i == 0) {
                llTab.setBackground(getResources().getDrawable(R.mipmap.tab_choose));
                tvTitle.setTextColor(getResources().getColor(R.color.view_head_bg));
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_mini));
                tvIcon.setVisibility(View.VISIBLE);
            } else {
                llTab.setBackground(getResources().getDrawable(R.mipmap.tab_unchoose));
                tvTitle.setTextColor(getResources().getColor(R.color.font_content));
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_8));
                tvIcon.setVisibility(View.INVISIBLE);
            }
            tab.setCustomView(inflate);
        }
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
        viewPager.setCurrentItem(0);
    }

    /**
     * tab切换效果实现
     */
    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            view.findViewById(R.id.tv_select_icon).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ll_tab).setBackground(getResources().getDrawable(R.mipmap.tab_choose));
            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.view_head_bg));
            ((TextView) view.findViewById(R.id.tv_title)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_mini));
            viewPager.setCurrentItem(tab.getPosition());
            currentIndex = tab.getPosition();
            strDate = tabList.get(currentIndex).getValue();
            if (isNotLoads.size() == 0 || !isNotLoads.get(currentIndex) ) {
                getListData();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            view.findViewById(R.id.tv_select_icon).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.ll_tab).setBackground(getResources().getDrawable(R.mipmap.tab_unchoose));
            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.font_content));
            ((TextView) view.findViewById(R.id.tv_title)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_8));
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    /**
     * 初始化列表页面
     */
    private void initAddView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_viewpager_add_view, null);
        HYRCListAdapter adapter = new HYRCListAdapter(activity);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter.setEmptyView(R.layout.view_erv_empty);
        recyclerView.setAdapter(adapter);
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 0, 0);
        adapters.add(adapter);
        myViews.add(view);

    }

}
