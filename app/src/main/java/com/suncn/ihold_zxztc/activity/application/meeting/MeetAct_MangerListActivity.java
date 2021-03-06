package com.suncn.ihold_zxztc.activity.application.meeting;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GITextView;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.BaseInterface;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.MeetAct_Main_RVAdapter;
import com.suncn.ihold_zxztc.adapter.ViewPagerAdapter;
import com.suncn.ihold_zxztc.bean.MeetingActListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.suncn.ihold_zxztc.view.MyTabLayout;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import skin.support.content.res.SkinCompatResources;

/**
 * ????????????????????????
 */
public class MeetAct_MangerListActivity extends BaseActivity implements BaseInterface {
    @BindView(id = R.id.tv_session)
    private TextView session_TextView;//??????TextView
    @BindView(id = R.id.ll_session, click = true)
    private LinearLayout session_LinearLayout;//??????LinearLayout
    @BindView(id = R.id.tabLayout)
    private MyTabLayout tabLayout;//??????MyTabLayout
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    @BindView(id = R.id.ll_top_search)//?????????????????????
    private LinearLayout llSearch;
    @BindView(id = R.id.et_top_search)//???????????????
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel_1, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.tv_search, click = true)
    private TextView search_TextView;
    private String strType = "";//???????????????-1 ?????????1?????????
    private List<MeetAct_Main_RVAdapter> adapters;
    private ArrayList<Boolean> isNotLoads;
    public ArrayList<SessionListBean.YearInfo> yearList; // ????????????
    private int curPage = 1;
    private ArrayList<View> myViews;
    private int[] intCurPages;
    private ViewPagerAdapter viewPagerAdapter;
    private int currentIndex = 0;
    private List<String> objList;
    private int intKind;
    private String strKeyValue = "";
    private ArrayList<String> yearArray = new ArrayList<>();

    private String strYear;//??????
    private SpinerPopWindow<String> mSpinerPopWindow;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                case 1:
                    intCurPages[currentIndex] = 1;
                    getMeetingList(true);
                    break;
                case 2:
                    intCurPages[currentIndex] = 1;
                    getMeetingList(false);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_global_tab_list);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            intKind = bundle.getInt("intKind", 0);
            yearList = (ArrayList<SessionListBean.YearInfo>) bundle.getSerializable("yearList");
            session_TextView.setText(yearList.get(0).getStrYear());
            strYear = yearList.get(0).getStrYear();
            for (SessionListBean.YearInfo yearInfo : yearList) {
                yearArray.add(yearInfo.getStrYear());
            }
            mSpinerPopWindow = new SpinerPopWindow<String>(activity, yearArray, itemClickListener);
            setHeadTitle(bundle.getString("headTitle"));
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        objList = new ArrayList<>();
        objList.add(getString(R.string.string_all));
        objList.add(getString(R.string.string_processing));
        objList.add(getString(R.string.string_over));
        initTab();
    }

    /**
     * popupwindow?????????ListView???item????????????(??????)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            session_TextView.setText(yearArray.get(position));
            strYear = yearArray.get(position);
            for (int i = 0; i < objList.size(); i++) {
                isNotLoads.set(i, false);
                intCurPages[i] = 1;
            }

            getMeetingList(true);
        }
    };

    /**
     * ????????????????????????????????????
     */
    private void getMeetingList(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", strType);
        textParamMap.put("strYear", strYear);
        textParamMap.put("strKeyValue", strKeyValue);
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        if (intKind == DefineUtil.hygl) {
            doRequestNormal(ApiManager.getInstance().getMangerMeetingList(textParamMap), 0);
        } else {
            doRequestNormal(ApiManager.getInstance().getMangerActList(textParamMap), 0);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_session:
                mSpinerPopWindow.setWidth(session_LinearLayout.getWidth()*2);
                mSpinerPopWindow.setHeight(session_TextView.getHeight() * yearList.size());
                mSpinerPopWindow.showAsDropDown(session_TextView);
                break;
            case R.id.tv_search:
                llSearch.setVisibility(View.VISIBLE);
                search_TextView.setVisibility(View.GONE);
                tvCancel.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel_1:
                llSearch.setVisibility(View.INVISIBLE);
                search_TextView.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.GONE);
                if (GIStringUtil.isNotBlank(etSearch.getText().toString())) {
                    etSearch.setText("");
                    intCurPages[currentIndex] = 1;
                    getMeetingList(true);
                }
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    strKeyValue = etSearch.getText().toString();
                    if (!GIStringUtil.isNotBlank(strKeyValue)) {
                        GIUtil.closeSoftInput(activity);
                    }
                    getMeetingList(false);
                    return true;
                }
                return false;
            }
        });

        etSearch.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getMeetingList(false);
            }
        });
    }

    /**
     * ?????????tab???
     */
    private void initTab() {
        //currentIndex = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(metrics.widthPixels / objList.size(), ViewGroup.LayoutParams.MATCH_PARENT);
        myViews = new ArrayList<View>();
        adapters = new ArrayList<>();
        isNotLoads = new ArrayList<>();
        intCurPages = new int[objList.size()];
        for (int i = 0; i < objList.size(); i++) {
            intCurPages[i] = 1;
            isNotLoads.add(false);
            initAddView(i);
        }

        viewPagerAdapter = new ViewPagerAdapter(myViews, objList);
        viewPager.setAdapter(viewPagerAdapter);
        //viewPager.setCurrentItem(currentIndex);
        strType = "";
        //viewPager.addOnPageChangeListener(pageListener);
        tabLayout.setupWithViewPager(viewPager);
        getMeetingList(true);


        //??????tab???item????????????
        for (int i = 0; i < objList.size(); i++) {
            MyTabLayout.Tab tab = tabLayout.getTabAt(i);
            View inflate = View.inflate(this, R.layout.view_tab_meet, null);
            GITextView textView = inflate.findViewById(R.id.tv_tab);
            textView.setText(objList.get(i));
            textView.setTextColor(getResources().getColor(R.color.font_title));

            //???????????????tab???????????????
            if (i == 0) {
                setSelectedTabTextView(textView);
            }
            tab.setCustomView(inflate);
        }
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
        viewPager.setCurrentItem(0);
    }

    TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override

        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
            View view = tab.getCustomView();
            setSelectedTabTextView(view.findViewById(R.id.tv_tab));
            currentIndex = tab.getPosition();
            if (currentIndex == 0) {
                strType = "";
            } else if (currentIndex == 1) {
                strType = "1";
            } else {
                strType = "-1";
            }
            GILogUtil.i("???????????????????????????" + objList.size() + "???????????????????????????" + tab.getPosition());

            if (!isNotLoads.get(currentIndex) || GIStringUtil.isNotBlank(strKeyValue)) {
                getMeetingList(true);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            View view = tab.getCustomView();
            ((TextView) view.findViewById(R.id.tv_tab)).setTextColor(getResources().getColor(R.color.font_title));
            Drawable drawable = SkinCompatResources.getDrawable(activity, R.drawable.shape_tab_bline_select);/// ?????????????????????,??????????????????.
            drawable.setBounds(0, GIDensityUtil.dip2px(activity, 5), drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ((TextView) view.findViewById(R.id.tv_tab)).setCompoundDrawables(null, null, null, null);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


    /**
     * ???????????????tab??????
     */
    private void setSelectedTabTextView(TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
        textView.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        Drawable drawable = SkinCompatResources.getDrawable(activity, R.drawable.shape_tab_bline_select);/// ?????????????????????,??????????????????.
        drawable.setBounds(0, GIDensityUtil.dip2px(activity, 5), drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null, null, null, drawable);
    }


    /**
     * ????????????
     */
    private void initAddView(final int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_meetingact_add_view, null);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        final SmartRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        //View viewHead = LayoutInflater.from(activity).inflate(R.layout.view_head_banner, null);
        MeetAct_Main_RVAdapter adapter = null;
        if (intKind == DefineUtil.zhxx) {
            adapter = new MeetAct_Main_RVAdapter(R.layout.item_rv_meetact_child, activity, intKind);
        } else {
            adapter = new MeetAct_Main_RVAdapter(R.layout.item_rv_meetact_main, activity, intKind);
        }
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                intCurPages[position] = 1;
                getMeetingList(false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                intCurPages[position]++;
                getMeetingList(false);
            }
        });
        adapters.add(adapter);
        myViews.add(view);
        MeetAct_Main_RVAdapter finalAdapter = adapter;
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                String strId = "";
                Bundle bundle = new Bundle();
                MeetingActListBean.ObjMeetingActBean myActivitiesBean = finalAdapter.getItem(position);
                boolean onlyShowNormalInfo = myActivitiesBean.getIntIsHavChild() == 1 ? true : false;
                strId = myActivitiesBean.getStrId();
                bundle.putString("strId", strId);
                bundle.putInt("sign", intKind);
                if (intKind == DefineUtil.hdgl) {
                    bundle.putBoolean("onlyShowNormalInfo", false);
                    bundle.putString("headTitle", getString(R.string.string_activity_detail));
                } else {
                    bundle.putBoolean("onlyShowNormalInfo", onlyShowNormalInfo);
                    bundle.putString("headTitle", getString(R.string.string_meeting_detail));
                }
                bundle.putBoolean("isFromList", true);
                bundle.putBoolean("isManager", true);
                bundle.putBoolean("onlyShowNormalInfo", onlyShowNormalInfo);
                showActivity(activity, MeetAct_DetailActivity.class, bundle, 2);
            }
        });
    }


    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    MeetAct_Main_RVAdapter adapter = adapters.get(currentIndex);
                    SmartRefreshLayout refreshLayout = myViews.get(currentIndex).findViewById(R.id.refreshLayout);
                    MeetingActListBean meetingListBean = (MeetingActListBean) data;
                    List<MeetingActListBean.ObjMeetingActBean> proposalBeans = meetingListBean.getObjList();
                    Utils.setLoadMoreViewState(meetingListBean.getIntAllCount(), intCurPages[currentIndex] * DefineUtil.GLOBAL_PAGESIZE, strKeyValue, refreshLayout, adapter);
                    if (intCurPages[currentIndex] == 1) {
                        adapter.setList(proposalBeans);
                    } else {
                        adapter.addData(proposalBeans);
                    }
                    if (isNotLoads != null && isNotLoads.size() > 0) {
                        isNotLoads.set(currentIndex, true);
                    }
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

    @Override
    public void setOnClick(Object object, View view, int type) {
        MeetingActListBean.ObjMeetingActBean myActivitiesBean = (MeetingActListBean.ObjMeetingActBean) object;
        Bundle bundle;
        switch (view.getId()) {
            case R.id.tv_state:
                if (myActivitiesBean.getStrState().equals("3")) {//??????
                    bundle = new Bundle();
                    if (type == DefineUtil.wdhd) {
                        bundle.putString("headTitle", getString(R.string.string_event_registration));
                    } else {
                        bundle.putString("headTitle", getString(R.string.string_meeting_registration));
                    }
                    //bundle.putString("headTitle", "????????????");
                    bundle.putInt("sign", type);
                    bundle.putString("strName", myActivitiesBean.getStrName());
                    bundle.putString("strStartDate", myActivitiesBean.getStrStartDate());
                    bundle.putString("strEndDate", myActivitiesBean.getStrEndDate());
                    bundle.putString("strPlace", myActivitiesBean.getStrPlace());
                    bundle.putString("strId", myActivitiesBean.getStrId());
                    showActivity(activity, MeetAct_SignUpActivity.class, bundle, 2);
                } else {//??????
                    String strSignWay = GIStringUtil.nullToEmpty(myActivitiesBean.getStrSignWay());
                    if (strSignWay.equals("0")) {
                        //Intent intent = new Intent(context, QRCodeSignInActivity.class);
                        bundle = new Bundle();
                        bundle.putString("strId", myActivitiesBean.getStrId());//??????id
                        if (type == DefineUtil.wdhd) {
                            bundle.putString("strType", "1");//??????????????????1
                        } else if (type == DefineUtil.wdhy) {
                            bundle.putString("strType", "0");//??????????????????0
                        }
                        showActivity(activity, QRCodeSignInActivity.class, bundle, 2);
                    } else if (strSignWay.equals("1")) {
                        String strWeiDu = ""; //??????
                        String strJingDu = ""; // ??????
                        String strMtTime = "";
                        String strMtName = "";
                        String strMtId = "";
                        String strLocationSignDistance = "";
                        strMtId = myActivitiesBean.getStrId();
                        strJingDu = myActivitiesBean.getStrLongitude();
                        strWeiDu = myActivitiesBean.getStrLatitude();
                        strMtName = myActivitiesBean.getStrName();
                        strMtTime = myActivitiesBean.getStrStartDate();
                        strLocationSignDistance = myActivitiesBean.getStrLocationSignDistance();
                        bundle = new Bundle();
                        bundle.putString("strMtId", strMtId);
                        bundle.putString("strLocationSignDistance", strLocationSignDistance);
                        bundle.putString("strWeiDu", strWeiDu);
                        bundle.putString("strJingDu", strJingDu);
                        bundle.putString("strMtTime", strMtTime);
                        bundle.putString("strMtName", strMtName);
                        if (type == DefineUtil.wdhd) {
                            bundle.putString("strType", "1");//??????????????????1
                        } else if (type == DefineUtil.wdhy) {
                            bundle.putString("strType", "0");//??????????????????0
                        }
                        bundle.putString("headTitle", ((TextView) view).getText().toString());
                        showActivity(activity, SignInActivity.class, bundle, 2);
                    } else if (strSignWay.equals("2")) {
                        String strWeiDu = ""; //??????
                        String strJingDu = ""; // ??????
                        String strMtTime = "";
                        String strMtName = "";
                        String strMtId = "";
                        String strLocationSignDistance = "";
                        strMtId = myActivitiesBean.getStrId();
                        strJingDu = myActivitiesBean.getStrLongitude();
                        strWeiDu = myActivitiesBean.getStrLatitude();
                        strMtName = myActivitiesBean.getStrName();
                        strMtTime = myActivitiesBean.getStrStartDate();
                        strLocationSignDistance = myActivitiesBean.getStrLocationSignDistance();
                        bundle = new Bundle();
                        bundle.putString("strMtId", strMtId);
                        bundle.putString("strLocationSignDistance", strLocationSignDistance);
                        bundle.putString("strWeiDu", strWeiDu);
                        bundle.putString("strJingDu", strJingDu);
                        bundle.putString("strMtTime", strMtTime);
                        bundle.putString("strMtName", strMtName);
                        bundle.putString("strSignWay", "2");
                        if (type == DefineUtil.wdhd) {
                            bundle.putString("strType", "1");//??????????????????1
                        } else if (type == DefineUtil.wdhy) {
                            bundle.putString("strType", "0");//??????????????????0
                        }
                        bundle.putString("headTitle", ((TextView) view).getText().toString());
                        showActivity(activity, SignInActivity.class, bundle, 2);
                    }
                }
                break;
            case R.id.ll_child_detail://????????????
                bundle = new Bundle();
                bundle.putString("headTitle", getString(R.string.string_second_meeting_info));
                bundle.putBoolean("isChildMt", true);
                bundle.putInt("sign", DefineUtil.hygl);
                bundle.putString("strId", myActivitiesBean.getStrId());
                showActivity(activity, Meet_ChildListActivity.class, bundle);
                break;
        }

    }
}
