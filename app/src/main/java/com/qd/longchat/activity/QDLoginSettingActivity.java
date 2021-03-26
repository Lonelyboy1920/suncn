package com.qd.longchat.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.longchat.base.util.QDStringUtil;
import com.qd.longchat.R;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDXEditText;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/14 下午1:46
 */

public class QDLoginSettingActivity extends AppCompatActivity {

    private final static int MAX_PORT = 65535;

    @BindView(R2.id.view_ls_title)
    View viewTitle;
    @BindView(R2.id.et_ls_server)
    QDXEditText etServer;
    @BindView(R2.id.et_ls_port)
    QDXEditText etPort;
    @BindView(R2.id.btn_ls_save)
    Button btnSave;

    @BindString(R2.string.ls_setting)
    String strTitle;
    @BindString(R2.string.ls_save)
    String strSave;
    @BindString(R2.string.ls_toast_server_ip_empty)
    String strServerIpEmpty;
    @BindString(R2.string.ls_toast_server_port_empty)
    String strServerPortEmpty;

    private TextView titleBack, titleName, titleRight;

    private Context context;

    private int portCount, serverCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login_setting);
        context = this;
        ButterKnife.bind(this);

        initTitleView();
        titleBack.setVisibility(View.VISIBLE);
        titleName.setText(strTitle);

        String address = QDLanderInfo.getInstance().getAddress();
        int port = QDLanderInfo.getInstance().getPort();
        if (!TextUtils.isEmpty(address)) {
            etServer.setText(address);
            etServer.setSelection(etServer.length());
        }
        if (port != 0) {
            etPort.setText(port + "");
            etPort.setSelection(etPort.length());
        }
    }

    private void initTitleView() {
        titleBack = viewTitle.findViewById(R.id.tv_title_back);
        titleName = viewTitle.findViewById(R.id.tv_title_name);
        titleRight = viewTitle.findViewById(R.id.tv_title_right);
    }

    @OnClick({R2.id.tv_title_back, R2.id.btn_ls_save})
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_title_back) {
            onBackPressed();

        } else if (i == R.id.btn_ls_save) {
            saveServerInfo();

        }
    }

    @OnTextChanged(R2.id.et_ls_port)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        portCount = s.length();
        String text = s.toString();
        if (!TextUtils.isEmpty(text) && Integer.valueOf(text) > MAX_PORT) {
            etPort.setText(MAX_PORT + "");
            etPort.setSelection(etPort.length());
        }
        changeButtonColor(portCount, serverCount);
    }

    @OnTextChanged(R2.id.et_ls_server)
    public void onServerTextChanged(CharSequence s, int start, int before, int count) {
        serverCount = s.length();
        changeButtonColor(portCount, serverCount);
    }

    private void changeButtonColor(int portCount, int serverCount) {
        if (portCount != 0 && serverCount != 0 && QDStringUtil.strToInt(etPort.getText().toString()) != 0) {
            btnSave.setEnabled(true);
            btnSave.setBackgroundResource(R.drawable.ic_rounded_rectangle_btn);
        } else {
            btnSave.setEnabled(false);
            btnSave.setBackgroundResource(R.drawable.ic_rounded_rectangle_unfouce_btn);
        }
    }

    private void saveServerInfo() {
        String serverIp = etServer.getText().toString();
        String serverPort = etPort.getText().toString();
        if (TextUtils.isEmpty(serverIp)) {
            QDUtil.showToast(context, strServerIpEmpty);
            return;
        }

        if (!TextUtils.isDigitsOnly(serverPort)) {
            QDUtil.showToast(context, strServerPortEmpty);
            return;
        }
        QDLanderInfo.getInstance().setAddress(serverIp);
        QDLanderInfo.getInstance().setPort(QDStringUtil.strToInt(serverPort));
        QDLanderInfo.getInstance().save();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
