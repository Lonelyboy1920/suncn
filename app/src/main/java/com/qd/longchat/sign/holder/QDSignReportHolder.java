package com.qd.longchat.sign.holder;

import android.view.View;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2017/12/29 下午3:13
 */

public class QDSignReportHolder {

    @BindView(R2.id.tv_report_msg_time)
    public TextView tvMsgTime;
    @BindView(R2.id.tv_report_name)
    public TextView tvName;
    @BindView(R2.id.tv_report_time)
    public TextView tvTime;
    @BindView(R2.id.tv_report_address)
    public TextView tvAddress;

    public QDSignReportHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
