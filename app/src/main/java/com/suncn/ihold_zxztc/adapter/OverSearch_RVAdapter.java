package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.OverSearchBean;

import org.jetbrains.annotations.NotNull;

/**
 * 全局搜索Adapter
 */
public class OverSearch_RVAdapter extends BaseQuickAdapter<OverSearchBean.OverSearchList, BaseViewHolder> {
    public OverSearch_RVAdapter(Context context) {
        super(R.layout.item_rv_oversearch_list);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, OverSearchBean.OverSearchList objInfo) {
        TextView tvType;
        TextView tvTitle;
        TextView tvContent;
        TextView tvName;
        TextView tvDate;
        tvType = baseViewHolder.getView(R.id.tv_type);
        tvTitle = baseViewHolder.getView(R.id.tv_title);
        tvContent = baseViewHolder.getView(R.id.tv_content);
        tvName = baseViewHolder.getView(R.id.tv_name);
        tvDate = baseViewHolder.getView(R.id.tv_date);
        if (GIStringUtil.isNotBlank(objInfo.getStrSorceTypeName())) {
            tvType.setVisibility(View.VISIBLE);
            tvType.setText(objInfo.getStrSorceTypeName());
        } else {
            tvType.setVisibility(View.GONE);
        }
        if (GIStringUtil.isNotBlank(objInfo.getStrTitle())) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(GIUtil.showHtmlInfo(objInfo.getStrTitle()));
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        if (GIStringUtil.isNotBlank(objInfo.getStrAbstractContent())) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(GIUtil.showHtmlInfo(objInfo.getStrAbstractContent()));
        } else {
            tvContent.setVisibility(View.GONE);
        }
        if (GIStringUtil.isNotBlank(objInfo.getStrSourceName())) {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(objInfo.getStrSourceName());
        } else {
            tvName.setVisibility(View.GONE);
        }
        if (GIStringUtil.isNotBlank(objInfo.getStrReoprtDate())) {
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText(objInfo.getStrReoprtDate());
        } else {
            tvDate.setVisibility(View.GONE);
        }
    }
}

