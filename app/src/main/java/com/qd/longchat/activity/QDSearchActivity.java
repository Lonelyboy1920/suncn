package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gavin.giframe.widget.GITextView;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.manager.QDFriendManager;
import com.longchat.base.model.gd.QDSearchModel;
import com.longchat.base.model.gd.QDSearchUserModel;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDSearchAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.model.QDSearchInfo;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDSearchUtil;
import com.qd.longchat.view.QDAlertView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

import com.qd.longchat.R2;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/5 下午2:18
 */
public class QDSearchActivity extends QDBaseActivity {

    public final static int SEARCH_TYPE_CONVERSATION = 101; //最近联系人
    public final static int SEARCH_TYPE_USER = 102; //通讯录
    public final static int SEARCH_TYPE_GROUP = 103; //群组
    public final static int SEARCH_TYPE_MORE_USER = 104; //更多联系人
    public final static int SEARCH_TYPE_MORE_PERSONAL_MSG = 105; //更多聊天记录
    public final static int SEARCH_TYPE_MORE_GROUP_MSG = 106; //更多群聊天记录
    public final static int SEARCH_TYPE_PERSONAL_HISTORY = 107; //和某人的聊天记录
    public final static int SEARCH_TYPE_GROUP_HISTORY = 108; //某群的聊天记录
    public final static int SEARCH_TYPE_GROUP_MEMBER = 109; //群成员
    public final static int SEARCH_TYPE_GROUP_MEMBER_DEL = 111; //群成员
    public final static int SEARCH_TYPE_REMOTE = 110;
    public final static int SEARCH_TYPE_MORE_GROUP = 110; //更多群

    @BindView(R2.id.et_search)
    EditText etSearch;
    @BindView(R2.id.tv_search_cancel)
    TextView tvCancel;
    @BindView(R2.id.lv_search_list)
    ListView listView;
    @BindView(R2.id.iv_search_back)
    GITextView ivBack;
    @BindView(R2.id.tv_search_title)
    TextView tvTitle;
    @BindView(R2.id.iv_search_title_line)
    ImageView ivTitleLine;
    @BindString(R2.string.search_msg_history)
    String strSearchMsgHistory;
    @BindString(R2.string.search_user)
    String strSearchUser;
    @BindString(R2.string.search_group)
    String strSearchGroup;
    @BindString(R2.string.search_personal_msg)
    String strPersonalMsg;
    @BindString(R2.string.search_group_msg)
    String strGroupMsg;
    @BindString(R2.string.search_linkman)
    String strLinkman;
    @BindString(R2.string.contact_is_forward)
    String strIsForward;
    @BindString(R2.string.str_with)
    String strWith;
    @BindString(R2.string.search_history)
    String strHistory;
    @BindString(R2.string.group_title)
    String strGroup;


    private int type;

    private Handler mHandler;

    private String searchText;

    private String searchTitle;

    private QDSearchAdapter adapter;

    private String chatId;

    private String groupId;

    private int mode;

    private QDAlertView alertView;

    private QDMessage message;

    private List<String> seletedUserIdList;

    private List<QDUser> seletedUserList;

    private boolean isRemote;

