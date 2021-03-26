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
 * @creatTime 2018/5/4 下午3:05
 */

public class QDBucketHolder {
    @BindView(R2.id.bucket_icon)
    public ImageView icon;
    @BindView(R2.id.bucket_name)
    public TextView name;
    @BindView(R2.id.bucket_right_icon)
    public ImageView rightIcon;

    public QDBucketHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
