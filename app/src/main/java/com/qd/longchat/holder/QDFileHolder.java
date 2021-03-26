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
 * @creatTime 2019/3/12 上午10:14
 */
public class QDFileHolder {

    @BindView(R2.id.iv_file_icon)
    public ImageView ivFileIcon;
    @BindView(R2.id.tv_file_name)
    public TextView tvFileName;
    @BindView(R2.id.tv_file_size)
    public TextView tvFileSize;
    @BindView(R2.id.tv_file_time)
    public TextView tvFileTime;

    public QDFileHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
