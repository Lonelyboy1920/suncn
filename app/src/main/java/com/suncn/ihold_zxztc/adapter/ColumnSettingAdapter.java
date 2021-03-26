package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.NewsColumnListBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import skin.support.content.res.SkinCompatResources;

/**
 * 首页新闻adapter
 */
public class ColumnSettingAdapter extends BaseQuickAdapter<NewsColumnListBean.NewsColumnBean, ColumnSettingAdapter.ViewHolder> {
    private List<NewsColumnListBean.NewsColumnBean> objList;
    private Context context;
    private int intType; // -1：我的订阅（编辑状态）；0-我的订阅（正常状态）；1-更多栏目

    public ColumnSettingAdapter(Context context, int type) {
        super(R.layout.view_lv_item_column_setting);
        intType = type;
        this.context = context;
    }

    public List<NewsColumnListBean.NewsColumnBean> getObjList() {
        return objList;
    }

    public void setObjList(List<NewsColumnListBean.NewsColumnBean> objList) {
        this.objList = objList;
    }

    public int getIntType() {
        return intType;
    }

    public void setIntType(int intType) {
        this.intType = intType;
    }


    @Override
    protected void convert(@NotNull ViewHolder viewHolder, NewsColumnListBean.NewsColumnBean objInfo) {
        viewHolder.name_TextView.setText(objInfo.getStrName());
        switch (intType) {
            case 0: // 我的订阅（正常状态）
            case -1: // 我的订阅（编辑状态）
                viewHolder.addTextView.setVisibility(View.GONE);
                viewHolder.delete_TextView.setVisibility(View.INVISIBLE);
                String strClickInfo = GISharedPreUtil.getString(context, "strClickInfo", "-1");//点击的栏目
                if (strClickInfo.equals(objInfo.getStrId())) { // 选中的栏目
                    viewHolder.info_RelativeLayout.setBackgroundResource(R.drawable.shape_item_column_checked_bg);
                    viewHolder.name_TextView.setTextColor(SkinCompatResources.getColor(context,R.color.view_head_bg));
                } else {
                    viewHolder. info_RelativeLayout.setBackgroundResource(R.drawable.shape_item_column_normal_bg);
                    if (!objInfo.isCanUnsubscribe()) { // 不可取消订阅的栏目
                        viewHolder.name_TextView.setTextColor(context.getResources().getColor(R.color.font_source));
                    } else {
                        viewHolder. name_TextView.setTextColor(context.getResources().getColor(R.color.font_title));
                    }
                }

                if (intType == -1 && objInfo.isCanUnsubscribe()) {
                    viewHolder.delete_TextView.setVisibility(View.VISIBLE);
                }
                break;
            case 1: // 更多栏目
                viewHolder.addTextView.setVisibility(View.VISIBLE);
                viewHolder.delete_TextView.setVisibility(View.GONE);
                break;
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private GITextView name_TextView;
        private GITextView addTextView;
        private GITextView delete_TextView;
        private RelativeLayout info_RelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            info_RelativeLayout = itemView.findViewById(R.id.rl_info);
            name_TextView = itemView.findViewById(R.id.tv_name);
            addTextView = itemView.findViewById(R.id.tv_icon);
            delete_TextView = itemView.findViewById(R.id.tv_delete);
        }


    }
}
