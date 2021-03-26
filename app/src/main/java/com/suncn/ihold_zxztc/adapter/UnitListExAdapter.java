package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.hot.UnitDetailActivty;
import com.suncn.ihold_zxztc.activity.hot.UnitListActivity;
import com.suncn.ihold_zxztc.bean.UnitListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;

/**
 * 微门户列表适配器
 */
public class UnitListExAdapter extends BaseExpandableListAdapter {
    private ArrayList<UnitListBean.UnitList> unitList;
    private Context context;
    private String curPageType = "1";
    private int intType;
    private String curUnitId = "";

    public UnitListExAdapter(Context context, ArrayList<UnitListBean.UnitList> unitList) {
        this.context = context;
        this.unitList = unitList;
        curUnitId = GISharedPreUtil.getString(context, "strDefalutRootUnitId");
    }

    @Override
    public int getGroupCount() {
        if (unitList != null) {
            return unitList.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (unitList.get(groupPosition).getObjUnits_list() != null
        &&unitList.size()>0) {
            return unitList.get(groupPosition).getObjUnits_list().size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        return unitList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return unitList.get(i).getObjUnits_list().get(i1);
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_unit_list, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        UnitListBean.UnitList objInfo = unitList.get(groupPosition);
        groupHolder.tvName.setText(objInfo.getStrUnit_name());
        GIImageUtil.loadImg(context, groupHolder.ivHead, Utils.formatFileUrl(context, objInfo.getStrUnit_IconPathUrl()), context.getResources().getDrawable(R.mipmap.unit_head_default));
        if (objInfo.getIntCount().equals("0")) {
            groupHolder.tvState.setText("+"+context.getString(R.string.string_subscription));
            groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.view_head_bg));
        } else {
            groupHolder.tvState.setText(context.getString(R.string.string_subscribed));
            groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.font_content));
        }
        if (curUnitId.equals(objInfo.getStrUnit_id())) {
            groupHolder.tvState.setVisibility(View.GONE);
        } else {
            groupHolder.tvState.setVisibility(View.VISIBLE);
        }
        groupHolder.tvIcon.setTextSize(8);
        groupHolder.tvEmpty.setVisibility(View.GONE);
        if (isExpand) {
            groupHolder.tvIcon.setText(context.getResources().getString(R.string.font_exlv_expan1));
        } else {
            groupHolder.tvIcon.setText(context.getResources().getString(R.string.font_exlv_cos1));
        }
        if (objInfo.getObjUnits_list() == null || objInfo.getObjUnits_list().size() <= 0) {
            groupHolder.tvIcon.setVisibility(View.INVISIBLE);
        } else {
            groupHolder.tvIcon.setVisibility(View.VISIBLE);
        }
        groupHolder.tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objInfo.getIntCount().equals("1")) {
                    objInfo.setIntCount("0");
                    groupHolder.tvState.setText("+"+context.getString(R.string.string_subscription));
                    groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.view_head_bg));
                } else {
                    objInfo.setIntCount("1");
                    groupHolder.tvState.setText(context.getString(R.string.string_subscribed));
                    groupHolder.tvState.setTextColor(context.getResources().getColor(R.color.font_content));
                }
                ((UnitListActivity) context).doSubscribeNews(objInfo.getStrUnit_id());
            }
        });
        groupHolder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("strRootUnitId", objInfo.getStrUnit_id());
                bundle.putString("strState", objInfo.getIntCount());
                bundle.putString("strPhotoUrl", objInfo.getStrUnit_IconPathUrl());
                bundle.putString("strUnitName", objInfo.getStrUnit_name());
                bundle.putString("strBgUrl", objInfo.getStrUnit_BackGroundPathUrl());
                intent.setClass(context, UnitDetailActivty.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final GroupHolder childHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_unit_list, viewGroup, false);
            childHolder = new GroupHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (GroupHolder) convertView.getTag();
        }
        UnitListBean.UnitList objInfo = unitList.get(groupPosition).getObjUnits_list().get(childPosition);
        childHolder.tvName.setText(objInfo.getStrUnit_name());
        GIImageUtil.loadImg(context, childHolder.ivHead, Utils.formatFileUrl(context, objInfo.getStrUnit_IconPathUrl()), context.getResources().getDrawable(R.mipmap.unit_head_default));
        if (objInfo.getIntCount().equals("0")) {
            childHolder.tvState.setText("+"+context.getString(R.string.string_subscription));
            childHolder.tvState.setTextColor(context.getResources().getColor(R.color.view_head_bg));
        } else {
            childHolder.tvState.setText(context.getString(R.string.string_subscribed));
            childHolder.tvState.setTextColor(context.getResources().getColor(R.color.font_content));
        }
        if (curUnitId.equals(objInfo.getStrUnit_id())) {
            childHolder.tvState.setVisibility(View.GONE);
        } else {
            childHolder.tvState.setVisibility(View.VISIBLE);
        }
        childHolder.tvIcon.setText(context.getResources().getString(R.string.font_zd));
        childHolder.tvIcon.setTextSize(18);
        childHolder.tvEmpty.setVisibility(View.VISIBLE);
        childHolder.tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objInfo.getIntCount().equals("1")) {
                    objInfo.setIntCount("0");
                    childHolder.tvState.setText("+"+context.getString(R.string.string_subscription));
                    childHolder.tvState.setTextColor(context.getResources().getColor(R.color.view_head_bg));
                } else {
                    objInfo.setIntCount("1");
                    childHolder.tvState.setText(context.getString(R.string.string_subscribed));
                    childHolder.tvState.setTextColor(context.getResources().getColor(R.color.font_content));
                }
                ((UnitListActivity) context).doSubscribeNews(objInfo.getStrUnit_id());
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder {
        private TextView tvIcon;
        private ImageView ivHead;
        private TextView tvName;
        private TextView tvState;
        private TextView tvEmpty;


        private GroupHolder(View view) {
            tvIcon = (TextView) view.findViewById(R.id.tv_lv_icon);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvState = (TextView) view.findViewById(R.id.tv_state);
            ivHead = (ImageView) view.findViewById(R.id.iv_head);
            tvEmpty = (TextView) view.findViewById(R.id.tv_empty);
        }
    }


}
