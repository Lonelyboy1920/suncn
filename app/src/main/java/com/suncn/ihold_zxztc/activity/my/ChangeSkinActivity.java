package com.suncn.ihold_zxztc.activity.my;

import android.view.View;
import android.widget.LinearLayout;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;

/**
 * @author :Sea
 * Date :2020-4-15 11:29
 * PackageName:com.suncn.ihold_zxztc.activity.my
 * Desc:换肤Activity
 */
public class ChangeSkinActivity extends BaseActivity {
    @BindView(id = R.id.ll_green, click = true)
    private LinearLayout ll_green;
    @BindView(id = R.id.ll_blue, click = true)
    private LinearLayout ll_blue;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_change_skin);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_theme_skinning));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_green: //恢复默主题
                GISharedPreUtil.setValue(activity, "curskin", "1");
                restoreDefaultTheme();
                break;
            case R.id.ll_blue: // 蓝色
                GISharedPreUtil.setValue(activity, "curskin", "2");
                changeSkinTheme("blue.skin");
                break;
        }
    }


}
