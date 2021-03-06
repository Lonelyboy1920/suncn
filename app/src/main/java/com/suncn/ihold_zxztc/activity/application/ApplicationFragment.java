package com.suncn.ihold_zxztc.activity.application;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.longchat.base.QDClient;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.H5WebViewActivity;
import com.suncn.ihold_zxztc.activity.MainActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetSpeakOpinion_MangerListActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetSpeak_MainActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_MainActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_MangerListActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.ProposalManagerListActivity;
import com.suncn.ihold_zxztc.activity.application.publicopinion.PublicOpinion_ListActivity;
import com.suncn.ihold_zxztc.activity.application.publicopinion.PublicOpinion_ManageListActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
;
import com.suncn.ihold_zxztc.activity.application.meeting.QRCodeCheckActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.SignConfirmActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.ProposalHandlerListActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_AuditListActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_MainActivity;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_TrackListActivity;
import com.suncn.ihold_zxztc.activity.application.theme.Theme_MainActivity;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.activity.hot.IntegralWebViewActivity;
import com.suncn.ihold_zxztc.activity.message.Contact_MainActivity;
import com.suncn.ihold_zxztc.activity.plenarymeeting.PlenaryMeetingGuideActivity;
import com.suncn.ihold_zxztc.activity.study.StudyActivity;
import com.suncn.ihold_zxztc.adapter.ApplicationListAdapter;
import com.suncn.ihold_zxztc.bean.ApplicationListBean;
import com.suncn.ihold_zxztc.bean.SessionListBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.DefineUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.suncn.ihold_zxztc.utils.ProjectNameUtil;

import androidx.recyclerview.widget.RecyclerView;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

import static android.app.Activity.RESULT_OK;

/**
 * ???????????????
 */
