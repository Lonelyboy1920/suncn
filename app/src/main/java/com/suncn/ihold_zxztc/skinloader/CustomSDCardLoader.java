package com.suncn.ihold_zxztc.skinloader;

import android.content.Context;

import java.io.File;

import skin.support.load.SkinSDCardLoader;
import skin.support.utils.SkinFileUtils;

/**
 * @author :Sea
 * Date :2020-4-15 11:15
 * PackageName:com.suncn.ihold_zxztc.skinloader
 * Desc: 自定义皮肤加载策略
 */
public class CustomSDCardLoader extends SkinSDCardLoader {
    public static final int SKIN_LOADER_STRATEGY_SDCARD = Integer.MAX_VALUE;

    @Override
    protected String getSkinPath(Context context, String skinName) {
        return new File(SkinFileUtils.getSkinDir(context), skinName).getAbsolutePath();
    }

    @Override
    public int getType() {
        return SKIN_LOADER_STRATEGY_SDCARD;
    }
}
