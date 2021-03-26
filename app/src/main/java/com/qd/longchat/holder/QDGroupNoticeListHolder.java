package com.qd.longchat.holder;

import android.view.View;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/15 下午1:48
 */
public class QDGroupNoticeListHolder {

    @BindView(R2.id.tv_item_desc)
    public TextView tvItemDesc;
    @BindView(R2.id.tv_item_sender)
    public TextView tvItemSender;

    public QDGroupNoticeListHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
