package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ApplicationListBean;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;

import org.jetbrains.annotations.NotNull;

/**
 * 应用adapter
 */
public class ApplicationListAdapter extends BaseQuickAdapter<ApplicationListBean.ApplicationBean, ApplicationListAdapter.ViewHolder> {
    private Context context;


    public ApplicationListAdapter(Context context) {
        super(R.layout.item_rv_application);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ApplicationListBean.ApplicationBean objInfo) {
        viewHolder.name_TextView.setText(objInfo.getStrName());
        if (objInfo.getIntCount() != 0) {
            viewHolder.count_TextView.setVisibility(View.VISIBLE);
            viewHolder. count_TextView.setText(objInfo.getIntCount() + "");
        } else {
            viewHolder. count_TextView.setVisibility(View.INVISIBLE);
        }
        String strType = objInfo.getStrType();
        switch (strType) {
            case "01": // 我的提案
                viewHolder.  icon_ImageView.setBackgroundResource(R.mipmap.zxta_icon);
                break;
            case "02": // 社情民意
                viewHolder. icon_ImageView.setBackgroundResource(R.mipmap.sqmy_icon);
                break;
            case "03": // 我的会议
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.wdhy_icon);
                break;
            case "04": // 会议发言
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.hyfy_icon);
                break;
            case "05": // 我的活动
                viewHolder. icon_ImageView.setBackgroundResource(R.mipmap.wdhd_icon);
                break;
            case "08":
                viewHolder. icon_ImageView.setBackgroundResource(R.mipmap.icon_xxyd);
                break;
            case "35"://网络议政
            case "37"://网络议政（机关）
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.ztyz_icon);
                break;
            case "06": // 政协委员、我的履职
                if (ProjectNameUtil.isGYSZX(context)) {
                    viewHolder. icon_ImageView.setBackgroundResource(R.mipmap.spxs_icon);
                } else if (ProjectNameUtil.isGZSZX(context)) {
                    viewHolder.  icon_ImageView.setBackgroundResource(R.mipmap.qhfw_icon);
                } else {
                    viewHolder.  icon_ImageView.setBackgroundResource(R.mipmap.icon_wdlz);
                }
                break;
            case "14"://提案审核
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.icon_tash);
                break;
            case "15"://提案管理(追踪)
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.icon_tazz);
                break;
            case "16"://委员管理
            case "36"://个人信息
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.icon_wylz);
                break;
            case "17"://会议管理
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.icon_hygl);
                break;
            case "18"://活动管理
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.icon_hdgl);
                break;
            case "19"://社情民意管理
                viewHolder. icon_ImageView.setBackgroundResource(R.mipmap.icon_sqmy);
                break;
            case "20"://会议发言
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.icon_hyfy);
                break;
            case "22"://提案交办（承办系统）
                viewHolder. icon_ImageView.setBackgroundResource(R.mipmap.icon_tash);
                break;
            case "23"://刊物交办（承办系统）
                viewHolder. icon_ImageView.setBackgroundResource(R.mipmap.icon_kwjb);
                break;
            case "24"://办理追踪
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.icon_tazz);
                break;
            case "25"://提案办理(承办端)
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.icon_tabl);
                break;
            case "30"://委员查询
                viewHolder.icon_ImageView.setBackgroundResource(R.mipmap.icon_wycx);
                break;
            default:
                break;
        }
    }


    public class ViewHolder extends BaseViewHolder {
        private ImageView icon_ImageView;
        private TextView name_TextView;
        private TextView count_TextView;

        public ViewHolder(View itemView) {
            super(itemView);
            icon_ImageView = (ImageView) itemView.findViewById(R.id.iv_icon);
            name_TextView = (TextView) itemView.findViewById(R.id.tv_name);
            count_TextView = (TextView) itemView.findViewById(R.id.tv_count);
        }

    }
}
