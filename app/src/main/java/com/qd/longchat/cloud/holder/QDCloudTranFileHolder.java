package com.qd.longchat.cloud.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/7 下午1:47
 */
public class QDCloudTranFileHolder {

    @BindView(R2.id.ll_item_title_layout)
    public LinearLayout itemTitleLayout;
    @BindView(R2.id.tv_item_title)
    public TextView tvItemTitle;
    @BindView(R2.id.tv_item_right)
    public TextView tvItemRight;
    @BindView(R2.id.iv_item_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_item_name)
    public TextView tvItemName;
    @BindView(R2.id.tv_item_info)
    public TextView tvItemInfo;
    @BindView(R2.id.pb_item_progress)
    public ProgressBar pbItemBar;

    public QDCloudTranFileHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
