package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalAllListByTypeServletBean;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.RadiusBackgroundSpan;

import org.jetbrains.annotations.NotNull;

/**
 * @author :Sea
 * Date :2020-6-12 15:47
 * PackageName:com.suncn.ihold_zxztc.adapter
 * Desc:
 */
public class ProposalManagerRvAdapter extends BaseQuickAdapter<ProposalAllListByTypeServletBean.ObjListBean, ProposalManagerRvAdapter.ViewHolder> {
    private Context context;
    private String strType; //1所有提案 2办理中提案 3已办结提案 4立案提案 5不予立案提案 6重点提案 7优秀提案

    public void setStrType(String strType) {
        this.strType = strType;
    }

    public ProposalManagerRvAdapter(Context context) {
        super(R.layout.item_rv_proposal_manager);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ProposalAllListByTypeServletBean.ObjListBean objInfo) {
        if (GIStringUtil.isNotBlank(objInfo.getStrNo())) {
            viewHolder.caseNo_TextView.setVisibility(View.VISIBLE);
            viewHolder.ivCaseNo.setVisibility(View.VISIBLE);
            viewHolder.caseNo_TextView.setText(objInfo.getStrNo());
        } else {
            viewHolder.caseNo_TextView.setVisibility(View.GONE);
            viewHolder.ivCaseNo.setVisibility(View.GONE);
        }

        viewHolder.tvItemCaseStatus.setText(objInfo.getStrFlowState());
        String strTitle = objInfo.getStrTitle();
        Drawable drawable = null;
        if ("1".equals(objInfo.getStrIsGood()) && "1".equals(objInfo.getStrIsKey())) {
            drawable = context.getResources().getDrawable(R.mipmap.icon_good_key);
        } else if ("1".equals(objInfo.getStrIsGood())) {
            drawable = context.getResources().getDrawable(R.mipmap.icon_good);
        } else if ("1".equals(objInfo.getStrIsKey())) {
            drawable = context.getResources().getDrawable(R.mipmap.icon_key);
        } else {
            drawable = null;
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.title_TextView.setCompoundDrawables(drawable, null, null, null);
        }else {
            viewHolder.title_TextView.setCompoundDrawables(null, null, null, null);
        }
        viewHolder.title_TextView.setText(strTitle);
        String nameAndSector = "";
        if (GIStringUtil.isNotBlank(objInfo.getStrSourceName())) {
            nameAndSector += objInfo.getStrSourceName() + " ";
        }
        if (GIStringUtil.isNotBlank(objInfo.getStrFaction())) {
            nameAndSector += objInfo.getStrFaction() + " ";
        }
        if (GIStringUtil.isNotBlank(objInfo.getStrSector())) {
            nameAndSector += objInfo.getStrSector() + " ";
        }

        if (GIStringUtil.isNotBlank(objInfo.getStrReportDate())) {
            nameAndSector += objInfo.getStrReportDate();
        }
        viewHolder.tvItemNameAndSectorAndDate.setText(nameAndSector);

        String unitAndOthers = "";
        if ("1".equals(strType) || "4".equals(strType) || "6".equals(strType) || "7".equals(strType)) {
            if (GIStringUtil.isNotBlank(objInfo.getStrType())) {
                unitAndOthers += context.getString(R.string.string_classification) + "：" + objInfo.getStrType();
            } else {
                unitAndOthers += context.getString(R.string.string_classification) + "：";
            }
        }
        if ("2".equals(strType) || "3".equals(strType)) {
            if ("1".equals(objInfo.getStrAttendType())) {  //主办
                if (GIStringUtil.isNotBlank(objInfo.getStrMainUnit())) {
                    unitAndOthers += context.getString(R.string.string_undertaker) + "：" + objInfo.getStrMainUnit();
                } else {
                    unitAndOthers += context.getString(R.string.string_undertaker) + "：";
                }
            } else if ("2".equals(objInfo.getStrAttendType())) {  //会办
                if (GIStringUtil.isNotBlank(objInfo.getStrMainUnit())) {
                    unitAndOthers += context.getString(R.string.string_organizer_main) + "：" + objInfo.getStrMainUnit();
                } else {
                    unitAndOthers += context.getString(R.string.string_organizer_main) + "：";
                }
                if (GIStringUtil.isNotBlank(objInfo.getStrMeetUnit())) {
                    unitAndOthers += "\n\n" + context.getString(R.string.string_organizer) + "：" + objInfo.getStrMeetUnit();
                } else {
                    unitAndOthers += "\n\n" + context.getString(R.string.string_organizer) + "：";
                }
            } else if ("3".equals(objInfo.getStrAttendType())) {  //分办
                if (GIStringUtil.isNotBlank(objInfo.getStrMainUnit())) {
                    unitAndOthers += context.getString(R.string.string_branch_office_name) + "：" + objInfo.getStrMainUnit();
                } else {
                    unitAndOthers += context.getString(R.string.string_branch_office_name) + "：";
                }
            }

        }
        if ("5".equals(strType)) {
            if (GIStringUtil.isNotBlank(objInfo.getStrCheckResultType())) {
                unitAndOthers += context.getString(R.string.string_destination) + "：" + objInfo.getStrCheckResultType();
            } else {
                unitAndOthers += context.getString(R.string.string_destination) + "：";
            }
        }

        if (GIStringUtil.isNotBlank(unitAndOthers)) {
            viewHolder.unit_TextView.setText(unitAndOthers);
            viewHolder.unit_TextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.unit_TextView.setVisibility(View.GONE);
        }
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;//案由
        private TextView caseNo_TextView;//案号
        private TextView unit_TextView;//承办单位
        private TextView good_TextView;//优秀
        private TextView focus_TextView;//重点
        private GITextView tvItemNameAndSectorAndDate;
        private GITextView tvItemCaseStatus;
        private ImageView ivCaseNo;

        public ViewHolder(View itemView) {
            super(itemView);
            title_TextView = itemView.findViewById(R.id.tv_title);
            good_TextView = itemView.findViewById(R.id.tv_good);
            focus_TextView = itemView.findViewById(R.id.tv_focus);
            caseNo_TextView = itemView.findViewById(R.id.tv_caseNo);
            unit_TextView = itemView.findViewById(R.id.tv_unit);
            tvItemNameAndSectorAndDate = itemView.findViewById(R.id.tvItemNameAndSectorAndDate);
            tvItemCaseStatus = itemView.findViewById(R.id.tvItemCaseStatus);
            ivCaseNo = itemView.findViewById(R.id.ivCaseNo);
        }

    }
}
