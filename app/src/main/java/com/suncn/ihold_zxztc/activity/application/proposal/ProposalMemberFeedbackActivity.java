package com.suncn.ihold_zxztc.activity.application.proposal;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.MemberFeedbackRvAdapter;
import com.suncn.ihold_zxztc.bean.ProposalFeedBackViewServletBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author :Sea
 * Date :2020-6-11 9:10
 * PackageName:com.suncn.ihold_zxztc.activity.application.proposal
 * Desc: 委员反馈
 */
public class ProposalMemberFeedbackActivity extends BaseActivity {
    //    @BindView(id = R.id.refreshLayout)
//    private SmartRefreshLayout refreshLayout;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.tvOverallEvaluationYes, click = true)
    private TextView tvOverallEvaluationYes;
    @BindView(id = R.id.tvOverallEvaluationNo, click = true)
    private TextView tvOverallEvaluationNo;

    private String strId; // 提案ID、
    private MemberFeedbackRvAdapter mAdapter;
    private int intCurPages = 1;
    private String strFeedBackAllState = "";
    private boolean isFromRemind;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_member_feedback);
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBar();
        requestCallBack = (data, sign) -> doLogic(sign, data);
        setHeadTitle(getString(R.string.string_member_feedback));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strId = bundle.getString("strId");
            isFromRemind = bundle.getBoolean("isFromRemind", false);

            goto_Button.setText(getString(R.string.string_submit));
            goto_Button.refreshFontType(activity, "2");
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            goto_Button.setVisibility(View.VISIBLE);
        }
        getProposalFeedBackViewServlet(true);
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0.5f, 0);
        mAdapter = new MemberFeedbackRvAdapter(this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.view_erv_empty);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto:
                HashMap<String, String> stringStringHashMap = mAdapter.getmFeedBackMap();
                Log.i("===================", stringStringHashMap.toString());
                if (GIStringUtil.isBlank(strFeedBackAllState) && findViewById(R.id.ll_main).getVisibility() == View.VISIBLE) {
                    showToast("请选择整体评价");
                } else if (mAdapter.getData().size()*4 != stringStringHashMap.size()) {
                    showToast("请补全反馈信息");
                } else {
                    //dealProposalFeedBackServlet(true, stringStringHashMap);
                }
                break;

            case R.id.tvOverallEvaluationYes:
                tvOverallEvaluationYes.setTextColor(Color.parseColor("#f57f17"));
                Drawable drawableLeftYes = getResources().getDrawable(R.mipmap.icon_satisfied);
                tvOverallEvaluationYes.setCompoundDrawablesWithIntrinsicBounds(drawableLeftYes,
                        null, null, null);

                tvOverallEvaluationNo.setTextColor(getResources().getColor(R.color.font_title));
                Drawable drawableLeftNo = getResources().getDrawable(R.mipmap.icon_unchecked_no);
                tvOverallEvaluationNo.setCompoundDrawablesWithIntrinsicBounds(drawableLeftNo,
                        null, null, null);

                strFeedBackAllState = "1";
                break;
            case R.id.tvOverallEvaluationNo:
                tvOverallEvaluationNo.setTextColor(Color.parseColor("#f57f17"));
                drawableLeftYes = getResources().getDrawable(R.mipmap.icon_unsatisfied);
                tvOverallEvaluationNo.setCompoundDrawablesWithIntrinsicBounds(drawableLeftYes,
                        null, null, null);

                tvOverallEvaluationYes.setTextColor(getResources().getColor(R.color.font_title));
                drawableLeftNo = getResources().getDrawable(R.mipmap.icon_unchecked_yes);
                tvOverallEvaluationYes.setCompoundDrawablesWithIntrinsicBounds(drawableLeftNo,
                        null, null, null);
                strFeedBackAllState = "0";
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    @Override
    public void setListener() {
        super.setListener();

//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                intCurPages = 1;
//                getProposalFeedBackViewServlet(false);
//            }
//        });
//        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                intCurPages++;
//                getProposalFeedBackViewServlet(false);
//            }
//        });
    }

    /**
     * 委员反馈的信息
     */
    private void getProposalFeedBackViewServlet(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId); // 提案ID
        textParamMap.put("intPageSize", String.valueOf(DefineUtil.GLOBAL_PAGESIZE));
        textParamMap.put("intCurPage", intCurPages + "");
        doRequestNormal(ApiManager.getInstance().getProposalFeedBackViewServlet(textParamMap), 0);
    }

    /**
     * 委员反馈提交
     */
    private void dealProposalFeedBackServlet(boolean isShow, HashMap<String, String> feedBackMap) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId); // 提案ID
        textParamMap.put("strFeedBackAllState", strFeedBackAllState);
        Iterator iterator = feedBackMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            textParamMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        doRequestNormal(ApiManager.getInstance().dealProposalFeedBackServlet(textParamMap), 1);
    }


    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    ProposalFeedBackViewServletBean proposalFeedBackViewServletBean = (ProposalFeedBackViewServletBean) obj;
//                    Utils.setLoadMoreViewState(proposalFeedBackViewServletBean.getIntAllCount(), intCurPages * DefineUtil.GLOBAL_PAGESIZE
//                            , "", refreshLayout, mAdapter);
                    mAdapter.setList(proposalFeedBackViewServletBean.getObjList());
                    if ("1".equals(proposalFeedBackViewServletBean.getStrAttendTypeCode())) {
                        findViewById(R.id.ll_main).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.ll_main).setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;

            case 1:
                prgDialog.closePrgDialog();
                try {
                    if (isFromRemind) {
                        setResult(-2);
                    } else {
                        setResult(RESULT_OK);
                    }
                    showToast(getString(R.string.string_feedback_success));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }

        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }
}
