package com.suncn.ihold_zxztc.view;

import android.content.Context;
import com.google.android.material.tabs.TabLayout;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

import skin.support.design.widget.SkinMaterialTabLayout;

/**
 * 自定义tabLayput
 * item条数小：填充屏幕
 * item条数多：实现滚动
 */
public class MyTabLayout extends SkinMaterialTabLayout {
    public MyTabLayout(Context context) {
        super(context);
    }

    public MyTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addTab(Tab tab, int position, boolean setSelected) {
        super.addTab(tab, position, setSelected);
//        setTab(tab);
    }

    private void setTab(Tab tab) {
        try {
            Field mViewF = Tab.class.getDeclaredField("mView");
            mViewF.setAccessible(true);
            LinearLayout mView = (LinearLayout) mViewF.get(tab);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            mView.setLayoutParams(layoutParams);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
