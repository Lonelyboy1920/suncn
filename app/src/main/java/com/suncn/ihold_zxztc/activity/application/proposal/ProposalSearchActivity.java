package com.suncn.ihold_zxztc.activity.application.proposal;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.ListAllYearServletBean;
import com.suncn.ihold_zxztc.bean.ParamByTypeBean;
import com.suncn.ihold_zxztc.bean.ProposalTypeAllListServletBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.view.MenuItemEditLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import skin.support.content.res.SkinCompatResources;

/**
 * @author :Sea
 * Date :2020-6-15 9:33
 * PackageName:com.suncn.ihold_zxztc.activity.application.proposal
 * Desc:  提案查询
 */
public class ProposalSearchActivity extends BaseActivity {
    @BindView(id = R.id.tv_year, click = true)
    private MenuItemEditLayout tvYear;
    @BindView(id = R.id.tvSession, click = true)
    private MenuItemEditLayout tvSession;
    @BindView(id = R.id.tvProposalType, click = true)
    private MenuItemEditLayout tvProposalType;
    @BindView(id = R.id.tvProposalStatus, click = true)
    private MenuItemEditLayout tvProposalStatus;
    @BindView(id = R.id.tvProponentType, click = true)
    private MenuItemEditLayout tvProponentType;
    @BindView(id = R.id.tvSector, click = true)
    private MenuItemEditLayout tvSector;
    @BindView(id = R.id.tvAffiliatedSpecialCommittee, click = true)
    private MenuItemEditLayout tvAffiliatedSpecialCommittee;
    @BindView(id = R.id.etOrganizer)
    private GIEditText etOrganizer;
    @BindView(id = R.id.etJointVenture)
    private GIEditText etJointVenture;
    @BindView(id = R.id.etContent)
    private GIEditText etContent;
    @BindView(id = R.id.etBranchOffice)
    private GIEditText etBranchOffice;
    @BindView(id = R.id.etProponent)
    private GIEditText etProponent;
    @BindView(id = R.id.etSubject)
    private GIEditText etSubject;
    @BindView(id = R.id.etSerialNumber)
    private GIEditText etSerialNumber;
    @BindView(id = R.id.etProposalNo)
    private GIEditText etProposalNo;
    @BindView(id = R.id.tv_confirm, click = true)
    private TextView tvConfirm;
    @BindView(id = R.id.tv_clear, click = true)
    private TextView tvClear;
    @BindView(id = R.id.tvSubjectTag)
    private TextView tvSubjectTag;
    @BindView(id = R.id.nestedScrollView)
    private NestedScrollView nestedScrollView;
    @BindView(id = R.id.rl_head)
    private RelativeLayout rl_head;

    private NormalListDialog normalListDialog;
    private String[] stringsYear;  //年份
    private String[] stringsSessionBeans;// 届次
    private String[] stringsProposalType; //提案类型
    private String[] stringsProposalTypeTwo;// 提案类型二级
    private String[] stringsProposalTypeThree;//提案类型三级
    private String[] stringsSector;  //界别
    private String[] stringsAffiliatedSpecialCommittee;  //专委会
    private String[] stringsProponentType;
    private String[] stringsState;
    private String strYear;
    private String strSession;
    private String strProposalType;
    private String strSector;
    private String strAffiliatedSpecialCommittee;
    private String strProponentType;
    private String strProposalStatus;
    public ArrayList<SessionListBean.SessionBean> sessionBeans; // 届次集合
    public ArrayList<SessionListBean.YearInfo> yearList; // 年份集合
    private ProposalTypeAllListServletBean proposalTypeAllListServletBean;
    private ParamByTypeBean sectorBean;  //界别
    private ParamByTypeBean affiliatedSpecialCommitteeBean;  //专委会
    private ParamByTypeBean proponentTypeBean; //提案者
    private ParamByTypeBean stateBean; //提案状态
    private String strType;
    private String strSessionId;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_proposal_search);
    }

    @Override
    public void initData() {
        super.initData();
        findViewById(R.id.view_place).setVisibility(View.GONE);
        setHeadTitle(getString(R.string.string_proposal_inquiry));
        requestCallBack = (data, sign) -> doLogic(data, sign);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sessionBeans = (ArrayList<SessionListBean.SessionBean>) bundle.getSerializable("sessionBeans");
            yearList = (ArrayList<SessionListBean.YearInfo>) bundle.getSerializable("yearList");
        }

