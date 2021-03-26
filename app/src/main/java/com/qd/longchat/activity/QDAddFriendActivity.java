package com.qd.longchat.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.callback.QDUserInfoCallBack;
import com.longchat.base.dao.QDCompany;
import com.longchat.base.dao.QDDept;
import com.longchat.base.dao.QDRelation;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDCompanyHelper;
import com.longchat.base.databases.QDDeptHelper;
import com.longchat.base.databases.QDRelationHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.listener.QDUserInfoCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.config.QDLanderInfo;
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
 * @creatTime 2018/8/14 下午4:24
 */
public class QDAddFriendActivity extends QDBaseActivity implements QDUserInfoCallBack {

    @BindView(R2.id.view_add_title)
    View viewTitle;
    @BindView(R2.id.view_add_info)
    View viewInfo;
    @BindView(R2.id.et_add_edit)
    EditText etEdit;

    @BindString(R2.string.user_detail_add_friend)
    String strAddFriend;
    @BindString(R2.string.friend_i_am)
    String strIAm;
    @BindString(R2.string.friend_invite_error)
    String strInviteError;
    @BindString(R2.string.friend_invite_success)
    String strInviteSuccess;

    private QDUser user;
    private String deptName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);

        deptName = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_DEPT_NAME);
        user = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_USER);

        QDUser user1 = QDUserHelper.getUserById(user.getId());
        if (user1 == null) {
            QDClient.getInstance().fetchUserInfo(user.getId());
        }

        initTitleView(viewTitle);
        tvTitleName.setText(strAddFriend);
        initInfo();
        etEdit.setText(QDLanderInfo.getInstance().getName());
        etEdit.setSelection(etEdit.length());
        QDUserInfoCallBackManager.getInstance().addCallBack(this);
    }

    private void initInfo() {
        QDCardInfoHolder holder = new QDCardInfoHolder(viewInfo);
        List<QDRelation> companyList = QDRelationHelper.getRelationsByChildIdAndType(user.getId(), QDRelation.TYPE_CU);
        if (companyList == null || companyList.size() == 0) {
            List<QDRelation> deptLit = QDRelationHelper.getRelationsByChildIdAndType(user.getId(), QDRelation.TYPE_DU);
            if (deptLit.size() > 0) {
                QDRelation relation = deptLit.get(0);
                QDDept dept = QDDeptHelper.getDeptById(relation.getParentId());
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
    }

    @OnClick(R2.id.btn_add)
    public void onClick(View view) {
        String desc = strIAm + etEdit.getText().toString();
        QDClient.getInstance().inviteFriend(user.getId(), user.getName(), desc, new QDResultCallBack<Boolean>() {
            @Override
            public void onError(String errorMsg) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, strInviteError + errorMsg);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, strInviteSuccess);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDUserInfoCallBackManager.getInstance().removeCallBack(this);
    }

    @Override
    public void onFetchUserInfo(String userId) {
    }
}
