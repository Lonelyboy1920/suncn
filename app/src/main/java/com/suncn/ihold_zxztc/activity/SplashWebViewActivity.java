package com.suncn.ihold_zxztc.activity;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.Utils;

/**
 * H5闪屏
 */
public class SplashWebViewActivity extends BaseActivity {
    @BindView(id = R.id.webview)
    private WebView mWebContent;
    @BindView(id = R.id.tv_gotoApp)
    private TextView gotoApp_TextView;

    private boolean isClickJumpBtn;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            // 始终允许窗口延伸到屏幕短边上的刘海区域
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }

    @Override
    public void initData() {
        super.initData();
        initWebView();
        findViewById(R.id.rl_head).setVisibility(View.GONE);
        mWebContent.loadUrl(Utils.formatFileUrl(activity, GISharedPreUtil.getString(activity, "strSplashUrl")));
        GILogUtil.e("strSplashUrl----->", mWebContent.getUrl());
        gotoApp_TextView.setVisibility(View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }

    /**
     * 设置全屏
     */
    private void hideNavigationBar() {
        // TODO Auto-generated method stub
        final View decorView = getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
    }

    private void initWebView() {
        WebSettings settings = mWebContent.getSettings();
        settings.setMediaPlaybackRequiresUserGesture(false);
        mWebContent.addJavascriptInterface(new JavaScriptObject(), "JsBridge");
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setAllowUniversalAccessFromFileURLs(true);
    }

    public class JavaScriptObject {
        /**
         * 关闭闪屏页面
         */
        @JavascriptInterface
        public void closePage() {
            if (!isClickJumpBtn) {
                if (getResources().getBoolean(R.bool.IS_OPEN_VISITOR) || GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
                    skipActivity(activity, MainActivity.class);
                } else {
                    skipActivity(activity, LoginActivity.class);
                }
            }
        }

        /**
         * 显示原生的进入系统按钮
         */
        @JavascriptInterface
        public void showNativeJumpBtn() {
            gotoApp_TextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        gotoApp_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickJumpBtn = true;
                if (getResources().getBoolean(R.bool.IS_OPEN_VISITOR) || GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
                    skipActivity(activity, MainActivity.class);
                } else {
                    skipActivity(activity, LoginActivity.class);
                }
            }
        });
    }
}
