package com.qd.longchat.cloud.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.activity.QDBaseActivity;
import com.qd.longchat.cloud.fragment.QDCloudDetailFragment;
import com.qd.longchat.cloud.fragment.QDCloudHomeFragment;
import com.qd.longchat.cloud.fragment.QDCloudTranFragment;
import com.qd.longchat.model.QDCloud;
import com.qd.longchat.util.QDIntentKeyUtil;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/3 上午10:03
 */
public class QDCloudMainActivity extends QDBaseActivity {

    @BindView(R2.id.fl_cloud_container)
    FrameLayout flContainer;
    @BindView(R2.id.tv_cloud_doc)
    TextView tvDoc;
    @BindView(R2.id.tv_cloud_tran)
    TextView tvTran;

    @BindDrawable(R2.drawable.ic_cloud_doc)
    Drawable icDocUnSelected;
    @BindDrawable(R2.drawable.ic_cloud_doc_selected)
    Drawable icDocSelected;
    @BindDrawable(R2.drawable.ic_cloud_tran)
    Drawable icTranUnSelected;
    @BindDrawable(R2.drawable.ic_cloud_tran_selected)
    Drawable icTranSelected;


    private Fragment currentFragment;
    private QDCloudHomeFragment homeFragment;
    private QDCloudTranFragment tranFragment;
    private QDCloudDetailFragment detailFragment;
    private boolean isHome;
    private String titleName;
    private int cloudType;
    private String folderId;
    private String rootId;
    private int power;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_main);
        ButterKnife.bind(this);
        isHome = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_IS_HOME, false);
        titleName = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME);
        cloudType = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_TYPE, QDCloud.TYPE_STATING);
        folderId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_FOLDER_ID);
        rootId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_ROOT_ID);
        power = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_POWER, 0);
        initData();
    }

    private void initData() {
        currentFragment = new Fragment();
        homeFragment = new QDCloudHomeFragment();
        tranFragment = new QDCloudTranFragment();
        detailFragment = new QDCloudDetailFragment();
        if (isHome) {
            switchFragment(homeFragment);
        } else {
            switchFragment(detailFragment);
        }
    }

    /**
     * 切换fragment
     *
     * @param targetFragment
     */
    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.fl_cloud_container, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
            if (targetFragment == tranFragment) {
                tranFragment.update();
            }
        }
        currentFragment = targetFragment;
        transaction.commit();
    }

    /**
     * 初始化底部按钮
     */
    private void initBottomUi() {
        setColorAndDrawable(tvDoc, Color.parseColor("#727272"), icDocUnSelected);
        setColorAndDrawable(tvTran, Color.parseColor("#727272"), icTranUnSelected);
    }

    /**
     * 设置底部按钮颜色和图标
     *
     * @param textView
     * @param color
     * @param drawable
     */
    private void setColorAndDrawable(TextView textView, int color, Drawable drawable) {
        textView.setTextColor(color);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    @OnClick({R2.id.tv_cloud_doc, R2.id.tv_cloud_tran})
    public void onClick(View view) {
        switch (view.getId()) {
            case R2.id.tv_cloud_doc:
                initBottomUi();
                setColorAndDrawable(tvDoc, Color.parseColor("#449CEA"), icDocSelected);
                if (isHome) {
                    switchFragment(homeFragment);
                } else {
                    switchFragment(detailFragment);
                }
                break;
            case R2.id.tv_cloud_tran:
                initBottomUi();
                setColorAndDrawable(tvTran, Color.parseColor("#449CEA"), icTranSelected);
                switchFragment(tranFragment);
                break;
        }
    }


    public String getTitleName() {
        return titleName;
    }

    public int getCloudType() {
        return cloudType;
    }

    public String getFolderId() {
        return folderId;
    }

    public String getRootId() {
        return rootId;
    }

    public int getPower() {
        return power;
    }
}
