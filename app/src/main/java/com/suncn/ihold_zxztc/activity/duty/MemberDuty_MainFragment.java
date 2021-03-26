package com.suncn.ihold_zxztc.activity.duty;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDateUtils;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.MainActivity;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.activity.message.Contact_DetailActivity;
import com.suncn.ihold_zxztc.adapter.MyViewPagerAdapter;
import com.suncn.ihold_zxztc.adapter.PerformDutyFragmentAdapter;
import com.suncn.ihold_zxztc.bean.ParamByTypeBean;
import com.suncn.ihold_zxztc.bean.PerformDutyBean;
import com.suncn.ihold_zxztc.interfaces.AppbarLayoutStateChange;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyTabLayout;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;
import com.suncn.ihold_zxztc.view.forscrollview.MyExListViewForScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import skin.support.content.res.SkinCompatResources;

/**
 * 委员履职
 */
public class MemberDuty_MainFragment extends BaseFragment {

    @BindView(id = R.id.ll_submit_proposal, click = true)
    private LinearLayout submitProposal_LinearLayout;//提案提交
    @BindView(id = R.id.ll_submit_opinion, click = true)
    private LinearLayout submitOpinion_LinearLayout;//社情民意提交
    @BindView(id = R.id.ll_submit_file, click = true)
    private LinearLayout submitFile_LinearLayout;//履职材料提交
    @BindView(id = R.id.ll_feedback_meet_act, click = true)
    private LinearLayout feedbackMeetAct_LinearLayout;//反馈会议活动

    @BindView(id = R.id.layout_appbar)
    private AppBarLayout appBarLayout;
    @BindView(id = R.id.ctb)
    private CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(id = R.id.toolbar)
    private Toolbar toolbar;
    @BindView(id = R.id.tabLayout)
    private TabLayout tabLayout;
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    @BindView(id = R.id.tv_title)
    private TextView tvTitle;
    @BindView(id = R.id.tv_year)
    private GITextView tvYear; // 年份选择按钮
    @BindView(id = R.id.ll_year, click = true)
    private LinearLayout llYear;
    @BindView(id = R.id.tv_score)
    private TextView tvScore;
    @BindView(id = R.id.tv_score_type)
    private TextView tvScoreType;
    @BindView(id = R.id.iv_person_head, click = true)
    private RoundImageView ivPersonHead;
    @BindView(id = R.id.tv_name)
    private TextView tvName;
    @BindView(id = R.id.tv_duty)
    private TextView tvDuty;
    @BindView(id = R.id.ll_score, click = true)
    private LinearLayout rlScore;
    @BindView(id = R.id.ll_feedback, click = true)
    private LinearLayout llFeedback;
    @BindView(id = R.id.tv_close, click = true)
    private TextView tvClose;
    private List<ParamByTypeBean.Param> tabList = new ArrayList<>();
    private MyViewPagerAdapter viewPagerAdapter;
    private ArrayList<Boolean> isNotLoads;
    private ArrayList<Integer> intCurPages;
    private int currentIndex;
    private ArrayList<View> myViews;
    private ArrayList<PerformDutyFragmentAdapter> adapters;
    private String strCurPageType;
    private String strYear = "";
    private String strUserId = "";
    private String strName = "";
    private String strLinkId = "";
    private ArrayList<String> yearArray = new ArrayList<>();
    private SpinerPopWindow<String> mSpinerPopWindow;

