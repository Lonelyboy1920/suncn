package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ObjTransactBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 标题栏下拉选择列表Adapter
 */
public class SessionCode_LVAdapter extends BaseAdapter {
    private ArrayList<SessionListBean.SessionBean> sessionBeans; // 提案列表届次
    private List<ObjTransactBean> transactBeans; // 办理追踪详情单位
    private String[] valueArray; // 征集通知列表年份
    private Context context;

    public SessionCode_LVAdapter(Context context, ArrayList<SessionListBean.SessionBean> sessionBeans) {
        this.context = context;
        this.sessionBeans = sessionBeans;
    }

    public SessionCode_LVAdapter(Context context, List<ObjTransactBean> transactBeans) {
        this.context = context;
        this.transactBeans = transactBeans;
    }

    public SessionCode_LVAdapter(Context context, String[] yearArray) {
        this.context = context;
        this.valueArray = yearArray;
    }

    public int getCount() {
        if (sessionBeans != null)
            return sessionBeans.size();
        else if (transactBeans != null)
            return transactBeans.size();
        else if (valueArray != null)
            return valueArray.length;
        return 0;
    }

    public Object getItem(int position) {
        if (sessionBeans != null)
            return sessionBeans.get(position);
        else if (transactBeans != null)
            return transactBeans.get(position);
        else if (valueArray != null)
            return valueArray[position];
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.popup_lv_item_sessioncode, null);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (sessionBeans != null)
            holder.name_TextView.setText(sessionBeans.get(position).getStrSessionName());
        else if (transactBeans != null)
            holder.name_TextView.setText(transactBeans.get(position).getStrAttendUnitName());
        else if (valueArray != null)
            holder.name_TextView.setText(valueArray[position]);
        return convertView;
    }

    private static class ViewHolder {
        private TextView name_TextView;
    }
}
