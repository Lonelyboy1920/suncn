package com.qd.longchat.acc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.longchat.base.callback.QDMessageCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.QDAccManager;
import com.longchat.base.manager.listener.QDMessageCallBackManager;
import com.longchat.base.model.gd.QDAccMenuModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.acc.adapter.QDAccChatAdapter;
import com.qd.longchat.acc.adapter.QDAccMenuAdapter;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.activity.QDWebActivity;
import com.qd.longchat.util.QDIntentKeyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/27 下午2:06
 */
public class QDAccChatActivity extends QDBaseActivity implements QDMessageCallBack {

    @BindView(R2.id.view_ac_title)
    View viewTitle;
    @BindView(R2.id.ll_ac_bottom_layout)
    LinearLayout llBottomLayout;
    @BindView(R2.id.lv_ac_list)
    ListView listView;

    @BindDrawable(R2.drawable.ic_acc_menu)
    Drawable drawable;
    @BindColor(R2.color.colorLine)
    int colorLine;

    private String accId;
    private QDAccChatAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_chat);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleName.setText(getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME));
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setBackgroundResource(R.drawable.ic_user_detail);

        accId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_ACC_ID);
        Log.e("accId=======", accId);
        getWarningDailog().show();
        QDAccManager.getInstance().getAccMenus(accId, new QDResultCallBack<List<QDAccMenuModel>>() {
            @Override
            public void onError(String errorMsg) {
                getWarningDailog().dismiss();
                llBottomLayout.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(List<QDAccMenuModel> modelList) {
                getWarningDailog().dismiss();
                initBottomLayout(modelList);
            }
        });

        List<QDMessage> messageList = QDMessageHelper.getMessageWithAppId(accId);
        adapter = new QDAccChatAdapter(context, messageList);
        listView.setAdapter(adapter);

        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getCount() - 1);
            }
        }, 100);

        QDMessageCallBackManager.getInstance().addCallBack(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDMessageCallBackManager.getInstance().removeLast();
    }

    @OnClick(R2.id.tv_title_right)
    public void onClick() {
        Intent intent = new Intent(context, QDAccDetailActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_ACC_ID, accId);
        startActivity(intent);
    }

    /**
     * 设置菜单选项
     *
     * @param modelList
     */
    private void initBottomLayout(final List<QDAccMenuModel> modelList) {
        if (modelList == null || modelList.size() == 0) {
            llBottomLayout.setVisibility(View.GONE);
        } else {
            llBottomLayout.setVisibility(View.VISIBLE);
            llBottomLayout.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            int size = modelList.size();
            for (int i = 0; i < size; i++) {
                if (i != 0) {
                    params.setMargins(5, 0, 0, 0);
                }
                final QDAccMenuModel model = modelList.get(i);
                final LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setBackgroundResource(R.drawable.bg_list_item);
                TextView textView = new TextView(this);
                textView.setText(model.getName());
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(16);
                final List<QDAccMenuModel> subMenuList = model.getSubMenus();
                if (subMenuList != null && subMenuList.size() != 0) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    textView.setCompoundDrawablePadding(10);
                }
                textView.setGravity(Gravity.CENTER);
                linearLayout.addView(textView);
                llBottomLayout.addView(linearLayout, params);

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (subMenuList != null && subMenuList.size() != 0) {
                            showPopupwindow(linearLayout, model.getSubMenus());
                        } else {
                            if (!TextUtils.isEmpty(model.getUrl())) {
                                toWebActivity(model.getUrl());
                            }
                        }
                    }
                });
            }
        }
    }

    private void showPopupwindow(View view, final List<QDAccMenuModel> modelList) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_acc_menu, null);
        ListView listView = contentView.findViewById(R.id.list_view);
        QDAccMenuAdapter adapter = new QDAccMenuAdapter(context, modelList);
        listView.setAdapter(adapter);
        final PopupWindow window = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置PopupWindow的背景
        window.setTouchable(true);
        window.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        window.setBackgroundDrawable(new BitmapDrawable());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popWidth = contentView.getMeasuredWidth();
        int popHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View itemView = adapter.getView(i, null, listView);
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            popHeight += itemView.getMeasuredHeight();
        }
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        window.showAtLocation(view, Gravity.NO_GRAVITY, (position[0] + view.getWidth() / 2) - popWidth / 2,
                position[1] - popHeight - 10);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = modelList.get(position).getUrl();
                if (!TextUtils.isEmpty(url)) {
                    toWebActivity(url);
                }
                window.dismiss();
            }
        });
    }

    public void toWebActivity(String url) {
        Intent intent = new Intent(context, QDWebActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_WEB_URL, url);
        startActivity(intent);
    }

    @Override
    public void onReceive(List<QDMessage> messageList) {
        List<QDMessage> msgList = new ArrayList<>();
        for (QDMessage msg : messageList) {
            if (msg.getCtype() == QDMessage.CTYPE_APP && msg.getCdata().equalsIgnoreCase(accId)) {
                msgList.add(msg);
            }
        }
        if (msgList.size() != 0) {
            adapter.addMessageList(msgList);
            QDMessageHelper.setMessageReadWithAppId(accId);

            listView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(adapter.getCount() - 1);
                }
            }, 100);
        }
    }

    @Override
    public void onReceiveGMsg(String groupId, List<QDMessage> messageList) {

    }

    @Override
    public void onMsgReaded(String userId) {

    }

    @Override
    public void onRevokeMessage(String msgId, String errorMsg) {

    }

    @Override
    public void onDeleteMessage(String groupId, String msgId) {

    }
}
