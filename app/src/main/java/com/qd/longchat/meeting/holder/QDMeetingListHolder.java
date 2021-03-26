package com.qd.longchat.meeting.holder;

import android.view.View;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/1/15 下午2:01
 */
public class QDMeetingListHolder {

    @BindView(R2.id.tv_item_title)
    public TextView tvItemTitle;
    @BindView(R2.id.tv_item_subname)
    public TextView tvItemSubname;
    @BindView(R2.id.tv_item_time)
    public TextView tvItemTime;
    @BindView(R2.id.tv_item_status)
    public TextView tvItemStatus;

    public QDMeetingListHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
