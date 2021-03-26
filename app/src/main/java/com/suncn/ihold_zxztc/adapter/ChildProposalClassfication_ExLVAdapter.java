package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalClassificationListBean;
import com.suncn.ihold_zxztc.bean.ProposalClassificationListBean.ChildProposalClassification;

import java.util.ArrayList;

/**
 * 提案分类子分类
 */
@SuppressWarnings("deprecation")
public class ChildProposalClassfication_ExLVAdapter extends BaseExpandableListAdapter {
    private ArrayList<ChildProposalClassification> proposalClassificationLists;
    private ArrayList<ProposalClassificationListBean.GroupProposalClassification> proposalClassifications;
    private Context context;
    private ProposalClassification_ExLVAdapter adapter;
    private String strCaseTypeId;//提案类别id
    private String strCaseTypeName;//提案类别名称
    private String strPackageName;
    private boolean isHg;//是否为黄冈政协

    public ChildProposalClassfication_ExLVAdapter(Context context, ProposalClassification_ExLVAdapter adapter, ArrayList<ChildProposalClassification> proposalClassificationLists) {
        this.adapter = adapter;
        this.context = context;
        this.proposalClassificationLists = proposalClassificationLists;
        strPackageName = context.getPackageName();
        isHg = strPackageName.equals("com.suncn.zxztc_hgszx") ? true : false;
    }

    public String getStrCaseTypeId() {
        return strCaseTypeId;
    }

    public void setStrCaseTypeId(String strCaseTypeId) {
        this.strCaseTypeId = strCaseTypeId;
    }

    public ArrayList<ProposalClassificationListBean.GroupProposalClassification> getProposalClassifications() {
        return proposalClassifications;
    }

    public void setProposalClassifications(ArrayList<ProposalClassificationListBean.GroupProposalClassification> proposalClassifications) {
        this.proposalClassifications = proposalClassifications;
    }

    @Override
    public int getGroupCount() {
        if (proposalClassificationLists != null)
            return proposalClassificationLists.size();
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (proposalClassificationLists.get(groupPosition).getCaseTopic_list() != null) {
            return proposalClassificationLists.get(groupPosition).getCaseTopic_list().size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return proposalClassificationLists.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return proposalClassificationLists.get(groupPosition).getCaseTopic_list().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_contact_group, null);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.choose_TextView = (GITextView) convertView.findViewById(R.id.tv_choose);
            holder.icon_TextView = (GITextView) convertView.findViewById(R.id.tv_icon);
            holder.count_TextView = (TextView) convertView.findViewById(R.id.tv_count);
            holder.count_TextView.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (isHg && getChildrenCount(groupPosition) > 0) {
            holder.choose_TextView.setVisibility(View.GONE);
        } else {
            holder.choose_TextView.setVisibility(View.VISIBLE);
        }
        if (isExpanded) {
            adapter.setShow(true);
            holder.icon_TextView.setText(context.getText(R.string.font_exlv_expan1));
        } else {
            adapter.setShow(false);
            holder.icon_TextView.setText(context.getText(R.string.font_exlv_cos1));
        }
        ChildProposalClassification childProposalClassification = proposalClassificationLists.get(groupPosition);
        if (childProposalClassification.getStrCaseTypeId().equals(strCaseTypeId)) {
            childProposalClassification.setChecked(true);
        } else {
            childProposalClassification.setChecked(false);
        }
        if (childProposalClassification.isChecked()) {
            holder.choose_TextView.setTextColor(context.getResources().getColor(R.color.red));
            holder.choose_TextView.setText(context.getText(R.string.font_c_check));
        } else {
            holder.choose_TextView.setTextColor(context.getResources().getColor(R.color.font_content));
            holder.choose_TextView.setText(context.getText(R.string.font_c_uncheck));
        }
        holder.name_TextView.setText(childProposalClassification.getStrCaseTypeName());
        holder.choose_TextView.setId(groupPosition);
        holder.choose_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = v.getId();
                ChildProposalClassification groupProposalClassification = proposalClassificationLists.get(pos);
                // adapter.setOnClick(groupProposalClassification, v, 0);
                if (groupProposalClassification.isChecked()) {
                    groupProposalClassification.setChecked(false);
                    adapter.setStrCaseTypeId("");
                    adapter.setStrCaseTypeName("");
                    // holder.choose_TextView.setText(Utils.showHtmlInfo("<font color=#666666>&#xe616;</font>"));
                } else {
                    groupProposalClassification.setChecked(true);
                    for (int j = 0; j < proposalClassifications.size(); j++) {
                        proposalClassifications.get(j).setChecked(false);
                        //adapter.notifyDataSetChanged();
                        for (int i = 0; i < proposalClassifications.get(j).getCaseTopic_list().size(); i++) {
                            proposalClassifications.get(j).getCaseTopic_list().get(i).setChecked(false);
                        }
                    }

                    adapter.setStrCaseTypeId(groupProposalClassification.getStrCaseTypeId());
                    adapter.setStrCaseTypeName(groupProposalClassification.getStrCaseTypeName());
                    //holder.choose_TextView.setText(Utils.showHtmlInfo("<font color=#ef4b39>&#xe614;</font>"));
                }
                proposalClassificationLists.set(pos, groupProposalClassification);
                /*if (groupProposalClassification.isChecked()) {
                    holder.choose_TextView.setText(Utils.showHtmlInfo("<font color=#ef4b39>&#xe614;</font>"));
                } else {
                    holder.choose_TextView.setText(Utils.showHtmlInfo("<font color=#666666>&#xe616;</font>"));
                }*/
                adapter.notifyDataSetChanged();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_expand_child, null);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.choose_TextView = (GITextView) convertView.findViewById(R.id.tv_choose);
            //holder.choose_TextView.setTypeface(iconFont);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ProposalClassificationListBean.ProposalClassification proposalClassification = proposalClassificationLists.get(groupPosition).getCaseTopic_list().get(childPosition);
        if (proposalClassification.getStrCaseTypeId().equals(strCaseTypeId)) {
            proposalClassification.setChecked(true);
        } else {
            proposalClassification.setChecked(false);
        }

