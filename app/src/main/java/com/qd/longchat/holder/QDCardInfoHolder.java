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
 * @creatTime 2018/8/14 下午4:54
 */
public class QDCardInfoHolder {

    @BindView(R2.id.tv_self_company)
    public TextView itemCompany;
    @BindView(R2.id.tv_self_name)
    public TextView itemName;
    @BindView(R2.id.tv_self_dept)
    public TextView itemDept;
    @BindView(R2.id.tv_self_mobile)
    public TextView itemMobile;
    @BindView(R2.id.iv_self_icon)
    public ImageView itemIcon;

    public QDCardInfoHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
