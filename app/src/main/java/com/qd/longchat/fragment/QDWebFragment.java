package com.qd.longchat.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDDept;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDFriendHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.QDAccManager;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.model.gd.QDAccHistoryModel;
import com.longchat.base.util.QDGson;
import com.longchat.base.util.QDLog;
import com.longchat.base.util.encrypt.QDAES256;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.acc.activity.QDAccSelfActivity;
import com.qd.longchat.activity.QDContactActivity;
import com.qd.longchat.activity.QDFileActivity;
import com.qd.longchat.activity.QDPicActivity;
import com.qd.longchat.activity.QDSelectPhotoActivity;
import com.qd.longchat.activity.QDWebActivity;
import com.qd.longchat.bean.QDDownloadBean;
import com.qd.longchat.bean.QDOpenWindowBean;
import com.qd.longchat.bean.QDPreviewBean;
import com.qd.longchat.bean.QDSelectContactBean;
import com.qd.longchat.bean.QDWebParam;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.model.QDContact;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.util.QDWXShare;
import com.qd.longchat.view.QDAlertView;
import com.qd.longchat.view.QDBottomPushPopupWindow;
import com.qd.longchat.view.QDX5WebView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/17 下午1:36
 */

public class QDWebFragment extends QDBaseFragment {

    private final static String TAG = "QDWebFragment";
    private final static int REQUEST_SELECT_CONTACT = 0;
    private final static int REQUEST_SELECT_FILE = 1;
    private final static int REQUEST_SELECT_PIC = 2;
    private final static int REQUEST_OPEN_WINDOW = 3;
    private final static int REQUEST_FORWARD = 4;
    private final static int REQUEST_CAMERA = 5;
    private final static int REQUEST_AUDIO = 6;
    private final static int REQUEST_VIDEO = 7;

    private String homeUrl;
    private String url;
    private Gson gson;
    private String cameraPath;

    @BindView(R2.id.wv_work)
    QDX5WebView webView;
    @BindView(R2.id.pb_work)
    ProgressBar progressBar;

    private String callFunc;

    private boolean isMain;

    private QDAlertView alertView;

    private ValueCallback<Uri> filePathCallBack;

    private ValueCallback<Uri[]> filePathCallBack4;

