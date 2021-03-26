package com.suncn.ihold_zxztc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_Detail_BaseInfoFragment;

import org.jetbrains.annotations.NotNull;

/**
 * 政协提案详情的基本信息
 */
public class Proposal_Detail_BaseInfo_RVAdapter extends BaseQuickAdapter<String, Proposal_Detail_BaseInfo_RVAdapter.ViewHolder> {
    private Proposal_Detail_BaseInfoFragment context;

    public Proposal_Detail_BaseInfo_RVAdapter(Proposal_Detail_BaseInfoFragment context) {
        super(R.layout.item_rv_proposal_detail_baseinfo);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, String objInfo) {
        String[] valueArray = objInfo.split("\\|");
        baseViewHolder.tab_TextView.setText(valueArray[0]);
        if (valueArray.length > 1)
            baseViewHolder. value_TextView.setText(GIUtil.showHtmlInfo(valueArray[1]));
        else {
            baseViewHolder.value_TextView.setText("");
        }
        if (valueArray.length > 2 && valueArray[2] != null && valueArray[2].equals("true") && !valueArray[1].equals("否")) {
            baseViewHolder.count_LinearLayout.setVisibility(View.VISIBLE);
            String[] countArray = valueArray[1].split("、");
            if ((countArray != null && countArray[0].equals(""))) {
                baseViewHolder. count_LinearLayout.setVisibility(View.GONE);
                baseViewHolder.count_TextView.setVisibility(View.GONE);
            } else {
                baseViewHolder.count_LinearLayout.setVisibility(View.VISIBLE);
                baseViewHolder.count_TextView.setVisibility(View.VISIBLE);
                baseViewHolder. count_TextView.setText(valueArray[3] + "人");
            }
            baseViewHolder.main_LinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (valueArray[0].contains("附")) {
                        context.setOnClick(objInfo, view, 0);
                    } else {
                        context.setOnClick(objInfo, view, 1);
                    }
                }
            });
        } else {
            baseViewHolder.count_LinearLayout.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private LinearLayout main_LinearLayout;
        private TextView tab_TextView;
        private TextView count_TextView;
        private LinearLayout count_LinearLayout;
        private TextView value_TextView;
        private View blockView;

        public ViewHolder(View itemView) {
            super(itemView);
            main_LinearLayout = itemView.findViewById(R.id.ll_main);
            tab_TextView = (TextView) itemView.findViewById(R.id.tv_tag);
            value_TextView = (TextView) itemView.findViewById(R.id.tv_value);
            count_LinearLayout = itemView.findViewById(R.id.ll_count);
            count_TextView = itemView.findViewById(R.id.tv_count);
            blockView = itemView.findViewById(R.id.block);
        }


    }

}
