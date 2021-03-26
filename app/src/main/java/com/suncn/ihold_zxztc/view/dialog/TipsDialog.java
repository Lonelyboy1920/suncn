package com.suncn.ihold_zxztc.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.suncn.ihold_zxztc.R;

/**
 * Created by: Sea Zhang
 * Date: 2020/2/11
 * Desc:
 */
public class TipsDialog extends BaseDialog<TipsDialog> {

    private TextView tvDialogContent;
    private TextView tvDialogConfirm;
    private String mTipsContent;

    public TipsDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.8f);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_tips, null);
        tvDialogContent = view.findViewById(R.id.tvDialogContent);
        tvDialogConfirm = view.findViewById(R.id.tvDialogConfirm);
        return view;
    }

    @Override
    public void setUiBeforShow() {
        tvDialogConfirm.setOnClickListener(view -> dismiss());
        tvDialogContent.setText(mTipsContent);
    }

    public void setTipsContent(String tipsContent) {
        this.mTipsContent = tipsContent;
    }
}
