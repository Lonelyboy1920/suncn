package com.suncn.ihold_zxztc.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.SkinAppCompatDelegateImpl;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethanhua.skeleton.SkeletonScreen;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.base.GIActivityStack;
import com.gavin.giframe.http.BaseResponse;
import com.gavin.giframe.http.ExceptionHandle;
import com.gavin.giframe.http.ResponseThrowable;
import com.gavin.giframe.ui.BindViewUtil;
import com.gavin.giframe.utils.GIExitUtil;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.utils.RxUtils;
import com.gavin.giframe.widget.CustomProgressDialog;
import com.gavin.giframe.widget.GITextView;
import com.qd.longchat.util.QDAppStatusManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.LoginActivity;
import com.suncn.ihold_zxztc.activity.SplashActivity;
import com.suncn.ihold_zxztc.rxhttp.RxDisposeManager;
import com.suncn.ihold_zxztc.utils.LanguageUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import cn.jzvd.Jzvd;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import skin.support.SkinCompatManager;
import skin.support.utils.SkinFileUtils;
import skin.support.utils.SkinPreference;

/**
 * 一个拥有DataBinding框架的基Activity
 * 这里根据项目业务可以换成你自己熟悉的BaseActivity, 但是需要继承RxAppCompatActivity,方便LifecycleProvider管理生命周期
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity, View.OnClickListener {
    public GITextView goto_Button; // 右边操作按钮
    public GITextView two_Button; // 标题栏右侧第二个按钮
    public GITextView back_Button; // 左边的返回按钮
    public TextView head_title_TextView; // 头部标题

    protected Activity activity;
    protected PushAgent mPushAgent;
    protected CustomProgressDialog prgDialog;

    private String localActivityName;
    private boolean isCanExitApp = false; // 是否可以双击返回键退出系统
    protected boolean isShowBackBtn; // 是否显示返回按钮
    protected HashMap<String, String> textParamMap; // 文本类型的参数Map
    protected String strFlowCaseDistType;//0-不显示详情办理情况，其他显示,2-分发类型，其他不显示
    public SkeletonScreen skeletonScreen;
    private AssetManager mAssetManager;

    private float fontSizeScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GIActivityStack.getInstance().addActivity(this);
        // checkAppStatus();
        activity = this;
        localActivityName = getClass().getSimpleName();
        setStatusBar();
        setRootView();
        prgDialog = new CustomProgressDialog(activity, com.gavin.giframe.R.style.ConfirmDialogStyle);
        strFlowCaseDistType = GISharedPreUtil.getString(activity, "strFlowCaseDistType");
        BindViewUtil.initBindView(this);
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();
        //初始化控件
        initView();
        //页面数据初始化方法
        initData();
        setListener();
        mAssetManager = getAssets();
        LanguageUtil.isLanguageChanged(activity, SplashActivity.class);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isCanExitApp && keyCode == KeyEvent.KEYCODE_BACK) {
            if (Jzvd.backPress()) {
                return true;
            } else {
                GIExitUtil.create().exit(activity);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        GILogUtil.log_state(this.getClass().getName(), "---------onDestroy ");
        GIActivityStack.getInstance().finishActivity(this);
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        GILogUtil.log_state(this.getClass().getName(), "---------onResume ");
        MobclickAgent.onResume(this);
        fontSizeScale = (float) GISharedPreUtil.getFloat(this, "FontScale");
        Log.i("=====================", "fontSizeScale:" + fontSizeScale);
        Resources res = super.getResources();
        Configuration config = res.getConfiguration();
        if (fontSizeScale > 0.5) {
            config.fontScale = fontSizeScale;//1 设置正常字体大小的倍数
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Override
    public void onPause() {
        super.onPause();
        GILogUtil.log_state(this.getClass().getName(), "---------onPause ");
        MobclickAgent.onPause(this);
        RxDisposeManager.get().cancel(getClass().getName()); //移除dispose
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.closePrgDialog();
        }
    }

    private void checkAppStatus() {
        if (QDAppStatusManager.getInstance().getAppStatus() == QDAppStatusManager.AppStatusConstant.APP_FORCE_KILLED) {
            //该应用已被回收，执行相关操作（下面有详解）
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void initView() {
        head_title_TextView = findViewById(R.id.tv_head_title);
        back_Button = findViewById(R.id.btn_back);
        goto_Button = findViewById(R.id.btn_goto);
        two_Button = findViewById(R.id.btn_two);
        if (two_Button != null) {
            two_Button.setOnClickListener(this);
        }
        if (goto_Button != null) {
            goto_Button.setOnClickListener(this);
        }
        if (back_Button != null) {
            back_Button.setOnClickListener(this);
        }
        if (back_Button != null && isShowBackBtn) {
            back_Button.setVisibility(View.VISIBLE);
            back_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GIUtil.closeSoftInput(activity);
                    activity.finish();
                }
            });
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void setListener() {
    }

    @Override
    public void onClick(View v) {
    }


    protected void setStatusBar() {
        setStatusBar(true, false);
    }

    /**
     * useStatusBarDarkColor  是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
     * useThemeStatusBarColor 是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
     */
    protected void setStatusBar(boolean useStatusBarDarkColor, boolean useThemeStatusBarColor) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (useThemeStatusBarColor) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        } else {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarDarkColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    public requestCallBack requestCallBack;

    public interface requestCallBack<T> {
        /**
         * 数据请求成功
         *
         * @param data 请求到的数据
         */
        void onSuccess(T data, int sign);
    }

    /**
     * 一般请求，返回数据带有body
     */
    public <T> void doRequestNormal(Observable<BaseResponse<T>> observable, final int sign) {
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.schedulersTransformer()) //线程调度
//                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                // .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Consumer<BaseResponse<T>>() {
                    @Override
                    public void accept(BaseResponse<T> tBaseResponse) {
                        String msg = tBaseResponse.getMsg();
                        if (tBaseResponse.getCode() == 200) {
                            requestCallBack.onSuccess(tBaseResponse.getData(), sign);
                            GISharedPreUtil.setValue(activity, "strSid", tBaseResponse.getStrSid() + "");
                        } else {
                            if (GIStringUtil.isBlank(msg)) {
                                msg = getString(R.string.data_error) + "（code=" + tBaseResponse.getCode() + "）";
                            }
                            apiErrorDo(tBaseResponse.getCode());
                        }
                        showToast(msg);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        ResponseThrowable responseThrowable = ExceptionHandle.handleException(throwable);
                        GILogUtil.e("doRequestNormal/Error----->" + responseThrowable.code + "/" + responseThrowable.message);
                        showToast(responseThrowable.message);
                        apiErrorDo(0);
                        requestCallBack.onSuccess(null, -100); // 请求出现异常时回调给主界面刷新UI
                    }
                });
        RxDisposeManager.get().add(getClass().getName(), disposable); //添加当前类名(lin.frameapp.xxx)的dispose
    }

    /**
     * 接口请求错误后的逻辑操作
     */
    private void apiErrorDo(int code) {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.closePrgDialog();
        }
        if (skeletonScreen != null) {
            skeletonScreen.hide();
        }
        if (code == 100) {
            showLoginDialog();
            return;
        }
        if (localActivityName.equals("SplashActivity")) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isNeedExit", true);
            showActivity(activity, LoginActivity.class, bundle);
        }
    }

    /**
     * 删除对话框
     */
    private void showLoginDialog() {
        Utils.sp_loginOut(activity); // 如果注销登录，需要清除登录数据
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.string_tips)).content(getString(R.string.string_loginout_tip)).btnText("", getString(R.string.string_determine)).
        showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        //dialog.dismiss();
                        dialog.superDismiss();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isNeedExit", true);
                        showActivity(activity, LoginActivity.class, bundle);

                    }
                }
        );
    }

    /**
     * 弹出Toast
     */
    public void showToast(String msg) {
        if (GIStringUtil.isNotBlank(msg))
            GIToastUtil.showMessage(activity, msg);
    }

    /**
     * 设置标题
     */
    public void setHeadTitle(String title) {
        if (head_title_TextView != null)
            head_title_TextView.setText(title);
    }

    /**
     * 跳转Activity并关闭当前Activity
     */
    public void skipActivity(Activity aty, Class<?> cls) {
        showActivity(aty, cls);
        aty.finish();
    }

    /**
     * 跳转Activity（不关闭当前Activity）
     */
    public void showActivity(Activity aty, Class<?> cls) {
        showActivity(aty, cls, -1);
    }

    public void showActivity(Activity aty, Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(aty, cls);
        if (requestCode > -1)
            aty.startActivityForResult(intent, requestCode);
        else
            aty.startActivity(intent);
    }

    /**
     * 跳转Activity并关闭当前Activity
     */
    public void skipActivity(Activity aty, Class<?> cls, Bundle extras) {
        showActivity(aty, cls, extras);
        aty.finish();
    }

    /**
     * 跳转Activity（不关闭当前Activity）
     */
    public void showActivity(Activity aty, Class<?> cls, Bundle extras) {
        showActivity(aty, cls, extras, -1);
    }

    public void showActivity(Activity aty, Class<?> cls, Bundle extras, int requestCode) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(aty, cls);
        if (requestCode > -1)
            aty.startActivityForResult(intent, requestCode);
        else
            aty.startActivity(intent);
    }

    public void setCanExitApp(boolean canExitApp) {
        isCanExitApp = canExitApp;
    }

    /**
     * 获取头像并显示
     */
    public void showHeadPhoto(String url, ImageView imageView) {
        if (GIStringUtil.isNotBlank(url)) {
            url = Utils.formatFileUrl(activity, url);
            GIImageUtil.loadImg(activity, imageView, url, 1);
        }
    }

    /**
     * 关闭加载对话框
     */
    public void closeLoadDialog() {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.closePrgDialog();
        }
    }

    /**
     * 显示加载对话框
     */
    public void showLoadDialog() {
        if (prgDialog != null) {
            prgDialog.showLoadDialog();
        }
    }


    /**
     * 恢复默认主题，使用应用自带资源.
     */
    public void restoreDefaultTheme() {
        SkinCompatManager.getInstance().restoreDefaultTheme();
    }

    /**
     * 切换主题
     *
     * @param skinName 主题名称
     */
    public void changeSkinTheme(String skinName) {
        // SkinCompatUserThemeManager.get().addColorState(R.color.text_background, "#ffff0000");
        //SkinCompatUserThemeManager.get().apply();
        //SkinCompatManager.getInstance().loadSkin("night", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
        String skinDir = SkinFileUtils.getSkinDir(this); //皮肤缓存路径
        boolean fileExists = SkinFileUtils.isFileExists(skinDir + "/" + skinName);
        try {
            String[] skins = mAssetManager.list("skins");
            if (null != skins && Arrays.asList(skins).contains(skinName)) { //判断asset中是否包含资源包
                SkinCompatManager.getInstance().loadSkin(skinName, new SkinCompatManager.SkinLoaderListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess() {
                        showToast("设置成功");
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        showToast(errMsg);
                    }
                }, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                int skinStrategy = SkinPreference.getInstance().getSkinStrategy();
                Log.i("===============", "skinName:" + skinName
                        + "\nskinStrategy:" + skinStrategy
                        + "\nskinDir:" + skinDir
                        + "\nfileExists:" + fileExists);
            } else if (fileExists) { //判断SDCard中(Android/data/包名/cache/skins)是否包含资源包
                SkinCompatManager.getInstance().loadSkin(skinName, new SkinCompatManager.SkinLoaderListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess() {
                        showToast("设置成功");
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        showToast(errMsg);
                    }
                }, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
            } else {
                showToast("非常抱歉,没有找到资源文件");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return SkinAppCompatDelegateImpl.get(this, this);
    }

}
