package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.qd.longchat.R;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/7 上午9:59
 */
public class QDChatHolder {

    public ImageView itemCheck;
    public TextView itemMsgTime;
    public ImageView itemChatIcon;
    public LinearLayout itemContainer;
    public RelativeLayout itemContentLayout;
    public TextView itemBurn;
    //left
    public TextView itemChatName;
    //right
    public TextView itemStatus;
    public SpinKitView itemProgress;
    public ImageView itemFailed;


    public static QDChatHolder init(View view) {
        QDChatHolder holder = new QDChatHolder();
        holder.itemCheck = view.findViewById(R.id.iv_chat_check);
        holder.itemMsgTime = view.findViewById(R.id.tv_chat_msg_time);
        holder.itemChatIcon = view.findViewById(R.id.iv_chat_icon);
        holder.itemContainer = view.findViewById(R.id.ll_chat_container);
        holder.itemContentLayout = view.findViewById(R.id.rl_chat_content_layout);
        holder.itemBurn = view.findViewById(R.id.tv_burn);

        holder.itemChatName = view.findViewById(R.id.tv_chat_name);

        holder.itemStatus = view.findViewById(R.id.tv_chat_status);
        holder.itemProgress = view.findViewById(R.id.view_chat_progress);
        holder.itemFailed = view.findViewById(R.id.iv_chat_failed);
        return holder;
    }
}
