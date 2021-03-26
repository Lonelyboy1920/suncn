package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MeetSpeakNoticeListBean;

import org.jetbrains.annotations.NotNull;

/**
 * 会议发言列表
 */
public class MeetSpeak_Notice_RVAdapter extends BaseQuickAdapter<MeetSpeakNoticeListBean.MeetSpeakNoticeBean, MeetSpeak_Notice_RVAdapter.ViewHolder> {
    private Context context;

    public MeetSpeak_Notice_RVAdapter(Context context) {
        super(R.layout.item_rv_meetspeak_notice);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, MeetSpeakNoticeListBean.MeetSpeakNoticeBean objInfo) {
        boolean isShow = "1".equals(objInfo.getStrState()) ? false : true;
        if (isShow) {
            viewHolder.dot_View.setVisibility(View.VISIBLE);
        } else {
            viewHolder. dot_View.setVisibility(View.GONE);
        }
        viewHolder. title_TextView.setText(objInfo.getStrTitle());
        viewHolder.date_TextView.setText(objInfo.getStrPubDate());
        String strState = objInfo.getStrPubUnit();
        viewHolder. state_TextView.setText(strState);
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;
        private View dot_View;
        private TextView date_TextView;
        private TextView state_TextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dot_View = itemView.findViewById(R.id.tv_dot);
            title_TextView = (TextView) itemView.findViewById(R.id.tv_title);
            date_TextView = (TextView) itemView.findViewById(R.id.tv_date);
            state_TextView = (TextView) itemView.findViewById(R.id.tv_state);

        }

    }
}
