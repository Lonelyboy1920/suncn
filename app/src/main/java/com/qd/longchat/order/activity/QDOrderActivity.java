package com.qd.longchat.order.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.longchat.base.callback.QDRefreshCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.databases.QDOrderHelper;
import com.longchat.base.manager.QDOrderManager;
import com.longchat.base.manager.listener.QDRefreshCallBackManager;
import com.longchat.base.model.QDOrder;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.activity.QDOrderSearchActivity;
import com.qd.longchat.model.QDOrderTypeModel;
import com.qd.longchat.order.QDOrderSelectFragment;
import com.qd.longchat.order.QDOrderUtil;
import com.qd.longchat.order.adapter.QDOrderAdapter;
import com.qd.longchat.util.QDUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/20 下午1:29
 */
public class QDOrderActivity extends QDBaseActivity implements QDRefreshCallBack {

    @BindView(R2.id.view_order_title)
    View viewTitle;
    @BindView(R2.id.view_order_search)
    View viewSearch;
    @BindView(R2.id.tv_order_type)
    TextView tvType;
    @BindView(R2.id.tv_order_level)
    TextView tvLevel;
    @BindView(R2.id.tv_order_status)
    TextView tvStatus;
    @BindView(R2.id.lv_order_list)
    PullToRefreshListView lvList;

    @BindString(R2.string.im_text_order)
    String strTitle;
    @BindString(R2.string.im_text_send_order)
    String strSend;

    private QDOrderAdapter adapter;

    private Context context;

    private int orderType = -1;

    private int index;

    private List<QDOrder> orderList;

    private List<String> typeList, levelList, statusList;

    private List<QDOrderTypeModel> typeModelList;

    private QDOrderSelectFragment.OnSelectClickListener selectListener = new QDOrderSelectFragment.OnSelectClickListener() {
        @Override
        public void onSelectClick(int type, int position) {
            orderType = type;
            index = position;
            setAdapterOrderList();
        }
    };

    private QDResultCallBack<Integer> managerListener = new QDResultCallBack<Integer>() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, "查看用户是否是管理员失败");
        }

        @Override
        public void onSuccess(Integer isPower) {
            QDOrderManager.getInstance().getOrderTypes(listener);
            if (isPower == 1) {
                tvTitleRight.setVisibility(View.VISIBLE);
            } else {
                tvTitleRight.setVisibility(View.GONE);
            }
        }
    };

    private QDResultCallBack<String> listener = new QDResultCallBack<String>() {
        @Override
        public void onError(String errorMsg) {
            getWarningDailog().dismiss();
            QDUtil.showToast(context, errorMsg);
        }

        @Override
        public void onSuccess(String model) {
            getWarningDailog().dismiss();
            typeModelList = QDGson.getGson().fromJson(model, new TypeToken<List<QDOrderTypeModel>>() {
            }.getType());
            QDOrderUtil.getInstance().setOrderTypeList(typeModelList);
            for (QDOrderTypeModel typeModel : typeModelList) {
                typeList.add(typeModel.getTypeName());
            }
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        context = this;
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        tvTitleRight.setText(strSend);
        initData();
        isManager();
        orderList = QDOrderHelper.loadAll();
        if (orderList == null || orderList.size() == 0) {
            getOrderList();
        }
        adapter = new QDOrderAdapter(this);
        lvList.setAdapter(adapter);
        adapter.setOrderList(orderList);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QDOrder order = orderList.get(position - 1);
                if (order.getIsRead() == QDOrder.STATUS_UNREAD) {
                    QDOrderHelper.setOrderRead(order.getOrderId());
                    orderList.remove(position - 1);
                    order.setIsRead(QDOrder.STATUS_READ);
                    orderList.add(position - 1, order);
                    adapter.setOrderList(orderList);
                }
                Intent intent = new Intent(context, QDOrderDetailActivity.class);
                intent.putExtra(QDOrderDetailActivity.ORDER_ID, orderList.get(position - 1).getOrderId());
                startActivity(intent);
            }
        });

        lvList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<SwipeMenuListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
                getOrderList();
                lvList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lvList.onRefreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {

            }
        });

        QDRefreshCallBackManager.getInstance().addCallBack(this);
    }

    private void initData() {
        typeList = new ArrayList<>();
        levelList = new ArrayList<>();
        statusList = new ArrayList<>();
        typeList.add("全部");
        levelList.add("一般");
        levelList.add("重要");
        levelList.add("紧急");
        statusList.add("未读");
        statusList.add("已读");
    }

    private void isManager() {
        QDOrderManager.getInstance().isManager(managerListener);
    }

    private void getOrderList() {
        QDOrderManager.getInstance().getOrderList(new QDResultCallBack() {
            @Override
            public void onError(String errorMsg) {
                QDUtil.showToast(context, "获取列标失败:" + errorMsg);
            }

            @Override
            public void onSuccess(Object o) {
                setAdapterOrderList();
            }
        });
    }


    @OnClick({R2.id.tv_order_type, R2.id.tv_order_level, R2.id.tv_order_status, R2.id.tv_title_right, R2.id.view_order_search})
    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.tv_title_right) {
            Intent intent = new Intent(QDOrderActivity.this, QDOrderSendActivity.class);
            startActivityForResult(intent, 1000);

        } else if (i1 == R.id.tv_order_type) {
            QDOrderUtil.getInstance().showDialog(QDOrderActivity.this, QDOrderSelectFragment.TYPE_OF_TYPE, typeList, selectListener);

        } else if (i1 == R.id.tv_order_level) {
            QDOrderUtil.getInstance().showDialog(QDOrderActivity.this, QDOrderSelectFragment.TYPE_OF_LEVEL, levelList, selectListener);

        } else if (i1 == R.id.tv_order_status) {
            QDOrderUtil.getInstance().showDialog(QDOrderActivity.this, QDOrderSelectFragment.TYPE_OF_STATUS, statusList, selectListener);

        } else if (i1 == R.id.view_order_search) {
            Intent i = new Intent(context, QDOrderSearchActivity.class);
            startActivityForResult(i, 1001);

        }
    }

    private void setAdapterOrderList() {
        switch (orderType) {
            case QDOrderSelectFragment.TYPE_OF_TYPE:
                if (index == 0) {
                    orderList = QDOrderHelper.loadAll();
                } else {
                    QDOrderTypeModel model = typeModelList.get(index - 1);
                    orderList = QDOrderHelper.getOrdersByType(model.getTypeId());
                }
                break;
            case QDOrderSelectFragment.TYPE_OF_LEVEL:
                orderList = QDOrderHelper.getOrdersByLevel(index + 1);
                break;
            case QDOrderSelectFragment.TYPE_OF_STATUS:
                orderList = QDOrderHelper.getOrdersByStatus(index);
                break;
            default:
                orderList = QDOrderHelper.loadAll();
                break;

        }
        adapter.setOrderList(orderList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setAdapterOrderList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDRefreshCallBackManager.getInstance().removeLast();
    }

    @Override
    public void onRefresh() {
        setAdapterOrderList();
    }
}
