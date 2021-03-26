package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/4 上午10:24
 */

public class QDPhotoSelectHolder {

    @BindView(R2.id.photo)
    public ImageView photo;
    @BindView(R2.id.photo_select)
    public ImageView photoSelect;

    public QDPhotoSelectHolder(View view) {
        ButterKnife.bind(this, view);
    }

}
