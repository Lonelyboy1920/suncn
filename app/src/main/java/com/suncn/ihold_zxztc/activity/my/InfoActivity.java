package com.suncn.ihold_zxztc.activity.my;

import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.UserBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

/**
 * 个人信息
 */
public class InfoActivity extends BaseActivity implements View.OnClickListener {
    @BindView(id = R.id.iv_head)
    private ImageView head_ImageView; // 头像
    @BindView(id = R.id.tv_name)
    private TextView name_TextView; // 姓名
    @BindView(id = R.id.tv_code)
    private TextView code_TextView; // 届次
    //    @BindView(id = R.id.et_cname) // 姓名
//    private EditText cName_EditText;
    @BindView(id = R.id.et_circles)
    private EditText circles_EditText; // 界别
    @BindView(id = R.id.ll_circles)
    private LinearLayout circles_LinearLayout; // 界别布局
    @BindView(id = R.id.ll_clan)
    private LinearLayout clan_LinearLayout; // 党派布局
    @BindView(id = R.id.et_clan)
    private EditText clan_EditText; // 党派
    @BindView(id = R.id.ll_unit)
    private LinearLayout unit_LinearLayout;//所属单位
    @BindView(id = R.id.et_unit)
    private EditText unit_EditText;//所属单位
    @BindView(id = R.id.et_job)
    private EditText job_EditText; // 职务
    @BindView(id = R.id.tv_job_lab)
    private TextView jobLab_TextView;//职务lab
    @BindView(id = R.id.tv_mobile_lab)
    private TextView mobileLab_TextView;//手机lab
    @BindView(id = R.id.et_mobile) // 手机号码
    private EditText mobile_EditText;
    @BindView(id = R.id.ll_email)
    private LinearLayout email_LinearLayout;//电子邮箱LinearLayout
    @BindView(id = R.id.et_email) // 电子邮箱
    private EditText email_EditText;
    @BindView(id = R.id.ll_link_adress)
    private LinearLayout linkAddress_LinearLayout;//联系地址LinearLayout
    @BindView(id = R.id.et_link_adress) // 联系地址
    private EditText linkAdress_EditText;
    @BindView(id = R.id.tv_circles_label)
    private TextView circlesLabel_TextView;
    @BindView(id = R.id.et_fax)
    private EditText etFax;
    @BindView(id = R.id.et_code)
    private EditText etCode;
    @BindView(id = R.id.rg_code)
    private RadioGroup rgCode;
    @BindView(id = R.id.rb_yes)
    private RadioButton rbYes;
    @BindView(id = R.id.rb_no)
    private RadioButton rbNo;
    @BindView(id = R.id.et_tel)
    private EditText etTel;
    @BindView(id = R.id.ll_office)
    private LinearLayout llOffice;
    @BindView(id = R.id.ll_fax)
    private LinearLayout llFax;
    @BindView(id = R.id.ll_code_check)
    private LinearLayout llCode;
    @BindView(id = R.id.ll_manage2)
    private LinearLayout llManage2;
    @BindView(id = R.id.et_manage2)
    private GIEditText tvManege2;
    @BindView(id = R.id.ll_manage2_tel)
    private LinearLayout llManage2Tel;
    @BindView(id = R.id.et_manage2_tel)
    private GIEditText tvManege2Tel;
    @BindView(id = R.id.ll_unit_type)
    private LinearLayout llUnitType;
    @BindView(id = R.id.et_unit_type)
    private GIEditText etUnitType;
    private String strIsPosted = "0";

