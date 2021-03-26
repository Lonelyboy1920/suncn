package com.suncn.ihold_zxztc.activity.hot;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.theme.Theme_MainActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.circle.Circle_AddDynamicActivity;
import com.suncn.ihold_zxztc.activity.study.StudyActivity;
import com.suncn.ihold_zxztc.utils.Utils;

/**
 * 积分/运营策划专题webview
 */
public class IntegralWebViewActivity extends BaseActivity {
    @BindView(id = R.id.webview)
    private WebView mWebContent;
    @BindView(id = R.id.view_place)
    private View viewPlace;


    @Override
    public void setRootView() {
        setContentView(R.layout.activity_webview_intergel);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebContent.reload();
    }

    @Override
    public void initData() {
        super.initData();
        viewPlace.setVisibility(View.VISIBLE);
        initWebView();
        Bundle bundle = getIntent().getExtras();
        String url = "";
        if (bundle != null) {
            url = bundle.getString("strUrl", "");
        }
        if (GIStringUtil.isBlank(url)) {
            url = Utils.formatFileUrl(activity, "res/dist/index.html#/myScores?strSid=" + GISharedPreUtil.getString(activity, "strSid"));
        }
        mWebContent.loadUrl(Utils.formatFileUrl(activity, url));
        GILogUtil.i("url----->" + url);
    }

    private void initWebView() {
        WebSettings settings = mWebContent.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebContent.addJavascriptInterface(new JavaScriptObject(), "JsBridge");
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setAllowUniversalAccessFromFileURLs(true);
        mWebContent.setWebViewClient(new MyWebViewClient());
        mWebContent.setLongClickable(true);
        mWebContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }


    public class JavaScriptObject {
        @JavascriptInterface //sdk17版本以上加上注解
        public void closePage() {
            setResult(RESULT_OK);
            finish();
        }

        @JavascriptInterface
        public void goNews() {
            setResult(RESULT_OK);
            finish();
        }

        @JavascriptInterface
        public void addDynamic() {
            showActivity(activity, Circle_AddDynamicActivity.class);
        }

        @JavascriptInterface
        public void goMemberLearning() {
            showActivity(activity, StudyActivity.class);
        }

        @JavascriptInterface
        public void goTheme() {
            Bundle bundle = new Bundle();
            bundle.putString("headTitle", "网络议政");
            showActivity(activity, Theme_MainActivity.class, bundle);
        }
    }

    @SuppressWarnings("deprecation")
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { // 网页页面开始加载的时候
            prgDialog.showLoadDialog();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) { // 网页加载结束的时候
            prgDialog.closePrgDialog();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { // 网页加载时的连接的网址,7.0之后被废弃
            url = Utils.formatFileUrl(activity, url);
            mWebContent.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebContent.canGoBack()) {
                mWebContent.goBack();
            } else {
                setResult(RESULT_OK);
                finish();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
