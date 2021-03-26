package com.suncn.ihold_zxztc.activity.message;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.longchat.base.QDClient;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.qd.longchat.activity.QDPersonChatActivity;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.UserBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.HashMap;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * 通讯录详情
 */
public class Contact_DetailActivity extends BaseActivity {
    @BindView(id = R.id.ll_main)
    private LinearLayout main_Layout;
    @BindView(id = R.id.iv_head)
    private ImageView head_ImageView; // 头像
    @BindView(id = R.id.tv_name)
    private TextView name_TextView; // 姓名
    @BindView(id = R.id.et_circles)
    private TextView circles_EditText; // 界别
    @BindView(id = R.id.et_clan)
    private TextView clan_EditText; // 党派
    @BindView(id = R.id.tv_job_label)
    private TextView jobLabel_TextView; // 职务标签
    @BindView(id = R.id.et_job)
    private TextView job_EditText; // 职务
    @BindView(id = R.id.et_mobile)
    private TextView mobile_EditText; // 手机号码
    @BindView(id = R.id.tv_call, click = true)
    private GITextView call_TextView; // 打电话按钮
    @BindView(id = R.id.tv_message, click = true)
    private GITextView message_TextView; // 发短信按钮
    @BindView(id = R.id.ll_telephone)
    private LinearLayout telephone_Layout; // 办公电话Layout
    @BindView(id = R.id.tv_telephone)
    private TextView telephone_TextView; // 办公电话
    @BindView(id = R.id.tv_call_telephone, click = true)
    private GITextView callTelephone_TextView; // 打办公电话按钮
    @BindView(id = R.id.et_email) // 电子邮箱
    private TextView email_EditText;
    @BindView(id = R.id.ll_address)
    private LinearLayout address_LinearLayout;//联系地址LinearLayout
    @BindView(id = R.id.tv_address)
    private TextView address_TextView;//联系地址TextView
    @BindView(id = R.id.ll_send, click = true)
    private LinearLayout send_LinearLayout;//发送信息

