package com.suncn.ihold_zxztc.activity.application.proposal;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.utils.Utils;

/**
 * @author :Sea
 * Date :2020-6-17 17:52
 * PackageName:com.suncn.ihold_zxztc.activity.application.proposal
 * Desc:
 */
public class ProposalManagerSituationAnalysisFragment extends BaseFragment {
    @BindView(id = R.id.webview)
    private WebView webview;
    private String url = "";

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_webview, null);
    }

    @Override
    public void initData() {
        super.initData();
        initWebView();
        String curskin = GISharedPreUtil.getString(activity, "curskin", "1");

        url = Utils.formatFileUrl(activity, "res/dist/index.html#/proManage?strSid=" + GISharedPreUtil.getString(activity, "strSid"))+ "&setColor=" +curskin;;
//        url = "http://yanf01.suncn.com.cn/cppcc2020/res/dist/index.html#/proManage?strSid=" + GISharedPreUtil.getString(activity, "strSid");
        GILogUtil.i("url----->" + url);
        webview.loadUrl(url);
    }

    private void initWebView() {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        webview.addJavascriptInterface(new JavaScriptObject(), "JsBridge");
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setTextZoom((int) (100 * (GISharedPreUtil.getFloat(getContext(), "FontScale") > 0 ? GISharedPreUtil.getFloat(getContext(), "FontScale") : 1)));
        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebChromeClient(new WebChromeClient());
        webview.setLongClickable(true);
        webview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }


    public class JavaScriptObject {
        @JavascriptInterface
        public void seletcUser(String strChooseValue, int selectCount) {

        }

        @JavascriptInterface
        public void closePage() {

        }


        @JavascriptInterface
        public void goNews() {

        }

        @JavascriptInterface
        public void goDJ() {

        }

    }

    @SuppressWarnings("deprecation")
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { // 网页页面开始加载的时候
            //prgDialog.showLoadDialog();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) { // 网页加载结束的时候
            prgDialog.closePrgDialog();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { // 网页加载时的连接的网址,7.0之后被废弃
            url = Utils.formatFileUrl(activity, url);
            webview.loadUrl(url);
            return true;
        }

    }
}
