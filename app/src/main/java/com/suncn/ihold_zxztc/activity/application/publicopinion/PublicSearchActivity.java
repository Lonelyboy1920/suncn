package com.suncn.ihold_zxztc.activity.application.publicopinion;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.ui.BindView;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.ParamByTypeBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.view.MenuItemEditLayout;

import java.util.ArrayList;
import java.util.HashMap;

import skin.support.content.res.SkinCompatResources;

public class PublicSearchActivity extends BaseActivity {
    @BindView(id = R.id.tv_year, click = true)
    private MenuItemEditLayout tvYear;
    @BindView(id = R.id.tv_state, click = true)
    private MenuItemEditLayout tvState;
    @BindView(id = R.id.tv_kwtype, click = true)
    private MenuItemEditLayout tvKwType;
    @BindView(id = R.id.tv_pstype, click = true)
    private MenuItemEditLayout tvPsType;
    @BindView(id = R.id.tv_fyrtype, click = true)
    private MenuItemEditLayout tvFYRType;
    @BindView(id = R.id.et_fyr)
    private EditText etFYR;
    @BindView(id = R.id.et_title)
    private EditText etTitle;
    @BindView(id = R.id.et_content)
    private EditText etContent;
    @BindView(id = R.id.tv_confirm, click = true)
    private TextView tvConfirm;
    @BindView(id = R.id.tv_clear, click = true)
    private TextView tvClear;
    private ArrayList<SessionListBean.YearInfo> yearInfoArrayList;
    private NormalListDialog normalListDialog;
    private String[] stringsYear;
    private String[] stringsState;
    private String[] stringsKW;
    private String[] stringsPS;
    private String[] stringsFYR;
    private ParamByTypeBean stateBean;
    private ParamByTypeBean kwBean;
    private ParamByTypeBean psBean;
    private ParamByTypeBean fyrBean;
    private String strYear;
    private String strSourceName;//反映人名称
    private String strLeaderType;//批示类型
    private String strReporterType;//反映人类型
    private String strTitle;
    private String strContent;
    private String strState;
    private String strPubType;//刊物类型

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_public_search);
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
        setHeadTitle("信息查询");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            yearInfoArrayList = (ArrayList<SessionListBean.YearInfo>) bundle.getSerializable("yearList");
        }
        stringsYear = new String[yearInfoArrayList.size()];
        if (yearInfoArrayList.size() > 0) {
            for (int i = 0; i < yearInfoArrayList.size(); i++) {
                stringsYear[i] = yearInfoArrayList.get(i).getStrYear();
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_year:
                showSelectDialog(0);
                break;
            case R.id.tv_state:
                getstate();
                break;
            case R.id.tv_kwtype:
                getKW();
                break;
            case R.id.tv_pstype:
                getParamByType();
                break;
            case R.id.tv_fyrtype:
                getFYRType();
                break;
            case R.id.tv_confirm:
                Bundle bundle = new Bundle();
                bundle.putString("strYear", strYear);
                bundle.putString("strSourceName", etFYR.getText().toString());
                bundle.putString("strLeaderType", strLeaderType);
                bundle.putString("strReporterType", strReporterType);
                bundle.putString("strTitle", etTitle.getText().toString());
                bundle.putString("strContent", etContent.getText().toString());
                bundle.putString("strState", strState);
                bundle.putString("strPubType", strPubType);
                showActivity(activity, PublicSearchResultListActivity.class, bundle);
                break;
            case R.id.tv_clear:
                tvYear.setValue("");
                tvState.setValue("");
                tvKwType.setValue("");
                tvPsType.setValue("");
                tvFYRType.setValue("");
                etFYR.setText("");
                etTitle.setText("");
                etContent.setText("");
                strYear = "";
                strSourceName = "";//反映人名称
                strLeaderType = "";//批示类型
                strReporterType = "";//反映人类型
                strTitle = "";
                strContent = "";
                strState = "";
                strPubType = "";//刊物类型
                break;
        }
    }

    /**
     * 获取批示类型
     */
    private void getParamByType() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "12");
        doRequestNormal(ApiManager.getInstance().getParamByType(textParamMap), 3);
    }

    /**
     * 获取反映人类型
     */
    private void getFYRType() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "2");  //后台把值给改了 1是提案的  2是社情民意的
        doRequestNormal(ApiManager.getInstance().ReportUserTypeListServlet(textParamMap), 4);
    }

    /**
     * 获取状态
     */
    private void getstate() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "2");
        doRequestNormal(ApiManager.getInstance().StateListByTypeServlet(textParamMap), 1);
    }

    /**
     * 获取刊物类型
     */
    private void getKW() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "2");
        doRequestNormal(ApiManager.getInstance().PubTypeListServlet(textParamMap), 2);
    }


    private void doLogic(int what, Object object) {
        switch (what) {
            case 1:
                stateBean = (ParamByTypeBean) object;
                stringsState = new String[stateBean.getObjList().size()];
                for (int i = 0; i < stateBean.getObjList().size(); i++) {
                    stringsState[i] = stateBean.getObjList().get(i).getStrName();
                }
                showSelectDialog(1);
                break;
            case 2:
                kwBean = (ParamByTypeBean) object;
                stringsKW = new String[kwBean.getObjList().size()];
                for (int i = 0; i < kwBean.getObjList().size(); i++) {
                    stringsKW[i] = kwBean.getObjList().get(i).getStrPubTypeName();
                }
                showSelectDialog(2);
                break;
            case 3:
                psBean = (ParamByTypeBean) object;
                stringsPS = new String[psBean.getObjList().size()];
                for (int i = 0; i < psBean.getObjList().size(); i++) {
                    stringsPS[i] = psBean.getObjList().get(i).getStrName();
                }
                showSelectDialog(3);
                break;
            case 4:
                fyrBean = (ParamByTypeBean) object;
                stringsFYR = new String[fyrBean.getObjList().size()];
                for (int i = 0; i < fyrBean.getObjList().size(); i++) {
                    stringsFYR[i] = fyrBean.getObjList().get(i).getStrName();
                }
                showSelectDialog(4);
                break;
        }
    }

    private void showSelectDialog(int type) {
        String[] typeStrings = new String[]{};
        String title = "";
        if (type == 0) {
            title = "年份";
            typeStrings = stringsYear;
        } else if (type == 1) {
            title = "状态";
            typeStrings = stringsState;
        } else if (type == 2) {
            title = "刊物类型";
            typeStrings = stringsKW;
        } else if (type == 3) {
            title = "批示类型";
            typeStrings = stringsPS;
        } else if (type == 4) {
            title = "反映人类型";
            typeStrings = stringsFYR;
        }
        normalListDialog = new NormalListDialog(activity, typeStrings);
        String[] finalTypeStrings = typeStrings;
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position1, long id) {
                String text = finalTypeStrings[position1];
                if (type == 0) {
                    tvYear.setValue(text);
                    strYear = text;
                } else if (type == 1) {
                    tvState.setValue(text);
                    strState = stateBean.getObjList().get(position1).getStrCode();
                } else if (type == 2) {
                    tvKwType.setValue(text);
                    strPubType = kwBean.getObjList().get(position1).getStrPubType();
                } else if (type == 3) {
                    tvPsType.setValue(text);
                    strLeaderType = psBean.getObjList().get(position1).getStrCode();
                } else if (type == 4) {
                    tvFYRType.setValue(text);
                    strReporterType = fyrBean.getObjList().get(position1).getStrCode();
                }
                normalListDialog.dismiss();
            }
        });
        normalListDialog.title(getString(R.string.picture_please_select) + title);
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        normalListDialog.show();
    }
}