    String strCompany;
    private String strUserId = "";
    private String strMobile; // 手机号码
    private String strTelephone; // 办公电话
    private String strName; // 姓名
    private String strLinkId;//聊天用户名id
    private String strLinkName;//聊天用户名
    private String strQdUserId;//用户名（启达的用户id）

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_contact_detail);
        isShowBackBtn = true;
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("");
        main_Layout.setVisibility(View.GONE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strUserId = bundle.getString("strUserId", "");
            strQdUserId = bundle.getString("strQdUserId", "");
            getInfo();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_send: // 发消息
                if (AppConfigUtil.isUseQDIM(activity)) {
                    if (!QDClient.getInstance().isOnline()) {
                        showToast(getString(R.string.string_im_login_failed_please_relogin));
                        return;
                    }
                    QDUser user = QDUserHelper.getUserById(strQdUserId);
                    if (user == null) {
                        showToast(getString(R.string.string_this_user_not_register_im_and_cannot_chat));
                        return;
                    }
                    Intent intent = new Intent(activity, QDPersonChatActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CHAT_ID, strQdUserId);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_USER, true);
                    startActivity(intent);
                }
                break;
            case R.id.tv_call://电话
                showMyDialog(strMobile);
                break;
            case R.id.tv_message://短信
                Uri uri = Uri.parse("smsto:" + strMobile);
                Intent intentMessage = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(intentMessage);
                break;
            case R.id.tv_call_telephone://办公电话
                showMyDialog(strTelephone);
                break;
        }
    }


    /**
     * 获取个人信息
     */
    private void getInfo() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<String, String>();
        textParamMap.put("strUserId", strUserId);
        textParamMap.put("strQdUserId", strQdUserId);
        doRequestNormal(ApiManager.getInstance().getInfo(textParamMap), 0);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object object, int sign) {
        prgDialog.closePrgDialog();
        String toastMessage = null;
        if (sign == 0) {
            try {
                UserBean user = (UserBean) object;
                strName = user.getStrName();
                name_TextView.setText(strName);
                strMobile = user.getStrMobile();
                strQdUserId = user.getStrQDUserId();
                strUserId = user.getStrUserId();
                //未注册启达或者是本人都不显示发消息按钮
                if (GIStringUtil.isBlank(strQdUserId) || !AppConfigUtil.isUseQDIM(activity)
                        || strUserId.equals(GISharedPreUtil.getString(activity, "strUserId"))) {
                    send_LinearLayout.setVisibility(View.GONE);
                } else {
                    send_LinearLayout.setVisibility(View.VISIBLE);
                }
                QDClient.getInstance().subscribeStatus(strQdUserId);
                if (GIStringUtil.isNotBlank(strMobile)) {
                    mobile_EditText.setText(strMobile);
                    call_TextView.setVisibility(View.VISIBLE);
                    message_TextView.setVisibility(View.VISIBLE);
                } else {
                    message_TextView.setVisibility(View.GONE);
                    call_TextView.setVisibility(View.GONE);
                    mobile_EditText.setText("");
                }
                if ("1".equals(user.getStrOpenMobile())) { // 不显示手机号时中间4位数用*代替，隐藏打电话和发短信按钮（2019-08-12跟胡伟确认）
                    message_TextView.setVisibility(View.GONE);
                    call_TextView.setVisibility(View.GONE);
                }
                email_EditText.setText(user.getStrEmail());
                String strPathUrl = Utils.formatFileUrl(activity, user.getStrPathUrl());
                GIImageUtil.loadImg(activity, head_ImageView, strPathUrl, 1);

                // 0 委员 1机关委用户 2上报单位用户 0显示界别、党派、届次号 1显示单位的名称 2显示领导名称、领导电话部门领导名称、部门领导电话 其他字段不做控制
                // 通用字段（头像、姓名、手机、邮箱）
                main_Layout.setVisibility(View.VISIBLE);
                circles_EditText.setVisibility(View.INVISIBLE);
                telephone_Layout.setVisibility(View.GONE);
                switch (user.getIntLoginUserType()) {
                    case 0: // 委员（显示界别、党派、单位及职位）
                        circles_EditText.setVisibility(View.VISIBLE);
                        circles_EditText.setText(user.getStrSector()); // 界别
                        clan_EditText.setText(user.getStrFaction()); // 党派
                        job_EditText.setText(user.getStrDuty());
                        break;
                    case 1: // 机关委用户（显示单位的名称、办公电话、职务，联系地址）
                        address_LinearLayout.setVisibility(View.VISIBLE);
                        address_TextView.setText(user.getStrLinkAdd());
                        clan_EditText.setText(user.getStrUnitName()); // 所属单位
                        telephone_Layout.setVisibility(View.VISIBLE);
                        strTelephone = user.getStrOfficeMobile();
                        telephone_TextView.setText(strTelephone);
                        if (GIStringUtil.isBlank(strTelephone)) {
                            callTelephone_TextView.setVisibility(View.GONE);
                        }
                        jobLabel_TextView.setText(R.string.string_duties);
                        job_EditText.setText(user.getStrDuty());
                        break;
                    case 2: // 上报单位用户（显示领导名称、领导电话部门领导名称、部门领导电话）
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                toastMessage = getString(R.string.data_error);
            }
        }
        if (GIStringUtil.isNotEmpty(toastMessage)) {
            showToast(toastMessage);
        }
    }

    private void showMyDialog(String number) {
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.content(strName + "：" + number).btnText(getString(R.string.string_cancle), getString(R.string.string_call)).showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.isTitleShow(false);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                        //请求授权
                        HiPermission.create(activity).checkSinglePermission(Manifest.permission.CALL_PHONE, new PermissionCallback() {
                            @Override
                            public void onGuarantee(String permisson, int position) { // 同意/已授权
                                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
                                }
                            }

                            @Override
                            public void onClose() { // 用户关闭权限申请
                            }

                            @Override
                            public void onFinish() { // 所有权限申请完成
                            }

                            @Override
                            public void onDeny(String permisson, int position) { // 拒绝
                            }
                        });
                    }
                }
        );
    }


}
