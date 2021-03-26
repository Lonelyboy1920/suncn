package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ThemeListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 推荐主题Adapter
 */
public class Theme_RVAdapter extends BaseQuickAdapter<ThemeListBean.ThemeBean, Theme_RVAdapter.ViewHolder> {
    private Context context;

    public Theme_RVAdapter(Context context) {
        super(R.layout.item_rv_theme_footer);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ThemeListBean.ThemeBean objInfo) {
        if (GIStringUtil.isNotBlank(objInfo.getStrHotImgUrl())) {
            viewHolder.ivImg.setVisibility(View.VISIBLE);
            GIImageUtil.loadImg(context, viewHolder.ivImg, Utils.formatFileUrl(context, objInfo.getStrHotImgUrl()), 0);
        } else {
            viewHolder.ivImg.setVisibility(View.GONE);
        }
        viewHolder.tvContent.setText(objInfo.getStrTitle());
        viewHolder.tvDate.setText(objInfo.getStrPubDate());
    }

    public class ViewHolder extends BaseViewHolder {
        private ImageView ivImg;
        private TextView tvContent;
        private TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_img);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvDate = itemView.findViewById(R.id.tv_date);
        }

    }
}
