package com.suncn.ihold_zxztc.activity.application.publicopinion;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ThemeTypeBean;
import com.suncn.ihold_zxztc.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeTypeDialogList extends Dialog {
    private OnItemClickListener onItemClickListener;
    private RecyclerView easyRecyclerView;
    private Activity mContext;
    private ThemeTypeAdapter adapter;
    private ArrayList<ThemeTypeBean.themeBean> objList;


    public ThemeTypeDialogList(Activity context,ArrayList<ThemeTypeBean.themeBean> objList) {
        super(context, R.style.MyDialogStyleBottom);
        this.mContext = context;
        this.objList= new ArrayList<>(objList);
    }

    public ThemeTypeAdapter getAdapter() {
        return adapter;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_list);
        setCanceledOnTouchOutside(true);
        easyRecyclerView = findViewById(R.id.recyclerView);
        initRecyclerView();

    }

    @Override
    public void show() {
        super.show();
        show(this);
    }

    private void show(ThemeTypeDialogList mDialog) {
        if (mDialog != null) {
            Window window = mDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.getDecorView().setPadding(0, 0, 0, 0);
            mDialog.setCanceledOnTouchOutside(false);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);  //将设置好的属性set回去
        }
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(mContext, easyRecyclerView, false, false, R.color.main_bg, 0.5f, 10);
        adapter = new ThemeTypeAdapter(objList);
        adapter.setOnItemClickListener(onItemClickListener);
        easyRecyclerView.setAdapter(adapter);
//        adapter.setList(objList);
//        adapter.notifyDataSetChanged();
    }

    class ThemeTypeAdapter extends BaseQuickAdapter<ThemeTypeBean.themeBean, BaseViewHolder> {

        public ThemeTypeAdapter(@Nullable ArrayList<ThemeTypeBean.themeBean> data) {
            super(R.layout.view_theme_type, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, ThemeTypeBean.themeBean themeBean) {
            ((TextView) baseViewHolder.getView(R.id.tv_value)).setText(themeBean.getStrTopicTypeName());
            if (themeBean.getObjChildList() != null && themeBean.getObjChildList().size() > 0) {
                ((GITextView) baseViewHolder.getView(R.id.tv_arrow)).setVisibility(View.VISIBLE);
            } else {
                ((GITextView) baseViewHolder.getView(R.id.tv_arrow)).setVisibility(View.GONE);
            }
        }
    }


    //监听事件接口
    public interface onItemClickListener {
        void onItemClick(View v, EditText e, String content);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
