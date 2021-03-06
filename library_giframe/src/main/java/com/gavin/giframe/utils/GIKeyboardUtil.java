package com.gavin.giframe.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gavin.giframe.ActivityLifecycleCallbacks;

import java.lang.reflect.Field;

/**
 * 提供更加便捷的方式针对给定的 EditText 显示/隐藏软键盘，并且提供了工具方法判断键盘是否当前可见。
 */
public class GIKeyboardUtil {
    /**
     * 显示软键盘的延迟时间
     */
    private static final int SHOW_KEYBOARD_DELAY_TIME = 200;
    private static final String TAG = GIKeyboardUtil.class.getSimpleName();
    private final static int KEYBOARD_VISIBLE_THRESHOLD_DP = 100;


    public static void showKeyboard(final EditText editText, boolean delay) {
        showKeyboard(editText, delay ? SHOW_KEYBOARD_DELAY_TIME : 0);
    }

    /**
     * 针对给定的editText显示软键盘（editText会先获得焦点）. 可以和{@link #hideKeyboard(View)}
     * 搭配使用，进行键盘的显示隐藏控制。
     */
    public static void showKeyboard(final EditText editText, int delay) {
        if (null == editText)
            return;

        if (!editText.requestFocus()) {
            Log.w(TAG, "showSoftInput() can not get focus");
            return;
        }
        if (delay > 0) {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) editText.getContext().getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }, delay);
        } else {
            InputMethodManager imm = (InputMethodManager) editText.getContext().getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 隐藏软键盘 可以和{@link #showKeyboard(EditText, boolean)}搭配使用，进行键盘的显示隐藏控制。
     *
     * @param view 当前页面上任意一个可用的view
     */
    public static boolean hideKeyboard(final View view) {
        if (null == view)
            return false;
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // 即使当前焦点不在editText，也是可以隐藏的。
        return inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 软键盘是否已显示
     */
    public static boolean isKeyboardVisible(Activity activity) {
        Rect r = new Rect();
        View activityRoot = GIViewUtil.getActivityRoot(activity);
        int visibleThreshold = Math.round(GIDensityUtil.dip2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));
        activityRoot.getWindowVisibleDisplayFrame(r);
        int heightDiff = activityRoot.getRootView().getHeight() - r.height();
        return heightDiff > visibleThreshold;
    }


    /**
     * Fix the leaks of soft input.
     *
     * @param activity The activity.
     */
    public static void fixSoftInputLeaks(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        String[] leakViews = new String[]{"mLastSrvView", "mCurRootView", "mServedView", "mNextServedView"};
        for (String leakView : leakViews) {
            try {
                Field leakViewField = InputMethodManager.class.getDeclaredField(leakView);
                if (!leakViewField.isAccessible()) {
                    leakViewField.setAccessible(true);
                }
                Object obj = leakViewField.get(imm);
                if (!(obj instanceof View)) continue;
                View view = (View) obj;
                if (view.getRootView() == activity.getWindow().getDecorView().getRootView()) {
                    leakViewField.set(imm, null);
                }
            } catch (Throwable ignore) {
                ignore.printStackTrace();
            }
        }
    }

    /**
     * 软键盘显示/隐藏监听
     *
     * @param activity Activity
     * @param listener KeyboardVisibilityEventListener
     */
    @SuppressWarnings("deprecation")
    public static void setVisibilityEventListener(final Activity activity, final KeyboardVisibilityEventListener listener) {
        if (activity == null) {
            throw new NullPointerException("Parameter:activity must not be null");
        }
        if (listener == null) {
            throw new NullPointerException("Parameter:listener must not be null");
        }
        final View activityRoot = GIViewUtil.getActivityRoot(activity);
        final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect r = new Rect();
            private final int visibleThreshold = Math.round(GIDensityUtil.dip2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));
            private boolean wasOpened = false;

            @Override
            public void onGlobalLayout() {
                activityRoot.getWindowVisibleDisplayFrame(r);
                int heightDiff = activityRoot.getRootView().getHeight() - r.height();
                boolean isOpen = heightDiff > visibleThreshold;
                if (isOpen == wasOpened) {
                    // keyboard state has not changed
                    return;
                }
                wasOpened = isOpen;
                boolean removeListener = listener.onVisibilityChanged(isOpen, heightDiff);
                if (removeListener) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activityRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        activityRoot.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        };
        activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        activity.getApplication().registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks(activity) {
            @Override
            protected void onTargetActivityDestroyed() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activityRoot.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                } else {
                    activityRoot.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
                }
            }
        });
    }

    public interface KeyboardVisibilityEventListener {
        /**
         * @return to remove global listener or not
         */
        boolean onVisibilityChanged(boolean isOpen, int heightDiff);
    }
}
