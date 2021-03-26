package com.qd.longchat.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.util.QDIntentKeyUtil;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/20 下午2:20
 */
public class QDWarningActivity extends QDBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        initTitleView(findViewById(R.id.view_warn_tilte));
        tvTitleName.setText(R.string.im_text_reminder);
        TextView tvResult = findViewById(R.id.tv_warn_result);
        tvResult.setText(getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO));
    }
}
