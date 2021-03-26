package com.suncn.ihold_zxztc.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_AuditLeaveActivity;
import com.suncn.ihold_zxztc.bean.CheckLeaveListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

public class CheckLeaveListAdapter extends BaseQuickAdapter<CheckLeaveListBean.CheckLeaveList, CheckLeaveListAdapter.ViewHolder> {
    private Activity context;
    private int sign;

    public CheckLeaveListAdapter(Activity context, int sign) {
        super(R.layout.item_exlv_meetact_attendperson_child);
        this.sign = sign;
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, CheckLeaveListBean.CheckLeaveList objInfo) {
        viewHolder.head_ImageView.setVisibility(View.VISIBLE);
        String imageUrl = Utils.formatFileUrl(context, objInfo.getStrPathUrl());
        GIImageUtil.loadImg(context, viewHolder.head_ImageView, imageUrl, 1);

        viewHolder.chidName_TextView.setText(objInfo.getStrApplyName());
        if (GIStringUtil.isNotBlank(objInfo.getStrMobile())) {
            viewHolder.mobile_TextView.setVisibility(View.VISIBLE);
            viewHolder.mobile_TextView.setText("\ue648" + " " + objInfo.getStrMobile());
            viewHolder.mobile_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMyDialog(objInfo.getStrApplyName(), objInfo.getStrMobile());
                }
            });
        } else {
            viewHolder. mobile_TextView.setVisibility(View.GONE);
        }
        String strState;
        strState = objInfo.getStrState();
        viewHolder.attend_TextView.setText(objInfo.getStrStateName());
        viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.white));
        switch (strState) {
            case "2"://通过
                viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.font_source));
                viewHolder.attend_TextView.setBackground(null);
                viewHolder.attend_TextView.setOnClickListener(null);
                break;
            case "3":
                viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.red));
                viewHolder.attend_TextView.setBackground(null);
                viewHolder.attend_TextView.setOnClickListener(null);
                break;
            case "0":
                viewHolder.attend_TextView.setText("审核");
                viewHolder. attend_TextView.setBackgroundResource(R.drawable.shape_zxta_state_bg_orange);
                viewHolder.attend_TextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MeetAct_AuditLeaveActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("strApplyId", objInfo.getStrApplyId());
                        bundle.putInt("sign", sign);
                        intent.putExtras(bundle);
                        context.startActivityForResult(intent, 1);
                    }
                });
                break;
            default:
                viewHolder.attend_TextView.setBackground(null);
                break;
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView chidName_TextView;
        private RoundImageView head_ImageView;
        private TextView attend_TextView;
        private GITextView mobile_TextView;
        private TextView arrow_TextView;

        public ViewHolder(View convertView) {
            super(convertView);
            head_ImageView = (RoundImageView) convertView.findViewById(R.id.iv_head);
            chidName_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            attend_TextView = (TextView) convertView.findViewById(R.id.tv_attend);
            mobile_TextView = (GITextView) convertView.findViewById(R.id.tv_mobile);
            arrow_TextView = (TextView) itemView.findViewById(R.id.tv_arrow);
            arrow_TextView.setVisibility(View.GONE);
        }

    }

    //
    public void showMyDialog(String name, String phone) {
        final String mobile = phone;
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.content(name + "：" + phone).btnText("取消", "呼叫").showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.isTitleShow(false);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                        HiPermission.create(context).checkSinglePermission(Manifest.permission.CALL_PHONE, new PermissionCallback() {
                            @Override
                            public void onClose() {

                            }

                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onDeny(String permission, int position) {

                            }

                            @SuppressLint("MissingPermission")
                            @Override
                            public void onGuarantee(String permission, int position) {
                                Uri uri = Uri.parse("tel:" + mobile);
                                Intent intentMessage = new Intent(Intent.ACTION_CALL, uri);
                                context.startActivity(intentMessage);
                            }
                        });
                        //GIPermissionUtil.requestPermission(activity, GIPermissionUtil.CODE_CALL_PHONE, mPermissionGrant); // 请求授权

                    }
                }
        );
    }
}
