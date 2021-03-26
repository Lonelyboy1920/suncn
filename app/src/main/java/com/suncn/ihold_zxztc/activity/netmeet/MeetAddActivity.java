package com.suncn.ihold_zxztc.activity.netmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDDateSelectView;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.message.Contact_MainActivity;
import com.suncn.ihold_zxztc.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.Nullable;

/**
 * 新增会议
 */
public class MeetAddActivity extends BaseActivity {
    @BindView(id = R.id.et_mc_title)
    private GIEditText etTitle;
    @BindView(id = R.id.tv_mc_start_time)
    private TextView tvStartDate;
    @BindView(id = R.id.ll_mc_start_time, click = true)
    private LinearLayout llStartDate;
    @BindView(id = R.id.tv_mc_end_time)
    private TextView tvEndDate;
    @BindView(id = R.id.ll_mc_end_time, click = true)
    private LinearLayout llEndDate;
    @BindView(id = R.id.et_mc_content)
    private GIEditText etReason;
    @BindView(id = R.id.tv_mc_member)
    private TextView tvWy;
    @BindView(id = R.id.ll_mc_member, click = true)
    private LinearLayout llWY;
    @BindView(id = R.id.btn_mc_start, click = true)
    private Button btn_mc_start;
    private QDDateSelectView startDatePicker;
    private QDDateSelectView endDatePicker;
    private long startTime;
    private long endTime;
    private SimpleDateFormat sdf;
    private String strSelectMember = "";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = new Bundle();
            if (data != null) {
                bundle = data.getExtras();
            }
            if (requestCode == 0) {
                if (bundle != null) {
                    strSelectMember = bundle.getString("strChooseValue");
                    tvWy.setText(Utils.getShowAddress(strSelectMember, false));
                }

            }
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_meeting_create);
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBar();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                showToast("发布成功");
                finish();
            }
        };
        setHeadTitle("发起会议");
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        initStartDatePicker();
        initEndDatePicker();
    }

    private void initStartDatePicker() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        startDatePicker = new QDDateSelectView(this, new QDDateSelectView.ResultHandler() {
            @Override
            public void handle(Date date) { // 回调接口，获得选中的时间
                tvStartDate.setText(sdf.format(date));
                startTime = date.getTime();
            }
        }, now, "2050-12-31 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        startDatePicker.showSpecificTime(true); // 不显示时和分
        startDatePicker.setIsLoop(false); // 不允许循环滚动
    }

    private void initEndDatePicker() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        endDatePicker = new QDDateSelectView(this, new QDDateSelectView.ResultHandler() {
            @Override
            public void handle(Date date) { // 回调接口，获得选中的时间
                tvEndDate.setText(sdf.format(date));
                endTime = date.getTime();
            }
        }, now, "2050-12-31 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        endDatePicker.showSpecificTime(true); // 不显示时和分
        endDatePicker.setIsLoop(false); // 不允许循环滚动
    }

    private void saveData() {
        textParamMap = new HashMap<>();
        textParamMap.put("strName", etTitle.getText().toString());
        textParamMap.put("strStartDate", tvStartDate.getText().toString());
        textParamMap.put("strEndDate", tvEndDate.getText().toString());
        textParamMap.put("strContent", etReason.getText().toString());
        textParamMap.put("strJointMember", strSelectMember);
        doRequestNormal(ApiManager.getInstance().AddStartMeetingServlet(textParamMap), 0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle;
        switch (v.getId()) {
            case R.id.btn_mc_start:
                if (GIStringUtil.isBlank(etTitle.getText().toString())) {
                    showToast("请输入会议标题");
                    return;
                }
                if (GIStringUtil.isBlank(tvStartDate.getText().toString())) {
                    showToast("请选择开始时间");
                    return;
                }
                if (GIStringUtil.isBlank(tvEndDate.getText().toString())) {
                    showToast("请选择结束时间");
                    return;
                }
                if (GIStringUtil.isBlank(strSelectMember)) {
                    showToast("请选择参会委员");
                    return;
                }
                saveData();
                break;
            case R.id.ll_mc_start_time:
                String now = sdf.format(new Date());
                startDatePicker.show(now);
                break;
            case R.id.ll_mc_end_time:
                if (startTime == 0) {
                    QDUtil.showToast(activity, "请先选择开始时间");
                } else {
                    String start = sdf.format(new Date(startTime + 60000));
                    endDatePicker.show(start);
                }
                break;
            case R.id.ll_mc_member:
                bundle = new Bundle();
                bundle.putString("strChooseValue", strSelectMember);
                bundle.putBoolean("isChoose", true);
                bundle.putBoolean("isShowHead", false);
                bundle.putString("showTitle", "选择参加委员");
                bundle.putInt("sign", 11);
                showActivity(activity, Contact_MainActivity.class, bundle, 0);
                break;

        }
    }
}
