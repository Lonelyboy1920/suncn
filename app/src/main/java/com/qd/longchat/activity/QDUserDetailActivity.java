package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDFriendCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.callback.QDUserInfoCallBack;
import com.longchat.base.callback.QDUserStatusCallBack;
import com.longchat.base.dao.QDFriend;
import com.longchat.base.dao.QDLinkman;
import com.longchat.base.dao.QDRelation;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDFriendHelper;
import com.longchat.base.databases.QDLinkmanHelper;
import com.longchat.base.databases.QDRelationHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.listener.QDFriendCallBackManager;
import com.longchat.base.manager.listener.QDRefreshCallBackManager;
import com.longchat.base.manager.listener.QDUserInfoCallBackManager;
import com.longchat.base.manager.listener.QDUserStatusCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.holder.QDBaseInfoHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.qd.longchat.R2;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/1 上午9:26
 */

public class QDUserDetailActivity extends QDBaseActivity implements QDFriendCallBack, QDUserInfoCallBack, QDUserStatusCallBack {

    @BindView(R2.id.view_ud_title)
    View viewTitle;
    @BindView(R2.id.iv_ud_icon)
    ImageView ivIcon;
    @BindView(R2.id.tv_ud_name)
    TextView tvName;
    @BindView(R2.id.tv_ud_subname)
    TextView tvSubname;
    @BindView(R2.id.btn_ud_friend)
    Button btnFriend;
    @BindView(R2.id.view_ud_dept)
    View viewDept;
    @BindView(R2.id.view_ud_job)
    View viewJob;
    @BindView(R2.id.view_ud_mobile)
    View viewMobile;
    @BindView(R2.id.view_ud_email)
    View viewEmail;
    @BindView(R2.id.view_ud_ophone)
    View viewOphone;
    @BindView(R2.id.view_ud_sex)
    View viewSex;
    @BindView(R2.id.view_ud_birthday)
    View viewBirthday;
    @BindView(R2.id.btn_ud_send_msg)
    Button btnSendMsg;
    @BindView(R2.id.btn_ud_add_contact)
    Button btnContact;

    @BindString(R2.string.user_detail_title)
    String strTitle;
    @BindString(R2.string.user_detail_dept)
    String strDept;
    @BindString(R2.string.user_detail_mobile)
    String strMobile;
    @BindString(R2.string.user_detail_job)
    String strJob;
    @BindString(R2.string.user_detail_ophone)
    String strOphone;
    @BindString(R2.string.user_detail_email)
    String strEmail;
    @BindString(R2.string.user_detail_sex)
    String strSex;
    @BindString(R2.string.user_detail_birthday)
    String strBirthday;
    @BindString(R2.string.user_detail_add_friend)
    String strAddFriend;
    @BindString(R2.string.user_detail_del_friend)
    String strDelFriend;
    @BindString(R2.string.user_detail_not_allow_chat_self)
    String strNotAllow;
    @BindString(R2.string.user_detail_no_permission_chat)
    String strNoPermission;
    @BindString(R2.string.ace_self_ban_mobile)
    String strAceMobile;
    @BindString(R2.string.ace_self_ban_ophone)
    String strAceOphone;
    @BindString(R2.string.ace_self_ban_email)
    String strAceEmail;
    @BindString(R2.string.user_detail_add_contact)
    String strAddContact;
    @BindString(R2.string.user_detail_remove_contact)
    String strRemoveContact;
    @BindString(R2.string.user_detail_not_empty)
    String strNoteEmpty;
    @BindString(R2.string.user_detail_del_friend_error)
    String strDelFriendError;
    @BindString(R2.string.user_detail_add_linkman_error)
    String strAddLinkmanError;
    @BindString(R2.string.user_detail_del_linkman_error)
    String strDelLinkmanError;

    @BindColor(R2.color.colorRed)
    int colorRed;
    @BindColor(R2.color.colorBtnBlue)
    int colorBlue;
    @BindView(R2.id.view_place)
    View view_place;

