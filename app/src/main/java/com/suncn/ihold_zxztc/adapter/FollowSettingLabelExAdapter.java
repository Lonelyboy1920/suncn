package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.circle.FollowSettingActivity;
import com.suncn.ihold_zxztc.bean.CircleTagListBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FollowSettingLabelExAdapter extends BaseExpandableListAdapter {
    private List<CircleTagListBean.CircleTagBean> objList;
    private Context context;

    public FollowSettingLabelExAdapter(Context context, List<CircleTagListBean.CircleTagBean> objList) {
        this.context = context;
        this.objList = objList;
    }

    public void setObjList(List<CircleTagListBean.CircleTagBean> objList) {
        this.objList = objList;
    }

    @Override
    public int getGroupCount() {
        if (objList != null) {
            return objList.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (objList.get(groupPosition).getObjTagList() != null) {
            return 1;
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return objList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return objList.get(groupPosition).getObjTagList().get(childPosition);
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
        final GroupHolder croupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_followtag_type, parent, false);
            croupHolder = new GroupHolder(convertView);
            convertView.setTag(croupHolder);
        } else {
            croupHolder = (GroupHolder) convertView.getTag();
        }
        croupHolder.tv_name.setText(objList.get(groupPosition).getStrTypeName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder childHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        GridLayoutManager layoutManage = new GridLayoutManager(context, 4);
        childHolder.recyclerView.setLayoutManager(layoutManage);
        FollowSettingAdapter adapter = new FollowSettingAdapter(context, 0);
        childHolder.recyclerView.setAdapter(adapter);
        adapter.setList(objList.get(groupPosition).getObjTagList());
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                CircleTagListBean.CircleTagBean circleTagBean = objList.get(groupPosition).getObjTagList().get(position);
                if (circleTagBean.getIntFollow() == 1) {
                    circleTagBean.setIntFollow(0);
                    ((FollowSettingActivity) context).strCodeSet.remove(circleTagBean.getStrCode());
                    ((FollowSettingActivity) context).strNameSet.remove(circleTagBean.getStrLabelName());
                } else {
                    circleTagBean.setIntFollow(1);
                    ((FollowSettingActivity) context).strCodeSet.add(circleTagBean.getStrCode());
                    ((FollowSettingActivity) context).strNameSet.add(circleTagBean.getStrLabelName());
                }
                adapter.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class ChildHolder {
        private RecyclerView recyclerView;
        private TextView tv_name;

        private ChildHolder(View view) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
        }
    }

    private class GroupHolder {
        private TextView tv_name;

        private GroupHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.tv_name);
        }
    }
}
