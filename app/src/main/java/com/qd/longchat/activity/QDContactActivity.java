package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.callback.QDUserStatusCallBack;
import com.longchat.base.dao.QDCompany;
import com.longchat.base.dao.QDConversation;
import com.longchat.base.dao.QDDept;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDRelation;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDCompanyHelper;
import com.longchat.base.databases.QDConversationHelper;
import com.longchat.base.databases.QDDeptHelper;
import com.longchat.base.databases.QDRelationHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.listener.QDUserStatusCallBackManager;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDContactNavigationAdapter;
import com.qd.longchat.adapter.QDRootContactAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.model.QDContact;
import com.qd.longchat.util.QDContactUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDSortUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;

import java.util.ArrayList;
import java.util.Collections;
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
 * @creatTime 2018/8/6 下午1:13
 */
public class QDContactActivity extends QDBaseActivity implements QDUserStatusCallBack {

    public final static int MODE_NORMAL = 0;
    public final static int MODE_MULTI = 1;
    public final static int MODE_SINGLE = 2;

    private final static int REQUEST_SELECTED_MEMBER = 1000;
    private final static int REQUEST_SELECTED_SELECT = 1001;

    @BindView(R2.id.view_contact_title)
    View viewTitle;
    @BindView(R2.id.rv_contact_view)
    RecyclerView rvNavigation;
    @BindView(R2.id.lv_contact_list)
    ListView lvList;
    @BindView(R2.id.iv_contact_line)
    ImageView ivLine;
    @BindView(R2.id.rl_contact_bottom_layout)
    RelativeLayout rlBottomLayout;
    @BindView(R2.id.tv_contact_count)
    TextView tvCount;
    @BindView(R2.id.btn_contact_sure)
    Button btnSure;
//    @BindView(R2.id.rl_contact_parent_layout)
//    RelativeLayout rlParentLayout;

    @BindString(R2.string.main_contact)
    String strContact;
    @BindString(R2.string.contact_title)
    String strContactTitle;
    @BindString(R2.string.contact_had_count)
    String strCount;
    @BindString(R2.string.max_selected)
    String strMaxSelected;
    @BindString(R2.string.str_item)
    String strItem;
    @BindString(R2.string.contact_is_forward)
    String strIsForward;
    @BindString(R2.string.contact_forward_success)
    String strForwardSuccess;
    @BindString(R2.string.contact_forward_failed)
    String strForwardFailed;
    @BindString(R2.string.user_detail_dept)
    String strDept;
    @BindString(R2.string.contact_people)
    String strPeople;
    @BindString(R2.string.user_detail_no_permission_chat)
    String strNoPermission;
    @BindView(R2.id.view_place)
    View view_place;

    private boolean isForward;
    private boolean isReturnDeptId;
    private QDMessage message;
    private int mode;
    private int limit;
    private String selectType;

    private List<QDContact> navigationList; //导航栏数据
    private List<QDContact> contactList; //本层通讯录的数据
    private List<String> contactUserIdList; //通讯录人员id列表
    private List<String> selectUserIdList; //已选的人员id列表
    private List<String> selectDeptIdList; //已选的部门id列表；
    private List<String> excludedUserIdList; //不可选的人员id列表
    private List<QDUser> selectUserList; //已选的人员列表;
    private List<QDDept> selectDeptList; //已选的部门列表

    private Map<String, String> listPositionMap; //listView滚动到的位置
    private QDRootContactAdapter adapter;
    private QDContactNavigationAdapter navigationAdapter;

    private QDAlertView alertView;
    private boolean isCanCancel;

    /**
     * 导航栏点击事件监听
     */
    private QDContactNavigationAdapter.OnNavigationItemClickListener onNavigationItemClickListener = new QDContactNavigationAdapter.OnNavigationItemClickListener() {
        @Override
        public void navigationItemClick(int position) {
            QDContact contact = navigationList.get(position);
            navigationList = navigationList.subList(0, position + 1);
            refreshList(contact);
            navigationAdapter.setContactList(navigationList);
            titleCloseVisible();
        }
    };

