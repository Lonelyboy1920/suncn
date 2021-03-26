package com.qd.longchat.acc.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.QDAccManager;
import com.longchat.base.model.gd.QDAccModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.application.QDApplication;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/26 下午3:34
 */
public class QDAccDetailActivity extends QDBaseActivity {

    @BindView(R2.id.view_ad_title)
    View viewTitle;
    @BindView(R2.id.iv_ad_icon)
    CircleImageView ivIcon;
    @BindView(R2.id.tv_ad_name)
    TextView tvName;
    @BindView(R2.id.tv_ad_desc)
    TextView tvDesc;
    @BindView(R2.id.btn_ad_into_acc)
    Button btnIntoAcc;
    @BindView(R2.id.btn_ad_follow)
    Button btnFollow;
    @BindView(R2.id.ll_ad_fetch_history)
    LinearLayout llHistoryLayout;

    @BindString(R2.string.acc_follow)
    String strFollow;
    @BindString(R2.string.acc_cancel_follow)
    String strCancelFollow;
    @BindString(R2.string.acc_clear_msg)
    String strClearMsg;
    @BindString(R2.string.acc_follow_error)
    String strFollowError;
    @BindString(R2.string.acc_cancel_follow_error)
    String strCancelFollowError;
    @BindString(R2.string.acc_get_detail_error)
    String strGetDetailError;

    private String accId;
    private QDAccModel accModel;
    private int isFollow;
    private int isWhite;
    private int isPublic;
    private QDAlertView alertView;

    private QDResultCallBack followCallBack = new QDResultCallBack() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, strFollowError + errorMsg);
        }

        @Override
        public void onSuccess(Object o) {
            isFollow = 1;
            initFollowButton();
        }
    };

    private QDResultCallBack cancelFollowCallBack = new QDResultCallBack() {
        @Override
        public void onError(String errorMsg) {
            QDUtil.showToast(context, strCancelFollowError + errorMsg);
        }

        @Override
        public void onSuccess(Object o) {
            isFollow = 0;
            initFollowButton();
        }
    };

    private QDAlertView.OnStringItemClickListener stringItemClickListener = new QDAlertView.OnStringItemClickListener() {
        @Override
        public void onItemClick(String str, int position) {
            if (str.equalsIgnoreCase(strCancelFollow)) {
                QDAccManager.getInstance().cancelFollowAcc(accModel.getId(), cancelFollowCallBack);
            } else {
                QDMessageHelper.deleteMessageWithAppId(accId);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_detail);
        ButterKnife.bind(this);
        initTitleView(viewTitle);
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setBackgroundResource(R.drawable.ic_more_white);


        accId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_ACC_ID);

        getWarningDailog().show();
        QDAccManager.getInstance().getAccDetail(accId, new QDResultCallBack<QDAccModel>() {
            @Override
            public void onError(String errorMsg) {
                getWarningDailog().dismiss();
                QDUtil.showToast(context, strGetDetailError + errorMsg);
            }

            @Override
            public void onSuccess(QDAccModel model) {
                getWarningDailog().dismiss();
                accModel = model;
                tvTitleName.setText(accModel.getName());
                String iconUrl = accModel.getIcon();
                ImageLoader.getInstance().displayImage(QDUtil.replaceWebServerAndToken(iconUrl), ivIcon, QDApplication.options);
                tvName.setText(accModel.getName());
                tvDesc.setText(accModel.getIntro());
                isFollow = accModel.getIsFollow();
                isWhite = accModel.getIsWhite();
                isPublic = accModel.getIs_public();
                initFollowButton();
                initAlertView();
            }
        });
    }

    private void initFollowButton() {
        if (isPublic == 0) {
            if (isWhite == 0) {
                btnFollow.setVisibility(View.GONE);
                llHistoryLayout.setVisibility(View.GONE);
                btnIntoAcc.setVisibility(View.GONE);
            } else {
                btnFollow.setVisibility(View.VISIBLE);
                if (isFollow == 0) {
                    btnFollow.setText(strFollow);
                    btnIntoAcc.setVisibility(View.GONE);
                    llHistoryLayout.setVisibility(View.GONE);
                } else {
                    btnFollow.setText(strCancelFollow);
                    btnIntoAcc.setVisibility(View.VISIBLE);
                    llHistoryLayout.setVisibility(View.VISIBLE);
                }
            }
        } else {
            btnFollow.setVisibility(View.VISIBLE);
            if (isFollow == 0) {
                btnFollow.setText(strFollow);
                btnIntoAcc.setVisibility(View.GONE);
                llHistoryLayout.setVisibility(View.GONE);
            } else {
                btnFollow.setText(strCancelFollow);
                btnIntoAcc.setVisibility(View.VISIBLE);
                llHistoryLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initAlertView() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Bottom)
                .setSelectList(strClearMsg, strCancelFollow)
                .setOnStringItemClickListener(stringItemClickListener)
                .setDismissOutside(true)
                .build();
    }

    @OnClick({R2.id.btn_ad_into_acc, R2.id.btn_ad_follow, R2.id.ll_ad_fetch_history, R2.id.tv_title_right})
    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.btn_ad_into_acc) {
            Intent i = new Intent(context, QDAccChatActivity.class);
            i.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, accModel.getName());
            i.putExtra(QDIntentKeyUtil.INTENT_ACC_ID, accId);
            startActivity(i);

        } else if (i1 == R.id.btn_ad_follow) {
            if (isFollow == 0) {
                QDAccManager.getInstance().followAcc(accModel.getId(), followCallBack);
            } else {
                QDAccManager.getInstance().cancelFollowAcc(accModel.getId(), cancelFollowCallBack);
            }

        } else if (i1 == R.id.ll_ad_fetch_history) {
            Intent intent = new Intent(context, QDAccHistoryActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_ACC, accModel);
            startActivity(intent);

        } else if (i1 == R.id.tv_title_right) {
            if (isPublic == 1) {
                if (isFollow == 0) {
                    alertView.setSelectList(strClearMsg);
                } else {
                    alertView.setSelectList(strClearMsg, strCancelFollow);
                }
            } else {
                if (isFollow == 0 || isWhite == 0) {
                    alertView.setSelectList(strClearMsg);
                } else {
                    alertView.setSelectList(strClearMsg, strCancelFollow);
                }
            }
            alertView.show();

        }
    }


}