    private QDUser user;
    private boolean isFriend;
    private boolean isLinkman;
    private String userId;
    private int profileView;
    private String deptName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true, false);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        view_place.setVisibility(View.VISIBLE);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);

        userId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID);
        user = QDUserHelper.getUserById(userId);
        isFriend = QDFriendHelper.isFriend(userId);
        isLinkman = QDLinkmanHelper.isLinkman(userId);

        profileView = QDLanderInfo.getInstance().getProfileView();

        if (user != null) {
            initUI();
        } else {
            QDClient.getInstance().fetchUserInfo(userId);
        }
        QDClient.getInstance().subscribeStatus(userId);
        QDUserInfoCallBackManager.getInstance().addCallBack(this);
        QDFriendCallBackManager.getInstance().addCallBack(this);
        QDUserStatusCallBackManager.getInstatnce().addCallBack(this);
    }

    private void initUI() {
        initUserInfo();
        initDept();
//        initJob();
//        initMobile();
//        initOphone();
//        initEmail();
//        initSex();
//        initBirthday();
        //initBtnContact();
    }

    private void initBtnContact() {
        if (userId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
            btnContact.setVisibility(View.GONE);
        } else {
            btnContact.setVisibility(View.VISIBLE);
        }

        if (isFriend) {
            btnContact.setText(strDelFriend);
            btnContact.setBackgroundResource(R.drawable.ic_rounded_rectangle_red_btn);
        }

        if (isLinkman) {
            btnContact.setText(strRemoveContact);
            btnContact.setBackgroundResource(R.drawable.ic_rounded_rectangle_red_btn);
        }

        if (!isFriend && !isLinkman) {
            String selfId = QDLanderInfo.getInstance().getId();
            if (selfId.toLowerCase().startsWith("im_") || userId.toLowerCase().startsWith("im_")) {
                btnContact.setText(strAddFriend);
                btnContact.setBackgroundResource(R.drawable.ic_rounded_rectangle_btn);
            } else {
                btnContact.setText(strAddContact);
                btnContact.setBackgroundResource(R.drawable.ic_rounded_rectangle_btn);
            }
        }
//        if (isLinkman) {
//            btnContact.setText(strRemoveContact);
//            btnContact.setBackgroundResource(R.drawable.ic_rounded_rectangle_red_btn);
//        } else {
//            btnContact.setText(strAddContact);
//            btnContact.setBackgroundResource(R.drawable.ic_rounded_rectangle_btn);
//        }
    }

    private void initUserInfo() {
        QDBitmapUtil.getInstance().createAvatar(context, user.getId(), user.getName(), user.getPic(), ivIcon);
        tvName.setText(user.getName());
        if (TextUtils.isEmpty(user.getNote())) {
            tvSubname.setText(strNoteEmpty);
        } else {
            tvSubname.setText(user.getNote());
        }
        btnFriend.setVisibility(View.GONE);
//        if (userId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
//            btnFriend.setVisibility(View.GONE);
//        } else {
//            btnFriend.setVisibility(View.VISIBLE);
//        }

//        if (isFriend) {
//            btnFriend.setText(strDelFriend);
//            btnFriend.setTextColor(colorRed);
//        } else {
//            btnFriend.setText(strAddFriend);
//            btnFriend.setTextColor(colorBlue);
//        }

    }

    private void initDept() {
        if (userId.toLowerCase().startsWith("im_")) {
            viewDept.setVisibility(View.GONE);
        } else {
            // viewDept.setVisibility(View.VISIBLE);
            QDBaseInfoHolder holder = new QDBaseInfoHolder(viewDept);
            holder.tvItemName.setText(strDept);
            List<QDRelation> relationList = QDRelationHelper.getRelationsByChildIdAndType(userId, QDRelation.TYPE_DU);
            if (relationList != null && relationList.size() != 0) {
                QDRelation relation = relationList.get(0);
                deptName = relation.getParentName();
                holder.tvItemInfo.setText(deptName);
            }
            holder.ivItemIcon.setVisibility(View.GONE);
            holder.ivItemArrow.setVisibility(View.GONE);
            holder.ivItemLine.setVisibility(View.VISIBLE);
        }
    }

    private void initJob() {
        if (userId.toLowerCase().startsWith("im_")) {
            viewJob.setVisibility(View.GONE);
        } else {
            // viewJob.setVisibility(View.VISIBLE);
            QDBaseInfoHolder holder = new QDBaseInfoHolder(viewJob);
            holder.tvItemName.setText(strJob);
            holder.tvItemInfo.setText(user.getJob());
            holder.ivItemIcon.setVisibility(View.GONE);
            holder.ivItemArrow.setVisibility(View.GONE);
            holder.ivItemLine.setVisibility(View.VISIBLE);
        }
    }

    private void initMobile() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewMobile);
        holder.tvItemName.setText(strMobile);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.GONE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
        if ((QDUtil.isHaveSelfAce(profileView, QDRolAce.ACE_PV_BAN_MOBILE) || (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_SECRET) && user.getLevel() < QDLanderInfo.getInstance().getLevel())) && !userId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
            holder.tvItemInfo.setText("******");
        } else {
            holder.tvItemInfo.setText(user.getMobile());
            holder.tvItemInfo.setAutoLinkMask(Linkify.ALL);
        }
    }

    private void initOphone() {
        if (userId.toLowerCase().startsWith("im_")) {
            viewOphone.setVisibility(View.GONE);
        } else {
            viewOphone.setVisibility(View.VISIBLE);
            QDBaseInfoHolder holder = new QDBaseInfoHolder(viewOphone);
            holder.tvItemName.setText(strOphone);
            holder.ivItemIcon.setVisibility(View.GONE);
            holder.ivItemArrow.setVisibility(View.GONE);
            holder.ivItemLine.setVisibility(View.VISIBLE);
            if ((QDUtil.isHaveSelfAce(profileView, QDRolAce.ACE_PV_BAN_OPHONE) || (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_SECRET) && user.getLevel() < QDLanderInfo.getInstance().getLevel())) && !userId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                holder.tvItemInfo.setText("******");
            } else {
                holder.tvItemInfo.setText(user.getOphone());
                holder.tvItemInfo.setAutoLinkMask(Linkify.ALL);
            }
        }
    }

    private void initEmail() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewEmail);
        holder.tvItemName.setText(strEmail);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.GONE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
        if ((QDUtil.isHaveSelfAce(profileView, QDRolAce.ACE_PV_BAN_EMAIL) || (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_SECRET) && user.getLevel() < QDLanderInfo.getInstance().getLevel())) && !userId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
            holder.tvItemInfo.setText("******");
        } else {
            holder.tvItemInfo.setText(user.getEmail());
            holder.tvItemInfo.setAutoLinkMask(Linkify.ALL);
        }
    }

    private void initSex() {
        if (userId.toLowerCase().startsWith("im_")) {
            viewSex.setVisibility(View.GONE);
        } else {
            viewSex.setVisibility(View.VISIBLE);
            QDBaseInfoHolder holder = new QDBaseInfoHolder(viewSex);
            holder.tvItemName.setText(strSex);
            holder.tvItemInfo.setText(QDUtil.getUserSex(context, user.getSex()));
            holder.ivItemIcon.setVisibility(View.GONE);
            holder.ivItemArrow.setVisibility(View.GONE);
            holder.ivItemLine.setVisibility(View.VISIBLE);
        }
    }

    private void initBirthday() {
        if (userId.toLowerCase().startsWith("im_")) {
            viewBirthday.setVisibility(View.GONE);
        } else {
            viewBirthday.setVisibility(View.VISIBLE);
            QDBaseInfoHolder holder = new QDBaseInfoHolder(viewBirthday);
            holder.tvItemName.setText(strBirthday);
            holder.tvItemInfo.setText(user.getBirthday());
            holder.ivItemIcon.setVisibility(View.GONE);
            holder.ivItemArrow.setVisibility(View.GONE);
            holder.ivItemLine.setVisibility(View.GONE);
        }
    }

    @OnClick({R2.id.btn_ud_send_msg, R2.id.btn_ud_friend, R2.id.btn_ud_add_contact})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_ud_send_msg) {
            if (user.getId().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                QDUtil.showToast(context, strNotAllow);
                return;
            }
            if (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_SECRET)) {
                if (!isFriend && user.getLevel() < QDLanderInfo.getInstance().getLevel()) {
                    QDUtil.showToast(context, strNoPermission);
                    return;
                }
            }

            Intent intent = new Intent(context, QDPersonChatActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, user.getId());
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_USER, true);
            startActivity(intent);

        } else if (i == R.id.btn_ud_friend) {
            if (isFriend) {
                getWarningDailog().show();
                QDClient.getInstance().deleteFriend(user.getId(), new QDResultCallBack<Boolean>() {
                    @Override
                    public void onError(String errorMsg) {
                        getWarningDailog().dismiss();
                        QDUtil.showToast(context, strDelFriendError + errorMsg);
                    }

                    @Override
                    public void onSuccess(Boolean b) {
                        getWarningDailog().dismiss();
                    }
                });
            } else {
                Intent friendIntent = new Intent(context, QDAddFriendActivity.class);
                friendIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER, user);
                friendIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_DEPT_NAME, deptName);
                startActivity(friendIntent);
            }

        } else if (i == R.id.btn_ud_add_contact) {
            if (isFriend) {
                getWarningDailog().show();
                QDClient.getInstance().deleteFriend(user.getId(), new QDResultCallBack<Boolean>() {
                    @Override
                    public void onError(String errorMsg) {
                        getWarningDailog().dismiss();
                        QDUtil.showToast(context, strDelFriendError + errorMsg);
                    }

                    @Override
                    public void onSuccess(Boolean b) {
                        getWarningDailog().dismiss();
                    }
                });
            }

            if (isLinkman) {
                getWarningDailog().show();
                QDClient.getInstance().deleteLinkman(userId, new QDResultCallBack() {
                    @Override
                    public void onError(String errorMsg) {
                        getWarningDailog().dismiss();
                        QDUtil.showToast(context, strDelLinkmanError);
                    }

                    @Override
                    public void onSuccess(Object o) {
                        getWarningDailog().dismiss();
                        QDLinkmanHelper.deleteLinkmanById(userId);
                        isLinkman = false;
                        initBtnContact();
                        QDRefreshCallBackManager.getInstance().refresh();
                    }
                });
            }

            if (!isLinkman && !isFriend) {
                String selfId = QDLanderInfo.getInstance().getId();
                if (selfId.toLowerCase().startsWith("im_") || userId.toLowerCase().startsWith("im_")) {
                    Intent friendIntent = new Intent(context, QDAddFriendActivity.class);
                    friendIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER, user);
                    friendIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_DEPT_NAME, deptName);
                    startActivity(friendIntent);
                } else {
                    getWarningDailog().show();
                    QDClient.getInstance().addLinkman(userId, user.getName(), new QDResultCallBack() {
                        @Override
                        public void onError(String errorMsg) {
                            getWarningDailog().dismiss();
                            QDUtil.showToast(context, strAddLinkmanError);
                        }

                        @Override
                        public void onSuccess(Object o) {
                            getWarningDailog().dismiss();
                            QDLinkman linkman = new QDLinkman();
                            linkman.setId(userId);
                            linkman.setIcon(user.getPic());
                            linkman.setName(user.getName());
                            linkman.setMobile(user.getMobile());
                            linkman.setNameSP(user.getNameSP());
                            linkman.setNameAP(user.getNameAP());
                            linkman.setNote(user.getNote());
                            QDLinkmanHelper.insertLinkman(linkman);
                            isLinkman = true;
                            initBtnContact();
                            QDRefreshCallBackManager.getInstance().refresh();
                        }
                    });
                }
            }
