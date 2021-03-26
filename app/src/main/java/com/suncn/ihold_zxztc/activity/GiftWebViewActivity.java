package com.suncn.ihold_zxztc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.message.Contact_MainActivity;
import com.suncn.ihold_zxztc.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 个性化
 */
public class GiftWebViewActivity extends BaseActivity {
    @BindView(id = R.id.webview)
    private WebView mWebContent;
    @BindView(id = R.id.view_white)
    private View viewPlace;
    @BindView(id = R.id.rl_head)
    private RelativeLayout rlHead;
    private String url = "";
    private String strChooseValue = "";
    private String strChooseName = "";
    private String strHeadUrl = "";
    private String titleColor = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle bundle = new Bundle();
            if (data != null) {
                bundle = data.getExtras();
            }
            if (bundle != null) {
                strChooseValue = bundle.getString("strChooseValue", "");
                strChooseName = Utils.getShowAddress(strChooseValue, false);
                strHeadUrl = bundle.getString("strHeadUrl", "");
                String[] ids = strChooseValue.split(",");
                String[] names = strChooseName.split("，");
                String[] urls = strHeadUrl.split(",");
                GILogUtil.e("ids===", ids);
                GILogUtil.e("names===", names);
                GILogUtil.e("urls===", urls);
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < ids.length; i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("strUserId", ids[i]);
                        jsonObject.put("strUserName", names[i]);
                        jsonObject.put("strUserHeadUrl", urls[i]);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                GILogUtil.e("jsonArray======", jsonArray);
                if (GIStringUtil.isNotBlank(strChooseValue)) {
                    mWebContent.loadUrl("javascript:getUserValue('" + jsonArray + "')");
                } else {
                    mWebContent.loadUrl("javascript:getUserValue('[]')");
                }
            }
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_webview_edit);
    }

    @Override
    public void onResume() {
        super.onResume();
        //mWebContent.reload();
    }

    @Override
    public void initData() {
        setStatusBar(false, false);
        //setHeadTitle("积分");
        viewPlace.setVisibility(View.GONE);
        rlHead.setVisibility(View.GONE);
        initWebView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = Utils.formatFileUrl(activity, bundle.getString("strUrl"));
            titleColor = bundle.getString("titleColor", "");
        }
//        if (GIStringUtil.isNotBlank(titleColor)) {
//            viewPlace.setVisibility(View.VISIBLE);
//            viewPlace.setBackgroundColor(Color.parseColor(titleColor));
//        }
//        if (GIStringUtil.isBlank(url)) {
//            viewPlace.setVisibility(View.VISIBLE);
//            //url="http://192.168.4.246:8080/#/getGiftPupTest";
//            url = Utils.formatFileUrl(activity, "res/dist/index.html#/integral?strSid=" + GISharedPreUtil.getString(activity, "strSid"));
//        }
        GILogUtil.i("url----->" + url);
        mWebContent.loadUrl(url);
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
        public void seletcUser(String strChooseValue, int selectCount) {
            GILogUtil.e("1213213131", strChooseValue);
            Bundle bundle = new Bundle();
            bundle.putString("strChooseValue", strChooseValue);
            bundle.putString("strHeadUrl", strHeadUrl);
            bundle.putBoolean("isChoose", true);
            bundle.putBoolean("isShowHead", false);
            bundle.putString("showTitle", "选择委员");
            bundle.putBoolean("isOnlyWY", false);
            bundle.putInt("selectCount", selectCount);
            bundle.putInt("sign", 11);
            showActivity(activity, Contact_MainActivity.class, bundle, 0);
        }

        @JavascriptInterface
        public void closePage() {
            finish();
        }


        @JavascriptInterface
        public void goNews() {
            finish();
        }

        @JavascriptInterface
        public void goDJ() {
            setResult(1000);
            finish();
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
