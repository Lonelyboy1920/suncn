package com.qd.longchat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.databases.QDDeptHelper;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDContactActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.holder.QDRootContactHolder;
import com.qd.longchat.model.QDContact;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/29 上午9:46
 */

public class QDRootContactAdapter extends BaseAdapter {

    private Context context;
    private List<QDContact> contactList;
    private int mode;
    private List<String> selectUserIds;
    private List<String> excludedUserIds;
    private List<String> selectDeptIds;
    private OnDeptSelectListener deptSelectListener;
    private int limit;
    private String selectType;
    private boolean isCanCancel;

    public QDRootContactAdapter(Context context) {
        this.context = context;
        this.selectDeptIds = new ArrayList<>();
        contactList = new ArrayList<>();
    }

    public void setCanCancel(boolean canCancel) {
        isCanCancel = canCancel;
    }

    @Override
    public int getItemViewType(int position) {
        return contactList.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return QDContact.COUNT;
    }

    @Override
    public int getCount() {
        if (contactList == null) {
            return 0;
        }
        return contactList.size();
    }

    @Override
    public QDContact getItem(int position) {
        return contactList.get(position);
    }

    public List<QDContact> getContactList() {
        return this.contactList;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDRootContactHolder holder;
        QDContact contact = contactList.get(position);
        if (convertView == null) {
            int type = contact.getType();
            if (type == QDContact.TYPE_SPACE || type == QDContact.TYPE_TITLE) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_text, null);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, null);
            }
            holder = QDRootContactHolder.init(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDRootContactHolder) convertView.getTag();
        }
        bindView(holder, contact, position);
        return convertView;
    }

    private void bindView(final QDRootContactHolder holder, final QDContact contact, int position) {
        int type = contact.getType();
        switch (type) {
            case QDContact.TYPE_SPACE:
            case QDContact.TYPE_TITLE:
                holder.itemTitle.setText(contact.getName());
                holder.itemLine.setVisibility(View.GONE);
                holder.itemIndex.setVisibility(View.GONE);
                break;
            case QDContact.TYPE_NEW_FRIEND:
                holder.itemIcon.setImageResource(R.mipmap.im_contact_item_friend);
                holder.itemName.setText(contact.getName());
                holder.itemLine.setVisibility(View.VISIBLE);
                holder.itemFunc.setBackgroundResource(R.drawable.ic_tab_red_round);
                String funcName = contact.getFuncName();
                if (TextUtils.isEmpty(funcName)) {
                    holder.itemFunc.setVisibility(View.GONE);
                } else {
                    holder.itemFunc.setText(funcName);
                    holder.itemFunc.setVisibility(View.VISIBLE);
                }
                holder.itemPhone.setVisibility(View.GONE);
                holder.itemIndex.setVisibility(View.GONE);
                break;
            case QDContact.TYPE_CONTACT:
                holder.itemIcon.setImageResource(R.mipmap.im_contact_item_company);
                holder.itemName.setText(contact.getName());
                holder.itemFunc.setVisibility(View.GONE);
                holder.itemLine.setVisibility(View.GONE);
                holder.itemPhone.setVisibility(View.GONE);
                holder.itemIndex.setVisibility(View.GONE);
                break;
            case QDContact.TYPE_SELF_DEPT:
                holder.itemIcon.setBackgroundResource(R.mipmap.im_contact_item_self_dept);
                holder.itemName.setText(contact.getName());
                holder.itemRightName.setText(contact.getFuncName());
                holder.itemFunc.setVisibility(View.GONE);
                holder.itemLine.setVisibility(View.VISIBLE);
                holder.itemPhone.setVisibility(View.GONE);
                holder.itemIndex.setVisibility(View.GONE);
                break;
            case QDContact.TYPE_GROUP:
                holder.itemIcon.setBackgroundResource(R.mipmap.im_contact_item_group);
                holder.itemName.setText(contact.getName());
                holder.itemRightName.setText(contact.getFuncName());
                holder.itemFunc.setVisibility(View.GONE);
                holder.itemLine.setVisibility(View.VISIBLE);
                holder.itemPhone.setVisibility(View.GONE);
                holder.itemIndex.setVisibility(View.GONE);
                break;
            case QDContact.TYPE_DEPT:
            case QDContact.TYPE_COMPANY:
                if (mode == QDContactActivity.MODE_MULTI && type != QDContact.TYPE_COMPANY
                        && (TextUtils.isEmpty(selectType) || (!TextUtils.isEmpty(selectType) && (selectType.equalsIgnoreCase("all") || selectType.equalsIgnoreCase("depts"))))) {
                    holder.itemCheck.setVisibility(View.VISIBLE);
                    holder.itemFunc.setVisibility(View.GONE);
                    holder.itemRightName.setVisibility(View.GONE);
                    if (selectDeptIds.contains(contact.getId())) {
                        holder.itemCheck.setImageResource(R.drawable.ic_round_selected);
                    } else {
                        holder.itemCheck.setImageResource(R.drawable.ic_round_unselected);
                    }

                    holder.itemCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int count = selectDeptIds.size() + selectUserIds.size();
                            if (limit != 0 && limit <= count) {

                                QDUtil.showToast(context, context.getResources().getString(R.string.max_selected) + limit + context.getResources().getString(R.string.str_item));
                                return;
                            }
                            if (selectDeptIds.contains(contact.getId())) {
                                holder.itemCheck.setImageResource(R.drawable.ic_round_unselected);
//                                selectDeptIds.remove(contact.getId());
                                if (deptSelectListener != null) {
                                    deptSelectListener.onDeptSelect(contact, true);
                                }
                            } else {
                                holder.itemCheck.setImageResource(R.drawable.ic_round_selected);
//                                selectDeptIds.add(contact.getId());
                                if (deptSelectListener != null) {
                                    deptSelectListener.onDeptSelect(contact, false);
                                }
                            }
                        }
                    });
                } else {
                    holder.itemCheck.setVisibility(View.GONE);
                    holder.itemFunc.setVisibility(View.VISIBLE);
                    holder.itemRightName.setVisibility(View.VISIBLE);

                    if (type == QDContact.TYPE_DEPT) {
                        holder.itemRightName.setText(QDDeptHelper.getUserCountByDept(contact.getCid(), contact.getId(), contact.getCode()) + "");
                    } else {
                        holder.itemRightName.setText(contact.getFuncName());
                    }
                }
                holder.itemIcon.setBackgroundResource(R.mipmap.im_contact_item_company);
                holder.itemName.setText(contact.getName());
                holder.itemLine.setVisibility(View.VISIBLE);
                holder.itemPhone.setVisibility(View.GONE);
                holder.itemIndex.setVisibility(View.GONE);
                break;
            case QDContact.TYPE_USER:
                if (mode == QDContactActivity.MODE_MULTI &&
                        (TextUtils.isEmpty(selectType) || !TextUtils.isEmpty(selectType) && (selectType.equalsIgnoreCase("all") || selectType.equalsIgnoreCase("user")))) {
                    if (excludedUserIds.contains(contact.getId()) || QDLanderInfo.getInstance().getId().toLowerCase().equals(contact.getId().toLowerCase())) {
                        holder.itemCheck.setVisibility(View.GONE);
                    } else {
                        holder.itemCheck.setVisibility(View.VISIBLE);
                        if (selectUserIds.contains(contact.getId())) {
                            holder.itemCheck.setImageResource(R.drawable.ic_round_selected);
                        } else {
                            holder.itemCheck.setImageResource(R.drawable.ic_round_unselected);
                        }
                    }
                } else {
                    holder.itemCheck.setVisibility(View.GONE);
                }
                QDBitmapUtil.getInstance().createAvatar(context, contact.getId(), contact.getName(), contact.getIcon(), holder.itemIcon);
                holder.itemName.setText(contact.getName());
                holder.itemFunc.setVisibility(View.GONE);
