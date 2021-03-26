package com.suncn.ihold_zxztc.activity.application.proposal;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.global.FileBrowserActivity;
import com.suncn.ihold_zxztc.bean.MotionHandlerWayBean;
import com.suncn.ihold_zxztc.bean.ParamByTypeBean;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.WrapLinearlayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import okhttp3.MultipartBody;

/**
 * 提案办理
 */
public class Proposal_DealActivity extends BaseActivity {
    @BindView(id = R.id.ll_select_file, click = true)
    private LinearLayout selectFile_Layout;//
    @BindView(id = R.id.tv_file_name)
    private TextView fileName_TextView;//
    @BindView(id = R.id.ll_way)
    private WrapLinearlayout way_LinearLayout;//协商方式LinearLayout
    @BindView(id = R.id.ll_degree)
    private LinearLayout degree_LinearLayout; // 解决程度LinearLayout
    @BindView(id = R.id.rg_degree)
    private RadioGroup degree_RadioGroup; // 解决程度
    @BindView(id = R.id.et_content)
    private GIEditText content_EditText;
    @BindView(id = R.id.ll_qualiqty)
    private LinearLayout llQualiqty;
    @BindView(id = R.id.rg_qualiqty)
    private RadioGroup rgQualiqty;
    @BindView(id = R.id.rb_normal)
    private RadioButton rbNormal;
    @BindView(id = R.id.rb_good)
    private RadioButton rbGood;

