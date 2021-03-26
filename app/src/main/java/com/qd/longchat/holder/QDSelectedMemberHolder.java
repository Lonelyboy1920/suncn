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
 * @creatTime 2018/7/3 下午2:05
 */
public class QDSelectedMemberHolder {

    @BindView(R2.id.iv_item_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_item_name)
    public TextView tvItemName;
    @BindView(R2.id.tv_item_remove)
    public TextView tvItemRemove;

    public QDSelectedMemberHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
