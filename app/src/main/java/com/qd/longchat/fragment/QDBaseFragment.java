package com.qd.longchat.fragment;

import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.view.QDAlertView;

import java.util.Objects;

import butterknife.Unbinder;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/17 下午1:38
 */

public class QDBaseFragment extends Fragment {

    protected Context context;
    protected QDAlertView warningDialog;
    protected Unbinder unbinder;

    protected TextView tvTitleBack;
    protected TextView tvTitleName;
    protected TextView tvTitleRight;
    protected TextView tvTitleClose;
    protected TextView tvTitleSunname;
    protected TextView tvTitleFun;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    protected QDAlertView getWarningDailog(String content) {
        if (warningDialog == null) {
            warningDialog = new QDAlertView.Builder()
                    .setContext(context)
                    .setWarning(content)
                    .setStyle(QDAlertView.Style.Warn)
                    .build();
        }
        return warningDialog;
    }

    protected QDAlertView getWarningDailog() {
        if (warningDialog == null) {
            warningDialog = new QDAlertView.Builder()
                    .setContext(context)
                    .setWarning("请稍后...")
                    .setStyle(QDAlertView.Style.Warn)
                    .build();
        }
        return warningDialog;
    }

    protected void initTitleView(View view) {
        tvTitleBack = view.findViewById(R.id.tv_title_back);
        tvTitleName = view.findViewById(R.id.tv_title_name);
        tvTitleRight = view.findViewById(R.id.tv_title_right);
        tvTitleClose = view.findViewById(R.id.tv_title_close);
        tvTitleSunname = view.findViewById(R.id.tv_title_subname);
        tvTitleFun = view.findViewById(R.id.tv_title_fun);

        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        tvTitleBack.setVisibility(View.VISIBLE);
        tvTitleClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
