package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Choose_JoinMemSearch_RVAdapter;
import com.suncn.ihold_zxztc.adapter.Choose_JoinMem_ExLVAdapter;
import com.suncn.ihold_zxztc.adapter.ViewPagerAdapter;
import com.suncn.ihold_zxztc.bean.BaseUser;
import com.suncn.ihold_zxztc.bean.JointMemSearchListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.bean.ZxtaJointMemListBean;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.suncn.ihold_zxztc.view.MyTabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * 选择联名委员
 */
public class Choose_JoinMemActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;//搜索列表
    @BindView(id = R.id.et_search_mem)
    private ClearEditText search_EditText;//搜索ClearEditText
    @BindView(id = R.id.tabLayout)
    private MyTabLayout tabLayout;//顶部MyTabLayout
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;

    private List<Choose_JoinMem_ExLVAdapter> adapters;
    private ArrayList<Boolean> isNotLoads;
    public ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    private ArrayList<View> myViews;
    private int[] intCurPages;
    private ViewPagerAdapter viewPagerAdapter;
    private List<String> menuList;
    private String strKeyValue = "";
    private ArrayList<ZxtaJointMemListBean.ZxtaJointMemBean> zxtaJointUnitBeans;//机构
    private ArrayList<ZxtaJointMemListBean.ZxtaJointMemBean> zxtaJointMemBeans;//委员
    private String strChooseValueId = "";
    private String strChooseUnitId = "";
    private String strChooseMemId = "";
    private Choose_JoinMemSearch_RVAdapter adapter;
    private ArrayList<BaseUser> searchBeans;
    private int strJointlyMemCount;

    public void setStrChooseValueId(String strChooseValueId) {
        this.strChooseValueId = strChooseValueId;
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_choose_joinmem);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strChooseValueId = bundle.getString("strChooseValueId");
            setHeadTitle(getString(R.string.string_select) + getString(R.string.string_associate));
            goto_Button.setText(getString(R.string.string_determine));
            goto_Button.setVisibility(View.VISIBLE);
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            goto_Button.refreshFontType(activity, "2");
            strJointlyMemCount = bundle.getInt("strJointlyMemCount", -1);
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        menuList = new ArrayList<>();
        menuList.add(getString(R.string.string_member));
        menuList.add(getString(R.string.string_mechanism));

        initTab();
        initRecyclerView();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, true, false, R.color.line_bg_white, 0.5f, 0);
        adapter = new Choose_JoinMemSearch_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter1, @NonNull View view, int position) {
                BaseUser baseUser = (BaseUser) adapter.getItem(position);
                if (baseUser.isChecked()) {
                    baseUser.setChecked(false);
                    adapter.setData(position, baseUser);
                    strChooseValueId = strChooseValueId.replaceAll(baseUser.getStrUserId() + ",", "");
                } else {
                    baseUser.setChecked(true);
                    adapter.setData(position, baseUser);
                    strChooseValueId = strChooseValueId + baseUser.getStrUserId() + ",";
                }
            }
        });
    }

    /**
     * 查询列表
     */
    private void getSearchList() {
        textParamMap = new HashMap<>();
        textParamMap.put("strKeyValue", strKeyValue); // 查询关键字
        doRequestNormal(ApiManager.getInstance().getJoinMemSearchList(textParamMap), 3);
    }

    /**
     * 委员列表
     */
    private void getListData(boolean isTrue) {
        if (isTrue)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getChooseReportUserServlet(textParamMap), 0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                if (GIStringUtil.isNotBlank(strKeyValue)) {
                    String[] string = strKeyValue.split(",");
                    GILogUtil.i("strChooseValueId==" + strChooseValueId);
                    strKeyValue = "";
                    search_EditText.setText("");
                    recyclerView.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    ExpandableListView view1 = myViews.get(0).findViewById(R.id.exListview);
                    view1.setAdapter(adapters.get(0));
                    adapters.get(0).setZxtaJointMemBeans(zxtaJointUnitBeans);
                    if (GIStringUtil.isNotBlank(strChooseValueId) && zxtaJointUnitBeans != null) {
                        strChooseUnitId = "";//清空之前的选中，重新赋值
                        ZxtaJointMemListBean.ZxtaJointMemBean zxtaJointMemBean;
                        for (int i = 0; i < zxtaJointUnitBeans.size(); i++) {
                            zxtaJointMemBean = zxtaJointUnitBeans.get(i);
                            if (zxtaJointMemBean.getObjChildList() != null) {
                                BaseUser memListBean;
                                for (int j = 0; j < zxtaJointUnitBeans.get(i).getObjChildList().size(); j++) {
                                    memListBean = zxtaJointUnitBeans.get(i).getObjChildList().get(j);
                                    memListBean.setChecked(false);
                                    if (strChooseValueId.contains(memListBean.getStrUserId())) { // 判断是否已选中
                                        strChooseUnitId = strChooseUnitId + memListBean.getStrUserId() + ",";
                                        memListBean.setChecked(true);
                                        view1.expandGroup(i); // 如果有选中项则自动展开
                                        zxtaJointUnitBeans.get(i).getObjChildList().set(j, memListBean);
                                    }
                                }
                            }
                        }
                    }
                    adapters.get(0).setStrChooseValueId(strChooseUnitId);
                    adapters.get(0).notifyDataSetChanged();
                    ExpandableListView view2 = myViews.get(1).findViewById(R.id.exListview);
                    view2.setAdapter(adapters.get(1));
                    adapters.get(1).setZxtaJointMemBeans(zxtaJointMemBeans);
                    if (GIStringUtil.isNotBlank(strChooseValueId)) {
                        strChooseMemId = "";
                        ZxtaJointMemListBean.ZxtaJointMemBean zxtaJointMemBean;
                        for (int i = 0; i < zxtaJointMemBeans.size(); i++) {
                            zxtaJointMemBean = zxtaJointMemBeans.get(i);
                            if (zxtaJointMemBean.getObjChildList() != null) {
                                BaseUser memListBean;
                                for (int j = 0; j < zxtaJointMemBeans.get(i).getObjChildList().size(); j++) {
                                    memListBean = zxtaJointMemBeans.get(i).getObjChildList().get(j);
                                    memListBean.setChecked(false);
                                    if (strChooseValueId.contains(memListBean.getStrUserId())) { // 判断是否已选中
                                        strChooseMemId = strChooseMemId + memListBean.getStrUserId() + ",";
                                        memListBean.setChecked(true);
                                        view2.expandGroup(i); // 如果有选中项则自动展开
                                        zxtaJointMemBeans.get(i).getObjChildList().set(j, memListBean);
                                    }
                                }
                            }
                        }
                    }
                    adapters.get(1).setStrChooseValueId(strChooseMemId);
                    adapters.get(1).notifyDataSetChanged();
                } else {
                    strChooseValueId = adapters.get(0).getStrChooseValueId() + adapters.get(1).getStrChooseValueId();
                    GILogUtil.i("strChooseValueId======================" + strChooseValueId);
                    String[] ChooseValueList = strChooseValueId.split(",");
                    if (strJointlyMemCount != -1 && ChooseValueList.length > strJointlyMemCount) {
                        showToast("最多选择" + strJointlyMemCount + "个");
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("strChooseValueId", strChooseValueId);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        search_EditText.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (GIStringUtil.isBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                    recyclerView.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    getSearchList();
                }
            }
        });
    }

    /**
     * 初始化tab栏
     */
    private void initTab() {
        //currentIndex = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        myViews = new ArrayList<View>();
        adapters = new ArrayList<>();
        isNotLoads = new ArrayList<>();
        intCurPages = new int[menuList.size()];
        for (int i = 0; i < menuList.size(); i++) {
            intCurPages[i] = 1;
            isNotLoads.add(false);
            initAddView(i);
        }

        viewPagerAdapter = new ViewPagerAdapter(myViews, menuList);
        viewPager.setAdapter(viewPagerAdapter);
        //viewPager.setCurrentItem(currentIndex);
        tabLayout.setupWithViewPager(viewPager);
        getListData(true);
    }

    /**
     * 添加视图
     */
    private void initAddView(final int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_meetact_attendperson, null);
        final ExpandableListView expandableListView = view.findViewById(R.id.exListview);
        Choose_JoinMem_ExLVAdapter adapter = new Choose_JoinMem_ExLVAdapter(activity, new ArrayList<>(), true, position); // 必须重新new，否则ExpandableListView刷新会出问题
        expandableListView.setAdapter(adapter);
        adapters.add(adapter);
        myViews.add(view);
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 3:
                JointMemSearchListBean searchPersonUnitListBean = (JointMemSearchListBean) data;
                searchBeans = new ArrayList<>();
                if (searchPersonUnitListBean.getObjList() != null && searchPersonUnitListBean.getObjList().size() > 0) {
                    for (int i = 0; i < searchPersonUnitListBean.getObjList().size(); i++) {
                        if (GIStringUtil.isNotBlank(strChooseValueId) && strChooseValueId.contains(searchPersonUnitListBean.getObjList().get(i).getStrUserId())) {
                            searchPersonUnitListBean.getObjList().get(i).setChecked(true);
                        }
                        searchBeans.add(searchPersonUnitListBean.getObjList().get(i));
                    }
                }
                adapter.setList(searchBeans);
                break;
            case 0:
                prgDialog.closePrgDialog();
                try {
                    ZxtaJointMemListBean zxtaJointMemListBean = (ZxtaJointMemListBean) data;
                    zxtaJointUnitBeans = zxtaJointMemListBean.getObjReportList();
                    zxtaJointMemBeans = zxtaJointMemListBean.getObjList();
                    ExpandableListView view1 = myViews.get(1).findViewById(R.id.exListview);
                    view1.setAdapter(adapters.get(1));
                    adapters.get(1).setZxtaJointMemBeans(zxtaJointUnitBeans);
                    if (GIStringUtil.isNotBlank(strChooseValueId)) {
                        ZxtaJointMemListBean.ZxtaJointMemBean zxtaJointMemBean;
                        if (zxtaJointUnitBeans != null)
                            for (int i = 0; i < zxtaJointUnitBeans.size(); i++) {
                                zxtaJointMemBean = zxtaJointUnitBeans.get(i);
                                if (zxtaJointMemBean.getObjChildList() != null) {
                                    BaseUser memListBean;
                                    for (int j = 0; j < zxtaJointUnitBeans.get(i).getObjChildList().size(); j++) {
                                        memListBean = zxtaJointUnitBeans.get(i).getObjChildList().get(j);
                                        if (strChooseValueId.contains(memListBean.getStrUserId())) { // 判断是否已选中
                                            strChooseUnitId = strChooseUnitId + memListBean.getStrUserId() + ",";
                                            memListBean.setChecked(true);
                                            view1.expandGroup(i); // 如果有选中项则自动展开
                                            zxtaJointUnitBeans.get(i).getObjChildList().set(j, memListBean);
                                            continue;
                                        }
                                    }
                                }
                            }
                    }
                    adapters.get(1).setStrChooseValueId(strChooseUnitId);
                    adapters.get(1).notifyDataSetChanged();
                    ExpandableListView view2 = myViews.get(0).findViewById(R.id.exListview);
                    view2.setAdapter(adapters.get(0));
                    adapters.get(0).setZxtaJointMemBeans(zxtaJointMemBeans);
                    if (GIStringUtil.isNotBlank(strChooseValueId)) {
                        ZxtaJointMemListBean.ZxtaJointMemBean zxtaJointMemBean;
                        for (int i = 0; i < zxtaJointMemBeans.size(); i++) {
                            zxtaJointMemBean = zxtaJointMemBeans.get(i);
                            if (zxtaJointMemBean.getObjChildList() != null) {
                                BaseUser memListBean;
                                for (int j = 0; j < zxtaJointMemBeans.get(i).getObjChildList().size(); j++) {
                                    memListBean = zxtaJointMemBeans.get(i).getObjChildList().get(j);
                                    if (strChooseValueId.contains(memListBean.getStrUserId())) { // 判断是否已选中
                                        strChooseMemId = strChooseMemId + memListBean.getStrUserId() + ",";
                                        memListBean.setChecked(true);
                                        view2.expandGroup(i); // 如果有选中项则自动展开
                                        zxtaJointMemBeans.get(i).getObjChildList().set(j, memListBean);
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                    adapters.get(0).setStrChooseValueId(strChooseMemId);
                    adapters.get(0).notifyDataSetChanged();
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
}