//                if (isLinkman) {
//                    QDClient.getInstance().deleteLinkman(userId, new QDResultCallBack() {
//                        @Override
//                        public void onError(String errorMsg) {
//                            getWarningDailog().dismiss();
//                            QDUtil.showToast(context, "删除联系人失败");
//                        }
//
//                        @Override
//                        public void onSuccess(Object o) {
//                            getWarningDailog().dismiss();
//                            QDLinkmanHelper.deleteLinkmanById(userId);
//                            isLinkman = false;
//                            initBtnContact();
//                            QDRefreshCallBackManager.getInstance().refresh();
//                        }
//                    });
//                } else {
//                    QDClient.getInstance().addLinkman(userId, new QDResultCallBack() {
//                        @Override
//                        public void onError(String errorMsg) {
//                            getWarningDailog().dismiss();
//                            QDUtil.showToast(context, "添加联系人失败");
//                        }
//
//                        @Override
//                        public void onSuccess(Object o) {
//                            getWarningDailog().dismiss();
//                            QDLinkman linkman = new QDLinkman();
//                            linkman.setId(userId);
//                            linkman.setIcon(user.getPic());
//                            linkman.setName(user.getName());
//                            linkman.setMobile(user.getMobile());
//                            linkman.setNameSP(user.getNameSP());
//                            linkman.setNameAP(user.getNameAP());
//                            linkman.setNote(user.getNote());
//                            QDLinkmanHelper.insertLinkman(linkman);
//                            isLinkman = true;
//                            initBtnContact();
//                            QDRefreshCallBackManager.getInstance().refresh();
//                        }
//                    });
//                }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDFriendCallBackManager.getInstance().removeLast();
        QDUserStatusCallBackManager.getInstatnce().removeLast();
        QDUserInfoCallBackManager.getInstance().removeLast();
    }

    @Override
    public void friendRelationChange(QDFriend friend, boolean isFriend) {
        this.isFriend = isFriend;
        initBtnContact();
    }

    @Override
    public void friendInviteStatusChange(String userId, int reply) {

    }

    @Override
    public void onFetchUserInfo(String userId) {
        if (userId.equalsIgnoreCase(this.userId)) {
            user = QDUserHelper.getUserById(userId);
            initUI();
        }
    }

    @Override
    public void onUserStatusChange(String userId, int status) {

    }

    @Override
    public void onUserInfoChange(QDUser user) {
        if (userId.equalsIgnoreCase(user.getId())) {
            this.user = user;
            initUserInfo();
            initMobile();
            initEmail();
        }
    }

    @Override
    public void onUserSecretChange(String userId, int status) {

    }
}
