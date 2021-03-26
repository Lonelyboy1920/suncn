package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.model.gd.QDGroupNoticeModel;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDGroupNoticeListAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/15 上午10:10
 */
public class QDGroupNoticeListActivity extends QDBaseActivity {

    @BindView(R2.id.view_gnl_title)
    View viewTitle;
    @BindView(R2.id.lv_gnl_list)
    SwipeMenuListView listView;
    @BindView(R2.id.view_empty)
    ViewStub viewEmpty;
    @BindView(R2.id.view_place)
    View viewPlace;
    @BindString(R2.string.group_info_notice)
    String strTitle;
    @BindString(R2.string.group_notice_issue)
    String strIssue;

    @BindColor(R2.color.colorSwipeMenuDelete)
    int colorMenuDel;
    @BindString(R2.string.conversation_del)
    String strDel;
    @BindString(R2.string.group_notice_get_error)
    String strGetError;
    @BindString(R2.string.group_notice_delete_error)
    String strDelError;
    @BindString(R2.string.group_notice_issue_success)
    String strIssueSuccess;
    @BindString(R2.string.group_notice_issue_failed)
    String strIssueFailed;

    private String groupId;
    private boolean isEdit;

    private QDGroupNoticeListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_group_notice_list);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        viewPlace.setVisibility(View.VISIBLE);
        tvTitleName.setText(strTitle);

        groupId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID);
        isEdit = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_EDIT, false);
        if (isEdit) {
            tvTitleRight.setVisibility(View.VISIBLE);
            tvTitleRight.setText(strIssue);
        } else {
            tvTitleRight.setVisibility(View.GONE);
        }

        adapter = new QDGroupNoticeListAdapter(context);
        listView.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                menu.addMenuItem(QDUtil.createMenuItem(context, QDUtil.dp2px(context, 90), strDel, colorMenuDel));
            }
        };
        listView.setMenuCreator(creator);
        getWarningDailog().show();

        QDClient.getInstance().getGroupNotices(groupId, new QDResultCallBack<List<QDGroupNoticeModel>>() {
            @Override
            public void onError(String errorMsg) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, strGetError + errorMsg);
            }

            @Override
            public void onSuccess(List<QDGroupNoticeModel> modelList) {
                getWarningDailog().dismiss();
                if (modelList == null || modelList.size() == 0) {
                    listView.setEmptyView(viewEmpty);
                } else {
                    adapter.setModelList(modelList);
                }
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                QDGroupNoticeModel model = adapter.getItem(position);
                QDClient.getInstance().deleteGroupNotice(groupId, model.getId(), new QDResultCallBack() {
                    @Override
                    public void onError(String errorMsg) {
                        QDUtil.showToast(context, strDelError + errorMsg);
                    }

                    @Override
                    public void onSuccess(Object o) {
                        adapter.deleteItem(position);
                        if (adapter.getCount() == 0) {
                            listView.setEmptyView(viewEmpty);
                        }
                    }
                });
                return false;
            }
        });

        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QDGroupNoticeActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_EDIT, true);
                startActivityForResult(intent, 1000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final String notice = data.getStringExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_NOTICE);
            QDClient.getInstance().issueNotice(groupId, notice, new QDResultCallBack<String>() {
                @Override
                public void onError(String errorMsg) {
                    getWarningDailog().dismiss();
                    QDUtil.showToast(context, strIssueFailed + errorMsg);
                }

                @Override
                public void onSuccess(String id) {
                    getWarningDailog().dismiss();
                    QDUtil.showToast(context, strIssueSuccess);
                    QDGroupNoticeModel model = new QDGroupNoticeModel();
                    model.setCreateUid(QDLanderInfo.getInstance().getId());
                    model.setCreateUname(QDLanderInfo.getInstance().getName());
                    model.setCreateTime(System.currentTimeMillis() / 1000);
                    model.setNotice(notice);
                    model.setId(id);
                    adapter.addModel(model);
                }
            });
        }
    }
}