    private ArrayList<TextView> tabTextViews;
    private String strResolveLevel; // 解决程度
    private String strReplyType; // 答复方式
    private String strReplyContent;
    private String strId;
    private boolean isSocialOpinions;
    private String strHandlerTypeName;//会办不显示解决程度
    private File contentFile; // 正文内容文件
    private String strQuality = "0";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0: // 上传答复函
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            contentFile = (File) bundle.getSerializable("file");
                            if (contentFile != null) {
                                fileName_TextView.setText(contentFile.getName());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_deal);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("提案办理");
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText("确定");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        goto_Button.refreshFontType(activity, "2");
        content_EditText.setTextView(findViewById(R.id.tv_count));
        getMotionHandlerWay();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            strId = bundle.getString("strId");
            strHandlerTypeName = bundle.getString("strHandlerTypeName");
            GILogUtil.i("strHandlerTypeName========================" + strHandlerTypeName);
            isSocialOpinions = bundle.getBoolean("isSocialOpinions");
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        if (isSocialOpinions || (GIStringUtil.isNotBlank(strHandlerTypeName) && strHandlerTypeName.contains("会办"))) {
            degree_LinearLayout.setVisibility(View.GONE);
        } else {
            degree_LinearLayout.setVisibility(View.VISIBLE);
            getParamByType();
        }
        if (ProjectNameUtil.isGYSZX(activity)) {
            llQualiqty.setVisibility(View.VISIBLE);
            rgQualiqty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == rbNormal.getId()) {
                        strQuality = "0";
                    } else {
                        strQuality = "1";
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                checkSendInfo();
                break;
            case R.id.ll_select_file:
                HiPermission.create(activity).checkSinglePermission(Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionCallback() {
                    @Override
                    public void onGuarantee(String permisson, int position) { // 同意/已授权
                        showActivity(activity, FileBrowserActivity.class, 0);

                    }

                    @Override
                    public void onClose() { // 用户关闭权限申请
                        GILogUtil.e("onClose");
                    }

                    @Override
                    public void onFinish() { // 所有权限申请完成
                        GILogUtil.e("onFinish");
                    }

                    @Override
                    public void onDeny(String permisson, int position) { // 拒绝
                        GILogUtil.e("onDeny");
                    }
                });
                break;
        }
    }

    /**
     * 初始化协商方式
     *
     * @param handlerList
     */
    private void initHandlerWay(final List handlerList, String checkedWay) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabTextViews = new ArrayList<TextView>();
        CheckBox checkBox = null;
        for (int i = 0; i < handlerList.size(); i++) {
            checkBox = new CheckBox(activity);
            checkBox.setTextSize(15);
            checkBox.setButtonDrawable(null);
            checkBox.setGravity(Gravity.CENTER);
            checkBox.setTextColor(getResources().getColor(R.color.font_title));
            checkBox.setBackgroundResource(R.drawable.selector_cb_zxta);
            checkBox.setPadding(GIDensityUtil.dip2px(activity, 10), GIDensityUtil.dip2px(activity, 4), GIDensityUtil.dip2px(activity, 6), GIDensityUtil.dip2px(activity, 4));
            checkBox.setText(handlerList.get(i) + "");
            checkBox.setMinHeight(checkBox.getHeight());
            checkBox.setLayoutParams(params);
            way_LinearLayout.addView(checkBox);
            tabTextViews.add(checkBox);
            if (checkedWay.contains(checkBox.getText().toString().trim())) {
                checkBox.setChecked(true);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(true);
                    checkBox.setTextColor(getResources().getColor(R.color.view_head_bg));
                } else {
                    checkBox.setTextColor(getResources().getColor(R.color.font_title));
                    checkBox.setChecked(false);
                }
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (int i = 0; i < tabTextViews.size(); i++) {
                        CheckBox checkBox = (CheckBox) tabTextViews.get(i);
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(true);
                            checkBox.setTextColor(getResources().getColor(R.color.view_head_bg));
                        } else {
                            checkBox.setTextColor(getResources().getColor(R.color.font_title));
                            checkBox.setChecked(false);
                        }
                    }
                }
            });
        }
    }

    /**
     * 解决程度
     */
    private void initDegree(List<ParamByTypeBean.Param> objList) {
        for (int i = 0; i < objList.size(); i++) {
            final String strName = objList.get(i).getStrName();
            RadioButton radioButton = new RadioButton(this);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            radioButton.setLayoutParams(layoutParams);
            radioButton.setText(strName);
            radioButton.setTextSize(15);
            radioButton.setButtonTintList(getResources().getColorStateList(R.color.radiobutton_selector));
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setPadding(10, 10, 10, 10);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strResolveLevel = strName;
                }
            });
            degree_RadioGroup.addView(radioButton);
        }
    }

    private void doLogic(int sign, Object object) {
        String toastMessage = null;
        switch (sign) {
            case -1:
                try {
                    prgDialog.closePrgDialog();
                    MotionHandlerWayBean motionHandlerWayBean = (MotionHandlerWayBean) object;
                    List handlerList = motionHandlerWayBean.getObjList();
                    initHandlerWay(handlerList, "");
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case -2:
                ParamByTypeBean paramByTypeBean = (ParamByTypeBean) object;
                initDegree(paramByTypeBean.getObjList());
                break;
            case 1:
                setResult(RESULT_OK);
                finish();
                break;
        }
        if (null != toastMessage) {
            showToast(toastMessage);
        }
    }

    /**
     * 检查参数
     */
    private void checkSendInfo() {
        StringBuffer buf = new StringBuffer();
        if (tabTextViews != null && tabTextViews.size() > 0) {
            for (int j = 0; j < tabTextViews.size(); j++) {
                CheckBox checkBox = (CheckBox) tabTextViews.get(j);
                if (checkBox.isChecked()) {
                    buf.append(checkBox.getText().toString() + ",");
                    // strConsign = strConsign + checkBox.getText().toString();
                }
            }
            strReplyType = buf.toString();
        }
        strReplyContent = content_EditText.getText().toString().trim();
        if (GIStringUtil.isBlank(strReplyType)) {
            showToast("请选择答复方式");
        } else if (!isSocialOpinions && (!strHandlerTypeName.contains("会办")) && GIStringUtil.isBlank(strResolveLevel)) {
            showToast("请选择解决程度");
        } else if (GIStringUtil.isBlank(strReplyContent)) {
            showToast(content_EditText.getHint().toString());
        } else {
            sendHandlerInfo();
        }
    }

    /**
     * 获取协商方式
     */
    private void getMotionHandlerWay() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getHandlerWayOptions(textParamMap), -1);
    }

    /**
     * 获取解决程度
     */
    private void getParamByType() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "1");
        doRequestNormal(ApiManager.getInstance().getParamByType(textParamMap), -2);
    }

    /**
     * 发送办理回复
     */
    private void sendHandlerInfo() {
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        textParamMap.put("strReplyType", GIStringUtil.catLastDot(strReplyType));
        textParamMap.put("strReplyContent", strReplyContent);
        if (ProjectNameUtil.isGYSZX(activity)) {
            textParamMap.put("strQuality", strQuality);
        }
        String url = "";
        if (isSocialOpinions) {
            doRequestNormal(ApiManager.getInstance().dealOinionHander(textParamMap), 1);
        } else {
            if (GIStringUtil.isNotBlank(strResolveLevel))
                textParamMap.put("strResolveLevel", strResolveLevel);
            List<File> fileParamList = new ArrayList<>();
            if (contentFile != null) {
                fileParamList.add(contentFile);
            }
            List<MultipartBody.Part> partList = Utils.filesToMultipartBodyParts(fileParamList);
            doRequestNormal(ApiManager.getInstance().dealProposalHanderWithFile(partList, textParamMap), 1);
        }
    }
}
