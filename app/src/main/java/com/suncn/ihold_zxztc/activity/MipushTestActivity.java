package com.suncn.ihold_zxztc.activity;

import android.content.Intent;
import android.os.Bundle;

import com.umeng.message.UmengNotifyClickActivity;

/**
 * 小米/华为渠道推送跳转的Activity
 */
public class MipushTestActivity extends UmengNotifyClickActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent intent1 = new Intent(this, SplashActivity.class);
        startActivity(intent1);
        finish();
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
//        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
//        TextView textView = new TextView(this);
//        textView.setText(body);
//        setContentView(textView);
    }
}