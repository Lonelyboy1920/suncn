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
 * ????????????DataBinding????????????Activity
 * ??????????????????????????????????????????????????????BaseActivity, ??????????????????RxAppCompatActivity,??????LifecycleProvider??????????????????
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity, View.OnClickListener {
    public GITextView goto_Button; // ??????????????????
    public GITextView two_Button; // ??????????????????????????????
    public GITextView back_Button; // ?????????????????????
    public TextView head_title_TextView; // ????????????

    protected Activity activity;
    protected PushAgent mPushAgent;
    protected CustomProgressDialog prgDialog;

    private String localActivityName;
    private boolean isCanExitApp = false; // ???????????????????????????????????????
    protected boolean isShowBackBtn; // ????????????????????????
    protected HashMap<String, String> textParamMap; // ?????????????????????Map
    protected String strFlowCaseDistType;//0-??????????????????????????????????????????,2-??????????????????????????????
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
        //???????????????
        initView();
        //???????????????????????????
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
            config.fontScale = fontSizeScale;//1 ?????????????????????????????????
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Override
    public void onPause() {
        super.onPause();
        GILogUtil.log_state(this.getClass().getName(), "---------onPause ");
        MobclickAgent.onPause(this);
        RxDisposeManager.get().cancel(getClass().getName()); //??????dispose
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.closePrgDialog();
        }
    }

    private void checkAppStatus() {
        if (QDAppStatusManager.getInstance().getAppStatus() == QDAppStatusManager.AppStatusConstant.APP_FORCE_KILLED) {
            //???????????????????????????????????????????????????????????????
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
     * useStatusBarDarkColor  ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????android6.0??????????????????
     * useThemeStatusBarColor ?????????????????????????????????????????????android5.0???????????????????????????????????????????????????????????????????????????
     */
    protected void setStatusBar(boolean useStatusBarDarkColor, boolean useThemeStatusBarColor) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (useThemeStatusBarColor) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        } else {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarDarkColor) {//android6.0?????????????????????????????????????????????????????????
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    public requestCallBack requestCallBack;

    public interface requestCallBack<T> {
        /**
         * ??????????????????
         *
         * @param data ??????????????????
         */
        void onSuccess(T data, int sign);
    }

    /**
     * ?????????????????????????????????body
     */
    public <T> void doRequestNormal(Observable<BaseResponse<T>> observable, final int sign) {
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.schedulersTransformer()) //????????????
//                .compose(RxUtils.exceptionTransformer()) // ???????????????????????????, ???????????????????????????ExceptionHandle
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
                                msg = getString(R.string.data_error) + "???code=" + tBaseResponse.getCode() + "???";
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
                        requestCallBack.onSuccess(null, -100); // ?????????????????????????????????????????????UI
                    }
                });
        RxDisposeManager.get().add(getClass().getName(), disposable); //??????????????????(lin.frameapp.xxx)???dispose
    }

    /**
     * ????????????????????????????????????
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
     * ???????????????
     */
    private void showLoginDialog() {
        Utils.sp_loginOut(activity); // ?????????????????????????????????????????????
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
     * ??????Toast
     */
    public void showToast(String msg) {
        if (GIStringUtil.isNotBlank(msg))
            GIToastUtil.showMessage(activity, msg);
    }

    /**
     * ????????????
     */
    public void setHeadTitle(String title) {
        if (head_title_TextView != null)
            head_title_TextView.setText(title);
    }

    /**
     * ??????Activity???????????????Activity
     */
    public void skipActivity(Activity aty, Class<?> cls) {
        showActivity(aty, cls);
        aty.finish();
    }

    /**
     * ??????Activity??????????????????Activity???
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
     * ??????Activity???????????????Activity
     */
    public void skipActivity(Activity aty, Class<?> cls, Bundle extras) {
        showActivity(aty, cls, extras);
        aty.finish();
    }

    /**
     * ??????Activity??????????????????Activity???
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
     * ?????????????????????
     */
    public void showHeadPhoto(String url, ImageView imageView) {
        if (GIStringUtil.isNotBlank(url)) {
            url = Utils.formatFileUrl(activity, url);
            GIImageUtil.loadImg(activity, imageView, url, 1);
        }
    }

    /**
     * ?????????????????????
     */
    public void closeLoadDialog() {
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.closePrgDialog();
        }
    }

    /**
     * ?????????????????????
     */
    public void showLoadDialog() {
        if (prgDialog != null) {
            prgDialog.showLoadDialog();
        }
    }


    /**
     * ?????????????????????????????????????????????.
     */
    public void restoreDefaultTheme() {
        SkinCompatManager.getInstance().restoreDefaultTheme();
    }

    /**
     * ????????????
     *
     * @param skinName ????????????
     */
    public void changeSkinTheme(String skinName) {
        // SkinCompatUserThemeManager.get().addColorState(R.color.text_background, "#ffff0000");
        //SkinCompatUserThemeManager.get().apply();
        //SkinCompatManager.getInstance().loadSkin("night", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
        String skinDir = SkinFileUtils.getSkinDir(this); //??????????????????
        boolean fileExists = SkinFileUtils.isFileExists(skinDir + "/" + skinName);
        try {
            String[] skins = mAssetManager.list("skins");
            if (null != skins && Arrays.asList(skins).contains(skinName)) { //??????asset????????????????????????
                SkinCompatManager.getInstance().loadSkin(skinName, new SkinCompatManager.SkinLoaderListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess() {
                        showToast("????????????");
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
            } else if (fileExists) { //??????SDCard???(Android/data/??????/cache/skins)?????????????????????
                SkinCompatManager.getInstance().loadSkin(skinName, new SkinCompatManager.SkinLoaderListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess() {
                        showToast("????????????");
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        showToast(errMsg);
                    }
                }, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
            } else {
                showToast("????????????,????????????????????????");
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
