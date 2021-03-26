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
import butterknife.OnClick;
import com.qd.longchat.R2;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/20 下午1:37
 */
public class QDSetMsgTextSizeActivity extends QDBaseActivity {

    @BindView(R2.id.view_ts_title)
    View viewTitle;
    @BindView(R2.id.view_ts_small)
    View viewSmall;
    @BindView(R2.id.view_ts_normal)
    View viewNormal;
    @BindView(R2.id.view_ts_large)
    View viewLarge;

    @BindString(R2.string.text_size_title)
    String strTitle;
    @BindString(R2.string.text_size_small)
    String strSmall;
    @BindString(R2.string.text_size_normal)
    String strNormal;
    @BindString(R2.string.text_size_large)
    String strLarge;

    private ImageView ivSmallIcon, ivNormalIcon, ivLargeIcon;

    private int textSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_msg_text_size);
        ButterKnife.bind(this);
        textSize = QDSDKConfig.getInstance().getMsgTextSize();
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        initSmall();
        initNormal();
        initLarge();
        initIcon();
    }

    private void initSmall() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewSmall);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
        holder.tvItemName.setText(strSmall);
        holder.tvItemName.setTextSize(QDSDKConfig.SIZE_SMALL);
        ivSmallIcon = holder.ivItemArrow;
    }

    private void initNormal() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewNormal);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
        holder.tvItemName.setText(strNormal);
        holder.tvItemName.setTextSize(QDSDKConfig.SIZE_NORMAL);
        ivNormalIcon = holder.ivItemArrow;
    }

    private void initLarge() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewLarge);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        holder.ivItemLine.setVisibility(View.GONE);
        holder.tvItemName.setText(strLarge);
        holder.tvItemName.setTextSize(QDSDKConfig.SIZE_LARGE);
        ivLargeIcon = holder.ivItemArrow;
    }

    private void initIcon() {
        if (textSize == QDSDKConfig.SIZE_SMALL) {
            ivSmallIcon.setImageResource(R.drawable.ic_tick);
            ivNormalIcon.setImageResource(0);
            ivLargeIcon.setImageResource(0);
        } else if (textSize == QDSDKConfig.SIZE_NORMAL) {
            ivNormalIcon.setImageResource(R.drawable.ic_tick);
            ivSmallIcon.setImageResource(0);
            ivLargeIcon.setImageResource(0);
        } else {
            ivLargeIcon.setImageResource(R.drawable.ic_tick);
            ivNormalIcon.setImageResource(0);
            ivSmallIcon.setImageResource(0);
        }
    }

    @OnClick({R2.id.view_ts_small, R2.id.view_ts_normal, R2.id.view_ts_large})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.view_ts_small) {
            if (textSize != QDSDKConfig.SIZE_SMALL) {
                textSize = QDSDKConfig.SIZE_SMALL;
                initIcon();
                QDSDKConfig.getInstance().setConfigMsgTextSize(QDSDKConfig.SIZE_SMALL);
            }

        } else if (i == R.id.view_ts_normal) {
            if (textSize != QDSDKConfig.SIZE_NORMAL) {
                textSize = QDSDKConfig.SIZE_NORMAL;
                initIcon();
                QDSDKConfig.getInstance().setConfigMsgTextSize(QDSDKConfig.SIZE_NORMAL);
            }

        } else if (i == R.id.view_ts_large) {
            if (textSize != QDSDKConfig.SIZE_LARGE) {
                textSize = QDSDKConfig.SIZE_LARGE;
                initIcon();
                QDSDKConfig.getInstance().setConfigMsgTextSize(QDSDKConfig.SIZE_LARGE);
            }

        }
    }
}
