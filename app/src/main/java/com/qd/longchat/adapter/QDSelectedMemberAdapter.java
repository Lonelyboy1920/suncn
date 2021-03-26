package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.dao.QDDept;
import com.qd.longchat.R;
import com.longchat.base.dao.QDUser;
import com.qd.longchat.holder.QDSelectedMemberHolder;
import com.qd.longchat.util.QDBitmapUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/3 下午2:03
 */
public class QDSelectedMemberAdapter extends BaseAdapter {

    private Context context;
    private List<QDUser> userList;
    private List<QDDept> deptList;
    private OnRemoveListener listener;

    public QDSelectedMemberAdapter(Context context, List<QDUser> userList, List<QDDept> deptList, OnRemoveListener listener) {
        this.context = context;
        this.userList = userList;
        this.deptList = deptList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return deptList.size() + userList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        QDSelectedMemberHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_selected_member, null);
            holder = new QDSelectedMemberHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDSelectedMemberHolder) convertView.getTag();
        }
        final int size = deptList.size();
        if (size != 0 && size > position) {
            QDDept dept = deptList.get(position);
            holder.ivItemIcon.setImageResource(R.mipmap.im_contact_item_company);
            holder.tvItemName.setText(dept.getName());
        } else {
            QDUser user = userList.get(position - size);
            QDBitmapUtil.getInstance().createAvatar(context, user.getId(), user.getName(), user.getPic(), holder.ivItemIcon);
            holder.tvItemName.setText(user.getName());
        }
        holder.tvItemRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (size != 0 && size > position) {
                    listener.onRemoveDept(deptList.get(position));
                } else {
                    listener.onRemoveUser(userList.get(position - size));
                }
            }
        });
        return convertView;
    }

    public void setUserList(List<QDUser> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void setDeptList(List<QDDept> deptList) {
        this.deptList = deptList;
        notifyDataSetChanged();
    }

    public interface OnRemoveListener {
        void onRemoveUser(QDUser user);

        void onRemoveDept(QDDept dept);
    }
}
