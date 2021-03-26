package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.CollectionListBean;
import com.suncn.ihold_zxztc.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * 活动成果adpter
 */
public class ActivityResult_RVAdapter extends BaseQuickAdapter<CollectionListBean.CollectionList, ActivityResult_RVAdapter.ViewHolder> {
    private Context context;

    public ActivityResult_RVAdapter(Context context) {
        super(R.layout.item_rv_mycollection);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, CollectionListBean.CollectionList objInfo) {
        if (objInfo.getImageWithVideoPathList().size() > 0) {
            GIImageUtil.loadImg(context, viewHolder.ivImg, Utils.formatFileUrl(context, objInfo.getImageWithVideoPathList().get(0).getStrImagePath()), 1);
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
