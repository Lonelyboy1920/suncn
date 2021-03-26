package com.suncn.ihold_zxztc.activity;

import android.content.Intent;
import android.widget.ImageView;

import com.gavin.giframe.ui.BindView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;

/**
 * Author :Sea
 * Date :2019-12-13 9:58
 * PackageName:com.suncn.zxztc_dwxzx.activity
 * Desc:
 */
public class SplashGuideActivity extends BaseActivity {
    @BindView(id = R.id.banner_guide_content)
    private BGABanner banner_guide_content;

    @Override
    public void onResume() {
        super.onResume();
        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        banner_guide_content.setBackgroundResource(android.R.color.white);
    }

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void initData() {
        super.initData();
        // Bitmap 的宽高在 maxWidth maxHeight 和 minWidth minHeight 之间
        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
        // 设置数据源
        if (ProjectNameUtil.isGZSZX(activity)) {
            banner_guide_content.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                    R.mipmap.icon_guide_one,
                    R.mipmap.icon_guide_three,
                    R.mipmap.icon_guide_fore);
        } else {
            banner_guide_content.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                    R.mipmap.icon_guide_one,
                    R.mipmap.icon_guide_two,
                    R.mipmap.icon_guide_three,
                    R.mipmap.icon_guide_fore);
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        banner_guide_content.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                startActivity(new Intent(SplashGuideActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
