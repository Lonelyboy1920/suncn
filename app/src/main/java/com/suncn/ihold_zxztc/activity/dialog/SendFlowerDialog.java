package com.suncn.ihold_zxztc.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.hot.HotMainFragment;
import com.suncn.ihold_zxztc.utils.Utils;

public class SendFlowerDialog extends Dialog {
    private TextView tvClose;
    private TextView tvSend;
    private TextView tvName;
    private TextView tvTip;
    private LinearLayout llMain;
    private LinearLayout llSend;
    private TextView tvRecive;
    private Activity mContext;
    private int intStyle;//区分鲜花类型，1表示系统生日祝福，2表示他人鲜花祝福，3表示有一人过生日，4表示多人生日,5运营活动
    private WebView webview;
    private Fragment fragment;
    private String url;

    public SendFlowerDialog(Context context) {
        super(context);
    }


    public SendFlowerDialog(Activity context, Fragment fragment, int themeResId, String url) {
        super(context, R.style.shareDialog);
        this.mContext = context;
        this.fragment = fragment;
        this.url = url;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send_flower);
        setCanceledOnTouchOutside(true);
        changeDialogStyle();
        initView();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void show() {
        super.show();
    }


    private void initView() {
        tvName = findViewById(R.id.tv_name);
        tvSend = findViewById(R.id.tv_send);
        tvClose = findViewById(R.id.tv_close);
        webview = findViewById(R.id.webview);
        webview.setBackgroundColor(0); // 设置背景色
        //webview.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
        initWebView();
        GILogUtil.e(url);
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
        public void closePage() {
            dismiss();
            if (intStyle != 5) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((HotMainFragment) fragment).hideBirthParm();
                    }
                });
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview.canGoBack()) {
                webview.goBack();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置dialog居下占满屏幕
     */
    private void changeDialogStyle() {
        Window window = getWindow();
        // window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                window.setAttributes(attr);
            }
        }
    }
}
