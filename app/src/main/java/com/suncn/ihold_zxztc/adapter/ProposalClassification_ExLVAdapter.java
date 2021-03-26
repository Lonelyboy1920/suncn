package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalClassificationListBean.GroupProposalClassification;
import com.suncn.ihold_zxztc.view.forscrollview.MyExListViewForScrollView;

import java.util.ArrayList;

/**
 * 提案分类
 */
public class ProposalClassification_ExLVAdapter extends BaseExpandableListAdapter {
    private ArrayList<GroupProposalClassification> proposalClassifications;
    private Context context;
    private String strCaseTypeId;//提案类别id
    private String strCaseTypeName;//提案类别名称
    private boolean isShow;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public ProposalClassification_ExLVAdapter(Context context, ArrayList<GroupProposalClassification> proposalClassifications) {
        this.context = context;
        this.proposalClassifications = proposalClassifications;
    }

    public String getStrCaseTypeName() {
        return strCaseTypeName;
    }

    public void setStrCaseTypeName(String strCaseTypeName) {
        this.strCaseTypeName = strCaseTypeName;
    }

    public String getStrCaseTypeId() {
        return strCaseTypeId;
    }

    public void setStrCaseTypeId(String strCaseTypeId) {
        this.strCaseTypeId = strCaseTypeId;
    }

    public ArrayList<GroupProposalClassification> getProposalClassifications() {
        return proposalClassifications;
    }

    public void setProposalClassifications(ArrayList<GroupProposalClassification> proposalClassifications) {
        this.proposalClassifications = proposalClassifications;
    }

    @Override
    public int getGroupCount() {
        if (proposalClassifications != null)
            return proposalClassifications.size();
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        /*if (proposalClassifications.get(groupPosition).getCaseTopic_list() != null) {
            return proposalClassifications.get(groupPosition).getCaseTopic_list().size();
        }*/
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return proposalClassifications.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return proposalClassifications.get(groupPosition).getCaseTopic_list().get(childPosition);
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
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_contact_group, null);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.choose_TextView = (GITextView) convertView.findViewById(R.id.tv_choose);
            holder.choose_TextView.setVisibility(View.VISIBLE);
            holder.icon_TextView = (GITextView) convertView.findViewById(R.id.tv_icon);
            holder.count_TextView = (TextView) convertView.findViewById(R.id.tv_count);
            holder.count_TextView.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GroupProposalClassification groupProposalClassification = proposalClassifications.get(groupPosition);
        if (groupProposalClassification.getStrCaseTypeId().equals(strCaseTypeId)) {
            groupProposalClassification.setChecked(true);
        } else {
            groupProposalClassification.setChecked(false);
        }
        if (groupProposalClassification.isChecked()) {
            holder.choose_TextView.setTextColor(context.getResources().getColor(R.color.red));
            holder.choose_TextView.setText(context.getText(R.string.font_c_check));
        } else {
            holder.choose_TextView.setTextColor(context.getResources().getColor(R.color.font_content));
            holder.choose_TextView.setText(context.getText(R.string.font_c_uncheck));
        }
        holder.choose_TextView.setId(groupPosition);
        holder.choose_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = view.getId();
                GroupProposalClassification groupProposalClassification = proposalClassifications.get(pos);
                if (groupProposalClassification.isChecked()) {
                    strCaseTypeId = "";
                    strCaseTypeName = "";
                    groupProposalClassification.setChecked(false);
                    //holder.choose_TextView.setText(Utils.showHtmlInfo("<font color=#666666>&#xe616;</font>"));
                } else {
                    for (int j = 0; j < proposalClassifications.size(); j++) {
                        proposalClassifications.get(j).setChecked(false);
                        for (int i = 0; i < proposalClassifications.get(j).getCaseTopic_list().size(); i++) {
                            proposalClassifications.get(j).getCaseTopic_list().get(i).setChecked(false);
                            for (int k = 0; k < proposalClassifications.get(j).getCaseTopic_list().get(i).getCaseTopic_list().size(); k++) {
                                proposalClassifications.get(j).getCaseTopic_list().get(i).getCaseTopic_list().get(k).setChecked(false);
                            }
                        }
                    }
                    groupProposalClassification.setChecked(true);
                    strCaseTypeId = groupProposalClassification.getStrCaseTypeId();
                    strCaseTypeName = groupProposalClassification.getStrCaseTypeName();
                    //holder.choose_TextView.setText(Utils.showHtmlInfo("<font color=#ef4b39>&#xe614;</font>"));
                }
                proposalClassifications.set(pos, groupProposalClassification);
                notifyDataSetChanged();
            }
        });
        if (isExpanded) {
            holder.icon_TextView.setText(context.getText(R.string.font_exlv_expan1));
        } else {
            holder.icon_TextView.setText(context.getText(R.string.font_exlv_cos1));
        }
        holder.name_TextView.setText(groupProposalClassification.getStrCaseTypeName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_expand_group_child, null);
            holder.childExpand = (MyExListViewForScrollView) convertView.findViewById(R.id.exListview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ChildProposalClassfication_ExLVAdapter childExpandAdapter = new ChildProposalClassfication_ExLVAdapter(context, this, proposalClassifications.get(groupPosition).getCaseTopic_list());
        childExpandAdapter.setProposalClassifications(proposalClassifications);
        childExpandAdapter.setStrCaseTypeId(strCaseTypeId);
        holder.childExpand.setAdapter(childExpandAdapter);
        for (int i = 0; i < proposalClassifications.get(groupPosition).getCaseTopic_list().size(); i++) {
            for (int j = 0; j < proposalClassifications.get(groupPosition).getCaseTopic_list().get(i).getCaseTopic_list().size(); j++) {
                if (proposalClassifications.get(groupPosition).getCaseTopic_list().get(i).getCaseTopic_list().get(j).getStrCaseTypeId().equals(strCaseTypeId)) {
                    holder.childExpand.expandGroup(i);
                    break;
                }
                for (int k = 0; k < proposalClassifications.get(i).getCaseTopic_list().get(j).getCaseTopic_list().size(); k++) {
                    if (isShow) {
                        holder.childExpand.expandGroup(i);
                        break;
                    }
                }
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolder {
        private GITextView icon_TextView;
        private TextView name_TextView;
        private TextView count_TextView;
        private GITextView choose_TextView;
        private MyExListViewForScrollView childExpand;
    }
}
