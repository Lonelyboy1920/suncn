package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.AlwaysMarqueeTextView;

import java.util.HashMap;

import skin.support.content.res.SkinCompatResources;

/**
 * 提案交办 社情民意交办
 */
public class Proposal_HandleActivity extends BaseActivity {
    @BindView(id = R.id.ll_type)
    private LinearLayout type_LinearLayout;//分发类型LinearLayout
    @BindView(id = R.id.ll_system)
    private LinearLayout system_LinearLayout;//承办系统LinearLayout
    @BindView(id = R.id.tv_system, click = true)
    private AlwaysMarqueeTextView system_TextView;//承办系统TextView
    @BindView(id = R.id.ll_government)
    private LinearLayout government_LinearLayout;//区县政府LinearLayout
    @BindView(id = R.id.tv_government, click = true)
    private AlwaysMarqueeTextView government_TextView;//区县政府TextView
    @BindView(id = R.id.ll_host)
    private LinearLayout host_LinearLayout;//主办单位LinearLayout
    @BindView(id = R.id.ll_unit)
    private LinearLayout unit_LinearLayout;//会办单位LinearLayout
    @BindView(id = R.id.tv_type, click = true)//分发类型TextView
    private TextView type_TextView;
    @BindView(id = R.id.tv_do_type, click = true)//办理类型TextView
    private TextView doType_TextView;
    @BindView(id = R.id.tv_host, click = true)
    private AlwaysMarqueeTextView host_TextView;//主办单位
    @BindView(id = R.id.tv_unit, click = true)
    private AlwaysMarqueeTextView unit_TextView;//会办单位
    @BindView(id = R.id.ll_do_type, click = true)
    private LinearLayout doType_LinearLayout;//办理类型LinearLayout
    private String strChooseSystem = "";//选择的承办系统
    private String strChooseHost = "";//选择的主办单位
    private String strChooseUnit = "";//选择的会办单位
    private String type;//分发类型名称
    private String doType;//办理类型
    private String host;//主办单位
    private String unit;//分办单位
    private String system;//承办系统
    private String[] menuArray;
    private String strId;
    private boolean isSocialOpinions;//社情民意
    private boolean isUnit; // 承办系统
    private String strCaseMotionId;// 提案的主键(承办系统显示)
    private boolean isPublicationHandler;//承办系统 刊物交办
    private String strPubId;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0://承办系统
                    strChooseSystem = data.getExtras().getString("strChooseUnitId");
                    system_TextView.setText(Utils.showAddress(strChooseSystem));
                    break;
                case 1://主办单位
                    strChooseHost = data.getExtras().getString("strChooseUnitId");
                    host_TextView.setText(Utils.showAddress(strChooseHost));
                    break;
                case 2://会办单位
                    strChooseUnit = data.getExtras().getString("strChooseUnitId");
                    unit_TextView.setText(Utils.showAddress(strChooseUnit));
                    break;
                case 3://区县政府
                    strChooseUnit = data.getExtras().getString("strChooseUnitId");
                    government_TextView.setText(Utils.showAddress(strChooseUnit));
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_handle);
    }

    @Override
    public void initData() {
        super.initData();
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText("确定");
        goto_Button.refreshFontType(activity, "2");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        type_LinearLayout.setVisibility(View.VISIBLE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strId = bundle.getString("strId");
            strCaseMotionId = bundle.getString("strCaseMotionId");
            strPubId = bundle.getString("strPubId");
            isSocialOpinions = bundle.getBoolean("isSocialOpinions");
            isPublicationHandler = bundle.getBoolean("isPublicationHandler");
            isUnit = bundle.getBoolean("isUnit");
        }
        setHeadTitle("提案交办");
        if (isSocialOpinions) {
            doType_LinearLayout.setVisibility(View.GONE);
            setHeadTitle("社情民意交办");
        } else if (isUnit) {
            type_LinearLayout.setVisibility(View.GONE);
            doType_LinearLayout.setVisibility(View.VISIBLE);
        } else if (isPublicationHandler) {
            setHeadTitle("刊物交办");
            type_LinearLayout.setVisibility(View.GONE);
            doType_LinearLayout.setVisibility(View.VISIBLE);
        }
        if (isSocialOpinions) {
            menuArray = new String[]{"区县政府", "承办系统"};
        } else {
            if (AppConfigUtil.isUseCbxt(activity)) {
                if (AppConfigUtil.isUseCbdw(activity)) {
                    menuArray = new String[]{"承办单位", "承办系统"};
                } else {
                    menuArray = new String[]{"承办系统"};
                }
            } else {
                if (AppConfigUtil.isUseCbdw(activity)) {
                    menuArray = new String[]{"承办单位"};
                } else {
                    menuArray = new String[]{};
                }
            }
            if (menuArray.length == 1) {
                type_TextView.setText(menuArray[0]);
                if (menuArray[0].contains("系统")) {
                    system_LinearLayout.setVisibility(View.VISIBLE);
                    host_LinearLayout.setVisibility(View.GONE);
                    unit_LinearLayout.setVisibility(View.GONE);
                    doType_LinearLayout.setVisibility(View.GONE);
                } else {
                    doType_LinearLayout.setVisibility(View.VISIBLE);
                    system_LinearLayout.setVisibility(View.GONE);
                    host_LinearLayout.setVisibility(View.VISIBLE);
                    //unit_LinearLayout.setVisibility(View.VISIBLE);
                }
            }
        }
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
                    setResult(RESULT_OK);
                    finish();
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle;
        //是否是会办
        boolean isHB = (doType_TextView.getText().toString().trim().equals("会办") || doType_TextView.getText().toString().trim().equals("主会办")) ? true : false;
        switch (v.getId()) {
            case R.id.tv_system://承办系统
                bundle = new Bundle();
                bundle.putBoolean("isSystem", true);
                bundle.putBoolean("isSingle", true);
                bundle.putString("strChooseUnitId", strChooseSystem);
                bundle.putString("headTitle", "承办系统");
                showActivity(activity, Choose_UnitActivity.class, bundle, 0);
                break;
            case R.id.tv_government://区县政府
                bundle = new Bundle();
                bundle.putBoolean("isQXZF", true);
                bundle.putBoolean("isSingle", false);
                bundle.putString("strChooseUnitId", strChooseUnit);
                bundle.putString("headTitle", "区县政府");
                showActivity(activity, Choose_UnitActivity.class, bundle, 3);
                break;

            case R.id.tv_host://主办单位
                doType = doType_TextView.getText().toString().trim();
                bundle = new Bundle();
                bundle.putString("strChooseUnitId", strChooseHost);
                bundle.putString("strHasChoose", strChooseUnit);
                bundle.putBoolean("isHB", isHB);
                bundle.putBoolean("isSystem", false);
                if (doType.equals("分办")) {
                    bundle.putBoolean("isSingle", false);
                } else {
                    bundle.putBoolean("isSingle", true);
                }
                bundle.putString("headTitle", "主办单位");
                showActivity(activity, Choose_UnitActivity.class, bundle, 1);
                break;
            case R.id.tv_unit://会办单位
                bundle = new Bundle();
                bundle.putBoolean("isHB", isHB);
                bundle.putString("strHasChoose", strChooseHost);
                bundle.putString("strChooseUnitId", strChooseUnit);
                bundle.putBoolean("isSystem", false);
                bundle.putBoolean("isSingle", false);
                bundle.putString("headTitle", "会办单位");
                showActivity(activity, Choose_UnitActivity.class, bundle, 2);
                break;
            case R.id.tv_type://分发类型
                if (isSocialOpinions) {
                    menuArray = new String[]{"承办系统", "区县政府"};
                } else {
                    if (AppConfigUtil.isUseCbxt(activity)) {
                        if (AppConfigUtil.isUseCbdw(activity)) {
                            menuArray = new String[]{"承办单位", "承办系统"};
                        } else {
                            menuArray = new String[]{"承办系统"};
                        }
                    } else {
                        if (AppConfigUtil.isUseCbdw(activity)) {
                            menuArray = new String[]{"承办单位"};
                        } else {
                            menuArray = new String[]{};
                        }
                    }
                }
                if (menuArray.length > 1)
                    showChooseDialog(menuArray, 0);
                break;
            case R.id.tv_do_type://办理类型
                if (ProjectNameUtil.isGYSZX(activity)) {
                    menuArray = new String[]{"主办", "主会办", "分办"};
                } else {
                    menuArray = new String[]{"主办", "会办", "分办"};
                }
                showChooseDialog(menuArray, 1);
                break;
            case R.id.btn_goto:
                type = type_TextView.getText().toString().trim();
                doType = doType_TextView.getText().toString().trim();
                host = host_TextView.getText().toString().trim();
                unit = unit_TextView.getText().toString().trim();
                system = system_TextView.getText().toString().trim();
                if (type_LinearLayout.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(type)) {
                    showToast("请选择分发类型");
                    type_LinearLayout.requestFocus();
                } else if (doType_LinearLayout.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(doType)) {
                    showToast("请选择办理类型");
                    doType_LinearLayout.requestFocus();
                } else if (host_LinearLayout.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(host)) {
                    showToast("请选择主办单位");
                    host_LinearLayout.requestFocus();
                } else if (unit_LinearLayout.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(unit)) {
                    showToast("请选择会办单位");
                    host_LinearLayout.requestFocus();
                } else if (system_LinearLayout.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(system)) {
                    showToast("请选择承办系统");
                    system_LinearLayout.requestFocus();
                } else {
                    doHandle();
                }
                break;
        }
    }

    /**
     * 提交交办
     */
    private void doHandle() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        if (GIStringUtil.isNotBlank(strId)) {
            textParamMap.put("strId", strId);
        }
        type = type_TextView.getText().toString().trim();
        /*String url = HttpCommonUtil.MotionFenfaServlet;
        if (isSocialOpinions) {
            url = HttpCommonUtil.PubFenfaServlet;
        } else*/
        if (isUnit) {
            //url = HttpCommonUtil.MotionDistFenfaServlet;
            textParamMap.put("strCaseMotionId", strCaseMotionId);//
            doType = doType_TextView.getText().toString().trim();
            if (doType.equals("主办")) {
                textParamMap.put("strHandlerType", "1");//1-主办，2-会办，3-分办
                textParamMap.put("strMainUnit", strChooseHost);//主办单位
            } else if (doType.equals("会办") || doType.equals("主会办")) {
                textParamMap.put("strHandlerType", "2");//1-主办，2-会办，3-分办
                textParamMap.put("strMainUnit", strChooseHost);//主办单位
                textParamMap.put("strMeetUnit", strChooseUnit);//会办单位
            } else {
                textParamMap.put("strHandlerType", "3");//1-主办，2-会办，3-分办
                textParamMap.put("strMainUnit", strChooseHost);//主办单位
            }
        } /*else if (isPublicationHandler) {
            url = HttpCommonUtil.PubDistFenfaServlet;
            textParamMap.put("strPubId", strPubId);//
            doType = doType_TextView.getText().toString().trim();
            if (doType.equals("主办")) {
                textParamMap.put("strHandlerType", "1");//1-主办，2-会办，3-分办
                textParamMap.put("strMainUnit", strChooseHost);//主办单位
            } else if (doType.equals("会办")) {
                textParamMap.put("strHandlerType", "2");//1-主办，2-会办，3-分办
                textParamMap.put("strMainUnit", strChooseHost);//主办单位
                textParamMap.put("strMeetUnit", strChooseUnit);//会办单位
            } else {
                textParamMap.put("strHandlerType", "3");//1-主办，2-会办，3-分办
                textParamMap.put("strMainUnit", strChooseHost);//主办单位
            }
        }*/
        if (type.contains("系统")) {
            textParamMap.put("intDistType", "1");//1-承办系统，2-承办单位
            textParamMap.put("strCbxtUnit", strChooseSystem);//承办系统
        } else {
            textParamMap.put("intDistType", "2");
            if (isSocialOpinions) {
                textParamMap.put("strMainUnit", strChooseUnit);
            } else {
                doType = doType_TextView.getText().toString().trim();
                if (doType.equals("主办")) {
                    textParamMap.put("strHandlerType", "1");//1-主办，2-会办，3-分办
                    textParamMap.put("strMainUnit", strChooseHost);//主办单位
                } else if (doType.equals("会办") || doType.equals("主会办")) {
                    textParamMap.put("strHandlerType", "2");//1-主办，2-会办，3-分办
                    textParamMap.put("strMainUnit", strChooseHost);//主办单位
                    textParamMap.put("strMeetUnit", strChooseUnit);//会办单位
                } else {
                    textParamMap.put("strHandlerType", "3");//1-主办，2-会办，3-分办
                    textParamMap.put("strMainUnit", strChooseHost);//主办单位
                }
            }
        }
        doRequestNormal(ApiManager.getInstance().dealProposalFenfa(textParamMap), 1);
    }

    private void showChooseDialog(final String[] menuArray, final int i) {

        final NormalListDialog normalListDialog = new NormalListDialog(activity, menuArray);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                String state = "";
                if (i == 0) {
                    state = menuArray[position];
                    type_TextView.setText(state);
                    if (isSocialOpinions) {
                        if (0 == position) {
                            system_LinearLayout.setVisibility(View.VISIBLE);
                            government_LinearLayout.setVisibility(View.GONE);
                        } else {
                            system_LinearLayout.setVisibility(View.GONE);
                            government_LinearLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (state.contains("系统")) {
                            system_LinearLayout.setVisibility(View.VISIBLE);
                            host_LinearLayout.setVisibility(View.GONE);
                            unit_LinearLayout.setVisibility(View.GONE);
                            doType_LinearLayout.setVisibility(View.GONE);
                        } else {
                            doType_LinearLayout.setVisibility(View.VISIBLE);
                            system_LinearLayout.setVisibility(View.GONE);
                            // host_LinearLayout.setVisibility(View.VISIBLE);
                            //unit_LinearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (i == 1) {
                    state = menuArray[position];
                    doType_TextView.setText(state);
                    if (state.contains("会办") || state.equals("主会办")) {
                        unit_LinearLayout.setVisibility(View.VISIBLE);
                        host_LinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        unit_LinearLayout.setVisibility(View.GONE);
                        host_LinearLayout.setVisibility(View.VISIBLE);
                    }
                    //leaveTypeId = leaveTypeBeans.get(position).getStrAbsentType();
                }
                normalListDialog.dismiss();
            }
        });
        if (i == 0) {
            normalListDialog.title("选择分发类型");
        } else if (i == 1) {
            normalListDialog.title("选择办理类型");
        }
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity,R.color.view_head_bg));
        normalListDialog.titleTextColor(Color.WHITE);
        normalListDialog.show();
    }
}
