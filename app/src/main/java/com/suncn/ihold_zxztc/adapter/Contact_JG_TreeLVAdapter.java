package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.longchat.base.dao.QDUser;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.activity.message.Contact_DetailActivity;
import com.suncn.ihold_zxztc.bean.BaseUser;
import com.suncn.ihold_zxztc.treelist.Node;
import com.suncn.ihold_zxztc.treelist.TreeListViewAdapter;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 多级列表Adapter
 */
public class Contact_JG_TreeLVAdapter extends TreeListViewAdapter {
    private Context context;
    private String strChooseValue = "";
    private boolean isChoose; // 是否是选择联名委员操作
    private List<QDUser> selectUserList = new ArrayList<>(); //已选的人员列表;
    private String strUrl;

    public void setStrUrl(String strUrl) {
        this.strUrl = strUrl;
    }
    public Contact_JG_TreeLVAdapter(boolean isChoose, ListView mTree, Context context, List<Node> datas, int defaultExpandLevel) {
        super(mTree, context, datas, defaultExpandLevel);
        this.context = context;
        this.isChoose = isChoose;
    }

    public String getStrChooseValue() {
        GILogUtil.i("strChooseValue==" + strChooseValue);
        return strChooseValue;
    }

    public List<QDUser> getSelectUserList() {
        return selectUserList;
    }

    public void setSelectUserList(List<QDUser> selectUserList) {
        this.selectUserList = selectUserList;
    }

    public void setStrChooseValue(String strChooseValue) {
        this.strChooseValue = strChooseValue;
    }

    @Override
    public View getConvertView(final Node node, int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        holder = new ViewHolder();
        if (node.getPersonUnit() != null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_contact_group, null);
            holder.icon_TextView = (TextView) convertView.findViewById(R.id.tv_icon);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.count_TextView = (TextView) convertView.findViewById(R.id.tv_count);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_contact_child, null);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.contactState_TextView = (TextView) convertView.findViewById(R.id.tv_contact_state);
            holder.gradeName_TextView = (TextView) convertView.findViewById(R.id.tv_gradeName);
            holder.head_ImageView = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.invitation_Button = convertView.findViewById(R.id.btn_invitation);
            holder.invitation_Button.setVisibility(View.GONE);
            holder.choose_TextView = convertView.findViewById(R.id.tv_check);
        }
        convertView.setTag(holder);

        if (node.getPersonUnit() != null) {
            holder.name_TextView.setText(node.getName());
            holder.count_TextView.setText(node.getChildren().size() + "");
            if (node.isExpand()) {
                holder.icon_TextView.setText(context.getString(R.string.font_exlv_expan1));
            } else {
                holder.icon_TextView.setText(context.getString(R.string.font_exlv_cos1));
            }
        } else {
            BaseUser baseUser = node.getBaseUser();
            if (isChoose) {
                holder.invitation_Button.setVisibility(View.GONE);
            } else {
                holder.choose_TextView.setVisibility(View.GONE);
                if ("0".equals(baseUser.getStrOpenState())) {
                    holder.invitation_Button.setVisibility(View.VISIBLE);
                    holder.contactState_TextView.setVisibility(View.VISIBLE);
                    //holder.invitation_Button.setTag(groupPosition + "," + childPosition);
                    holder.invitation_Button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
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
            holder.name_TextView.setText(baseUser.getStrName());
            String imageUrl = Utils.formatFileUrl(context, baseUser.getStrPathUrl());
            GIImageUtil.loadImg(context, holder.head_ImageView, imageUrl, 1);
            if (node.isChecked()) {
                holder.choose_TextView.setVisibility(View.VISIBLE);
            } else {
                holder.choose_TextView.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isChoose) {
                        if (node.isChecked()) {
                            node.setChecked(false);
                            if (AppConfigUtil.isUseQDIM(context)) {
                                for (QDUser qdUser : selectUserList) {
                                    if (qdUser.getId().equals(baseUser.getStrQDUserId())){
                                        selectUserList.remove(qdUser);
                                        break;
                                    }
                                }
                            }
                            strChooseValue = strChooseValue.replaceAll("u/" + baseUser.getStrUserId() + "/" + baseUser.getStrName() + ",", "");
                            notifyDataSetChanged();
                        } else if (strChooseValue.contains(baseUser.getStrName() + ",")) {
                            GIToastUtil.showMessage(context, "已经添加过了");
                        } else {
                            strChooseValue = strChooseValue + "u/" + baseUser.getStrUserId() + "/" + baseUser.getStrName() + ",";
                            if (AppConfigUtil.isUseQDIM(context) &&  GIStringUtil.isNotBlank(baseUser.getStrQDUserId())) {
                                QDUser user=new QDUser();
                                user.setId(baseUser.getStrQDUserId());
                                user.setName(baseUser.getStrName());
                                selectUserList.add(user);
                            }
                            node.setChecked(true);
                            notifyDataSetChanged();
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent();
                        bundle.putString("strUserId", baseUser.getStrUserId());
                        bundle.putString("strLinkName", baseUser.getStrName());
                        bundle.putString("strLinkId", baseUser.getStrLinkId());
                        bundle.putBoolean("isShowChatBtn", true);
                        intent.setClass(context, Contact_DetailActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView icon_TextView;
        private TextView name_TextView;
        private TextView contactState_TextView;
        private RoundImageView head_ImageView;
        private TextView count_TextView;
        private TextView gradeName_TextView;
        private Button invitation_Button;
        private GITextView choose_TextView;//选择
    }
}
