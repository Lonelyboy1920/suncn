package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.circle.FollowSettingActivity;
import com.suncn.ihold_zxztc.bean.CircleTagListBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import skin.support.content.res.SkinCompatResources;


public class CircleTagPopWindow<T> extends PopupWindow {
    private LayoutInflater inflater;
    private RecyclerView mListView;
    private List<CircleTagListBean.CircleTagBean> list;
    private MyAdapter mAdapter;
    private Context context;
    private TextView tv_edit;
    private String selectCode = "0000";

    public CircleTagPopWindow(Context context, List<CircleTagListBean.CircleTagBean> list, OnItemClickListener clickListener ) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        init(clickListener);
    }

    public void setList(List<CircleTagListBean.CircleTagBean> list) {
        this.list = list;
        mAdapter.notifyDataSetChanged();
    }

    private void init(OnItemClickListener clickListener) {
        View view = inflater.inflate(R.layout.popup_circle_tag, null);
        setContentView(view);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        tv_edit = view.findViewById(R.id.tv_edit);
        mListView = view.findViewById(R.id.listview);
        mListView.setLayoutManager(new GridLayoutManager(context, 4));
        mListView.setAdapter(mAdapter = new MyAdapter());
        mAdapter.setList(list);
        mAdapter.setOnItemClickListener(clickListener);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, FollowSettingActivity.class);
                context.startActivity(intent);
                dismiss();
            }
        });
    }

    public void setSelectCode(String selectCode) {
        this.selectCode = selectCode;
    }

    class MyAdapter extends BaseQuickAdapter<CircleTagListBean.CircleTagBean, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_lv_circle_tag);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, CircleTagListBean.CircleTagBean objInfo) {
            TextView tvContent = baseViewHolder.findView(R.id.tv_content);
            tvContent.setText(objInfo.getStrLabelName());
            if (selectCode.equals(objInfo.getStrCode())) {
                tvContent.setBackgroundResource(R.drawable.shape_item_column_checked_bg);
                tvContent.setTextColor(SkinCompatResources.getColor(context, R.color.view_head_bg));
            } else {
                tvContent.setBackground(context.getDrawable(R.drawable.shape_item_column_normal_bg));
                tvContent.setTextColor(context.getResources().getColor(R.color.font_content));
            }
        }

    }
}