    /**
     * 部门选择事件监听
     */
    private QDRootContactAdapter.OnDeptSelectListener onDeptSelectListener = new QDRootContactAdapter.OnDeptSelectListener() {
        @Override
        public void onDeptSelect(QDContact contact, boolean isRemove) {
            String deptId = contact.getId();
            if (isRemove) {
                int index = selectDeptIdList.indexOf(deptId);
                selectDeptIdList.remove(deptId);
                selectDeptList.remove(index);
            } else {
                QDDept dept = QDDeptHelper.getDeptById(deptId);
                if (!selectDeptIdList.contains(deptId))
                    selectDeptIdList.add(deptId);
                selectDeptList.add(dept);

                List<QDDept> childDeptList = QDDeptHelper.getDeptsWithCidAndParentId(dept.getCid(), dept.getId(), dept.getCode()); //该部门下面的所有子部门
                List<QDRelation> relationList = new ArrayList<>();
//                relationList.addAll(QDRelationHelper.getRelationByDeptId(deptId));
                for (QDDept childDept : childDeptList) {
                    relationList.addAll(QDRelationHelper.getRelationByDeptId(childDept.getId()));
                }

                for (QDRelation relation : relationList) {
                    String userId = relation.getChildId();
                    if (selectUserIdList.contains(userId)) {
                        int index = selectUserIdList.indexOf(userId);
                        selectUserIdList.remove(userId);
                        selectUserList.remove(index);
                    }
                }
            }
            updateBottomLayout();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        view_place.setVisibility(View.VISIBLE);
        initTitleView(viewTitle);
//        tvTitleName.setText(strContact);
        listPositionMap = new HashMap<>();
        contactUserIdList = new ArrayList<>();

        String contactId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_ID);
        int contactType = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_TYPE, QDContact.TYPE_CONTACT);
        mode = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, MODE_NORMAL);
        isForward = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, false);
        isReturnDeptId = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_RETURN_DEPT_ID, false);
        isCanCancel = getIntent().getBooleanExtra("isCanCancel", false);
        if (isForward) {
            message = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE);
        }

        if (mode != MODE_NORMAL) {
            tvTitleName.setText(strContactTitle);
        } else {
            tvTitleName.setText(strContact);
        }

        if (mode == MODE_MULTI) {
            rlBottomLayout.setVisibility(View.VISIBLE);
            selectUserIdList = getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST);
            selectUserList = getIntent().getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
            excludedUserIdList = getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_EXCLUDED_ID_LIST);
            selectDeptIdList = getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_ID_LIST);
            selectDeptList = getIntent().getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_LIST);
            limit = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_SELECT_LIMIT, 0);
            selectType = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_SELECT_TYPE);

            if (selectUserIdList == null) {
                selectUserIdList = new ArrayList<>();
            }
            if (selectUserList == null) {
                selectUserList = new ArrayList<>();
            }
            if (excludedUserIdList == null) {
                excludedUserIdList = new ArrayList<>();
            }
            if (selectDeptList == null) {
                selectDeptList = new ArrayList<>();
            }
            if (selectDeptIdList == null) {
                selectDeptIdList = new ArrayList<>();
            }
            updateBottomLayout();
        }
        contactList = getContactData(contactId, contactType);
        listPositionMap.put(contactId + contactType, "0,0");
        initListView();
        initNavigationData(contactId, contactType);
        initNavigationView();
        titleCloseVisible();

        lvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //不滚动时候的位置
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int index = lvList.getFirstVisiblePosition();
                    View v = lvList.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();
                    QDContact contact = navigationList.get(navigationList.size() - 1);
                    listPositionMap.put(contact.getId() + contact.getType(), index + "," + top);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
