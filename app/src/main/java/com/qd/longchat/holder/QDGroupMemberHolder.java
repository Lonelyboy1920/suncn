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
 * @creatTime 2018/6/2 下午4:56
 */
public class QDGroupMemberHolder {

    @BindView(R2.id.iv_item_icon)
    public ImageView itemIcon;
    @BindView(R2.id.tv_item_name)
    public TextView itemName;

    public QDGroupMemberHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