//                if (contact.getId().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
//                    holder.itemSubname.setText("[在线]");
//                } else {
//                    holder.itemSubname.setText("[" + QDUtil.getUserStatus(context, contact.getId()) + "]");
//                }
                String subname = contact.getSubname();
                if (TextUtils.isEmpty(subname)) {
                    subname = context.getResources().getString(R.string.user_detail_not_empty);
                }
                holder.itemSubname.setVisibility(View.VISIBLE);
                holder.itemSubname.setText(subname);
                holder.itemPhone.setVisibility(View.GONE);
                holder.itemIndex.setVisibility(View.GONE);
                break;
            case QDContact.TYPE_LINKMAN:
            case QDContact.TYPE_FRIEND:
                if (position == 0 || !contact.getFuncName().equalsIgnoreCase(contactList.get(position - 1).getFuncName())) {
                    holder.itemIndex.setVisibility(View.VISIBLE);
                    holder.itemIndex.setText(QDUtil.getInitial(contact.getFuncName().toUpperCase()));
                } else {
                    holder.itemIndex.setVisibility(View.GONE);
                }
                if (position != (contactList.size() - 1) && contact.getFuncName().equalsIgnoreCase(contactList.get(position + 1).getFuncName())) {
                    holder.itemLine.setVisibility(View.VISIBLE);
                } else {
                    holder.itemLine.setVisibility(View.GONE);
                }
                if (position == getCount() - 1) {
                    holder.itemLine.setVisibility(View.VISIBLE);
                }
                QDBitmapUtil.getInstance().createAvatar(context, contact.getId(), contact.getName(), contact.getIcon(), holder.itemIcon);
                holder.itemName.setText(contact.getName());
                holder.itemSubname.setVisibility(View.VISIBLE);
                holder.itemPhone.setVisibility(View.VISIBLE);
                final String code = contact.getCode();
                if (TextUtils.isEmpty(code)) {
                    holder.itemPhone.setImageResource(R.drawable.ic_not_have_phone);
                    holder.itemSubname.setText("[" + QDUtil.getUserStatus(context, contact.getId()) + "] ");
                } else {
                    holder.itemPhone.setImageResource(R.drawable.ic_have_phone);
                    holder.itemSubname.setText("[" + QDUtil.getUserStatus(context, contact.getId()) + "] " + contact.getCode());

                    holder.itemPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!AndPermission.hasPermissions(context, Permission.CALL_PHONE)) {
                                QDUtil.getPermission(context, Permission.CALL_PHONE);
                            } else {
                                QDUtil.callPhone(context, code);
                            }
                        }
                    });
                }
                break;
        }
    }

    public void setContactList(List<QDContact> contactList) {
        this.contactList.clear();
        this.contactList.addAll(contactList);
//        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setSelectUserIds(List<String> selectUserIds) {
        this.selectUserIds = selectUserIds;
    }

    public void setExcludedUserIds(List<String> excludedUserIds) {
        this.excludedUserIds = excludedUserIds;
    }

    public List<String> getSelectDeptIds() {
        return selectDeptIds;
    }

    public void setSelectDeptIds(List<String> selectDeptIds) {
        this.selectDeptIds = selectDeptIds;
    }

    public void setDeptSelectListener(OnDeptSelectListener deptSelectListener) {
        this.deptSelectListener = deptSelectListener;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public interface OnDeptSelectListener {
        void onDeptSelect(QDContact contact, boolean isRemove);
    }
}