    private QDAccHistoryModel accHistoryModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(QDIntentKeyUtil.INTENT_KEY_WEB_URL);
            accHistoryModel = (QDAccHistoryModel) bundle.getSerializable(QDIntentKeyUtil.INTENT_ACC_ARTICLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work, container, false);
        unbinder = ButterKnife.bind(this, view);
        gson = QDGson.getGson();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWebView();
        webView.getSettings().setUseWideViewPort(true); //自适应屏幕
        if (TextUtils.isEmpty(url)) {
            webView.loadUrl("http://192.168.1.202:8041/static/docs/file/im_native.html");
        } else {
            webView.loadUrl(url);
        }
        progressBar.setMax(100);
    }

    private void initWebView() {

        webView.addJavascriptInterface(new ToJs(), "IMClient");
        String appCachePath = getContext().getApplicationContext().getCacheDir()
                .getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
        webView.setWebChromeClient(new WebClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (TextUtils.isEmpty(url)) {
                    return false;
                }
                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        webView.loadUrl(url);
                        return true;
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                QDLog.e(TAG, "onPageFinished");
                if (progressBar != null)
                    progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                QDLog.e(TAG, "onPageStarted");
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
            }
        });
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    private class WebClient extends WebChromeClient {
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            filePathCallBack = uploadMsg;
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsgs) {
            filePathCallBack = uploadMsgs;
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String s, String s1) {
            filePathCallBack = valueCallback;
        }

        // For Android  >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            String[] acceptTypes = fileChooserParams.getAcceptTypes();
            if (acceptTypes == null) {
                showBottomDialog();
            } else {
                String type = acceptTypes[0];
                if (type.equalsIgnoreCase("image/*")) {
                    cameraPath = QDStorePath.IMG_PATH + System.currentTimeMillis() + ".png";
                    File file = new File(cameraPath);
                    if (file.exists()) {
                        file.delete();
                    }
                    try {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri uri = QDUtil.getUri(context, file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                        QDUtil.showToast(context, context.getResources().getString(R.string.open_camera_error) + e.getMessage());
                    }
                } else if (type.equalsIgnoreCase("audio/*")) {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    String voicePath = QDStorePath.MSG_VOICE_PATH + System.currentTimeMillis() + ".m4a";
                    //添加权限
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //获取uri
                    Uri uri = AndPermission.getFileUri(context, new File(voicePath));
//                    mVoiceUri = FileProvider.getUriForFile(MainActivity.this, "com.my.wordbar.provider", mVoiceFile);
                    //将uri加入到额外数据
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, REQUEST_AUDIO);
                } else if (type.equalsIgnoreCase("video/*")) {
                    String filePath = QDStorePath.MSG_VIDEO_PATH + System.currentTimeMillis() + ".mp4";   // 保存路径
                    Uri uri = AndPermission.getFileUri(context, new File(filePath));   // 将路径转换为Uri对象
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);  // 表示跳转至相机的录视频界面
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);    // MediaStore.EXTRA_VIDEO_QUALITY 表示录制视频的质量，从 0-1，越大表示质量越好，同时视频也越大
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);    // 表示录制完后保存的录制，如果不写，则会保存到默认的路径，在onActivityResult()的回调，通过intent.getData中返回保存的路径
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);   // 设置视频录制的最长时间
                    startActivityForResult(intent, REQUEST_VIDEO);  // 跳转
                } else {
                    showBottomDialog();
                }
            }
            filePathCallBack4 = valueCallback;
            return true;
        }

        @Override
        public void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
            QDLog.e(TAG, "onProgressChanged, i:" + i);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(i);
                if (i == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public void onReceivedTitle(WebView webView, String s) {
            super.onReceivedTitle(webView, s);
            ((QDWebActivity) context).setTitle(s);
        }
    }

    public class ToJs {

        private final static String FUN_OPEN_WINDOW = "openWindow";
        private final static String FUN_SELECT_CONTACTS = "selectContacts";
        private final static String FUN_CLOSE_WINDOW = "closeWindow";
        private final static String FUN_SET_WINDOW = "setWindow";
        private final static String FUN_DOWNLOAD_FILE = "downloadFile";
        private final static String FUN_PREVIEW_FILE = "previewFile";
        private final static String FUN_PREVIEW_PHOTO = "previewPhoto";
        private final static String FUN_START_LOCAL_APP = "startLocalApp";
        private final static String FUN_START_SHARE_MENU = "startShareMenu";
        private final static String FUN_BACK_WINDOW = "backWindow";
        private final static String FUN_GET_POSITION = "getPosition";
        private final static String FUN_GET_LOGIN_INFO = "getLoginInfo";

        @JavascriptInterface
        public void function(String params) {
            QDLog.e(TAG, "============= web params ===========\n" + params);
            QDWebParam webParam = gson.fromJson(params, QDWebParam.class);
            Object param = webParam.getParams();
            JsonObject paramObject = null;
            if (param instanceof JsonObject) {
                paramObject = (JsonObject) param;
            }

            callFunc = webParam.getCallback();
            switch (webParam.getFunName()) {
                case FUN_OPEN_WINDOW: {
                    QDOpenWindowBean bean = gson.fromJson(paramObject.toString(), QDOpenWindowBean.class);
                    if (bean.getType() == 1) {
                        QDUtil.toBrowser(getActivity(), bean.getUrl());
                    } else {
                        Intent intent = new Intent(context, QDWebActivity.class);
                        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_WEB_PARAM, paramObject.toString());
                        startActivityForResult(intent, REQUEST_OPEN_WINDOW);
                    }
                }
                break;
                case FUN_SELECT_CONTACTS: {
                    QDSelectContactBean bean = gson.fromJson(paramObject.toString(), QDSelectContactBean.class);
                    List<QDSelectContactBean.DataBean> beanList = bean.getData();
                    List<String> selectUserIdList = new ArrayList<>();
                    List<QDUser> selectUserList = new ArrayList<>();
                    List<String> selectDeptIdList = new ArrayList<>();
                    List<QDDept> selectDeptList = new ArrayList<>();
                    List<String> excludedIdList = new ArrayList<>();

                    List<String> ids = QDUserHelper.getUserIdsLtSelfLevel();
                    List<String> friendIdList = QDFriendHelper.getAllFriendIds();
                    for (String friendId : friendIdList) {
                        if (ids.contains(friendId)) {
                            ids.remove(friendId);
                        }
                    }
                    excludedIdList = ids;
                    if (beanList != null && beanList.size() != 0) {
                        for (QDSelectContactBean.DataBean dataBean : beanList) {
                            String type = dataBean.getType();
                            if (type.equalsIgnoreCase("u")) {
                                QDUser user = new QDUser();
                                user.setId(dataBean.getId());
                                user.setName(dataBean.getName());
                                user.setPic(dataBean.getAvatar());
                                selectUserIdList.add(dataBean.getId());
                                selectUserList.add(user);
                            } else if (type.equalsIgnoreCase("d")) {
                                QDDept dept = new QDDept();
                                dept.setId(dataBean.getId());
                                dept.setName(dataBean.getName());
                                selectDeptIdList.add(dataBean.getId());
                                selectDeptList.add(dept);
                            }
                        }
                    }
                    Intent intent = new Intent(context, QDContactActivity.class);
                    intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_EXCLUDED_ID_LIST, (ArrayList<String>) excludedIdList);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_MULTI);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_TYPE, QDContact.TYPE_CONTACT);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_RETURN_DEPT_ID, true);
                    intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_ID_LIST, (ArrayList<String>) selectUserIdList);
                    intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_ID_LIST, (ArrayList<String>) selectDeptIdList);
                    intent.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST, (ArrayList<? extends Parcelable>) selectUserList);
                    intent.putParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_LIST, (ArrayList<? extends Parcelable>) selectDeptList);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SELECT_TYPE, bean.getType());
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SELECT_LIMIT, bean.getLimit());
                    startActivityForResult(intent, REQUEST_SELECT_CONTACT);
                }
                break;
                case FUN_CLOSE_WINDOW: {
                    if (!isMain) {
                        Intent intent = new Intent();
                        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CALL_PARAM, paramObject.toString());
                        getActivity().setResult(RESULT_OK, intent);
                        getActivity().finish();
                    }
                }
                break;
                case FUN_SET_WINDOW: {
                    QDOpenWindowBean bean = gson.fromJson(paramObject.toString(), QDOpenWindowBean.class);
                    Activity mActivity = ((Activity) context);
                    if (mActivity instanceof QDWebActivity) {
                        ((QDWebActivity) mActivity).setWindow(bean);
                    }
                }
                break;
                case FUN_DOWNLOAD_FILE:
                case FUN_PREVIEW_FILE: {
                    QDDownloadBean bean = gson.fromJson(paramObject.toString(), QDDownloadBean.class);
                    String url = bean.getUrl();
                    String name = bean.getName();
                    if (TextUtils.isEmpty(name)) {
                        name =  url.substring(url.lastIndexOf("/") + 1);
                    }
                    final String filePath = QDStorePath.APP_PATH + name;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        downloadFile(url, filePath);
                    } else {
                        QDUtil.openFile(context, filePath);
                    }
                }
                break;
                case FUN_START_LOCAL_APP: {
                    if (paramObject != null) {
                        if (paramObject.has("app_code")) {
                            String appCode = paramObject.get("app_code").getAsString();
                            if (appCode.equalsIgnoreCase("addon_acc")) {
                                Intent intent = new Intent(context, QDAccSelfActivity.class);
                                context.startActivity(intent);
                            }
                        }
                    }
                }
                break;
                case FUN_BACK_WINDOW: {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        getActivity().finish();
                    }
                }
                break;
                case FUN_START_SHARE_MENU: {
                    String url = paramObject.get("shareUrl").getAsString();
                    showShareDialog(QDUtil.replaceWebServerAndToken(url));
                }
                break;
                case FUN_PREVIEW_PHOTO: {
                    QDPreviewBean bean = gson.fromJson(paramObject.toString(), QDPreviewBean.class);
                    Intent i = new Intent(context, QDPicActivity.class);
                    i.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) bean.getPhotos());
                    i.putExtra(QDIntentKeyUtil.INTENT_KEY_INDEX, bean.getIndex());
                    i.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_REMOTE, true);
                    i.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_HAVE_LONG_CLICK, true);
                    context.startActivity(i);
                }
                break;
                case FUN_GET_POSITION: {
                    if (AndPermission.hasPermissions(context, Permission.Group.LOCATION)) {
                        getLocation();
                    } else {
                        getPermission(Permission.Group.LOCATION);
                    }
                }
                break;
                case FUN_GET_LOGIN_INFO: {
                    JsonObject json = new JsonObject();
                    json.addProperty("account", QDLanderInfo.getInstance().getAccount());
                    json.addProperty("ssid", QDLanderInfo.getInstance().getSsid());
                    json.addProperty("password", QDAES256.encryptPwd(QDUtil.decodeString(QDLanderInfo.getInstance().getPassword())));
                    doCallBack(json.toString());
                }
                break;
            }

        }
    }

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    private AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWarningDailog().dismiss();
                }
            });
            if (aMapLocation.getErrorCode() == 0) {
                JsonObject data = new JsonObject();
                data.addProperty("latitude", aMapLocation.getLatitude());
                data.addProperty("longitude", aMapLocation.getLongitude());
                data.addProperty("address", aMapLocation.getAddress());
                data.addProperty("title", aMapLocation.getPoiName());
                doCallBack(data.toString());
            } else {
                QDUtil.showToast(context,"location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    };

    private void getLocation() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWarningDailog().show();
            }
        });
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        if(null != mLocationClient){
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    private void downloadFile(final String url, final String path) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWarningDailog().show();
                QDFileManager.getInstance().downloadFile(path, QDUtil.replaceWebServerAndToken(url), new QDFileDownLoadCallBack() {
                    @Override
                    public void onDownLoading(int per) {

                    }

                    @Override
                    public void onDownLoadSuccess() {
                        getWarningDailog().dismiss();
                        QDUtil.openFile(context, path);
                    }

                    @Override
                    public void onDownLoadFailed(String msg) {
                        getWarningDailog().dismiss();

                    }
                });
            }
        });
    }

    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getContext().getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        String temp = cursor.getString(index);
        cursor.close();
        return temp;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    QDUtil.createBitmapFromCamera(cameraPath);
                    uploadFile(cameraPath);
                    break;
                case REQUEST_AUDIO:
                    try {
                        Uri uri = data.getData();
                        String filePath = getAudioFilePathFromUri(uri);
                        uploadFile(filePath);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    break;
                case REQUEST_VIDEO:
                    Uri uri = data.getData();
                    String videoPath = uri.getPath();
                    uploadFile(videoPath);
                    break;
                case REQUEST_SELECT_CONTACT:
                    requestSelectContact(data);
                    break;
                case REQUEST_SELECT_FILE:
                    File file = (File) data.getSerializableExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_FILE);
                    String path = file.getPath();
                    if (!path.contains(".")) {
                        QDUtil.showToast(context, context.getResources().getString(R.string.file_must_contain_suffixes));
                        return;
                    }
                    if (file.length() == 0) {
                        QDUtil.showToast(context, context.getResources().getString(R.string.file_length_is_zero));
                        return;
                    }
                    uploadFile(path);
