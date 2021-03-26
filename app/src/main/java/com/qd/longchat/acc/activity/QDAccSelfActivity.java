package com.qd.longchat.acc.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.manager.QDAccManager;
import com.longchat.base.model.gd.QDAccModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.acc.adapter.QDAccAdapter;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/26 下午2:36
 */
public class QDAccSelfActivity extends QDBaseActivity {

    @BindView(R2.id.view_as_title)
    View viewTitle;
    @BindView(R2.id.lv_as_list)
    ListView lvList;

    @BindString(R2.string.acc_self)
    String strTitle;
    @BindString(R2.string.acc_get_list_error)
    String strGetListError;

    private QDAccAdapter adapter;
    protected boolean isHaveTag;

    private QDResultCallBack<List<QDAccModel>> callBack = new QDResultCallBack<List<QDAccModel>>() {
        @Override
        public void onError(String errorMsg) {
            getWarningDailog().dismiss();
            QDUtil.showToast(context, strGetListError + errorMsg);
        }

        @Override
        public void onSuccess(List<QDAccModel> qdAccModels) {
            getWarningDailog().dismiss();
            if (isHaveTag) {
                adapter.setModelList(QDSortUtil.sortAcc(qdAccModels));
            } else {
                List<QDAccModel> modelList = new ArrayList<>();
                for (QDAccModel model : qdAccModels) {
                    if (model.getIsFollow() == 1) {
                        modelList.add(model);
                    }
                }
                adapter.setModelList(modelList);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_self);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        tvTitleRight.setBackgroundResource(R.drawable.ic_add);
        tvTitleRight.setVisibility(View.VISIBLE);

        adapter = new QDAccAdapter(context, isHaveTag);
        lvList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWarningDailog().show();
        QDAccManager.getInstance().getAccList(callBack);
    }

    @OnClick(R2.id.tv_title_right)
    public void onClick() {
        Intent intent = new Intent(context, QDAccListActivity.class);
        startActivity(intent);
    }

    @OnItemClick(R2.id.lv_as_list)
    public void onItemClick(int position) {
        Intent intent = new Intent(context, QDAccDetailActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_ACC_ID, adapter.getItem(position).getId());
        startActivity(intent);
    }

}
