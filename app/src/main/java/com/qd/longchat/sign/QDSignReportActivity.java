package com.qd.longchat.sign;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.longchat.base.callback.QDMessageCallBack;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.listener.QDMessageCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.sign.adapter.QDSignReportAdapter;
import com.qd.longchat.util.QDIntentKeyUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/25 上午11:18
 */
public class QDSignReportActivity extends QDBaseActivity implements QDMessageCallBack {

    @BindView(R2.id.view_report_title)
    View viewTitle;
    @BindView(R2.id.lv_report_listview)
    ListView lvList;

    private List<QDMessage> msgList;
    private QDSignReportAdapter adapter;
    private String appId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_report);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleName.setText("现场记录");
        appId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_APP_ID);
        msgList = QDMessageHelper.getMessageWithAppId(appId);

        adapter = new QDSignReportAdapter(this, msgList);
        lvList.setAdapter(adapter);


        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QDMessage message = msgList.get(position);
                Intent intent = new Intent(context, QDSignListActivity.class);
                if (message.getSenderId().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_INDEX, 0);
                } else {
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_INDEX, 1);
                }
                startActivity(intent);
            }
        });
        scrollListViewToBottom();

        QDMessageCallBackManager.getInstance().addCallBack(this);
    }

    public void scrollListViewToBottom() {
        lvList.post(new Runnable() {
            @Override
            public void run() {
                lvList.setSelection(adapter.getCount() - 1);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        QDMessageHelper.setMessageReadWithAppId(appId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDMessageCallBackManager.getInstance().removeLast();
    }

    @Override
    public void onReceive(List<QDMessage> message) {
        for (QDMessage msg : message) {
            if (msg.getCtype() == QDMessage.CTYPE_APP && msg.getCdata().equalsIgnoreCase(appId)) {
                msgList.add(msg);
            }
        }
        adapter.setMsgList(msgList);
        scrollListViewToBottom();
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
