package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/5 下午4:06
 */
public class QDSearchHolder {

    @BindView(R2.id.tv_search_title)
    public TextView tvItemTitle;
    @BindView(R2.id.iv_search_line1)
    public ImageView ivItemTileLine;
    @BindView(R2.id.iv_search_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_search_name)
    public TextView tvItemName;
    @BindView(R2.id.tv_search_subname)
    public TextView tvItemSubname;
    @BindView(R2.id.tv_search_time)
    public TextView tvItemTime;
    @BindView(R2.id.iv_search_line2)
    public ImageView ivItemContentLine;
    @BindView(R2.id.ll_search_more)
    public LinearLayout llItemMoreLayout;
    @BindView(R2.id.iv_search_space)
    public ImageView ivItemSpace;
    @BindView(R2.id.tv_item_more)
    public TextView tvItemMore;

    public QDSearchHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
