package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_DetailActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.bean.PerformDutyBean;

import java.util.ArrayList;

/**
 * 评论与回复列表的适配器
 */
public class PerformDutyFragmentAdapter extends BaseExpandableListAdapter {
    private ArrayList<PerformDutyBean.DutyList> dutyLists;
    private Context context;
    private String curPageType = "1";
    private int intType;

    public PerformDutyFragmentAdapter(Context context, ArrayList<PerformDutyBean.DutyList> dutyLists, int type) {
        this.context = context;
        this.dutyLists = dutyLists;
        this.intType = type;
    }

    public void setDutyLists(ArrayList<PerformDutyBean.DutyList> dutyLists) {
        this.dutyLists = dutyLists;
    }

    public ArrayList<PerformDutyBean.DutyList> getDutyLists() {
        return dutyLists;
    }

    public void setCurPageType(String curPageType) {
        this.curPageType = curPageType;
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
        if (dutyLists.get(i).getObjOneList() == null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public Object getGroup(int i) {
        return dutyLists.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return dutyLists.get(i).getObjOneList().get(i1);
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
            if (curPageType.equals("01") || curPageType.equals("02")) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_performduty_main_group, viewGroup, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_performduty_main_group2, viewGroup, false);
            }
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (curPageType.equals("01") || curPageType.equals("02")) {
            groupHolder.tv_type.setText(info.getStrType());
        } else {
            groupHolder.tvMeetDate.setText(info.getStrDate());
            groupHolder.tvMeetName.setText(info.getStrTitle());
            groupHolder.tvState.setText(info.getStrStateName());
            if ("2".equals(info.getStrSourceType())) {
                groupHolder.tv_deal.setVisibility(View.VISIBLE);
                groupHolder.tv_deal.setText("联名");
            } else {
                groupHolder.tv_deal.setVisibility(View.GONE);
            }
            if (groupPosition == 0) {
                groupHolder.llLine.setVisibility(View.GONE);
            } else {
                groupHolder.llLine.setVisibility(View.VISIBLE);
            }
            groupHolder.llLine.setVisibility(View.VISIBLE);
            if ("缺席".equals(info.getStrStateName())) {
                groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.red));
            } else if ("请假".equals(info.getStrStateName())) {
                groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.qj_color));
            } else {
                groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.chuxi));
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();
                    if (curPageType.equals("03")) {//提案
                        bundle.putString("strId", info.getStrId());
                        intent.putExtras(bundle);
                        intent.setClass(context, Proposal_DetailActivity.class);
                        context.startActivity(intent);
                    } else if (curPageType.equals("04")) {//社情民意
                        bundle.putString("strUrl", info.getStrUrl());
                        bundle.putString("headTitle", context.getString(R.string.string_social_opinion));
                        intent.putExtras(bundle);
                        intent.setClass(context, WebViewActivity.class);
                        context.startActivity(intent);
                    } else if (curPageType.equals("05")) {//会议发言
                        bundle.putString("strUrl", info.getStrUrl());
                        bundle.putString("headTitle", context.getString(R.string.string_meeting_speach));
                        intent.putExtras(bundle);
                        intent.setClass(context, WebViewActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;
        final PerformDutyBean.DutyList info = dutyLists.get(groupPosition).getObjOneList().get(childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_performduty_main_child, viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        PerformDutyChildAdapter performDutyChildAdapter = new PerformDutyChildAdapter(context, dutyLists.get(groupPosition).getObjOneList(), intType);
        childHolder.expandableListView.setAdapter(performDutyChildAdapter);
        for (int i = 0; i < performDutyChildAdapter.getGroupCount(); i++) {
            childHolder.expandableListView.expandGroup(i);
        }
        childHolder.expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder {
        private TextView tv_type;
        private TextView tvMeetName;
        private TextView tvMeetDate;
        private TextView tvState;
        private View llLine;
        private TextView tv_deal;


        private GroupHolder(View view) {
            tv_type = (TextView) view.findViewById(R.id.tv_type);
            tvMeetName = (TextView) view.findViewById(R.id.tv_meet_name);
            tvMeetDate = (TextView) view.findViewById(R.id.tv_meet_date);
            tvState = (TextView) view.findViewById(R.id.tv_state);
            llLine = view.findViewById(R.id.ll_line);
            tv_deal = view.findViewById(R.id.tv_deal);
        }
    }

    private class ChildHolder {
        private ExpandableListView expandableListView;

        private ChildHolder(View view) {
            expandableListView = (ExpandableListView) view.findViewById(R.id.exListView);
        }
    }
}
