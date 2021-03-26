package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/4/13 下午6:05
 */

public class QDWorkHolder {

    @BindView(R2.id.tv_work_name)
    public TextView itemName;
    @BindView(R2.id.iv_work_icon)
    public ImageView itemIcon;

    public QDWorkHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
