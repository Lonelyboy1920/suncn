package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.message.Contact_MainActivity;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.MotionHandlerWayBean;
import com.suncn.ihold_zxztc.bean.OtherOptionBean;
import com.suncn.ihold_zxztc.bean.SimilarProposalListBean;
import com.suncn.ihold_zxztc.bean.SumbitInfoBean;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MenuItemEditLayout;
import com.suncn.ihold_zxztc.view.WrapLinearlayout;
import com.suncn.ihold_zxztc.view.dialog.TipsDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skin.support.content.res.SkinCompatResources;

/**
 * @author :Sea
 * Date :2020-6-9 14:42
 * PackageName:com.suncn.ihold_zxztc.activity.application.proposal
 * Desc:  提交提案 V5.0
 */
public class NewProposalAddActivity extends BaseActivity {
    @BindView(id = R.id.tv_other)
    private TextView other_TextView;//其他选项TextView
    @BindView(id = R.id.ll_process)
    private LinearLayout process_LinearLayout;//调研过程LinearLayout
    @BindView(id = R.id.et_process)
    private GIEditText process_EditText;//调研过程EditText
    @BindView(id = R.id.ll_dynamic_one)
    private LinearLayout llDynamicOne;//动态名称一(提案内容)LinearLayout
    @BindView(id = R.id.tv_label_one)
    private TextView tvLabelOne; //动态名称一(情况分析)TextView
    @BindView(id = R.id.et_content_one)
    private GIEditText etContentOne; //动态名称一(情况分析)EditText
    @BindView(id = R.id.ll_dynamic_two)
    private LinearLayout llDynamicTwo; //动态名称二(具体建议)LinearLayout
    @BindView(id = R.id.tv_label_two)
    private TextView tvLabelTwo; //动态名称一(具体建议)TextView
    @BindView(id = R.id.et_content_two)
    private GIEditText etContentTwo; //动态名称一(具体建议)EditText
    @BindView(id = R.id.tv_way_tag)
    private TextView wayTag_TextView;//协商方式tag TextView
    @BindView(id = R.id.ll_way)
    private WrapLinearlayout way_LinearLayout;//协商方式LinearLayout
    @BindView(id = R.id.ll_other_otions)
    private WrapLinearlayout otherOptions_WrapLinearlayout;//其他选项WrapLinearlayout
    @BindView(id = R.id.et_title)
    private GIEditText title_EditText; // 案由
    @BindView(id = R.id.cb_meeting)
    private CheckBox meeting_CheckBox; // 会中提案
    @BindView(id = R.id.rb_yes)
    private RadioButton yes_RadioButton;//附议是
    @BindView(id = R.id.tv_mainwrite, click = true)
    private MenuItemEditLayout tvMainWrite;//主笔人
    @BindView(id = R.id.tv_unit, click = true)
    private MenuItemEditLayout unit_TextView;
    @BindView(id = R.id.tv_jointMem, click = true)
    private MenuItemEditLayout jointMem_TextView; // 联名委员
    @BindView(id = R.id.tv_mike, click = true)
    private TextView mike_TextView; // 语音识别按钮
    @BindView(id = R.id.tv_mike1, click = true)
    private TextView mike1_TextView; // 语音识别按钮
    @BindView(id = R.id.tv_mike2, click = true)
    private TextView mike2_TextView; // 语音识别按钮(调研过程)
    @BindView(id = R.id.ll_support)
    private LinearLayout llSupport;

    @BindView(id = R.id.tv_unit_main, click = true)
    private MenuItemEditLayout tvUnitMain; // 建议主办单位
    @BindView(id = R.id.tv_unit_help)
    private MenuItemEditLayout tvUnitHelp; // 建议会办单位

    private String strOriginCase; // 案由
    private String strConsign = ""; // 协商方式
    private String strReason; // 情况分析
    private String strWay; // 具体建议
    private String strProcess; // 调研过程
    private String strChooseValue = ""; // 选中的委员、结构
    private String strChooseValueId = ""; // 选中的委员Id、机构Id
    private String strChooseUnitId = ""; // 承办单位
    private String strChooseUnitMain = "";//主办单位
    private String strChooseUnitHelp = "";//会办单位
    private String strWriterId = "";//主笔人id(江门)
    private String strId;//草稿箱提案id
    private int strJointlyMemCount;
    private HashMap<String, String> myValueMap;
    private ArrayList<TextView> tabTextViews;
    private ArrayList<TextView> OtherOptionsTextViews;

