package com.qd.longchat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.longchat.base.databases.QDOrderHelper;
import com.longchat.base.model.QDOrder;
import com.qd.longchat.R;
import com.qd.longchat.order.activity.QDOrderDetailActivity;
import com.qd.longchat.order.adapter.QDOrderAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/23 上午9:45
 */
public class QDOrderSearchActivity extends QDBaseActivity {

    @BindView(R2.id.et_search_name)
    EditText etSearch;
    @BindView(R2.id.tv_os_cancel)
    TextView tvCancel;
    @BindView(R2.id.lv_os_list)
    ListView lvList;
    @BindView(R2.id.tv_os_tag)
    TextView tvTag;

    private Context context;
    private QDOrderAdapter adapter;
    List<QDOrder> orderList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_search);
        ButterKnife.bind(this);
        context = this;

        adapter = new QDOrderAdapter(this);
        lvList.setAdapter(adapter);

    }

    @OnEditorAction(R2.id.et_search_name)
    public boolean onEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String searchTxt = etSearch.getText().toString();
            orderList = QDOrderHelper.searchOrderByTitle(searchTxt);
            if (orderList.size() != 0) {
                lvList.setVisibility(View.VISIBLE);
                tvTag.setVisibility(View.GONE);
                adapter.setOrderList(orderList);
            } else {
                lvList.setVisibility(View.GONE);
                tvTag.setVisibility(View.VISIBLE);
            }
        }
        return false;
    }

    @OnClick(R2.id.tv_os_cancel)
    public void onClick(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @OnItemClick(R2.id.lv_os_list)
    public void onItemClick(int position) {
        QDOrder order = adapter.getItem(position);
        if (order.getIsRead() == QDOrder.STATUS_UNREAD) {
            QDOrderHelper.setOrderRead(order.getOrderId());
            orderList.remove(position);
            order.setIsRead(QDOrder.STATUS_READ);
            orderList.add(position, order);
            adapter.setOrderList(orderList);
        }
        Intent intent = new Intent(context, QDOrderDetailActivity.class);
        intent.putExtra(QDOrderDetailActivity.ORDER_ID, orderList.get(position).getOrderId());
        startActivity(intent);
    }
}
