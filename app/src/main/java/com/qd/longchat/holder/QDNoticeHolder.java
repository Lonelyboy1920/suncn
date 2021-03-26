package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/3/9 上午11:52
 */
public class QDNoticeHolder {

    @BindView(R2.id.tv_time)
    public TextView tvTime;
    @BindView(R2.id.iv_icon)
    public ImageView ivIcon;
    @BindView(R2.id.tv_title)
    public TextView tvTitle;
    @BindView(R2.id.tv_sender_tag)
    public TextView tvSenderTag;
    @BindView(R2.id.tv_sender)
    public TextView tvSender;
    @BindView(R2.id.tv_sender_time)
    public TextView tvSenderTime;
    @BindView(R2.id.tv_content)
    public TextView tvContent;

    public QDNoticeHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
