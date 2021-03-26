package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalGoTypeListBean;

import org.jetbrains.annotations.NotNull;

public class ChooseListAdapter extends BaseQuickAdapter<ProposalGoTypeListBean.ProposalGoType, ChooseListAdapter.ViewHolder> {
    private Context context;
    private String chooseCode;

    public ChooseListAdapter(Context context) {
        super(R.layout.item_choose_list);
    }

    public void setChooseCode(String chooseCode) {
        this.chooseCode = chooseCode;
    }


    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ProposalGoTypeListBean.ProposalGoType objInfo) {
        viewHolder.tvContent.setText(objInfo.getStrName());
        if (objInfo.getStrCode().equals(chooseCode)) {
            viewHolder.tvCheck.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvCheck.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private TextView tvContent;
        private TextView tvCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvCheck = itemView.findViewById(R.id.tv_check);
        }
    }
}
