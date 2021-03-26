package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.giframe.utils.GILogUtil;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDGroupCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDConversation;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDGroupMember;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDConversationHelper;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.databases.QDGroupMemberHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.QDGroupManager;
import com.longchat.base.manager.listener.QDGroupCallBackManager;
import com.qd.longchat.R;

import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDGroupMemberAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.holder.QDBaseInfoHolder;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;
import com.suncn.ihold_zxztc.activity.message.Contact_DetailActivity;
import com.suncn.ihold_zxztc.activity.message.Contact_MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/1 下午6:40
 */

public class QDGroupInfoActivity extends QDBaseActivity implements QDGroupCallBack, QDAlertView.OnStringItemClickListener {

    private final static int REQUEST_NAME = 100;
    private final static int REQUEST_DESC = 101;
    private final static int REQUEST_NICKNAME = 102;
    private final static int REQUEST_ADD_MEMBER = 103;
    private final static int REQUEST_DEL_MEMBER = 104;
    private final static int REQUEST_EDIT_NOTICE = 105;

    private final static int DEFAULT_COUNT = 20;

    @BindView(R2.id.td_title)
    View viewTitle;
    @BindView(R2.id.view_gi_info)
    View viewInfo;
    @BindView(R2.id.view_gi_name)
    View viewName;
    @BindView(R2.id.view_gi_desc)
    View viewDesc;
    @BindView(R2.id.view_gi_nickname)
    View viewNickName;
    @BindView(R2.id.view_gi_notice)
    View viewNotice;
    @BindView(R2.id.view_gi_history)
    View viewHistory;
    @BindView(R2.id.view_gi_file)
    View viewFile;
    @BindView(R2.id.view_gi_top)
    View viewTop;
    @BindView(R2.id.view_gi_shield)
    View viewShield;
    @BindView(R2.id.view_gi_banned)
    View viewBanned;
    @BindView(R2.id.view_gi_clear)
    View viewClear;
    @BindView(R2.id.tv_gi_btn)
    TextView tvGroupBtn;
    @BindView(R2.id.view_gi_set_manager)
    View viewSetManager;
    @BindView(R2.id.view_gi_change_owner)
    View viewChangeOwner;
    @BindView(R2.id.iv_gi_space)
    ImageView ivSpace;
    @BindView(R2.id.gv_gi_grid)
    GridView gvList;
    @BindView(R2.id.view_place)
    View viewPlace;

    @BindString(R2.string.group_info_title)
    String strTitle;
    @BindString(R2.string.group_info_all_members)
    String strAllMember;
    @BindString(R2.string.group_info_name)
    String strName;
    @BindString(R2.string.group_info_desc)
    String strDesc;
    @BindString(R2.string.group_info_nickname)
    String strNickName;
    @BindString(R2.string.group_info_notice)
    String strNotice;
    @BindString(R2.string.person_info_history)
    String strHistory;
    @BindString(R2.string.group_info_file)
    String strFile;
    @BindString(R2.string.group_info_top)
    String strTop;
    @BindString(R2.string.group_info_shield)
    String strShield;
    @BindString(R2.string.group_info_clear)
    String strClear;
    @BindString(R2.string.group_info_exit_group)
    String strExitGroup;
    @BindString(R2.string.group_info_del_group)
    String strDelGroup;
    @BindString(R2.string.group_info_set_manager)
    String strSetManager;
    @BindString(R2.string.group_info_change_owner)
    String strChangeOwner;
    @BindString(R2.string.group_info_sure)
    String strSure;
    @BindString(R2.string.group_info_del_group_title)
    String strDelGroupTitle;
    @BindString(R2.string.group_info_exit_group_title)
    String strExitGroupTitle;
    @BindString(R2.string.group_info_clear_title)
    String strClearTitle;
    @BindString(R2.string.group_info_clear_sure)
    String strClearSure;
    @BindString(R2.string.ace_client_ban_group_file)
    String strBanGroupFile;
    @BindString(R2.string.group_inf_banned)
    String strBanned;
    @BindString(R2.string.group_info_banned_hint)
    String strBannedHint;
    @BindString(R2.string.group_notice_issue_success)
    String strIssueSuccess;
    @BindString(R2.string.group_notice_issue_failed)
    String strIssueFailed;
    @BindString(R2.string.group_notice_member_empty)
    String strEmptyMember;
    @BindString(R2.string.operation_error)
    String strOperationError;

