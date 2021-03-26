package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.NewsColumnListBean;
import java.util.List;

public class PlenaryMeetingGuideAdapter extends BaseAdapter {
    private Context context;
    private List<NewsColumnListBean.NewsColumnBean> objList;

    public PlenaryMeetingGuideAdapter(Context context, List<NewsColumnListBean.NewsColumnBean> objList) {
        this.context = context;
        this.objList = objList;
    }

    @Override
    public int getCount() {
        if (objList != null) {
            return objList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return objList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_plenary_meet_guide, null);
        GITextView tvIcon = convertView.findViewById(R.id.tv_icon);
        TextView tvName = convertView.findViewById(R.id.tv_name);
        NewsColumnListBean.NewsColumnBean info = objList.get(position);
        tvName.setText(info.getStrName());
        if (info.getStrCode().equals("02")) {//会议日程
            tvIcon.setText("\ue610");
        } else if (info.getStrCode().equals("03")) {
            tvIcon.setText("\ue68d");//会议议程
        } else if (info.getStrCode().equals("04")) {
            tvIcon.setText("\ue61c");//全会简报
        } else if (info.getStrCode().equals("05")) {
            tvIcon.setText("\ue60f");//全会名单
        } else if (info.getStrCode().equals("06")) {
            tvIcon.setText("\ue68b");//会议相关事项
        } else if (info.getStrCode().equals("07")) {
            tvIcon.setText("\ue618");//全会通讯录
        } else if (info.getStrCode().equals("08")) {
            tvIcon.setText("\ue68e");//住宿安排
        } else if (info.getStrCode().equals("09")) {
            tvIcon.setText("\ue690");//乘车安排
        }
        return convertView;
    }
}
