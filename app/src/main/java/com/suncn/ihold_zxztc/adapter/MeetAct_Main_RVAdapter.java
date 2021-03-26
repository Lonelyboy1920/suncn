package com.suncn.ihold_zxztc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.meeting.Meet_ChildListActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_MainActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_MangerListActivity;
import com.suncn.ihold_zxztc.bean.MeetingActListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.view.MenuItemLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 会议/活动主界面列表adapter
 */
public class MeetAct_Main_RVAdapter extends BaseQuickAdapter<MeetingActListBean.ObjMeetingActBean, MeetAct_Main_RVAdapter.ViewHolder> {
    private Activity context;
    private int intKind;

    public MeetAct_Main_RVAdapter(int layoutResId, Activity context, int intKind) {
        super(layoutResId);
        this.context = context;
        this.intKind = intKind;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, MeetingActListBean.ObjMeetingActBean objInfo) {
        boolean isShow = ("3".equals(objInfo.getStrState()) || "4".equals(objInfo.getStrState())) && 1 == objInfo.getIntModify();
        if (intKind == DefineUtil.wdhd || intKind == DefineUtil.hdgl) {
            viewHolder.startDate_TextView.setLabel(context.getString(R.string.string_start_date_no_line));
            viewHolder.endDate_TextView.setLabel(context.getString(R.string.string_end_date_no_line));
            viewHolder.title_TextView.setLabel(context.getString(R.string.string_activity_name));
            viewHolder.place_TextView.setLabel(context.getString(R.string.string_activity_address));
        } else if (intKind == DefineUtil.wdhy || intKind == DefineUtil.hygl) {
            viewHolder.startDate_TextView.setLabel(context.getString(R.string.string_starting_time_no_line));
            viewHolder.endDate_TextView.setLabel(context.getString(R.string.string_end_time_no_line));
            viewHolder.title_TextView.setLabel(context.getString(R.string.string_meeting_name));
            viewHolder.place_TextView.setLabel(context.getString(R.string.string_meeting_address));
            if (ProjectNameUtil.isCZSZX(context) && GIStringUtil.isNotEmpty(objInfo.getStrSeat())) {
                viewHolder.llSeat.setVisibility(View.VISIBLE);
                viewHolder.tvSeat.setText(objInfo.getStrSeat());
            }
        }

        if (isShow) {
            viewHolder.right_TextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.right_TextView.setVisibility(View.GONE);
        }
        if (intKind == DefineUtil.zhxx) {
            if (isShow) {
                viewHolder.state_TextView.setVisibility(View.VISIBLE);
                viewHolder.right_TextView.setVisibility(View.VISIBLE);
                viewHolder.type_TextView.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.state_TextView.setVisibility(View.VISIBLE);
                viewHolder.type_TextView.setVisibility(View.GONE);
                viewHolder.right_TextView.setVisibility(View.GONE);
            }
        } else {
            viewHolder.type_TextView.setVisibility(View.VISIBLE);
            if (15 > objInfo.getStrType().length()) {
                viewHolder.type_TextView.setText(objInfo.getStrType());
            } else {
                viewHolder.type_TextView.setText(objInfo.getStrType().substring(0, 12) + "...");
            }
        }

        viewHolder.state_TextView.setText(objInfo.getStrState());
        viewHolder.title_TextView.setValue(objInfo.getStrName());
        viewHolder.startDate_TextView.setValue(objInfo.getStrStartDate());
        viewHolder.endDate_TextView.setValue(objInfo.getStrEndDate());
        viewHolder.place_TextView.setValue(objInfo.getStrPlace());
        viewHolder.detail_LinearLayout.setVisibility(View.GONE);
        viewHolder.state_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShow) {
                    if (intKind == DefineUtil.zhxx) {
                        ((Meet_ChildListActivity) context).setOnClick(objInfo, view, intKind);
                    } else {
                        ((MeetAct_MainActivity) context).setOnClick(objInfo, view, intKind);
                    }
                }
            }
        });
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView type_TextView;
        private TextView state_TextView;
        private MenuItemLayout title_TextView;
        private MenuItemLayout startDate_TextView;
        private MenuItemLayout endDate_TextView;
        private MenuItemLayout place_TextView;
        private LinearLayout detail_LinearLayout;
        private GITextView right_TextView;//箭头
        private LinearLayout llSeat;
        private TextView tvSeat;

        public ViewHolder(View itemView) {
            super(itemView);
            type_TextView = (TextView) itemView.findViewById(R.id.tv_type);
            state_TextView = (TextView) itemView.findViewById(R.id.tv_state);
            title_TextView = (MenuItemLayout) itemView.findViewById(R.id.tv_title);
            startDate_TextView = (MenuItemLayout) itemView.findViewById(R.id.tv_start_date);
            endDate_TextView = (MenuItemLayout) itemView.findViewById(R.id.tv_end_date);
            detail_LinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_child_detail);
            place_TextView = (MenuItemLayout) itemView.findViewById(R.id.tv_place);
            right_TextView = (GITextView) itemView.findViewById(R.id.tv_right);
            llSeat = itemView.findViewById(R.id.llSeat);
            tvSeat = itemView.findViewById(R.id.tvSeat);
        }

    }
}