    private PopupWindow optionWindow;
    private TipsDialog mTipsDialog;
    private List<MotionHandlerWayBean.ObjListBean> handlerList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = new Bundle();
            if (data != null) {
                bundle = data.getExtras();
            }
            switch (requestCode) {
                case 0://联名人
                    if (bundle != null) {
                        strChooseValue = bundle.getString("strChooseValue");
                        strChooseValueId = bundle.getString("strChooseValueId");
                        jointMem_TextView.setValue(Utils.getShowAddress(strChooseValueId, false));
                    }
                    break;
                case 4://主笔人
                    if (bundle != null) {
                        strWriterId = bundle.getString("strChooseValue");
                        tvMainWrite.setValue(Utils.getShowAddress(strWriterId, false));
                    }
                    break;
                case 1://承办单位
                    if (bundle != null) {
                        strChooseUnitId = bundle.getString("strChooseUnitId");
                        unit_TextView.setValue(Utils.showAddress(strChooseUnitId));
                    }
                    break;
                case 2:
                    if (bundle != null) {
                        if (!bundle.getBoolean("isBack", false)) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                    break;
                case 5://主办单位
                    if (bundle != null) {
                        strChooseUnitMain = bundle.getString("strChooseUnitId");
                        tvUnitMain.setValue(Utils.showAddress(strChooseUnitMain));
                    }
                    break;
                case 6://会办单位
                    if (bundle != null) {
                        strChooseUnitHelp = bundle.getString("strChooseUnitId");
                        tvUnitHelp.setValue(Utils.showAddress(strChooseUnitHelp));
                    }
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
        setStatusBar();
        setContentView(R.layout.activity_proposal_add_new);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_proposal_submit));
        findViewById(R.id.view_place).setVisibility(View.GONE);

        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };

        // 是否显示协商方式
        if (AppConfigUtil.isShowXsfs(activity)) {
            wayTag_TextView.setVisibility(View.VISIBLE);
            way_LinearLayout.setVisibility(View.VISIBLE);
        } else {
            wayTag_TextView.setVisibility(View.GONE);
            way_LinearLayout.setVisibility(View.GONE);
        }
        // 是否启用附议提案选项
        if (AppConfigUtil.isUseSupportMotion(activity)) {
            llSupport.setVisibility(View.VISIBLE);
        } else {
            llSupport.setVisibility(View.GONE);
        }
        // 是否显示建议承办单位
        if (AppConfigUtil.isShowTjcbdw(activity)) {
            if (ProjectNameUtil.isGZSZX(activity)) { //贵州政协使用主会办单位，不用承办单位字段
                tvUnitMain.setVisibility(View.VISIBLE);
                tvUnitHelp.setVisibility(View.VISIBLE);
                unit_TextView.setVisibility(View.GONE);
            } else {
                tvUnitMain.setVisibility(View.GONE);
                tvUnitHelp.setVisibility(View.GONE);
                unit_TextView.setVisibility(View.VISIBLE);
            }
        } else {
            tvUnitMain.setVisibility(View.GONE);
            tvUnitHelp.setVisibility(View.GONE);
            unit_TextView.setVisibility(View.GONE);
        }
        //江门政协如果是团体用户需要主笔人字段
        if (ProjectNameUtil.isJMSZX(activity) && GISharedPreUtil.getInt(activity, "intGroup") == 1) {
            tvMainWrite.setVisibility(View.VISIBLE);
        } else {
            tvMainWrite.setVisibility(View.GONE);
        }
        //做滁州市政协时增加。strMotionTitle（登录接口返回）
        String strMotionTitle = GISharedPreUtil.getString(activity, "strMotionTitle");
        if (GIStringUtil.isBlank(strMotionTitle)) {
            title_EditText.setHint(R.string.string_please_enter_proposal_title);
        } else {
            title_EditText.setHint(getString(R.string.string_please_enter_proposal) + strMotionTitle);
        }
        tvLabelOne.setText(GISharedPreUtil.getString(activity, "strContentTitle1"));//情况分析-名称动态
        etContentOne.setHint(getString(R.string.string_please_input) + tvLabelOne.getText().toString());
        if (GISharedPreUtil.getString(activity, "strContentType").equals("2")) {//返回数字代表几段
            llDynamicTwo.setVisibility(View.VISIBLE);
            tvLabelTwo.setText(GISharedPreUtil.getString(activity, "strContentTitle2"));//具体建议-名称动态
            etContentTwo.setHint(getString(R.string.string_please_input) + tvLabelTwo.getText().toString());
        } else {
            llDynamicTwo.setVisibility(View.GONE);
        }
        process_EditText.setTextView(findViewById(R.id.tv_process_count));


