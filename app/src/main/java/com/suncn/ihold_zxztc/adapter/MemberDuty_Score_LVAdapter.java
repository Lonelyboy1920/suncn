package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MemRecordScoreBean.AbilityAnalysisBean.RadarIndicatorListBean;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;

import java.util.List;

/**
 * 履职得分项目adapter
 */
public class MemberDuty_Score_LVAdapter extends BaseAdapter {
    private List<RadarIndicatorListBean> objList;
    private Context context;

    public MemberDuty_Score_LVAdapter(Context context, List<RadarIndicatorListBean> radarIndicatorList) {
        this.context = context;
        this.objList = radarIndicatorList;
    }

    @Override
    public int getCount() {
        if (objList != null)
            return objList.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return objList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_rv_memberduty_score, null);
            viewHolder.name_TextView = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.score_TextView = (TextView) view.findViewById(R.id.tv_score);
            viewHolder.icon_TextView = (TextView) view.findViewById(R.id.tv_icon);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        RadarIndicatorListBean objInfo = objList.get(i);
        viewHolder.name_TextView.setText(objInfo.getStrTypeName());
        viewHolder.score_TextView.setText(objInfo.getDbScore() + "");
        if (ProjectNameUtil.isSZSZX(context)) {
            viewHolder.icon_TextView.setVisibility(View.GONE);
        }
        return view;
    }

    private static class ViewHolder {
        private TextView name_TextView;
        private TextView score_TextView;
        private TextView icon_TextView;
    }
}
