package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.SimilarProposalListAdapter;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.SimilarProposalListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 相似提案
 */
public class SimilarProposalsActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.tv_msg)
    private GITextView msg_TextView;//搜索结果
    private SimilarProposalListAdapter adapter;
    private ArrayList<SimilarProposalListBean.SimilarProposal> objList;
    private String strCheckMsg;
    private boolean isSaveData;

    private String strOriginCase; // 案由
    private String strConsign = ""; // 协商方式
    private String strReason; // 情况分析
    private String strWay; // 具体建议
    private String strChooseValue = ""; // 选中的委员、结构
    private String strChooseValueId = ""; // 选中的委员Id、机构Id
    private String strChooseUnitId = "";
    private String StrId;
    private String strDiaoyanguocheng = "";
    public HashMap<String, String> myValueMap;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_similar_proposals);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("相似提案");
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strDiaoyanguocheng = bundle.getString("strDiaoyanguocheng");
            myValueMap = (HashMap<String, String>) bundle.getSerializable("myValueMap");
            objList = (ArrayList<SimilarProposalListBean.SimilarProposal>) bundle.getSerializable("objList");
            strCheckMsg = bundle.getString("strCheckMsg");
            isSaveData = bundle.getBoolean("isSaveData");
            msg_TextView.setText("\ue683" + strCheckMsg);
            strOriginCase = bundle.getString("strOriginCase");
            strChooseUnitId = bundle.getString("strChooseUnitId");
            strChooseValueId = bundle.getString("strChooseValueId");
            strConsign = bundle.getString("strConsign");
            strReason = bundle.getString("strReason");
            strWay = bundle.getString("strWay");
            StrId = bundle.getString("StrId");

            if (isSaveData) {
                goto_Button.setVisibility(View.VISIBLE);
                goto_Button.setText("继续提交");
                goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                goto_Button.refreshFontType(activity, "2");
            }
           /* recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            adapter = new SimilarProposalListAdapter(activity);
            adapter.clear();
            adapter.addAll(objList);
            recyclerView.setAdapter(adapter);*/
            initRecyclerView();
        }
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        adapter = new SimilarProposalListAdapter(activity);
        recyclerView.setAdapter(adapter);
        adapter.setList(objList);
    }

    /**
     * 请求结果
     *
     * @param what
     * @param obj
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;

                    toastMessage = "提案提交成功！";
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                showConfirmDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 继续发送提案
     */
    private void sendProposal() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strTitle", GIStringUtil.nullToEmpty(strOriginCase)); //案由
        textParamMap.put("strReportUnit", GIStringUtil.nullToEmpty(strChooseUnitId)); //建议承办单位
        textParamMap.put("strJointMem", GIStringUtil.nullToEmpty(strChooseValueId)); //联名委员        (id/名字/手机号码)
        textParamMap.put("strDiaoyanguocheng", GIStringUtil.nullToEmpty(strDiaoyanguocheng)); //调研过程
        if (myValueMap != null) {
            for (Map.Entry<String, String> entry : myValueMap.entrySet()) {
                textParamMap.put(entry.getKey(), entry.getValue());
            }
        }
        textParamMap.put("strConsign", GIStringUtil.nullToEmpty(strConsign)); //协商方式
        textParamMap.put("strReason", GIStringUtil.nullToEmpty(strReason)); //情况分析
        textParamMap.put("strWay", GIStringUtil.nullToEmpty(strWay)); //具体建议
        textParamMap.put("intState", "1"); //0为暂存 1为上报
        textParamMap.put("intNoReply", "0"); //不需要承办单位答复
        textParamMap.put("strCheckType", "skip");
        if (GIStringUtil.isNotBlank(StrId)) {//StrId不为空表示从草稿箱过来
            textParamMap.put("strId", StrId);
        }
        doRequestNormal(ApiManager.getInstance().getProposalAddServlet(textParamMap), 0);
    }

    @Override
    public void setListener() {
        super.setListener();
    }

    /**
     * 对话框
     */
    private void showConfirmDialog() {
        String title;
        title = "您确定要提交这份提案吗？";

        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title("提示").content(title).btnText("返回修改", "确定提交").showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isBack", true);
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                        //dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        sendProposal();
                    }
                }
        );
    }
}
