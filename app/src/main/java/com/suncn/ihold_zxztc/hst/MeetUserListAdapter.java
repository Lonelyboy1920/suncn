package com.suncn.ihold_zxztc.hst;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.EventBusCarrier;
import com.suncn.ihold_zxztc.bean.MeetUserBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

/**
 * 用户管理列表adapter
 */
public class MeetUserListAdapter extends BaseQuickAdapter<MeetUserBean, MeetUserListAdapter.ViewHolder> {
    private Context context;
    private String type = "0";//0表示已关注，1表示未关注
    private boolean isCreate = false;

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public MeetUserListAdapter(Context context) {
        super(R.layout.item_meet_user);
        this.context = context;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, MeetUserBean objInfo) {
        if (GIStringUtil.isBlank(objInfo.getStrUserName())) {
            objInfo.setStrUserName(objInfo.getStrUserId());
        }
        if (isCreate) {
            if (objInfo.isOnline()) {
                viewHolder.tvRemove.setVisibility(View.VISIBLE);
                viewHolder.tvRemind.setVisibility(View.GONE);
            } else {
                viewHolder.tvRemove.setVisibility(View.GONE);
                viewHolder.tvRemind.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.tvRemove.setVisibility(View.GONE);
            viewHolder.tvRemind.setVisibility(View.GONE);
        }
        if (objInfo.getStrUserId().equals(GISharedPreUtil.getString(context, "strLoginUserId"))) {
            viewHolder.tvRemove.setVisibility(View.GONE);
            viewHolder.tvRemind.setVisibility(View.GONE);
            viewHolder.tvName.setText(objInfo.getStrUserName() + "(自己)");
            objInfo.setOnline(true);
        } else {
            viewHolder.tvName.setText(objInfo.getStrUserName());
        }
        if (objInfo.isAudio()) {
            viewHolder.tvAudio.setText(context.getResources().getString(R.string.font_audio));
        } else {
            viewHolder.tvAudio.setText(context.getResources().getString(R.string.font_audio_close));
        }
        if (objInfo.isVideo()) {
            viewHolder.tvVideo.setText(context.getResources().getString(R.string.font_video));
        } else {
            viewHolder.tvVideo.setText(context.getResources().getString(R.string.font_video_close));
        }
        if (objInfo.isOnline()) {
            viewHolder.tvState.setBackground(context.getResources().getDrawable(R.drawable.shape_dot_online));
            viewHolder.tvAudio.setTextColor(context.getResources().getColor(R.color.font_content));
        } else {
            viewHolder.tvState.setBackground(context.getResources().getDrawable(R.drawable.shape_dot_not_online));
            viewHolder.tvAudio.setTextColor(context.getResources().getColor(R.color.font_source));
        }
        viewHolder.tvAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objInfo.getStrUserId().equals(GISharedPreUtil.getString(context, "strLoginUserId"))) {
                    if (FspManager.getInstance().isAudioPublishing()) {
                        EventBusCarrier eventBusCarrier = new EventBusCarrier();
                        eventBusCarrier.setEventType(89);
                        eventBusCarrier.setObject("0");
                        EventBus.getDefault().post(eventBusCarrier); //通知MainActivity刷新数据
                        viewHolder.tvAudio.setText(context.getResources().getString(R.string.font_audio_close));
                    } else {
                        EventBusCarrier eventBusCarrier = new EventBusCarrier();
                        eventBusCarrier.setEventType(89);
                        eventBusCarrier.setObject("1");
                        EventBus.getDefault().post(eventBusCarrier); //通知MainActivity刷新数据
                        viewHolder.tvAudio.setText(context.getResources().getString(R.string.font_audio));
                    }
                } else if (isCreate && objInfo.isOnline()) {
                    if (viewHolder.tvAudio.getText().toString().equals(context.getResources().getString(R.string.font_audio))) {
                        viewHolder.tvAudio.setText(context.getResources().getString(R.string.font_audio_close));
                        FspManager.getInstance().sendUserMsg(objInfo.getStrUserId(), DefineUtil.suncnCloseAudio);
                    } else {
                        viewHolder. tvAudio.setText(context.getResources().getString(R.string.font_audio));
                        FspManager.getInstance().sendUserMsg(objInfo.getStrUserId(), DefineUtil.suncnOpenAudio);
                    }
                }

            }
        });
        viewHolder.tvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objInfo.getStrUserId().equals(GISharedPreUtil.getString(context, "strLoginUserId"))) {
                    if (FspManager.getInstance().isVideoPublishing()) {
                        EventBusCarrier eventBusCarrier = new EventBusCarrier();
                        eventBusCarrier.setEventType(88);
                        eventBusCarrier.setObject("0");
                        EventBus.getDefault().post(eventBusCarrier); //通知MainActivity刷新数据
                        viewHolder. tvVideo.setText(context.getResources().getString(R.string.font_video_close));
                    } else {
                        EventBusCarrier eventBusCarrier = new EventBusCarrier();
                        eventBusCarrier.setEventType(88);
                        eventBusCarrier.setObject("1");
                        EventBus.getDefault().post(eventBusCarrier); //通知MainActivity刷新数据
                        FspManager.getInstance().sendUserMsg(objInfo.getStrUserId(), DefineUtil.suncnOpenVideo);
                        viewHolder.tvVideo.setText(context.getResources().getString(R.string.font_video));
                    }
                }
            }
        });
        viewHolder.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FspManager.getInstance().sendUserMsg(objInfo.getStrUserId(), DefineUtil.suncnExitMeet);
            }
        });
        viewHolder.tvRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MeetUserListActivity) context).remindJoin(objInfo.getStrUserId());
            }
        });
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView tvName;
        private TextView tvVideo;
        private TextView tvAudio;
        private TextView tvRemove;
        private TextView tvRemind;
        private TextView tvState;


        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvVideo = itemView.findViewById(R.id.tv_video);
            tvAudio = itemView.findViewById(R.id.tv_audio);
            tvRemove = itemView.findViewById(R.id.tv_remove);
            tvRemind = itemView.findViewById(R.id.tv_remind);
            tvState = itemView.findViewById(R.id.tv_state);

        }

    }
}
