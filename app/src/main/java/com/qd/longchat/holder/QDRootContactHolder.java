package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/29 下午3:14
 */

public class QDRootContactHolder {

    public TextView itemIndex;
    public ImageView itemCheck;
    public ImageView itemIcon;
    public TextView itemName;
    public TextView itemSubname;
    public TextView itemRightName;
    public TextView itemFunc;
    public ImageView itemLine;
    public TextView itemTitle;
    public ImageView itemPhone;

    public static QDRootContactHolder init(View view) {
        QDRootContactHolder holder = new QDRootContactHolder();
        holder.itemIndex = view.findViewById(R.id.tv_item_index);
        holder.itemCheck = view.findViewById(R.id.iv_item_check);
        holder.itemIcon = view.findViewById(R.id.iv_item_icon);
        holder.itemName = view.findViewById(R.id.tv_item_name);
        holder.itemSubname = view.findViewById(R.id.tv_item_subname);
        holder.itemRightName = view.findViewById(R.id.tv_item_right_name);
        holder.itemFunc = view.findViewById(R.id.tv_item_func);
        holder.itemLine = view.findViewById(R.id.iv_item_line);
        holder.itemTitle = view.findViewById(R.id.tv_item_txt);
        holder.itemPhone = view.findViewById(R.id.iv_item_phone);
        return holder;
    }
}
