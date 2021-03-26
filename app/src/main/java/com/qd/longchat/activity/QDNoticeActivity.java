package com.qd.longchat.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.longchat.base.callback.QDMessageCallBack;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.listener.QDMessageCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDNoticeAdapter;
import com.qd.longchat.util.QDIntentKeyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/3/9 上午10:03
 */
public class QDNoticeActivity extends QDBaseActivity implements QDMessageCallBack {

    @BindView(R2.id.view_ac_title)
    View viewTitle;
    @BindView(R2.id.lv_ac_list)
    ListView listView;
    @BindView(R2.id.ll_ac_bottom_layout)
    LinearLayout bottomLayout;

    private String accId;
    private QDNoticeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_chat);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleName.setText(R.string.str_notice_title);
        bottomLayout.setVisibility(View.GONE);

        accId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_ACC_ID);
        adapter = new QDNoticeAdapter(this);
        listView.setAdapter(adapter);

        List<QDMessage> messageList = QDMessageHelper.getMessageWithAppId(accId);
        adapter.setDataList(messageList);
        scrollListViewToBottom();

        QDMessageCallBackManager.getInstance().addCallBack(this);
    }

    public void scrollListViewToBottom() {
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getCount() - 1);
            }
        }, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        QDMessageHelper.setMessageReadWithAppId(accId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDMessageCallBackManager.getInstance().removeCallBack(this);
    }

    @Override
    public void onReceive(List<QDMessage> messageList) {
        List<QDMessage> msgList = new ArrayList<>();
        for (QDMessage message : messageList) {
            if (message.getCtype() == QDMessage.CTYPE_APP && message.getCdata().equalsIgnoreCase(accId)) {
                msgList.add(message);
            }
        }
        if (msgList.size() != 0) {
            adapter.addDataList(msgList);
            scrollListViewToBottom();
        }
    }

    @Override
    public void onReceiveGMsg(String groupId, List<QDMessage> messageList) {

    }

    @Override
    public void onMsgReaded(String userId) {

    }

    @Override
    public void onRevokeMessage(String msgId, String errorMsg) {

    }

    @Override
    public void onDeleteMessage(String groupId, String msgId) {

    }
}
