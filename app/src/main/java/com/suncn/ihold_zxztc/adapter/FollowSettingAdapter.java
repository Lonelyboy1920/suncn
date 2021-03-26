package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.circle.FollowSettingActivity;
import com.suncn.ihold_zxztc.bean.CircleTagListBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import skin.support.content.res.SkinCompatResources;

/**
 * 首页新闻adapter
 */
public class FollowSettingAdapter extends BaseQuickAdapter<CircleTagListBean.CircleTagBean, BaseViewHolder> {
    private List<CircleTagListBean.CircleTagBean> objList;
    private Context context;
    private int intType;//-1可删除
    private String strClickInfo;//点击的栏目

    public void setObjList(List<CircleTagListBean.CircleTagBean> objList) {
        this.objList = objList;
    }

    public FollowSettingAdapter(Context context, int type) {
        super(R.layout.view_lv_item_column_setting);
        intType = type;
        this.context = context;
    }

    public List<CircleTagListBean.CircleTagBean> getObjList() {
        return objList;
    }

    public int getIntType() {
        return intType;
    }

    public void setIntType(int intType) {
        this.intType = intType;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CircleTagListBean.CircleTagBean circleTagBean) {
        TextView view = baseViewHolder.getView(R.id.tv_name);
        view.setText(circleTagBean.getStrLabelName());
        baseViewHolder.getView(R.id.tv_icon).setVisibility(View.GONE);
        baseViewHolder.getView(R.id.tv_delete).setVisibility(View.GONE);
        if (circleTagBean.getIntFollow() == 1) {
            ((FollowSettingActivity) context).strCodeSet.add(circleTagBean.getStrCode());
            ((FollowSettingActivity) context).strNameSet.add(circleTagBean.getStrLabelName());
            view.setTextColor(SkinCompatResources.getColor(context,R.color.view_head_bg));
            baseViewHolder.getView(R.id.rl_info).setBackgroundResource(R.drawable.shape_item_column_checked_bg);
        }else {
            baseViewHolder.getView(R.id.rl_info).setBackgroundResource(R.drawable.shape_item_column_normal_bg);
            view.setTextColor(context.getResources().getColor(R.color.font_title));
        }
    }
}