//
//        if (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_OPEN_WATERMARK)) {
//            rlParentLayout.setBackground(QDBitmapUtil.addWaterMark(context, QDLanderInfo.getInstance().getName(), Color.WHITE));
//        }

        QDUserStatusCallBackManager.getInstatnce().addCallBack(this);
    }

    /**
     * 初始化通讯录列表
     */
    private void initListView() {
        adapter = new QDRootContactAdapter(context);
        lvList.setAdapter(adapter);
        adapter.setMode(mode);
        adapter.setCanCancel(isCanCancel);
        if (mode == MODE_MULTI) {
            adapter.setDeptSelectListener(onDeptSelectListener);
            adapter.setSelectUserIds(selectUserIdList);
            adapter.setExcludedUserIds(excludedUserIdList);
            adapter.setSelectDeptIds(selectDeptIdList);
            adapter.setLimit(limit);
            adapter.setSelectType(selectType);
        }
        adapter.setContactList(contactList);
    }

    /**
     * 初始化导航数据
     *
     * @param contactId
     * @param type
     */
    private void initNavigationData(String contactId, int type) {
        navigationList = new ArrayList<>();
        //导航栏数据有从通讯录进来和我的部门进来两种情况，从通讯录进来的话只有首层公司数据；从我的部门
        //进来的话，要将从我的部门开始一层层往上获取，部门获取完后还要公司的信息和首层数据
        if (type == QDContact.TYPE_SELF_DEPT) {
            QDDept dept = QDDeptHelper.getDeptById(contactId);
            navigationList.add(QDContactUtil.createDeptContact(dept));
            String parentId = dept.getParentId();
            while (!TextUtils.isEmpty(parentId)) {
                QDDept parentDept = QDDeptHelper.getDeptById(parentId);
                navigationList.add(QDContactUtil.createDeptContact(parentDept));
                parentId = parentDept.getParentId();
            }
            QDCompany company = QDCompanyHelper.getCompanyById(dept.getCid());
            navigationList.add(QDContactUtil.createCompanyContact(company));
        }
        QDContact contact = new QDContact();
        contact.setName(strContact);
        contact.setType(QDContact.TYPE_CONTACT);
        navigationList.add(contact);
        Collections.reverse(navigationList);
    }

    /**
     * 初始化导航栏
     */
    private void initNavigationView() {
        navigationAdapter = new QDContactNavigationAdapter(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvNavigation.setLayoutManager(layoutManager);
        rvNavigation.setAdapter(navigationAdapter);
        navigationAdapter.setContactList(navigationList);
        navigationAdapter.setOnNavigationItemClickListener(onNavigationItemClickListener);
    }

    @OnItemClick(R2.id.lv_contact_list)
    public void onItemClick(int position) {
        QDContact contact = adapter.getItem(position);
        listPositionMap.put(contact.getId() + contact.getType(), "0,0");
        int type = contact.getType();
        switch (type) {
            case QDContact.TYPE_DEPT:
            case QDContact.TYPE_COMPANY:
                List<String> deptIdList = adapter.getSelectDeptIds();
                if (mode == MODE_MULTI && deptIdList.contains(contact.getId()) && type == QDContact.TYPE_DEPT) {
                    return;
                }
                contactList = getContactData(contact.getId(), type);
                adapter.setContactList(contactList);
                addNavigationData(contact);
                break;
            case QDContact.TYPE_USER:
                if (mode == MODE_MULTI) {
//                    if (!TextUtils.isEmpty(selectType) && selectType.equalsIgnoreCase("depts")) {
//                        Intent intent = new Intent(context, QDUserDetailActivity.class);
//                        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, contact.getId());
//                        startActivity(intent);
//                        return;
//                    }
                    if (excludedUserIdList.contains(contact.getId())) {
                        return;
                    }
                    int count = selectDeptIdList.size() + selectUserIdList.size();
                    String userId = contact.getId();
                    if (limit != 0 && limit <= count && !selectUserIdList.contains(userId)) {
                        QDUtil.showToast(context, strMaxSelected + limit + strItem);
                        return;
                    }
                    if (selectUserIdList.contains(userId)) {
                        int index = selectUserIdList.indexOf(userId);
                        selectUserList.remove(index);
                        selectUserIdList.remove(userId);
                    } else {
                        selectUserIdList.add(userId);
                        QDUser user = QDUserHelper.getUserById(userId);
//                        user.setId(userId);
//                        user.setName(contact.getName());
//                        user.setPic(contact.getIcon());
                        selectUserList.add(user);
                    }
                    adapter.setSelectUserIds(selectUserIdList);
                    adapter.notifyDataSetChanged();
                    updateBottomLayout();
                } else if (mode == MODE_SINGLE) {
                    if (isForward) {
                        int level = contact.getLevel();
                        if (level < QDLanderInfo.getInstance().getLevel()) {
                            QDConversation conversation = QDConversationHelper.getConversationById(contact.getId());
                            if (conversation == null) {
                                QDUtil.showToast(context, strNoPermission);
                                return;
                            }
                        }

                        QDUser user = new QDUser();
                        user.setId(contact.getId());
                        user.setName(contact.getName());
                        user.setPic(contact.getIcon());
                        initAlert(user);
                    } else {
                        if (contact.getId().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                            return;
                        }
                        QDUser user = new QDUser();
                        user.setId(contact.getId());
                        user.setName(contact.getName());
                        user.setPic(contact.getIcon());
                        Intent intent = new Intent();
                        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER, user);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
//                else {
//                    Intent intent = new Intent(context, QDUserDetailActivity.class);
//                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, contact.getId());
//                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, mode);
//                    startActivity(intent);
//                }
                break;
        }
    }

    @OnClick({R2.id.tv_title_close, R2.id.btn_contact_sure, R2.id.tv_contact_count, R2.id.view_contact_search})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_title_close) {
            finish();

        } else if (i == R.id.btn_contact_sure) {
            Intent intent = new Intent();
            if (isReturnDeptId) {
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) selectUserIdList);
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_ID_LIST, (ArrayList<String>) selectDeptIdList);
                intent.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST, (ArrayList<? extends Parcelable>) selectUserList);
                intent.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_LIST, (ArrayList<? extends Parcelable>) selectDeptList);
            } else {
                List<QDRelation> relationList = new ArrayList<>();
                for (String deptId : selectDeptIdList) {
                    QDDept dept = QDDeptHelper.getDeptById(deptId);
                    List<QDDept> childDeptList = QDDeptHelper.getDeptsWithCidAndParentId(dept.getCid(), dept.getId(), dept.getCode()); //该部门下面的所有子部门
//                        relationList.addAll(QDRelationHelper.getRelationByDeptId(deptId));
                    for (QDDept childDept : childDeptList) {
                        relationList.addAll(QDRelationHelper.getRelationByDeptId(childDept.getId()));
                    }
                }
                List<String> idList = new ArrayList<>();
                for (QDRelation relation : relationList) {
                    if (!excludedUserIdList.contains(relation.getChildId()) && !selectUserIdList.contains(relation.getChildId())) {
//                            QDUser user = QDUserHelper.getUserById(relation.getChildId());
//                            user.setId(relation.getChildId());
//                            user.setName(relation.getUser().getName());
//                            user.setPic(relation.getUser().getPic());
                        idList.add(relation.getChildId());
                    }
                }
                if ((idList.size() + selectUserIdList.size()) > 200) {
                    QDUtil.showToast(context, context.getResources().getString(R.string.number_exceeding_limit));
                    idList.clear();
                    return;
                }
                for (String id : idList) {
                    if (!selectUserIdList.contains(id)) {
                        selectUserIdList.add(id);
                        selectUserList.add(QDUserHelper.getUserById(id));
                    }
                }

                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) selectUserIdList);
                intent.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST, (ArrayList<? extends Parcelable>) selectUserList);
            }
            setResult(RESULT_OK, intent);
            finish();

        } else if (i == R.id.tv_contact_count) {
            int size = selectUserIdList.size() + selectDeptIdList.size();
            if (size == 0) {
                return;
            }
            Intent intent1 = new Intent(context, QDSelectedMemberListActivity.class);
            intent1.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) selectUserIdList);
            intent1.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_ID_LIST, (ArrayList<String>) selectDeptIdList);
            intent1.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST, (ArrayList<? extends Parcelable>) selectUserList);
            intent1.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_LIST, (ArrayList<? extends Parcelable>) selectDeptList);
            startActivityForResult(intent1, REQUEST_SELECTED_MEMBER);

        } else if (i == R.id.view_contact_search) {
            Intent searchIntent = new Intent(context, QDSearchActivity.class);
            searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TYPE, QDSearchActivity.SEARCH_TYPE_USER);
            if (isForward) {
                searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, MODE_SINGLE);
                searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, isForward);
                searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, message);
                startActivityForResult(searchIntent, REQUEST_SELECTED_SELECT);
            } else {
                if (mode != MODE_NORMAL) {
                    searchIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, MODE_SINGLE);
                    startActivityForResult(searchIntent, REQUEST_SELECTED_SELECT);
                } else {
                    startActivity(searchIntent);
                }
            }

        }
    }

    /**
     * 根据contactId和contactType获取通讯录数据
     *
     * @param contactId
     * @param contactType
     * @return
     */
    private List<QDContact> getContactData(String contactId, int contactType) {
        if (contactType == QDContact.TYPE_CONTACT) {
            return createCompanyContact();
        } else if (contactType == QDContact.TYPE_COMPANY) {
            return createDeptAndUserContact(contactId, true);
        } else {
            return createDeptAndUserContact(contactId, false);
        }
    }

    /**
     * 获取通讯录第一层数据（公司数据）
     *
     * @return
     */
    private List<QDContact> createCompanyContact() {
        List<QDContact> contactList = new ArrayList<>();
        List<QDCompany> companyList = QDSortUtil.sortCompany(QDCompanyHelper.getAllCompanies());
        for (QDCompany company : companyList) {
            contactList.add(QDContactUtil.createCompanyContact(company));
        }
        return contactList;
    }

    /**
     * 获取通讯录除了第一层的数据
     *
     * @param contactId
     * @param isCompany 是否是公司下面的第一层
     * @return
     */
    private List<QDContact> createDeptAndUserContact(String contactId, boolean isCompany) {
        List<QDContact> contactList = new ArrayList<>();
        List<QDDept> deptList;
        List<QDUser> userList;
        if (isCompany) {
            deptList = QDDeptHelper.getFirstDeptsByCid(contactId);
            userList = QDRelationHelper.getUserList(contactId, QDRelation.TYPE_CU);
        } else {
            deptList = QDDeptHelper.getDeptsByParenId(contactId);
            userList = QDRelationHelper.getUserList(contactId, QDRelation.TYPE_DU);
        }
        List<QDDept> sortDeptList = QDSortUtil.sortDept(deptList);
        List<QDUser> sortUserList = QDSortUtil.sortUser(userList);
        for (QDDept dept : sortDeptList) {
            contactList.add(QDContactUtil.createDeptContact(dept));
        }
//        contactUserIdList.clear();
        for (QDUser user : sortUserList) {
            contactList.add(QDContactUtil.createUserContact(user));
//            contactUserIdList.add(user.getId());
        }
//        QDClient.getInstance().subscribeStatus(contactUserIdList);
        return contactList;
    }

    /**
     * 刷新通讯录列表
     *
     * @param contact
     */
    private void refreshList(QDContact contact) {
        contactList = getContactData(contact.getId(), contact.getType());
        adapter.setContactList(contactList);
        setListViewPosition();
    }

    /**
     * 导航栏关闭按钮是否显示
     */
    private void titleCloseVisible() {
        int size = navigationList.size();
        if (size > 1) {
            tvTitleClose.setVisibility(View.VISIBLE);
        } else {
            tvTitleClose.setVisibility(View.GONE);
        }
    }

    /**
     * 导航栏增加一项
     *
     * @param contact
     */
    private void addNavigationData(QDContact contact) {
        navigationList.add(contact);
        navigationAdapter.setContactList(navigationList);
        titleCloseVisible();
    }

    /**
     * 删除导航栏数据最后一项
     */
    private void removeNavigationLastData() {
        navigationList.remove(navigationList.size() - 1);
        navigationAdapter.setContactList(navigationList);
        titleCloseVisible();
    }

    /**
     * 更新已选择的数量
     */
    private void updateBottomLayout() {
        StringBuilder sb = new StringBuilder();
        sb.append(strCount).append(" ")
                .append(selectUserIdList.size())
                .append(strPeople).append(" ");
        tvCount.setText(sb.toString());
    }

    /**
     * 返回按钮和返回键点击事件
     */
    @Override
    public void onBackPressed() {
        int size = navigationList.size();
        if (size > 1) {
            QDContact contact = navigationList.get(size - 2);
            removeNavigationLastData();
            refreshList(contact);
        } else {
            finish();
        }
    }

    /**
     * 设置列表位置
     */
    private void setListViewPosition() {
        QDContact contact = navigationList.get(navigationList.size() - 1);
        String info = listPositionMap.get(contact.getId() + contact.getType());
        if (TextUtils.isEmpty(info)) {
            listPositionMap.put(contact.getId() + contact.getType(), "0,0");
        } else {
            String[] infos = info.split(",");
            lvList.setSelectionFromTop(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]));
        }
    }

    /**
     * 转发弹出框
     *
     * @param user
     */
    private void initAlert(final QDUser user) {
        if (alertView == null) {
            alertView = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Alert)
                    .setContent(strIsForward)
                    .isHaveCancelBtn(true)
                    .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                        @Override
                        public void onClick(boolean isSure) {
                            if (isSure) {
                                QDClient.getInstance().forwardMessage(user, message, new QDResultCallBack() {
                                    @Override
                                    public void onError(String errorMsg) {
                                        QDUtil.showToast(context, strForwardFailed);
                                    }

                                    @Override
                                    public void onSuccess(Object o) {
                                        QDUtil.showToast(context, strForwardSuccess);
                                    }
                                });
                                finish();
                            }
                        }
                    }).build();
        }
        alertView.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SELECTED_MEMBER:
                    selectUserList = data.getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
                    selectUserIdList = data.getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST);
                    selectDeptIdList = data.getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_ID_LIST);
                    selectDeptList = data.getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_LIST);
                    adapter.setSelectUserIds(selectUserIdList);
                    adapter.setSelectDeptIds(selectDeptIdList);
                    adapter.notifyDataSetChanged();
                    updateBottomLayout();
                    break;
                case REQUEST_SELECTED_SELECT:
                    String id = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID);
                    if (mode == MODE_SINGLE) {
                        QDUser user = data.getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_USER);
                        Intent intent = new Intent();
                        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER, user);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        QDUser user = data.getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_USER);
                        if (!selectUserIdList.contains(id) && !excludedUserIdList.contains(id)) {
                            selectUserIdList.add(id);
                            selectUserList.add(user);
                            adapter.setSelectUserIds(selectUserIdList);
                            adapter.notifyDataSetChanged();
                            updateBottomLayout();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDUserStatusCallBackManager.getInstatnce().removeLast();
    }

    @Override
    public void onUserStatusChange(String userId, int status) {
        if (contactUserIdList.contains(userId)) {
            QDContact contact = navigationList.get(navigationList.size() - 1);
            adapter.setContactList(getContactData(contact.getId(), contact.getType()));
        }
    }

    @Override
    public void onUserInfoChange(QDUser user) {
        if (contactUserIdList.contains(user.getId())) {
            QDContact contact = navigationList.get(navigationList.size() - 1);
            adapter.setContactList(getContactData(contact.getId(), contact.getType()));
        }
    }

    @Override
    public void onUserSecretChange(String userId, int status) {

    }
}
