package com.suncn.ihold_zxztc.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDLoginCallBack;
import com.longchat.base.config.QDSDKConfig;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDCompanyHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.listener.QDLoginCallBackManager;
import com.longchat.base.model.QDLoginInfo;
import com.longchat.base.model.QDLoginParams;
import com.longchat.base.util.QDConst;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.ApplicationFragment;
import com.suncn.ihold_zxztc.activity.application.meeting.QRCodeCheckActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.QRCodeSignInActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.circle.Circle_AddDynamicActivity;
import com.suncn.ihold_zxztc.activity.circle.Circle_MainFragment;
import com.suncn.ihold_zxztc.activity.duty.MemberDuty_MainFragment;
import com.suncn.ihold_zxztc.activity.hot.HotMainFragment;
import com.suncn.ihold_zxztc.activity.hot.RobotActivity;
import com.suncn.ihold_zxztc.activity.message.Contact_MainFragment;
import com.suncn.ihold_zxztc.activity.message.Message_MainFragment;
import com.suncn.ihold_zxztc.activity.message.Message_RemindActivity;
import com.suncn.ihold_zxztc.bean.EventBusCarrier;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.view.DragFloatActionButton;
import com.suncn.ihold_zxztc.view.NormalItemView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
import skin.support.content.res.SkinCompatResources;

/**
 * 主界面，底部菜单
 */