    private String mobile;
    private String linkAddr;
    private String email;
    private String fgldMobile; // 分管领导手机号
    private String fgldName; // 分管领导姓名
    private String bmfzrMobile; // 部门负责人手机号
    private String bmfzrName; // 部门负责人姓名
    private String strPublic = "0";
    private String strBank = "";//工商银行： 1 交通银行：2
    private String strCardId;//银行卡号
    private String strGwy = "1";//是否公务员 0：是；1-否

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_info);
    }

    @Override
    public void initData() {
        setStatusBar();
        super.initData();
        setHeadTitle(getString(R.string.string_personal_information));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        getInfo();
        findViewById(R.id.view_place).setVisibility(View.GONE);
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getString(R.string.string_save));
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        goto_Button.refreshFontType(activity, "2");
        rgCode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbYes.getId()) {
                    strIsPosted = "0";
                } else {
                    strIsPosted = "1";
                }
            }
        });
    }

    /**
     * 获取个人信息
     */
    private void getInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<String, String>();
        doRequestNormal(ApiManager.getInstance().getInfo(textParamMap), 0);
    }

    /**
     * 修改个人信息
     */
    private void doSave() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<String, String>();
        textParamMap.put("strMobile", mobile);
        textParamMap.put("strDuty", job_EditText.getText().toString().trim());
        textParamMap.put("strEmail", email);
        textParamMap.put("strLinkAdd", linkAdress_EditText.getText().toString().trim());
        textParamMap.put("strOpenMobile", strPublic);
        textParamMap.put("strPostalCode", etCode.getText().toString());
        textParamMap.put("strFax", etFax.getText().toString());
        textParamMap.put("strIsPosted", strIsPosted);
        textParamMap.put("strOPhone", etTel.getText().toString());
        doRequestNormal(ApiManager.getInstance().modifyInfo(textParamMap), 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto:
                checkInput();
                break;
        }
    }

    private void checkInput() {
        mobile = mobile_EditText.getText().toString();
        linkAddr = linkAdress_EditText.getText().toString();
        email = email_EditText.getText().toString();
        if (GIStringUtil.isBlank(mobile)) {
            showToast(getString(R.string.string_please_enter_the_phone_number));
            mobile_EditText.requestFocus();
        } else if (!GIStringUtil.isMobile(mobile)) {
            showToast(getString(R.string.string_please_enter_the_correct_phone_number));
            mobile_EditText.requestFocus();
        } else if (GIStringUtil.isBlank(linkAddr)) {
            showToast(getString(R.string.string_please_enter_contact_address));
            linkAdress_EditText.requestFocus();
        } else if (GIStringUtil.isNotBlank(email) && !GIStringUtil.isEmail(email)) {
            showToast(getString(R.string.string_please_enter_the_correct_email_address));
            email_EditText.requestFocus();
        } else {
            doSave();
        }
    }

    /**
     * 请求结果
     */
    private void doLogic(Object object, int sign) {
        prgDialog.closePrgDialog();
        String toastMessage = null;
        switch (sign) {
            case 0:
                try {
                    UserBean user = (UserBean) object;
                    int intLoginUserType = user.getIntLoginUserType(); // 0 委员 1机关委用户 2上报单位用户 0显示界别、党派、届次号 1显示单位的名称 2显示领导名称、领导电话部门领导名称、部门领导电话 其他字段不做控制
                    name_TextView.setText(user.getStrName());
                    code_TextView.setText(user.getStrSessionCode());
                    String strPathUrl = Utils.formatFileUrl(activity, user.getStrPathUrl());
                    if (GIStringUtil.isNotEmpty(strPathUrl)) {
                        showHeadPhoto(strPathUrl, head_ImageView);
                    }
                    switch (intLoginUserType) {
                        case 1:
                            circles_LinearLayout.setVisibility(View.GONE);
                            goto_Button.setVisibility(View.INVISIBLE);
                            code_TextView.setVisibility(View.GONE);
                            clan_LinearLayout.setVisibility(View.GONE);
                            unit_LinearLayout.setVisibility(View.VISIBLE);
                            unit_EditText.setVisibility(View.VISIBLE);
                            unit_EditText.setText(user.getStrUnitName());
                            unit_EditText.setCompoundDrawables(null, null, null, null);
                            unit_EditText.setEnabled(false);
                            unit_EditText.setHint("");
                            job_EditText.setCompoundDrawables(null, null, null, null);
                            job_EditText.setEnabled(false);
                            job_EditText.setHint("");
                            mobile_EditText.setCompoundDrawables(null, null, null, null);
                            mobile_EditText.setEnabled(false);
                            mobile_EditText.setHint("");
                            linkAdress_EditText.setCompoundDrawables(null, null, null, null);
                            linkAdress_EditText.setEnabled(false);
                            linkAdress_EditText.setHint("");
                            email_EditText.setCompoundDrawables(null, null, null, null);
                            email_EditText.setEnabled(false);
                            email_EditText.setHint("");
                            job_EditText.setText(user.getStrDuty());
                            llCode.setVisibility(View.GONE);;
                            llFax.setVisibility(View.GONE);
                            llOffice.setVisibility(View.GONE);
                            if (GIStringUtil.isBlank(user.getStrSessionCode())) {
                                goto_Button.setVisibility(View.GONE);
                            }
                            mobile_EditText.setText(user.getStrMobile());
                            break;
                        case 2:
                            goto_Button.setVisibility(View.INVISIBLE);
                            code_TextView.setVisibility(View.GONE);
                            job_EditText.setHint("");
                            circles_LinearLayout.setVisibility(View.GONE);
                            clan_LinearLayout.setVisibility(View.GONE);
                            unit_LinearLayout.setVisibility(View.VISIBLE);
                            unit_EditText.setVisibility(View.VISIBLE);
                            unit_EditText.setText(user.getStrUnitName());
                            unit_EditText.setCompoundDrawables(null, null, null, null);
                            unit_EditText.setEnabled(false);
                            unit_EditText.setHint("");
                            llManage2.setVisibility(View.VISIBLE);
                            tvManege2.setText(user.getStrManagerTwoName());
                            tvManege2.setEnabled(false);
                            llManage2Tel.setVisibility(View.VISIBLE);
                            tvManege2Tel.setText(user.getStrManagerTwoPhone());
                            tvManege2Tel.setEnabled(false);
                            jobLab_TextView.setText(getString(R.string.string_manager));
                            job_EditText.setText(user.getStrManagerOneName());
                            job_EditText.setCompoundDrawables(null, null, null, null);
                            job_EditText.setEnabled(false);
                            mobileLab_TextView.setText(getString(R.string.string_manager_mobile_phone_number));
                           // mobile_EditText.setCompoundDrawables(null, null, null, null);
                            mobile_EditText.setEnabled(false);
                            mobile_EditText.setText(user.getStrManagerOnePhone());
                            email_LinearLayout.setVisibility(View.GONE);
                            linkAddress_LinearLayout.setVisibility(View.GONE);
                            llCode.setVisibility(View.GONE);
                            llFax.setVisibility(View.GONE);
                            etTel.setText(user.getStrOPhone());
                            llUnitType.setVisibility(View.VISIBLE);
                            etUnitType.setEnabled(false);
                            etUnitType.setText(user.getStrUnitType());
                            if ("0".equals(user.getStrIsPosted())) {
                                rbYes.setChecked(true);
                            } else {
                                rbNo.setChecked(true);
                            }
                            llOffice.setVisibility(View.GONE);
                            break;
                        default:
                            circles_EditText.setText(user.getStrSector()); // 界别
                            clan_EditText.setText(user.getStrFaction()); // 党派
                            job_EditText.setText(user.getStrDuty());
                            unit_EditText.setText(user.getStrUnitName());
                            unit_EditText.setCompoundDrawables(null, null, null, null);
                            job_EditText.setCompoundDrawables(null, null, null, null);
                            mobile_EditText.setCompoundDrawables(null, null, null, null);
                            linkAdress_EditText.setCompoundDrawables(null, null, null, null);
                            email_EditText.setCompoundDrawables(null, null, null, null);
                            etCode.setText(user.getStrPostalCode());
                            etFax.setText(user.getStrFax());
                            etTel.setText(user.getStrOPhone());
                            llCode.setVisibility(View.VISIBLE);
                            mobile_EditText.setText(user.getStrMobile());
                            if ("0".equals(user.getStrIsPosted())) {
                                rbYes.setChecked(true);
                            } else {
                                rbNo.setChecked(true);
                            }
                            break;
                    }
                    linkAdress_EditText.setText(user.getStrLinkAdd());
                    email_EditText.setText(user.getStrEmail());
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 1:
                try {
                    toastMessage = getString(R.string.string_information_saved_successfully);
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (GIStringUtil.isNotEmpty(toastMessage)) {
            showToast(toastMessage);
        }
    }
}
