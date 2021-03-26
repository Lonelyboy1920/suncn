package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.bean.CircleListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 圈子主界面头部水平列表的Adapter（推荐关注/我的关注）
 */
public class Circle_MainHeader_RVAdapter extends BaseQuickAdapter<CircleListBean.RecommendBean, BaseViewHolder> {
    private Context context;
    private boolean isMyFollow;

    /**
     * 推荐关注
     */
    public Circle_MainHeader_RVAdapter(BaseFragment fragment) {
        super(R.layout.item_rv_circle_main_header_dynamic);
        this.context = fragment.getContext();
    }

    /**
     * 我的关注
     */
    public Circle_MainHeader_RVAdapter(BaseFragment fragment, boolean isMyFollow) {
        super(R.layout.item_rv_circle_main_header_follow);
        this.context = fragment.getContext();
        this.isMyFollow = isMyFollow;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CircleListBean.RecommendBean objInfo) {
        GIImageUtil.loadImg(context, baseViewHolder.getView(R.id.iv_img), Utils.formatFileUrl(context, objInfo.getStrPhotoPath()), 1);
        baseViewHolder.setText(R.id.tv_name, objInfo.getStrUserName());
        if (!isMyFollow) {
            baseViewHolder.setText(R.id.tv_describe, objInfo.getStrDuty());
            baseViewHolder.setText(R.id.tv_follow, R.string.string_follow);
        }
    }
}
