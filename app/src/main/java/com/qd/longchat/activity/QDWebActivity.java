package com.qd.longchat.activity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.bean.QDOpenWindowBean;
import com.qd.longchat.fragment.QDWebFragment;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/17 下午4:53
 */

public class QDWebActivity extends QDBaseActivity {

    private QDWebFragment workFragment;

    @BindView(R2.id.view_web_title)
    View viewTitle;

    private QDOpenWindowBean bean;
    private String url;

    private boolean isHaveBackEvent;

    private String leftEvent;

    private String closePage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        Gson gson = QDGson.getGson();
        String param = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_WEB_PARAM);
        if (!TextUtils.isEmpty(param)) {
            bean = gson.fromJson(param, QDOpenWindowBean.class);
            url = bean.getUrl();
        }
        String webUrl = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_WEB_URL);
        if (!TextUtils.isEmpty(webUrl)) {
            url = webUrl;
        }

        workFragment = new QDWebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(QDIntentKeyUtil.INTENT_KEY_WEB_URL, QDUtil.replaceWebUrl(url));
        bundle.putSerializable(QDIntentKeyUtil.INTENT_ACC_ARTICLE, getIntent().getSerializableExtra(QDIntentKeyUtil.INTENT_ACC_ARTICLE));
        workFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_web, workFragment, workFragment.getClass().getName()).commit();
        initTitle();
    }

    private void initTitle() {
        if (bean != null) {
            if (bean.isHidden()) {
                viewTitle.setVisibility(View.GONE);
            } else {
                viewTitle.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(bean.getBgColor())) {
                viewTitle.setBackgroundColor(Color.parseColor(bean.getBgColor()));
            } else {
                viewTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            if (!TextUtils.isEmpty(bean.getColor())) {
                tvTitleName.setTextColor(Color.parseColor(bean.getColor()));
            } else {
                tvTitleName.setTextColor(Color.WHITE);
            }
            tvTitleName.setText(bean.getTitle());
            url = bean.getUrl();
            final QDOpenWindowBean.LeftMenuBean leftMenuBean = bean.getLeftMenu();
            if (leftMenuBean != null) {
                tvTitleBack.setText(leftMenuBean.getText());
                if (!TextUtils.isEmpty(leftMenuBean.getEvent())) {
                    isHaveBackEvent = true;
                    leftEvent = leftMenuBean.getEvent();
                    tvTitleBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                }

                if (!TextUtils.isEmpty(leftMenuBean.getClosePage())) {
                    closePage = leftMenuBean.getClosePage();
                }
            }
            final QDOpenWindowBean.RightMenuBean rightMenuBean = bean.getRightMenu();
            if (rightMenuBean != null) {
                tvTitleRight.setVisibility(View.VISIBLE);
                String icon = rightMenuBean.getIcon();
                if (!TextUtils.isEmpty(icon)) {
                    if (icon.equalsIgnoreCase("more")) {
                        tvTitleRight.setBackgroundResource(R.drawable.ic_more_white);
                    } else if (icon.equalsIgnoreCase("add")) {
                        tvTitleRight.setBackgroundResource(R.drawable.ic_add);
                    } else {
                        tvTitleRight.setText(rightMenuBean.getText());
                    }
                } else {
                    tvTitleRight.setText(rightMenuBean.getText());
                }

                if (!TextUtils.isEmpty(rightMenuBean.getEvent())) {
                    tvTitleRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            workFragment.getWebView().loadUrl("javascript:" + rightMenuBean.getEvent());
                        }
                    });
                }
            } else {
                tvTitleRight.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R2.id.tv_title_back})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_title_back) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(closePage) && closePage.equalsIgnoreCase(workFragment.getWebView().getUrl())) {
            finish();
        } else {
            if (isHaveBackEvent) {
                workFragment.getWebView().loadUrl("javascript:" + leftEvent);
            } else {
                if (workFragment.getWebView().canGoBack()) {
                    workFragment.getWebView().goBack();
                } else {
                    finish();
                }
            }
        }
    }

    public void setWindow(QDOpenWindowBean bean) {
        this.bean = bean;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initTitle();
            }
        });
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(tvTitleName.getText().toString())) {
            tvTitleName.setText(title);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
