package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MenuListBean;

import java.util.ArrayList;

/**
 * 评论与回复列表的适配器
 */
public class MenuListExAdapter extends BaseExpandableListAdapter {
    private ArrayList<MenuListBean.MenuBean> dutyLists;
    private Context context;
    private String curPageType = "1";
    private int intType;

    public MenuListExAdapter(Context context, ArrayList<MenuListBean.MenuBean> dutyLists) {
        this.context = context;
        this.dutyLists = dutyLists;

    }

    public void setDutyLists(ArrayList<MenuListBean.MenuBean> dutyLists) {
        this.dutyLists = dutyLists;
    }

    public ArrayList<MenuListBean.MenuBean> getDutyLists() {
        return dutyLists;
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
        if (dutyLists.get(i) == null) {
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
        return dutyLists.get(i);
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
        final MenuListBean.MenuBean info = dutyLists.get(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_menu_list_group, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (info.getStrStatus().equals("1")) {
            groupHolder.tvTitle.setText("早餐");
            groupHolder.tvIcon.setTextColor(Color.parseColor("#7EF1B7"));
        } else if (info.getStrStatus().equals("2")) {
            groupHolder.tvTitle.setText("午餐");
            groupHolder.tvIcon.setTextColor(Color.parseColor("#F3B9AC"));
        } else if (info.getStrStatus().equals("3")) {
            groupHolder.tvTitle.setText("晚餐");
            groupHolder.tvIcon.setTextColor(Color.parseColor("#92BAF9"));
        }
        groupHolder.tvTime.setText(info.getStrStarTime() + "-" + info.getStrEndTime());
        if (isExpand) {
            groupHolder.tvArrow.setText(context.getResources().getText(R.string.font_up));
        } else {
            groupHolder.tvArrow.setText(context.getResources().getText(R.string.font_down));
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;
        final MenuListBean.MenuBean info = dutyLists.get(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_menu_list_child, viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        childHolder.tv_content.setText(info.getStrMenu());
        childHolder.tv_content_qz.setText(info.getStrQZMenu());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder {
        private TextView tvIcon;
        private TextView tvTitle;
        private TextView tvTime;
        private TextView tvArrow;


        private GroupHolder(View view) {
            tvIcon = (TextView) view.findViewById(R.id.tv_icon);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvArrow = (TextView) view.findViewById(R.id.tv_arrow);
        }
    }

    private class ChildHolder {
        private TextView tv_content;
        private TextView tv_content_qz;

        private ChildHolder(View view) {
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_content_qz = (TextView) view.findViewById(R.id.tv_content_qz);
        }
    }
}
