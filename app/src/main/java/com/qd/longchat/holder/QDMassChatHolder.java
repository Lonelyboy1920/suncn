package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/2/27 上午10:43
 */
public class QDMassChatHolder {

    @BindView(R2.id.iv_chat_check)
    public ImageView ivCheck;
    @BindView(R2.id.tv_chat_msg_time)
    public TextView tvTime;
    @BindView(R2.id.iv_chat_icon)
    public ImageView ivIcon;
    @BindView(R2.id.ll_chat_container)
    public LinearLayout llContainer;

    public QDMassChatHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
