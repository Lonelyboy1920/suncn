package com.suncn.ihold_zxztc.adapter;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.TabBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import skin.support.content.res.SkinCompatResources;

public class PublicSearch_tabAdapter extends BaseQuickAdapter<TabBean, BaseViewHolder> {
    private String strSelectId = "";
    private Activity activity;

    public PublicSearch_tabAdapter(@Nullable List<TabBean> data, Activity activity) {
        super(R.layout.item_public_tab, data);
        this.activity = activity;
    }

    public void setStrSelectId(String strSelectId) {
        this.strSelectId = strSelectId;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, TabBean tabBean) {
        ((TextView) baseViewHolder.getView(R.id.tv_count)).setText(tabBean.getIntCount() + "");
        ((TextView) baseViewHolder.getView(R.id.tv_name)).setText(tabBean.getStrName());
        if (tabBean.getStrType().equals(strSelectId)) {
            ((TextView) baseViewHolder.getView(R.id.tv_count)).setTextColor(SkinCompatResources.getColor(activity,R.color.view_head_bg));
            ((LinearLayout) baseViewHolder.getView(R.id.ll_main)).setBackground(SkinCompatResources.getDrawable(activity,R.drawable.checked));
        } else {
            ((LinearLayout) baseViewHolder.getView(R.id.ll_main)).setBackground(activity.getResources().getDrawable(R.drawable.uncheck));
            ((TextView) baseViewHolder.getView(R.id.tv_count)).setTextColor(activity.getResources().getColor(R.color.font_title));
        }
    }
}
