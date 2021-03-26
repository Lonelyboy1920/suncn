package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalFeedBackViewServletBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * @author :Sea
 * Date :2020-6-11 9:30
 * PackageName:com.suncn.ihold_zxztc.adapter
 * Desc:
 */
public class MemberFeedbackRvAdapter extends BaseQuickAdapter<ProposalFeedBackViewServletBean.ObjListBean, MemberFeedbackRvAdapter.ViewHolder> {
    private Context mContext;
    private HashMap<String, String> mFeedBackMap = new HashMap<>();

    public HashMap<String, String> getmFeedBackMap() {
        return mFeedBackMap;
    }

    public void setmFeedBackMap(HashMap<String, String> mFeedBackMap) {
        this.mFeedBackMap = mFeedBackMap;
    }

    public MemberFeedbackRvAdapter(Context context) {
        super(R.layout.item_rv_member_feedback);
        this.mContext = context;
    }


    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, ProposalFeedBackViewServletBean.ObjListBean objListBean) {
        int adapterPosition = baseViewHolder.getAdapterPosition();
        int defItemCount = getDefItemCount();
        Log.i("======================", defItemCount + "\n" + adapterPosition);
        baseViewHolder.tvItemMemberFeedbackOrganizerTag.setText(objListBean.getStrAttendType() + "单位");
        baseViewHolder.tvItemMemberFeedbackOrganizer.setText(objListBean.getStrRecUnitName());
//        baseViewHolder.rgItemHandlingResult.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                String state = "1";
//                if (i == baseViewHolder.rbItemHandlingResultYes.getId()) {
//                    objListBean.setStrFeedBackResultState("1");
//                    state = "1";
//                } else if (i == baseViewHolder.rbItemHandlingResultNo.getId()) {
//                    objListBean.setStrFeedBackResultState("0");
//                    state = "0";
//                }
//
//                mFeedBackMap.put("strFeedBackResultState" + objListBean.getStrProposalDispenseId(), state);
//            }
//        });
//        baseViewHolder.rgItemHandlingAttitude.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                String state = "1";
//                if (i == baseViewHolder.rbItemHandlingAttitudeYes.getId()) {
//                    objListBean.setStrFeedBackAttitudeState("1");
//                    state = "1";
//                } else if (i == baseViewHolder.rbItemHandlingAttitudeNo.getId()) {
//                    objListBean.setStrFeedBackAttitudeState("0");
//                    state = "0";
//                }
//
//                mFeedBackMap.put("strFeedBackAttitudeState" + objListBean.getStrProposalDispenseId(), state);
//            }
//        });
//        baseViewHolder.rgItemOverallEvaluation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                String state = "1";
//                if (i == baseViewHolder.rbItemOverallEvaluationYes.getId()) {
//
//                    objListBean.setStrFeedBackTotalState("1");
//                    state = "1";
//                } else if (i == baseViewHolder.rbItemOverallEvaluationNo.getId()) {
//
//                    objListBean.setStrFeedBackTotalState("0");
//                    state = "0";
//                }
//                mFeedBackMap.put("strFeedBackTotalState" + objListBean.getStrProposalDispenseId(), state);
//            }
//        });
        //办理结果
        baseViewHolder.rbItemHandlingResultYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseViewHolder.rbItemHandlingResultYes.setTextColor(Color.parseColor("#f57f17"));
                Drawable drawableLeftYes = mContext.getResources().getDrawable(R.mipmap.icon_checked_yes);
                baseViewHolder.rbItemHandlingResultYes.setCompoundDrawablesWithIntrinsicBounds(drawableLeftYes,
                        null, null, null);

                baseViewHolder.rbItemHandlingResultNo.setTextColor(mContext.getResources().getColor(R.color.font_title));
                Drawable drawableLeftNo = mContext.getResources().getDrawable(R.mipmap.icon_unchecked_no);
                baseViewHolder.rbItemHandlingResultNo.setCompoundDrawablesWithIntrinsicBounds(drawableLeftNo,
                        null, null, null);
                mFeedBackMap.put("strFeedBackResultState" + objListBean.getStrProposalDispenseId(), "1");
            }
        });
        baseViewHolder.rbItemHandlingResultNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseViewHolder.rbItemHandlingResultNo.setTextColor(Color.parseColor("#f57f17"));
                Drawable drawableLeftYes = mContext.getResources().getDrawable(R.mipmap.icon_checked_no);
                baseViewHolder.rbItemHandlingResultNo.setCompoundDrawablesWithIntrinsicBounds(drawableLeftYes,
                        null, null, null);

                baseViewHolder.rbItemHandlingResultYes.setTextColor(mContext.getResources().getColor(R.color.font_title));
                Drawable drawableLeftNo = mContext.getResources().getDrawable(R.mipmap.icon_unchecked_yes);
                baseViewHolder.rbItemHandlingResultYes.setCompoundDrawablesWithIntrinsicBounds(drawableLeftNo,
                        null, null, null);

                mFeedBackMap.put("strFeedBackResultState" + objListBean.getStrProposalDispenseId(), "0");
            }
        });
        //办理态度
        baseViewHolder.rbItemHandlingAttitudeYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseViewHolder.rbItemHandlingAttitudeYes.setTextColor(Color.parseColor("#f57f17"));
                Drawable drawableLeftYes = mContext.getResources().getDrawable(R.mipmap.icon_checked_yes);
                baseViewHolder.rbItemHandlingAttitudeYes.setCompoundDrawablesWithIntrinsicBounds(drawableLeftYes,
                        null, null, null);

                baseViewHolder.rbItemHandlingAttitudeNo.setTextColor(mContext.getResources().getColor(R.color.font_title));
                Drawable drawableLeftNo = mContext.getResources().getDrawable(R.mipmap.icon_unchecked_no);
                baseViewHolder.rbItemHandlingAttitudeNo.setCompoundDrawablesWithIntrinsicBounds(drawableLeftNo,
                        null, null, null);
                mFeedBackMap.put("strFeedBackAttitudeState" + objListBean.getStrProposalDispenseId(), "1");
            }
        });
        baseViewHolder.rbItemHandlingAttitudeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseViewHolder.rbItemHandlingAttitudeNo.setTextColor(Color.parseColor("#f57f17"));
                Drawable drawableLeftYes = mContext.getResources().getDrawable(R.mipmap.icon_checked_no);
                baseViewHolder.rbItemHandlingAttitudeNo.setCompoundDrawablesWithIntrinsicBounds(drawableLeftYes,
                        null, null, null);

                baseViewHolder.rbItemHandlingAttitudeYes.setTextColor(mContext.getResources().getColor(R.color.font_title));
                Drawable drawableLeftNo = mContext.getResources().getDrawable(R.mipmap.icon_unchecked_yes);
                baseViewHolder.rbItemHandlingAttitudeYes.setCompoundDrawablesWithIntrinsicBounds(drawableLeftNo,
                        null, null, null);

                mFeedBackMap.put("strFeedBackAttitudeState" + objListBean.getStrProposalDispenseId(), "0");
            }
        });
        //总体评价
        baseViewHolder.rbItemOverallEvaluationYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseViewHolder.rbItemOverallEvaluationYes.setTextColor(Color.parseColor("#f57f17"));
                Drawable drawableLeftYes = mContext.getResources().getDrawable(R.mipmap.icon_checked_yes);
                baseViewHolder.rbItemOverallEvaluationYes.setCompoundDrawablesWithIntrinsicBounds(drawableLeftYes,
                        null, null, null);

                baseViewHolder.rbItemOverallEvaluationNo.setTextColor(mContext.getResources().getColor(R.color.font_title));
                Drawable drawableLeftNo = mContext.getResources().getDrawable(R.mipmap.icon_unchecked_no);
                baseViewHolder.rbItemOverallEvaluationNo.setCompoundDrawablesWithIntrinsicBounds(drawableLeftNo,
                        null, null, null);

                mFeedBackMap.put("strFeedBackTotalState" + objListBean.getStrProposalDispenseId(), "1");
            }
        });
        baseViewHolder.rbItemOverallEvaluationNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseViewHolder.rbItemOverallEvaluationNo.setTextColor(Color.parseColor("#f57f17"));
                Drawable drawableLeftYes = mContext.getResources().getDrawable(R.mipmap.icon_checked_no);
                baseViewHolder.rbItemOverallEvaluationNo.setCompoundDrawablesWithIntrinsicBounds(drawableLeftYes,
                        null, null, null);

                baseViewHolder.rbItemOverallEvaluationYes.setTextColor(mContext.getResources().getColor(R.color.font_title));
                Drawable drawableLeftNo = mContext.getResources().getDrawable(R.mipmap.icon_unchecked_yes);
                baseViewHolder.rbItemOverallEvaluationYes.setCompoundDrawablesWithIntrinsicBounds(drawableLeftNo,
                        null, null, null);

                mFeedBackMap.put("strFeedBackTotalState" + objListBean.getStrProposalDispenseId(), "0");
            }
        });

        baseViewHolder.etItemParticularView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (GIStringUtil.isNotBlank(editable.toString().trim())) {
                    mFeedBackMap.put("strFeedBackSpecificIdea" + objListBean.getStrProposalDispenseId(), editable.toString().trim());
                }
            }
        });
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView tvItemMemberFeedbackOrganizerTag;
        private TextView tvItemMemberFeedbackOrganizer;
        private LinearLayout rgItemHandlingResult;
        private TextView rbItemHandlingResultYes;
        private TextView rbItemHandlingResultNo;
        private LinearLayout rgItemHandlingAttitude;
        private TextView rbItemHandlingAttitudeYes;
        private TextView rbItemHandlingAttitudeNo;
        private LinearLayout rgItemOverallEvaluation;
        private TextView rbItemOverallEvaluationYes;
        private TextView rbItemOverallEvaluationNo;
        private GIEditText etItemParticularView;
        private TextView tvItemSubmit;


        public ViewHolder(View convertView) {
            super(convertView);
            tvItemMemberFeedbackOrganizerTag = convertView.findViewById(R.id.tvItemMemberFeedbackOrganizerTag);
            tvItemMemberFeedbackOrganizer = convertView.findViewById(R.id.tvItemMemberFeedbackOrganizer);
            rgItemHandlingResult = convertView.findViewById(R.id.rgItemHandlingResult);
            rbItemHandlingResultYes = convertView.findViewById(R.id.rbItemHandlingResultYes);
            rbItemHandlingResultNo = convertView.findViewById(R.id.rbItemHandlingResultNo);
            rgItemHandlingAttitude = convertView.findViewById(R.id.rgItemHandlingAttitude);
            rbItemHandlingAttitudeYes = convertView.findViewById(R.id.rbItemHandlingAttitudeYes);
            rbItemHandlingAttitudeNo = convertView.findViewById(R.id.rbItemHandlingAttitudeNo);
            rgItemOverallEvaluation = convertView.findViewById(R.id.rgItemOverallEvaluation);
            rbItemOverallEvaluationYes = convertView.findViewById(R.id.rbItemOverallEvaluationYes);
            rbItemOverallEvaluationNo = convertView.findViewById(R.id.rbItemOverallEvaluationNo);
            etItemParticularView = convertView.findViewById(R.id.etItemParticularView);
            tvItemSubmit = convertView.findViewById(R.id.tvItemSubmit);
        }
    }
}
