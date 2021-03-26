package com.suncn.ihold_zxztc.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ObjAnnouncementBean;

import org.jetbrains.annotations.NotNull;

/**
 * 消息提醒adpter
 */
public class Message_Notice_RVAdapter extends BaseQuickAdapter<ObjAnnouncementBean, Message_Notice_RVAdapter.ViewHolder> {
    private Activity context;

    public Message_Notice_RVAdapter(Activity context) {
        super(R.layout.item_rv_message_notice);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ObjAnnouncementBean objInfo) {
        if (GIStringUtil.isNotBlank(objInfo.getStrPubDate())) {
            viewHolder.date_TextView.setText(objInfo.getStrPubDate());
            viewHolder.date_TextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.date_TextView.setVisibility(View.GONE);
        }
        viewHolder.type_TextView.setText(objInfo.getStrIssue());
        viewHolder.title_TextView.setText(objInfo.getStrTitle());
        viewHolder. content_TextView.setText(objInfo.getStrContent());
        boolean isShow = "0".equals(objInfo.getStrState()) ? true : false;
        if (isShow) {
            viewHolder.dot_TextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.dot_TextView.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView date_TextView;//时间
        private TextView type_TextView;//类型
        private TextView title_TextView;//标题
        private TextView content_TextView;//内容
        private TextView state_TextView;//左下角状态
        private TextView dot_TextView;//红点

        public ViewHolder(View itemView) {
            super(itemView);
            date_TextView = itemView.findViewById(R.id.tv_date);
            type_TextView = itemView.findViewById(R.id.tv_type);
            title_TextView = itemView.findViewById(R.id.tv_title);
            content_TextView = itemView.findViewById(R.id.tv_content);
            state_TextView = itemView.findViewById(R.id.tv_state);
            dot_TextView = itemView.findViewById(R.id.tv_dot);
        }

    }
}
