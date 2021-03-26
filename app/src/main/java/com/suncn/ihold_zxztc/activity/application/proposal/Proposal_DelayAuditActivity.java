package com.suncn.ihold_zxztc.activity.application.proposal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.DelayAuditBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

/**
 * 延期审核
 */
public class Proposal_DelayAuditActivity extends BaseActivity {
    @BindView(id = R.id.tv_tag)
    private TextView tag_TextView;//承办单位tag  TextView
    @BindView(id = R.id.tv_apply_unit)
    private TextView applyUnit_TextView;//承办单位TextView
    @BindView(id = R.id.tv_limit_date)
    private TextView limitDate_TextView;//期限日期TextView
    @BindView(id = R.id.tv_date)
    private TextView date_TextView;//延期日期TextView
    @BindView(id = R.id.tv_reason)
    private TextView reason_TextView;//申请理由TextView
    @BindView(id = R.id.rb_pass)
    private RadioButton pass_RadioButton;//同意延期RadioButton
    @BindView(id = R.id.rb_refuse)
    private RadioButton refuse_RadioButton;//同意延期RadioButton
    @BindView(id = R.id.ll_select_date, click = true)
    private LinearLayout selectDate_LinearLayout; // 选择最新应答复日期LinearLayout
    @BindView(id = R.id.tv_select_date)
    private TextView selectDate_TextView; // 选择最新应答复日期
    @BindView(id = R.id.et_idea)
    private GIEditText idea_EditText;//具体意见EditText

    private String strId;
    private String strCaseDelayId;//提案的延期审核id
    private boolean isSocialOpinions; //社情民意延期审核
    private String strPubDelayId; // 社情民意的延期审核id
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private DatePicker datePicker;
    private AlertDialog ad;
    private String strTime;
    private String strReportDate; // 应答复日期

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_delayaudit);
    }

    @Override
    public void initData() {
        super.initData();
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getString(R.string.string_submit));
        goto_Button.refreshFontType(activity, "2");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        idea_EditText.setTextView(findViewById(R.id.tv_analysis_count));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tag_TextView.setText(getString(R.string.string_undertaker));
            setHeadTitle(getString(R.string.string_delayed_audit));
            strId = bundle.getString("strId");
            isSocialOpinions = bundle.getBoolean("isSocialOpinions");
            getDelayInfo();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                if (GIStringUtil.isBlank(idea_EditText.getText().toString().trim())) {
                    showToast(idea_EditText.getHint().toString());
                    idea_EditText.requestFocus();
                } else if (GIStringUtil.isBlank(date_TextView.getText().toString().trim())) {
                    showToast(getString(R.string.string_please_select_an_extension_date));
                } else {
                    doDelayAudit();
                }
                break;
            case R.id.ll_select_date:
                dateTimePicKDialog(selectDate_TextView);
                //selectDateDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        pass_RadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    selectDate_LinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        refuse_RadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    selectDate_LinearLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 延期审核
     */
    private void doDelayAudit() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strCheckIdea", idea_EditText.getText().toString().trim());
        if (pass_RadioButton.isChecked()) {
            textParamMap.put("strDelayLimitDate", selectDate_TextView.getText().toString().trim());
            textParamMap.put("strCheckAgreeState", "0");
        } else {
            textParamMap.put("strCheckAgreeState", "1");
        }
        if (isSocialOpinions) {
            textParamMap.put("strPubDelayId", strPubDelayId);
            doRequestNormal(ApiManager.getInstance().dealOpinionDelayAudit(textParamMap), 0);
        } else {
            textParamMap.put("strCaseDelayId", strCaseDelayId);
            doRequestNormal(ApiManager.getInstance().dealProposalDelayAudit(textParamMap), 0);
        }
    }

    /**
     * 获取延期信息
     */
    private void getDelayInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        if (isSocialOpinions) {
            textParamMap.put("strId", strId);
            doRequestNormal(ApiManager.getInstance().getOpinionDelayInfo(textParamMap), 1);
        } else {
            textParamMap.put("strId", strId);
            doRequestNormal(ApiManager.getInstance().getProposalDelayInfo(textParamMap), 1);
        }
    }

    /**
     * 请求结果
     *
     * @param what
     * @param obj
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        prgDialog.closePrgDialog();
        switch (what) {
            case 0:
                try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 1:
                try {
                    DelayAuditBean delayAuditBean = (DelayAuditBean) obj;
                    applyUnit_TextView.setText(delayAuditBean.getStrUnitName());
                    strReportDate = delayAuditBean.getStrDelayLimitDate();
                    date_TextView.setText(strReportDate);
                    selectDate_TextView.setText(strReportDate);
                    limitDate_TextView.setText(delayAuditBean.getStrHandlerLimitDate());
                    reason_TextView.setText(delayAuditBean.getStrDelayIdea());
                    strCaseDelayId = delayAuditBean.getStrCaseDelayId();
                    strPubDelayId = delayAuditBean.getStrPubDelayId();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    public void dateTimePicKDialog(final View inputDate) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.dialog_datetime, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        init(datePicker);

        ad = new AlertDialog.Builder(activity, R.style.MyDatePickerDialogTheme)
                .setTitle(R.string.string_please_select_time)
                .setView(dateTimeLayout)
                .setPositiveButton(getString(R.string.string_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        selectDate_TextView.setText(strTime);
                        // getQhViewHtml();
                    }
                })
                .setNegativeButton(getString(R.string.string_cancle), new DialogInterface.OnClickListener() {
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

}
