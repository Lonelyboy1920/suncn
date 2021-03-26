package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.longchat.base.callback.QDGroupCallBack;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDGroupMember;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.manager.listener.QDGroupCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDGroupViewPagerAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDIntentKeyUtil;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/30 上午9:53
 */

public class QDGroupActivity extends QDBaseActivity implements QDGroupCallBack {

    @BindView(R2.id.view_group_title)
    View viewTitle;
    @BindView(R2.id.view_group_search)
    View viewSearch;
    @BindView(R2.id.tl_group_layout)
    TabLayout tlTabLayout;
    @BindView(R2.id.vp_group_pager)
    ViewPager viewPager;
//    @BindView(R2.id.ll_group_parent_layout)
//    LinearLayout llPatentLayout;

    @BindString(R2.string.group_mine)
    String strMine;
    @BindString(R2.string.group_joined)
    String strJoined;
    @BindString(R2.string.group_title)
    String strTitle;
    @BindString(R2.string.group_create)
    String strCreate;
    @BindView(R2.id.view_place)
    View view_place;

    private String[] tabTitles;

    private QDGroupViewPagerAdapter adapter;

    private List<QDGroup> mineGroupList;

    private List<QDGroup> joinedGroupList;

    private boolean isForward;
    private QDMessage message;

    @Override
    protected void onResume() {
        super.onResume();
        mineGroupList = QDGroupHelper.getMineGroups();
        joinedGroupList = QDGroupHelper.getJoinedGroups();
        adapter.setMineGroupList(mineGroupList);
        adapter.setJoinedGroupList(joinedGroupList);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        view_place.setVisibility(View.VISIBLE);
        tvTitleBack.setVisibility(View.VISIBLE);

        tabTitles = new String[]{strMine, strJoined};
        mineGroupList = QDGroupHelper.getMineGroups();
        joinedGroupList = QDGroupHelper.getJoinedGroups();

        isForward = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, false);
        message = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE);
        adapter = new QDGroupViewPagerAdapter(context, tabTitles, mineGroupList, joinedGroupList, isForward);
        adapter.setMessage(message);
        viewPager.setAdapter(adapter);
        tlTabLayout.setupWithViewPager(viewPager);

//        if (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_OPEN_WATERMARK)) {
//            llPatentLayout.setBackground(QDBitmapUtil.addWaterMark(context, QDLanderInfo.getInstance().getName(), Color.WHITE));
//        }
        QDGroupCallBackManager.getInstance().addCallBack(this);
    }

    @OnClick({R2.id.tv_title_right, R2.id.view_group_search})
    public void OnClick(View view) {
        int i = view.getId();
        if (i == R.id.view_group_search) {
            Intent searchIntent = new Intent(context, QDSearchActivity.class);
            searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TYPE, QDSearchActivity.SEARCH_TYPE_GROUP);
            startActivity(searchIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDGroupCallBackManager.getInstance().removeLast();
    }

    @Override
    public void groupChange(String groupId, QDGroup group, boolean isOwner) {
        mineGroupList = QDGroupHelper.getMineGroups();
        joinedGroupList = QDGroupHelper.getJoinedGroups();
        adapter.setJoinedGroupList(joinedGroupList);
        adapter.setMineGroupList(mineGroupList);
        adapter.notifyDataSetChanged();
    }

    private List<QDGroup> deleteGroup(String groupId, List<QDGroup> groupList) {
        int index = 0;
        int size = groupList.size();
        for (int i = 0; i < size; i++) {
            if (groupId.equalsIgnoreCase(groupList.get(i).getId())) {
                index = i;
                break;
            }
        }
        groupList.remove(index);
        return groupList;
    }

    @Override
    public void groupMemberAdd(String groupId, List<QDGroupMember> groupMembers) {

    }

    @Override
    public void groupMemberDelete(String groupId, List<String> memberIdList) {

    }

    @Override
    public void groupInfoChange(String groupId, QDGroup group) {
        String ownerId = group.getOwnerId();
        if (ownerId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
            for (QDGroup mineGroup : mineGroupList) {
                if (mineGroup.getId().equalsIgnoreCase(groupId)) {
                    mineGroup.setName(group.getName());
                    mineGroup.setDesc(group.getDesc());
                    mineGroup.setIcon(group.getIcon());
                    break;
                }
            }
            adapter.setMineGroupList(mineGroupList);
        } else {
            for (QDGroup joinedGroup : joinedGroupList) {
                if (joinedGroup.getId().equalsIgnoreCase(groupId)) {
                    joinedGroup.setName(group.getName());
                    joinedGroup.setDesc(group.getDesc());
                    joinedGroup.setIcon(group.getIcon());
                    break;
                }
            }
            adapter.setJoinedGroupList(joinedGroupList);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void nickNameChange(String groupId, String userId, String nickName) {

    }

    @Override
    public void groupManagerChange(String groupId, String userId, boolean isManager) {

    }

    @Override
    public void groupOwnerChange(String groupId, String userId) {
        mineGroupList = QDGroupHelper.getMineGroups();
        joinedGroupList = QDGroupHelper.getJoinedGroups();
        adapter.setJoinedGroupList(joinedGroupList);
        adapter.setMineGroupList(mineGroupList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void groupCreateNotice(String groupId, String notice) {

    }

    @Override
    public void groupNotifyChange(String groupId, int notifyType) {

    }

    @Override
    public void groupBannedChange(String groupId, int flag) {

    }
}