        if (proposalClassification.isChecked()) {
            holder.choose_TextView.setTextColor(context.getResources().getColor(R.color.red));
            holder.choose_TextView.setText(context.getText(R.string.font_c_check));
        } else {
            holder.choose_TextView.setTextColor(context.getResources().getColor(R.color.font_content));
            holder.choose_TextView.setText(context.getText(R.string.font_c_uncheck));
        }
        holder.name_TextView.setText(proposalClassification.getStrCaseTypeName());
        holder.choose_TextView.setTag(groupPosition + "," + childPosition);
        holder.choose_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setShow(true);
                String s = (String) v.getTag();
                String[] pos = s.split(",");
                int g = Integer.parseInt(pos[0]);
                int c = Integer.parseInt(pos[1]);
                ProposalClassificationListBean.ProposalClassification proposalClassification = proposalClassificationLists.get(g).getCaseTopic_list().get(c);
                if (proposalClassification.isChecked()) {
                    proposalClassification.setChecked(false);
                    adapter.setStrCaseTypeId("");
                    adapter.setStrCaseTypeName("");
                    //holder.choose_TextView.setText(Utils.showHtmlInfo("<font color=#666666>&#xe616;</font>"));
                } else {
                    for (int j = 0; j < proposalClassifications.size(); j++) {
                        proposalClassifications.get(j).setChecked(false);
                        //adapter.notifyDataSetChanged();
                        for (int i = 0; i < proposalClassifications.get(j).getCaseTopic_list().size(); i++) {
                            proposalClassifications.get(j).getCaseTopic_list().get(i).setChecked(false);
                            for (int k = 0; k < proposalClassifications.get(j).getCaseTopic_list().get(i).getCaseTopic_list().size(); k++) {
                                proposalClassifications.get(j).getCaseTopic_list().get(i).getCaseTopic_list().get(k).setChecked(false);
                            }
                        }
                    }
                    proposalClassification.setChecked(true);
                    adapter.setStrCaseTypeId(proposalClassification.getStrCaseTypeId());
                    adapter.setStrCaseTypeName(proposalClassification.getStrCaseTypeName());
                    //holder.choose_TextView.setText(Utils.showHtmlInfo("<font color=#ef4b39>&#xe614;</font>"));
                }
                proposalClassificationLists.get(g).getCaseTopic_list().set(c, proposalClassification);
                adapter.notifyDataSetChanged();
                notifyDataSetChanged();
                // adapter.setOnClick(proposalClassification, v, 1);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolder {
        private GITextView icon_TextView;
        private TextView name_TextView;
        private GITextView choose_TextView;
        private TextView count_TextView;
        private TextView menu_TextView;
        private ImageView dot_ImageView;
    }
}
