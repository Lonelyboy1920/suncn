package com.suncn.ihold_zxztc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.ChooseListAdapter;
import com.suncn.ihold_zxztc.bean.ProposalGoTypeListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseListActivity extends BaseActivity {
    private String goTypeReasonCode;
    private String strGoTypeReason;
    private ChooseListAdapter adapter;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_choose_list);
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
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            goTypeReasonCode = bundle.getString("goTypeReasonCode");
            setHeadTitle(bundle.getString("strTitle"));
        }
        adapter = new ChooseListAdapter(activity);
        recyclerView.setAdapter(adapter);
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.main_bg, 1, 0);

        getGoTypeReason();
    }

    @Override
    public void setListener() {
        super.setListener();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("goTypeReasonCode", ((ProposalGoTypeListBean.ProposalGoType) adapter.getItem(position)).getStrCode());
                bundle.putString("strGoTypeReason", ((ProposalGoTypeListBean.ProposalGoType) adapter.getItem(position)).getStrName());
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 获取不予立案理由
     */
    private void getGoTypeReason() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().NoLianReasonList(textParamMap), 2);
    }

    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        switch (what) {
            case 2:
                prgDialog.closePrgDialog();
                try {
                    ProposalGoTypeListBean proposalGoTypeListBean = (ProposalGoTypeListBean) obj;
                    adapter.setList(proposalGoTypeListBean.getObjList());
                    adapter.setChooseCode(goTypeReasonCode);

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
