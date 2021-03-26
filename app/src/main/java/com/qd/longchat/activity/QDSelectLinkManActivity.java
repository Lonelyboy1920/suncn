package com.qd.longchat.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDLinkman;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDLinkmanHelper;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDSelectLinkManAdapter;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.view.QDAlertView;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/20 下午3:33
 */
public class QDSelectLinkManActivity extends QDBaseActivity {

    @BindView(R2.id.view_sl_title)
    View viewTitle;
    @BindView(R2.id.lv_sl_list)
    ListView listView;

    @BindString(R2.string.contact_title)
    String strContactTitle;
    @BindString(R2.string.contact_is_forward)
    String strIsForwar;

    private List<QDLinkman> linkmanList;
    private QDAlertView alertView;
    private QDMessage message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_linkman);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleName.setText(strContactTitle);

        message = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE);
        linkmanList = QDLinkmanHelper.getAll();
        QDSelectLinkManAdapter adapter = new QDSelectLinkManAdapter(context, linkmanList);
        listView.setAdapter(adapter);
    }

    @OnItemClick(R2.id.lv_sl_list)
    public void onItemClick(int position) {
        QDLinkman linkman = linkmanList.get(position);
        initAlert(linkman);
    }

    private void initAlert(final QDLinkman linkman) {
        if (alertView == null) {
            alertView = new QDAlertView.Builder()
                    .setContext(context)
                    .setStyle(QDAlertView.Style.Alert)
                    .setContent(strIsForwar)
                    .isHaveCancelBtn(true)
                    .setOnButtonClickListener(new QDAlertView.OnButtonClickListener() {
                        @Override
                        public void onClick(boolean isSure) {
                            if (isSure) {
                                QDUser user = new QDUser();
                                user.setId(linkman.getId());
                                user.setName(linkman.getName());
                                user.setPic(linkman.getIcon());
                                QDClient.getInstance().forwardMessage(user, message, new QDResultCallBack() {
                                    @Override
                                    public void onError(String errorMsg) {

                                    }

                                    @Override
                                    public void onSuccess(Object o) {

                                    }
                                });
                                finish();
                            }
                        }
                    }).build();
        }
        alertView.show();
    }

}
