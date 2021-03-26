package com.suncn.ihold_zxztc.activity.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildLongClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDConversationCallBack;
import com.longchat.base.callback.QDLoginCallBack;
import com.longchat.base.config.QDSDKConfig;
import com.longchat.base.dao.QDConversation;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDCompanyHelper;
import com.longchat.base.databases.QDConversationHelper;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.databases.QDGroupNoticeHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.listener.QDConversationCallBackManager;
import com.longchat.base.model.QDLoginInfo;
import com.longchat.base.model.QDLoginParams;
import com.longchat.base.util.QDConst;
import com.qd.longchat.activity.QDGroupChatActivity;
import com.qd.longchat.activity.QDGroupNotifyActivity;
import com.qd.longchat.activity.QDPersonChatActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.MainActivity;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.activity.netmeet.NetMeetListActivity;
import com.suncn.ihold_zxztc.adapter.chat.RecentMessageListAdapter;
import com.suncn.ihold_zxztc.bean.EventBusCarrier;
import com.suncn.ihold_zxztc.bean.chat.MessageInfo;
import com.suncn.ihold_zxztc.bean.chat.RecentMessageListData;
import com.suncn.ihold_zxztc.bean.chat.UserInfoBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.DividerDecoration;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 消息主界面
 */
public class Message_MainFragment extends BaseFragment implements QDConversationCallBack, QDLoginCallBack {
    @BindView(id = R.id.btn_two, click = true)
    private GITextView contact_TextView;//通讯录按钮
    @BindView(id = R.id.ll_xxtx, click = true)
    private LinearLayout xxtx_LinearLayout;//消息提醒LinearLayout
    @BindView(id = R.id.tv_xxtx)
    private TextView xxtx_TextView;//消息提醒内容
    @BindView(id = R.id.tv_xxtx_count)
    private TextView xxtxCount_TextView;//消息提醒数量
    @BindView(id = R.id.ll_tzgg, click = true)
    private LinearLayout tzgg_LinearLayout;//通知公告LinearLayout
    @BindView(id = R.id.tv_tzgg)
    private TextView tzgg_TextView;//通知公告内容
    @BindView(id = R.id.tv_tzgg_count)
    private TextView tzggCount_TextView;//通知公告数量
    @BindView(id = R.id.ll_sphy, click = true)
    private LinearLayout sphy_LinearLayout;//视频会议LinearLayout
    @BindView(id = R.id.tv_sphy)
    private TextView sphy_TextView;//视频会议内容
    @BindView(id = R.id.tv_sphy_count)
    private TextView sphyCount_TextView;//视频会议数量
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.refreshlayout)
    private SmartRefreshLayout refreshLayout;
    private RecentMessageListAdapter adapter;
    private List<UserInfoBean> objData = new ArrayList<>();
    private List<QDConversation> conversationList = new ArrayList<>();//启达聊天列表
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GILogUtil.i("action:" + intent.getAction());
            GILogUtil.i("messageInfo:" + intent.getSerializableExtra("messageInfo"));
            MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("messageInfo");
            boolean isNew = false;
            for (int i = 0; i < objData.size(); i++) {
                UserInfoBean userInfoBean = objData.get(i);
                if (messageInfo.getStrLinkId().contains(userInfoBean.getStrLinkId())) {
                    if ("3".equals(messageInfo.getStrMsgType())) {
                        userInfoBean.setStrMsgContent(messageInfo.getGroupNickname() + "：" + messageInfo.getContent());
                    } else {
                        userInfoBean.setStrMsgContent(messageInfo.getContent());
                    }
                    userInfoBean.setIntReadStateNo(userInfoBean.getIntReadStateNo() + 1);
                    userInfoBean.setStrSendDate(messageInfo.getTime());
                    objData.remove(userInfoBean);
                    objData.add(0, userInfoBean);
                    isNew = true;
                    adapter.setList(objData);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            if (!isNew) {
                getListData(false);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        GILogUtil.i("Contact_RecentListFragment:onResume");
        if (((MainActivity) getActivity()).currentFragment == this) {
            if (AppConfigUtil.isUseQDIM(activity)) {
                doQdVideoLogin();
                if (GISharedPreUtil.getBoolean(activity, "QDInitSuccess")) {
                    setQDIMData();
                }
            }
            getListData(false);
        }
    }

    /**
     * 刷新启达的im数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier eventBusCarrier) {
        if (eventBusCarrier.getEventType() == 10) {
            setQDIMData();
            QDClient.getInstance().fetchOfflineMsg();
        }
    }

    @Override
    public View inflaterView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_main, null);
    }

    @Override
    public void initData() {
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        EventBus.getDefault().register(this);  //注册EventBus
        setStatusBar(false);
        setHeadTitle(getString(R.string.string_message));
        if (0 == GISharedPreUtil.getInt(activity, "intUserRole") || 1 == GISharedPreUtil.getInt(activity, "intUserRole")) {
            contact_TextView.setVisibility(View.VISIBLE);
            contact_TextView.setText("\ue7de");
            if (AppConfigUtil.isUseQDIM(activity)) {
                goto_Button.setVisibility(View.VISIBLE);
                goto_Button.setText(getString(R.string.font_add));
            } else {
                goto_Button.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contact_TextView.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                contact_TextView.setLayoutParams(params); //使layout更新
            }
        }
        if (ProjectNameUtil.isGZSZX(activity) && 0 == GISharedPreUtil.getInt(activity, "intUserRole")) {
            contact_TextView.setVisibility(View.GONE);
        }
        initRecyclerView();
        if (GISharedPreUtil.getBoolean(activity, "QDInitSuccess")) {//启达的IM从缓存里面取数据
            QDConversationCallBackManager.getInstance().setCallBack(this);
            setQDIMData();
        } else {
            IntentFilter intentFilter = new IntentFilter("com.suncn.zxztc.chat");
            getActivity().registerReceiver(receiver, intentFilter);
        }
        if (AppConfigUtil.isUseQDIM(activity) && QDClient.getInstance().isOnline()) {
            QDClient.getInstance().fetchOfflineMsg();
        }
    }

    /**
     * 设置启达的IM数据
     */
    private void setQDIMData() {
        objData.clear();
        adapter.setList(new ArrayList<>());
        conversationList = QDConversationHelper.loadConversations();
        for (QDConversation qdConversation : conversationList) {
            UserInfoBean userInfoBean = new UserInfoBean();
            userInfoBean.setStrLinkName(qdConversation.getName());
            userInfoBean.setCreationDate(QDDateUtil.getConversationTime(qdConversation.getTime()));
            userInfoBean.setStrMsgContent(qdConversation.getSubname());
            userInfoBean.setStrMsgType(qdConversation.getType() + "");
            userInfoBean.setStrLinkId(qdConversation.getId());
            userInfoBean.setIsTop(qdConversation.getIsTop());
            userInfoBean.setStrSendDate(QDDateUtil.getConversationTime(qdConversation.getTime()));
            if (qdConversation.getType() == QDConversation.TYPE_GROUP) {
                QDGroup group = QDGroupHelper.getGroupById(qdConversation.getId());
                if (group == null) {
                    continue;
                }
                userInfoBean.setIntReadStateNo(QDMessageHelper.getUnreadMessageCountWithGroupId(qdConversation.getId()));
            } else if (qdConversation.getType() == QDConversation.TYPE_GROUP_NOTICE) {
                userInfoBean.setIntReadStateNo(QDGroupNoticeHelper.getUnreadCount());
            } else {
                QDUser user = QDUserHelper.getUserById(qdConversation.getId());
                if (user != null) {
                    userInfoBean.setStrPathUrl(user.getPic());
                }
                userInfoBean.setIntReadStateNo(QDMessageHelper.getUnreadMessageCountWithUserId(qdConversation.getId()));
            }
            objData.add(userInfoBean);
        }
        adapter.setList(objData);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (!getResources().getBoolean(R.bool.IS_OPEN_IM)) {
            getActivity().unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_xxtx://消息提醒
                showActivity(fragment, Message_RemindActivity.class);
                break;
            case R.id.ll_tzgg://通知公告
                showActivity(fragment, Message_NoticeActivity.class);
                break;
            case R.id.ll_sphy://视频会议
                showActivity(fragment, NetMeetListActivity.class);
                break;
            case R.id.btn_goto: // 创建群组
                if (GISharedPreUtil.getBoolean(activity, "QDInitSuccess", true)) {
                    showActivity(fragment, Chat_AddRoomActivity.class);
                } else {
                    showToast("IM未初始化成功，请联系管理员");
                }

                break;
            case R.id.btn_two: // 通讯录
                Bundle bundle = new Bundle();
                bundle.putBoolean("isShowHead", true);
                showActivity(fragment, Contact_MainActivity.class, bundle);
                break;
        }
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        int padding10 = GIDensityUtil.dip2px(activity, 10);
        DividerDecoration itemDecoration = new DividerDecoration(getResources().getColor(R.color.line_bg_white), GIDensityUtil.dip2px(activity, 0.5f), padding10, padding10);//颜色 & 高度 & 左边距 & 右边距
        itemDecoration.setDrawLastItem(false);//设置最后一个item有分割线,默认true.
        itemDecoration.setDrawHeaderFooter(false);//设置分割线是否对Header和Footer有效,默认false.
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity)); //给ERV添加布局管理器
        adapter = new RecentMessageListAdapter(activity);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getListData(false);
            }
        });
        refreshLayout.setEnableLoadMore(false);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                UserInfoBean data = (UserInfoBean) adapter.getItem(position);
                if (AppConfigUtil.isUseQDIM(activity) && QDClient.getInstance().isOnline()) {
                    if (data.getStrMsgType().equals(String.valueOf(QDConversation.TYPE_GROUP))) {
                        Intent groupIntent = new Intent(activity, QDGroupChatActivity.class);
                        groupIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, data.getStrLinkId());
                        startActivity(groupIntent);
                    } else if (data.getStrMsgType().equals(String.valueOf(QDConversation.TYPE_GROUP_NOTICE))) {
                        showActivity(fragment, QDGroupNotifyActivity.class);
                    } else {
                        Intent intent = new Intent(activity, QDPersonChatActivity.class);
                        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, data.getStrLinkId());
                        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_USER, true);
                        startActivity(intent);
                    }
                } else {
                    showToast(getString(R.string.string_im_login_failed));
                }
            }
        });
        adapter.setOnItemChildLongClickListener(new OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                BaseAnimatorSet mBasIn = new BounceTopEnter();
                BaseAnimatorSet mBasOut = new BounceTopEnter();
                try {
                    mBasIn = BounceEnter.class.newInstance();
                    mBasOut = ZoomOutExit.class.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final MaterialDialog dialog = new MaterialDialog(activity);
                dialog.title(getString(R.string.string_tips)).content(getString(R.string.string_is_del_reply)).btnText(getString(R.string.string_cancle), getString(R.string.string_confirm)).showAnim(mBasIn).dismissAnim(mBasOut).show();
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        }, new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                                UserInfoBean data = (UserInfoBean) adapter.getItem(position);
                                QDConversationHelper.deleteConversationById(data.getStrLinkId());
                                setQDIMData();

                            }
                        }
                );
                return false;
            }
        });
    }

    //
    private void getListData(boolean isShow) {
        if (isShow)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().geRecentChatList(textParamMap), 0);
    }

    /**
     * 数据的填充和数量
     */
    private void doLogic(Object object, int sign) {
        prgDialog.closePrgDialog();
        String toastMessage = null;
        try {
            switch (sign) {
                case 0:
                    refreshLayout.finishRefresh();
                    RecentMessageListData recentMessageListData = (RecentMessageListData) object;
                    if (!getResources().getBoolean(R.bool.IS_OPEN_IM)) {
                        objData = recentMessageListData.getObjData();
                        adapter.setList(objData);
                    }
                    ArrayList<RecentMessageListData.NoticeInfo> objList = recentMessageListData.getObjList();
                    if (objList != null && objList.size() > 0) {
                        xxtx_TextView.setText(GIStringUtil.nullToEmpty(objList.get(0).getStrTitle()));
                        if (0 < objList.get(0).getIntNotReadCount()) {
                            xxtxCount_TextView.setVisibility(View.VISIBLE);
                            if (objList.get(0).getIntNotReadCount() > 99) {
                                xxtxCount_TextView.setText("99+");
                            } else {
                                xxtxCount_TextView.setText(objList.get(0).getIntNotReadCount() + "");
                            }
                        } else {
                            xxtxCount_TextView.setVisibility(View.GONE);
                        }
                        tzgg_TextView.setText(GIStringUtil.nullToEmpty(objList.get(1).getStrTitle()));
                        if (0 < objList.get(1).getIntNotReadCount()) {
                            tzggCount_TextView.setVisibility(View.VISIBLE);
                            if (objList.get(0).getIntNotReadCount() > 99) {
                                tzggCount_TextView.setText("99+");
                            } else {
                                tzggCount_TextView.setText(objList.get(1).getIntNotReadCount() + "");
                            }
                        } else {
                            tzggCount_TextView.setVisibility(View.GONE);
                        }
                        if ("1".equals(GISharedPreUtil.getString(activity, "strUseHST"))) {
                            sphy_LinearLayout.setVisibility(View.VISIBLE);
                            if (objList.size() > 2) {
                                sphy_TextView.setText(GIStringUtil.nullToEmpty(objList.get(2).getStrTitle()));
                                if (0 < objList.get(2).getIntNotReadCount()) {
                                    sphyCount_TextView.setVisibility(View.VISIBLE);
                                    if (objList.get(0).getIntNotReadCount() > 99) {
                                        sphyCount_TextView.setText("99+");
                                    } else {
                                        sphyCount_TextView.setText(objList.get(2).getIntNotReadCount() + "");
                                    }
                                } else {
                                    sphyCount_TextView.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            sphy_LinearLayout.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getString(R.string.data_error);
        }
        if (GIStringUtil.isNotBlank(toastMessage)) {
            showToast(toastMessage);
        }
    }

    /**
     * 执行企达的登录
     */
    private void doQdVideoLogin() {
        String account = GISharedPreUtil.getString(activity, "QDUserName");
        String password = GISharedPreUtil.getString(activity, "QDPWD");
        String address = GISharedPreUtil.getString(activity, "QDMAINSERVER");
        int port = Integer.parseInt(GISharedPreUtil.getString(activity, "QDMAINSERVERPORT"));
        if (com.longchat.base.util.QDUtil.isNetworkAvailable(activity)
                && !GISharedPreUtil.getBoolean(activity, "QDInitSuccess", false)
                && QDClient.getInstance().getService() != null
                && !QDClient.getInstance().isOnline()) {
            QDLanderInfo.getInstance().setAccount(account);
            QDLanderInfo.getInstance().setPassword(QDUtil.encoderString(password));
            QDLanderInfo.getInstance().setAddress(address);
            QDLanderInfo.getInstance().setPort(port);
            QDLanderInfo.getInstance().save();
            QDLoginParams params = QDUtil.getLoginParams(activity);
            params.setServerIP(address);
            params.setServerPort(port);
            params.setLogin(account);
            params.setPassword(password, QDConst.PWD_TYPE_ATEN);
            //params.setPushType(QDUtil.getSystem());
            params.setLoginFlag(0);
            QDClient.getInstance().loginLC(params);
        }
    }

    @Override
    public void onLoginSuccess(final QDLoginInfo loginInfo) {//登陆成功
        GILogUtil.i("企达视频会议登录成功----->" + loginInfo);
        QDStorePath.getInstance().init(activity, loginInfo.getUserID(), loginInfo.getSSID());
        QDLanderInfo.getInstance().updateSp(loginInfo);
        QDClient.getInstance().getUserResource();
        QDClient.getInstance().setLogOut(false);
    }

    @Override
    public void onLoginFailed(int errorCode, String errorMsg) {//登陆失败
        GISharedPreUtil.setValue(activity, "QDInitSuccess", false);
        GILogUtil.i("企达视频会议登录失败----->" + QDUtil.getErrorMsg(activity, errorCode));
    }

    @Override
    public void onFetchGroup(boolean isSuccess, String errorMsg) {//获取群组结果(已弃用)
        QDClient.getInstance().fetchAllFriend();
    }

    @Override
    public void onFetchFriend(boolean isSuccess, String errorMsg) {//获取好友结果(已弃用)
        QDClient.getInstance().fetchDept();
    }

    @Override
    public void onGetToken(boolean isSuccess, String webToken, long expiresTime, String errorMsg) {//获取webtoken（可以根据自己需求是否获取）
        if (isSuccess) {
            //设置webtoken和过期时间
            QDSDKConfig.getInstance().setWebToken(webToken);
            QDSDKConfig.getInstance().setWebValidity(expiresTime);
        } else {
            showToast(errorMsg);
        }
        //获取组织架构（如果没有获取token，在获取用户资源返回结果出获取）
        QDClient.getInstance().fetchDept();
    }

    @Override
    public void onLoadingDept() {//正在获取组织架构
    }

    @Override
    public void onLoadingUserInfo() {//正在获取组织人员信息
    }

    @Override
    public void onComplete() {//登陆后所有操作全部成功
        GILogUtil.i("企达视频服务启动成功");
        GISharedPreUtil.setValue(activity, "QDInitSuccess", true);
        if (QDSDKConfig.getInstance().isHaveHook()) {
            QDUtil.setSpeakMode(activity, true);
        } else {
            QDUtil.setSpeakMode(activity, false);
        }
        //计算组织人员
        QDCompanyHelper.statUserCount();
    }

    @Override
    public void onLostConnect() {
    }

    @Override
    public void onFetchUserInfo(boolean isSuccess, String errorMsg) {

    }

    @Override
    public void removeConversation(String id) {
        QDConversation conversation = QDConversationHelper.getConversationById(id);
        if (conversation != null) {
            setQDIMData();
        }
    }

    @Override
    public void updateConversation() {
        setQDIMData();
    }

}
