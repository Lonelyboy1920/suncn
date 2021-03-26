package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MeetingSpeakListBean;

import org.jetbrains.annotations.NotNull;

/**
 * 会议发言列表
 */
public class MangerMeetSpeakListAdapter extends BaseQuickAdapter<MeetingSpeakListBean.MeetingSpeakBean, MangerMeetSpeakListAdapter.ViewHolder> {
    private Context context;
    private int intType;

    public MangerMeetSpeakListAdapter(Context context, int intType) {
        super(R.layout.view_item_manger_meet_speak);
        this.context = context;
        this.intType = intType;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, MeetingSpeakListBean.MeetingSpeakBean objInfo) {

        viewHolder.title_TextView.setText(objInfo.getStrTitle());
        viewHolder.name_TextView.setText(objInfo.getStrSourceName());

        String strState = GIStringUtil.nullToEmpty(objInfo.getStrStateName());
        if (GIStringUtil.isNotBlank(strState)) {
            viewHolder.date_TextView.setText(objInfo.getStrPubDate());
            viewHolder.state_TextView.setText(strState);
        } else {
            viewHolder.date_TextView.setVisibility(View.INVISIBLE);
            viewHolder.state_TextView.setText(objInfo.getStrPubDate());
        }

    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;
        private GITextView name_TextView;
        private GITextView date_TextView;
        private GITextView state_TextView;


        public ViewHolder(View itemView) {
            super(itemView);
            title_TextView = (TextView) itemView.findViewById(R.id.tv_title);
            name_TextView = (GITextView) itemView.findViewById(R.id.tv_name);
            date_TextView = (GITextView) itemView.findViewById(R.id.tv_date);
            state_TextView = (GITextView) itemView.findViewById(R.id.tv_state);

        }


    }
}