//                    try {
//                        String path;
//                        if (Build.VERSION.SDK_INT >= 19) {
//                            path = QDUtil.handleImageOnKitKat(context, data);
//                        } else {
//                            path = QDUtil.handleImageBeforeKitKat(context, data);
//                        }
//                        if (!path.contains(".")) {
//                            QDUtil.showToast(context, context.getResources().getString(R.string.file_must_contain_suffixes));
//                            return;
//                        }
//                        File file = new File(path);
//                        if (file.length() == 0) {
//                            QDUtil.showToast(context, context.getResources().getString(R.string.file_length_is_zero));
//                            return;
//                        }
//                        uploadFile(file.getPath());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    break;
                case REQUEST_SELECT_PIC:
                    String path1 = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH);
                    uploadFile(path1);
                    break;
                case REQUEST_OPEN_WINDOW:
                    doCallBack(data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_CALL_PARAM));
                    break;
                case REQUEST_FORWARD:
                    QDUser user = data.getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_USER);
                    List<Map<String, String>> recverList = new ArrayList<>();
                    Map<String, String> recverMap = new HashMap<>();
                    recverMap.put("id", user.getId());
                    recverMap.put("name", user.getName());
                    recverList.add(recverMap);

                    Map<String, String> params = new HashMap<>();
                    params.put("sender", QDLanderInfo.getInstance().getId());
                    params.put("recver", QDGson.getGson().toJson(recverList));
                    params.put("url", QDUtil.replaceWebServerAndToken(accHistoryModel.getPcUrl()));
                    params.put("title", accHistoryModel.getTitle());
                    params.put("desc", accHistoryModel.getIntro());
                    params.put("icon", QDUtil.replaceWebServerAndToken(accHistoryModel.getCover()));
                    params.put("url_mobile", QDUtil.replaceWebServerAndToken(accHistoryModel.getMobileUrl()));
                    QDAccManager.getInstance().sendCustomMsg(params, new QDResultCallBack() {
                        @Override
                        public void onError(String errorMsg) {
                            QDLog.e("1111", "error");
                        }

                        @Override
                        public void onSuccess(Object o) {
                            QDLog.e("1111", "success");
                        }
                    });
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            cancelFilePathCallback();
        }
    }

    private void requestSelectContact(Intent intent) {
        List<QDUser> userList = intent.getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_USER_LIST);
        List<QDDept> deptList = intent.getParcelableArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_DEPT_LIST);
        List<Map<String, String>> mapList = new ArrayList<>();
        for (QDUser user : userList) {
            mapList.add(getUserMap(user));
        }
        for (QDDept dept : deptList) {
            mapList.add(getDeptMap(dept.getId(), dept.getName()));
        }
        String jsonString = gson.toJson(mapList);
        doCallBack(jsonString);
    }

    private Map<String, String> getUserMap(QDUser user) {
        Map<String, String> map = new HashMap<>();
        map.put("type", "u");
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("avatar", user.getPic());
        map.put("mobile", user.getMobile());
        map.put("email", user.getEmail());
        map.put("title", user.getJob());
        return map;
    }

    private Map<String, String> getDeptMap(String id, String name) {
        Map<String, String> map = new HashMap<>();
        map.put("type", "d");
        map.put("id", id);
        map.put("name", name);
        return map;
    }

    private void doCallBack(String param) {
        if (context == null) {
            return;
        }
        webView.loadUrl("javascript:" + callFunc + "(" + param + ")");
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public QDX5WebView getWebView() {
        return webView;
    }

    private QDAlertView.OnStringItemClickListener listener = new QDAlertView.OnStringItemClickListener() {
        @Override
        public void onItemClick(String str, int position) {
            if (position == 0) {
                cameraPath = QDStorePath.IMG_PATH + System.currentTimeMillis() + ".png";
                File file = new File(cameraPath);
                if (file.exists()) {
                    file.delete();
                }
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri uri = QDUtil.getUri(context, file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                    QDUtil.showToast(context, context.getResources().getString(R.string.open_camera_error) + e.getMessage());
                }

            } else if (position == 1) {
                Intent fileIntent = new Intent(context, QDFileActivity.class);
                startActivityForResult(fileIntent, REQUEST_SELECT_FILE);

//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(Intent.createChooser(intent, context.getResources().getString(R.string.select_file)), REQUEST_SELECT_FILE);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    QDUtil.showToast(context, context.getResources().getString(R.string.file_management_application_not_found));
//                }
            } else {
                Intent imageIntent = new Intent(context, QDSelectPhotoActivity.class);
                imageIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_SINGLE, true);
                startActivityForResult(imageIntent, REQUEST_SELECT_PIC);
            }
        }
    };

    private QDAlertView.OnCancelClickListener cancelClickListener = new QDAlertView.OnCancelClickListener() {
        @Override
        public void onCancel(boolean b) {
            if (!b)
                cancelFilePathCallback();
        }
    };

    private void showBottomDialog() {
        if (alertView == null) {
            alertView = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Bottom)
                    .setSelectList(context.getResources().getString(R.string.text_camera), context.getResources().getString(R.string.select_file), context.getResources().getString(R.string.select_pic))
                    .setOnStringItemClickListener(listener)
                    .setCancelClickListener(cancelClickListener)
                    .setDismissOutside(true)
                    .isAutoDismiss(true)
                    .build();
        }
        alertView.show();
    }

    private void cancelFilePathCallback() {
        if (filePathCallBack != null) {
            filePathCallBack.onReceiveValue(null);
            filePathCallBack = null;
        }
        if (filePathCallBack4 != null) {
            filePathCallBack4.onReceiveValue(null);
            filePathCallBack4 = null;
        }
    }

    private void uploadFile(String path) {
        Uri uri = Uri.fromFile(new File(path));
        if (filePathCallBack != null) {
            filePathCallBack.onReceiveValue(uri);
            filePathCallBack = null;
        }
        if (filePathCallBack4 != null) {
            filePathCallBack4.onReceiveValue(new Uri[]{uri});
            filePathCallBack4 = null;
        }
        if (alertView != null) {
            alertView.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            ViewGroup viewGroup = ((ViewGroup) webView.getParent());
            if (viewGroup != null)
                ((ViewGroup) webView.getParent()).removeView(webView);
            webView.stopLoading();
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        super.onDestroy();
    }

    /**
     * 显示分享弹框
     */
    private void showShareDialog(final String url) {
        final QDBottomPushPopupWindow detailPopup = new QDBottomPushPopupWindow(context);
        View popView = detailPopup.initView(R.layout.im_popup_menu_share_select_item);
        TextView toContact = popView.findViewById(R.id.tv_share_contact);
        TextView toWechat = popView.findViewById(R.id.tv_share_wechat);
        final TextView toWechatFriend = popView.findViewById(R.id.tv_share_wechat_friend);
        TextView cancelItem = popView.findViewById(R.id.tv_cancel_item);
        cancelItem.setTextSize(18);
        cancelItem.setText(R.string.cancel);
        cancelItem.setTextColor(context.getResources().getColor(R.color.colorRed));
        toContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QDContactActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_SINGLE);
                startActivityForResult(intent, REQUEST_FORWARD);
                detailPopup.dismiss();
            }
        });
        toWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QDWXShare.getInstance().getApi().isWXAppInstalled()) {
                    QDWXShare.getInstance().shareURL(url, true);
                } else {
                    QDUtil.showToast(context, context.getResources().getString(R.string.install_wechat));
                }
                detailPopup.dismiss();
            }
        });
        toWechatFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QDWXShare.getInstance().getApi().isWXAppInstalled()) {
                    QDWXShare.getInstance().shareURL(url, false);
                } else {
                    QDUtil.showToast(context, context.getResources().getString(R.string.install_wechat));
                }
                detailPopup.dismiss();
            }
        });
        cancelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailPopup.dismiss();
            }
        });

        detailPopup.show(getActivity());
    }

    private void getPermission(final String[] permission) {
        AndPermission.with(context)
                .runtime()
                .permission(permission)
                .rationale(rationale)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        getLocation();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        getPermission(permission);
                    }
                })
                .start();
    }

    private Rationale rationale = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, final RequestExecutor executor) {
            final QDAlertView alertDialog = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Alert)
                    .setContent(getString(R.string.permission_hint))
                    .isHaveCancelBtn(false)
                    .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                        @Override
                        public void onClick(boolean isSure) {
                            executor.execute();
                        }
                    })
                    .build();
            alertDialog.show();

        }
    };

}