    private QDSearchAdapter.OnMoreClickListener onMoreClickListener = new QDSearchAdapter.OnMoreClickListener() {
        @Override
        public void onMoreClick(int itemType) {
            if (itemType == QDSearchInfo.ITEM_TYPE_USER) {
                toSearchActivity(SEARCH_TYPE_MORE_USER, strLinkman);
            } else if (itemType == QDSearchInfo.ITEM_TYPE_PERSON_CHAT) {
                toSearchActivity(SEARCH_TYPE_MORE_PERSONAL_MSG, strPersonalMsg);
            } else if (itemType == QDSearchInfo.ITEM_TYPE_GROUP) {
                toSearchActivity(SEARCH_TYPE_MORE_GROUP, strGroup);
            } else {
                toSearchActivity(SEARCH_TYPE_MORE_GROUP_MSG, strGroupMsg);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mHandler = new Handler();
        type = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TYPE, SEARCH_TYPE_CONVERSATION);
        mode = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_NORMAL);
        message = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE);
        isRemote = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_REMOTE, false);

        if (type == SEARCH_TYPE_PERSONAL_HISTORY || type == SEARCH_TYPE_GROUP_HISTORY || type == SEARCH_TYPE_MORE_PERSONAL_MSG || type == SEARCH_TYPE_MORE_GROUP_MSG) {
            etSearch.setHint(strSearchMsgHistory);
        } else if (type == SEARCH_TYPE_USER) {
            etSearch.setHint(strSearchUser);
        } else if (type == SEARCH_TYPE_GROUP) {
            etSearch.setHint(strSearchGroup);
        }

        if (type == SEARCH_TYPE_PERSONAL_HISTORY || type == SEARCH_TYPE_GROUP_HISTORY) {
            chatId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID);
        }

        if (type == SEARCH_TYPE_MORE_GROUP || type == SEARCH_TYPE_MORE_USER || type == SEARCH_TYPE_MORE_PERSONAL_MSG || type == SEARCH_TYPE_MORE_GROUP_MSG
                || type == SEARCH_TYPE_PERSONAL_HISTORY || type == SEARCH_TYPE_GROUP_HISTORY) {
            searchText = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TEXT);
            searchTitle = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TITLE);

            ivBack.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(searchText)) {
                tvTitle.setVisibility(View.VISIBLE);
                ivTitleLine.setVisibility(View.VISIBLE);
                tvTitle.setText(searchTitle);
            }
        } else {
            ivBack.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
            ivTitleLine.setVisibility(View.GONE);
        }

        if (type == SEARCH_TYPE_GROUP_MEMBER || type==SEARCH_TYPE_GROUP_MEMBER_DEL) {
            groupId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID);
        }

        initListView();

        if (!TextUtils.isEmpty(searchText)) {
            etSearch.setText(searchText);
            etSearch.setSelection(searchText.length());
        }

        if (isRemote) {
            etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String text = etSearch.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            QDFriendManager.getInstance().searchFriend(text, new QDResultCallBack<QDSearchModel>() {
                                @Override
                                public void onError(String errorMsg) {

                                }

                                @Override
                                public void onSuccess(QDSearchModel model) {
                                    List<QDSearchUserModel> userList = model.getList().getUsers();
                                    List<QDSearchUserModel> customerList = model.getList().getCustomers();
                                    List<QDSearchInfo> searchInfoList = new ArrayList<>();
                                    if (userList != null && userList.size() != 0) {
                                        for (QDSearchUserModel user : userList) {
                                            QDSearchInfo searchInfo = new QDSearchInfo();
                                            searchInfo.setId(user.getId());
                                            searchInfo.setIcon(user.getAvatar());
                                            searchInfo.setName(user.getName());
                                            searchInfo.setInfoText(user.getDeptName());
                                            searchInfo.setType(SEARCH_TYPE_REMOTE);
                                            searchInfoList.add(searchInfo);
                                        }
                                    }
                                    if (customerList != null && customerList.size() != 0) {
                                        for (QDSearchUserModel customer : customerList) {
                                            QDSearchInfo searchInfo = new QDSearchInfo();
                                            searchInfo.setId(customer.getId());
                                            searchInfo.setIcon(customer.getAvatar());
                                            searchInfo.setName(customer.getName());
                                            searchInfo.setType(SEARCH_TYPE_REMOTE);
                                            searchInfoList.add(searchInfo);
                                        }
                                    }
                                    adapter.setData(searchInfoList);
                                }
                            });
                        } else {
                            adapter.setData(null);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    return false;
                }
            });
        }

        EventBus.getDefault().register(this);
    }

    private void initListView() {
        adapter = new QDSearchAdapter(context);
        listView.setAdapter(adapter);
        adapter.setOnMoreClickListener(onMoreClickListener);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    Glide.with(context).resumeRequests();
                } else {
                    Glide.with(context).pauseRequests();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @OnClick({R2.id.tv_search_cancel, R2.id.iv_search_back})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_search_cancel) {
            EventBus.getDefault().post(true);
        } else {
            finish();
        }
    }

    @OnTextChanged(R2.id.et_search)
    public void onTextChange(CharSequence s, int start, int before, int count) {
        if (isRemote) {
            return;
        }
        if (s.length() <= 0) {
            adapter.setData(null);
            adapter.notifyDataSetChanged();
            return;
        }
        searchText = s.toString();
        mHandler.removeCallbacks(searchTask);
        mHandler.postDelayed(searchTask, 200);
    }

    @OnItemClick(R2.id.lv_search_list)
    public void onItemClick(int position) {
        QDSearchInfo searchInfo = adapter.getItem(position);
        switch (type) {
            case SEARCH_TYPE_CONVERSATION:
                int itemType = searchInfo.getItemType();
                if (itemType == QDSearchInfo.ITEM_TYPE_USER) {
                    toPersonDetail(searchInfo.getId());
                } else if (itemType == QDSearchInfo.ITEM_TYPE_PERSON_CHAT) {
                    int count = searchInfo.getCount();
                    if (count <= 1) {
                        toPersonChat(searchInfo.getId(), searchInfo.getTime());
                    } else {
                        toSearchActivity(SEARCH_TYPE_PERSONAL_HISTORY, searchInfo.getId(), strWith + "\"" + searchInfo.getName() + "\"" + strHistory);
                    }

                } else if (itemType == QDSearchInfo.ITEM_TYPE_GROUP_CHAT) {
                    int count = searchInfo.getCount();
                    if (count <= 1) {
                        toGroupChat(searchInfo.getId(), searchInfo.getTime());
                    } else {
                        toSearchActivity(SEARCH_TYPE_GROUP_HISTORY, searchInfo.getId(), strWith + "\"" + searchInfo.getName() + "\"" + strHistory);
                    }
                } else if (itemType == QDSearchInfo.ITEM_TYPE_GROUP) {
                    toGroupChat(searchInfo.getId(), searchInfo.getTime());
                }
                break;
            case SEARCH_TYPE_GROUP_MEMBER:
                toPersonChat(searchInfo.getId(),searchInfo.getTime());
                break;
            case SEARCH_TYPE_GROUP_MEMBER_DEL:
                Intent intent1 = new Intent();
                intent1.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, searchInfo.getId());
                setResult(RESULT_OK, intent1);
                finish();
                break;
            case SEARCH_TYPE_USER:
                if (mode == QDContactActivity.MODE_NORMAL) {
                    toPersonDetail(searchInfo.getId());
                } else if (mode == QDContactActivity.MODE_SINGLE) {
                    QDUser user = new QDUser();
                    user.setId(searchInfo.getId());
                    user.setName(searchInfo.getName());
                    user.setPic(searchInfo.getIcon());
                    if (getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, false)) {
                        initAlert(user);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, searchInfo.getId());
                        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER, user);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;
            case SEARCH_TYPE_GROUP:
                toGroupChat(searchInfo.getId(), searchInfo.getTime());
                break;
            case SEARCH_TYPE_PERSONAL_HISTORY:
                toPersonChat(chatId, searchInfo.getTime());
                break;
            case SEARCH_TYPE_GROUP_HISTORY:
                toGroupChat(chatId, searchInfo.getTime());
                break;
            case SEARCH_TYPE_REMOTE:
                String selfId = QDLanderInfo.getInstance().getId();
                String chatId = searchInfo.getId();
                if (selfId.toLowerCase().startsWith("im_") || chatId.toLowerCase().startsWith("im_")) {
                    Intent intent = new Intent(context, QDAddFriendActivity.class);
                    QDUser user = new QDUser();
                    user.setId(searchInfo.getId());
                    user.setName(searchInfo.getName());
                    user.setPic(searchInfo.getIcon());
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER, user);
                    startActivity(intent);
                }
