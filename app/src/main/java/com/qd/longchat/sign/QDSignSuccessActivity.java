package com.qd.longchat.sign;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.util.QDDateUtil;


/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2017/12/11 下午7:56
 */

public class QDSignSuccessActivity extends QDBaseActivity {

    public final static String KEY_OF_TIME = "time";
    public final static String KEY_OF_ADDRESS = "address";
    public final static String KEY_OF_IS_MEETING = "isMeeting";

    private TextView tvDate;
    private TextView tvTime;
    private TextView tvAddress;
    private TextView tvMsg;
    private ImageView ivImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_activity_sign_success);

        initTitleView(findViewById(R.id.view_ss_tile));
        tvTitleBack.setVisibility(View.GONE);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText("关闭");

        tvDate = (TextView) findViewById(R.id.tv_ss_date);
        tvTime = (TextView) findViewById(R.id.tv_ss_time);
        tvAddress = (TextView) findViewById(R.id.tv_ss_address);
        tvMsg = (TextView) findViewById(R.id.tv_ss_msg);
        ivImg = (ImageView) findViewById(R.id.iv_ss_img);

        long time = getIntent().getLongExtra(KEY_OF_TIME, 0);
        String address = getIntent().getStringExtra(KEY_OF_ADDRESS);
        tvDate.setText("记录时间 " + QDDateUtil.longToString(time, QDDateUtil.MSG_FORMAT5));
        tvTime.setText(QDDateUtil.longToString(time, QDDateUtil.MSG_FORMAT6));
        tvAddress.setText("记录地点 " + address);

        boolean isMeeting = getIntent().getBooleanExtra(KEY_OF_IS_MEETING, false);
        if (isMeeting) {
            tvDate.setText("签到时间 " + QDDateUtil.longToString(time, QDDateUtil.MSG_FORMAT5));
            tvTime.setText(QDDateUtil.longToString(time, QDDateUtil.MSG_FORMAT6));
            tvAddress.setText("签到地点 " + address);
            tvMsg.setText("签到成功");
            ivImg.setBackgroundResource(R.mipmap.im_icon_meeting_ok);
        } else {
            tvDate.setText("记录时间 " + QDDateUtil.longToString(time, QDDateUtil.MSG_FORMAT5));
            tvTime.setText(QDDateUtil.longToString(time, QDDateUtil.MSG_FORMAT6));
            tvAddress.setText("记录地点 " + address);
            tvMsg.setText("记录成功");
            ivImg.setBackgroundResource(R.mipmap.im_icon_sign_ok);
        }

        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
