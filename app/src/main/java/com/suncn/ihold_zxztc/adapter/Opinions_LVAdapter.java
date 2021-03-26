package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.PublicOpinionListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;

/**
 * 公开刊物列表（社情民意、文史资料）
 */
public class Opinions_LVAdapter extends BaseAdapter {
    private ArrayList<PublicOpinionListBean.PubListBean> opinionsList;
    private Context context;

    public Opinions_LVAdapter(Context context, ArrayList<PublicOpinionListBean.PubListBean> opinionsLists) {
        this.context = context;
        this.opinionsList = opinionsLists;
    }

    public void setOpinionsList(ArrayList<PublicOpinionListBean.PubListBean> opinionsList) {
        this.opinionsList = opinionsList;
    }

    public ArrayList<PublicOpinionListBean.PubListBean> getOpinionsList() {
        return opinionsList;
    }

    public int getCount() {
        if (opinionsList != null)
            return opinionsList.size();
        return 0;
    }

    public Object getItem(int position) {
        return opinionsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_rv_publicopinion_top, null);
            holder.icon_ImageView = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.main_RelativeLayout = convertView.findViewById(R.id.rl_main);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PublicOpinionListBean.PubListBean opinionsBean = opinionsList.get(position);
        String strUrl = Utils.formatFileUrl(context, opinionsBean.getStrPhotoPath());
        //ImageLoader.getInstance().displayImage(strUrl, holder.icon_ImageView, options);
        holder.name_TextView.setText(opinionsBean.getStrType() + "\n" + opinionsBean.getStrIssue());
        return convertView;
    }

    private static class ViewHolder {
        private RelativeLayout main_RelativeLayout;
        private ImageView icon_ImageView;
        private TextView name_TextView;
    }

}
