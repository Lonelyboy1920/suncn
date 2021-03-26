package com.suncn.ihold_zxztc.activity.application.publicopinion;

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
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.SimilarOpinionListAdapter;
import com.suncn.ihold_zxztc.adapter.SimilarProposalListAdapter;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.SimilarProposalListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 相似社情民意
 */
public class SimilarOpinionActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.tv_msg)
    private GITextView msg_TextView;//搜索结果
    private SimilarOpinionListAdapter adapter;
    private ArrayList<SimilarProposalListBean.SimilarProposal> objList;
    private String strCheckMsg;
    private boolean isSaveData;

    private String strId;
    private String strDiaoyanguocheng = "";
    private String title;
    private String content;
    private String strChooseValueId;
    private String themeTypeId;

    public HashMap<String, String> myValueMap;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_similar_proposals);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("相似社情民意");
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
            title=bundle.getString("title","");
            content=bundle.getString("content","");
            strChooseValueId=bundle.getString("strChooseValueId","");
            themeTypeId=bundle.getString("themeTypeId");
            strId = bundle.getString("strId");
            if (isSaveData) {
                goto_Button.setVisibility(View.VISIBLE);
                goto_Button.setText("继续提交");
                goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                goto_Button.refreshFontType(activity, "2");
            }
            initRecyclerView();
        }
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 10, 0);
        adapter = new SimilarOpinionListAdapter(activity);
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
                    toastMessage = "提交成功！";
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
     * 社情民意提交
     */
    private void submitSqmy() {
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<>();
        if (GIStringUtil.isNotBlank(strId)) {
            textParamMap.put("strId", strId);
        }
        textParamMap.put("strTitle", title);
        textParamMap.put("strContent", content);
        textParamMap.put("strState", "1");
        if (GIStringUtil.isNotBlank(strChooseValueId) && strChooseValueId.substring(strChooseValueId.length() - 2, strChooseValueId.length() - 1).equals(",")) {
            textParamMap.put("strJointMem", strChooseValueId.substring(0, strChooseValueId.length() - 1));
        } else {
            textParamMap.put("strJointMem", strChooseValueId);
        }
        textParamMap.put("strTopicType", themeTypeId);
        textParamMap.put("strCheckType", "skip");
        doRequestNormal(ApiManager.getInstance().sumbitPublicOpinionInfo(textParamMap), 0);
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
        title = "您确定要提交这份社情民意吗？";

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
                        submitSqmy();
                    }
                }
        );
    }
}
