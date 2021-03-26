package com.suncn.ihold_zxztc.view;

import android.content.Context;
import com.google.android.material.tabs.TabLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;

import java.util.List;

/**
 * 自定义tabLayput
 * item条数小：填充屏幕
 * item条数多：实现滚动
 */
public class MyZxtaTabLayout extends TabLayout {
    private List<String> titles;

    public MyZxtaTabLayout(Context context) {
        super(context);
    }

    public MyZxtaTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyZxtaTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(List<String> titles) {
        this.titles = titles;
        for (String title : this.titles) {
            Tab tab = newTab();
            tab.setCustomView(R.layout.view_tab_proposal);
            if (tab.getCustomView() != null) {
                TextView text = tab.getCustomView().findViewById(R.id.tv_tab);
                text.setText(title);
            }
            this.addTab(tab);
        }
    }
}
