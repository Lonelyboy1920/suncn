package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ThemeListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 网络议政热门主题列表adpter
 */
public class Theme_Hot_RVAdapter extends BaseQuickAdapter<ThemeListBean.ThemeBean, Theme_Hot_RVAdapter.ViewHolder> {
    private Context context;

    public Theme_Hot_RVAdapter(Context context) {
        super(R.layout.item_rv_theme_hot);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ThemeListBean.ThemeBean objInfo) {
        GIImageUtil.loadImg(context, viewHolder.pic_ImageView, Utils.formatFileUrl(context, objInfo.getStrRankImgUrl()), 1);
        GIImageUtil.setBgForImage(context, viewHolder.info_LinearLayout, Utils.formatFileUrl(context, objInfo.getStrRankImgBgUrl()));
        viewHolder.look_TextView.setText(String.valueOf(objInfo.getIntBrowseCount()));
        viewHolder. reply_TextView.setText(String.valueOf(objInfo.getIntDiscussCount()));
        viewHolder.title_TextView.setText(objInfo.getStrTitle());
        viewHolder. date_TextView.setText(objInfo.getStrPubDate());
    }

    public class ViewHolder extends BaseViewHolder {
        private ImageView pic_ImageView;
        private TextView title_TextView;
        private TextView look_TextView;
        private TextView reply_TextView;
        private TextView date_TextView;
        private LinearLayout info_LinearLayout;

        public ViewHolder(View view) {
            super(view);
            pic_ImageView = (ImageView) view.findViewById(R.id.iv_pic);
            title_TextView = (TextView) view.findViewById(R.id.tv_title);
            look_TextView = (TextView) view.findViewById(R.id.tv_look);
            reply_TextView = (TextView) view.findViewById(R.id.tv_reply);
            date_TextView = (TextView) view.findViewById(R.id.tv_date);
            info_LinearLayout = view.findViewById(R.id.ll_info);
        }

    }
}
