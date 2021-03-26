package com.suncn.ihold_zxztc.activity.application.proposal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

/**
 * Description: 延期申请页面
 * Date:2018/9/25
 * Author:whh
 */
public class Proposal_DelayApplyActivity extends BaseActivity {
    @BindView(id = R.id.tv_date)
    private TextView date_TextView; // 应答复时间
    @BindView(id = R.id.ll_select_date, click = true)
    private LinearLayout selectDate_LinearLayout; // 选择延期时间LinearLayout
    @BindView(id = R.id.tv_select_date)
    private TextView selectDate_TextView; // 选择延期时间
    @BindView(id = R.id.tv_mike, click = true)
    private TextView mike_TextView; // 语音识别按钮
    @BindView(id = R.id.et_content)
    private GIEditText content_EditText; // 延期理由
    private String strId;
    private String strDelayLimitDate;
    private String strDelayIdea;
    private String strReportDate; // 应答复日期
    private boolean isSocialOpinions;
    private Calendar calendar;

    //private Calendar calendar;
    private SimpleDateFormat sdf;
    private DatePicker datePicker;
    private AlertDialog ad;
    private String strTime;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_delayapply);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("申请延期");
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };

        goto_Button.setText("确定");
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.refreshFontType(activity, "2");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        content_EditText.setTextView(findViewById(R.id.tv_count));
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            strId = bundle.getString("strId");
            isSocialOpinions = bundle.getBoolean("isSocialOpinions");
            strReportDate = bundle.getString("strReportDate");
        }
        date_TextView.setText(strReportDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_mike:
                new MikeEditTextUtil(activity, content_EditText).viewShow();
                break;
            case R.id.btn_goto:
                checkDelayInfo();
                break;
            case R.id.ll_select_date:
                dateTimePicKDialog(selectDate_TextView);
                //selectDateDialog();
                break;
        }
    }

    public void dateTimePicKDialog(final View inputDate) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.dialog_datetime, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        init(datePicker);

        ad = new AlertDialog.Builder(activity, R.style.MyDatePickerDialogTheme)
                .setTitle("请选择日期")
                .setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        selectDate_TextView.setText(strTime);
                        // getQhViewHtml();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ad.dismiss();
                    }
                }).show();

        //onDateChanged(null, 0, 0, 0);
        //return ad;
    }

    public void init(final DatePicker datePicker) {
        //datePicker.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        datePicker.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        long s = 24 * 60 * 60 * 1000;
        try {
            datePicker.setMinDate(sdf.parse(strReportDate).getTime() + s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.set(datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth());
        strTime = sdf.format(calendar.getTime());
        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(datePicker.getYear(), datePicker.getMonth(),
                                datePicker.getDayOfMonth());
                        strTime = sdf.format(calendar.getTime());
                        //ad.setTitle("请选择时间");
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void selectDateDialog() {
        try {
            final DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectDate_TextView.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            DatePicker datePicker = dialog.getDatePicker();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            long s = 24 * 60 * 60 * 1000;
            datePicker.setMinDate(formatter.parse(strReportDate).getTime() + s);
            dialog.setCustomTitle(null);
            dialog.show();
        } catch (Exception e) {

        }
    }

    private void doLogic(int sign, Object object) {
        prgDialog.closePrgDialog();
        setResult(-2);
        finish();
    }

    private void checkDelayInfo() {
        strDelayIdea = content_EditText.getText().toString().trim();
        strDelayLimitDate = selectDate_TextView.getText().toString().trim();
        if (GIStringUtil.isBlank(strDelayLimitDate)) {
            showToast("请选择延期日期");
        } else if (GIStringUtil.isBlank(strDelayIdea)) {
            showToast("请输入延期理由");
        } else {
            sendDelayInfo();
        }
    }

    private void sendDelayInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        textParamMap.put("strDelayLimitDate", strDelayLimitDate);
        textParamMap.put("strDelayIdea", strDelayIdea);
        if (isSocialOpinions) {
            doRequestNormal(ApiManager.getInstance().dealOpinionDelay(textParamMap), 0);
        } else {
            doRequestNormal(ApiManager.getInstance().dealProposalDelay(textParamMap), 0);
        }
    }
}
