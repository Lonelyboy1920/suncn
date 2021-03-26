package com.suncn.ihold_zxztc.activity.message;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;

/**
 * 通讯录/委员信息（查看履职）/选择人员
 */
public class Contact_MainActivity extends BaseActivity {
    @Override
    public void setRootView() {
        setContentView(R.layout.activity_fragment);
    }

    @Override
    public void initData() {
        super.initData();
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.frameLayout, new Contact_MainFragment())   // 此处的R.id.frameLayout是要盛放fragment的父容器
                .commit();
    }
}
