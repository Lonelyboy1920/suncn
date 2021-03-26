package com.suncn.ihold_zxztc.adapter;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ObjAttendMemBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 会议及会议的参会人员（会议通知）
 */
public class AttendPerson_ExLVAdapter extends BaseExpandableListAdapter {
    private List<ObjAttendMemBean> objMemListBeen;
    private Activity context;
    private List<String> groupNames = new ArrayList<>();
    private boolean isPaved; // 是否是委员管理的平铺展示
    private boolean intModify;

    public void setIntModify(boolean intModify) {
        this.intModify = intModify;
    }

    public boolean isIntModify() {
        return intModify;
    }

    public AttendPerson_ExLVAdapter(Activity context, List<ObjAttendMemBean> objMemListBeen) {
        this.context = context;
        this.objMemListBeen = objMemListBeen;
    }

    public void setPaved(boolean paved) {
        isPaved = paved;
        for (ObjAttendMemBean zxtaJointMemBean : objMemListBeen) {
            groupNames.add(zxtaJointMemBean.getStrName());
        }
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    @Override
    public int getGroupCount() {
        if (objMemListBeen != null)
            return objMemListBeen.size();
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (objMemListBeen.get(groupPosition).getMem_list() != null)
            return objMemListBeen.get(groupPosition).getMem_list().size();
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return objMemListBeen.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return objMemListBeen.get(groupPosition).getMem_list().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_contact_group, null);
            holder.icon_TextView = (TextView) convertView.findViewById(R.id.tv_icon);
            holder.count_TextView = (TextView) convertView.findViewById(R.id.tv_count);
            holder.groupName_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (isPaved) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.main_bg));
            holder.groupName_TextView.setTextColor(context.getResources().getColor(R.color.font_content));
            holder.groupName_TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        if (isExpanded) {
            holder.icon_TextView.setText(context.getText(R.string.font_exlv_expan1));
        } else {
            holder.icon_TextView.setText(context.getText(R.string.font_exlv_cos1));
        }
        ObjAttendMemBean objMemListBean = objMemListBeen.get(groupPosition);
        holder.groupName_TextView.setText(objMemListBean.getStrName());
        //holder.count_TextView.setText(objMemListBean.getIntCount() + "人");
        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_meetact_attendperson_child, null);
            // convertView.setBackgroundResource(R.color.main_bg);
            viewHolder.head_ImageView = (RoundImageView) convertView.findViewById(R.id.iv_head);
            viewHolder.chidName_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.attend_TextView = (TextView) convertView.findViewById(R.id.tv_attend);
            viewHolder.arrow_TextView = (TextView) convertView.findViewById(R.id.tv_arrow);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ObjAttendMemBean objPerson = objMemListBeen.get(groupPosition).getMem_list().get(childPosition);
        viewHolder.head_ImageView.setVisibility(View.VISIBLE);
        String imageUrl = Utils.formatFileUrl(context, objPerson.getStrPathUrl());

        GIImageUtil.loadImg(context, viewHolder.head_ImageView, imageUrl, 1);
        viewHolder.chidName_TextView.setText(objPerson.getStrName());
        final int intAttend = objPerson.getIntAttend();
        if (intAttend == 0) { // 0--请假
            viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.view_leave));
            // viewHolder.attend_TextView.setBackgroundResource(R.drawable.shape_zxta_state_bg_green);
            viewHolder.attend_TextView.setText("请假");
        } else if (intAttend == 1) { // 1--出席
            viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.font_content));
            //viewHolder.attend_TextView.setBackgroundResource(R.drawable.shape_zxta_state_bg_grey);
            viewHolder.attend_TextView.setText("出席");
        } else if (intAttend == -1) { // 2--缺席
            viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.zxta_state_red));
            //viewHolder.attend_TextView.setBackgroundResource(R.drawable.shape_zxta_state_bg_orange);
            viewHolder.attend_TextView.setText("");
        } else {
            viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.zxta_state_red));
            //viewHolder.attend_TextView.setBackgroundResource(R.drawable.shape_zxta_state_bg_orange);
            viewHolder.attend_TextView.setText("缺席");
        }
        if (intModify) {
            viewHolder.arrow_TextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.arrow_TextView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < objMemListBeen.size(); i++) {
            String sortStr = objMemListBeen.get(i).getStrName();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (section == firstChar) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolder {
        private TextView icon_TextView;
        private TextView groupName_TextView;
        private TextView chidName_TextView;
        private TextView count_TextView;
        private RoundImageView head_ImageView;
        private TextView attend_TextView;
        private TextView arrow_TextView;
    }
}
