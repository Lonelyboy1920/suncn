package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MeetSpeakListBean;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 会议发言列表
 */
public class MeetSpeak_Main_RVAdapter extends BaseQuickAdapter<MeetSpeakListBean.MeetSpeakBean, MeetSpeak_Main_RVAdapter.ViewHolder> {
    private Context context;

    public MeetSpeak_Main_RVAdapter(Context context) {
        super(R.layout.item_rv_publicopinion);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, MeetSpeakListBean.MeetSpeakBean objInfo) {
        if (GIStringUtil.isBlank(objInfo.getStrMeetName())) {
            viewHolder.meetName_TextView.setVisibility(View.GONE);
        } else {
            viewHolder.meetName_TextView.setVisibility(View.VISIBLE);
            viewHolder.meetName_TextView.setText(objInfo.getStrMeetName());
        }
        viewHolder.title_TextView.setText(objInfo.getStrTitle());
        viewHolder.date_TextView.setText(objInfo.getStrPubDate());
        String strState = objInfo.getStrStateName();
        viewHolder.state_TextView.setText(strState);
        int textColor = context.getResources().getColor(R.color.red);
        if (context.getString(R.string.string_under_review).equals(strState)) {
            textColor = context.getResources().getColor(R.color.font_title);
        } else if (context.getString(R.string.string_unuse).equals(strState)) {
            textColor = context.getResources().getColor(R.color.red);
        } else if (context.getString(R.string.string_oral_statement).equals(strState) || context.getString(R.string.string_written_speech).equals(strState)) {
            textColor = context.getResources().getColor(R.color.zxta_state_green);
        } else {
            textColor = context.getResources().getColor(R.color.red);
        }
        viewHolder.state_TextView.setTextColor(textColor);
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;
        private TextView date_TextView;
        private TextView state_TextView;
        private TextView meetName_TextView;

        public ViewHolder(View itemView) {
            super(itemView);
            title_TextView = (TextView) itemView.findViewById(R.id.tv_title);
            date_TextView = (TextView) itemView.findViewById(R.id.tv_date);
            state_TextView = (TextView) itemView.findViewById(R.id.tv_state);
            meetName_TextView = (TextView) itemView.findViewById(R.id.tv_meet_name);
        }

    }
}
