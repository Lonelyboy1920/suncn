package com.qd.longchat.order.holder;

import android.view.View;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/21 下午2:35
 */
public class QDOrderHolder {

    @BindView(R2.id.tv_item_title)
    public TextView itemTitle;
    @BindView(R2.id.tv_item_info)
    public TextView itemInfo;
    @BindView(R2.id.tv_item_content)
    public TextView itemContent;
    @BindView(R2.id.tv_item_unread)
    public TextView itemUnread;

    public QDOrderHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
