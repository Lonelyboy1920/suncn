package com.suncn.ihold_zxztc.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.adapter.LoginSettingListviewAdapter;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.ArrayList;
import java.util.Map;

/**
 * 登陆界面的服务器设置对话框
 *
 * @author Administrator
 */
public class Login_SettingDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private TextView send_Button;
    private TextView cancle_TextView;
    private ClearEditText server_EditText;
    private ClearEditText port_EditText;
    private TextView deviceCode_TextView;
    private TextView serverDrop_ImageView;
    private String spfName = "SERVER_SETTING";
    private SharedPreferences spf;
    private Map<String, String> map;
    private ArrayList<String> serveList;
    private ArrayList<String> portList;
    private int count = 0;
    private PopupWindow popWindow;
    private LoginSettingListviewAdapter loginSettingListviewAdapter;
    private LinearLayout server_LinearLayout;

    public Login_SettingDialog(Activity activity) {
        super(activity, R.style.style_dialog);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_setting);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        send_Button.setOnClickListener(this);
        cancle_TextView.setOnClickListener(this);
        serverDrop_ImageView.setOnClickListener(this);
    }

    private void initView() {
        send_Button = findViewById(R.id.btn_dialog_send);
        cancle_TextView = findViewById(R.id.tv_dialog_cancle);
        server_EditText = findViewById(R.id.et_server);
        port_EditText = findViewById(R.id.et_port);
        deviceCode_TextView = findViewById(R.id.tv_deviceCode);
        serverDrop_ImageView = findViewById(R.id.iv_serve_drop);
        server_LinearLayout = findViewById(R.id.ll_serve);
    }

    private void initData() {
        cancle_TextView.setText(GIUtil.showHtmlInfo("<u>取消</u>"));
        String a=GISharedPreUtil.getString(activity, "server");
        String b=GISharedPreUtil.getString(activity, "port");
        server_EditText.setText(GISharedPreUtil.getString(activity, "server"));
        port_EditText.setText(GISharedPreUtil.getString(activity, "port"));
        deviceCode_TextView.setText(GISharedPreUtil.getString(activity, "deviceCode"));
        spf = getContext().getSharedPreferences(spfName, Context.MODE_PRIVATE);
        map = (Map<String, String>) spf.getAll();
        serveList = new ArrayList<String>();
        portList = new ArrayList<String>();
        if (map != null && map.size() > 0) {
            for (int i = 0; i < map.size() / 2; i++) {
                String server = spf.getString("serverName" + i, "");
                String port = spf.getString("serverPort" + i, "");
                serveList.add(server);
                portList.add(port);
            }
            count = map.size() / 2;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_cancle:
                dismiss();
                break;
            case R.id.btn_dialog_send:
                String server = getEditText(server_EditText);
                String port = getEditText(port_EditText);
                if (GIStringUtil.isEmpty(server)) {
                    server_EditText.requestFocus();
                    GIToastUtil.showMessage(activity, "请输入OA服务器地址！");
                } else if (GIStringUtil.isEmpty(port)) {
                    port_EditText.requestFocus();
                    GIToastUtil.showMessage(activity, "请输入OA服务器端口！");
                } else {
                    GISharedPreUtil.setValue(activity, "server", server);
                    GISharedPreUtil.setValue(activity, "port", port);
                    setSettingData(server, port);
                    dismiss();
                    GIToastUtil.showMessage(activity, "服务器配置完成！");
                    if (server.equals(GISharedPreUtil.getString(activity, "server")) && port.equals(GISharedPreUtil.getString(activity, "port"))) {

                    } else {
                        GISharedPreUtil.setValue(activity, "strSid", "");
                        GISharedPreUtil.setValue(activity,"isHasLogin",false);
                        ApiManager.refreshRetrofit();
                    }
                }
                break;
            case R.id.iv_serve_drop:
                if (serveList != null && serveList.size() > 0)
                    initPopWindow();
                break;
            default:
                break;
        }
    }

    /**
     * 存储服务器配置
     */
    private void setSettingData(String server, String port) {
        if (serveList != null && serveList.size() > 0) {
            for (int i = 0; i < serveList.size(); i++) {
                String sv = serveList.get(i);
                if (sv.equals(server)) { // 若已存在则直接替换
                    spf.edit().putString("serverName" + i, server).commit();
                    spf.edit().putString("serverPort" + i, port).commit();
                    return;
                }
            }
        }
        if (GIStringUtil.isNotEmpty(server)) {
            spf.edit().putString("serverName" + count, server).commit();
            spf.edit().putString("serverPort" + count, port).commit();
            if (count >= 5) {
                map = (Map<String, String>) spf.getAll();
                serveList = new ArrayList<String>();
                portList = new ArrayList<String>();
                if (map != null && map.size() > 0) {
                    for (int i = 0; i < map.size() / 2; i++) {
                        String server1 = spf.getString("serverName" + i, "");
                        String port1 = spf.getString("serverPort" + i, "");
                        serveList.add(server1);
                        portList.add(port1);
                    }
                    count = map.size() / 2;
                    deleteHistory(1); //
                }
            } else {
                count = count + 1;
            }
        }
    }

    private String getEditText(ClearEditText editText) {
        return editText.getText().toString().trim();
    }

    /**
     * 服务器地址下拉框
     */
    private void initPopWindow() {
        if (popWindow == null) {
            View v = getLayoutInflater().inflate(R.layout.view_popwindow_doc_type, null);
            ListView settingListview = v.findViewById(R.id.lv_login_setting);
            settingListview.setVisibility(View.VISIBLE);
            ListView typeListview = v.findViewById(R.id.lv_type);
            typeListview.setVisibility(View.GONE);
            loginSettingListviewAdapter = new LoginSettingListviewAdapter(activity, serveList, portList, this);
            settingListview.setAdapter(loginSettingListviewAdapter);
            settingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String serverName = parent.getItemAtPosition(position).toString();
                    String serverPort = spf.getString("serverPort" + position, "");
                    server_EditText.setText(serverName);
                    port_EditText.setText(serverPort);
                    popWindow.dismiss();
                }
            });
            popWindow = new PopupWindow(v, server_LinearLayout.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
            popWindow.setFocusable(true);
            popWindow.setBackgroundDrawable(new BitmapDrawable());// 点击窗口外消失
            popWindow.setOutsideTouchable(true);
            popWindow.setOnDismissListener(new popWindowDismissListener()); //添加pop窗口关闭事件
        }
        popWindow.showAsDropDown(server_LinearLayout);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     */
    class popWindowDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    public void deleteHistory(int pos) {
        if (popWindow != null)
            popWindow.dismiss();
        for (int i = 0; i < serveList.size(); i++) {
            if (i == pos) {
                String server = serveList.get(i);
                if (server != null && server.equals(getEditText(server_EditText))) {
                    server_EditText.setText("");
                    port_EditText.setText("");
                }
                serveList.remove(i);
                portList.remove(i);
                if (loginSettingListviewAdapter != null)
                    loginSettingListviewAdapter.notifyDataSetChanged();
                break;
            }
        }
        spf.edit().clear().commit(); // 清除文本的数据
        for (int j = 0; j < serveList.size(); j++) {
            spf.edit().putString("serverName" + j, serveList.get(j)).commit();
            spf.edit().putString("serverPort" + j, portList.get(j)).commit();
        }
        map = (Map<String, String>) spf.getAll();
        count = map.size() / 2;
    }
}
