package com.suncn.ihold_zxztc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gavin.giframe.authcode.GIAESOperator;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIPhoneUtils;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.EventBusCarrier;
import com.suncn.ihold_zxztc.bean.LoginBean;
import com.suncn.ihold_zxztc.bean.MulUnitBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import skin.support.content.res.SkinCompatResources;

/**
 * 切换角色Activity
 */
public class ChangeRoleActivity extends BaseActivity {
    @BindView(id = R.id.listview)
    private ListView listView;
    @BindView(id = R.id.tv_title)
    private TextView tvTitle;
    @BindView(id = R.id.iv_head)
    private RoundImageView ivHead;
    @BindView(id = R.id.tv_close, click = true)
    private TextView tvClose;
    @BindView(id = R.id.ll_check, click = true)
    private LinearLayout llCheck;
    @BindView(id = R.id.tv_check)
    private GITextView tvCheck;
    @BindView(id = R.id.iv_confirm, click = true)
    private ImageView ivConfirm;
    private ArrayList<MulUnitBean.MulUnit> objList;
    private LoginBean LoginBean;//登录界面传递过来的数据
    private String strSelectUnitId;//当前选择的机构id
    private ChangeRoleAdapter adapter;
    private int intUserRole = -1;
    private boolean isChecked = false;

    @Override
    public void setRootView() {
        setStatusBar(true, false);
        setContentView(R.layout.activity_change_role);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            LoginBean = (LoginBean) bundle.getSerializable("loginBean");
        }
        tvTitle.setText(GISharedPreUtil.getString(activity, "strName") + "欢迎您登录使用");
        GIImageUtil.loadImg(activity, ivHead, Utils.formatFileUrl(activity, GISharedPreUtil.getString(activity, "strPhotoUrl")), 1);
        if (LoginBean == null) {
            getRoleList();
        } else {
            objList = LoginBean.getObjList();
        }
        isChecked = GISharedPreUtil.getBoolean(activity, "Checked");
        if (isChecked) {
            tvCheck.setText(getResources().getString(R.string.font_c_check));
            tvCheck.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        } else {
            tvCheck.setText(getResources().getString(R.string.font_c_uncheck));
            tvCheck.setTextColor(getResources().getColor(R.color.font_content));
        }
        adapter = new ChangeRoleAdapter();
        listView.setAdapter(adapter);
    }

    /**
     * 获取角色列表
     */
    private void getRoleList() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().ChangeRootUnitById(textParamMap), 0);
    }

    private void doLogic(Object data, int what) {
        String toastMessage = null;
        try {
            switch (what) {
                case 0:
                    MulUnitBean obj = (MulUnitBean) data;
                    objList = obj.getObjList();
                    adapter.notifyDataSetChanged();
                    break;
                case 1://切换角色
                    LoginBean loginBean = (LoginBean) data;
                    if (LoginBean != null) { // 登录后选择角色
                        Utils.sp_loginIn(activity, loginBean);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isHasLogin", true);
                        skipActivity(activity, MainActivity.class, bundle);
                    } else { // 切换角色
                        Utils.sp_loginIn(activity, loginBean);
                        EventBusCarrier eventBusCarrier = new EventBusCarrier();
                        eventBusCarrier.setEventType(1);
                        eventBusCarrier.setObject(intUserRole);
                        EventBus.getDefault().post(eventBusCarrier); //通知MainActivity刷新数据
                        toastMessage = "切换成功";
                        setResult(RESULT_OK);
                        finish();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getString(R.string.data_error);
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.iv_confirm:
                doLogin();
                break;
            case R.id.ll_check:
                if (!isChecked) {
                    isChecked = true;
                    tvCheck.setText(getResources().getString(R.string.font_c_check));
                    tvCheck.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                    GISharedPreUtil.setValue(activity, "Checked", true);
                } else {
                    isChecked = false;
                    tvCheck.setText(getResources().getString(R.string.font_c_uncheck));
                    tvCheck.setTextColor(getResources().getColor(R.color.font_content));
                    GISharedPreUtil.setValue(activity, "Checked", false);
                }
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (objList.get(position).getStrRootUnitFlagState().equals("0")) {
                    showToast("账号被锁定，请联系管理员处理。");
                    return;
                }
                strSelectUnitId = objList.get(position).getStrRootUnitId();
                intUserRole = objList.get(position).getIntUserRole();
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 登录请求（以指纹识别的模式进行登录）
     */
    private void doLogin() {
        textParamMap = new HashMap<String, String>();
//        textParamMap.put("strUsername", GISharedPreUtil.getString(activity, "strLoginUserId")); // 切换多独立机构时不需要传值，用户名
//        textParamMap.put("strPassword", GISharedPreUtil.getString(activity, "PWD")); // 切换多独立机构时不需要传值，密码
//        textParamMap.put("isFingerprint", "true");
        textParamMap.put("strUdid", GISharedPreUtil.getString(activity, "deviceCode")); // 必传------ 设备号
        textParamMap.put("strVersion", String.valueOf(GIPhoneUtils.getAppVersionCode(activity))); // 必传------ 客户端版本号
        textParamMap.put("strRootUnitId", strSelectUnitId); // 切换多独立机构时传入独立机构的id
        textParamMap.put("strDefalutUserId", GISharedPreUtil.getString(activity, "strDefalutUserId")); // 切换多独立机构是传入用户的账号
        textParamMap.put("intUserRole", intUserRole + ""); // 必传------ 用户类型（0-委员；1-工作人员；2-承办单位；3承办系统）,默认委员0
        GISharedPreUtil.setValue(activity, "resetUserRole", true);
        doRequestNormal(ApiManager.getInstance().doLogin(textParamMap), 1);
    }

    class ChangeRoleAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return objList == null ? 0 : objList.size();
        }

        @Override
        public Object getItem(int position) {
            return objList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChangeRoleViewHoder viewHoder;
            if (null == convertView) {
                viewHoder = new ChangeRoleViewHoder();
                convertView = LayoutInflater.from(activity).inflate(R.layout.item_change_role, null);
                convertView.setTag(viewHoder);
                viewHoder.tvTitle = (GITextView) convertView.findViewById(R.id.tv_name);
            } else {
                viewHoder = (ChangeRoleViewHoder) convertView.getTag();
            }
            viewHoder.tvTitle.setText(objList.get(position).getStrRootUnitName());
            if (GIStringUtil.isBlank(strSelectUnitId) && objList.get(position).getStrRootUnitDefalut().equals("1")) {
                //未选择的时候需要按照后来字段来判断默认选中哪一个
                strSelectUnitId = objList.get(position).getStrRootUnitId();
                intUserRole = objList.get(position).getIntUserRole();
                viewHoder.tvTitle.setBackground(getResources().getDrawable(R.mipmap.role_selected));
                viewHoder.tvTitle.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                viewHoder.tvTitle.setTextSize(25);
            } else if (objList.get(position).getStrRootUnitId().equals(strSelectUnitId)
                    && objList.get(position).getIntUserRole() == intUserRole) {
                viewHoder.tvTitle.setBackground(getResources().getDrawable(R.mipmap.role_selected));
                viewHoder.tvTitle.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                viewHoder.tvTitle.setTextSize(25);
            } else {
                viewHoder.tvTitle.setBackground(null);
                viewHoder.tvTitle.setTextColor(getResources().getColor(R.color.font_title));
                viewHoder.tvTitle.setTextSize(18);
            }
            return convertView;
        }

        class ChangeRoleViewHoder {
            private TextView tvTitle;
        }
    }
}