public class ApplicationFragment extends BaseFragment {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.view_place)
    private View viewPlace;
    private ApplicationListAdapter adapter;
    private ArrayList<SessionListBean.SessionBean> objCode_list;
    private ArrayList<SessionListBean.YearInfo> year_list;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // ???????????????/????????????
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (data != null) {
                        String strEventInfo = data.getStringExtra("strEventInfo");
                        Bundle bundle = new Bundle();
                        bundle.putString("strEventInfo", strEventInfo);
                        //bundle.putInt("sign", sign);
                        showActivity(fragment, SignConfirmActivity.class, bundle, 1);
                    }
                    break;
                default:
                    showActivity(fragment, QRCodeCheckActivity.class, 0);
                    break;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).currentFragment == this) {
            getSessionList(false);
        }
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_application, null);
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBar(false);
        viewPlace.setVisibility(View.VISIBLE);
        setHeadTitle(getString(R.string.string_application));
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        adapter = new ApplicationListAdapter(activity);
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.view_erv_empty); // ?????????????????????view
        goto_Button.setVisibility(View.GONE);
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        goto_Button.setText("\ue62f");
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
//        getSessionList(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
                HiPermission.create(activity).checkSinglePermission(Manifest.permission.CAMERA, new PermissionCallback() {
                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        showActivity(fragment, QRCodeCheckActivity.class, 0);
                    }
                });
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ApplicationListBean.ApplicationBean applicationBean = (ApplicationListBean.ApplicationBean) adapter.getItem(position);
                String strType = applicationBean.getStrType();
                Bundle bundle = new Bundle();
                bundle.putString("headTitle", applicationBean.getStrName());
                bundle.putString("strMenuType", applicationBean.getStrType());
                bundle.putSerializable("sessionBeans", objCode_list);
                bundle.putSerializable("yearList", year_list);
                switch (strType) {
                    case "01"://??????
                        showActivity(fragment, Proposal_MainActivity.class, bundle);
                        break;
                    case "02"://????????????
                        showActivity(fragment, PublicOpinion_ListActivity.class, bundle);
                        break;
                    case "03"://????????????
                        bundle.putInt("intKind", DefineUtil.wdhy);
                        showActivity(fragment, MeetAct_MainActivity.class, bundle);
                        break;
                    case "04"://????????????
                        showActivity(fragment, MeetSpeak_MainActivity.class, bundle);
                        break;
                    case "05"://????????????
                        bundle.putInt("intKind", DefineUtil.wdhd);
                        showActivity(fragment, MeetAct_MainActivity.class, bundle);
                        break;
                    case "06"://???????????????????????????

                        break;
                    case "08"://????????????
                        showActivity(fragment, StudyActivity.class, bundle);
                        break;
                    case "35"://????????????
                    case "37"://????????????????????????
                        bundle.putString("strUrl", "res/wlyz/index.html?strSid=" + GISharedPreUtil.getString(activity, "strSid"));
                        bundle.putBoolean("isNeedTitle", false);
                        bundle.putInt("color", R.color.color_red_top);
                        showActivity(fragment, H5WebViewActivity.class, bundle);
                        break;
                    case "14"://????????????
                        showActivity(fragment, ProposalManagerListActivity.class, bundle);
                        break;
                    case "15"://????????????????????????
                    case "24"://??????????????????????????????
                    case "25"://????????????
                        showActivity(fragment, Proposal_TrackListActivity.class, bundle);
                        break;
                    case "16"://????????????
                        if (ProjectNameUtil.isJMSZX(activity)) {//????????????????????????????????????
                            bundle.putBoolean("isCommissioner", false);
                        } else {
                            bundle.putBoolean("isCommissioner", true);
                        }
                        bundle.putBoolean("isShowHead", true);
                        bundle.putString("showTitle", "????????????");
                        showActivity(fragment, Contact_MainActivity.class, bundle);
                        break;
                    case "17"://????????????
                        bundle.putInt("intKind", DefineUtil.hygl);
                        showActivity(fragment, MeetAct_MangerListActivity.class, bundle);
                        break;
                    case "18"://????????????
                        bundle.putInt("intKind", DefineUtil.hdgl);
                        showActivity(fragment, MeetAct_MangerListActivity.class, bundle);
                        break;
                    case "19"://????????????
                        bundle.putInt("intKind", DefineUtil.sqmy);
                        showActivity(fragment, PublicOpinion_ManageListActivity.class, bundle);
                        break;
                    case "20"://????????????
                        bundle.putInt("intKind", DefineUtil.hyfy);
                        showActivity(fragment, MeetSpeakOpinion_MangerListActivity.class, bundle);
                        break;
                    case "22"://????????????
                        showActivity(fragment, ProposalHandlerListActivity.class, bundle);
                        break;
                }
            }
        });

    }

    /**
     * ??????????????????
     */
    private void getSessionList(boolean b) {
        if (b)
            prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getSessionList(textParamMap), 1);
    }

    /**
     * ??????????????????
     */
    private void getApplicationList() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().getApplicationList(textParamMap), 0);
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        try {
            switch (sign) {
                case 1://????????????
                    SessionListBean sessionListBean = (SessionListBean) data;
                    objCode_list = sessionListBean.getObjList();
                    year_list = sessionListBean.getObjYearList();
                    if (objCode_list != null && objCode_list.size() > 0) {
                        GISharedPreUtil.setValue(activity, "strSessionCode", objCode_list.get(0).getStrSessionCode());
                        GISharedPreUtil.setValue(activity, "strSessionName", objCode_list.get(0).getStrSessionName());
                        GISharedPreUtil.setValue(activity, "strSessionId", objCode_list.get(0).getStrSessionId());
                    }
                    getApplicationList();
                    break;
                case 0:
                    prgDialog.closePrgDialog();
                    ApplicationListBean applicationListBean = (ApplicationListBean) data;
                    List<ApplicationListBean.ApplicationBean> applicationBeans = applicationListBean.getObjList();
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setList(applicationBeans);
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
}