//        if (null != yearList && yearList.size() > 0) {
//            stringsYear = new String[yearList.size()];
//            for (int i = 0; i < yearList.size(); i++) {
//                stringsYear[i] = yearList.get(i).getStrYear();
//            }
//        }
//        if (null != sessionBeans && sessionBeans.size() > 0) {
//            stringsSessionBeans = new String[sessionBeans.size()];
//            for (int i = 0; i < sessionBeans.size(); i++) {
//                stringsSessionBeans[i] = sessionBeans.get(i).getStrSessionName();
//            }
//        }

        tvSubjectTag.setText(GIStringUtil.isBlank(GISharedPreUtil.getString(activity, "strTitle")) ? getString(R.string.string_subject) : GISharedPreUtil.getString(activity, "strTitle"));
        etSubject.setHint(getString(R.string.string_please_input) +
                (GIStringUtil.isBlank(GISharedPreUtil.getString(activity, "strTitle")) ? getString(R.string.string_subject) : GISharedPreUtil.getString(activity, "strTitle")));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_year:  //年份
//                showSelectDialog(0);
                getListAllYearServlet();
                break;
            case R.id.tvSession: //届次
//                showSelectDialog(1);
                getProposalSessionListServlet();
                break;
            case R.id.tvProposalType:  //提案类型
                getProposalTypeAllListServlet();
                break;
            case R.id.tvProposalStatus:  //提案状态
                getStateListByTypeServlet();
                break;
            case R.id.tvProponentType:  //提案者类型
                getReportUserTypeListServlet();
                break;
            case R.id.tvSector:  //所属界别
                strType = "10";
                getParamByType(2);
                break;
            case R.id.tvAffiliatedSpecialCommittee: //所属专委会
                strType = "11";
                getParamByType(3);
                break;
            case R.id.tv_confirm:
//                if (GIStringUtil.isBlank(strYear)
//                        && GIStringUtil.isBlank(strSession)
//                        && GIStringUtil.isBlank(strProposalType)
//                        && GIStringUtil.isBlank(strProposalStatus)
//                        && GIStringUtil.isBlank(strProponentType)
//                        && GIStringUtil.isBlank(strSector)
//                        && GIStringUtil.isBlank(strAffiliatedSpecialCommittee)
//                        && GIStringUtil.isBlank(etOrganizer.getText().toString())
//                        && GIStringUtil.isBlank(etJointVenture.getText().toString())
//                        && GIStringUtil.isBlank(etContent.getText().toString())
//                        && GIStringUtil.isBlank(etBranchOffice.getText().toString())
//                        && GIStringUtil.isBlank(etProponent.getText().toString())
//                        && GIStringUtil.isBlank(etSubject.getText().toString())
//                        && GIStringUtil.isBlank(etSerialNumber.getText().toString())
//                        && GIStringUtil.isBlank(etProposalNo.getText().toString())) {
//                    showToast("请填写任一条件进行查询");
//                } else {
                Bundle bundle = new Bundle();
                bundle.putString("strYear", strYear);
                bundle.putString("strSession", strSessionId);
                bundle.putString("strProposalType", strProposalType);
                bundle.putString("strProposalStatus", strProposalStatus);
                bundle.putString("strProponentType", strProponentType);
                bundle.putString("strSector", strSector);
                bundle.putString("strAffiliatedSpecialCommittee", strAffiliatedSpecialCommittee);
                bundle.putString("etOrganizer", etOrganizer.getText().toString());
                bundle.putString("etJointVenture", etJointVenture.getText().toString());
                bundle.putString("strContent", etContent.getText().toString());
                bundle.putString("etBranchOffice", etBranchOffice.getText().toString());
                bundle.putString("etProponent", etProponent.getText().toString());
                bundle.putString("etSubject", etSubject.getText().toString());
                bundle.putString("etSerialNumber", etSerialNumber.getText().toString());
                bundle.putString("etProposalNo", etProposalNo.getText().toString());
                showActivity(activity, ProposalSearchResultListActivity.class, bundle);
