package com.qd.longchat.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.holder.QDBaseInfoHolder;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/20 下午2:03
 */
public class QDAboutActivity extends QDBaseActivity {

    @BindView(R2.id.view_about_title)
    View viewTitle;
    @BindView(R2.id.view_about_version)
    View viewVersion;

    @BindString(R2.string.setting_about)
    String strTitle;
    @BindString(R2.string.about_version)
    String strVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewVersion);
        holder.ivItemIcon.setVisibility(View.GONE);
        holder.ivItemLine.setVisibility(View.VISIBLE);
        holder.ivItemArrow.setVisibility(View.GONE);
        holder.ivItemLine.setVisibility(View.GONE);
        holder.tvItemName.setText(strVersion);

        tvTitleName.setText(strTitle);
        try {
            String version = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            holder.tvItemInfo.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