//                else {
//                    Intent intent = new Intent(context, QDUserDetailActivity.class);
//                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, chatId);
//                    startActivity(intent);
//                }
                break;
        }
    }

    /**
     * 跳转到个人聊天页面
     *
     * @param chatId
     */
    private void toPersonDetail(String chatId) {
//        Intent intent = new Intent(context, QDUserDetailActivity.class);
//        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, chatId);
//        startActivity(intent);
    }

    private void toPersonChat(String chatId, long time) {
        Intent intent = new Intent(context, QDPersonChatActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, chatId);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE_TIME, time);
        startActivity(intent);
    }

    /**
     * 跳转到群聊页面
     *
     * @param chatId
     */
    private void toGroupChat(String chatId, long time) {
        Intent intent = new Intent(context, QDGroupChatActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, chatId);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE_TIME, time);
        startActivity(intent);
    }

    /**
     * 创建新的搜索页面
     *
     * @param searchType
     * @param title
     */
    private void toSearchActivity(int searchType, String title) {
        toSearchActivity(searchType, "", title);
    }

    private void toSearchActivity(int searchType, String chatId, String title) {
        Intent intent = new Intent(context, QDSearchActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TYPE, searchType);
        if (!TextUtils.isEmpty(chatId)) {
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, chatId);
        }
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TEXT, searchText);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TITLE, title);
        startActivity(intent);
    }


    private Runnable searchTask = new Runnable() {
        @Override
        public void run() {
            switch (type) {
                case SEARCH_TYPE_CONVERSATION:
                    adapter.setData(QDSearchUtil.searchMessage(searchText));
                    break;
                case SEARCH_TYPE_MORE_USER:
                case SEARCH_TYPE_USER:
                    adapter.setData(QDSearchUtil.searchUser(searchText));
                    break;
                case SEARCH_TYPE_MORE_GROUP:
                case SEARCH_TYPE_GROUP:
                    adapter.setData(QDSearchUtil.searchGroup(searchText));
                    break;
                case SEARCH_TYPE_PERSONAL_HISTORY:
                    adapter.setData(QDSearchUtil.searchUserHistory(chatId, searchText));
                    break;
                case SEARCH_TYPE_GROUP_HISTORY:
                    adapter.setData(QDSearchUtil.searchGroupHistory(chatId, searchText));
                    break;
                case SEARCH_TYPE_MORE_PERSONAL_MSG:
                    adapter.setData(QDSearchUtil.searchPersonalMessageForMore(searchText));
                    break;
                case SEARCH_TYPE_MORE_GROUP_MSG:
                    adapter.setData(QDSearchUtil.searchGroupMessageFroMore(searchText));
                    break;
                case SEARCH_TYPE_GROUP_MEMBER:
                case SEARCH_TYPE_GROUP_MEMBER_DEL:
                    adapter.setData(QDSearchUtil.searchGroupMember(groupId, searchText));
                    break;
            }
        }
    };

    @Subscribe
    public void onSubscribe(Boolean isFinish) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

                                    }

                                    @Override
                                    public void onSuccess(Object o) {

                                    }
                                });
                            }
                        }
                    }).build();
        }
        alertView.show();
    }
}
