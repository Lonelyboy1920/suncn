package com.qd.longchat.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.longchat.base.databases.QDGroupNoticeHelper;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDGroupNotifyAdapter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.qd.longchat.R2;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/11 下午4:10
 */
public class QDGroupNotifyActivity extends QDBaseActivity {

    @BindView(R2.id.view_fi_title)
    View viewTitle;
    @BindView(R2.id.lv_fi_list)
    SwipeMenuListView lvList;

    @BindString(R2.string.group_notify_title)
    String strTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_friend_invite);
        ButterKnife.bind(this);
        findViewById(R.id.view_place).setVisibility(View.VISIBLE);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);

        QDGroupNotifyAdapter adapter = new QDGroupNotifyAdapter(context, QDGroupNoticeHelper.loadAll());
        lvList.setAdapter(adapter);
        QDGroupNoticeHelper.setReaded();
    }
}
