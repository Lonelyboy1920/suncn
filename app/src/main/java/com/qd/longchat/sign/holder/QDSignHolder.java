package com.qd.longchat.sign.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/24 下午4:27
 */
public class QDSignHolder {

    @BindView(R2.id.iv_record_photo)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_record_name)
    public TextView tvItemName;
    @BindView(R2.id.tv_record_address)
    public TextView tvItemAddress;
    @BindView(R2.id.tv_record_time)
    public TextView tvItemTime;
    @BindView(R2.id.tv_record_note)
    public TextView tvItemNote;
    @BindView(R2.id.tv_record_receivers)
    public TextView tvItemReveivers;
    @BindView(R2.id.iv_record_img)
    public ImageView ivItemImg;

    public QDSignHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
