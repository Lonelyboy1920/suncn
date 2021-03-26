package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/16 上午11:44
 */
public class QDSelfFileHolder {

    @BindView(R2.id.ll_file_time_layout)
    public LinearLayout itemTimeLayout;
    @BindView(R2.id.tv_file_date)
    public TextView itemDate;
    @BindView(R2.id.iv_file_icon)
    public ImageView itemIcon;
    @BindView(R2.id.tv_file_name)
    public TextView itemName;
    @BindView(R2.id.tv_file_time)
    public TextView itemTime;
    @BindView(R2.id.tv_file_size)
    public TextView itemSize;
    @BindView(R2.id.tv_file_tag)
    public TextView itemTag;

    public QDSelfFileHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
