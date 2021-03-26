package com.suncn.ihold_zxztc.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethanhua.skeleton.SkeletonScreen;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.http.BaseResponse;
import com.gavin.giframe.http.ExceptionHandle;
import com.gavin.giframe.http.ResponseThrowable;
import com.gavin.giframe.ui.BindViewUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.utils.RxUtils;
import com.gavin.giframe.widget.CustomProgressDialog;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.LoginActivity;
import com.suncn.ihold_zxztc.activity.SplashActivity;
import com.suncn.ihold_zxztc.rxhttp.RxDisposeManager;
import com.suncn.ihold_zxztc.utils.LanguageUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by goldze on 2017/6/15.
 */
public abstract class BaseFragment extends Fragment implements IBaseFragment, View.OnClickListener {
    private View view = null;
    public GITextView goto_Button; // 右边操作按钮
    public TextView head_title_TextView; // 头部标题
    public GITextView back_Button;

    private ImageView headHome_ImageView;
    protected boolean isShowBackBtn; // 是否显示返回按钮
    protected Activity activity;
    protected Fragment fragment;
    protected HashMap<String, String> textParamMap; // 文本类型的参数Map
    protected CustomProgressDialog prgDialog;
    public SkeletonScreen skeletonScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        fragment = this;
        GILogUtil.log_state(this.getClass().getName(), "---------onCreate ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflaterView(inflater, container, savedInstanceState);
        BindViewUtil.initBindView(this, view);
        prgDialog = new CustomProgressDialog(activity, R.style.ConfirmDialogStyle);
        GILogUtil.log_state(this.getClass().getName(), "---------onCreateView ");
        back_Button = view.findViewById(R.id.btn_back);
        goto_Button = view.findViewById(R.id.btn_goto);
        if (goto_Button != null) {
            goto_Button.setOnClickListener(this);
        }
        headHome_ImageView = view.findViewById(R.id.iv_home_head);
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
        head_title_TextView = view.findViewById(R.id.tv_head_title);
        return view;
    }


    /**
     * useThemeStatusBarColor 是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
     * useStatusBarDarkColor  是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
     */
    protected void setStatusBar(boolean useStatusBarDarkColor) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        if (useThemeStatusBarColor) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
//        } else {
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarDarkColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*//移除dispose
        RxDisposeManager.get().cancel(getClass().getName());*/
        //移除dispose
        RxDisposeManager.get().cancelAll();
        RxDisposeManager.get().cancel(getClass().getName());
        if (prgDialog != null && prgDialog.isShowing()) {
            prgDialog.closePrgDialog();
        }
        GILogUtil.log_state(this.getClass().getName(), "---------onDestroyView ");
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
                .subscribe(new Consumer<BaseResponse<T>>() {
                    @Override
                    public void accept(BaseResponse<T> tBaseResponse) {
                        String msg = tBaseResponse.getMsg();
                        if (tBaseResponse.getCode() == 200) {
                            requestCallBack.onSuccess(tBaseResponse.getData(), sign);
//                            GISharedPreUtil.setValue(activity, "strSid", tBaseResponse.getStrSid() + "");
                        } else {
                            if (GIStringUtil.isBlank(msg)) {
                                msg = getString(R.string.data_error) + "（code=" + tBaseResponse.getCode() + "）";
                            }
                            if (tBaseResponse.getCode()==100){
                                showLoginDialog();
                            }
                            prgDialog.closePrgDialog();
                            if (skeletonScreen != null) {
                                skeletonScreen.hide();
                            }
                        }
                        showToast(msg);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        if (throwable.getClass() == IllegalStateException.class) {
                            GILogUtil.e("doRequestNormal/Error----->" + throwable.getMessage());
                            LanguageUtil.restartApp(activity, SplashActivity.class);
                        }
                        ResponseThrowable responseThrowable = ExceptionHandle.handleException(throwable);
                        GILogUtil.e("doRequestNormal/Error----->" + responseThrowable.code + "/" + responseThrowable.message);
                        showToast(responseThrowable.message);
                        if (prgDialog != null && prgDialog.isShowing()) {
                            prgDialog.closePrgDialog();
                        }
                        if (skeletonScreen != null) {
                            skeletonScreen.hide();
                        }
                        requestCallBack.onSuccess(null, -100); // 请求出现异常时回调给主界面刷新UI
                    }
                });
        RxDisposeManager.get().add(getClass().getName(), disposable); //添加当前类名(lin.frameapp.xxx)的dispose
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
                        showActivity(fragment, LoginActivity.class, bundle);

                    }
                }
        );
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //页面数据初始化方法
        initData();
        setListener();
        GILogUtil.log_state(this.getClass().getName(), "---------onViewCreated ");
    }

    @Override
    public void setListener() {
    }

    /**
     * 弹出Toast
     */
    public void showToast(String msg) {
        if (GIStringUtil.isNotBlank(msg))
            GIToastUtil.showMessage(activity, msg);
    }

    /**
     * 跳转Activity并关闭当前Activity
     */
    public void skipActivity(Fragment aty, Class<?> cls) {
        showActivity(aty, cls);
        activity.finish();
    }

    /**
     * 跳转Activity（不关闭当前Activity）
     */
    public void showActivity(Fragment fragment, Class<?> cls) {
        showActivity(fragment, cls, -1);
    }

    public void showActivity(Fragment aty, Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        if (requestCode > -1)
            aty.startActivityForResult(intent, requestCode);
        else
            aty.startActivity(intent);
    }

    /**
     * 跳转Activity并关闭当前Activity
     */
    public void skipActivity(Fragment aty, Class<?> cls, Bundle extras) {
        showActivity(aty, cls, extras);
        activity.finish();
    }

    /**
     * 跳转Activity（不关闭当前Activity）
     */
    public void showActivity(Fragment aty, Class<?> cls, Bundle extras) {
        showActivity(aty, cls, extras, -1);
    }

    public void showActivity(Fragment aty, Class<?> cls, Bundle extras, int requestCode) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(activity, cls);
        if (requestCode > -1)
            aty.startActivityForResult(intent, requestCode);
        else
            aty.startActivity(intent);
    }

    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle);

    @Override
    public void initData() {
        /*back_Button = (Button) view.findViewById(R.id.btn_back);
        goto_Button = (Button) view.findViewById(R.id.btn_goto);
        headHome_ImageView = (ImageView) view.findViewById(R.id.iv_home_head);
        if (back_Button != null)
            back_Button.setTypeface(FontUtil.setFont(getActivity(), "1"));
        if (back_Button != null && isShowBackBtn) {
            back_Button.setVisibility(View.VISIBLE);
            back_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GIUtil.closeSoftInput(getActivity());
                    getActivity().finish();
                }
            });
        }
        head_title_TextView = (TextView) view.findViewById(R.id.tv_head_title);*/
    }

    /**
     * 设置标题
     */
    public void setHeadTitle(String title) {
        if (head_title_TextView != null)
            head_title_TextView.setText(title);
    }

    public boolean isBackPressed() {
        return false;
    }
    private float fontSizeScale;
    @Override
    public void onResume() {
        GILogUtil.log_state(this.getClass().getName(), "---------onResume ");
        super.onResume();
        fontSizeScale = (float) GISharedPreUtil.getFloat(getContext(), "FontScale");
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
        GILogUtil.log_state(this.getClass().getName(), "---------onPause ");
        super.onPause();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onStop() {
        GILogUtil.log_state(this.getClass().getName(), "---------onStop ");
        super.onStop();
    }
}