//                }
                break;
            case R.id.tv_clear:
                tvYear.setValue("");
                tvSession.setValue("");
                tvProposalType.setValue("");
                tvProponentType.setValue("");
                tvSector.setValue("");
                tvAffiliatedSpecialCommittee.setValue("");
                etOrganizer.setText("");
                etJointVenture.setText("");
                etContent.setText("");
                tvProposalStatus.setValue("");
                etBranchOffice.setText("");
                etProponent.setText("");
                etSubject.setText("");
                etSerialNumber.setText("");
                etProposalNo.setText("");
                strYear = "";
                strSession = "";
                strProposalType = "";
                strSector = "";
                strAffiliatedSpecialCommittee = "";
                strProponentType = "";
                strProposalStatus = "";
                nestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);
                nestedScrollView.scrollTo(0, rl_head.getBottom());
                break;
        }
    }

    /**
     * 年份
     */
    private void getListAllYearServlet() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getListAllYearServlet(textParamMap), 5);
    }

    /**
     * 届次
     */
    private void getProposalSessionListServlet() {
        textParamMap = new HashMap<>();
        if (GIStringUtil.isNotBlank(strYear)) {
            textParamMap.put("strYear", strYear);
        }
        doRequestNormal(ApiManager.getInstance().getProposalSessionListServlet(textParamMap), 6);
    }

    /**
     * 提案分类
     */
    private void getProposalTypeAllListServlet() {
        textParamMap = new HashMap<>();
        if (GIStringUtil.isNotBlank(strSessionId)) {
            textParamMap.put("strSessionId", strSessionId);
        }
        doRequestNormal(ApiManager.getInstance().getProposalTypeAllListServlet(textParamMap), 0);
    }

    /**
     * 提案状态
     */
    private void getStateListByTypeServlet() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "1");
        doRequestNormal(ApiManager.getInstance().StateListByTypeServlet(textParamMap), 1);
    }

    /**
     * 提案者类型
     */
    private void getReportUserTypeListServlet() {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", "1");
        doRequestNormal(ApiManager.getInstance().ReportUserTypeListServlet(textParamMap), 4);
    }

    /**
     * 届别 /专委会
     */
    private void getParamByType(int sign) {
        textParamMap = new HashMap<>();
        textParamMap.put("strType", strType);
        doRequestNormal(ApiManager.getInstance().getParamByType(textParamMap), sign);
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        try {
            switch (sign) {
                case 0://提案分类
                    proposalTypeAllListServletBean = (ProposalTypeAllListServletBean) data;
                    if (null != proposalTypeAllListServletBean.getObjList() && proposalTypeAllListServletBean.getObjList().size() > 0) {
                        stringsProposalType = new String[proposalTypeAllListServletBean.getObjList().size()];
                        for (int i = 0; i < proposalTypeAllListServletBean.getObjList().size(); i++) {
                            stringsProposalType[i] = proposalTypeAllListServletBean.getObjList().get(i).getStrProposalTypeName();
                        }
                        if (stringsProposalType.length > 0) {
                            showSelectDialog(2);
                        } else {
                            showToast(getString(R.string.string_have_no_data));
                        }
                    } else {
                        showToast(getString(R.string.string_have_no_data));
                    }

                    break;
                case 1:
                    stateBean = (ParamByTypeBean) data;
                    stringsState = new String[stateBean.getObjList().size()];
                    for (int i = 0; i < stateBean.getObjList().size(); i++) {
                        stringsState[i] = stateBean.getObjList().get(i).getStrName();
                    }
                    if (stringsState.length > 0) {
                        showSelectDialog(3);
                    } else {
                        showToast(getString(R.string.string_have_no_data));
                    }
                    break;
                case 2:
                    sectorBean = (ParamByTypeBean) data;
                    stringsSector = new String[sectorBean.getObjList().size()];
                    for (int i = 0; i < sectorBean.getObjList().size(); i++) {
                        stringsSector[i] = sectorBean.getObjList().get(i).getStrName();
                    }
                    if (stringsSector.length > 0) {
                        showSelectDialog(5);
                    } else {
                        showToast(getString(R.string.string_have_no_data));
                    }

                    break;
                case 3:
                    affiliatedSpecialCommitteeBean = (ParamByTypeBean) data;
                    stringsAffiliatedSpecialCommittee = new String[affiliatedSpecialCommitteeBean.getObjList().size()];
                    for (int i = 0; i < affiliatedSpecialCommitteeBean.getObjList().size(); i++) {
                        stringsAffiliatedSpecialCommittee[i] = affiliatedSpecialCommitteeBean.getObjList().get(i).getStrName();
                    }
                    if (stringsAffiliatedSpecialCommittee.length > 0) {
                        showSelectDialog(6);
                    } else {
                        showToast(getString(R.string.string_have_no_data));
                    }
                    break;
                case 4:
                    proponentTypeBean = (ParamByTypeBean) data;
                    stringsProponentType = new String[proponentTypeBean.getObjList().size()];
                    for (int i = 0; i < proponentTypeBean.getObjList().size(); i++) {
                        stringsProponentType[i] = proponentTypeBean.getObjList().get(i).getStrName();
                    }
                    if (stringsProponentType.length > 0) {
                        showSelectDialog(4);
                    } else {
                        showToast(getString(R.string.string_have_no_data));
                    }
                    break;
                case 5:
                    ListAllYearServletBean listAllYearServletBean = (ListAllYearServletBean) data;
                    stringsYear = new String[listAllYearServletBean.getObjList().size()];
                    for (int i = 0; i < listAllYearServletBean.getObjList().size(); i++) {
                        stringsYear[i] = listAllYearServletBean.getObjList().get(i).getStrYear();
                    }

                    if (stringsYear.length > 0) {
                        showSelectDialog(0);
                    } else {
                        showToast(getString(R.string.string_have_no_data));
                    }
                    break;
                case 6:
                    SessionListBean sessionListBean = (SessionListBean) data;
                    sessionBeans = sessionListBean.getObjList();
                    if (null != sessionBeans && sessionBeans.size() > 0) {
                        stringsSessionBeans = new String[sessionBeans.size()];
                        for (int i = 0; i < sessionBeans.size(); i++) {
                            stringsSessionBeans[i] = sessionBeans.get(i).getStrSessionName();
                        }
                        showSelectDialog(1);
                    } else {
                        showToast(getString(R.string.string_have_no_data));
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getString(R.string.data_error);
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }

    private List<ProposalTypeAllListServletBean.ObjListBean.ObjChlidList> objChildList;

    private void showSelectDialog(int type) {
        String[] typeStrings = new String[]{};
        String title = "";
        if (type == 0) {
            title = getString(R.string.string_particular_year);
            typeStrings = stringsYear;
        } else if (type == 1) {
            title = getString(R.string.string_session);
            typeStrings = stringsSessionBeans;
        } else if (type == 2) {
            title = getString(R.string.string_proposal_type);
            typeStrings = stringsProposalType;
        } else if (type == 3) {
            title = getString(R.string.string_proposal_status_no_line);
            typeStrings = stringsState;
        } else if (type == 4) {
            title = getString(R.string.string_proponen_type);
            typeStrings = stringsProponentType;
        } else if (5 == type) {
            title = getString(R.string.string_belonging_to_sector);
            typeStrings = stringsSector;
        } else if (6 == type) {
            title = getString(R.string.string_affiliated_special_committee);
            typeStrings = stringsAffiliatedSpecialCommittee;
        } else if (10 == type) {
            title = "";
            typeStrings = stringsProposalTypeTwo;
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
                    normalListDialog.dismiss();
                } else if (type == 1) {
                    tvSession.setValue(text);
                    strSession = sessionBeans.get(position1).getStrSessionCode();
                    strSessionId = sessionBeans.get(position1).getStrSessionId();
                    normalListDialog.dismiss();
                } else if (type == 2) {
                    objChildList = proposalTypeAllListServletBean.getObjList().get(position1).getObjChildList();
                    if (null != objChildList && objChildList.size() > 0) {
                        stringsProposalTypeTwo = new String[objChildList.size()];
                        for (int i = 0; i < objChildList.size(); i++) {
                            stringsProposalTypeTwo[i] = objChildList.get(i).getStrProposalTypeName();
                        }
                        normalListDialog.dismiss();
                        showSelectDialog(10);
                    } else {
                        tvProposalType.setValue(text);
                        strProposalType = proposalTypeAllListServletBean.getObjList().get(position1).getStrProposalTypeId();
                        normalListDialog.dismiss();
                    }
                } else if (type == 3) {
                    tvProposalStatus.setValue(text);
                    strProposalStatus = stateBean.getObjList().get(position1).getStrCode();
                    normalListDialog.dismiss();
                } else if (type == 4) {
                    tvProponentType.setValue(text);
                    strProponentType = proponentTypeBean.getObjList().get(position1).getStrCode();
                    normalListDialog.dismiss();
                } else if (5 == type) {  //届次
                    tvSector.setValue(text);
                    strSector = sectorBean.getObjList().get(position1).getStrCode();
                    normalListDialog.dismiss();
                } else if (6 == type) {  //专委会
                    tvAffiliatedSpecialCommittee.setValue(text);
                    strAffiliatedSpecialCommittee = affiliatedSpecialCommitteeBean.getObjList().get(position1).getStrCode();
                    normalListDialog.dismiss();
                } else if (10 == type) {
                    if (null != objChildList.get(position1).getObjChildList() && objChildList.get(position1).getObjChildList().size() > 0) {
                        stringsProposalTypeTwo = new String[objChildList.get(position1).getObjChildList().size()];
                        for (int i = 0; i < objChildList.get(position1).getObjChildList().size(); i++) {
                            stringsProposalTypeTwo[i] = objChildList.get(position1).getObjChildList().get(i).getStrProposalTypeName();
                        }
                        normalListDialog.dismiss();
                        showSelectDialog(10);
                    } else {
                        tvProposalType.setValue(text);
                        strProposalType = objChildList.get(position1).getStrProposalTypeId();
                        normalListDialog.dismiss();
                    }
                }

            }
        });
        normalListDialog.title(getString(R.string.picture_please_select) + title);
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        normalListDialog.show();
    }
}
