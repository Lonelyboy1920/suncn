package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalAllListByTypeServletBean;

import org.jetbrains.annotations.NotNull;

/**
 * @author :Sea
 * Date :2020-6-12 15:47
 * PackageName:com.suncn.ihold_zxztc.adapter
 * Desc: 提案查询搜索结果 Rv Adapter
 */
public class ProposalSearchResultRvAdapter extends BaseQuickAdapter<ProposalAllListByTypeServletBean.ObjListBean, ProposalSearchResultRvAdapter.ViewHolder> {
    private Context context;

    public ProposalSearchResultRvAdapter(Context context) {
        super(R.layout.item_rv_proposal_search_result);
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
        if (GIStringUtil.isNotBlank(objInfo.getStrType())) {
            unitAndOthers += "分类：" + objInfo.getStrType();
        } else {
            unitAndOthers += "分类：";
        }
//        if (GIStringUtil.isNotBlank(objInfo.getStrMainUnit())) {
//            unitAndOthers += "主办单位：" + objInfo.getStrMainUnit() + "\n\n";
//        } else {
//            unitAndOthers += "主办单位：" + "\n\n";
//        }
//        if (GIStringUtil.isNotBlank(objInfo.getStrMeetUnit())) {
//            unitAndOthers += "会办单位：" + objInfo.getStrMeetUnit();
//        } else {
//            unitAndOthers += "会办单位：";
//        }
        if (GIStringUtil.isNotBlank(unitAndOthers)) {
            viewHolder.unit_TextView.setText(unitAndOthers);
            viewHolder.unit_TextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.unit_TextView.setVisibility(View.GONE);
        }


    }


    public class ViewHolder extends BaseViewHolder {
        private TextView title_TextView;//案由
        //        private TextView caseNo_TextView;//案号
        private TextView unit_TextView;//承办单位
        private TextView good_TextView;//优秀
        private TextView focus_TextView;//重点
        private GITextView tvItemNameAndSectorAndDate;
        private GITextView tvItemCaseStatus;
        private ImageView ivCaseNo;
        private TextView caseNo_TextView;//案号

        public ViewHolder(View itemView) {
            super(itemView);
            title_TextView = itemView.findViewById(R.id.tv_title);
            good_TextView = itemView.findViewById(R.id.tv_good);
            focus_TextView = itemView.findViewById(R.id.tv_focus);
            caseNo_TextView = itemView.findViewById(R.id.tv_caseNo);
            unit_TextView = itemView.findViewById(R.id.tv_unit);
            tvItemNameAndSectorAndDate = itemView.findViewById(R.id.tvItemNameAndSectorAndDate);
            tvItemCaseStatus = itemView.findViewById(R.id.tvItemCaseStatus);
            ivCaseNo=itemView.findViewById(R.id.ivCaseNo);
        }

    }
}
