package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDLinkman;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDLinkmanHelper;
import com.longchat.base.manager.QDFriendManager;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.model.QDContact;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/3/14 下午1:10
 */
public class QDAddActivity extends QDBaseActivity {

    private final static int REQUEST_ADD = 1000;

    @BindView(R2.id.view_title)
    View viewTitle;
    @BindView(R2.id.tv_add_link)
    TextView tvAddLink;
    @BindView(R2.id.tv_add_customer)
    TextView tvAddCustomer;

    @BindString(R2.string.add_success)
    String strAddSuccess;
    @BindString(R2.string.add_failed)
    String strAddFailed;
    @BindString(R2.string.ace_self_ban_customer)
    String strBanCustomer;

    private List<String> excludedUserIdList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(R.string.str_add);

        excludedUserIdList = new ArrayList<>();
        List<QDLinkman> linkmanList = QDLinkmanHelper.getAll();
        for (QDLinkman linkman : linkmanList) {
            excludedUserIdList.add(linkman.getId());
        }
        excludedUserIdList.add(QDLanderInfo.getInstance().getId());
    }

    @OnClick({R2.id.tv_add_link, R2.id.tv_add_customer})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_add_link) {
            Intent intent1 = new Intent(context, QDContactActivity.class);
            intent1.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_MULTI);
            intent1.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_TYPE, QDContact.TYPE_CONTACT);
            intent1.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_EXCLUDED_ID_LIST, (ArrayList<String>) excludedUserIdList);
            startActivityForResult(intent1, REQUEST_ADD);

        } else if (i == R.id.tv_add_customer) {
            if (QDUtil.isHaveSelfAce(QDLanderInfo.getInstance().getMsgAce(), QDRolAce.ACE_MA_BAN_ADD_CUSTOMER)) {
                QDUtil.showToast(context, strBanCustomer);
                return;
            }
            Intent intent = new Intent(context, QDSearchActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_REMOTE, true);
            startActivity(intent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final List<QDUser> userList = data.getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
            if (userList.size() == 0) {
                QDUtil.showToast(context, context.getResources().getString(R.string.create_group_empty_member));
                return;
            }
            List<Map<String, String>> dataList = new ArrayList<>();
            for (QDUser user : userList) {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", user.getId());
                map.put("name", user.getName());
                dataList.add(map);
            }
            getWarningDailog().show();
            QDFriendManager.getInstance().addLinkmen(QDGson.getGson().toJson(dataList), new QDResultCallBack() {
                @Override
                public void onError(String errorMsg) {
                    getWarningDailog().dismiss();
                    QDUtil.showToast(context, strAddFailed);
                }

                @Override
                public void onSuccess(Object o) {
                    List<QDLinkman> dataList = new ArrayList<>();
                    for (QDUser user : userList) {
                        QDLinkman linkman = new QDLinkman();
                        linkman.setId(user.getId());
                        linkman.setIcon(user.getPic());
                        linkman.setName(user.getName());
                        linkman.setMobile(user.getMobile());
                        linkman.setNameSP(user.getNameSP());
                        linkman.setNameAP(user.getNameAP());
                        linkman.setNote(user.getNote());
                        dataList.add(linkman);
                    }
                    QDLinkmanHelper.insertLinkmanList(dataList);
                    getWarningDailog().dismiss();
                    QDUtil.showToast(context, strAddSuccess);
                }
            });

        }
    }
}
