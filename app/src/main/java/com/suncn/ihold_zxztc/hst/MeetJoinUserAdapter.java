package com.suncn.ihold_zxztc.hst;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.hst.fsp.FspEngine;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MeetUserBean;

import org.jetbrains.annotations.NotNull;

public class MeetJoinUserAdapter extends BaseQuickAdapter<MeetUserBean, MeetJoinUserAdapter.ViewHolder> {
    private Context context;
    private String chooseCode;

    public MeetJoinUserAdapter(Context context) {
        super(R.layout.item_meet);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, MeetUserBean objInfo) {
        viewHolder.tvName.setText(objInfo.getStrUserId());
        if (objInfo.isAudio()) {
            viewHolder.tvAudio.setText(context.getResources().getString(R.string.font_audio));
        } else {
            viewHolder.tvAudio.setText(context.getResources().getString(R.string.font_audio_close));
        }
        if (objInfo.getStrUserId().equals(GISharedPreUtil.getString(context, "strLoginUserId"))) {
            FspManager.getInstance().publishVideo(true, viewHolder.surfaceView);
            viewHolder.surfaceView.setVisibility(View.VISIBLE);
        } else if (GIStringUtil.isBlank(objInfo.getVideoId())) {
            viewHolder.surfaceView.setVisibility(View.GONE);
        } else {
            viewHolder.surfaceView.setVisibility(View.VISIBLE);
            FspManager.getInstance().setRemoteVideoRender(objInfo.getStrUserId(), objInfo.getVideoId(), viewHolder.surfaceView, FspEngine.RENDER_MODE_CROP_FILL);
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private SurfaceView surfaceView;
        private TextView tvName;
        private TextView tvAudio;

        public ViewHolder(View itemView) {
            super(itemView);
            surfaceView = itemView.findViewById(R.id.surfaceView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAudio = itemView.findViewById(R.id.tv_audio);
        }
    }
}
