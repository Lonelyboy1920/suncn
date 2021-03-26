package com.suncn.ihold_zxztc.activity.application.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;

import com.gavin.giframe.service.GIDownloadService;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIMyIntent;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.ShowBigImgActivity;
import com.suncn.ihold_zxztc.adapter.File_GVAdapter;
import com.suncn.ihold_zxztc.bean.AuditLeaveBean;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyGridview;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 请假审核
 */
public class MeetAct_AuditLeaveActivity extends BaseActivity {
    @BindView(id = R.id.rb_pass)
    private RadioButton pass_RadioButton;//同意请假RadioButton
    @BindView(id = R.id.rb_refuse)
    private RadioButton refuse_RadioButton;//不同意RadioButton
    @BindView(id = R.id.tv_name)
    private TextView name_TextView;//申请人TextView
    @BindView(id = R.id.tv_type)
    private TextView type_TextView;//请假类型TextView
    @BindView(id = R.id.tv_reason)
    private TextView reason_TextView;//请假理由TextView
    @BindView(id = R.id.gv_scan_file)
    private GridView fileScan_MyGridView;//附件MyGridview
    private ArrayList<ObjFileBean> scanFileList;
    @BindView(id = R.id.rb_yes)
    private RadioButton rbYes;
    @BindView(id = R.id.rb_no)
    private RadioButton rbNo;
    private File_GVAdapter scanFileGridViewAdapter; // 请假材料Adapter
    private int sign;
    private String strApplyId;
    private String strCheckResult = "";
    private String strLeaveType = "1";

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_audit_leave);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_leave_for_review));
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getString(R.string.string_submit));
        goto_Button.refreshFontType(activity, "2");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sign = bundle.getInt("sign");
            strApplyId = bundle.getString("strApplyId");
        }
        getLeaveInfo();
    }

    /**
     * 获取请假审核信息
     * AuditLeaveBean
     */
    private void getLeaveInfo() {
        textParamMap = new HashMap<>();
        textParamMap.put("strApplyId", strApplyId);
        if (sign == DefineUtil.hygl) {
            doRequestNormal(ApiManager.getInstance().getMeetLeaveInfo(textParamMap), 0);
        } else {
            doRequestNormal(ApiManager.getInstance().getActLeaveInfo(textParamMap), 0);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                if (GIStringUtil.isBlank(strCheckResult)) {
                    showToast(getString(R.string.string_please_select_the_review_result));
                } else {
                    doAuditLeave();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 请假审核
     */
    private void doAuditLeave() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strApplyId", strApplyId);
        textParamMap.put("strCheckResult", strCheckResult);
        if (sign == DefineUtil.hdgl) {
            doRequestNormal(ApiManager.getInstance().getActLeaveCheck(textParamMap), 1);
        } else {
            doRequestNormal(ApiManager.getInstance().getMeetLeaveCheck(textParamMap), 1);
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        fileScan_MyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ObjFileBean objFile = (ObjFileBean) parent.getItemAtPosition(position);
                if (objFile.getStrFile_Type().contains("image/")) {
                    ArrayList<String> recordPaths = new ArrayList<>();
                    scanFileList = scanFileGridViewAdapter.getFileList();
                    for (int i = 0; i < scanFileList.size(); i++) {
                        if (scanFileList.get(i).getStrFile_Type().contains("image/")) {
                            recordPaths.add(Utils.formatFileUrl(activity, scanFileList.get(i).getStrFile_url()));
                        }
                    }
                    //图片集合 ...
                    Intent imgIntent = new Intent(activity, ShowBigImgActivity.class);
                    imgIntent.putStringArrayListExtra("paths", recordPaths);
                    imgIntent.putExtra("title", getString(R.string.string_image));
                    imgIntent.putExtra("position", position);
                    startActivity(imgIntent);
                } else {
                    Intent intent = new Intent(activity, GIDownloadService.class);
                    Bundle bundle = new Bundle();
                    String myUrl = objFile.getStrFile_url();
                    bundle.putString("url", Utils.formatFileUrl(activity, myUrl));
                    bundle.putString("filename", Utils.getMailContactName(myUrl));
                    bundle.putInt("smallIcon", R.mipmap.ic_launcher);
                    intent.putExtras(bundle);
                    startService(intent);
                }
            }
        });
        pass_RadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    strCheckResult = "1";
                }
            }
        });
        refuse_RadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    strCheckResult = "0";
                }
            }
        });
    }

    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        switch (what) {
            case 1:
                prgDialog.closePrgDialog();
                try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                    toastMessage = getString(R.string.string_successful_leave_review);
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 0:
                try {
                    AuditLeaveBean auditLeaveBean = (AuditLeaveBean) obj;
                    name_TextView.setText(auditLeaveBean.getStrApplyName());
                    type_TextView.setText(auditLeaveBean.getStrAbsentType());
                    reason_TextView.setText(auditLeaveBean.getStrReason());
                    scanFileList = auditLeaveBean.getAffix();
                    ArrayList<ObjFileBean> objFileBeans = new ArrayList<>();
                    if (scanFileList != null && scanFileList.size() > 0) {
                        for (int i = 0; i < scanFileList.size(); i++) {
                            ObjFileBean objFileBean = scanFileList.get(i);
                            objFileBean.setStrFile_url(Utils.formatFileUrl(activity, scanFileList.get(i).getStrFile_url()));
                            objFileBean.setStrFile_Type(GIMyIntent.getMIMEType(new File(scanFileList.get(i).getStrFile_url())));
                            objFileBeans.add(objFileBean);
                            GILogUtil.d(objFileBean.getStrFile_url());
                        }
                        scanFileList = objFileBeans;
                        fileScan_MyGridView.setVisibility(View.VISIBLE);
                        scanFileGridViewAdapter = new File_GVAdapter(activity, scanFileList);
                        fileScan_MyGridView.setAdapter(scanFileGridViewAdapter);
                    } else {
                        fileScan_MyGridView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }
}
