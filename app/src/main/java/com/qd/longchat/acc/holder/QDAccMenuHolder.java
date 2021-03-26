package com.qd.longchat.acc.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/27 下午3:05
 */
public class QDAccMenuHolder {
    @BindView(R2.id.text)
    public TextView textView;
    @BindView(R2.id.image)
    public ImageView imageView;

    public QDAccMenuHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
