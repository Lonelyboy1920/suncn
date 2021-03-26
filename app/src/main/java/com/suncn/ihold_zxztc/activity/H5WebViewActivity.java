package com.suncn.ihold_zxztc.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.Utils;


import java.util.Date;
import java.util.HashMap;

/**
 * 生日祝福、知情明政功能WebView
 */
public class H5WebViewActivity extends BaseActivity {
    @BindView(id = R.id.webview)
    private WebView mWebContent;
    private LinearLayout rlMainTop;
    private String strUrl;
    private boolean isLZDA;//履职档案
    private boolean isLZBG;//履职报告
    private Date startDate;//打开网页开始时间
    private Date endDate;//打开网页结束时间
    private String strId = "";
    private String strFileType = "";//url类型，1普通，2,3表示海比在线文档或者视频
    private String strUserId = "";
    private int color = 0;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_webview_h5);
        setStatusBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        startDate = new Date();
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBar();
        rlMainTop = findViewById(R.id.rl_main_top);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {

            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            findViewById(R.id.view_place).setVisibility(View.GONE);
            color = bundle.getInt("color", R.color.white);
            if (bundle.getBoolean("isNeedTitle", true)) {
                setHeadTitle(bundle.getString("headTitle"));
                findViewById(R.id.rl_head).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.rl_head).setVisibility(View.GONE);
            }
            strUrl = bundle.getString("strUrl", "");
            strId = bundle.getString("strId", "");
            strFileType = bundle.getString("strFileType", "");
            strUserId = bundle.getString("strUserId", "");
        }
        if (color != 0) {
            rlMainTop.setBackgroundColor(getResources().getColor(color));
        }
        initWebView();
        prgDialog.showLoadDialog();
        mWebContent.loadUrl(Utils.formatFileUrl(activity, strUrl));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (GIStringUtil.isNotBlank(strUrl)) {
            endDate = new Date();
            long strDurationDate = (endDate.getTime() - startDate.getTime()) / 1000;
            textParamMap = new HashMap<>();
            textParamMap.put("strId", strId);
            textParamMap.put("strType", "2");
            if (strFileType.equals("1")) {
                textParamMap.put("strDurationDate", String.valueOf(strDurationDate));
            }
            doRequestNormal(ApiManager.getInstance().CommonReadRuleServlet(textParamMap), 99);
        }
    }

    private void initWebView() {
        WebSettings settings = mWebContent.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebContent.addJavascriptInterface(new JavaScriptObject(), "JsBridge");
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setAllowUniversalAccessFromFileURLs(true);
        mWebContent.setWebViewClient(new MyWebViewClient());
        mWebContent.setWebChromeClient(new WebChromeClient());
        mWebContent.setLongClickable(true);
        mWebContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    public class JavaScriptObject {
        @JavascriptInterface
        public void closePage() {
            finish();
        }

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
            mWebContent.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebContent.canGoBack()) {
                mWebContent.goBack();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
