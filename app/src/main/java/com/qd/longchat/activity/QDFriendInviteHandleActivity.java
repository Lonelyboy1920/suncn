package com.qd.longchat.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDFriendCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.callback.QDUserInfoCallBack;
import com.longchat.base.dao.QDCompany;
import com.longchat.base.dao.QDDept;
import com.longchat.base.dao.QDFriend;
import com.longchat.base.dao.QDFriendInvite;
import com.longchat.base.dao.QDRelation;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDCompanyHelper;
import com.longchat.base.databases.QDDeptHelper;
import com.longchat.base.databases.QDRelationHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.listener.QDFriendCallBackManager;
import com.longchat.base.manager.listener.QDUserInfoCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.holder.QDCardInfoHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/12 下午6:12
 */
public class QDFriendInviteHandleActivity extends QDBaseActivity implements QDFriendCallBack, QDUserInfoCallBack {

    @BindView(R2.id.view_fh_title)
    View viewTitle;
    @BindView(R2.id.view_fh_info)
    View viewInfo;
    @BindView(R2.id.tv_fh_desc)
    TextView tvDesc;
    @BindView(R2.id.tv_fh_info)
    TextView tvInfo;
    @BindView(R2.id.btn_fh_agree)
    Button btnAgree;
    @BindView(R2.id.btn_fh_refuse)
    Button btnRefuse;

    @BindString(R2.string.friend_invite_had_allow_1)
    String strAgree;
    @BindString(R2.string.friend_invite_had_refuse_1)
    String strRefuse;
    @BindString(R2.string.friend_invite_tag)
    String strTag;
    @BindString(R2.string.operation_error)
    String strOperationError;

    private QDFriendInvite invite;
    private QDUser user;

    private QDResultCallBack callBack = new QDResultCallBack() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, strOperationError + errorMsg);
        }

        @Override
        public void onSuccess(Object o) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite_handle);
        ButterKnife.bind(this);

        invite = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_FRIEND_INVITE);
        user = QDUserHelper.getUserById(invite.getUserId());
        if (user != null) {
            initInfo();
        } else {
            QDClient.getInstance().fetchUserInfo(invite.getUserId());
        }

        initTitleView(viewTitle);
        initUI();

        QDFriendCallBackManager.getInstance().addCallBack(this);
        QDUserInfoCallBackManager.getInstance().addCallBack(this);
    }

    private void initUI() {
        int reply = invite.getReply();
        if (reply == QDFriendInvite.REPLY_NORMAL) {
            tvInfo.setVisibility(View.GONE);
            btnAgree.setVisibility(View.VISIBLE);
            btnRefuse.setVisibility(View.VISIBLE);
        } else if (reply == QDFriendInvite.REPLY_AGREE) {
            tvInfo.setVisibility(View.VISIBLE);
            btnAgree.setVisibility(View.GONE);
            btnRefuse.setVisibility(View.GONE);
            tvInfo.setText(strAgree);
        } else {
            tvInfo.setVisibility(View.VISIBLE);
            btnAgree.setVisibility(View.GONE);
            btnRefuse.setVisibility(View.GONE);
            tvInfo.setText(strRefuse);
        }
    }

    private void initInfo() {
        QDCardInfoHolder holder = new QDCardInfoHolder(viewInfo);
        List<QDRelation> companyList = QDRelationHelper.getRelationsByChildIdAndType(user.getId(), QDRelation.TYPE_CU);
        String deptName = "";
        if (companyList == null || companyList.size() == 0) {
            List<QDRelation> deptLit = QDRelationHelper.getRelationsByChildIdAndType(user.getId(), QDRelation.TYPE_DU);
            if (deptLit.size() > 0) {
                QDRelation relation = deptLit.get(0);
                QDDept dept = QDDeptHelper.getDeptById(relation.getParentId());
                deptName = dept.getName();
                QDCompany company = QDCompanyHelper.getCompanyById(dept.getCid());
                holder.itemCompany.setText(company.getName());
            }
        } else {
            QDRelation relation = companyList.get(0);
            holder.itemCompany.setText(relation.getParentName());
        }
        holder.itemName.setText(user.getName());
        holder.itemDept.setText(deptName);
        holder.itemMobile.setText(user.getMobile());
        QDBitmapUtil.getInstance().createAvatar(context, user.getId(), user.getName(), user.getPic(), holder.itemIcon);
        tvDesc.setText(invite.getDesc());
    }

    @OnClick({R2.id.view_fh_info, R2.id.btn_fh_agree, R2.id.btn_fh_refuse})
    public void onClick(View view) {
        int i = view.getId();
//        if (i == R.id.view_fh_info) {
//            Intent intent = new Intent(context, QDUserDetailActivity.class);
//            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, invite.getUserId());
//            startActivity(intent);
//
//        } else
            if (i == R.id.btn_fh_agree) {
            QDClient.getInstance().replyInvite(user, QDFriendInvite.REPLY_AGREE, callBack);

        } else if (i == R.id.btn_fh_refuse) {
            QDClient.getInstance().replyInvite(user, QDFriendInvite.REPLY_REFUSE, callBack);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDUserInfoCallBackManager.getInstance().removeLast();
        QDFriendCallBackManager.getInstance().removeLast();
    }

    @Override
    public void friendRelationChange(QDFriend friend, boolean isFriend) {

    }

    @Override
    public void friendInviteStatusChange(String userId, int reply) {
        if (userId.equalsIgnoreCase(user.getId())) {
            invite.setReply(reply);
            initUI();
        }
    }

    @Override
    public void onFetchUserInfo(String userId) {
        if (userId.equalsIgnoreCase(invite.getUserId())) {
            user = QDUserHelper.getUserById(invite.getUserId());
            initInfo();
        }
    }
}
