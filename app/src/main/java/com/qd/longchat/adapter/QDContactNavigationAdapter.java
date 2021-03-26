package com.qd.longchat.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qd.longchat.R;
import com.qd.longchat.holder.QDContactNavigationHolder;
import com.qd.longchat.model.QDContact;

import java.util.List;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/30 下午4:07
 * 通讯录头部导航栏adapter
 */

public class QDContactNavigationAdapter extends RecyclerView.Adapter<QDContactNavigationHolder> {

    private List<QDContact> contactList;
    private Context context;
    private OnNavigationItemClickListener listener;

    public QDContactNavigationAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public QDContactNavigationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact_navigation, parent, false);
        QDContactNavigationHolder holder = new QDContactNavigationHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QDContactNavigationHolder holder, final int position) {
        QDContact contact = contactList.get(position);
        String name = contact.getName();
        if (name.length() > 5) {
            name = name.substring(0, 5) + "...";
        }
        holder.tvNavigationName.setText(name);
        if (position != getItemCount() - 1 || position == 0) {
            holder.ivNavigationIcon.setVisibility(View.VISIBLE);
            holder.tvNavigationName.setTextColor(context.getResources().getColor(R.color.colorNavigationText));
        } else {
            holder.ivNavigationIcon.setVisibility(View.GONE);
            holder.tvNavigationName.setTextColor(context.getResources().getColor(R.color.colorNavigationTextGray));
        }
        holder.tvNavigationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == getItemCount() -1 ) {
                    return;
                }
                listener.navigationItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (contactList == null) {
            return 0;
        }
        return contactList.size();
    }

    public List<QDContact> getContactList() {
        return contactList;
    }

    public void setContactList(List<QDContact> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public void setOnNavigationItemClickListener(OnNavigationItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnNavigationItemClickListener {
        void navigationItemClick(int position);
    }
}
