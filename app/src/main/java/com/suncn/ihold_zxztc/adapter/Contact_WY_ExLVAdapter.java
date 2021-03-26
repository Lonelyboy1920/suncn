package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.longchat.base.dao.QDUser;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.duty.MemberDuty_MainActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.activity.message.Contact_DetailActivity;
import com.suncn.ihold_zxztc.bean.BaseUser;
import com.suncn.ihold_zxztc.bean.ZxtaJointMemListBean;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 委员列表
 */
public class Contact_WY_ExLVAdapter extends BaseExpandableListAdapter {
    private List<ZxtaJointMemListBean.ZxtaJointMemBean> zxtaJointMemBeans;
    private Context context;
    private String strChooseValue = "";
    private boolean isChoose; // 是否是选择操作
    private boolean isCommissioner; // 是否机关查看党派界别专委会
    private boolean isPaved; // 是否是委员管理的平铺展示
    private List<String> groupNames = new ArrayList<>();
    private String strUrl;
    private List<QDUser> selectUserList = new ArrayList<>(); //已选的人员列表;
    private boolean isSingle;

    public Contact_WY_ExLVAdapter(Context context, List<ZxtaJointMemListBean.ZxtaJointMemBean> zxtaJointMemBeans, boolean isChoose) {
        this.context = context;
        this.zxtaJointMemBeans = zxtaJointMemBeans;
        this.isChoose = isChoose;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    public void setStrUrl(String strUrl) {
        this.strUrl = strUrl;
    }

    public void setCommissioner(boolean commissioner) {
        isCommissioner = commissioner;
    }

    public void setPaved(boolean paved) {
        isPaved = paved;
        for (ZxtaJointMemListBean.ZxtaJointMemBean zxtaJointMemBean : zxtaJointMemBeans) {
            groupNames.add(zxtaJointMemBean.getStrName());
        }
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public void setZxtaJointMemBeans(List<ZxtaJointMemListBean.ZxtaJointMemBean> zxtaJointMemBeans) {
        this.zxtaJointMemBeans = zxtaJointMemBeans;
    }

    public List<ZxtaJointMemListBean.ZxtaJointMemBean> getZxtaJointMemBeans() {
        return zxtaJointMemBeans;
    }

    public List<QDUser> getSelectUserList() {
        return selectUserList;
    }

    public String getStrChooseValue() {
        GILogUtil.i("strChooseValue==" + strChooseValue);
        return strChooseValue;
    }

    public void setSelectUserList(List<QDUser> selectUserList) {
        this.selectUserList = selectUserList;
    }

    public void setStrChooseValue(String strChooseValue) {
        this.strChooseValue = strChooseValue;
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
        if (isPaved) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.main_bg));
            holder.icon_TextView.setVisibility(View.GONE);
            holder.count_TextView.setVisibility(View.GONE);
            holder.name_TextView.setTextColor(context.getResources().getColor(R.color.font_content));
            holder.name_TextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.icon_TextView.setVisibility(View.VISIBLE);
            holder.count_TextView.setVisibility(View.VISIBLE);
            if (isExpanded) {
                holder.icon_TextView.setText(context.getText(R.string.font_exlv_expan1));
            } else {
                holder.icon_TextView.setText(context.getText(R.string.font_exlv_cos1));
            }
        }
        ZxtaJointMemListBean.ZxtaJointMemBean zxtaJointMemBean = zxtaJointMemBeans.get(groupPosition);
        holder.name_TextView.setText(zxtaJointMemBean.getStrName());
        holder.count_TextView.setText(getChildrenCount(groupPosition) + "人");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_contact_child, null);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.contactState_TextView = (TextView) convertView.findViewById(R.id.tv_contact_state);
            holder.gradeName_TextView = (TextView) convertView.findViewById(R.id.tv_gradeName);
            holder.source_TextView = (TextView) convertView.findViewById(R.id.tv_source);
            holder.head_ImageView = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.invitation_Button = convertView.findViewById(R.id.btn_invitation);
            holder.choose_TextView = convertView.findViewById(R.id.tv_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BaseUser memListBean = zxtaJointMemBeans.get(groupPosition).getObjChildList().get(childPosition);
        if (isChoose) {
            holder.invitation_Button.setVisibility(View.GONE);
            if (memListBean.isChecked()) {
                holder.choose_TextView.setVisibility(View.VISIBLE);
            } else {
                holder.choose_TextView.setVisibility(View.GONE);
            }
        } else if (isCommissioner) {
            holder.invitation_Button.setVisibility(View.GONE);
            holder.choose_TextView.setVisibility(View.GONE);
        } else {
            holder.choose_TextView.setVisibility(View.GONE);
            if ("0".equals(memListBean.getStrOpenState())) {
                holder.invitation_Button.setVisibility(View.VISIBLE);
                holder.contactState_TextView.setVisibility(View.VISIBLE);
                holder.invitation_Button.setTag(groupPosition + "," + childPosition);
                holder.invitation_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String s = (String) view.getTag();
                        String[] pos = s.split(",");
                        int g = Integer.parseInt(pos[0]);
                        int c = Integer.parseInt(pos[1]);
                        BaseUser memListBean = zxtaJointMemBeans.get(g).getObjChildList().get(c);
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent();
                        bundle.putBoolean("isPersonInfo", true);
                        bundle.putString("strUrl", strUrl);
                        bundle.putString("headTitle", Utils.getMyString(R.string.string_invitationOpen));
                        intent.setClass(context, WebViewActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.invitation_Button.setVisibility(View.GONE);
                holder.contactState_TextView.setVisibility(View.GONE);
            }
        }
        if (GIStringUtil.isNotBlank(memListBean.getStrSector())) {
            holder.source_TextView.setVisibility(View.VISIBLE);
            holder.source_TextView.setText(memListBean.getStrSector());
        } else {
            holder.source_TextView.setVisibility(View.GONE);
        }
        if (GIStringUtil.isNotBlank(memListBean.getStrFaction())) {
            holder.gradeName_TextView.setVisibility(View.VISIBLE);
            holder.gradeName_TextView.setText(memListBean.getStrFaction());
        } else {
            holder.gradeName_TextView.setVisibility(View.GONE);
        }
        holder.head_ImageView.setVisibility(View.VISIBLE);
        String imageUrl = Utils.formatFileUrl(context, memListBean.getStrPathUrl());
        if (imageUrl != null && !imageUrl.equals("")) {
            GIImageUtil.loadImg(context, holder.head_ImageView, imageUrl, 1);
        } else {
            holder.head_ImageView.setImageResource(R.mipmap.img_person);
        }
        holder.name_TextView.setText(memListBean.getStrName());
        convertView.setTag(R.id.tag_gpos, groupPosition);
        convertView.setTag(R.id.tag_cpos, childPosition);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int gpos = (int) view.getTag(R.id.tag_gpos);
                int cpos = (int) view.getTag(R.id.tag_cpos);
                BaseUser memListBean = zxtaJointMemBeans.get(gpos).getObjChildList().get(cpos);
                if (isChoose) { // 选择人
                    String strUserId = memListBean.getStrUserId();
                    String strName = memListBean.getStrName();
                    if (memListBean.isChecked()) {
                        memListBean.setChecked(false);
                        if (AppConfigUtil.isUseIM(context)) {
                            for (QDUser qdUser : selectUserList) {
                                if (qdUser.getId().equals(memListBean.getStrQDUserId())) {
                                    selectUserList.remove(qdUser);
                                    break;
                                }
                            }
                        }
                        strChooseValue = strChooseValue.replaceAll("u/" + strUserId + "/" + strName + ",", "");
                        zxtaJointMemBeans.get(gpos).getObjChildList().set(cpos, memListBean);
                        notifyDataSetChanged();
                    } else if (strChooseValue.contains(memListBean.getStrName() + ",")) {
                        GIToastUtil.showMessage(context, "已经添加过了");
                    } else {
                        if (AppConfigUtil.isUseIM(context) && GIStringUtil.isNotBlank(memListBean.getStrQDUserId())) {
                            QDUser user = new QDUser();
                            user.setId(memListBean.getStrQDUserId());
                            user.setName(memListBean.getStrName());
                            selectUserList.add(user);
                        }
                        if (isSingle) {
                            strChooseValue = "";
                            for (BaseUser memListBean1 : zxtaJointMemBeans.get(gpos).getObjChildList()) {
                                memListBean1.setChecked(false);
                            }
                        }
                        strChooseValue = strChooseValue + "u/" + strUserId + "/" + strName + ",";
                        memListBean.setChecked(true);
                        zxtaJointMemBeans.get(gpos).getObjChildList().set(cpos, memListBean);
                        notifyDataSetChanged();
                    }
                } else if (isCommissioner) { // 进入委员履职
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();
                    bundle.putString("strUserId", memListBean.getStrUserId());
                    bundle.putString("strLinkName", memListBean.getStrName());
                    bundle.putString("strLinkId", memListBean.getStrLinkId());
                    intent.setClass(context, MemberDuty_MainActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else { // 进入通讯录详情
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();
                    bundle.putString("strUserId", memListBean.getStrUserId());
                    bundle.putString("strLinkName", memListBean.getStrName());
                    bundle.putString("strLinkId", memListBean.getStrLinkId());
                    bundle.putBoolean("isShowChatBtn", true);
                    intent.setClass(context, Contact_DetailActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < zxtaJointMemBeans.size(); i++) {
            String sortStr = zxtaJointMemBeans.get(i).getStrName();
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

    private class ViewHolder {
        private TextView icon_TextView;
        private TextView name_TextView;
        private TextView contactState_TextView;
        private RoundImageView head_ImageView;
        private TextView count_TextView;
        private TextView gradeName_TextView;//党派
        private TextView source_TextView;//界别
        private Button invitation_Button;
        private GITextView choose_TextView;//选择
    }
}