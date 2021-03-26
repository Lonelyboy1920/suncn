package com.suncn.ihold_zxztc.activity.my;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.widget.TextView;

import com.gavin.giframe.base.GIActivityStack;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.MainActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.view.FontSizeView;

/**
 * @author :Sea
 * Date :2020-5-13 9:20
 * PackageName:com.suncn.ihold_zxztc.activity.my
 * Desc:  字体设置
 */
public class ChangeFontSizeActivity extends BaseActivity {
    @BindView(id = R.id.tv_head_title)
    private GITextView tv_head_title;
    @BindView(id = R.id.btn_back)
    private GITextView btn_back;
    @BindView(id = R.id.fsv_font_size)
    FontSizeView fsvFontSize;
    @BindView(id = R.id.tv_font_size1)
    TextView tv_font_size1;
    @BindView(id = R.id.tv_font_size2)
    TextView tv_font_size2;
    @BindView(id = R.id.tv_font_size3)
    TextView tv_font_size3;
    private float fontSizeScale;

    private boolean isChange;//用于监听字体大小是否有改动
    private int defaultPos;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_change_font_size);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("字体大小");
    }

    @Override
    public void setListener() {
        super.setListener();
        //滑动返回监听
        fsvFontSize.setChangeCallbackListener(position -> {
            int dimension = getResources().getDimensionPixelSize(R.dimen.sp_stander);
            //根据position 获取字体倍数
            fontSizeScale = (float) (0.875 + 0.125 * position);
            //放大后的sp单位
            double v = fontSizeScale * GIDensityUtil.px2sp(ChangeFontSizeActivity.this, dimension);
            //改变当前页面大小
            changeTextSize((int) v);
            isChange = !(position == defaultPos);
        });
        float scale = GISharedPreUtil.getFloat(this, "FontScale");
        if (scale > 0.5) {
            defaultPos = (int) ((scale - 0.875) / 0.125);
        } else {
            defaultPos = 1;
        }
        //注意： 写在改变监听下面 —— 否则初始字体不会改变
        fsvFontSize.setDefaultPosition(defaultPos);

        btn_back.setOnClickListener(view -> {
            if (isChange) {
                GISharedPreUtil.setValue(ChangeFontSizeActivity.this, "FontScale", fontSizeScale);
                //重启应用
//            AppManager.getAppManager().finishAllActivity();
                skipActivity(ChangeFontSizeActivity.this, MainActivity.class);
                GIActivityStack.getInstance().finishAllActivity();
            } else {
                finish();
            }
        });

    }

    /**
     * 改变textsize 大小
     */
    private void changeTextSize(int dimension) {
        tv_font_size1.setTextSize(dimension);
        tv_font_size2.setTextSize(dimension);
        tv_font_size3.setTextSize(dimension);
        tv_head_title.setTextSize(dimension);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isChange) {
            GISharedPreUtil.setValue(ChangeFontSizeActivity.this, "FontScale", fontSizeScale);
            //重启应用
//            AppManager.getAppManager().finishAllActivity();
            skipActivity(ChangeFontSizeActivity.this, MainActivity.class);
            GIActivityStack.getInstance().finishAllActivity();
        } else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重新配置缩放系数
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = res.getConfiguration();
        config.fontScale = 1;//1 设置正常字体大小的倍数
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
