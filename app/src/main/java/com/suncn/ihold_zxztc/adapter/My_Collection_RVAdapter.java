package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.CollectionListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class My_Collection_RVAdapter extends BaseQuickAdapter<CollectionListBean.CollectionList, BaseViewHolder>  {
    private Context context;

    public My_Collection_RVAdapter(@Nullable List<CollectionListBean.CollectionList> data, Context context) {
        super(R.layout.item_rv_mycollection, data);
        this.context = context;
    }


    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CollectionListBean.CollectionList collectionList) {
        ImageView imageView = baseViewHolder.getView(R.id.iv_img);
        ((TextView) baseViewHolder.getView(R.id.tv_content)).setText(collectionList.getStrTitle());
        baseViewHolder.setText(R.id.tv_date, collectionList.getStrPubDate());
        if (GIStringUtil.isNotBlank(collectionList.getStrRootUnitName())) {
            baseViewHolder.setText(R.id.tv_type, collectionList.getStrRootUnitName());
        }
        if (collectionList.getImageWithVideoPathList() != null && collectionList.getImageWithVideoPathList().size() > 0) {
            imageView.setVisibility(View.VISIBLE);
            GIImageUtil.loadImg(context, imageView, Utils.formatFileUrl(context, collectionList.getImageWithVideoPathList().get(0).getStrThumbPath()), 0);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

}
