package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.UpUnitListBean;

import org.jetbrains.annotations.NotNull;

/**
 * 选择承办单位搜索Adapter
 */
public class Choose_Unit_RVAdapter extends BaseQuickAdapter<UpUnitListBean.UpUnitBean, Choose_Unit_RVAdapter.ViewHolder> {
    private Context context;

    public Choose_Unit_RVAdapter(Context context) {
        super(R.layout.item_exlv_contact_child);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, UpUnitListBean.UpUnitBean objInfo) {
        viewHolder.header_ImageView.setVisibility(View.GONE);
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
