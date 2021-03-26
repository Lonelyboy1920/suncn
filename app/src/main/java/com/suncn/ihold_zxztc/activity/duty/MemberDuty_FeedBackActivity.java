package com.suncn.ihold_zxztc.activity.duty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDateUtils;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.ParamByTypeBean;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.view.MenuItemEditLayout;
import com.suncn.ihold_zxztc.view.MenuItemLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import skin.support.content.res.SkinCompatResources;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

/**
 * 提交履职反馈
 */
public class MemberDuty_FeedBackActivity extends BaseActivity {
    @BindView(id = R.id.tv_date, click = true)
    private MenuItemEditLayout tvDate;
    @BindView(id = R.id.tv_duty_type, click = true)
    private MenuItemEditLayout tvDutyType;
    @BindView(id = R.id.et_place)
    private GIEditText etPlace;
    @BindView(id = R.id.et_content)
    private GIEditText etContent;
    private DatePicker datePicker;
    private AlertDialog ad;
    private String strTime;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private NormalListDialog normalListDialog;
    private String[] typeStrings;
    private String strPlace = "";
    private String strContent = "";

    @Override
    public void setRootView() {
        setStatusBar();
        isShowBackBtn = true;
        setContentView(R.layout.activity_memberduty_feedback);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        findViewById(R.id.view_place).setVisibility(View.GONE);
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        setHeadTitle(getString(R.string.string_duty_feedback_submit));
        goto_Button.setText(R.string.string_submit);
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        goto_Button.refreshFontType(activity, "2");
        tvDate.setValue(GIDateUtils.getTodayDate());
        etPlace.setTextView(findViewById(R.id.tv_place_count));
        etContent.setTextView(findViewById(R.id.tv_content_count));
        getParamByType();
    }

    /**
     * 委员履职反馈
     */
    private void commitData() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strType", tvDutyType.getValue());
        textParamMap.put("strContent", strContent);
        textParamMap.put("strPlace", strPlace);
        textParamMap.put("strHappenDate", tvDate.getValue().toString());
        doRequestNormal(ApiManager.getInstance().addMemberRecord(textParamMap), 0);
    }

    /**
     * 获取解决程度
     */
    private void getParamByType() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "13");
        doRequestNormal(ApiManager.getInstance().getParamByType(textParamMap), 1);
    }

    private void doLogic(int what, Object object) {
        prgDialog.closePrgDialog();
        switch (what) {
            case 0:
                showToast(getString(R.string.string_submit_success));
                finish();
                break;
            case 1:
                ParamByTypeBean paramByTypeBean = (ParamByTypeBean) object;
                typeStrings = new String[paramByTypeBean.getObjList().size()];
                for (int i = 0; i < paramByTypeBean.getObjList().size(); i++) {
                    typeStrings[i] = paramByTypeBean.getObjList().get(i).getStrName();
                }
                break;
        }
    }

    public void dateTimePicKDialog(final View inputDate) {
        LinearLayout dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_datetime, null);
        datePicker = dateTimeLayout.findViewById(R.id.datepicker);
        init(datePicker);
        ad = new AlertDialog.Builder(activity)
                .setTitle(getString(R.string.picture_please_select) + getString(R.string.string_happen_time))
                .setView(dateTimeLayout)
                .setPositiveButton(getString(R.string.string_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        tvDate.setValue(strTime);
                    }
                })
                .setNegativeButton(getString(R.string.string_cancle), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ad.dismiss();
                    }
                }).show();
    }

    public void init(final DatePicker datePicker) {
        datePicker.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        long s = 24 * 60 * 60 * 1000;
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

    private void showMyDialog() {
        normalListDialog = new NormalListDialog(activity, typeStrings);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = typeStrings[position];
                tvDutyType.setValue(text);
                normalListDialog.dismiss();
            }
        });
        normalListDialog.title(getString(R.string.picture_please_select) + getString(R.string.string_duty_type));
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        normalListDialog.show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_date:
                dateTimePicKDialog(tvDate);
                break;
            case R.id.tv_duty_type:
                showMyDialog();
                break;
            case R.id.btn_goto:
                strPlace = etPlace.getText().toString();
                strContent = etContent.getText().toString();
                if (GIStringUtil.isBlank(tvDutyType.getValue())) {
                    showToast(tvDutyType.getHint());
                } else if (GIStringUtil.isBlank(strPlace)) {
                    showToast(etPlace.getHint().toString());
                    etPlace.requestFocus();
                } else if (GIStringUtil.isBlank(strContent)) {
                    showToast(etContent.getHint().toString());
                    etContent.requestFocus();
                } else {
                    commitData();
                }
                break;
        }
    }
}
