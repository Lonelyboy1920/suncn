package com.suncn.ihold_zxztc.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MessageRemindListBean;

import org.jetbrains.annotations.NotNull;

/**
 * 消息提醒adpter
 */
public class Message_Remind_RVAdapter extends BaseQuickAdapter<MessageRemindListBean.MessageRemindBean , Message_Remind_RVAdapter.ViewHolder> {
    private Activity context;

    public Message_Remind_RVAdapter(Activity context) {
        super(R.layout.item_rv_message_remind);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, MessageRemindListBean.MessageRemindBean  objInfo) {
        baseViewHolder.date_TextView.setText(objInfo.getStrDistanceTime());
        baseViewHolder.type_TextView.setText(objInfo.getStrMsgTypeName());
        baseViewHolder.content_TextView.setText(objInfo.getStrMsgContent());
        baseViewHolder.state_TextView.setText(objInfo.getStrButtonName());
        boolean isShow = "0".equals(objInfo.getStrMobileState()) ? true : false;
        if (isShow) {
            baseViewHolder.dot_TextView.setVisibility(View.VISIBLE);
        } else {
            baseViewHolder.dot_TextView.setVisibility(View.GONE);
        }
        String strAffirType = objInfo.getStrAffirType();//01 提案 02 会议 03 活动 04 社情民意 05 刊物 06 大会发言
        if (strAffirType==null){
            strAffirType="";
        }
        switch (strAffirType) {
            case "01":
                baseViewHolder.notice_ImageView.setBackground(context.getResources().getDrawable(R.mipmap.notice_ta));
                break;
            case "02":
                baseViewHolder.notice_ImageView.setBackground(context.getResources().getDrawable(R.mipmap.notice_hytz));
                break;
            case "03":
                baseViewHolder. notice_ImageView.setBackground(context.getResources().getDrawable(R.mipmap.notice_hdtz));
                break;
            case "04":
                baseViewHolder.notice_ImageView.setBackground(context.getResources().getDrawable(R.mipmap.notice_sqmy));
                break;
            case "05":
                baseViewHolder.notice_ImageView.setBackground(context.getResources().getDrawable(R.mipmap.notice_kw));
                break;
            case "06":
                baseViewHolder. notice_ImageView.setBackground(context.getResources().getDrawable(R.mipmap.notice_dhfy));
                break;
            default:
                baseViewHolder.notice_ImageView.setBackground(context.getResources().getDrawable(R.mipmap.notice_hytz));
                break;
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView date_TextView;//时间
        private TextView type_TextView;//类型
        private TextView content_TextView;//内容
        private TextView state_TextView;//左下角状态
        private TextView dot_TextView;//红点
        private LinearLayout state_LinearLayout;//下方LinearLayout
        private ImageView notice_ImageView;//左上角图标

        public ViewHolder(View itemView) {
            super(itemView);
            date_TextView = itemView.findViewById(R.id.tv_date);
            state_LinearLayout = itemView.findViewById(R.id.ll_state);
            type_TextView = itemView.findViewById(R.id.tv_type);
            content_TextView = itemView.findViewById(R.id.tv_content);
            state_TextView = itemView.findViewById(R.id.tv_state);
            dot_TextView = itemView.findViewById(R.id.tv_dot);
            notice_ImageView = itemView.findViewById(R.id.icon_notice);
        }
    }
}
