package com.suncn.ihold_zxztc.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.proposal.Choose_JoinMemActivity;
import com.suncn.ihold_zxztc.bean.BaseUser;
import com.suncn.ihold_zxztc.bean.ZxtaJointMemListBean.ZxtaJointMemBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;

/**
 * 选择机构/委员列表
 */
public class Choose_JoinMem_ExLVAdapter extends BaseExpandableListAdapter {
    private ArrayList<ZxtaJointMemBean> zxtaJointMemBeans;
    private Choose_JoinMemActivity context;
    // private String strChooseValue = "";
    private String strChooseValueId = "";
    private boolean isChoose; // 是否是选择联名委员操作
    private int intType;//0党派界别；1-委员

    public Choose_JoinMem_ExLVAdapter(Activity context, ArrayList<ZxtaJointMemBean> zxtaJointMemBeans, boolean isChoose, int intType) {
        this.context = (Choose_JoinMemActivity) context;
        this.zxtaJointMemBeans = zxtaJointMemBeans;
        this.isChoose = isChoose;
        this.intType = intType;
    }

    public String getStrChooseValueId() {
        return strChooseValueId;
    }

    public void setStrChooseValueId(String strChooseValueId) {
        this.strChooseValueId = strChooseValueId;
    }

    public void setZxtaJointMemBeans(ArrayList<ZxtaJointMemBean> zxtaJointMemBeans) {
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
        ZxtaJointMemBean zxtaJointMemBean = zxtaJointMemBeans.get(groupPosition);
        holder.name_TextView.setText(zxtaJointMemBean.getStrName());
        if (1 == intType) {
            holder.count_TextView.setText(getChildrenCount(groupPosition) + "");
        } else {
            holder.count_TextView.setText(getChildrenCount(groupPosition) + "人");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_exlv_item_member_child, null);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.check_TextView = (TextView) convertView.findViewById(R.id.tv_check);
            holder.message_TextView = (TextView) convertView.findViewById(R.id.tv_message);
            holder.tel_TextView = (TextView) convertView.findViewById(R.id.tv_tel);
            holder.head_ImageView = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.arrow_TextView = convertView.findViewById(R.id.tv_arrow);
            holder.mobile_TextView = (TextView) convertView.findViewById(R.id.tv_mobile);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.main_bg));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BaseUser memListBean = zxtaJointMemBeans.get(groupPosition).getObjChildList().get(childPosition);
        holder.head_ImageView.setVisibility(View.VISIBLE);

        if (GIStringUtil.isNotBlank(memListBean.getStrPathUrl())) {
            String imageUrl = Utils.formatFileUrl(context, memListBean.getStrPathUrl());
            GIImageUtil.loadImg(context, holder.head_ImageView, imageUrl, 1);
        } else {
            holder.head_ImageView.setImageResource(R.mipmap.img_person);
        }
        holder.name_TextView.setText(memListBean.getStrName());
        if (memListBean.isChecked()) {
            holder.check_TextView.setVisibility(View.VISIBLE);
        } else {
            holder.check_TextView.setVisibility(View.GONE);
        }
        if (isChoose) {
            holder.arrow_TextView.setVisibility(View.GONE);
        } else {
            holder.arrow_TextView.setVisibility(View.VISIBLE);
        }
        holder.mobile_TextView.setVisibility(View.GONE);
        holder.message_TextView.setVisibility(View.GONE);
        holder.tel_TextView.setVisibility(View.GONE);
        convertView.setTag(R.id.tag_gpos, groupPosition);
        convertView.setTag(R.id.tag_cpos, childPosition);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int gpos = (int) view.getTag(R.id.tag_gpos);
                int cpos = (int) view.getTag(R.id.tag_cpos);
                doChoose(gpos, cpos, 0);
            }
        });
        return convertView;
    }

    private void doChoose(int gpos, int cpos, int type) {
        BaseUser memListBean = zxtaJointMemBeans.get(gpos).getObjChildList().get(cpos);
        String strUserId = memListBean.getStrUserId();//获取格式u/id/名称/手机的strUserId
        //String strName = memListBean.getStrName();
        if (memListBean.isChecked()) {
            memListBean.setChecked(false);
            strChooseValueId = strChooseValueId.replaceAll(strUserId + ",", "");
        } else {
            strChooseValueId = strChooseValueId + strUserId + ",";
            memListBean.setChecked(true);
        }
        zxtaJointMemBeans.get(gpos).getObjChildList().set(cpos, memListBean);
        context.setStrChooseValueId(strChooseValueId);
        notifyDataSetChanged();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolder {
        private TextView icon_TextView;
        private TextView name_TextView;
        private TextView check_TextView;
        private RoundImageView head_ImageView;
        private GITextView arrow_TextView;
        private TextView count_TextView;
        private TextView mobile_TextView;
        private TextView message_TextView;
        private TextView tel_TextView;
    }
}
