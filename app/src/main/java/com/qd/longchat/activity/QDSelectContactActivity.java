package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDConversation;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDConversationHelper;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDSelectContactAdapter;
import com.qd.longchat.model.QDContact;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.view.QDAlertView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/3 下午3:21
 */
public class QDSelectContactActivity extends QDBaseActivity {

    @BindView(R2.id.view_sc_title)
    View viewTitle;
    @BindView(R2.id.view_sc_search)
    View viewSearch;
    @BindView(R2.id.rl_sc_contact)
    RelativeLayout rlContact;
    @BindView(R2.id.rl_sc_group)
    RelativeLayout rlGroup;
    @BindView(R2.id.lv_sc_list)
    ListView lvList;
    @BindView(R2.id.rl_contact_bottom_layout)
    RelativeLayout rlBottomLayout;
    @BindView(R2.id.tv_sc_tag)
    TextView tvTag;

    @BindString(R2.string.contact_title)
    String strContactTitle;
    @BindString(R2.string.contact_is_forward)
    String strIsForward;

    private QDSelectContactAdapter adapter;
    private List<String> excludedUserIds;
    private List<String> selectUserIds;
    private int mode;
    private boolean isForward;
    private QDMessage message;
    private List<QDConversation> conversationList;
    private List<QDUser> selectUserList;
    private QDAlertView alertView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        setStatusBar(true,false);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleName.setText(strContactTitle);

        mode = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_NORMAL);
        excludedUserIds = getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_EXCLUDED_ID_LIST);
        selectUserIds = getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST);
        isForward = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, false);
        if (isForward) {
            message = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE);
        }

        if (excludedUserIds == null) {
            excludedUserIds = new ArrayList<>();
        }

        if (selectUserIds == null) {
            selectUserIds = new ArrayList<>();
        }

        if (mode == QDContactActivity.MODE_MULTI) {
            rlGroup.setVisibility(View.GONE);
            selectUserList = new ArrayList<>();
        } else if (mode == QDContactActivity.MODE_SINGLE) {
            rlBottomLayout.setVisibility(View.GONE);
        }

        conversationList = QDConversationHelper.getConversationList();
        adapter = new QDSelectContactAdapter(context, conversationList, mode);
        adapter.setExcludedUserIds(excludedUserIds);
        adapter.setSelectUserIds(selectUserIds);
        lvList.setAdapter(adapter);
    }

    @OnClick({R2.id.rl_sc_contact, R2.id.rl_sc_group, R2.id.rl_sc_linkman, R2.id.view_sc_search})
    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.rl_sc_contact) {
            Intent contactIntent = new Intent(context, QDContactActivity.class);
            contactIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_ID, "");
            contactIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_TYPE, QDContact.TYPE_CONTACT);
            contactIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, mode);
            contactIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, true);
            contactIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, message);
            if (mode == QDContactActivity.MODE_MULTI) {
                contactIntent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_EXCLUDED_ID_LIST, (ArrayList<String>) excludedUserIds);
                contactIntent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) selectUserIds);
                contactIntent.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST, (ArrayList<? extends Parcelable>) selectUserList);
            }
            contactIntent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_EXCLUDED_ID_LIST, (ArrayList<String>) excludedUserIds);
            contactIntent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) selectUserIds);
            context.startActivity(contactIntent);

        } else if (i1 == R.id.rl_sc_group) {
            Intent groupIntent = new Intent(context, QDGroupActivity.class);
            groupIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, true);
            groupIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, message);
            startActivity(groupIntent);

        } else if (i1 == R.id.rl_sc_linkman) {
            Intent i = new Intent(context, QDSelectLinkManActivity.class);
            i.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, message);
            startActivity(i);

        } else if (i1 == R.id.view_sc_search) {
            Intent searchIntent = new Intent(context, QDSearchActivity.class);
            searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TYPE, QDSearchActivity.SEARCH_TYPE_USER);
            if (isForward) {
                searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_SINGLE);
                searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, isForward);
                searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, message);
                startActivity(searchIntent);
            }

        }
    }

    @OnItemClick(R2.id.lv_sc_list)
    public void onItemClick(int position) {
        QDConversation conversation = conversationList.get(position);
        if (mode == QDContactActivity.MODE_SINGLE) {
            if (isForward) {
                initAlert(conversation);
            }
        } else if (mode == QDContactActivity.MODE_MULTI) {
            if (excludedUserIds.contains(conversation.getId())) {
                return;
            }
            QDUser user = new QDUser();
            user.setId(conversation.getId());
            user.setName(conversation.getName());
            user.setPic(conversation.getIcon());
            if (selectUserIds.contains(conversation.getId())) {
                selectUserIds.remove(conversation.getId());
                selectUserList.remove(user);
            } else {
                selectUserIds.add(conversation.getId());
                selectUserList.add(user);
            }
            adapter.setSelectUserIds(selectUserIds);
        }
    }

    private void initAlert(final QDConversation conversation) {
        if (alertView == null) {
            alertView = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Alert)
                    .setContent(strIsForward)
                    .isHaveCancelBtn(true)
                    .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                        @Override
                        public void onClick(boolean isSure) {
                            if (isSure) {
                                QDMessage msg;
                                String chaId;
                                if (conversation.getType() == QDConversation.TYPE_PERSONAL) {
                                    if (message.getDirection() == QDMessage.DIRECTION_IN) {
                                        chaId = message.getSenderId();
                                    } else {
                                        chaId = message.getReceiverId();
                                    }
                                    QDUser user = new QDUser();
                                    user.setId(conversation.getId());
                                    user.setName(conversation.getName());
                                    user.setPic(conversation.getIcon());
                                    msg = QDClient.getInstance().forwardMessage(user, message, new QDResultCallBack() {
                                        @Override
                                        public void onError(String errorMsg) {

                                        }

                                        @Override
                                        public void onSuccess(Object o) {

                                        }
                                    });
                                } else {
                                    chaId = message.getGroupId();
                                    QDGroup group = new QDGroup();
                                    group.setId(conversation.getId());
                                    group.setName(conversation.getName());
                                    group.setIcon(conversation.getIcon());
                                    msg = QDClient.getInstance().forwardGMessage(group, message, new QDResultCallBack() {
                                        @Override
                                        public void onError(String errorMsg) {

                                        }

                                        @Override
                                        public void onSuccess(Object o) {

                                        }
                                    });
                                }

                                if (conversation.getId().equalsIgnoreCase(chaId)) {
                                    msg.setStatus(QDMessage.MSG_STATUS_SEND_OK);
                                    Intent intent = new Intent();
                                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, msg);
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                            }
                        }
                    }).build();
        }
        alertView.show();
    }

}
