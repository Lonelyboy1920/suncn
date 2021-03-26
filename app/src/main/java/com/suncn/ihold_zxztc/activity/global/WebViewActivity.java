package com.suncn.ihold_zxztc.activity.global;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.service.GIDownloadService;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIFileUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIPhoneUtils;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetSpeak_AddActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetSpeakOpinion_CheckActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_JoinAffirmActivity;
import com.suncn.ihold_zxztc.activity.application.publicopinion.EditContentDialog;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.UrlConstantsBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.ShareUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.SpinerPopWindow;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * webview
 */
public class WebViewActivity extends BaseActivity {
    @BindView(id = R.id.webview)
    private WebView webView; // WebView
    @BindView(id = R.id.btn_close)
    private GITextView close_TextView; // 关闭页面
    @BindView(id = R.id.ll_btn)
    private LinearLayout llBtn;
    @BindView(id = R.id.tv_reply, click = true)
    private TextView tvReply;
    @BindView(id = R.id.tv_commit, click = true)
    private TextView tvCommit;
    private String myUrl;
    private String strId; // 会议发言的主键
    private boolean isSocialOpinions; // 是否为社情民意信息
    private boolean isPersonInfo;
    private boolean isMsgRemind;//是否为消息提醒
    private String url;//跳转的url
    private String strShowAddMeetSpeak;//1显示提交发言材料
    private String strMeetSpeakNoticeId;//id
    private int intKind;
    private SpinerPopWindow<String> mSpinerPopWindow;
    private ArrayList<String> dealArray = new ArrayList<>();
    private String strExamineStatus = "";// 社情民意审核状态:1通过、0不通过
    private String strTypeForQH = "";
    private String mIntToMsz;  //提交给秘书张  0不显示 1显示  滁州是政协添加
    private String mIntToZx;  //提交给主席  0不显示 1显示  滁州市政协添加
    private String mStrCheckType;
    private String mStrType;
    private String mIntPass;
    private boolean isShowJoin;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    goto_Button.setVisibility(View.INVISIBLE);
                    setResult(RESULT_OK);
                    finish();
                    break;
                case 0://新闻回复返回url
                    if (data != null) {
                        url = data.getExtras().getString("strUrl");
                        webView.loadUrl(url);
                    }
                    break;
                case 2://会议发言处理
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_webview);
    }

    @Override
    public void setListener() {
        super.setListener();
        back_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        close_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                setResult(RESULT_OK);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 获取Url页面
     */
    private void getUrlConstants() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<String, String>();
        textParamMap.put("intType", "0");//0-关于

        doRequestNormal(ApiManager.getInstance().getUrl(textParamMap), 0);
    }

    /**
     * 全会模块获取url
     */
    private void getWebViewUrl() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", strTypeForQH);
        doRequestNormal(ApiManager.getInstance().ToViewServlet(textParamMap), 2);
    }

    @Override
    public void initData() {
        super.initData();
        initWebView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strShowAddMeetSpeak = bundle.getString("strShowAddMeetSpeak", "0");
            intKind = bundle.getInt("intKind", 0);
            isMsgRemind = bundle.getBoolean("isMsgRemind", false);
            isPersonInfo = bundle.getBoolean("isPersonInfo", false);
            String title = bundle.getString("headTitle");
            strTypeForQH = bundle.getString("strTypeForQH", "");
            mStrCheckType = bundle.getString("strCheckType");
            if (isPersonInfo) {
                setHeadTitle(title);
            } else {
                setHeadTitle(title + getString(R.string.string_detail));
            }
            if (GIStringUtil.isNotBlank(strTypeForQH)) {
                getWebViewUrl();
            } else {
                url = bundle.getString("strUrl");
                url = Utils.formatFileUrl(activity, url);
                GILogUtil.i("detailUrl----->" + url);
                webView.loadUrl(url);
                String strType = bundle.getString("strType");
                isSocialOpinions = bundle.getBoolean("isSocialOpinions");
                isShowJoin = bundle.getBoolean("isShowJoin", false);
                if (isShowJoin) {
                    strId = bundle.getString("strId");
                    goto_Button.setVisibility(View.VISIBLE);
                    goto_Button.setText("联名确认");
                    goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                } else if ("1".equals(strType)) {
                    goto_Button.setText("审核");
                    goto_Button.refreshFontType(activity, "2");
                    if (ProjectNameUtil.isGZSZX(activity)) {  //贵州政协，屏蔽掉机关人员的审核
                        goto_Button.setVisibility(View.GONE);
                    } else {
                        goto_Button.setVisibility(View.VISIBLE);
                    }
                    goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    strId = bundle.getString("strId");
                    setResult(RESULT_OK);
                } else if ("1".equals(strShowAddMeetSpeak)) {
                    llBtn.setVisibility(View.VISIBLE);
                    GISharedPreUtil.setValue(activity, "isRefreshSpeakNotice", true);
                    strMeetSpeakNoticeId = bundle.getString("strMeetSpeakNoticeId");
                } else if (head_title_TextView.getText().toString().equals(Utils.getMyString(R.string.string_invitationOpen))) {
                    goto_Button.setVisibility(View.VISIBLE);
                    goto_Button.setText("分享");
                    goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                } else {
                    goto_Button.setVisibility(View.GONE);
                }
            }

            if (ProjectNameUtil.isCZSZX(activity)) {
                if (intKind == DefineUtil.hyfy) {
                    mIntToMsz = bundle.getString("intToMsz");
                    mIntToZx = bundle.getString("intToZx");
                    mStrType = bundle.getString("strTypeHYFY");
                    goto_Button.setText(mStrCheckType == null ? "审核" : mStrCheckType);
                } else if (intKind == DefineUtil.sqmy) {
                    mIntToZx = bundle.getString("intToZx");
                    mIntPass = bundle.getString("intPass");
                    goto_Button.setText(mStrCheckType == null ? "审核" : mStrCheckType);
                } else {
                    if ("1".equals(strShowAddMeetSpeak)) {
                        GISharedPreUtil.setValue(activity, "isRefreshSpeakNotice", true);
                        goto_Button.setText(getResources().getString(R.string.font_add));
                        goto_Button.setVisibility(View.VISIBLE);
                        strMeetSpeakNoticeId = bundle.getString("strMeetSpeakNoticeId");
                    } else {
                        goto_Button.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            setHeadTitle(getString(R.string.string_about) + getString(R.string.app_name));
            getUrlConstants();
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
    }

    /**
     * popupwindow显示的ListView的item点击事件(年份)
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    strExamineStatus = "1";
                    sendCheckResult();
                    break;
                case 1:
                    strExamineStatus = "0";
                    sendCheckResult();
                    break;
                default:
                    break;
            }
            mSpinerPopWindow.dismiss();
        }
    };

    /**
     * 发送审核结果
     */
    private void sendCheckResult() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        textParamMap.put("strExamineStatus", strExamineStatus);
        doRequestNormal(ApiManager.getInstance().checkOpinions(textParamMap), 1);
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 1:
                setResult(RESULT_OK);
                finish();
                break;
            case 0:
                prgDialog.closePrgDialog();
                try {
                    UrlConstantsBean globalResult = (UrlConstantsBean) data;
                    String url = Utils.formatFileUrl(activity, globalResult.getStrUrl());
                    if (GIStringUtil.isNotBlank(url)) {
                        url = url + "?strVersionName=" + "For Android V" + GIPhoneUtils.getAppVersionName(activity);
                        webView.loadUrl(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 2:
                prgDialog.closePrgDialog();
                try {
                    UrlConstantsBean globalResult = (UrlConstantsBean) data;
                    String url = Utils.formatFileUrl(activity, globalResult.getStrUrl());
                    GILogUtil.e("url=====", url);
                    if (GIStringUtil.isNotBlank(url)) {
                        webView.loadUrl(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 3:
                showToast("回复成功");
                break;
            default:
                break;
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_goto:
                if (goto_Button.getText().toString().equals("分享")) {
                    ShareUtil.showDialog(activity, " ", GISharedPreUtil.getString(activity, "strName") + "委员邀请您下载最新版的职掌中宝APP,快来下载体验吧", "", url, new String[]{"微信", "朋友圈", "QQ", "微博"});
                } else if (goto_Button.getText().toString().equals("联名确认")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("strId", strId);
                    bundle.putInt("sign", DefineUtil.sqmy);
                    bundle.putString("strTitle", "联名确认");
                    showActivity(activity, Proposal_JoinAffirmActivity.class, bundle, 1);
                } else if (GIStringUtil.isNotBlank(strId)) {
                    if (isSocialOpinions) {
                        Bundle bundle = new Bundle();
                        bundle.putString("strId", strId);
                        bundle.putBoolean("isSocialOpinions", isSocialOpinions);
                        bundle.putString("intToZx", mIntToZx);
                        bundle.putString("intPass", mIntPass);
                        showActivity(activity, MeetSpeakOpinion_CheckActivity.class, bundle, 1);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("strId", strId);
                        bundle.putBoolean("isSocialOpinions", isSocialOpinions);
                        if (ProjectNameUtil.isCZSZX(activity)) {  //滁州市政协增加
                            if (intKind == DefineUtil.hyfy) {
                                bundle.putString("intToMsz", mIntToMsz);
                                bundle.putString("intToZx", mIntToZx);
                                bundle.putString("strTypeHYFY", mStrType);
                            } else if (intKind == DefineUtil.sqmy) {
                                bundle.putString("intToZx", mIntToZx);
                                bundle.putString("intPass", mIntPass);
                            }
                        }
                        showActivity(activity, MeetSpeakOpinion_CheckActivity.class, bundle, 1);
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("headTitle", getString(R.string.string_meeting_speach));
                    bundle.putString("strMeetSpeakNoticeId", strMeetSpeakNoticeId);
                    showActivity(activity, MeetSpeak_AddActivity.class, bundle, 2);
                }
                break;
            case R.id.tv_commit:
                Bundle bundle = new Bundle();
                bundle.putString("headTitle", getString(R.string.string_meeting_speach));
                bundle.putString("strMeetSpeakNoticeId", strMeetSpeakNoticeId);
                showActivity(activity, MeetSpeak_AddActivity.class, bundle, 2);
                break;
            case R.id.tv_reply:
                EditContentDialog editContentDialog = new EditContentDialog(activity, R.style.MyDialogStyleBottom);
                editContentDialog.setMyTitle(getResources().getString(R.string.string_reply));
                editContentDialog.setMaxLength(500);
                editContentDialog.setMyHint(getResources().getString(R.string.string_hint_reply));
                editContentDialog.setOnItemClickListener(new EditContentDialog.onItemClickListener() {
                    @Override
                    public void onItemClick(View v, EditText e, String content) {
                        if (GIStringUtil.isBlank(content)){
                            showToast("请输入回复内容");
                            return;
                        }
                        GIUtil.closeSoftInput(activity);
                        doReply(content);
                        editContentDialog.dismiss();
                    }
                });
                editContentDialog.show();
                break;
        }
    }

    /**
     * 会议发言回复
     */
    private void doReply(String content) {
        textParamMap = new HashMap<>();
        textParamMap.put("strSpeakNoticeId", strMeetSpeakNoticeId);
        textParamMap.put("strReplyContent", content);
        doRequestNormal(ApiManager.getInstance().MeetSpeakNoticeReplyServlet(textParamMap), 3);
    }

    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setTextZoom((int) (100 * (GISharedPreUtil.getFloat(this, "FontScale") > 0 ? GISharedPreUtil.getFloat(this, "FontScale") : 1)));
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setLongClickable(true);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @SuppressWarnings("deprecation")
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { // 网页页面开始加载的时候
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) { // 网页加载结束的时候
            if (view.canGoBack()) {
                close_TextView.setVisibility(View.VISIBLE);
            } else {
                close_TextView.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { // 网页加载时的连接的网址,7.0之后被废弃
            url = Utils.formatFileUrl(activity, url);
            myUrl = url;
            GILogUtil.i("myUrl----->" + myUrl);
            if (url.contains("/upload/")) {
                HiPermission.create(activity).checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                    @Override
                    public void onGuarantee(String permisson, int position) { // 同意/已授权
                        String[] urlArr;
                        String myFileName = GIFileUtil.getFileName(myUrl);
                        String myDownLoadUrl = myUrl;
                        try {
                            if (myUrl.contains("$")) {
                                urlArr = myUrl.split("\\$");
                                myFileName = URLDecoder.decode(urlArr[1], "UTF-8");
                                myDownLoadUrl = GIStringUtil.isBlank(urlArr[0]) ? myUrl : urlArr[0];
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        GILogUtil.i("myDownLoadUrl----->" + myDownLoadUrl);
                        GILogUtil.i("myFileName----->" + myFileName);
                        Intent intent = new Intent(activity, GIDownloadService.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("url", Utils.formatFileUrl(activity, myDownLoadUrl));
                        bundle.putString("filename", GIStringUtil.nullToEmpty(myFileName));
                        bundle.putInt("smallIcon", R.mipmap.ic_launcher);
                        intent.putExtras(bundle);
                        activity.startService(intent);
                    }

                    @Override
                    public void onClose() { // 用户关闭权限申请
                    }

                    @Override
                    public void onFinish() { // 所有权限申请完成
                    }

                    @Override
                    public void onDeny(String permisson, int position) { // 拒绝
                    }
                });
            } else {
                webView.loadUrl(url);
            }
            return true;
        }
    }
}
