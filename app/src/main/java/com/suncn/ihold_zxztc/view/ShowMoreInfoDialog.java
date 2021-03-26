package com.suncn.ihold_zxztc.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;

public class ShowMoreInfoDialog extends Dialog {
    private final Context activity;
    private String text;
    private String title;

    public ShowMoreInfoDialog(Context activity, String text, String title) {
        super(activity, R.style.instructionsDialog);
        this.activity = activity;
        this.text = text;
        this.title=title;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_show_more_info);
        TextView tvVaule = (TextView) findViewById(R.id.tv_value);
        tvVaule.setText(text);
        TextView tvTitle=(TextView)findViewById(R.id.tv_title);
        tvTitle.setText(title);
    }

}
