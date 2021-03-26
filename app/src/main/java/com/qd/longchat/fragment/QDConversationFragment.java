package com.qd.longchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDConversationCallBack;
import com.longchat.base.dao.QDConversation;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDConversationHelper;
import com.longchat.base.databases.QDFriendHelper;
import com.longchat.base.databases.QDGroupNoticeHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.listener.QDConversationCallBackManager;
import com.longchat.base.notify.QDNFAppInfo;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.acc.activity.QDAccChatActivity;
import com.qd.longchat.activity.QDGroupChatActivity;
import com.qd.longchat.activity.QDGroupNotifyActivity;
import com.qd.longchat.activity.QDMassChatActivity;
import com.qd.longchat.activity.QDNoticeActivity;
import com.qd.longchat.activity.QDPersonChatActivity;
import com.qd.longchat.activity.QDWebActivity;
import com.qd.longchat.adapter.QDConversationAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.sign.QDSignReportActivity;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/17 下午1:25
 */

public class QDConversationFragment extends QDBaseFragment implements QDConversationCallBack{

    @BindView(R2.id.swl_conversation_list)
    SwipeMenuListView listView;

    @BindColor(R2.color.colorSwipeMenuDelete)
    int colorMenuDel;
    @BindColor(R2.color.colorSwipeMenuTop)
    int colorMenuTop;
    @BindString(R2.string.conversation_del)
    String strDel;
    @BindString(R2.string.conversation_top)
    String strTop;
    @BindString(R2.string.conversation_untop)
    String strUntop;

    private List<QDConversation> conversationList;
    private QDConversationAdapter adapter;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gson = QDGson.getGson();
        conversationList = new ArrayList<>();
        adapter = new QDConversationAdapter(context);
        listView.setAdapter(adapter);
        createMenuCreator();

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                QDConversation conversation = adapter.getItem(position);
                if (index == 0) {
                    if (conversation.getIsTop() == QDConversation.TOP) {
                        conversation.setIsTop(QDConversation.UNTOP);
                    } else {
                        conversation.setIsTop(QDConversation.TOP);
                    }
                    QDConversationHelper.updateConversation(conversation);
                } else {
                    if (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_SECRET)) {
                        QDUser user = QDUserHelper.getUserById(conversation.getId());
                        boolean isFriend = QDFriendHelper.isFriend(user.getId());
                        if (!isFriend && QDLanderInfo.getInstance().getLevel() < user.getLevel()) {
                            QDClient.getInstance().deleteConversation(conversation.getId() + ";" + conversation.getName());
                        }
                    }
                    QDConversationHelper.deleteConversationById(conversation.getId());
                    HashMap<String, String> map;
                    String draft = QDUtil.readDraftFile();
                    if (!TextUtils.isEmpty(draft)) {
                        map = QDUtil.stringToMap(draft);
                        if (map != null && map.containsKey(conversation.getId())) {
                            map.remove(conversation.getId());
                            QDUtil.writeDraftFile(QDUtil.mapToString(map));
                        }
                    }
                    switch (conversation.getType()) {
                        case QDConversation.TYPE_SELF:
                        case QDConversation.TYPE_PERSONAL:
                            QDMessageHelper.setMessageIgnoreWithUserId(conversation.getId());
                            break;
                        case QDConversation.TYPE_GROUP:
                            QDMessageHelper.setMessageReadWithGroupId(conversation.getId());
                            break;
                        case QDConversation.TYPE_APP:
                            QDMessageHelper.setMessageReadWithAppId(conversation.getId());
                            break;
                        case QDConversation.TYPE_GROUP_NOTICE:
                            QDGroupNoticeHelper.setReaded();
                            break;
                    }
                }
                initConversationInfo();
                return false;
            }
        });

        QDConversationCallBackManager.getInstance().setCallBack(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initConversationInfo();
    }

    private void initConversationInfo() {
        conversationList = QDConversationHelper.loadConversations();
        adapter.setConversationList(conversationList);
        int unreadNum = QDMessageHelper.getUnreadMessageCount() + QDGroupNoticeHelper.getUnreadCount();

        QDUtil.setUnreadNum(context, unreadNum);
        int system = QDUtil.getSystem();
        if (system == 1) {
            QDUtil.setHuaweiBadge(context, unreadNum);
        }
    }

    private void createMenuCreator() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                switch (menu.getViewType()) {
                    case QDConversation.TOP:
                        menu.addMenuItem(QDUtil.createMenuItem(context, QDUtil.dp2px(context, 90), strUntop, colorMenuTop));
                        break;
                    case QDConversation.UNTOP:
                        menu.addMenuItem(QDUtil.createMenuItem(context, QDUtil.dp2px(context, 90), strTop, colorMenuTop));
                        break;
                }
                menu.addMenuItem(QDUtil.createMenuItem(context, QDUtil.dp2px(context, 90), strDel, colorMenuDel));
            }
        };
        listView.setMenuCreator(creator);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnItemClick(R2.id.swl_conversation_list)
    public void onItemClick(int position) {
        QDConversation conversation = adapter.getItem(position);
        int type = conversation.getType();
        switch (type) {
            case QDConversation.TYPE_SELF:
            case QDConversation.TYPE_PERSONAL:
                Intent intent = new Intent(context, QDPersonChatActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, conversation.getId());
                startActivity(intent);
                break;
            case QDConversation.TYPE_GROUP:
                Intent groupIntent = new Intent(context, QDGroupChatActivity.class);
                groupIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, conversation.getId());
                startActivity(groupIntent);
                break;
            case QDConversation.TYPE_APP:
                QDNFAppInfo info = gson.fromJson(conversation.getExtData(), QDNFAppInfo.class);
                QDMessageHelper.setMessageReadWithAppId(conversation.getId());
                if (info.getAppCode().equalsIgnoreCase("addon_sign")) {
                    Intent i = new Intent(context, QDSignReportActivity.class);
                    i.putExtra(QDIntentKeyUtil.INTENT_KEY_APP_ID, conversation.getId());
                    startActivity(i);
                } else if (info.getAppCode().equalsIgnoreCase("addon_acc")) {
                    Intent i = new Intent(context, QDAccChatActivity.class);
                    i.putExtra(QDIntentKeyUtil.INTENT_ACC_ID, info.getAccId());
                    i.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, info.getAccName());
                    startActivity(i);
                }else if (info.getAppCode().equalsIgnoreCase("addon_notice")) {
                    Intent i = new Intent(context, QDNoticeActivity.class);
                    i.putExtra(QDIntentKeyUtil.INTENT_ACC_ID, conversation.getId());
                    startActivity(i);
                } else {
                    String url = QDUtil.replaceWebServerAndToken(info.getUrlMobile());
                    toWebActivity(url);
                }
                break;
            case QDConversation.TYPE_GROUP_NOTICE:
                Intent noticeIntent = new Intent(context, QDGroupNotifyActivity.class);
                startActivity(noticeIntent);
                break;
            case QDConversation.TYPE_MUSER:
                Intent massIntent = new Intent(context, QDMassChatActivity.class);
                startActivity(massIntent);
                break;
        }
    }

    private void toWebActivity(String url) {
        Intent intent = new Intent(context, QDWebActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_WEB_URL, url);
        startActivity(intent);
    }

    @Override
    public void removeConversation(String id) {
        QDConversation conversation = QDConversationHelper.getConversationById(id);
        if (conversation != null) {
            conversationList.remove(conversation);
            adapter.setConversationList(conversationList);
            QDConversationHelper.deleteConversationById(id);
        }
    }

    @Override
    public void updateConversation() {
        initConversationInfo();
    }

}
