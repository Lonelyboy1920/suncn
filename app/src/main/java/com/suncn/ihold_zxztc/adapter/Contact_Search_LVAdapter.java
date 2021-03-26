package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.longchat.base.dao.QDUser;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.duty.MemberDuty_MainActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.activity.message.Contact_DetailActivity;
import com.suncn.ihold_zxztc.bean.BaseUser;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询委员、机关列表
 */
public class Contact_Search_LVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<BaseUser> baseUsers;
    private String strChooseValue = "";
    private boolean isChoose; // 是否是选择操作
    private boolean isCommissioner; // 是机关查看党派界别专委会
    private List<QDUser> selectUserList = new ArrayList<>(); //已选的人员列表;
    private String strUrl;

    public Contact_Search_LVAdapter(Context context, ArrayList<BaseUser> baseUsers, boolean isChoose) {
        this.context = context;
        this.baseUsers = baseUsers;
        this.isChoose = isChoose;
    }

    public void setStrUrl(String strUrl) {
        this.strUrl = strUrl;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public void setCommissioner(boolean commissioner) {
        isCommissioner = commissioner;
    }

    public String getStrChooseValue() {
        GILogUtil.i("strChooseValue==" + strChooseValue);
        return strChooseValue;
    }

    public void setStrChooseValue(String strChooseValue) {
        this.strChooseValue = strChooseValue;
    }

    public void setsearchPersonUnitLists(ArrayList<BaseUser> searchPersonUnitLists) {
        this.baseUsers = searchPersonUnitLists;
    }

    public List<QDUser> getSelectUserList() {
        return selectUserList;
    }

    public void setSelectUserList(List<QDUser> selectUserList) {
        this.selectUserList = selectUserList;
    }

    public ArrayList<BaseUser> getBaseUsers() {
        return baseUsers;
    }

    public int getCount() {
        if (baseUsers != null)
            return baseUsers.size();
        return 0;
    }

    public Object getItem(int position) {
        return baseUsers.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlv_contact_child, null);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.gradeName_TextView = (TextView) convertView.findViewById(R.id.tv_gradeName);
            holder.source_TextView = (TextView) convertView.findViewById(R.id.tv_source);
            holder.head_ImageView = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.check_TextView = convertView.findViewById(R.id.tv_check);
            holder.invitation_Button = convertView.findViewById(R.id.btn_invitation);
            holder.contactState_TextView = (TextView) convertView.findViewById(R.id.tv_contact_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BaseUser baseUser = baseUsers.get(position);
        if (GIStringUtil.isBlank(baseUser.getStrFaction())) {
            holder.gradeName_TextView.setVisibility(View.GONE);
        } else {
            holder.gradeName_TextView.setVisibility(View.VISIBLE);
            holder.gradeName_TextView.setText(baseUser.getStrFaction());
        }
        if (GIStringUtil.isBlank(baseUser.getStrSector())) {
            holder.source_TextView.setVisibility(View.GONE);
        } else {
            holder.source_TextView.setVisibility(View.VISIBLE);
            holder.source_TextView.setText(baseUser.getStrSector());
        }
        String imageUrl = Utils.formatFileUrl(context, baseUser.getStrPathUrl());
        GIImageUtil.loadImg(context, holder.head_ImageView, imageUrl, 1);
        holder.name_TextView.setText(baseUser.getStrName());
        if (baseUser.isChecked()) {
            holder.check_TextView.setVisibility(View.VISIBLE);
        } else {
            holder.check_TextView.setVisibility(View.GONE);
        }
        if (isChoose) {
            holder.invitation_Button.setVisibility(View.GONE);
        } else if (isCommissioner) {
            holder.invitation_Button.setVisibility(View.GONE);
        } else {
            if ("0".equals(baseUser.getStrOpenState())) {
                holder.invitation_Button.setVisibility(View.VISIBLE);
                holder.contactState_TextView.setVisibility(View.VISIBLE);
                holder.invitation_Button.setTag(position + "");
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
        convertView.setId(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUser baseUser = baseUsers.get(v.getId());
                if (isChoose) {
                    if (baseUser.isChecked()) {
                        baseUser.setChecked(false);
                        if ( AppConfigUtil.isUseQDIM(context)) {
                            for (QDUser qdUser : selectUserList) {
                                if (qdUser.getId().equals(baseUser.getStrQDUserId())) {
                                    selectUserList.remove(qdUser);
                                    break;
                                }
                            }
                        }
                        strChooseValue = strChooseValue.replaceAll("u/" + baseUser.getStrUserId() + "/" + baseUser.getStrName() + ",", "");
                        baseUsers.set(v.getId(), baseUser);
                        notifyDataSetChanged();
                    } else if (strChooseValue.contains(baseUser.getStrName() + ",")) {
                        GIToastUtil.showMessage(context, "已经添加过了");
                    } else {
                        baseUser.setChecked(true);
                        if (AppConfigUtil.isUseQDIM(context) &&  GIStringUtil.isNotBlank(baseUser.getStrQDUserId())) {
                            QDUser user = new QDUser();
                            user.setId(baseUser.getStrQDUserId());
                            user.setName(baseUser.getStrName());
                            selectUserList.add(user);
                        }
                        strChooseValue = strChooseValue + "u/" + baseUser.getStrUserId() + "/" + baseUser.getStrName() + ",";
                        baseUsers.set(v.getId(), baseUser);
                        notifyDataSetChanged();
                    }
                } else if (isCommissioner) { // 进入委员履职
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent();
                    bundle.putString("strUserId", baseUser.getStrUserId());
                    bundle.putString("strLinkName", baseUser.getStrName());
                    bundle.putString("strLinkId", baseUser.getStrLinkId());
                    intent.setClass(context, MemberDuty_MainActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
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
        return convertView;
    }

    private class ViewHolder {
        private TextView name_TextView;
        private TextView gradeName_TextView;
        private TextView check_TextView;
        private RoundImageView head_ImageView;
        private TextView source_TextView;
        private Button invitation_Button;
        private TextView contactState_TextView;
    }
}