public class MainActivity extends BaseActivity implements QDLoginCallBack {
    @BindView(id = R.id.pager_bottom_tab)
    private PageNavigationView Tab_PageBottomTabLayout;
    @BindView(id = R.id.anan_btn, click = true)
    private DragFloatActionButton btnAnAn;
    private List<Fragment> mFragments;
    private int currentIndex = 2;
    private String strNewPassword = "";
    private String strChatLoginName = "";
    private int intUserRole;
    private String mStrStartingMeeting;
    public Fragment currentFragment;
    private float fontSizeScale;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//这行不可以删除，否则Fragment中onActivityResult()无法回调
        if (resultCode != RESULT_OK || resultCode != 0) return;
    }

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppConfigUtil.isUseQDIM(activity)) {
            doQdVideoLogin();
        }
        JzvdStd.goOnPlayOnResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); //解除注册
    }

    @Override
    public void onPause() {
        super.onPause();
        JzvdStd.goOnPlayOnPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void initData() {
        setCanExitApp(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JzvdStd.setVideoImageDisplayType(JzvdStd.VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER);//设置容器内播放器高。还原成默认，否则视频会有拉伸
            }
        }, 1000);
        fontSizeScale = (float) GISharedPreUtil.getFloat(this, "FontScale");
        EventBus.getDefault().register(this);  //注册EventBus
        intUserRole = GISharedPreUtil.getInt(activity, "intUserRole");
        QDLoginCallBackManager.getInstance().setLoginCallBack(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (0 == intUserRole || 1 == intUserRole) {
                currentIndex = bundle.getInt("currentIndex", 2);
            } else {
                currentIndex = bundle.getInt("currentIndex", 1);
            }

            if (ProjectNameUtil.isLWQZX(activity)) {
                mStrStartingMeeting = bundle.getString("strStartingMeeting");
                if (GIStringUtil.isNotBlank(mStrStartingMeeting)) {
                    showConfirmDialog();
                }
            }
        } else {
            if (0 == intUserRole || 1 == intUserRole) {
                currentIndex = 2;
            } else {
                currentIndex = 1;
            }
        }
        if (GISharedPreUtil.getBoolean(activity, "isHasLogin", false)
                && "1".equals(GISharedPreUtil.getString(activity, "strShowAnAn"))
        ) {
            btnAnAn.setVisibility(View.VISIBLE);
        } else {
            btnAnAn.setVisibility(View.GONE);
        }
        btnAnAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //从Service中跳到Activity必须设置，不然会闪退
                intent.setClass(getBaseContext(), RobotActivity.class);
                startActivity(intent);
            }
        });
        btnAnAn.setNoticeClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //从Service中跳到Activity必须设置，不然会闪退
                intent.putExtra("strAnAnIds", GISharedPreUtil.getString(activity, "strAnAnIds"));
                intent.putExtra("headTitle", getString(R.string.string_message_notice));
                intent.setClass(getBaseContext(), Message_RemindActivity.class);
                startActivity(intent);
                btnAnAn.getLlMessage().setVisibility(View.GONE);
            }
        });
        //初始化Fragment
        initFragment();

        //初始化底部Button
        initBottomTab();
        if (AppConfigUtil.isUseQDIM(activity) && QDClient.getInstance().isOnline()) {
            QDClient.getInstance().fetchOfflineMsg();
        }
        //要确保API Level 大于等于 25才可以创建动态shortcut，否则会报异常。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            initDynamicShortcuts();
        }

    }


    /**
     * 切换角色后刷新菜单数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier eventBusCarrier) {
        this.intUserRole = (int) eventBusCarrier.getObject();
        currentIndex = 2;
        if (0 == intUserRole) { // 委员
            mFragments.set(0, new Message_MainFragment());
            mFragments.set(1, new Circle_MainFragment());
            mFragments.set(2, new HotMainFragment());
            mFragments.set(3, new MemberDuty_MainFragment());
            mFragments.set(4, new ApplicationFragment());
//            mFragments.get(2).onResume();

        } else if (1 == intUserRole) { // 机关工作人员
            mFragments.set(0, new Message_MainFragment());
            mFragments.set(1, new Circle_MainFragment());
            mFragments.set(2, new HotMainFragment());
            Contact_MainFragment contactMainFragment = new Contact_MainFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isCommissioner", true);
            bundle.putBoolean("isShowHead", true);
            bundle.putString("showTitle", getString(R.string.string_duty));
            contactMainFragment.setArguments(bundle);
            mFragments.set(3, contactMainFragment);
            mFragments.set(4, new ApplicationFragment());
//            mFragments.get(2).onResume();
        }
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        if (ProjectNameUtil.isGZSZX(activity)) {
            mFragments.add(new HotMainFragment());
        } else {
            mFragments.add(new Message_MainFragment());
        }
        // intGroup----->是否是团体用户1是0否
        if (((0 == intUserRole && 0 == GISharedPreUtil.getInt(activity, "intGroup")) || 1 == intUserRole)
                && !ProjectNameUtil.isLWQZX(activity) && !ProjectNameUtil.isGZSZX(activity)) {
            mFragments.add(new Circle_MainFragment());
        }
        if (ProjectNameUtil.isGZSZX(activity)) {
            mFragments.add(new Message_MainFragment());
        } else {
            mFragments.add(new HotMainFragment());
        }
        if (GISharedPreUtil.getString(activity, "strShowLzIconState").equals("1")) {
            if (0 == intUserRole && 0 == GISharedPreUtil.getInt(activity, "intGroup")) { // 我的履职
                mFragments.add(new MemberDuty_MainFragment());
            } else if (1 == intUserRole) { // 履职管理
                Contact_MainFragment contactMainFragment = new Contact_MainFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isCommissioner", true);
                bundle.putBoolean("isShowHead", true);
                bundle.putString("showTitle", getString(R.string.string_duty));
                contactMainFragment.setArguments(bundle);
                mFragments.add(contactMainFragment);
            }
        }
        mFragments.add(new ApplicationFragment());


    }

    //创建一个Item
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        NormalItemView normalItemView = new NormalItemView(this);
        normalItemView.initialize(drawable, checkedDrawable, text);
        normalItemView.setTextDefaultColor(getResources().getColor(R.color.font_content));
//        normalItemView.setTextCheckedColor(getResources().getColor(R.color.view_head_bg));
        normalItemView.setTextCheckedColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        return normalItemView;
    }

    private void initBottomTab() {
        final NavigationController navigationController;
        if ((0 == intUserRole && 0 == GISharedPreUtil.getInt(activity, "intGroup")) || 1 == intUserRole) {
            if (GISharedPreUtil.getString(activity, "strShowLzIconState").equals("0")) {
                navigationController = Tab_PageBottomTabLayout.custom()
                        .addItem(newItem(R.mipmap.bottom_message, R.mipmap.bottom_message_hover, getString(R.string.string_message)))
                        .addItem(newItem(R.mipmap.bottom_circle, R.mipmap.bottom_circle_hover, getString(R.string.string_circle)))
                        .addItem(newItem(R.mipmap.bottom_hot, R.mipmap.bottom_hot_hover, getString(R.string.string_hot)))
                        .addItem(newItem(R.mipmap.bottom_func, R.mipmap.bottom_func_hover, getString(R.string.string_application)))
                        .build();
            } else {
                navigationController = Tab_PageBottomTabLayout.custom()
                        .addItem(newItem(R.mipmap.bottom_message, R.mipmap.bottom_message_hover, getString(R.string.string_message)))
                        .addItem(newItem(R.mipmap.bottom_circle, R.mipmap.bottom_circle_hover, getString(R.string.string_circle)))
                        .addItem(newItem(R.mipmap.bottom_hot, R.mipmap.bottom_hot_hover, getString(R.string.string_hot)))
                        .addItem(newItem(R.mipmap.bottom_duty, R.mipmap.bottom_duty_hover, getString(R.string.string_duty)))
                        .addItem(newItem(R.mipmap.bottom_func, R.mipmap.bottom_func_hover, getString(R.string.string_application)))
                        .build();
            }

        } else {
            navigationController = Tab_PageBottomTabLayout.custom()
                    .addItem(newItem(R.mipmap.bottom_message, R.mipmap.bottom_message_hover, getString(R.string.string_message)))
                    .addItem(newItem(R.mipmap.bottom_hot, R.mipmap.bottom_hot_hover, getString(R.string.string_hot)))
                    .addItem(newItem(R.mipmap.bottom_func, R.mipmap.bottom_func_hover, getString(R.string.string_application)))
                    .build();
        }
        // navigationController.setHasMessage(0, true);//指定位置是否显示角标数字
        // navigationController.setMessageNumber(0, 10);//指定位置显示角标数量
        //底部按钮的点击事件监听
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                //设置游客模式可以跳到热点界面，如果点击其他则跳转到登陆页
                if (GISharedPreUtil.getBoolean(activity, "isHasLogin") || (index == 2 && !ProjectNameUtil.isLWQZX(activity) && !ProjectNameUtil.isGZSZX(activity)) || (index == 1 && (ProjectNameUtil.isLWQZX(activity)) || (index == 0 && ProjectNameUtil.isGZSZX(activity)))) {
                    switchFragment(mFragments.get(index));
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("currentIndex", index);
                    showActivity(activity, LoginActivity.class, bundle);
                }
            }

            @Override
            public void onRepeat(int index) {
            }
        });
        navigationController.setSelect(currentIndex);
    }

    /**
     * 切换Fragment（正确的做法）
     */
    private void switchFragment(Fragment targetFragment) {
        GILogUtil.d("switchFragment----->" + targetFragment.getClass().getName());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            if (currentFragment == null) {
                transaction.add(R.id.frameLayout, targetFragment);
            } else {
                transaction.hide(currentFragment).add(R.id.frameLayout, targetFragment);
                currentFragment.onPause(); // 解决切换Fragment时，HotMainFragment中的视频仍在播放的问题
            }
            transaction.commitAllowingStateLoss();
            currentFragment = targetFragment;
        } else {
            transaction.hide(currentFragment).show(targetFragment);
            currentFragment.onPause(); // 解决切换Fragment时，HotMainFragment中的视频仍在播放的问题
            transaction.commitAllowingStateLoss();
            currentFragment = targetFragment;
            targetFragment.onResume(); // 解决切换Fragment时，onResume不被调用的问题
        }
    }

    /**
     * 执行企达视频聊天的登录
     */
    private void doQdVideoLogin() {
        if (GISharedPreUtil.getBoolean(activity, "isHasLogin", false)
                && com.longchat.base.util.QDUtil.isNetworkAvailable(activity)
                && QDClient.getInstance().getService() != null) {
            String account = GISharedPreUtil.getString(activity, "QDUserName");
            String password = GISharedPreUtil.getString(activity, "QDPWD");
            String address = GISharedPreUtil.getString(activity, "QDMAINSERVER");

            int port = Integer.parseInt(GISharedPreUtil.getString(activity, "QDMAINSERVERPORT"));
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
        GILogUtil.i("企达登录成功----->" + loginInfo);
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
        //QDClient.getInstance().fetchDept();
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
        List<QDUser> users = QDUserHelper.getAllUser();
        //计算组织人员
        QDCompanyHelper.statUserCount();
        EventBusCarrier eventBusCarrier = new EventBusCarrier();
        eventBusCarrier.setEventType(10);
        EventBus.getDefault().post(eventBusCarrier);
    }

    @Override
    public void onLostConnect() {
    }

    @Override
    public void onFetchUserInfo(boolean isSuccess, String errorMsg) {
        QDClient.getInstance().fetchDept();
    }

    /**
     * 对话框
     */
    private void showConfirmDialog() {
        String title;
        title = "委员您好,你有关于" + mStrStartingMeeting + "的会议需要签到。";
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.string_tips)).content(title).btnText(getString(R.string.string_cancle), getString(R.string.string_confirm)).showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        showActivity(MainActivity.this, QRCodeSignInActivity.class);
                        dialog.dismiss();
                    }
                }
        );
    }

    /**
     * 为App创建动态Shortcuts
     */
    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void initDynamicShortcuts() {
        Bundle bundle = new Bundle();
        ShortcutInfo scAddDynamic = null, scScan = null, scQrCodeSinIn = null, scSearch = null;

        //①、创建动态快捷方式的第一步，创建ShortcutManager
        ShortcutManager scManager = getSystemService(ShortcutManager.class);
        //②、构建动态快捷方式的详细信息
//        ShortcutInfo scInfoOne = new ShortcutInfo.Builder(this, "dynamic_one")
//                .setShortLabel("跳系统浏览器")
//                .setLongLabel("to open Dynamic Web Site")
//                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
//                .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com")))
//                .build();
        //发布动态
        bundle.putBoolean("isShortCut", true);
        scAddDynamic = new ShortcutInfo.Builder(this, "dynamic_two")
                .setShortLabel(getString(R.string.string_send_dynamic))
                .setLongLabel("to open dynamic one activity")
                .setIcon(Icon.createWithResource(this, R.mipmap.icon_shortcut_public))
                .setIntents(
                        // this dynamic shortcut set up a back stack using Intents, when pressing back, will go to MainActivity
                        // the last Intent is what the shortcut really opened
                        new Intent[]{
                                new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("isShortcut", true),
                                new Intent(Circle_AddDynamicActivity.ACTION_OPEN_CIRCLE_ADD)
                                // intent's action must be set
                        })
                .build();
        //扫一扫
        scScan = new ShortcutInfo.Builder(this, "dynamic_three")
                .setShortLabel(getString(R.string.string_scan))
                .setLongLabel("to open dynamic one activity")
                .setIcon(Icon.createWithResource(this, R.mipmap.icon_shortcut_scan))
                .setIntents(
                        // this dynamic shortcut set up a back stack using Intents, when pressing back, will go to MainActivity
                        // the last Intent is what the shortcut really opened

                        new Intent[]{
                                new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                                new Intent(QRCodeCheckActivity.ACTION_OPEN_SCAN_QR_CODE)
                                // intent's action must be set
                        })
                .build();

        //电子履职卡(委员)
        bundle.putString("strType", "-1");
        scQrCodeSinIn = new ShortcutInfo.Builder(this, "dynamic_four")
                .setShortLabel(getString(R.string.string_duty_idcard))
                .setLongLabel("to open dynamic one activity")
                .setIcon(Icon.createWithResource(this, R.mipmap.icon_shortcut_duty))
                .setIntents(
                        // this dynamic shortcut set up a back stack using Intents, when pressing back, will go to MainActivity
                        // the last Intent is what the shortcut really opened
                        new Intent[]{
                                new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                                new Intent(QRCodeSignInActivity.ACTION_OPEN_QR_CODE_SINE).putExtras(bundle)
                                // intent's action must be set
                        })
                .build();

        //TODO  暂时不要搜索功能
        //搜索
//        bundle.putInt("searchType", 0);
//        scSearch = new ShortcutInfo.Builder(this, "dynamic_five")
//                .setShortLabel(getString(R.string.string_search))
//                .setLongLabel("to open dynamic one activity")
//                .setIcon(Icon.createWithResource(this, R.mipmap.icon_shortcut_search))
//                .setIntents(
//                        // this dynamic shortcut set up a back stack using Intents, when pressing back, will go to MainActivity
//                        // the last Intent is what the shortcut really opened
//                        new Intent[]{
//                                new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
//                                new Intent(OverSearchListActivity.ACTION_OPEN_SEARCH).putExtras(bundle)
//                                // intent's action must be set
//                        })
//                .build();
        //③、为ShortcutManager设置动态快捷方式集合
//        scManager.setDynamicShortcuts(Arrays.asList(scInfoOne, scAddDynamic, scScan, scQrCodeSinIn, scSearch));
        if (GISharedPreUtil.getBoolean(activity, "isHasLogin", false)) {
            if (0 == GISharedPreUtil.getInt(activity, "intUserRole")) {
                //TODo  暂时不要搜索
//                scManager.setDynamicShortcuts(Arrays.asList(scAddDynamic, scQrCodeSinIn, scSearch));
                scManager.setDynamicShortcuts(Arrays.asList(scAddDynamic, scQrCodeSinIn));
//            scManager.removeDynamicShortcuts(Collections.singletonList("dynamic_four"));
            } else if (2 == GISharedPreUtil.getInt(activity, "intUserRole")
                    || 3 == GISharedPreUtil.getInt(activity, "intUserRole")) { //承办单位和承办系统账号
//                scManager.setDynamicShortcuts(Arrays.asList(scSearch));
            } else {
//            scManager.addDynamicShortcuts(Arrays.asList(scAddDynamic, scScan, scQrCodeSinIn, scSearch));
                //TODo  暂时不要搜索
//                scManager.setDynamicShortcuts(Arrays.asList(scAddDynamic, scScan, scSearch));
                scManager.setDynamicShortcuts(Arrays.asList(scAddDynamic, scScan));
            }
        } else {
//            scManager.setDynamicShortcuts(Arrays.asList(scSearch));
        }

    }
}
