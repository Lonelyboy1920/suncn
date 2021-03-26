package com.suncn.ihold_zxztc.activity.duty;

import android.widget.FrameLayout;

import com.gavin.giframe.ui.BindView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;


public class MemberDuty_MainActivity extends BaseActivity {
    @BindView(id = R.id.frameLayout)
    private FrameLayout frameLayout;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_fragment);
    }

    @Override
    public void initData() {
        super.initData();
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.frameLayout, new MemberDuty_MainFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();
    }
}
