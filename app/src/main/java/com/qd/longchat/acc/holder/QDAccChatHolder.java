package com.qd.longchat.acc.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/27 下午5:44
 */
public class QDAccChatHolder {

    @BindView(R2.id.tv_acc_msg_time)
    public TextView tvMsgTime;
    @BindView(R2.id.ll_acc_container)
    public LinearLayout llContainerLayout;

    public QDAccChatHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
