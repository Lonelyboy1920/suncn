package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.longchat.base.dao.QDConversation;
import com.longchat.base.databases.QDConversationHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.qd.longchat.R;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.qd.longchat.holder.QDBaseInfoHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.view.QDAlertView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.qd.longchat.R2;
import com.suncn.ihold_zxztc.activity.message.Contact_DetailActivity;
import com.suncn.ihold_zxztc.utils.Utils;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/1 下午5:10
 * 聊天对象设置
 */

public class QDPersonInfoActivity extends QDBaseActivity {

    @BindView(R2.id.view_pi_title)
    View viewTitle;
    @BindView(R2.id.view_pi_top)
    View viewTop;
    @BindView(R2.id.view_pi_history)
    View viewHistory;
    @BindView(R2.id.view_pi_clear)
    View viewClear;
    @BindView(R2.id.iv_pi_icon)
    ImageView ivIcon;
    @BindView(R2.id.tv_pi_name)
    TextView tvName;
    @BindView(R2.id.rl_pi_user)
    RelativeLayout rlUser;
    @BindView(R2.id.view_place)
    View view_place;

    @BindString(R2.string.person_info_title)
    String strTitle;
    @BindString(R2.string.person_info_top)
    String strTop;
    @BindString(R2.string.person_info_history)
    String strHistory;
    @BindString(R2.string.person_info_clear)
    String strClear;
    @BindString(R2.string.group_info_clear_title)
    String strClearTitle;
    @BindString(R2.string.group_info_clear_sure)
    String strClearSure;


    private String userId;
    private QDAlertView alertView;
    private boolean isClear;
    private QDConversation conversation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true, false);
        setContentView(R.layout.activity_person_info);
        ButterKnife.bind(this);
        userId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID);
        view_place.setVisibility(View.VISIBLE);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        initUI();
        initBottomAlert();
    }

    private void initUI() {
        initUserInfo();
        initTop();
        initHistory();
        initClear();
    }

    private void initUserInfo() {
        QDUser user = QDUserHelper.getUserById(userId);
        GIImageUtil.loadImg(context, ivIcon, Utils.formatFileUrl(context, user.getPic()), 1);
        tvName.setText(user.getName());
    }

    private void initTop() {
        final QDBaseInfoHolder holder = new QDBaseInfoHolder(viewTop);
        holder.tvItemName.setText(strTop);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
        conversation = QDConversationHelper.getConversationById(userId);
        if (conversation == null) {
            holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
        } else {
            int isTop = conversation.getIsTop();
            if (isTop == QDConversation.TOP) {
                holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_open);
            } else {
                holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
            }
        }

        holder.ivItemArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conversation == null || conversation.getIsTop() == QDConversation.UNTOP) {
                    holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_open);
                    if (conversation == null) {
                        conversation = new QDConversation();
                        conversation.setId(userId);
                        conversation.setName(tvName.getText().toString());
                        conversation.setType(QDConversation.TYPE_PERSONAL);
                        conversation.setIsTop(QDConversation.TOP);
                        conversation.setTime(System.currentTimeMillis());
                    } else {
                        conversation.setIsTop(QDConversation.TOP);
                    }
                } else {
                    conversation.setIsTop(QDConversation.UNTOP);
                    holder.ivItemArrow.setImageResource(R.mipmap.im_switcher_close);
                }
                QDConversationHelper.insertConversation(conversation);
            }
        });

    }

    private void initHistory() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewHistory);
        holder.tvItemName.setText(strHistory);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
    }

    private void initClear() {
        QDBaseInfoHolder holder = new QDBaseInfoHolder(viewClear);
        holder.tvItemName.setText(strClear);
        holder.ivItemArrow.setVisibility(View.VISIBLE);
    }

    @OnClick({R2.id.rl_pi_user, R2.id.view_pi_clear, R2.id.tv_title_back, R2.id.view_pi_history})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rl_pi_user) {
            Intent intent = new Intent(context, Contact_DetailActivity.class);
            intent.putExtra("strQdUserId", userId);
            startActivity(intent);
        } else if (i == R.id.view_pi_clear) {
            alertView.show();

        } else if (i == R.id.tv_title_back) {
            onBackPressed();

        } else if (i == R.id.view_pi_history) {
            Intent intent1 = new Intent(context, QDSearchActivity.class);
            intent1.putExtra(QDIntentKeyUtil.INTENT_KEY_SEARCH_TYPE, QDSearchActivity.SEARCH_TYPE_PERSONAL_HISTORY);
            intent1.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, userId);
            startActivity(intent1);
        }
    }

    private QDAlertView.OnStringItemClickListener listener = new QDAlertView.OnStringItemClickListener() {
        @Override
        public void onItemClick(String str, int position) {
            QDMessageHelper.deleteMessageWithUserId(userId);
            if (conversation != null) {
                conversation.setSubname("");
                QDConversationHelper.updateConversation(conversation);
            }
            isClear = true;
        }
    };

    private void initBottomAlert() {
        alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Bottom)
                .setSelectList(strClearSure)
                .setTitle(strClearTitle)
                .setDismissOutside(true)
                .setOnStringItemClickListener(listener)
                .build();
    }

    @Override
    public void onBackPressed() {
        if (isClear) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
