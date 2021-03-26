package com.qd.longchat.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/10 下午2:37
 */

public class QDAnimationUtil {

    /**
     * 设置透明渐变动画
     * @param start
     * @param end
     * @param duration
     * @return
     */
    public static AlphaAnimation setAlphaAnimation(float start, float end, long duration) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(start, end);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        return alphaAnimation;
    }

    public static TranslateAnimation setTranslateAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 100, 0);
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}