    @Override
    public View inflaterView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_memberduty_main, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).currentFragment == this) {
            if (null != tabLayout && tabList.size() > 0) {
                View customView = tabLayout.getTabAt(currentIndex).getCustomView();
                TextView tvTitle = customView.findViewById(R.id.tv_title);
                LinearLayout llTab = customView.findViewById(R.id.ll_tab);
                llTab.setBackground(SkinCompatResources.getDrawable(activity, R.mipmap.tab_choose));
                tvTitle.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_14_dp));
            }
        }
    }

    @Override
    public void initData() {
        setStatusBar(false);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        yearArray = GIDateUtils.getYearStringArray(5);
        strYear = yearArray.get(0);
        tvYear.setText(strYear);
        mSpinerPopWindow = new SpinerPopWindow<String>(activity, yearArray, itemClickListener);
        if (activity.getClass() == MemberDuty_MainActivity.class) {
            tvClose.setVisibility(View.VISIBLE);
            llFeedback.setVisibility(View.GONE);
            Bundle bundle = activity.getIntent().getExtras();
            if (bundle != null) {
                strUserId = bundle.getString("strUserId");
                strLinkId = bundle.getString("strLinkId");
                strName = bundle.getString("strLinkName", "");
                tvTitle.setText(strName + getString(R.string.string_of_duty));
            }
        }

        if (ProjectNameUtil.isGZSZX(activity)) { // 贵州政协不显示反馈按钮
            llFeedback.setVisibility(View.GONE);
        }
        getParamByType();
    }

    /**
     * 获取履职类型
     */
    private void getParamByType() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "14");
        doRequestNormal(ApiManager.getInstance().getParamByType(textParamMap), 1);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.iv_person_head:
                if (activity.getClass() == MemberDuty_MainActivity.class) {
                    bundle = new Bundle();
                    bundle.putString("strUserId", strUserId);
                    bundle.putString("strLinkName", strName);
                    bundle.putString("strLinkId", strLinkId);
                    bundle.putBoolean("isShowChatBtn", true);
                    showActivity(this, Contact_DetailActivity.class, bundle);
                }
                break;
            case R.id.tv_close:
                activity.finish();
                break;
            case R.id.ll_submit_proposal://提案提交

                break;
            case R.id.ll_submit_opinion://社情民意提交

                break;
            case R.id.ll_submit_file://提交履职材料

                break;
            case R.id.ll_feedback_meet_act://反馈会议活动

                break;
            case R.id.ll_year: // 选择年度
                if (!ProjectNameUtil.isGZSZX(activity)) { // 贵州政协不允许切换年份，只显示2020
                    mSpinerPopWindow.setWidth(llYear.getWidth() + 20);
                    mSpinerPopWindow.showAsDropDown(llYear);
                }
                break;
            case R.id.ll_score:
                bundle.putString("strUserId", strUserId);
                bundle.putString("strYear", strYear);
                showActivity(fragment, MemberDuty_ScoreDetailActivity.class, bundle);
                break;
            case R.id.ll_feedback:
                showActivity(fragment, MemberDuty_FeedBackActivity.class);
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        appBarLayout.addOnOffsetChangedListener(new AppbarLayoutStateChange.AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    tvTitle.setVisibility(View.GONE);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    tvTitle.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                } else {
                    //中间状态
                }
            }
        });
    }

    /**
     * popupwindow显示的ListView的item点击事件(年份)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            tvYear.setText(yearArray.get(position));
            strYear = yearArray.get(position);
            for (int i = 0; i < tabList.size(); i++) {
                isNotLoads.set(i, false);
            }
            getPerformDutyMessage();
        }
    };

    /**
     * 获取履职情况
     */
    private void getPerformDutyMessage() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strUserId", strUserId);
        textParamMap.put("strYear", strYear);
        textParamMap.put("strRecordType", strCurPageType);
        doRequestNormal(ApiManager.getInstance().getPerformDutyMassage(textParamMap), 0);
    }

    /**
     * 初始化tab栏
     */
    private void initTab() {
        currentIndex = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        myViews = new ArrayList<>();
        adapters = new ArrayList<>();
        isNotLoads = new ArrayList<>();
        intCurPages = new ArrayList<>();
        for (int i = 0; i < tabList.size(); i++) {
            intCurPages.add(1);
            isNotLoads.add(false);
            initAddView(i);
        }
        viewPagerAdapter = new MyViewPagerAdapter(myViews);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).select();
        if (GIStringUtil.isBlank(strCurPageType)) {
            strCurPageType = tabList.get(0).getStrCode();
        }
        //设置tab的item布局样式
        for (int i = 0; i < tabList.size(); i++) {
            MyTabLayout.Tab tab = tabLayout.getTabAt(i);
            View inflate = View.inflate(activity, R.layout.view_tab_memberduty, null);
            TextView tvTitle = inflate.findViewById(R.id.tv_title);
            LinearLayout llTab = inflate.findViewById(R.id.ll_tab);
            TextView tvIcon = inflate.findViewById(R.id.tv_select_icon);
            tvTitle.setText(tabList.get(i).getStrName());
            tvTitle.setTextColor(getResources().getColor(R.color.font_content));

            //设置第一条tab的选中样式
            if (i == 0) {
                llTab.setBackground(SkinCompatResources.getDrawable(activity, R.mipmap.tab_choose));
                tvTitle.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_14_dp));
                tvIcon.setVisibility(View.VISIBLE);
            } else {
                llTab.setBackground(getResources().getDrawable(R.mipmap.tab_unchoose));
                tvTitle.setTextColor(getResources().getColor(R.color.font_content));
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_12_dp));
                tvIcon.setVisibility(View.INVISIBLE);
            }
            tab.setCustomView(inflate);
        }
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
        viewPager.setCurrentItem(0);
    }


    /**
     * 初始化列表页面
     */
    private void initAddView(int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_viewpager_add_view_scrollview, null);
        view.findViewById(R.id.myExListView).setVisibility(View.VISIBLE);
        PerformDutyFragmentAdapter adapter = new PerformDutyFragmentAdapter(activity, new ArrayList<PerformDutyBean.DutyList>(), i);
        MyExListViewForScrollView expandableListView = view.findViewById(R.id.myExListView);
        expandableListView.setAdapter(adapter);
        adapters.add(adapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        if (i == 0 || i == 1) {
            expandableListView.setPadding(25, 10, 25, 10);
        } else {
            expandableListView.setPadding(0, 30, 0, 30);
        }
        myViews.add(view);
    }

    private void doLogic(int what, Object object) {
        prgDialog.closePrgDialog();
        switch (what) {
            case 0:
                try {
                    PerformDutyBean performDutyBean = (PerformDutyBean) object;
                    tvName.setText(performDutyBean.getStrName());
                    tvDuty.setText(performDutyBean.getStrFaction() + "  " + performDutyBean.getStrSector());
                    GIImageUtil.loadImg(activity, ivPersonHead, Utils.formatFileUrl(activity, performDutyBean.getStrPathUrl()), 1);
                    if (("0").equals(performDutyBean.getStrLzGrade())) {
                        rlScore.setVisibility(View.VISIBLE);
                        tvScore.setText(performDutyBean.getStrGradeName());
                        tvScoreType.setText(R.string.string_duty_grade);
                    } else if ("1".equals(performDutyBean.getStrLzGrade())) {
                        rlScore.setVisibility(View.VISIBLE);
                        tvScore.setText(performDutyBean.getStrAllScore());
                        tvScoreType.setText(R.string.string_duty_score);
                    } else {
                        rlScore.setVisibility(View.GONE);
                    }
                    View view = myViews.get(currentIndex);
                    MyExListViewForScrollView expandableListView = view.findViewById(R.id.myExListView);
                    ImageView empty_TextView = view.findViewById(R.id.tv_empty);
                    PerformDutyFragmentAdapter adapter = adapters.get(currentIndex);
                    adapter.setCurPageType(strCurPageType);
                    if (adapter == null) {
                        adapter = new PerformDutyFragmentAdapter(activity, performDutyBean.getObjList(), currentIndex);
                        expandableListView.setAdapter(adapter);
                    } else {
                        adapter.setDutyLists(performDutyBean.getObjList());
                        adapter.notifyDataSetChanged();
                    }
                    if (performDutyBean.getObjList().size() == 0 || performDutyBean.getObjList() == null) {
                        empty_TextView.setVisibility(View.VISIBLE);
                    } else {
                        empty_TextView.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < adapter.getGroupCount(); i++) {
                        expandableListView.expandGroup(i);
                    }
                    if (isNotLoads != null && isNotLoads.size() > 0)
                        isNotLoads.set(currentIndex, true);

                } catch (Exception e) {
                    showToast(getResources().getString(R.string.data_error));
                }
                break;
            case 1:
                ParamByTypeBean paramByTypeBean = (ParamByTypeBean) object;
                tabList = paramByTypeBean.getObjList();
                initTab();
                getPerformDutyMessage();
                break;
        }
    }

    /**
     * tab切换效果实现
     */
    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            view.findViewById(R.id.tv_select_icon).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ll_tab).setBackground(SkinCompatResources.getDrawable(activity, R.mipmap.tab_choose));
            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
            ((TextView) view.findViewById(R.id.tv_title)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_14_dp));
            strCurPageType = tabList.get(tab.getPosition()).getStrCode();
            viewPager.setCurrentItem(tab.getPosition());
            currentIndex = tab.getPosition();
            if (isNotLoads.size() == 0 || !isNotLoads.get(currentIndex)) {
                getPerformDutyMessage();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            view.findViewById(R.id.tv_select_icon).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.ll_tab).setBackground(getResources().getDrawable(R.mipmap.tab_unchoose));
            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.font_content));
            ((TextView) view.findViewById(R.id.tv_title)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_12_dp));
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


}
