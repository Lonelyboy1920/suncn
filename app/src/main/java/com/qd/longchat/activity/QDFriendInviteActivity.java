package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.longchat.base.callback.QDFriendCallBack;
import com.longchat.base.callback.QDRefreshCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDFriend;
import com.longchat.base.dao.QDFriendInvite;
import com.longchat.base.databases.QDFriendInviteHelper;
import com.longchat.base.manager.listener.QDFriendCallBackManager;
import com.longchat.base.manager.listener.QDRefreshCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDFriendInviteAdapter;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/12 下午4:13
 */
public class QDFriendInviteActivity extends QDBaseActivity implements QDFriendCallBack, QDRefreshCallBack {

    @BindView(R2.id.view_fi_title)
    View viewTitle;
    @BindView(R2.id.lv_fi_list)
    SwipeMenuListView lvList;

    @BindColor(R2.color.colorSwipeMenuDelete)
    int colorMenuDel;
    @BindString(R2.string.conversation_del)
    String strDel;
    @BindString(R2.string.friend_invite_title)
    String strTitle;
    @BindString(R2.string.operation_error)
    String strOperationError;

    private QDFriendInviteAdapter adapter;
    private List<QDFriendInvite> friendInvites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                menu.addMenuItem(QDUtil.createMenuItem(context, QDUtil.dp2px(context, 90), strDel, colorMenuDel));
            }
        };
        lvList.setMenuCreator(creator);

        friendInvites = QDFriendInviteHelper.loadAll();
        adapter = new QDFriendInviteAdapter(context, friendInvites, new QDResultCallBack() {
            @Override
            public void onError(String errorMsg) {
                QDUtil.showToast(context, strOperationError + errorMsg);
            }

            @Override
            public void onSuccess(Object o) {

            }
        });
        lvList.setAdapter(adapter);

        lvList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                QDFriendInvite invite = friendInvites.get(position);
                QDFriendInviteHelper.deleteInvite(invite.getUserId());
                friendInvites.remove(position);
                adapter.setInviteList(friendInvites);
                return false;
            }
        });

        QDFriendCallBackManager.getInstance().addCallBack(this);
        QDRefreshCallBackManager.getInstance().addCallBack(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        QDFriendInviteHelper.setAllRead();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDFriendCallBackManager.getInstance().removeLast();
        QDRefreshCallBackManager.getInstance().removeLast();
    }

    @OnItemClick(R2.id.lv_fi_list)
    public void onItemClick(int position) {
        QDFriendInvite invite = friendInvites.get(position);
        Intent intent = new Intent(context, QDFriendInviteHandleActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_FRIEND_INVITE, invite);
        startActivity(intent);
    }

    @Override
    public void friendRelationChange(QDFriend friend, boolean isFriend) {

    }

    @Override
    public void friendInviteStatusChange(String userId, int reply) {
        for (QDFriendInvite invite : friendInvites) {
            if (invite.getUserId().equalsIgnoreCase(userId)) {
                invite.setReply(reply);
                break;
            }
        }
        adapter.setInviteList(friendInvites);
    }

    @Override
    public void onRefresh() {
        friendInvites = QDFriendInviteHelper.loadAll();
        adapter.setInviteList(friendInvites);
    }
}
