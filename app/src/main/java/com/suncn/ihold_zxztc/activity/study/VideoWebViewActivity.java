package com.suncn.ihold_zxztc.activity.study;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.Utils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

/**
 * 知情明政视频详情
 */
public class VideoWebViewActivity extends BaseActivity {
    @BindView(id = R.id.webview)
    private WebView wvBookPlay;
    @BindView(id = R.id.flVideoContainer)
    private FrameLayout flVideoContainer;
    @BindView(id = R.id.tv_close, click = true)
    private GITextView close_TextView;

    private String strUrl;
    private Date startDate;//打开网页开始时间
    private Date endDate;//打开网页结束时间
    private String strId = "";
    private String strFileType = "";//url类型，1普通，2,3表示海比在线文档或者视频

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_webview_video);
    }

    @Override
    public void onResume() {
        super.onResume();
        startDate = new Date();
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {

            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strUrl = bundle.getString("strUrl", "");
            strId = bundle.getString("strId", "");
            strFileType = bundle.getString("strFileType", "");
        }
        initWebView();
        prgDialog.showLoadDialog();

        CookieManager cookieManager = CookieManager.getInstance();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("android");
        cookieManager.setCookie(Utils.formatFileUrl(activity, strUrl), stringBuffer.toString());
        cookieManager.setAcceptCookie(true);
        wvBookPlay.loadUrl(Utils.formatFileUrl(activity, strUrl));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wvBookPlay != null) {
            wvBookPlay.destroy();
        }

        if (GIStringUtil.isNotBlank(strUrl)) {
            endDate = new Date();
            long strDurationDate = (endDate.getTime() - startDate.getTime()) / 1000;
            textParamMap = new HashMap<>();
            textParamMap.put("strId", strId);
            textParamMap.put("strType", "2");
            textParamMap.put("strDurationDate", String.valueOf(strDurationDate));
            doRequestNormal(ApiManager.getInstance().CommonReadRuleServlet(textParamMap), 99);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_close:
                finish();
                break;
        }
    }

    private void initWebView() {
        WebSettings settings = wvBookPlay.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = wvBookPlay.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(wvBookPlay.getSettings(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDomStorageEnabled(true);// 必须保留，否则无法播放优酷视频，其他的OK
        wvBookPlay.setWebChromeClient(new MyWebChromeClient());// 重写一下，有的时候可能会出现问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wvBookPlay.getSettings().setMixedContentMode(wvBookPlay.getSettings().MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wvBookPlay.setWebViewClient(new MyWebViewClient());
    }

    @SuppressWarnings("deprecation")
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { // 网页页面开始加载的时候
            GILogUtil.e(url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) { // 网页加载结束的时候
            prgDialog.closePrgDialog();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { // 网页加载时的连接的网址,7.0之后被废弃
            url = Utils.formatFileUrl(activity, url);
            wvBookPlay.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wvBookPlay.canGoBack()) {
                wvBookPlay.goBack();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        CustomViewCallback mCallback;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Log.i("ToVmp", "onShowCustomView");
            fullScreen();

            wvBookPlay.setVisibility(View.GONE);
            flVideoContainer.setVisibility(View.VISIBLE);
            flVideoContainer.addView(view);
            mCallback = callback;
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            Log.i("ToVmp", "onHideCustomView");
            fullScreen();

            wvBookPlay.setVisibility(View.VISIBLE);
            flVideoContainer.setVisibility(View.GONE);
            flVideoContainer.removeAllViews();
            super.onHideCustomView();

        }
    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.i("ToVmp", "横屏");
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.i("ToVmp", "竖屏");
        }
    }
}
