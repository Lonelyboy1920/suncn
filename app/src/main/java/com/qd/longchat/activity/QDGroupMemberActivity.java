package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;
import android.widget.ListView;

import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDMemberAdapter;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDGroupMember;
import com.longchat.base.databases.QDGroupMemberHelper;
import com.longchat.base.util.QDStringUtil;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;
import com.suncn.ihold_zxztc.activity.message.Contact_DetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/5 下午1:46
 */
public class QDGroupMemberActivity extends QDBaseActivity {

    public final static int MODE_SET_MANAGER = 1;
    public final static int MODE_CHANGE_OWNER = 2;
    public final static int MODE_DEL_MEMBER = 3;
    public final static int MODE_FOR_AT = 4;

    @BindView(R2.id.view_gm_title)
    View viewTitle;
    @BindView(R2.id.view_gm_search)
    View viewSearch;
    @BindView(R2.id.lv_gm_list)
    ListView lvList;

    @BindString(R2.string.group_member_title)
    String strTitle;
    @BindString(R2.string.group_member_owner)
    String strOwner;
    @BindString(R2.string.group_member_manager)
    String strManager;
    @BindString(R2.string.group_member_owner_cannot_manager)
    String strOwnerCannotManager;
    @BindString(R2.string.group_member_warn_set_manager)
    String strWarn;
    @BindString(R2.string.title_sure)
    String strSure;
    @BindString(R2.string.operation_error)
    String strOperationError;
    @BindString(R2.string.group_member_cannot_select_self)
    String strCannotSelectSelf;
    @BindString(R2.string.group_member_selected_list_zero)
    String strMemberEmpty;
    @BindView(R2.id.view_place)
    View view_place;
    private List<QDGroupMember> memberList;
    private int mode;
    private boolean isOwner;
    private QDMemberAdapter adapter;

    private List<String> selectIdList;
    private String groupId;

    private QDResultCallBack callBack = new QDResultCallBack<String>() {
        @Override
        public void onError(String errorMsg) {
            getWarningDailog().dismiss();
            QDUtil.showToast(context, strOperationError + errorMsg);
        }

        @Override
        public void onSuccess(String info) {
            getWarningDailog().dismiss();
            String[] infos = info.split(",");
            for (QDGroupMember member : memberList) {
                String userId = infos[0];
                if (member.getUid().equalsIgnoreCase(userId)) {
                    member.setRole(QDStringUtil.strToInt(infos[1]));
                    break;
                }
            }
            QDSortUtil.sortGroupMember(memberList);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true, false);
        setContentView(R.layout.activity_group_member);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        initSearchView(viewSearch);
        tvTitleName.setText(strTitle);
        groupId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID);
        memberList = QDGroupMemberHelper.getMembersByGroupId(groupId);
        mode = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_MEMBER_MODE, 0);
        isOwner = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_OWNER, false);

        if (mode == MODE_DEL_MEMBER) {
            selectIdList = new ArrayList<>();
            tvTitleRight.setVisibility(View.VISIBLE);
            tvTitleRight.setText(strSure);
        }

        adapter = new QDMemberAdapter(context, QDSortUtil.sortGroupMember(memberList), mode, isOwner);
        lvList.setAdapter(adapter);
    }

    @OnClick({R2.id.tv_title_right, R2.id.view_gm_search})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_title_right) {
            if (selectIdList.size() == 0) {
                QDUtil.showToast(context, strMemberEmpty);
            } else {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) selectIdList);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            Intent intent = new Intent(context, QDSearchActivity.class);
            if (mode == MODE_DEL_MEMBER) {
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TYPE, QDSearchActivity.SEARCH_TYPE_GROUP_MEMBER_DEL);
            } else {
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TYPE, QDSearchActivity.SEARCH_TYPE_GROUP_MEMBER);
            }
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID, groupId);
            startActivityForResult(intent,0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==0){
                String id=data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID);
                if (selectIdList.contains(id)){
                    return;
                }
                selectIdList.add(id);
                adapter.setSelectedIdList(selectIdList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @OnItemClick(R2.id.lv_gm_list)
    public void onItemClick(int position) {
        QDGroupMember member = memberList.get(position);
        String id = member.getUid();
        if (mode == MODE_SET_MANAGER) {
            int role = member.getRole();
            if (role == QDGroupMember.ROLE_MANAGER) {
                getWarningDailog().show();
                QDClient.getInstance().setManager(member.getGroupId(), id, QDGroupMember.ROLE_NORMAL, callBack);
            } else if (role == QDGroupMember.ROLE_OWNER) {
                QDUtil.showToast(context, strOwnerCannotManager);
            } else {
                getWarningDailog().show();
                QDClient.getInstance().setManager(member.getGroupId(), id, QDGroupMember.ROLE_MANAGER, callBack);
            }
        } else if (mode == MODE_CHANGE_OWNER) {
            if (isOwner) {
                getWarningDailog().show();
                int role = member.getRole();
                if (role == QDGroupMember.ROLE_OWNER) {
                    return;
                }
                QDClient.getInstance().changeOwner(member.getGroupId(), id, new QDResultCallBack<String>() {
                    @Override
                    public void onError(String errorMsg) {
                        getWarningDailog().dismiss();
                        QDUtil.showToast(context, strOperationError + errorMsg);
                    }

                    @Override
                    public void onSuccess(String userId) {
                        getWarningDailog().dismiss();
                        isOwner = false;
                        for (QDGroupMember member1 : memberList) {
                            if (member1.getUid().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                                member1.setRole(QDGroupMember.ROLE_NORMAL);
                            }
                            if (member1.getUid().equalsIgnoreCase(userId)) {
                                member1.setRole(QDGroupMember.ROLE_OWNER);
                            }
                        }
                        memberList = QDSortUtil.sortGroupMember(memberList);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        } else if (mode == MODE_DEL_MEMBER) {
            if (selectIdList.contains(member.getUid())) {
                selectIdList.remove(member.getUid());
            } else {
                selectIdList.add(member.getUid());
            }
            adapter.setSelectedIdList(selectIdList);
        } else if (mode == MODE_FOR_AT) {
            if (id.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                QDUtil.showToast(context, strCannotSelectSelf);
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_MEMBER, member);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            QDUser user = QDUserHelper.getUserById(id);
            String userId = user.getId();
            Intent intent = new Intent(context, Contact_DetailActivity.class);
            intent.putExtra("strQdUserId", userId);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
