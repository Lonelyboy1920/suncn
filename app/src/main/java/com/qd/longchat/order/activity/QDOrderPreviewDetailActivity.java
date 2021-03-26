package com.qd.longchat.order.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.longchat.base.model.gd.QDOrderPreviewModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.order.adapter.QDOrderPreviewAdapter;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/27 上午10:25
 */
public class QDOrderPreviewDetailActivity extends QDBaseActivity {

    @BindView(R2.id.view_pd_title)
    View viewTitle;
    @BindView(R2.id.tv_pd_unread)
    TextView tvUnread;
    @BindView(R2.id.iv_pd_unread)
    ImageView ivUnread;
    @BindView(R2.id.tv_pd_read)
    TextView tvRead;
    @BindView(R2.id.iv_pd_read)
    ImageView ivRead;
    @BindView(R2.id.lv_pd_list)
    ListView lvList;

    @BindString(R2.string.im_text_order_preview_detail)
    String strTitle;
    @BindColor(R2.color.colorTextUnFocused)
    int colorUnSelected;
    @BindColor(R2.color.colorTabSelected)
    int colorSelected;

    private QDOrderPreviewModel model;
    private QDOrderPreviewAdapter adapter;
    private List<QDOrderPreviewModel.Bean.ListBean> listBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_preview_detail);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        model = (QDOrderPreviewModel) getIntent().getSerializableExtra("detail");

        int readCount = model.getRead().getCount();
        int unreadCount = model.getUnread().getCount();

        tvUnread.setText("未读 (" + unreadCount + ")人");
        tvRead.setText("已读 (" + readCount + ")人");

        listBean = model.getUnread().getList();
        adapter = new QDOrderPreviewAdapter(this, listBean);
        lvList.setAdapter(adapter);
    }

    @OnClick({R2.id.rl_pd_unread_layout, R2.id.rl_pd_read_layout})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rl_pd_unread_layout) {
            if (ivUnread.getVisibility() != View.VISIBLE) {
                initTable();
                tvUnread.setTextColor(colorSelected);
                ivUnread.setVisibility(View.VISIBLE);
                adapter.setListBean(model.getUnread().getList());
            }

        } else if (i == R.id.rl_pd_read_layout) {
            if (ivRead.getVisibility() != View.VISIBLE) {
                initTable();
                tvRead.setTextColor(colorSelected);
                ivRead.setVisibility(View.VISIBLE);
                adapter.setListBean(model.getRead().getList());
            }

        }
    }

    private void initTable() {
        tvUnread.setTextColor(colorUnSelected);
        tvRead.setTextColor(colorUnSelected);
        ivUnread.setVisibility(View.GONE);
        ivRead.setVisibility(View.GONE);
    }
}
