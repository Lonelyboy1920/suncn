package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.publicopinion.PublicOpinion_MainActivity;
import com.suncn.ihold_zxztc.bean.PublicOpinionListBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 社情民意addHead adpter
 */
public class PublicOpinion_MainTop_RVAdapter extends BaseQuickAdapter<PublicOpinionListBean.PubListBean, PublicOpinion_MainTop_RVAdapter.ViewHolder> {
    private Context mContext;

    public PublicOpinion_MainTop_RVAdapter(Context context) {
        super(R.layout.item_rv_publicopinion_top);
        this.mContext = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, PublicOpinionListBean.PubListBean objInfo) {
        String strPhotoUrl = Utils.formatFileUrl(mContext, objInfo.getStrPhotoPath());
        GIImageUtil.setBgForImage(mContext, viewHolder.main_RelativeLayout, Utils.formatFileUrl(mContext, strPhotoUrl));
        viewHolder.name_TextView.setText(objInfo.getStrType());
        viewHolder.issue_TextView.setText(objInfo.getStrIssue());
    }

    public class ViewHolder extends BaseViewHolder {
        private GITextView name_TextView;
        private GITextView issue_TextView;
        private RelativeLayout main_RelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name_TextView = (GITextView) itemView.findViewById(R.id.tv_name);
            issue_TextView = (GITextView) itemView.findViewById(R.id.tv_issue);
            main_RelativeLayout = itemView.findViewById(R.id.rl_main);

            DisplayMetrics dm = new DisplayMetrics();
            ((PublicOpinion_MainActivity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenWidth = dm.widthPixels - GIDensityUtil.dip2px(mContext, 40);

            int screenHeight = dm.heightPixels;
            int mViewWidth = screenWidth / 3;
            ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) itemView.getLayoutParams();
            //将宽度设置为屏幕的1/3
//            itemView.setPadding(GIDensityUtil.dip2px(mContext,15),0,GIDensityUtil.dip2px(mContext,15),0);
            lp.width = mViewWidth;
            itemView.setLayoutParams(lp);
        }

    }
}
