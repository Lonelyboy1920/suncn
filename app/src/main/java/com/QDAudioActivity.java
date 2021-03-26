package com;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.longchat.base.QDClient;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDAVActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDIntentKeyUtil;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class QDAudioActivity extends AppCompatActivity implements CordovaInterface {
    // Plugin to call when activity result is received
    protected CordovaPlugin activityResultCallback = null;
    protected boolean activityResultKeepRunning;

    private SystemWebView systemWebView;
    private CordovaWebView cordovaWebView;
    private WebView webView;

    protected boolean keepRunning = true;

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    BroadcastReceiver receiver;

    private QDUser user;
    private String roomId;
    private long startTime;
    private boolean isAudio;
    private boolean isOut;
    private String avUrl;
    private boolean isMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        systemWebView = findViewById(R.id.system_webview);
        user = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_USER);
        String data = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_ROOM_ID);
        JsonObject jsonObject = new Gson().fromJson(data, JsonObject.class);
        if (!TextUtils.isEmpty(data)) {
            isMeeting = false;
            avUrl = jsonObject.get("mobile_url").getAsString();
        } else {
            isMeeting = true;
            avUrl = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_AV_URL);
        }
        isOut = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_OUT, false);
        isAudio = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_AUDIO, true);
        ConfigXmlParser parser = new ConfigXmlParser();

        parser.parse(this);//这里会解析res/xml/config.xml配置文件
        cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(systemWebView));//创建一个cordovawebview
        cordovaWebView.init(new CordovaInterfaceImpl(this), parser.getPluginEntries(), parser.getPreferences());//初始化
        String[] strs;
        if (avUrl.contains("rd")) {
            strs = avUrl.split("rd=");
        } else {
            strs = avUrl.split("=");
        }
        String roomId = strs[1].split("&")[0];
        String server = "";
        try {
            String mediaServer = QDLanderInfo.getInstance().getMediaServer().split("//")[1].split(":")[0];
            server = "&host=" + mediaServer + "&relayHost=" + mediaServer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String finalServer = server;
        List<PermissionItem> permissionItems = new ArrayList<>();
        permissionItems.add(new PermissionItem(Manifest.permission.RECORD_AUDIO, "录音", R.drawable.permission_ic_storage));
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "相机", R.drawable.permission_ic_camera));
        HiPermission.create(this)
                .permissions(permissionItems)
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {

                        GILogUtil.i("onClose" + "1");
                    }

                    @Override
                    public void onFinish() {
                        if (TextUtils.isEmpty(data)) {
                            if (TextUtils.isEmpty(finalServer)) {
                                systemWebView.loadUrl("file:///android_asset/www/index.html#/service?rd=" + roomId + "&p=2&name=" + QDLanderInfo.getInstance().getName());
                            } else {
                                systemWebView.loadUrl("file:///android_asset/www/index.html#/service?rd=" + roomId + "&p=2&name=" + QDLanderInfo.getInstance().getName() + finalServer);
                            }
                        } else {
                            if (TextUtils.isEmpty(finalServer)) {
                                systemWebView.loadUrl("file:///android_asset/www/index.html#/service?rd=" + roomId + "&p=1&name=" + QDLanderInfo.getInstance().getName());
                            } else {
                                systemWebView.loadUrl("file:///android_asset/www/index.html#/service?rd=" + roomId + "&p=1&name=" + QDLanderInfo.getInstance().getName() + finalServer);
                            }
                        }

                        receiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                try {
                                    JSONObject data = new JSONObject(intent.getExtras().getString("userdata"));
                                    int index = data.getInt("recode");
                                    String time = "";
                                    if (!data.isNull("timeStr")) {
                                        time = data.getString("timeStr");
                                    }

                                    Log.d("CDVBroadcaster",
                                            String.format("Native event [%s] received with data [%s]", intent.getAction(), String.valueOf(data)));

                                    if (isMeeting) {
                                        if (index == 2002) {
                                            finish();
                                        }
                                        return;
                                    }

                                    if (index == 2006 || index == 2002) {
                                        if (user == null) {
                                            finish();
                                            return;
                                        }
                                        String content;
                                        if (TextUtils.isEmpty(time)) {
                                            content = "通话时长 00:00:00";
                                        } else {
                                            content = "通话时长 " + time;
                                        }
                                        QDMessage message;
                                        if (isAudio) {
                                            message = QDClient.getInstance().createAVMessage(user.getId(), user.getName(), content, false, isOut);
                                        } else {
                                            message = QDClient.getInstance().createAVMessage(user.getId(), user.getName(), content, true, isOut);
                                        }
                                        finishActivity(message);
                                    }
//                    if (index == 2002) {
//                        finish();
//                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        };

                        LocalBroadcastManager.getInstance(QDAudioActivity.this)
                                .registerReceiver(receiver, new IntentFilter("meeting.event"));
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        GILogUtil.i("onDeny" + "1");

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                    }
                });


    }

    private void finishActivity(QDMessage message) {
        GISharedPreUtil.setValue(getActivity(),"isInCall",false);
        Intent intent = new Intent(QDAVActivity.ACTION_ON_AV_END);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, message);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("KeyEvent", "KeyUp has been triggered on the view" + keyCode);
        // If back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("KeyEvent", "KeyUp has been triggered on the view KEYCODE_BACK" + keyCode);
            systemWebView.loadUrl("javascript:cordova.fireDocumentEvent('backbutton');");
            return true;
        }
        // Legacy
        else if (keyCode == KeyEvent.KEYCODE_MENU) {
            systemWebView.loadUrl("javascript:cordova.fireDocumentEvent('menubutton');");
            return true;
        }
        // If search key
        else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            systemWebView.loadUrl("javascript:cordova.fireDocumentEvent('searchbutton');");
            return true;
        }
        return false;
    }


    // native 主动退出会议室，发消息给会议室
    public void startService(View view) {

        final Intent intent = new Intent("527meeting");

        Bundle b = new Bundle();
        b.putString("userdata", "{ data: 'exitRoom'}");
        intent.putExtras(b);

        LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
    }


    @Override
    public void startActivityForResult(CordovaPlugin plugin, Intent intent, int i) {
        this.activityResultCallback = plugin;
        this.activityResultKeepRunning = this.keepRunning;

        // If multitasking turned on, then disable it for activities that return results
        if (plugin != null) {
            this.keepRunning = false;
        }

        // Start activity
        super.startActivityForResult(intent, i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        cordovaWebView.handleDestroy();
//        cordovaWebView.clearHistory();
//        cordovaWebView.stopLoading();
//        cordovaWebView = null;
//        systemWebView.clearHistory();
//        systemWebView.removeAllViews();
//        systemWebView.stopLoading();
//        systemWebView.destroy();
//        systemWebView = null;
    }

    @Override
    public void setActivityResultCallback(CordovaPlugin plugin) {
        this.activityResultCallback = plugin;
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public Object onMessage(String s, Object o) {
        return null;
    }

    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    @Override
    public void requestPermission(CordovaPlugin cordovaPlugin, int i, String s) {

    }

    @Override
    public void requestPermissions(CordovaPlugin cordovaPlugin, int i, String[] strings) {

    }

    @Override
    public boolean hasPermission(String s) {
        return true;
    }
}
