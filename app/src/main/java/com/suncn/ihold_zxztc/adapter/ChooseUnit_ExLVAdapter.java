package com.suncn.ihold_zxztc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.gavin.giframe.utils.GILogUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.proposal.Choose_UnitActivity;
import com.suncn.ihold_zxztc.bean.UpUnitListBean;
import com.suncn.ihold_zxztc.bean.UpUnitListBean.UpUnitBean;

import java.util.List;


/**
 * 承办单位、系统列表
 */
public class ChooseUnit_ExLVAdapter extends BaseExpandableListAdapter {
    private List<UpUnitBean> zxtaJointMemBeans;
    private Choose_UnitActivity context;
    private String strChooseValueId;
    private boolean isSingle; // 是否是单选


    public ChooseUnit_ExLVAdapter(Choose_UnitActivity context, List<UpUnitBean> zxtaJointMemBeans, boolean isSingle) {
        this.context = context;
        this.zxtaJointMemBeans = zxtaJointMemBeans;
        this.isSingle = isSingle;
    }

    public String getStrChooseValueId() {
        return strChooseValueId;
    }

    public void setStrChooseValueId(String strChooseValueId) {
        this.strChooseValueId = strChooseValueId;
    }

    public void setZxtaJointMemBeans(List<UpUnitBean> zxtaJointMemBeans) {
        this.zxtaJointMemBeans = zxtaJointMemBeans;
    }

    @Override
    public int getGroupCount() {
        if (zxtaJointMemBeans != null)
            return zxtaJointMemBeans.size();
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (zxtaJointMemBeans.get(groupPosition).getObjChildList() != null) {
            return zxtaJointMemBeans.get(groupPosition).getObjChildList().size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return zxtaJointMemBeans.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return zxtaJointMemBeans.get(groupPosition).getObjChildList().get(childPosition);
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
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.count_TextView = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (isExpanded) {
            holder.icon_TextView.setText(context.getText(R.string.font_exlv_expan1));
        } else {
            holder.icon_TextView.setText(context.getText(R.string.font_exlv_cos1));
        }
        UpUnitBean zxtaJointMemBean = zxtaJointMemBeans.get(groupPosition);
        holder.name_TextView.setText(zxtaJointMemBean.getStrName());
        holder.name_TextView.getPaint().setFakeBoldText(true);
        holder.count_TextView.setText(getChildrenCount(groupPosition) + "项");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_lv_item_chooseunit, null);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.check_TextView = (TextView) convertView.findViewById(R.id.tv_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UpUnitListBean.Unit memListBean = zxtaJointMemBeans.get(groupPosition).getObjChildList().get(childPosition);
        holder.name_TextView.setText(memListBean.getStrName());
        if (memListBean.isChecked()) {
            holder.check_TextView.setVisibility(View.VISIBLE);
        } else {
            holder.check_TextView.setVisibility(View.GONE);
        }
        convertView.setTag(R.id.tag_gpos, groupPosition);
        convertView.setTag(R.id.tag_cpos, childPosition);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int gpos = (int) view.getTag(R.id.tag_gpos);
                int cpos = (int) view.getTag(R.id.tag_cpos);
                UpUnitListBean.Unit memListBean = zxtaJointMemBeans.get(gpos).getObjChildList().get(cpos);
                String strId = memListBean.getStrId();
                String strName = memListBean.getStrName();
                if (memListBean.isChecked()) {
                    memListBean.setChecked(false);
                    strChooseValueId = strChooseValueId.replaceAll(strId + ",", "");
                } else {
                    if (isSingle) {
                        for (int i = 0; i < zxtaJointMemBeans.size(); i++) {
                            for (int j = 0; j < zxtaJointMemBeans.get(i).getObjChildList().size(); j++) {
                                zxtaJointMemBeans.get(i).getObjChildList().get(j).setChecked(false);
                            }
                        }
                        strChooseValueId = strId + ",";
                        memListBean.setChecked(true);
                    } else {
                        strChooseValueId = strChooseValueId + strId + ",";
                        memListBean.setChecked(true);
                    }
                }
                zxtaJointMemBeans.get(gpos).getObjChildList().set(cpos, memListBean);
                context.setStrChooseUnitId(strChooseValueId);
                notifyDataSetChanged();
                GILogUtil.i("strChooseValueId===========================" + strChooseValueId);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        private TextView icon_TextView;
        private TextView name_TextView;
        private TextView check_TextView;
        private TextView count_TextView;
    }
}
