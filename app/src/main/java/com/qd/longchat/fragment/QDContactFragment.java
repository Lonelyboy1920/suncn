package com.qd.longchat.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDRefreshCallBack;
import com.longchat.base.callback.QDUserStatusCallBack;
import com.longchat.base.dao.QDDept;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDDeptHelper;
import com.longchat.base.manager.listener.QDRefreshCallBackManager;
import com.longchat.base.manager.listener.QDUserStatusCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDContactActivity;
import com.qd.longchat.activity.QDFriendInviteActivity;
import com.qd.longchat.activity.QDGroupActivity;
import com.qd.longchat.adapter.QDRootContactAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.model.QDContact;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDContactUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDSideBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/17 下午1:36
 */

public class QDContactFragment extends QDBaseFragment implements QDRefreshCallBack, QDUserStatusCallBack {

    @BindView(R2.id.lv_contact_list)
    ListView listView;
    @BindView(R2.id.side_bar)
    QDSideBar sideBar;
    @BindView(R2.id.fl_contact_parent_layout)
    FrameLayout flParentLayout;

    private List<QDContact> rootContactList;
    private QDRootContactAdapter adapter;
    private Dialog dialog;
    private TextView textView;
    private String currentContent;

    private List<String> friendIdList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootContactList = new ArrayList<>();
        adapter = new QDRootContactAdapter(context);
        listView.setAdapter(adapter);
        initDialog();

        friendIdList = new ArrayList<>();

        if (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_OPEN_WATERMARK)) {
            flParentLayout.setBackground(QDBitmapUtil.addWaterMark(context, QDLanderInfo.getInstance().getName(), Color.WHITE));
        }

        QDRefreshCallBackManager.getInstance().addCallBack(this);
        QDUserStatusCallBackManager.getInstatnce().addCallBack(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        if (rootContactList == null) {
            rootContactList = new ArrayList<>();
        } else {
            rootContactList.clear();
        }

        QDContact contact = QDContactUtil.createNewFriendContact();
        rootContactList.add(contact);
        rootContactList.add(QDContactUtil.createSelfDeptContact());
        rootContactList.add(QDContactUtil.createGroupContact());
        rootContactList.add(QDContactUtil.createContact());

        List<QDContact> contactList = QDContactUtil.createRootContactUsers();
        if (contactList.size() == 0) {
            sideBar.setVisibility(View.GONE);
            adapter.setContactList(rootContactList);
            return;
        } else {
            List<String> s = new ArrayList<>();
            friendIdList.clear();
            List<QDContact> sortContactList = QDSortUtil.sortContact(contactList);
            for (QDContact c : sortContactList) {
                if (TextUtils.isEmpty(c.getNameSp())) {
                    s.add(QDUtil.getInitial(c.getName().toUpperCase()));
                } else {
                    s.add(QDUtil.getInitial(c.getNameSp()).toUpperCase());
                }
                friendIdList.add(c.getId());
            }
            rootContactList.addAll(sortContactList);
            QDClient.getInstance().subscribeStatus(friendIdList);
            List<String> indexes = QDUtil.removeDuplicate(s);
            sideBar.setIndexItems(indexes);
            sideBar.setVisibility(View.VISIBLE);
        }
        adapter.setContactList(rootContactList);

        sideBar.setOnSelectIndexItemListener(new QDSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(int index, String content, int action) {
                if (action == MotionEvent.ACTION_UP) {
                    dialog.dismiss();
                } else {
                    if (!content.equals(currentContent)) {
                        currentContent = content;
                        textView.setText(currentContent.toUpperCase());
                        dialog.show();
                        dialog.getWindow().setContentView(textView);
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        WindowManager.LayoutParams params =
                                dialog.getWindow().getAttributes();
                        params.width = 200;
                        params.height = 200;
                        dialog.getWindow().setAttributes(params);
                    }

                }

                if (index == 0) {
                    listView.smoothScrollToPosition(0);
                } else {
                    for (int i = 0; i < rootContactList.size(); i++) {
                        String itemInfo = rootContactList.get(i).getFuncName();
                        if (!TextUtils.isEmpty(itemInfo) && itemInfo.equalsIgnoreCase(content)) {
                            listView.smoothScrollToPositionFromTop(i, 0, 200);
                            return;
                        }
                    }
                }
            }
        });
    }

    private void initDialog() {
        dialog = new Dialog(getContext(), R.style.DialogStyle);
        textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(getContext().getResources().getDimension(R.dimen.text_view_size_focused));
        textView.setBackground(getContext().getResources().getDrawable(R.drawable.im_text_dialog));
    }


    @OnItemClick(R2.id.lv_contact_list)
    public void onItemClick(int position) {
        QDContact contact = adapter.getItem(position);
        int type = contact.getType();
        switch (type) {
            case QDContact.TYPE_CONTACT:
            case QDContact.TYPE_SELF_DEPT:
                if (type == QDContact.TYPE_SELF_DEPT) {
                    QDDept selfDept = QDDeptHelper.getDeptById(contact.getId());
                    if (TextUtils.isEmpty(contact.getFuncName()) || selfDept == null) {
                        return;
                    }
                }
                Intent contactIntent = new Intent(context, QDContactActivity.class);
                contactIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_ID, contact.getId());
                contactIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_TYPE, contact.getType());
                context.startActivity(contactIntent);
                break;
            case QDContact.TYPE_GROUP:
                Intent groupIntent = new Intent(context, QDGroupActivity.class);
                context.startActivity(groupIntent);
                break;
//            case QDContact.TYPE_LINKMAN:
//            case QDContact.TYPE_FRIEND:
//                Intent detailIntent = new Intent(context, QDUserDetailActivity.class);
//                detailIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, contact.getId());
//                context.startActivity(detailIntent);
//                break;
            case QDContact.TYPE_NEW_FRIEND:
                Intent inviteIntent = new Intent(context, QDFriendInviteActivity.class);
                context.startActivity(inviteIntent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        QDUserStatusCallBackManager.getInstatnce().removeLast();
        QDRefreshCallBackManager.getInstance().removeLast();
    }

    @Override
    public void onUserStatusChange(String userId, int status) {
        if (friendIdList != null && friendIdList.contains(userId)) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onUserInfoChange(QDUser user) {
        if (friendIdList != null && friendIdList.contains(user.getId())) {
            initData();
        }
    }

    @Override
    public void onUserSecretChange(String userId, int status) {


    }

    @Override
    public void onRefresh() {
        initData();
    }
}
