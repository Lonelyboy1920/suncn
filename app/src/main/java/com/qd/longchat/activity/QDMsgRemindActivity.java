package com.qd.longchat.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.longchat.base.config.QDSDKConfig;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDBaseInfoHolder;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/20 下午2:13
 */
public class QDMsgRemindActivity extends QDBaseActivity {

    @BindView(R2.id.view_remind_title)
    View viewTitle;
    @BindView(R2.id.view_remind_sound)
    View viewSound;
    @BindView(R2.id.view_remind_vibrate)
    View viewVibrate;

    @BindString(R2.string.setting_msg_remind)
    String strTitle;
    @BindString(R2.string.remind_sound)
    String strSound;
    @BindString(R2.string.remind_vibrate)
    String strVibrate;

    private boolean isHaveSound, isHaveVibrate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_remind);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        initSound();
        initVibrate();
    }

    private void initSound() {
        final QDBaseInfoHolder holder = new QDBaseInfoHolder(viewSound);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
        holder.tvItemName.setText(strSound);
        isHaveSound = QDSDKConfig.getInstance().isHaveSound();
        isOpen(holder.ivItemArrow, isHaveSound);

        holder.ivItemArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHaveSound) {
                    isHaveSound = false;
                } else {
                    isHaveSound = true;
                }
                isOpen(holder.ivItemArrow, isHaveSound);
                QDSDKConfig.getInstance().setConfigSound(isHaveSound);
            }
        });
    }

    private void initVibrate() {
        final QDBaseInfoHolder holder = new QDBaseInfoHolder(viewVibrate);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemLine.setVisibility(View.GONE);
        holder.tvItemName.setText(strVibrate);
        isHaveVibrate = QDSDKConfig.getInstance().isHaveVibrate();
        isOpen(holder.ivItemArrow, isHaveVibrate);

        holder.ivItemArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHaveVibrate) {
                    isHaveVibrate = false;
                } else {
                    isHaveVibrate = true;
                }
                isOpen(holder.ivItemArrow, isHaveVibrate);
                QDSDKConfig.getInstance().setConfigVibrate(isHaveVibrate);
            }
        });
    }

    private void isOpen(ImageView imageView, boolean b) {
        if (b) {
            imageView.setImageResource(R.mipmap.im_switcher_open);
        } else {
            imageView.setImageResource(R.mipmap.im_switcher_close);
        }
    }
}
