package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_DetailActivity;
import com.suncn.ihold_zxztc.bean.PerformDutyBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;

import java.util.ArrayList;


/**
 * 评论与回复列表的适配器
 */
public class PerformDutyChildAdapter extends BaseExpandableListAdapter {
    private ArrayList<PerformDutyBean.DutyList> dutyLists;
    private Context context;

    public PerformDutyChildAdapter(Context context, ArrayList<PerformDutyBean.DutyList> dutyLists, int type) {
        this.context = context;
        this.dutyLists = dutyLists;
    }

    @Override
    public int getGroupCount() {
        if (dutyLists != null) {
            return dutyLists.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        if (dutyLists.get(i).getChildList() == null) {
            return 0;
        } else {
            return dutyLists.get(i).getChildList().size();
        }
    }

    @Override
    public Object getGroup(int i) {
        return dutyLists.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return dutyLists.get(i).getChildList().get(i1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
        final GroupHolder groupHolder;
        final PerformDutyBean.DutyList info = dutyLists.get(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_performduty_main_group2, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            if (groupPosition != 0) {
                groupHolder.llLine.setVisibility(View.VISIBLE);
            } else {
                groupHolder.llLine.setVisibility(View.GONE);
            }
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.tvMeetDate.setText(info.getStrDate());
        groupHolder.tvMeetName.setText(info.getStrTitle());
        if (info.getChildList() == null || info.getChildList().size() == 0) {
            groupHolder.tvState.setVisibility(View.VISIBLE);
            groupHolder.tvState.setText(info.getStrStateName());
            if ("缺席".equals(info.getStrStateName())) {
                groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.red));
            } else if ("请假".equals(info.getStrStateName())) {
                groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.qj_color));
            } else {
                groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.chuxi));
            }
        } else {
            groupHolder.tvState.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                if (info.getStrRecordType().equals("01")) {
                    bundle.putString("strId", info.getStrId());
                    bundle.putString("headTitle", "会议详情");
                    bundle.putInt("sign", DefineUtil.hygl);
                    intent.putExtras(bundle);
                    intent.setClass(context, MeetAct_DetailActivity.class);
                    context.startActivity(intent);
                } else if (info.getStrRecordType().equals("02")) {
                    bundle.putString("strId", info.getStrId());
                    bundle.putBoolean("onlyShowNormalInfo", true);
                    bundle.putInt("sign", DefineUtil.hdgl);
                    bundle.putString("headTitle", "活动详情");
                    intent.putExtras(bundle);
                    intent.setClass(context, MeetAct_DetailActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;
        final PerformDutyBean.DutyList info = dutyLists.get(groupPosition).getChildList().get(childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_performduty_main_child2, viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        childHolder.tvMeet.setText(info.getStrTitle());
        childHolder.tvState.setText(info.getStrStateName());
//        childHolder.tvState.setText(info.getStrStateName());
        if ("缺席".equals(info.getStrStateName())) {
            childHolder.tvState.setTextColor(context.getResources().getColor(R.color.red));
        } else if ("请假".equals(info.getStrStateName())) {
            childHolder.tvState.setTextColor(context.getResources().getColor(R.color.qj_color));
        } else {
            childHolder.tvState.setTextColor(context.getResources().getColor(R.color.chuxi));
        }
        if (childPosition == 0) {
            childHolder.lineTop.setVisibility(View.INVISIBLE);
        } else {
            childHolder.lineTop.setVisibility(View.VISIBLE);
        }
        if (childPosition == dutyLists.get(groupPosition).getChildList().size() - 1) {
            childHolder.linebottom.setVisibility(View.INVISIBLE);
        } else {
            childHolder.linebottom.setVisibility(View.VISIBLE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                if (dutyLists.get(groupPosition).getStrRecordType().equals("01")) {
                    bundle.putString("strId", dutyLists.get(groupPosition).getStrId());
                    bundle.putString("headTitle", "会议详情");
                    bundle.putInt("sign", DefineUtil.hygl);
                    intent.putExtras(bundle);
                    intent.setClass(context, MeetAct_DetailActivity.class);
                    context.startActivity(intent);
                } else if (dutyLists.get(groupPosition).getStrRecordType().equals("02")) {
                    bundle.putString("strId", dutyLists.get(groupPosition).getStrId());
                    bundle.putBoolean("onlyShowNormalInfo", true);
                    bundle.putInt("sign", DefineUtil.hdgl);
                    bundle.putString("headTitle", "活动详情");
                    intent.putExtras(bundle);
                    intent.setClass(context, MeetAct_DetailActivity.class);
                    context.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder {
        private TextView tvMeetName;
        private TextView tvMeetDate;
        private TextView tvState;
        private View llLine;

        public GroupHolder(View view) {
            tvMeetName = (TextView) view.findViewById(R.id.tv_meet_name);
            tvMeetDate = (TextView) view.findViewById(R.id.tv_meet_date);
            tvState = (TextView) view.findViewById(R.id.tv_state);
            llLine = view.findViewById(R.id.ll_line);
        }
    }

    private class ChildHolder {
        private TextView tvMeet;
        private TextView tvState;
        private LinearLayout lineTop;
        private LinearLayout linebottom;

        public ChildHolder(View view) {
            tvMeet = (TextView) view.findViewById(R.id.tv_meet);
            tvState = (TextView) view.findViewById(R.id.tv_state);
            lineTop = (LinearLayout) view.findViewById(R.id.line_top);
            linebottom = (LinearLayout) view.findViewById(R.id.line_bot);
        }
    }
}
