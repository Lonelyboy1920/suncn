package com.qd.longchat.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.longchat.base.callback.QDMessageCallBack;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.listener.QDMessageCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDMassChatAdapter;
import com.qd.longchat.config.QDLanderInfo;

import com.qd.longchat.R2;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/2/27 上午10:18
 */
public class QDMassChatActivity extends QDBaseActivity implements QDMessageCallBack {

    @BindView(R2.id.view_title)
    View viewTitle;
    @BindView(R2.id.lv_list)
    PullToRefreshListView listView;
    @BindView(R2.id.ll_chat_bottom_layout)
    LinearLayout llBottomLayout;

    @BindDrawable(R2.drawable.arrow_left)
    Drawable backDraw;

    private QDMassChatAdapter adapter;

    private QDMassChatAdapter.OnMultiListener listener = new QDMassChatAdapter.OnMultiListener() {
        @Override
        public void onMulti(QDMessage message) {
            adapter.setModel(QDChatActivity.MODE_DEL);
            adapter.addSelectedList(message);
            updateLayout(QDChatActivity.MODE_DEL);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mass_chat);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(R.string.mass_message);

        adapter = new QDMassChatAdapter(context, listener);
        listView.setAdapter(adapter);
        listView.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        initData();

        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llBottomLayout.getVisibility() == View.VISIBLE) {
                    updateLayout(0);
                    adapter.setModel(0);
                    adapter.clearSelectedList();
                } else {
                    onBackPressed();
                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == SCROLL_STATE_IDLE) {
                    Glide.with(context).resumeRequests();
                } else {
                    Glide.with(context).pauseRequestsRecursive();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        QDMessageCallBackManager.getInstance().addCallBack(this);
    }

    private void initData() {
        List<QDMessage> msgList = QDMessageHelper.getMassMessages();
        adapter.setMsgList(msgList);
        scrollListViewToBottom();
    }

    @OnClick({R2.id.tv_del})
    public void onClick(View view) {
        List<String> cdataList = adapter.getCdataList();
        QDMessageHelper.deleteMessageByCdatas(cdataList);
        updateLayout(0);
        adapter.deleteMessages();
        adapter.setModel(0);
        adapter.clearSelectedList();
    }

    public void scrollListViewToBottom() {
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.getRefreshableView().setSelection(adapter.getCount() - 1);
            }
        }, 100);
    }

    private void updateLayout(int mode) {
        if (mode == QDChatActivity.MODE_DEL) {
            llBottomLayout.setVisibility(View.VISIBLE);
            tvTitleBack.setText(R.string.cancel);
            tvTitleBack.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            llBottomLayout.setVisibility(View.GONE);
            tvTitleBack.setText(R.string.title_back);
            tvTitleBack.setCompoundDrawablesWithIntrinsicBounds(backDraw, null, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDMessageCallBackManager.getInstance().removeCallBack(this);
    }

    @Override
    public void onReceive(List<QDMessage> msgList) {
        List<QDMessage> messageList = new ArrayList<>();
        for (QDMessage msg : msgList) {
            if (msg.getCtype() == QDMessage.CTYPE_MUSER && msg.getSenderId().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                messageList.add(msg);
            }
        }
        if (messageList.size() != 0) {
            adapter.addMsgList(messageList);
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