    private QDGroup group;
    private boolean isOwner;
    private boolean isManager;
    private List<QDGroupMember> memberList;
    private QDGroupMember selfMember;
    private QDGroupMemberAdapter adapter;
    private String nickName;
    private List<String> excludedIdList;

    private List<QDUser> selectedUserList=new ArrayList<>();

    private List<String> delIdList;

    private boolean isClear;

    private int notifyType;

    private QDAlertView alertView;

    private QDConversation conversation;

    private QDResultCallBack callBack = new QDResultCallBack() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, strOperationError + errorMsg);
        }

        @Override
        public void onSuccess(Object o) {
            finish();
        }
    };

    private QDResultCallBack callBack1 = new QDResultCallBack() {
        @Override
        public void onError(String errorMsg) {
            getWarningDailog().dismiss();
            QDUtil.showToast(context, strOperationError + errorMsg);
        }

        @Override
        public void onSuccess(Object o) {
            getWarningDailog().dismiss();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true, false);
        setContentView(R.layout.activity_group_info_qd);
        ButterKnife.bind(this);
        viewPlace.setVisibility(View.VISIBLE);
        initTitleView(viewTitle);
        selectedUserList = new ArrayList<>();
        tvTitleName.setText(strTitle);
        group = QDGroupHelper.getGroupById(getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID));
        memberList = QDGroupMemberHelper.getMembersByGroupId(group.getId());
        selfMember = QDGroupMemberHelper.getMemberInfoByGroupIdAndUserId(group.getId(), QDLanderInfo.getInstance().getId());
        nickName = selfMember.getNickName();
        int role = selfMember.getRole();
        if (role == QDGroupMember.ROLE_MANAGER) {
            isOwner = false;
            isManager = true;
        } else if (role == QDGroupMember.ROLE_OWNER) {
            isOwner = true;
            isManager = false;
        } else {
            isOwner = false;
            isManager = false;
        }
        initUI();
        initAlertView();
        QDGroupCallBackManager.getInstance().addCallBack(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUI() {
        initInfo();
        initName();
        initDesc();
        initNickName();
        initNotice();
        initHistory();
        initFile();
        initTop();
        initShield();
        initBanned();
        initClear();
        if (isOwner) {
            initChangeOwner();
            initSetManager();
//            viewSetManager.setVisibility(View.VISIBLE);
//            viewChangeOwner.setVisibility(View.VISIBLE);
//            ivSpace.setVisibility(View.VISIBLE);
        } else {
            viewSetManager.setVisibility(View.GONE);
            viewChangeOwner.setVisibility(View.GONE);
            ivSpace.setVisibility(View.GONE);
        }
        initGridView();

        if (group.getType() == QDGroup.TYPE_ADMGROUP || group.getType() == QDGroup.TYPE_DEPT) {
            tvGroupBtn.setVisibility(View.GONE);
        } else {
            tvGroupBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initAlertView() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Bottom)
                .setSelectList(strSure)
                .setOnStringItemClickListener(this)
                .build();
    }

    private void initInfo() {
        if (isOwner || isManager) {
            excludedIdList = new ArrayList<>();
        }
        if (isOwner) {
            tvGroupBtn.setText(strDelGroup);
        } else {
            tvGroupBtn.setText(strExitGroup);
        }

        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewInfo);
        holder.tvItemName.setText(strAllMember);
        holder.tvItemInfo.setText(memberList.size() + "");
        holder.ivItemArrow.setVisibility(View.VISIBLE);
    }

    private void initName() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewName);
        holder.tvItemName.setText(strName);
        holder.tvItemInfo.setText(group.getName());
        if (isOwner || isManager) {
            holder.ivItemArrow.setVisibility(View.VISIBLE);
        } else {
            holder.ivItemArrow.setVisibility(View.GONE);
        }
        holder.ivItemLine.setVisibility(View.VISIBLE);
    }

    private void initDesc() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewDesc);
        holder.tvItemName.setText(strDesc);
        holder.tvItemInfo.setText(group.getDesc());
        if (isOwner || isManager) {
            holder.ivItemArrow.setVisibility(View.VISIBLE);
        } else {
            holder.ivItemArrow.setVisibility(View.GONE);
        }
        holder.ivItemLine.setVisibility(View.VISIBLE);
    }

    private void initNickName() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewNickName);
        holder.tvItemName.setText(strNickName);
        holder.tvItemInfo.setText(nickName);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
    }

    private void initNotice() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewNotice);
        holder.tvItemName.setText(strNotice);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
    }

    private void initHistory() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewHistory);
        holder.tvItemName.setText(strHistory);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
    }

    private void initFile() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewFile);
        holder.tvItemName.setText(strFile);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
    }

    private void initTop() {
        final QDBaseInfoHolder holder = new QDBaseInfoHolder(viewTop);
        holder.tvItemName.setText(strTop);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        conversation = QDConversationHelper.getConversationById(group.getId());
        if (conversation == null) {
            holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
        } else {
            int isTop = conversation.getIsTop();
            if (isTop == QDConversation.TOP) {
                holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_open);
            } else {
                holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
            }
        }
        holder.ivItemLine.setVisibility(View.VISIBLE);

        holder.ivItemArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conversation == null || conversation.getIsTop() == QDConversation.UNTOP) {
                    holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_open);
                    if (conversation == null) {
                        conversation = new QDConversation();
                        conversation.setId(group.getId());
                        conversation.setName(group.getName());
                        conversation.setType(QDConversation.TYPE_GROUP);
                        conversation.setIsTop(QDConversation.TOP);
                        conversation.setTime(System.currentTimeMillis());
                    } else {
                        conversation.setIsTop(QDConversation.TOP);
                    }
                } else {
                    conversation.setIsTop(QDConversation.UNTOP);
                    holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
                }
                QDConversationHelper.insertConversation(conversation);
            }
        });
    }

    private void initShield() {
        final QDBaseInfoHolder holder = new QDBaseInfoHolder(viewShield);
        holder.tvItemName.setText(strShield);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        notifyType = group.getNotifyType();
        if (isManager || isOwner) {
            holder.ivItemLine.setVisibility(View.VISIBLE);
        } else {
            holder.ivItemLine.setVisibility(View.GONE);
        }
        if (notifyType == QDGroup.NOTIFY_ON) {
            holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
        } else {
            holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_open);
        }
        holder.ivItemArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifyType == QDGroup.NOTIFY_ON) {
                    holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_open);
                    group.setNotifyType(QDGroup.NOTIFY_OFF);
                    notifyType = QDGroup.NOTIFY_OFF;
                } else {
                    holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
                    group.setNotifyType(QDGroup.NOTIFY_ON);
                    notifyType = QDGroup.NOTIFY_ON;
                }
                QDClient.getInstance().setNotifyType(group.getId(), notifyType, new QDResultCallBack() {
                    @Override
                    public void onError(String errorMsg) {

                    }

                    @Override
                    public void onSuccess(Object o) {
                        GILogUtil.e("设置免打扰成功啦-----");
                    }
                });
                QDGroupHelper.updateGroup(group);
            }
        });
    }

    private void initBanned() {
        if (isManager || isOwner) {
            viewBanned.setVisibility(View.VISIBLE);
        } else {
            viewBanned.setVisibility(View.GONE);
        }
        final QDBaseInfoHolder holder = new QDBaseInfoHolder(viewBanned);
        holder.tvItemName.setText(strBanned);
        holder.ivItemArrow.setVisibility(View.VISIBLE);

        if (group.getFlag() == QDGroup.FLAG_BANNED) {
            holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_open);
        } else {
            holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
        }

        holder.ivItemArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag;
                if (group.getFlag() == QDGroup.FLAG_BANNED) {
                    flag = 0;
                    holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
                } else {
                    flag = 1;
                    holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_open);
                }
                QDGroupManager.getInstance().setGroupBanned(group.getId(), flag, new QDResultCallBack() {
                    @Override
                    public void onError(String errorMsg) {

                    }

                    @Override
                    public void onSuccess(Object o) {

                    }
                });
                group.setFlag(flag);
                QDGroupHelper.updateGroup(group);
            }
        });
    }

    private void initClear() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewClear);
        holder.tvItemName.setText(strClear);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
    }

    private void initSetManager() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewSetManager);
        holder.tvItemName.setText(strSetManager);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
    }

    private void initChangeOwner() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewChangeOwner);
        holder.tvItemName.setText(strChangeOwner);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
    }

    private void initGridView() {
        if (adapter == null) {
            adapter = new QDGroupMemberAdapter(context);
            gvList.setAdapter(adapter);
        }
        memberList = QDSortUtil.sortGroupMember(memberList);
        adapter.setVisible(isManager || isOwner);

        int count;
        if (isOwner || isManager) {
            if (group.getType() == QDGroup.TYPE_DEPT) {
                count = DEFAULT_COUNT;
            } else {
                count = DEFAULT_COUNT - 2;
            }
        } else {
            count = DEFAULT_COUNT;
        }

        if (memberList.size() > count) {
            adapter.setMemberList(memberList.subList(0, count));
        } else {
            adapter.setMemberList(memberList);
        }
    }

    @OnItemClick(R2.id.gv_gi_grid)
    public void OnItemClick(int position) {
        if (isManager || isOwner) {
            StringBuilder strFilterUserIds = new StringBuilder();
            if (position == adapter.getCount() - 2) {
                excludedIdList.clear();
                for (QDGroupMember groupMember : memberList) {
                    excludedIdList.add(groupMember.getUid());
                    strFilterUserIds.append(groupMember.getUid()).append(",");
                }
                Intent intent = new Intent(context, Contact_MainActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_MULTI);
                intent.putExtra("strFilterUserIds", strFilterUserIds.toString());
                intent.putExtra("chooseType", 5);
                intent.putExtra("isSingle", false);
                intent.putExtra("isChoose",true);
                startActivityForResult(intent, REQUEST_ADD_MEMBER);
            } else if (position == adapter.getCount() - 1) {
                Intent memberIntent = new Intent(context, QDGroupMemberActivity.class);
                memberIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID, group.getId());
                memberIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_OWNER, isOwner);
                memberIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_MEMBER_MODE, QDGroupMemberActivity.MODE_DEL_MEMBER);
                startActivityForResult(memberIntent, REQUEST_DEL_MEMBER);
            } else {
                String userId = adapter.getItem(position).getUid();
                Intent intent = new Intent(context, Contact_DetailActivity.class);
                intent.putExtra("strQdUserId", userId);
                startActivity(intent);
            }
        } else {
            String userId = adapter.getItem(position).getUid();
            Intent intent = new Intent(context, Contact_DetailActivity.class);
            intent.putExtra("strQdUserId", userId);
            startActivity(intent);
        }
    }

    @OnClick({R2.id.tv_gi_btn, R2.id.view_gi_info, R2.id.view_gi_clear, R2.id.view_gi_name,
            R2.id.view_gi_desc, R2.id.view_gi_nickname, R2.id.view_gi_set_manager, R2.id.view_gi_change_owner,
            R2.id.tv_title_back, R2.id.view_gi_notice, R2.id.view_gi_file, R2.id.view_gi_history})
    public void OnClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_gi_btn) {
            String title;
            if (isOwner) {
                title = strDelGroupTitle;
            } else {
                title = strExitGroupTitle;
            }
            alertView.setTitle(title);
            alertView.setSelectList(strSure);
            alertView.show();

        } else if (i == R.id.view_gi_info) {
            Intent memberIntent = new Intent(context, QDGroupMemberActivity.class);
            memberIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID, group.getId());
            startActivity(memberIntent);

        } else if (i == R.id.view_gi_clear) {
            alertView.setTitle(strClearTitle);
            alertView.setSelectList(strClearSure);
            alertView.show();

        } else if (i == R.id.view_gi_name) {
            if (isOwner || isManager) {
                Intent nameIntent = new Intent(context, QDModifyInfoActivity.class);
                nameIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, strName);
                nameIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, group.getName());
                startActivityForResult(nameIntent, REQUEST_NAME);
            }

        } else if (i == R.id.view_gi_desc) {
            if (isOwner || isManager) {
                Intent descIntent = new Intent(context, QDModifyInfoActivity.class);
                descIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, strDesc);
                descIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, group.getDesc());
                startActivityForResult(descIntent, REQUEST_DESC);
            }

        } else if (i == R.id.view_gi_nickname) {
            Intent nickNameIntent = new Intent(context, QDModifyInfoActivity.class);
            nickNameIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, strNickName);
            nickNameIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, nickName);
            startActivityForResult(nickNameIntent, REQUEST_NICKNAME);

        } else if (i == R.id.view_gi_set_manager) {
            Intent setManagerIntent = new Intent(context, QDGroupMemberActivity.class);
            setManagerIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID, group.getId());
            setManagerIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_MEMBER_MODE, QDGroupMemberActivity.MODE_SET_MANAGER);
            startActivity(setManagerIntent);

        } else if (i == R.id.view_gi_change_owner) {
            Intent ownerIntent = new Intent(context, QDGroupMemberActivity.class);
            ownerIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID, group.getId());
            ownerIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_MEMBER_MODE, QDGroupMemberActivity.MODE_CHANGE_OWNER);
            ownerIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_OWNER, isOwner);
            startActivity(ownerIntent);

        } else if (i == R.id.tv_title_back) {
            onBackPressed();

        } else if (i == R.id.view_gi_notice) {
            Intent noticeIntent = new Intent(context, QDGroupNoticeListActivity.class);
//                noticeIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_NOTICE, group.getNotice());
            noticeIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID, group.getId());
            if (isOwner || isManager) {
                noticeIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_EDIT, true);
            } else {
                noticeIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_EDIT, false);
            }
            startActivity(noticeIntent);

        } else if (i == R.id.view_gi_file) {
            if (!QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_BAN_GROUP_FILE)) {
                Intent fileIntent = new Intent(context, QDGroupFileActivity.class);
                fileIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID, group.getId());
                startActivity(fileIntent);
            } else {
                QDUtil.showToast(context, strBanGroupFile);
            }

        } else if (i == R.id.view_gi_history) {
            Intent intent = new Intent(context, QDSearchActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TYPE, QDSearchActivity.SEARCH_TYPE_GROUP_HISTORY);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, group.getId());
            startActivity(intent);

        }
    }

    private Map<String, String> map = new HashMap<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        map.clear();
        if (resultCode == RESULT_OK) {
            String info = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO);
            switch (requestCode) {
                case REQUEST_NAME:
                    map.put(QDGroup.TAG_NAME, info);
                    QDClient.getInstance().updateGroupInfo(group.getId(), map, callBack1);
                    QDUtil.showToast(context, "保存成功");
                    break;
                case REQUEST_DESC:
                    map.put(QDGroup.TAG_DESC, info);
                    QDClient.getInstance().updateGroupInfo(group.getId(), map, callBack1);
                    QDUtil.showToast(context, "保存成功");
                    break;
                case REQUEST_NICKNAME:
                    nickName = info;
                    QDClient.getInstance().updateNickName(group.getId(), info, callBack1);
                    QDUtil.showToast(context, "保存成功");
                    break;
                case REQUEST_ADD_MEMBER:
                    getWarningDailog().show();
                    selectedUserList =  data.getExtras().getParcelableArrayList(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
                    if (selectedUserList.size() == 0) {
                        getWarningDailog().dismiss();
                        QDUtil.showToast(context, strEmptyMember);
                        return;
                    }
                    List<QDGroupMember> groupMembers = new ArrayList<>();
                    for (QDUser user : selectedUserList) {
                        String userId = user.getId();
                        if (excludedIdList.contains(userId)) {
                            continue;
                        }
                        QDGroupMember groupMember = new QDGroupMember();
                        groupMember.setGroupId(group.getId());
                        groupMember.setGroupName(group.getName());
                        groupMember.setRole(QDGroupMember.ROLE_NORMAL);
                        groupMember.setNickName(user.getName());
                        groupMember.setIcon(user.getPic());
                        groupMember.setUid(user.getId());
                        groupMember.setName(user.getName());
                        groupMembers.add(groupMember);
                    }
                    QDClient.getInstance().addGroupMember(group.getId(), groupMembers, callBack1);
                    break;
                case REQUEST_DEL_MEMBER:
                    getWarningDailog().show();
                    delIdList = data.getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST);
                    StringBuilder sb = new StringBuilder();
                    for (String id : delIdList) {
                        sb.append(id).append(",");
                    }
                    String ids = sb.toString().substring(0, sb.toString().length() - 1);
                    QDClient.getInstance().removeGroupMember(group.getId(), ids, callBack1);
                    break;
                case REQUEST_EDIT_NOTICE:
                    getWarningDailog().show();
                    final String notice = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_NOTICE);
                    QDClient.getInstance().issueNotice(group.getId(), notice, new QDResultCallBack() {
                        @Override
                        public void onError(String errorMsg) {
                            getWarningDailog().dismiss();
                            QDUtil.showToast(context, strIssueFailed + errorMsg);
                        }

                        @Override
                        public void onSuccess(Object o) {
                            getWarningDailog().dismiss();
                            group.setNotice(notice);
                            QDGroupHelper.updateGroup(group);
                            QDUtil.showToast(context, strIssueSuccess);
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isClear) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDGroupCallBackManager.getInstance().removeLast();
    }

    @Override
    public void groupChange(String groupId, QDGroup group, boolean isOwner) {
        if (group == null && groupId.equalsIgnoreCase(this.group.getId())) {
            finish();
        }
    }

    @Override
    public void groupMemberAdd(String groupId, List<QDGroupMember> groupMembers) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            memberList = QDGroupMemberHelper.getMembersByGroupId(group.getId());
            initGridView();
            initInfo();
        }
    }

    @Override
    public void groupMemberDelete(String groupId, List<String> memberIdList) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            List<QDGroupMember> list = new ArrayList<>();
            for (QDGroupMember member : memberList) {
                if (!memberIdList.contains(member.getUid())) {
                    list.add(member);
                }
            }
            memberList = list;
            adapter.setMemberList(memberList);
            initInfo();
        }
    }

    @Override
    public void groupInfoChange(String groupId, QDGroup group) {
        if (groupId.equalsIgnoreCase(this.group.getId())) {
            this.group = group;
            initName();
            initDesc();
        }
    }

    @Override
    public void nickNameChange(String groupId, String userId, String nickName) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            for (QDGroupMember member : memberList) {
                if (member.getUid().equalsIgnoreCase(userId)) {
                    member.setNickName(nickName);
                    break;
                }
            }
            if (userId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                this.nickName = nickName;
                initNickName();
            }
            initGridView();
        }
    }

    @Override
    public void groupManagerChange(String groupId, String userId, boolean isManager) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            if (userId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                if (isManager) {
                    this.isManager = true;
                } else {
                    this.isManager = false;
                }
            }

            for (QDGroupMember member : memberList) {
                if (member.getUid().equalsIgnoreCase(userId)) {
                    if (isManager) {
                        member.setRole(QDGroupMember.ROLE_MANAGER);
                    } else {
                        member.setRole(QDGroupMember.ROLE_NORMAL);
                    }
                    break;
                }
            }
            memberList = QDSortUtil.sortGroupMember(memberList);
            initUI();
        }
    }

    @Override
    public void groupOwnerChange(String groupId, String userId) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            isOwner = false;
            isManager = false;
            for (QDGroupMember member : memberList) {
                if (member.getRole() == QDGroupMember.ROLE_OWNER) {
                    member.setRole(QDGroupMember.ROLE_NORMAL);
                }
                if (member.getUid().equalsIgnoreCase(userId)) {
                    member.setRole(QDGroupMember.ROLE_OWNER);
                }
            }
            memberList = QDSortUtil.sortGroupMember(memberList);
            initGridView();
            initInfo();
            initDesc();
            viewSetManager.setVisibility(View.GONE);
            viewChangeOwner.setVisibility(View.GONE);
            ivSpace.setVisibility(View.GONE);
        }

    }

    @Override
    public void groupCreateNotice(String groupId, String notice) {
        if (groupId.equalsIgnoreCase(group.getIcon())) {
            group.setNotice(notice);
        }
    }

    @Override
    public void groupNotifyChange(String groupId, int notifyType) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            group.setNotifyType(notifyType);
            initShield();
        }
    }

    @Override
    public void groupBannedChange(String groupId, int flag) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            group.setFlag(flag);
            initBanned();
        }
    }

    @Override
    public void onItemClick(String str, int position) {
        if (str.equalsIgnoreCase(strSure)) {
            if (isOwner) {
                QDClient.getInstance().deleteGroup(group.getId(), callBack);
            } else {
                QDClient.getInstance().exitGroup(group.getId(), callBack);
            }
        } else {
            QDMessageHelper.deleteMessageWithGroupId(group.getId());
            QDConversationHelper.deleteConversationById(group.getId());
            isClear = true;
        }
    }
}
