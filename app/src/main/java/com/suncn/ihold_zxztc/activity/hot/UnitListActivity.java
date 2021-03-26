package com.suncn.ihold_zxztc.activity.hot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.UnitListExAdapter;
import com.suncn.ihold_zxztc.bean.BaseTypeBean;
import com.suncn.ihold_zxztc.bean.UnitListBean;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.forscrollview.MyExListViewForScrollView;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.HashMap;

/**
 * 微门户列表主界面
 */
public class UnitListActivity extends BaseActivity {
    @BindView(id = R.id.exListView)
    private MyExListViewForScrollView exListView;
    @BindView(id = R.id.iv_head)
    private ImageView ivHead;
    @BindView(id = R.id.tv_name)
    private TextView tvName;
    @BindView(id = R.id.tv_state, click = true)
    private TextView tvState;
    @BindView(id = R.id.ll_title) //标题
    private LinearLayout llTitle;
    @BindView(id = R.id.ll_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.ll_top, click = true)
    private LinearLayout llTop;
    private UnitListExAdapter adapter;
    private String strRootUnitId;
    private String strKeyValue = "";
    private UnitListBean.UnitList topUnitBean;//头部省级数据

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            getUnitList();
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_unit_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUnitList();
    }

    @Override
    public void initData() {
        super.initData();
        goto_Button.setText(getResources().getString(R.string.font_search));
        goto_Button.setVisibility(View.VISIBLE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        setHeadTitle(getString(R.string.string_micro_portal));
    }

    @Override
    public void setListener() {
        super.setListener();
        etSearch.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getUnitList();
            }
        });

        exListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                UnitListBean.UnitList objInfo = (UnitListBean.UnitList) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                Bundle bundle = new Bundle();
                bundle.putString("strRootUnitId", objInfo.getStrUnit_id());
                bundle.putString("strState", objInfo.getIntCount());
                bundle.putString("strPhotoUrl", objInfo.getStrUnit_IconPathUrl());
                bundle.putString("strUnitName", objInfo.getStrUnit_name());
                bundle.putString("strBgUrl", objInfo.getStrUnit_BackGroundPathUrl());
                showActivity(activity, UnitDetailActivty.class, bundle, 0);
                return true;
            }
        });
    }

    /**
     * 获取独立机构列表
     */
    private void getUnitList() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "1");
        if (GIStringUtil.isNotBlank(strKeyValue)) {
            textParamMap.put("strKeyValue", strKeyValue);
        }
        doRequestNormal(ApiManager.getInstance().getUnitList(textParamMap), 0);
    }

    private void doLogic(Object data, int what) {
        try {
            prgDialog.closePrgDialog();
            switch (what) {
                case 0:
                    UnitListBean unitList = (UnitListBean) data;
                    if (unitList.getObjList().size()>0){
                        GIImageUtil.loadImg(activity, ivHead, Utils.formatFileUrl(activity, unitList.getObjList().get(0).getStrUnit_IconPathUrl()), getResources().getDrawable(R.mipmap.unit_head_default));
                    }
                    if (GIStringUtil.isBlank(strKeyValue)) {
                        llTop.setVisibility(View.VISIBLE);
                        topUnitBean = unitList.getObjList().get(0);
                        tvName.setText(topUnitBean.getStrUnit_name());
                        if (GISharedPreUtil.getString(activity, "strDefalutRootUnitId").equals(topUnitBean.getStrUnit_id())) {
                            tvState.setVisibility(View.GONE);
                        } else {
                            tvState.setVisibility(View.VISIBLE);
                            if (topUnitBean.getIntCount().equals("1")) {
                                tvState.setText(getString(R.string.string_subscribed));
                                tvState.setTextColor(getResources().getColor(R.color.font_source));
                            } else {
                                tvState.setText("+"+getString(R.string.string_subscription));
                                tvState.setTextColor(getResources().getColor(R.color.view_head_bg));
                            }
                        }
                        strRootUnitId = topUnitBean.getStrUnit_id();
                        unitList.getObjList().remove(0);
                    } else {
                        llTop.setVisibility(View.GONE);
                    }
                    adapter = new UnitListExAdapter(activity, unitList.getObjList());
                    exListView.setAdapter(adapter);
                    break;
                case 1:
                    BaseTypeBean baseGlobal = (BaseTypeBean) data;
                    if (baseGlobal.getIntCount() == 1) {
                        showToast(getString(R.string.string_subcripe_success));
                        tvState.setText(getString(R.string.string_subscribed));
                        tvState.setTextColor(getResources().getColor(R.color.font_source));
                    } else {
                        showToast(getString(R.string.string_cancle_subcripe));
                        tvState.setText("+"+getString(R.string.string_subscription));
                        tvState.setTextColor(getResources().getColor(R.color.view_head_bg));
                    }
                    break;
            }

        } catch (Exception e) {
            showToast(getResources().getString(R.string.data_error));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                if (llTitle.getVisibility() == View.VISIBLE) {
                    llTitle.setVisibility(View.GONE);
                    llSearch.setVisibility(View.VISIBLE);
                    goto_Button.setVisibility(View.INVISIBLE);
                    tvCancel.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_state:
                doSubscribeNews(strRootUnitId);
                break;
            case R.id.tv_cancel:
                llTitle.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                goto_Button.setVisibility(View.VISIBLE);
                GIUtil.closeSoftInput(activity);
                if (GIStringUtil.isNotBlank(etSearch.getText().toString())) {
                    etSearch.setText("");
                    getUnitList();
                }
                break;
            case R.id.ll_top:
                Bundle bundle = new Bundle();
                bundle.putString("strRootUnitId", strRootUnitId);
                bundle.putString("strState", topUnitBean.getIntCount());
                bundle.putString("strPhotoUrl", topUnitBean.getStrUnit_IconPathUrl());
                bundle.putString("strUnitName", topUnitBean.getStrUnit_name());
                bundle.putString("strBgUrl", topUnitBean.getStrUnit_BackGroundPathUrl());
                showActivity(activity, UnitDetailActivty.class, bundle, 0);
                break;
        }
    }

    /**
     * 订阅取消订阅
     */
    public void doSubscribeNews(String strRootUnitId) {
        textParamMap = new HashMap<>();
        textParamMap.put("strRootUnitId", strRootUnitId);
        doRequestNormal(ApiManager.getInstance().SubscribeNewsUnit(textParamMap), 1);
    }
}
