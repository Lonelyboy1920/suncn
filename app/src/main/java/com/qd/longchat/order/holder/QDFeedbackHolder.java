package com.qd.longchat.order.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/27 下午4:17
 */
public class QDFeedbackHolder {

    @BindView(R2.id.iv_item_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_item_name)
    public TextView tvItemName;
    @BindView(R2.id.tv_item_state)
    public TextView tvItemState;
    @BindView(R2.id.tv_item_time)
    public TextView tvItemTime;
    @BindView(R2.id.tv_item_content)
    public TextView tvItemContent;

    public QDFeedbackHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
