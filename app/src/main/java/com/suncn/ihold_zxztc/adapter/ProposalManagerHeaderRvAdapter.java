package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ProposalAllTypeServletBean;

import org.jetbrains.annotations.NotNull;

import skin.support.content.res.SkinCompatResources;

/**
 * @author :Sea
 * Date :2020-6-12 17:21
 * PackageName:com.suncn.ihold_zxztc.adapter
 * Desc:提案管理 提案查询 顶部Rv Adapter
 */
public class ProposalManagerHeaderRvAdapter extends BaseQuickAdapter<ProposalAllTypeServletBean.ObjListBean, ProposalManagerHeaderRvAdapter.ViewHolder> {
    private Context context;
    private String strSelectId = "";

    public ProposalManagerHeaderRvAdapter(Context context) {
        super(R.layout.item_public_tab);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ProposalAllTypeServletBean.ObjListBean objInfo) {
        viewHolder.tv_count.setText(objInfo.getIntCount());
        viewHolder.tv_name.setText(objInfo.getStrName() + "(件)");

        if (objInfo.getStrType().equals(strSelectId)) {
            viewHolder.ll_main.setBackground(SkinCompatResources.getDrawable(context, R.drawable.checked));
            viewHolder.tv_count.setTextColor(SkinCompatResources.getColor(context, R.color.view_head_bg));
        } else {
            viewHolder.ll_main.setBackground(SkinCompatResources.getDrawable(context, R.drawable.uncheck));
            viewHolder.tv_count.setTextColor(context.getResources().getColor(R.color.black));
        }

    }

    public class ViewHolder extends BaseViewHolder {
        private LinearLayout ll_main;
        private TextView tv_count;
        private TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            ll_main = itemView.findViewById(R.id.ll_main);
            tv_count = itemView.findViewById(R.id.tv_count);
            tv_name = itemView.findViewById(R.id.tv_name);

        }
    }

    public void setStrSelectId(String strSelectId) {
        this.strSelectId = strSelectId;
        notifyDataSetChanged();
    }

}
