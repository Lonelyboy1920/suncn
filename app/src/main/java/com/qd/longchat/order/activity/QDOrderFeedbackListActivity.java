package com.qd.longchat.order.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.manager.QDOrderManager;
import com.longchat.base.model.gd.QDOrderFeedbackModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.order.adapter.QDOrderFeedbackAdapter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/27 下午3:57
 */
public class QDOrderFeedbackListActivity extends QDBaseActivity {

    @BindView(R2.id.view_fl_title)
    View viewTitle;
    @BindView(R2.id.lv_fl_list)
    ListView lvList;
    @BindView(R2.id.btn_fl_feedback)
    Button btnFeedback;

    @BindString(R2.string.im_text_order_feedback_1)
    String strTitle;

    private QDOrderFeedbackAdapter adapter;
    private String orderId;
    private Context context;
    private QDOrderFeedbackModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_feedback_list);
        ButterKnife.bind(this);
        context = this;
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        orderId = getIntent().getStringExtra("orderId");
        if (QDLanderInfo.getInstance().getId().equalsIgnoreCase(getIntent().getStringExtra("createUserId"))) {
            btnFeedback.setVisibility(View.GONE);
        } else {
            btnFeedback.setVisibility(View.VISIBLE);
        }

        adapter = new QDOrderFeedbackAdapter(this);
        lvList.setAdapter(adapter);

        getFeedbackList();
    }

    private void getFeedbackList() {
        QDOrderManager.getInstance().getFeedbackList(orderId, new QDResultCallBack<QDOrderFeedbackModel>() {
            @Override
            public void onError(String errorMsg) {

            }

            @Override
            public void onSuccess(QDOrderFeedbackModel feedbackModel) {
                model = feedbackModel;
                adapter.setBeanList(model.getList());
                adapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R2.id.btn_fl_feedback)
    public void onClick() {
        Intent i = new Intent(context, QDOrderFeedbackActivity.class);
        i.putExtra("orderId", orderId);
        startActivityForResult(i, 1000);
    }

    @OnItemClick(R2.id.lv_fl_list)
    public void onItemClick(int position) {
        Intent intent = new Intent(context, QDOrderFeedbackDetailActivity.class);
        intent.putExtra("bean", adapter.getItem(position));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1000) {
            getFeedbackList();
        }
    }
}
