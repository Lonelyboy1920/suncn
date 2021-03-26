package com.qd.longchat.acc.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.qd.longchat.R2;

import butterknife.BindString;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/26 下午3:31
 */
public class QDAccListActivity extends QDAccSelfActivity {

    @BindString(R2.string.acc_title)
    String strTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isHaveTag = true;
        super.onCreate(savedInstanceState);
        tvTitleName.setText(strTitle);
        tvTitleRight.setVisibility(View.GONE);
    }
}
