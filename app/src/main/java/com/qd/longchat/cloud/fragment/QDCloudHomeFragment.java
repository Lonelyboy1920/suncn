package com.qd.longchat.cloud.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.cloud.activity.QDCloudMainActivity;
import com.qd.longchat.fragment.QDBaseFragment;
import com.qd.longchat.holder.QDCloudHolder;
import com.qd.longchat.model.QDCloud;
import com.qd.longchat.util.QDIntentKeyUtil;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/3 上午10:29
 */
public class QDCloudHomeFragment extends QDBaseFragment {

    @BindView(R2.id.view_cloud_title)
    View viewTitle;
    @BindView(R2.id.view_cloud_person)
    View viewPerson;
    @BindView(R2.id.view_cloud_company)
    View viewCompany;
    @BindView(R2.id.iv_cloud_line)
    ImageView ivLine;

    @BindString(R2.string.cloud_person)
    String strPersonCloud;
    @BindString(R2.string.cloud_company)
    String strCompanyCloud;
    @BindString(R2.string.cloud_title)
    String strTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTitleView(viewTitle);
        tvTitleName.setText(strTitle);
        initPerson();
        initCompany();
    }

    private void initPerson() {
        QDCloudHolder holder = new QDCloudHolder(viewPerson);
        holder.tvItemName.setText(strPersonCloud);
        holder.ivItemMore.setVisibility(View.GONE);
        holder.ivItemIcon.setImageResource(R.drawable.ic_folder);
    }

    private void initCompany() {
        QDCloudHolder holder = new QDCloudHolder(viewCompany);
        holder.tvItemName.setText(strCompanyCloud);
        holder.ivItemMore.setVisibility(View.GONE);
        holder.ivItemIcon.setImageResource(R.drawable.ic_folder);
    }

    @OnClick({R2.id.view_cloud_person, R2.id.view_cloud_company})
    public void onClick(View view) {
        Intent intent = new Intent(context, QDCloudMainActivity.class);
        int i = view.getId();
        if (i == R.id.view_cloud_person) {
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_TYPE, QDCloud.TYPE_PERSON_CLOUD);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, strPersonCloud);

        } else if (i == R.id.view_cloud_company) {
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CLOUD_TYPE, QDCloud.TYPE_COMPANY_CLOUD);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_TITLE_NAME, strCompanyCloud);

        }
        startActivity(intent);
    }

}
