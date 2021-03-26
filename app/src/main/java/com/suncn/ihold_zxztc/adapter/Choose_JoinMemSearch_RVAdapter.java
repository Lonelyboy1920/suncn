package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.BaseUser;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 选择联名提案搜索Adapter
 */
public class Choose_JoinMemSearch_RVAdapter extends BaseQuickAdapter<BaseUser, Choose_JoinMemSearch_RVAdapter.ViewHolder> {
    private Context context;

    public Choose_JoinMemSearch_RVAdapter(Context context) {
        super(R.layout.item_exlv_contact_child);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, BaseUser objInfo) {
        String imageUrl = Utils.formatFileUrl(context, objInfo.getStrPathUrl());
        GIImageUtil.loadImg(context, viewHolder.header_ImageView, imageUrl, 1);
        GILogUtil.e("objInfo.getStrName()=====", imageUrl);
        viewHolder.name_TextView.setText(objInfo.getStrName());
        if (objInfo.isChecked()) {
            viewHolder.check_TextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.check_TextView.setVisibility(View.GONE);
        }
    }


    public class ViewHolder extends BaseViewHolder {
        private RoundImageView header_ImageView;
        private TextView name_TextView;
        private GITextView check_TextView;

        public ViewHolder(View itemView) {
            super(itemView);
            header_ImageView = itemView.findViewById(R.id.iv_head);
            name_TextView = itemView.findViewById(R.id.tv_name);
            check_TextView = itemView.findViewById(R.id.tv_check);
        }

    }
}
