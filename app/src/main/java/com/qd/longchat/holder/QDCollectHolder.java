package com.qd.longchat.holder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/10 上午9:43
 */
public class QDCollectHolder {

    @BindView(R2.id.fl_collect_container)
    public FrameLayout itemContainer;
    @BindView(R2.id.tv_collect_info)
    public TextView itemInfo;

    public QDCollectHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
