package com.suncn.ihold_zxztc.activity.hot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.ColumnSettingAdapter;
import com.suncn.ihold_zxztc.bean.NewsColumnListBean;

import java.util.HashMap;
import java.util.List;

import skin.support.content.res.SkinCompatResources;

/**
 * 栏目设置
 * 剩余问题：排序后选中的栏目存在错乱问题
 */
public class ColumnSettingActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(id = R.id.more_recyclerView)
    RecyclerView more_recyclerView;
    @BindView(id = R.id.tv_change, click = true)
    private TextView change_TextView;//编辑按钮
    @BindView(id = R.id.tv_channel_info)
    private TextView channelInfo_TextView;//我的频道信息
    private ColumnSettingAdapter adapter;
    private ColumnSettingAdapter more_Adapter;
    private List<NewsColumnListBean.NewsColumnBean> newsColumnBeans;
    private List<NewsColumnListBean.NewsColumnBean> newsMoreColumnBeans;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_column_setting);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_all_channels));
        channelInfo_TextView.setText(R.string.string_mychannels_click_enter_channels);
        goto_Button.setText("\ue63f");
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setTextColor(getResources().getColor(R.color.font_content));
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        GridLayoutManager layoutManage = new GridLayoutManager(activity, 3);
        recyclerView.setLayoutManager(layoutManage);
        adapter = new ColumnSettingAdapter(activity, 0);
        recyclerView.setAdapter(adapter);

        GridLayoutManager moreLayoutManage = new GridLayoutManager(activity, 3);
        more_recyclerView.setLayoutManager(moreLayoutManage);
        more_Adapter = new ColumnSettingAdapter(activity, 1);
        more_recyclerView.setAdapter(more_Adapter);
        getColumnInfo();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.tv_change:
                String strChange = change_TextView.getText().toString().trim();
                if (strChange.contains(getString(R.string.string_edit))) {
                    channelInfo_TextView.setText("我的频道　拖拽可以排序");
                    adapter.setIntType(-1);
                    adapter.notifyDataSetChanged();
                    change_TextView.setText(getString(R.string.string_finish));
                    change_TextView.setBackgroundResource(R.drawable.shape_btn_submit_bg);
                    change_TextView.setTextColor(getResources().getColor(R.color.white));
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                } else {
                    channelInfo_TextView.setText("我的频道　点击进入频道");
                    adapter.setIntType(0);
                    adapter.setList(newsColumnBeans);
                    adapter.notifyDataSetChanged();
                    change_TextView.setText(getString(R.string.string_edit));
                    change_TextView.setBackgroundResource(R.drawable.shape_btn_column_bg);
                    change_TextView.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                    itemTouchHelper.attachToRecyclerView(null);
                    getColumnDeal();
                }
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter1, @NonNull View view, int position) {
                if (position == -1) {
                    return;
                }
                NewsColumnListBean.NewsColumnBean newsColumnBean = (NewsColumnListBean.NewsColumnBean) adapter.getItem(position);
                switch (adapter.getIntType()) {
                    case 0:  // 不可编辑，点击直接跳转到对应栏目
                        GISharedPreUtil.setValue(activity, "strClickInfo", newsColumnBean.getStrId());
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("currentIndex", position);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    case -1: // 取消订阅
                        if (newsColumnBean.isCanUnsubscribe()) {
                            newsColumnBeans.remove(newsColumnBean);
                            adapter.remove(newsColumnBean);
                            newsMoreColumnBeans.add(newsColumnBean);
                            more_Adapter.addData(newsColumnBean);
                        }
                        break;
                }
            }
        });

        /**
         * 更多栏目（添加订阅）点击事件
         */
        more_Adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter1, @NonNull View view, int position) {
                NewsColumnListBean.NewsColumnBean newsColumnBean = (NewsColumnListBean.NewsColumnBean) more_Adapter.getItem(position);
                newsColumnBeans.add(newsColumnBean);
                adapter.addData(newsColumnBean);
                newsMoreColumnBeans.remove(newsColumnBean);
                more_Adapter.remove(newsColumnBean);
                getColumnDeal();
            }
        });
    }

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            //也就是说返回值是组合式的
            //makeMovementFlags (int dragFlags, int swipeFlags)，看下面的解释说明
            int swipFlag = 0;
            //如果也监控左右方向的话，swipFlag=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
            //int swipFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int dragflag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            //等价于：0001&0010;多点触控标记触屏手指的顺序和个数也是这样标记哦
            return makeMovementFlags(dragflag, swipFlag);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
//            GILogUtil.i("viewHolder.getAdapterPosition()==" + viewHolder.getAdapterPosition());
//            GILogUtil.i("viewHolder1.getAdapterPosition()==" + viewHolder1.getAdapterPosition());
            //直接按照文档来操作啊，这文档写得太给力了,简直完美！
            if (newsColumnBeans.get(viewHolder.getAdapterPosition()).isCanUnsubscribe() && newsColumnBeans.get(viewHolder1.getAdapterPosition()).isCanUnsubscribe()) {
                //注意这里有个坑的，itemView 都移动了，对应的数据也要移动
//                Collections.swap(newsColumnBeans, viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
                NewsColumnListBean.NewsColumnBean newsColumnBean = newsColumnBeans.get(viewHolder.getAdapterPosition());
                int index = viewHolder1.getAdapterPosition();
//                GILogUtil.i("index==" + index);
                newsColumnBeans.remove(viewHolder.getAdapterPosition());
                newsColumnBeans.add(index, newsColumnBean);
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
            }
//            adapter = (ColumnSettingAdapter) recyclerView.getAdapter();
//            newsColumnBeans = adapter.getObjList();
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        }

        //当长按选中item的时候（拖拽开始的时候）调用
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        //当手指松开的时候（拖拽完成的时候）调用
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            viewHolder.itemView.setBackgroundResource(R.drawable.shape_item_column_normal_bg);
        }
    });

    /**
     * 获取栏目信息
     */
    private void getColumnInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
        }
        doRequestNormal(ApiManager.getInstance().getColumnSettingList(textParamMap), 1);
    }

    /**
     * 栏目信息处理
     * strType 1 新增我的栏目 strId为栏目id
     * 2 删除我的栏目 strId为 栏目记录的id
     * 3 修改栏目的排序
     */
    private void getColumnDeal() {
        textParamMap = new HashMap<>();
        if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < newsColumnBeans.size(); i++) {
            stringBuffer.append(newsColumnBeans.get(i).getStrId() + ",");
        }
        String strId = stringBuffer.toString();
        if (GIStringUtil.isNotBlank(strId)) {
            textParamMap.put("strId", strId.substring(0, strId.length() - 1));
            doRequestNormal(ApiManager.getInstance().getColumnSettingDeal(textParamMap), 0);
        }
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 0:
                try {
                    setResult(RESULT_OK, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 1:
                try {
                    NewsColumnListBean newsColumnListBean = (NewsColumnListBean) data;
                    newsColumnBeans = newsColumnListBean.getObjMyList();
                    newsMoreColumnBeans = newsColumnListBean.getObjList();
                    adapter.setObjList(newsColumnBeans);
                    adapter.setList(newsColumnBeans);
                    if (newsColumnBeans != null && newsColumnBeans.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                    more_Adapter.setList(newsMoreColumnBeans);
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                prgDialog.closePrgDialog();
                break;
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }
}
