package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.StudyBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 学习园地adapter
 */
public class StudyListAdapter extends BaseQuickAdapter<StudyBean.study, StudyListAdapter.ViewHolder> {
    private Context context;
    private int intType;

    public StudyListAdapter(Context context, int intType) {
        super(R.layout.item_study_list);
        this.context = context;
        this.intType = intType;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, StudyBean.study objInfo) {
        String imageUrl = objInfo.getStrImagePath();
        if (GIStringUtil.isNotBlank(imageUrl)) {
            GIImageUtil.loadImg(context, viewHolder.ivImg, Utils.formatFileUrl(context, imageUrl), 0);
            GIImageUtil.loadImg(context,  viewHolder.ivImgVideo, Utils.formatFileUrl(context, imageUrl), 0);
            if (objInfo.getStrFileType().equals("3")) {
                viewHolder. ivImg.setVisibility(View.GONE);
                viewHolder.ll_video_img.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivImg.setVisibility(View.VISIBLE);
                viewHolder.ll_video_img.setVisibility(View.GONE);
            }
        } else {
            viewHolder. ivImg.setVisibility(View.GONE);
            viewHolder. ll_video_img.setVisibility(View.GONE);
            if (objInfo.getStrFileType().equals("2")) {
                viewHolder. tv_file.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tv_file.setVisibility(View.GONE);
            }
        }

        viewHolder. tvTitle.setText(objInfo.getStrTitle());
        viewHolder. tvDate.setText(objInfo.getStrPubDate());
        viewHolder. tvCount.setText(objInfo.getIntReadCount() + "");
    }


    public class ViewHolder extends BaseViewHolder {
        private ImageView ivImg;
        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvCount;
        private ImageView ivImgVideo;
        private RelativeLayout ll_video_img;
        private GITextView tv_file;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_img);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count);
            ivImgVideo = (ImageView) itemView.findViewById(R.id.iv_video_img);
            ll_video_img = (RelativeLayout) itemView.findViewById(R.id.ll_video_img);
            tv_file = (GITextView) itemView.findViewById(R.id.tv_file);

        }
    }
}