//        if (ProjectNameUtil.isLWQZX(activity)) {//荔湾区政协需要暂存操作
//            goto_Button.setText("操作");
//            analysisCount_TextView.setVisibility(View.GONE);
//            suggestCount_TextView.setVisibility(View.GONE);
//
//            if (null == mTipsDialog) {
//                mTipsDialog = new TipsDialog(activity);
//            }
//            mTipsDialog.setTipsContent("情况分析和具体建议总字数不能超过1500字，否则无法提交");
//            mTipsDialog.show();
//        } else {
        goto_Button.setText(getString(R.string.string_operation));
//        }
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.refreshFontType(activity, "2");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setHeadTitle(bundle.getString("headTitle", getString(R.string.string_proposal_submit)));
            strId = bundle.getString("strId");
            if (GIStringUtil.isNotBlank(strId)) {
                getSumbitInfo(strId);
            } else {
                List handlerList = (List) bundle.getSerializable("handlerList");
                if (way_LinearLayout.getVisibility() == View.VISIBLE) {
                    initHandlerWay(handlerList, "");
                }
            }
        }

//        if (GIStringUtil.isBlank(strId)) {
        getMotionHandlerWay();
//        }
        getOtheroptions();
    }

    @Override
    public void setListener() {
        super.setListener();
        title_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.contains("\n")) {
                    text = text.replace("\n", "");
                    title_EditText.setText(text);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tv_mike:
                new MikeEditTextUtil(activity, etContentOne).viewShow();
                break;
            case R.id.tv_mike1:
                new MikeEditTextUtil(activity, etContentTwo).viewShow();
                break;
            case R.id.tv_mike2:
                new MikeEditTextUtil(activity, process_EditText).viewShow();
                break;
            case R.id.btn_goto:
                if (goto_Button.getText().toString().equals(getString(R.string.string_operation))) {
                    initRecInOptionPopWindow(); // 提交 or暂存
                } else {
                    sendOrSave(1);
                }
                break;
            case R.id.tv_unit: // 选择承办单位
                bundle = new Bundle();
                bundle.putString("strChooseUnitId", strChooseUnitId);
                bundle.putString("headTitle", getString(R.string.string_undertaker));
                showActivity(activity, Choose_UnitActivity.class, bundle, 1);
                break;
            case R.id.tv_unit_main: // 选择主办单位
                bundle = new Bundle();
                bundle.putString("strChooseUnitId", strChooseUnitMain);
                bundle.putString("headTitle", getString(R.string.string_suggested_organizer));
                bundle.putBoolean("isSingle", true);
                showActivity(activity, Choose_UnitActivity.class, bundle, 5);
                break;
            case R.id.tv_unit_help: // 选择会办单位
                bundle = new Bundle();
                bundle.putString("strChooseUnitId", strChooseUnitHelp);
                bundle.putInt("mustSelectCount", 4);
                bundle.putString("headTitle", getString(R.string.string_suggested_joint_organization));
                showActivity(activity, Choose_UnitActivity.class, bundle, 6);
                break;
            case R.id.tv_jointMem: // 选择联名人
                bundle = new Bundle();
                bundle.putString("strChooseValue", strChooseValue);
                bundle.putString("strChooseValueId", strChooseValueId);
                bundle.putInt("strJointlyMemCount", strJointlyMemCount);
                GILogUtil.e("sssss", strJointlyMemCount + "");
                showActivity(activity, Choose_JoinMemActivity.class, bundle, 0);
                break;
            case R.id.tv_mainwrite: // 选择主笔人
                bundle = new Bundle();
                bundle.putString("strChooseValue", strWriterId);
                bundle.putBoolean("isChoose", true);
                bundle.putBoolean("isShowHead", false);
                bundle.putBoolean("isSingle", true);
                bundle.putString("showTitle", getString(R.string.string_select_lead_author));
                bundle.putBoolean("isOnlyWY", true);
                bundle.putInt("sign", 11);
                showActivity(activity, Contact_MainActivity.class, bundle, 4);
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    /**
     * 操作框
     */
    private void initRecInOptionPopWindow() {
        if (optionWindow == null) {
            View v = getLayoutInflater().inflate(R.layout.view_popwindow_list, null);
            Button cancel_Button = (Button) v.findViewById(R.id.btn_cancel);
            Button send_Button = (Button) v.findViewById(R.id.btn_send);
            LinearLayout delete_LinearLayout = (LinearLayout) v.findViewById(R.id.ll_delete);
            LinearLayout submit_LinearLayout = (LinearLayout) v.findViewById(R.id.ll_submit);
            if (GIStringUtil.isNotBlank(strId)) {
                delete_LinearLayout.setVisibility(View.VISIBLE);
            } else {
                delete_LinearLayout.setVisibility(View.GONE);
            }
            /**
             * 提交提案
             */
            send_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendOrSave(1);
                    optionWindow.dismiss();
                }
            });
            /**
             * 暂存提案
             */
            Button save_Button = (Button) v.findViewById(R.id.btn_save);
            save_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendOrSave(0);
                    optionWindow.dismiss();
                }
            });
            Button delete_Button = (Button) v.findViewById(R.id.btn_delete);
            delete_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmDialog();
                    //deleteInfo();
                    optionWindow.dismiss();
                }
            });
            cancel_Button.setOnClickListener(cancelBtn_clickListener); // 取消按钮
            optionWindow = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            optionWindow.setFocusable(true);
            optionWindow.setBackgroundDrawable(new BitmapDrawable());// 点击窗口外消失
            optionWindow.setOutsideTouchable(true);
            optionWindow.setOnDismissListener(new popWindowDismissListener()); //添加pop窗口关闭事件
            optionWindow.setAnimationStyle(R.style.AnimationPreview);
        }
        backgroundAlpha(0.5f);
        optionWindow.showAsDropDown(goto_Button);
    }

    /**
     * 对话框 删除
     */
    private void showConfirmDialog() {
        String title = "";
        title = getString(R.string.string_is_del_this_proposal);
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.string_tips)).content(title).btnText(getString(R.string.string_cancle), getString(R.string.string_determine)).showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        //dialog.dismiss();
                        dialog.superDismiss();
                        deleteInfo();

                    }
                }
        );
    }

    /**
     * 点击取消
     */
    View.OnClickListener cancelBtn_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (optionWindow != null && optionWindow.isShowing()) {
                optionWindow.dismiss();
            }
        }
    };

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     */
    class popWindowDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    /**
     * 提案提交、暂存（1-提交，0-暂存）
     */
    private void sendOrSave(int i) {
        strOriginCase = title_EditText.getText().toString();
        strReason = etContentOne.getText().toString();
        strWay = etContentTwo.getText().toString();
        strProcess = process_EditText.getText().toString();
        if (GIStringUtil.isBlank(strOriginCase)) {
            showToast(title_EditText.getHint().toString());
            title_EditText.requestFocus();
        } else if (GIStringUtil.isBlank(strReason)) {
            showToast(etContentOne.getHint().toString());
            etContentOne.requestFocus();
        } else if ((llDynamicTwo.getVisibility() == View.VISIBLE) && GIStringUtil.isBlank(strWay)) {
            showToast(etContentTwo.getHint().toString());
            etContentTwo.requestFocus();
        } else if (process_LinearLayout.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(strProcess)) {
            showToast(process_EditText.getHint().toString());
            process_EditText.requestFocus();
        } else if (tvMainWrite.getVisibility() == View.VISIBLE && GIStringUtil.isBlank(strWriterId)) {
            showToast(getString(R.string.string_select_lead_author));
        } else if (ProjectNameUtil.isLWQZX(activity) && (strReason.length() + strWay.length()) > 1500) {
            if (null == mTipsDialog) {
                mTipsDialog = new TipsDialog(activity);
            }
            mTipsDialog.setTipsContent("情况分析和具体建议总字数已超过限制字数，请重新输入");
            mTipsDialog.show();
        } else {
            doZxtaAdd(i);
            //showConfirmDialog(i);
        }
    }

    /**
     * 获取协商方式
     */
    private void getMotionHandlerWay() {
        textParamMap = new HashMap<>();
        if (GIStringUtil.isNotBlank(strId))
            textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().getProposalHandleWayServlet(textParamMap), 4);
    }

    /**
     * 获取其他选项
     */
    private void getOtheroptions() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        if (GIStringUtil.isNotBlank(strId))
            textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().getProposalConfigServlet(textParamMap), 3);
    }

    /**
     * 获取草稿箱再次编辑的信息
     */
    private void getSumbitInfo(String strId) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().getProposalAddViewServlet(textParamMap), 1);
    }

    /**
     * 删除草稿箱提案
     */
    private void deleteInfo() {
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().deleteProposalDeleteServlet(textParamMap), 2);
    }

    private int strstate;

    /**
     * 提案提交、暂存（1-提交，0-暂存）
     */
    private void doZxtaAdd(int i) {
        strstate = i;
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strTitle", strOriginCase); //案由
        if (yes_RadioButton.isChecked()) {
            textParamMap.put("intSupportMotion", "1"); //允许附议附议 1是 0否
        } else {
            textParamMap.put("intSupportMotion", "0"); //允许附议附议 1是 0否
        }
        if (GIStringUtil.isNotBlank(strChooseUnitId)) {
            textParamMap.put("strSuggestUnitId", GIStringUtil.nullToEmpty(strChooseUnitId.length() > 0 ? strChooseUnitId.substring(0, strChooseUnitId.length() - 1) : strChooseUnitId)); //建议承办单位
        }
        if (GIStringUtil.isNotBlank(strChooseValueId)) {
            textParamMap.put("strJointlyMem", GIStringUtil.nullToEmpty(strChooseValueId.length() > 0 ? strChooseValueId.substring(0, strChooseValueId.length() - 1) : strChooseValueId)); //联名委员        (id/名字/手机号码)
        }
        if (myValueMap != null) {
            for (Map.Entry<String, String> entry : myValueMap.entrySet()) {
                textParamMap.put(entry.getKey(), entry.getValue());
            }
        }

        if (process_LinearLayout.getVisibility() == View.VISIBLE) {
            textParamMap.put("strSurveyProcess", strProcess); //调研过程
        }
        if (tabTextViews != null && tabTextViews.size() > 0) {
            StringBuffer buf = new StringBuffer();
            for (int j = 0; j < tabTextViews.size(); j++) {
                CheckBox checkBox = (CheckBox) tabTextViews.get(j);
                if (checkBox.isChecked()) {
                    buf.append(handlerList.get(j).getStrCode() + ",");
                }
            }
            if (buf.length() > 0) {
                //方法一  : substring
                strConsign = buf.substring(0, buf.length() - 1);
            }
        }
        if (GIStringUtil.isNotBlank(strConsign))
            textParamMap.put("strTalkHandleType", strConsign); //协商方式
        textParamMap.put("strReason", strReason);
        if (llDynamicTwo.getVisibility() == View.VISIBLE) {
            textParamMap.put("strWay", strWay); //具体建议
        }
        textParamMap.put("intState", i + ""); //0为暂存 1为上报

        if (GIStringUtil.isNotBlank(strId)) {//StrId不为空表示从草稿箱过来
            textParamMap.put("strId", strId);
        }
        if (GIStringUtil.isNotBlank(strWriterId)) {
            textParamMap.put("strWriterId", strWriterId.replace(",", ""));
        }
        if (ProjectNameUtil.isGZSZX(activity)) {
            if (GIStringUtil.isNotBlank(strChooseUnitMain)) {
                textParamMap.put("strMainSuggestUnitId", strChooseUnitMain);
            }
            if (GIStringUtil.isNotBlank(strChooseUnitHelp)) {
                textParamMap.put("strSuggestUnitId", strChooseUnitHelp);
            }
        }
        doRequestNormal(ApiManager.getInstance().getProposalAddServlet(textParamMap), -1);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        switch (what) {
            case 4:
                try {
                    MotionHandlerWayBean motionHandlerWayBean = (MotionHandlerWayBean) obj;
                    handlerList = motionHandlerWayBean.getObjList();
                    initHandlerWay(handlerList, "");
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 3:
                prgDialog.closePrgDialog();
                try {
                    OtherOptionBean otherOptionBean = (OtherOptionBean) obj;
                    List<OtherOptionBean.OtherOption> options = otherOptionBean.getObjOtherList();
                    List<OtherOptionBean.OtherOption> objContentList = otherOptionBean.getObjContentList();
                    List<OtherOptionBean.OtherOption> objList = otherOptionBean.getObjList();
                    initOptions(objList);
                    if (options != null && options.size() > 0) {
                        other_TextView.setVisibility(View.VISIBLE);
                        otherOptions_WrapLinearlayout.setVisibility(View.VISIBLE);
                        initOtherOptions(options);
                    } else {
                        other_TextView.setVisibility(View.GONE);
                        otherOptions_WrapLinearlayout.setVisibility(View.GONE);
                    }
                    initContentOptions(objContentList);

                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case -1://提案相似
                prgDialog.closePrgDialog();
                try {
                    SimilarProposalListBean similarProposalListBean = (SimilarProposalListBean) obj;
                    ArrayList<SimilarProposalListBean.SimilarProposal> similarProposals = similarProposalListBean.getObjList();
                    if (similarProposals != null && similarProposals.size() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("strOriginCase", strOriginCase);
                        bundle.putString("strChooseUnitId", strChooseUnitId);
                        bundle.putString("strChooseValueId", strChooseValueId);
                        bundle.putString("strConsign", strConsign);
                        bundle.putString("strReason", strReason);
                        bundle.putString("strWay", strWay);
                        bundle.putString("StrId", strId);
                        bundle.putString("strDiaoyanguocheng", strProcess);
                           /* for (Map.Entry<String, String> entry : myValueMap.entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }*/
                        bundle.putSerializable("myValueMap", myValueMap);
                        //bundle.putParcelable("myValueMap", (Parcelable) myValueMap);
                        bundle.putSerializable("objList", similarProposals);
                        bundle.putString("strCheckMsg", similarProposalListBean.getStrCheckMsg());
                        bundle.putBoolean("isSaveData", similarProposalListBean.isSaveData());
                        showActivity(activity, SimilarProposalsActivity.class, bundle, 2);
                    } else {
                        if (strstate == 1) {
                            toastMessage = getString(R.string.string_operation_success);
                        } else {
                            toastMessage = getString(R.string.string_proposal_save_success);
                        }
                        setResult(RESULT_OK);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            case 0:
            case 2:
                prgDialog.closePrgDialog();
                BaseGlobal baseGlobal = (BaseGlobal) obj;
                if (what == 0) {
                    toastMessage = getString(R.string.string_proposal_save_success);
                } else {
                    toastMessage = getString(R.string.string_proposal_del_success);
                }
                setResult(RESULT_OK);
                finish();
                break;
            case 1:
                prgDialog.closePrgDialog();
                try {
                    SumbitInfoBean sumbitInfoBean = (SumbitInfoBean) obj;
                    title_EditText.setText(sumbitInfoBean.getStrOriginCase());
                    unit_TextView.setValue(sumbitInfoBean.getStrSuggestUnitName());
                    strChooseUnitId = sumbitInfoBean.getStrSuggestUnitId();
                    jointMem_TextView.setValue(sumbitInfoBean.getStrJointlyMemName());
                    strChooseValue = sumbitInfoBean.getStrJointlyMemName();
                    strChooseValueId = sumbitInfoBean.getStrJointlyMem();
                    String strDiaoyanguocheng = sumbitInfoBean.getStrSurveyProcess();
                    if (GIStringUtil.isNotBlank(strDiaoyanguocheng)) {
                        strDiaoyanguocheng = strDiaoyanguocheng.replaceAll(" ", "&nbsp;");
                        process_EditText.setText(GIUtil.showHtmlInfo(strDiaoyanguocheng));
                    }
                    String Consultative_way = GIStringUtil.nullToEmpty(sumbitInfoBean.getStrTalkHandleType());

                    List strName = new ArrayList();
                    List<SumbitInfoBean.HandlerWay> handlerWay = sumbitInfoBean.getObjList();
                    if (handlerWay != null && handlerWay.size() > 0) {
                        for (int i = 0; i < handlerWay.size(); i++) {
                            strName.add(handlerWay.get(i).getStrName());
                        }
                        initHandlerWay(strName, Consultative_way);
                    }
                    String strReason = sumbitInfoBean.getStrReason();
                    if (GIStringUtil.isNotBlank(strReason)) {
                        strReason = strReason.replaceAll(" ", "&nbsp;");
                        etContentOne.setText(GIUtil.showHtmlInfo(strReason));
                    }
                    String strWay = sumbitInfoBean.getStrWay();
                    if (GIStringUtil.isNotBlank(strWay)) {
                        strWay = strWay.replaceAll(" ", "&nbsp;");
                        etContentTwo.setText(GIUtil.showHtmlInfo(strWay));
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

    /**
     * 初始化 建议承办单位  办理的协商方式  联名人
     *
     * @param objList
     */
    private void initOptions(List<OtherOptionBean.OtherOption> objList) {
        for (int i = 0; i < objList.size(); i++) {
            if ("strSuggestUnitId".equals(objList.get(i).getStrKey())) {
                unit_TextView.setVisibility(View.VISIBLE);
                unit_TextView.setLabel(objList.get(i).getStrName());
            } else if ("strTalkHandleType".equals(objList.get(i).getStrKey())) {
                wayTag_TextView.setVisibility(View.VISIBLE);
                way_LinearLayout.setVisibility(View.VISIBLE);
                wayTag_TextView.setText(objList.get(i).getStrName());
            } else if ("strJointlyMem".equals(objList.get(i).getStrKey())) {
                jointMem_TextView.setLabel(objList.get(i).getStrName());
                strJointlyMemCount = objList.get(i).getIntCount();
            }
        }
    }

    /**
     * 初始化 提案内容 和情况分析
     *
     * @param objContentList
     */
    private void initContentOptions(List<OtherOptionBean.OtherOption> objContentList) {
        if (objContentList != null && objContentList.size() > 0) {
            for (int i = 0; i < objContentList.size(); i++) {
                if ("strReason".equals(objContentList.get(i).getStrKey())) {
                    llDynamicOne.setVisibility(View.VISIBLE);
                    tvLabelOne.setText(objContentList.get(i).getStrName());
                    etContentOne.setHint(getString(R.string.string_please_input) + tvLabelOne.getText().toString());
                    etContentOne.setMaxLength(objContentList.get(i).getIntCount());
                    etContentOne.setTextView(findViewById(R.id.tv_label_one_count));
                    etContentOne.setText(etContentOne.getText());
                } else if ("strWay".equals(objContentList.get(i).getStrKey())) {
                    llDynamicTwo.setVisibility(View.VISIBLE);
                    tvLabelTwo.setText(objContentList.get(i).getStrName());
                    etContentTwo.setHint(getString(R.string.string_please_input) + tvLabelTwo.getText().toString());
                    etContentTwo.setMaxLength(objContentList.get(i).getIntCount());
                    etContentTwo.setTextView(findViewById(R.id.tv_label_two_count));
                    etContentTwo.setText(etContentTwo.getText());

                }
            }

        } else {
            llDynamicOne.setVisibility(View.GONE);
            llDynamicTwo.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化协商方式
     */
    private void initHandlerWay(final List<MotionHandlerWayBean.ObjListBean> handlerList, String checkedWay) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabTextViews = new ArrayList<TextView>();
        CheckBox checkBox = null;
        for (int i = 0; i < handlerList.size(); i++) {
            checkBox = new CheckBox(activity);
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            checkBox.setButtonDrawable(null);
            checkBox.setGravity(Gravity.CENTER);
            checkBox.setTextColor(getResources().getColor(R.color.font_title));
            checkBox.setBackground(SkinCompatResources.getDrawable(activity, R.drawable.selector_cb_zxta));
            checkBox.setPadding(GIDensityUtil.dip2px(activity, 10), GIDensityUtil.dip2px(activity, 4), GIDensityUtil.dip2px(activity, 6), GIDensityUtil.dip2px(activity, 4));
            checkBox.setText(handlerList.get(i).getStrName() + "");
            checkBox.setMinHeight(checkBox.getHeight());
            checkBox.setLayoutParams(params);
            way_LinearLayout.addView(checkBox);
            tabTextViews.add(checkBox);
            if ("1".equals(handlerList.get(i).getChecked())) {
                checkBox.setChecked(true);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(true);
                    checkBox.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
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
                            checkBox.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
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
     * 初始化其他选项
     */
    private void initOtherOptions(List<OtherOptionBean.OtherOption> options) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        OtherOptionsTextViews = new ArrayList<TextView>();
        myValueMap = new HashMap<>();
        CheckBox checkBox = null;
        for (int i = 0; i < options.size(); i++) {
            checkBox = new CheckBox(activity);
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            checkBox.setButtonDrawable(null);
            checkBox.setGravity(Gravity.CENTER);
            checkBox.setTextColor(getResources().getColor(R.color.font_title));
            checkBox.setBackground(SkinCompatResources.getDrawable(activity, R.drawable.selector_cb_zxta));
            checkBox.setPadding(GIDensityUtil.dip2px(activity, 10), GIDensityUtil.dip2px(activity, 4), GIDensityUtil.dip2px(activity, 6), GIDensityUtil.dip2px(activity, 4));
            checkBox.setText(options.get(i).getStrName() + "");
            checkBox.setTag(options.get(i).getStrKey() + "," + options.get(i).getStrValue());
            checkBox.setMinHeight(checkBox.getHeight());
            checkBox.setLayoutParams(params);
            otherOptions_WrapLinearlayout.addView(checkBox);
            OtherOptionsTextViews.add(checkBox);
            for (int j = 0; j < OtherOptionsTextViews.size(); j++) {
                checkBox = (CheckBox) OtherOptionsTextViews.get(i);
                String[] tag = checkBox.getTag().toString().split(",");
                if (tag[1].equals("1")) {
                    checkBox.setChecked(true);
                    checkBox.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                    if (tag[0].equals("strSurvey")) {
                        process_LinearLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    checkBox.setTextColor(getResources().getColor(R.color.font_title));
                    checkBox.setChecked(false);
                }
                myValueMap.put(tag[0], tag[1]);
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String[] tag = new String[]{};
                    for (int i = 0; i < OtherOptionsTextViews.size(); i++) {
                        CheckBox checkBox = (CheckBox) OtherOptionsTextViews.get(i);
                        tag = checkBox.getTag().toString().split(",");
                        if (checkBox.isChecked()) {
                            checkBox.setTag(tag[0] + "," + "1");
                            checkBox.setChecked(true);
                            checkBox.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                        } else {
                            checkBox.setTag(tag[0] + "," + "0");
                            checkBox.setTextColor(getResources().getColor(R.color.font_title));
                            checkBox.setChecked(false);
                        }
                        tag = checkBox.getTag().toString().split(",");

                        myValueMap.put(tag[0], tag[1]);
                    }

//                    if (AppConfigUtil.isShowDygc(activity)) {
                    for (Map.Entry<String, String> entry : myValueMap.entrySet()) {
                        if (entry.getKey().equals("strSurvey")) {
                            if (entry.getValue().equals("1")) {
                                process_LinearLayout.setVisibility(View.VISIBLE);
                            } else {
                                process_LinearLayout.setVisibility(View.GONE);
                            }
                        }
                    }
//                    } else {
//                        process_LinearLayout.setVisibility(View.GONE);
//                    }
                }
            });
        }
    }
}
