package com.qd.longchat.holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/12 下午4:17
 */
public class QDFriendInviteHolder {

    @BindView(R2.id.iv_item_photo)
    public ImageView itemPhoto;
    @BindView(R2.id.tv_item_name)
    public TextView itemName;
    @BindView(R2.id.tv_item_info)
    public TextView itemInfo;
    @BindView(R2.id.iv_item_btn)
    public Button itemBtn;

    public QDFriendInviteHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
