package com.qd.longchat.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.longchat.base.util.QDLog;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.util.QDWXShare;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/12/6 下午1:14
 */
public class WXEntryActivity extends QDBaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QDLog.i("WXEntryActivity", "onCreate");
        api = QDWXShare.getInstance().getApi();

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            if (!api.handleIntent(getIntent(), this)) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        QDLog.i("WXEntryActivity","onNewIntent");
        setIntent(intent);
        if (!api.handleIntent(intent, this)) {
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Intent intent = new Intent(QDWXShare.ACTION_SHARE_RESPONSE);
        intent.putExtra(QDWXShare.EXTRA_RESULT, new QDWXShare.Response(baseResp));
        sendBroadcast(intent);
        finish();
    }

}
