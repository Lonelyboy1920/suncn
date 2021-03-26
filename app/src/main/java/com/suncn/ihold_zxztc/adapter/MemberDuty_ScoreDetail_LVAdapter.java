package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MemRecordScoreBean.AbilityAnalysisBean.RadarIndicatorListBean.ExamListBean;

import java.util.List;

/**
 * 履职得分详细列表adapter
 */
public class MemberDuty_ScoreDetail_LVAdapter extends BaseAdapter {
    private List<ExamListBean> objList;
    private Context context;

    public MemberDuty_ScoreDetail_LVAdapter(Context context, List<ExamListBean> radarIndicatorList) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_rv_memberduty_rank, null);
            viewHolder.icon_ImageView = (ImageView) view.findViewById(R.id.iv_icon);
            viewHolder.icon_ImageView.setVisibility(View.GONE);
            viewHolder.name_TextView = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.name1_TextView = (TextView) view.findViewById(R.id.tv_name1);
            viewHolder.name1_TextView.setVisibility(View.VISIBLE);
            viewHolder.score_TextView = (TextView) view.findViewById(R.id.tv_score);
            viewHolder.rank_TextView = (TextView) view.findViewById(R.id.tv_rank);
            viewHolder.name_Layout = (LinearLayout) view.findViewById(R.id.ll_name);
            viewHolder.name_Layout.setVisibility(View.GONE);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ExamListBean objInfo = objList.get(i);
        viewHolder.name1_TextView.setText(objInfo.getStrExamItemName());
        viewHolder.score_TextView.setText(objInfo.getStrScoreTypeName());
        viewHolder.rank_TextView.setText(objInfo.getDbScore() + "");
        return view;
    }

    private static class ViewHolder {
        private ImageView icon_ImageView;
        private TextView name_TextView;
        private TextView name1_TextView;
        private TextView score_TextView;
        private TextView rank_TextView;
        private LinearLayout name_Layout;
    }
}
