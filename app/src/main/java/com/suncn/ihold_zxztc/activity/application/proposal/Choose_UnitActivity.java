package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.ChooseUnit_ExLVAdapter;
import com.suncn.ihold_zxztc.adapter.Choose_Unit_RVAdapter;
import com.suncn.ihold_zxztc.bean.UpUnitListBean;
import com.suncn.ihold_zxztc.bean.UpUnitListBean.UpUnitBean;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 选择单位Activity
 */
public class Choose_UnitActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;//搜索列表
    @BindView(id = R.id.exListview)
    private ExpandableListView exListView;//单位ExpandableListView
    @BindView(id = R.id.tv_empty)
    private ImageView empty_TextView;
    @BindView(id = R.id.ll_search_bt)
    private LinearLayout search_Layout;//搜索Layout
    @BindView(id = R.id.et_search_1)
    private ClearEditText search_EditText;//搜索ClearEditText

    private String strKeyValue = ""; // 搜索关键字
    private ChooseUnit_ExLVAdapter adapter;
    private String strChooseUnitId = ""; // 选中的单位ID
    private boolean isSingle; // 是否只能单选
    private boolean isSystem; // 是否是承办系统
    private boolean isHB;//是否是会办
    private String strHasChoose;//已选择数据
    private boolean isQXZF;
    private boolean isCBDW; // 承办单位
    private List<UpUnitBean> upUnitBeans;
    private Choose_Unit_RVAdapter rv_Adapter;
    private int mustSelectCount;
    private String strTitle;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_global_exlistview);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        goto_Button.setText(R.string.string_determine);
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        goto_Button.refreshFontType(activity, "2");
        search_Layout.setVisibility(View.VISIBLE);
        search_EditText.setHint(R.string.string_please_enter_company_name);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setHeadTitle(getString(R.string.string_select) + bundle.getString("headTitle"));
            strTitle = bundle.getString("headTitle", "");
            strChooseUnitId = bundle.getString("strChooseUnitId", "");
            strHasChoose = bundle.getString("strHasChoose", "");
            isSingle = bundle.getBoolean("isSingle");
            isSystem = bundle.getBoolean("isSystem");
            isHB = bundle.getBoolean("isHB");
            isQXZF = bundle.getBoolean("isQXZF");
            isCBDW = bundle.getBoolean("isCBDW");
            mustSelectCount = bundle.getInt("mustSelectCount", -1);
            if (isSystem)
                search_EditText.setHint(R.string.string_please_enter_the_system_name);
        }
        initRecyclerView();
        getListData(true);
    }

    public void setStrChooseUnitId(String strChooseUnitId) {
        this.strChooseUnitId = strChooseUnitId;
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, true, false, R.color.line_bg_white, 0.5f, 0);
        rv_Adapter = new Choose_Unit_RVAdapter(this);
        recyclerView.setAdapter(rv_Adapter);
        rv_Adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                UpUnitListBean.UpUnitBean upUnitBean = (UpUnitBean) rv_Adapter.getItem(position);
                if (upUnitBean.isChecked()) {
                    upUnitBean.setChecked(false);
                    rv_Adapter.setData(position, upUnitBean);
                    strChooseUnitId = strChooseUnitId.replaceAll(upUnitBean.getStrId() + ",", "");
                } else {
                    upUnitBean.setChecked(true);
                    rv_Adapter.setData(position, upUnitBean);
                    strChooseUnitId = strChooseUnitId + upUnitBean.getStrId() + ",";
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto:
                if (GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                    search_EditText.setText("");
                    strKeyValue = "";
                    recyclerView.setVisibility(View.GONE);
                    exListView.setVisibility(View.VISIBLE);
                    doCheck(upUnitBeans);
                } else {
                    if (adapter != null) {
                        strChooseUnitId = adapter.getStrChooseValueId();
                        /*GILogUtil.i("=============" + adapter.getStrChooseValueId());
                        if (GIStringUtil.isBlank(strChooseUnitId)) {
                            showToast("请" + head_title_TextView.getText().toString() + "！");
                        } else {*/
                        String[] list = strChooseUnitId.split(",");
                        if (mustSelectCount != -1 && list.length > mustSelectCount) {
                            showToast(getString(R.string.string_select_at_most) + mustSelectCount + getString(R.string.string_a) + strTitle);
                            return;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("strChooseUnitId", strChooseUnitId);
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                        //    }
                    }
                }
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    @Override
    public void setListener() {
        search_EditText.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (GIStringUtil.isBlank(strKeyValue)) {
                    exListView.setVisibility(View.VISIBLE);
                } else {
                    exListView.setVisibility(View.GONE);
                }

                getListData(false);
            }
        });
        super.setListener();
    }

    /**
     * 承办单位列表
     */
    private void getListData(boolean isTrue) {
        if (isTrue)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        if (isSystem) {
            textParamMap.put("intType", "1"); // 分发类型 1--承办系统 2承办单位
            textParamMap.put("type", "cbxt");//承办系统
        } else if (isQXZF) {
            textParamMap.put("intType", "2"); // 分发类型 1--承办系统 2承办单位，区县政府
            textParamMap.put("type", "qxzf");//区县政府
        } else if (isCBDW) {
            textParamMap.put("intType", "2"); // 分发类型 1--承办系统 2承办单位
            textParamMap.put("type", "cbdw");//承办单位
        } else {
            textParamMap.put("intType", "2"); // 分发类型 1--承办系统 2承办单位
            textParamMap.put("type", "cbdw,cbxt");//承办单位
        }
        if (isHB) {
            textParamMap.put("delIds", strHasChoose); // 已选择数据回传
        }
        textParamMap.put("showSelfWithChildId", ""); // 承办系统自身id
        if (GIStringUtil.isNotBlank(strKeyValue)) {
            textParamMap.put("strKeyValue", strKeyValue); // 搜索关键字
        }

        doRequestNormal(ApiManager.getInstance().getChooseReportUnitServlet(textParamMap), 0);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
//                try {
                    UpUnitListBean upUnitListBean = (UpUnitListBean) obj;
                    upUnitBeans = upUnitListBean.getObjList();
                    if (upUnitBeans != null && upUnitBeans.size() > 0) {
                        empty_TextView.setVisibility(View.GONE);
                        //strKeyValue为空表示获取数据，不为空表示搜索
                        if (GIStringUtil.isBlank(strKeyValue)) {
                            recyclerView.setVisibility(View.GONE);
                            exListView.setVisibility(View.VISIBLE);
                            doCheck(upUnitBeans);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            exListView.setVisibility(View.GONE);
                            List<UpUnitBean> list = new ArrayList<>();
                            for (int i = 0; i < upUnitBeans.size(); i++) {
                                if (strChooseUnitId.contains(upUnitBeans.get(i).getStrId())) {
                                    upUnitBeans.get(i).setChecked(true);
                                }
                                list.add(upUnitBeans.get(i));
                            }
                            rv_Adapter.setList(list);
                        }
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        exListView.setVisibility(View.GONE);
                        empty_TextView.setVisibility(View.VISIBLE);
                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    toastMessage = getString(R.string.data_error);
//                }
                break;
            default:
                break;
        }

        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    /**
     * 执行选中操作
     */
    private void doCheck(List<UpUnitBean> upUnitBeans) {
        if (adapter == null) {
            adapter = new ChooseUnit_ExLVAdapter(this, upUnitBeans, isSingle);
            adapter.setStrChooseValueId(strChooseUnitId);
            exListView.setAdapter(adapter);
        } else {
            adapter.setStrChooseValueId(strChooseUnitId);
            adapter.setZxtaJointMemBeans(upUnitBeans);
            adapter.notifyDataSetChanged();
        }
        if (GIStringUtil.isNotBlank(strChooseUnitId)) {
            UpUnitBean upUnitBean;
            for (int i = 0; i < upUnitBeans.size(); i++) {
                upUnitBean = upUnitBeans.get(i);
                if (upUnitBean.getObjChildList() != null) {
                    UpUnitListBean.Unit unit;
                    for (int j = 0; j < upUnitBeans.get(i).getObjChildList().size(); j++) {
                        unit = upUnitBeans.get(i).getObjChildList().get(j);
                        if (strChooseUnitId.contains(unit.getStrId())) {
                            unit.setChecked(true);
                            exListView.expandGroup(i); // 如果有选中项则自动展开
                            upUnitBeans.get(i).getObjChildList().set(j, unit);
                        }
                    }
                }
            }
            adapter.setZxtaJointMemBeans(upUnitBeans);
            adapter.notifyDataSetChanged();
        }
        if (upUnitBeans.size() == 1) { // 如果只有一个则默认展开
            exListView.expandGroup(0);
        }
    }
}
