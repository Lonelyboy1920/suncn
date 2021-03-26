package com.qd.longchat.util;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/24 上午11:20
 */
public class QDAtClickSpan extends ClickableSpan {

    private int color;
    private String userId;
    private String userName;
    private Context context;

    public QDAtClickSpan(Context context, int color, String userId, String userName) {
        this.context = context;
        this.color = color;
        this.userId = userId;
        this.userName = userName;
    }

    @Override
    public void onClick(View widget) {
//        Intent intent = new Intent(context, QDUserDetailActivity.class);
//        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, userId);
//        context.startActivity(intent);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setColor(color);
    }

    public String getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return userName;
    }
}
