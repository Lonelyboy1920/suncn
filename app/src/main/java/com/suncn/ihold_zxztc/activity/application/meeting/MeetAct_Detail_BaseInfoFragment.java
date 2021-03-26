package com.suncn.ihold_zxztc.activity.application.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIMyIntent;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.activity.global.FileListActivity;
import com.suncn.ihold_zxztc.adapter.ChildMeetAttendSituationLVAdapter;
import com.suncn.ihold_zxztc.adapter.MeetAct_Detail_BaseInfo_LVAdapter;
import com.suncn.ihold_zxztc.bean.ActivityViewBean;
import com.suncn.ihold_zxztc.bean.MeetingViewBean;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.forscrollview.MyListView;

import java.io.File;
import java.util.ArrayList;

import skin.support.content.res.SkinCompatResources;

import static android.app.Activity.RESULT_OK;

/**
 * 会议、活动详情中的基本信息Fragment
 */
public final class MeetAct_Detail_BaseInfoFragment extends BaseFragment {
    @BindView(id = R.id.listview)
    private ListView listView; // 基本信息列表
    @BindView(id = R.id.listview1)
    private MyListView chilMt_Listview; // 次会列表
    @BindView(id = R.id.ll_attend_state)
    private LinearLayout attendState_LinearLayout;
    @BindView(id = R.id.tv_leave_tag)
    private TextView leaveTag_TextView; // 会议请假
    @BindView(id = R.id.ll_attend_situation)
    private LinearLayout situation_LinearLayout; // 参加情况LinearLayout
    @BindView(id = R.id.tv_situation_value)
    private TextView situation_TextView; // 参加情况TextView
    @BindView(id = R.id.ll_leave_type)
    private LinearLayout leaveType_LinearLayout; // 请假类型布局
    @BindView(id = R.id.tv_leave_type_value)
    private TextView leaveType_TextView; // 请假类型
    @BindView(id = R.id.ll_leave_reason)
    private LinearLayout leaveReason_LinearLayout;
    @BindView(id = R.id.et_leave_reason)
    private TextView leaveReason_TextView;
    @BindView(id = R.id.ll_file, click = true)//相关附件
    private LinearLayout llFile;
    @BindView(id = R.id.tv_tag)
    private TextView tvTag;
    @BindView(id = R.id.tv_value)
    private TextView tvValue;
    private static ActivityViewBean activityViewBean; // 活动详情
    private static MeetingViewBean meetingViewBean; // 会议详情
    private static int sign;
    private int intUserRole; // 角色
    private GITextView goto_Button; // 提交按钮
    private Button spinner_Button;
    private String intAttend = "-2"; // 出席状态
    private String strAbsentType; // 请假类型
    private String strReason = ""; // 请假原因
    private int intModify; // 状态（1--可修改，0--不可修改）
    private ArrayList<MeetingViewBean.ObjChildMtListBean> objChildMtListBeen; // 次会列表
    private ChildMeetAttendSituationLVAdapter adapter1;
    private ArrayList<ObjFileBean> objFileBeans;//我的活动附件
    private ArrayList<ObjFileBean> objFileMeetingBeans;//我的会议附件
    private MeetAct_DetailActivity active_detailActivity;
    private boolean isPrepared;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
        } else if (resultCode == -2) {
            getActivity().setResult(-2);
            getActivity().finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setUserVisibleHint(true);//第一个fragment必须设置该属性，负责操作会出现空指针
        super.onActivityCreated(savedInstanceState);
    }

    public static MeetAct_Detail_BaseInfoFragment newInstance(ActivityViewBean mActivityViewBean, MeetingViewBean mMeetingViewBean, int mSign) {
        MeetAct_Detail_BaseInfoFragment baseInfoFragment = new MeetAct_Detail_BaseInfoFragment();
        meetingViewBean = mMeetingViewBean;
        activityViewBean = mActivityViewBean;
        sign = mSign;
        return baseInfoFragment;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_notice_active_base_info_listview, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isPrepared = true;//初始化已完成
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        active_detailActivity = (MeetAct_DetailActivity) getActivity();
    }

    @Override
    public void initData() {
        super.initData();
        intUserRole = GISharedPreUtil.getInt(activity, "intUserRole");
        goto_Button = active_detailActivity.findViewById(R.id.btn_goto);
        if (null != activityViewBean && "1".equals(activityViewBean.getStrOpenResult()) && 1 == intUserRole) {
            goto_Button.setText(R.string.string_activity_results);
            goto_Button.setVisibility(View.VISIBLE);
            goto_Button.refreshFontType(activity, "2");
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            goto_Button.setOnClickListener(this);
        } else {
            goto_Button.setVisibility(View.GONE);
        }
        if (active_detailActivity.isShowSign) {
            goto_Button.setVisibility(View.VISIBLE);
            goto_Button.setText(R.string.string_sign_up);
            goto_Button.refreshFontType(activity, "2");
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            goto_Button.setOnClickListener(this);
        } else {
            goto_Button.setVisibility(View.INVISIBLE);
        }
        setData();
    }

    private void setData() {
        if (activityViewBean != null) {
            ArrayList<String> baseInfoList = new ArrayList<>();
            baseInfoList.add(getString(R.string.string_activity_theme) + activityViewBean.getStrAtName() + "|true");
            baseInfoList.add(getString(R.string.string_type_of_activity) + activityViewBean.getStrMtType());
            baseInfoList.add(getString(R.string.string_start_date) + activityViewBean.getStrStartDate());
            baseInfoList.add(getString(R.string.string_end_date) + activityViewBean.getStrEndDate());
            baseInfoList.add(getString(R.string.string_event_location) + activityViewBean.getStrPlace());
            baseInfoList.add(getString(R.string.string_activity_content) + "" + "|true");
            baseInfoList.add("url|" + activityViewBean.getStrContentUrl());
            baseInfoList.add(getString(R.string.string_undertaking_department) + activityViewBean.getStrHostUnit());
            baseInfoList.add(getString(R.string.string_release_date) + activityViewBean.getDtPubDate());
            if (GIStringUtil.isNotBlank(activityViewBean.getStrState())) {
                baseInfoList.add(getString(R.string.string_meet_desire) + activityViewBean.getStrState());
            }
            MeetAct_Detail_BaseInfo_LVAdapter adapter = new MeetAct_Detail_BaseInfo_LVAdapter(getActivity(), baseInfoList);
            //adapter.setSign(sign);
            listView.setAdapter(adapter);
            intAttend = activityViewBean.getStrAttendResult();
            strAbsentType = activityViewBean.getStrAbsentType();
            strReason = activityViewBean.getStrReason();
            intModify = activityViewBean.getIntModify();
            if (activityViewBean.getFileList() != null && activityViewBean.getFileList().size() > 0) {
                llFile.setVisibility(View.VISIBLE);
                tvTag.setText(R.string.string_relevant_attachments);
                tvValue.setText(activityViewBean.getFileList().size() + "");

            }
            objFileBeans = new ArrayList<>();
            // objFileBeans = activityViewBean.getAffix();

            if (activityViewBean.getAffix() != null && activityViewBean.getAffix().size() > 0) {
                //ObjFileBean objFileBean = new ObjFileBean();
                for (int i = 0; i < activityViewBean.getAffix().size(); i++) {
                    ObjFileBean objFileBean = activityViewBean.getAffix().get(i);
                    objFileBean.setStrFile_url(Utils.formatFileUrl(activity, activityViewBean.getAffix().get(i).getStrFile_url()));
                    objFileBean.setStrFile_Type(GIMyIntent.getMIMEType(new File(activityViewBean.getAffix().get(i).getStrFile_url())));
                    objFileBeans.add(objFileBean);
                }
            }
        } else if (meetingViewBean != null) {
            ArrayList<String> baseInfoList = new ArrayList<>();
            baseInfoList.add(getString(R.string.string_conference_name) + meetingViewBean.getStrMtName() + "|true");
            baseInfoList.add(getString(R.string.string_conference_type) + meetingViewBean.getStrMtType());
            baseInfoList.add(getString(R.string.string_starting_time) + meetingViewBean.getStrStartDate());
            baseInfoList.add(getString(R.string.string_end_time) + meetingViewBean.getStrEndDate());
            baseInfoList.add(getString(R.string.string_meeting_place) + meetingViewBean.getStrPlace());
            baseInfoList.add(getString(R.string.string_conference_content) + "" + "|true");
            baseInfoList.add("url|" + meetingViewBean.getStrContentUrl());
            if (ProjectNameUtil.isGYSZX(activity)) {
                baseInfoList.add(getString(R.string.string_conference_press_release) + "" + "|");
                baseInfoList.add("|" + meetingViewBean.getStrNewsContent());
            }
            baseInfoList.add(getString(R.string.string_undertaking_department) + meetingViewBean.getStrHostUnit());
            baseInfoList.add(getString(R.string.string_release_date) + meetingViewBean.getDtPubDate());
            if (GIStringUtil.isNotBlank(meetingViewBean.getStrState())) {
                baseInfoList.add(getString(R.string.string_meet_desire) + meetingViewBean.getStrState());
            }
            // baseInfoList.add("发布单位|" + meetingViewBean.getStrPubUnit());
            MeetAct_Detail_BaseInfo_LVAdapter adapter = new MeetAct_Detail_BaseInfo_LVAdapter(getActivity(), baseInfoList);
            //adapter.setSign(sign);
            listView.setAdapter(adapter);
            intModify = meetingViewBean.getIntModify();
            intAttend = meetingViewBean.getIntAttend();
            strAbsentType = meetingViewBean.getStrAbsentType();
            strReason = meetingViewBean.getStrReason();
            objChildMtListBeen = meetingViewBean.getObjChildMt_list();
            if (meetingViewBean.getFileList() != null && meetingViewBean.getFileList().size() > 0) {
                llFile.setVisibility(View.VISIBLE);
                tvTag.setText(getString(R.string.string_relevant_attachments));
                tvValue.setText(meetingViewBean.getFileList().size() + "");
            }
            if (objChildMtListBeen == null || objChildMtListBeen.size() == 0) {
                objFileMeetingBeans = new ArrayList<>();
                if (meetingViewBean.getAffix() != null && meetingViewBean.getAffix().size() > 0) {
                    for (int i = 0; i < meetingViewBean.getAffix().size(); i++) {
                        //ObjFileBean objFileMeetBean = new ObjFileBean();
                        ObjFileBean objFileMeetBean = meetingViewBean.getAffix().get(i);
                        objFileMeetBean.setStrFile_url(Utils.formatFileUrl(activity, meetingViewBean.getAffix().get(i).getStrFile_url()));
                        objFileMeetBean.setStrFile_Type(GIMyIntent.getMIMEType(new File(meetingViewBean.getAffix().get(i).getStrFile_url())));
                        objFileMeetingBeans.add(objFileMeetBean);
                    }
                }
            }
        }
        if (((sign == DefineUtil.wdhd || sign == DefineUtil.wdhy) && (intAttend.equals("-1") || intAttend.equals("-2"))) || (sign == DefineUtil.hygl) || (sign == DefineUtil.hdgl)) {
            attendState_LinearLayout.setVisibility(View.GONE);
        } else {
            situation_TextView.setText(Utils.getAttendST(Integer.parseInt(intAttend)) + "  ");
            situation_TextView.setGravity(Gravity.RIGHT);
            leaveTag_TextView.setVisibility(View.VISIBLE);
            leaveTag_TextView.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
            if (sign == DefineUtil.wdhy) {
                leaveTag_TextView.setText(R.string.string_conference_participation_status);
            } else {
                leaveTag_TextView.setText(R.string.string_activity_participation_status);
            }
            if (intAttend.equals("0")) { // 请假
                leaveType_LinearLayout.setVisibility(View.VISIBLE);
                leaveReason_LinearLayout.setVisibility(View.VISIBLE);
                //leaveReason_TextView.setVisibility(View.VISIBLE);
                leaveType_TextView.setText(strAbsentType + "  ");
                leaveType_TextView.setGravity(Gravity.RIGHT);
                leaveReason_TextView.setText(strReason);
            } else {
                leaveReason_LinearLayout.setVisibility(View.GONE);
                leaveType_LinearLayout.setVisibility(View.GONE);
            }
        }

        if (sign == DefineUtil.wdhy && intUserRole == 0 && (objChildMtListBeen != null && objChildMtListBeen.size() > 0)) { // 参会通知,委员,有次会
            attendState_LinearLayout.setVisibility(View.GONE);
            chilMt_Listview.setVisibility(View.VISIBLE);
            adapter1 = new ChildMeetAttendSituationLVAdapter(activity, objChildMtListBeen, sign);
            chilMt_Listview.setAdapter(adapter1);
        }

        if (intModify == 1) {
            situation_TextView.setOnClickListener(this);
        } else {
            situation_TextView.setCompoundDrawables(null, null, null, null);
            leaveType_TextView.setCompoundDrawables(null, null, null, null);
        }
        setListViewHeight(listView);
    }

    public static void setListViewHeight(ListView lv) {
        ListAdapter la = lv.getAdapter();
        if (null == la) {
            return;
        }

// calculate height of all items.
        int h = 0;
        final int cnt = la.getCount();
        for (int i = 0; i < cnt; i++) {
            View item = la.getView(i, null, lv);
            item.measure(0, 0);
            h += item.getMeasuredHeight();
        }
// reset ListView height
        ViewGroup.LayoutParams lp = lv.getLayoutParams();
        lp.height = h + (lv.getDividerHeight() * (cnt) - 1);
        lv.setLayoutParams(lp);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle;
        switch (v.getId()) {
            case R.id.btn_goto:
                if (goto_Button.getText().toString().trim().equals(getString(R.string.string_sign_up))) {
                    bundle = new Bundle();
                    if (sign == DefineUtil.wdhd) {
                        bundle.putString("headTitle", getString(R.string.string_event_registration));
                        bundle.putString("strName", activityViewBean.getStrAtName());
                        bundle.putString("strStartDate", activityViewBean.getStrStartDate());
                        bundle.putString("strEndDate", activityViewBean.getStrEndDate());
                        bundle.putString("strPlace", activityViewBean.getStrPlace());
                        bundle.putString("strId", activityViewBean.getStrId());
                    } else {
                        bundle.putString("headTitle", getString(R.string.string_meeting_registration));
                        bundle.putString("strName", meetingViewBean.getStrMtName());
                        bundle.putString("strStartDate", meetingViewBean.getStrStartDate());
                        bundle.putString("strEndDate", meetingViewBean.getStrEndDate());
                        bundle.putString("strPlace", meetingViewBean.getStrPlace());
                        bundle.putString("strId", meetingViewBean.getStrId());
                    }
                    bundle.putInt("sign", sign);
                    bundle.putBoolean("isFromRemindList", true);
                    showActivity(fragment, MeetAct_SignUpActivity.class, bundle, 2);
                } else {
                    bundle = new Bundle();
                    bundle.putString("strId", activityViewBean.getStrId());
                    showActivity(fragment, Activity_ResultActivity.class, bundle);
                }
                break;
            case R.id.tv_situation_value:
                if (sign == DefineUtil.wdhd || sign == DefineUtil.xghd) {
                    bundle = new Bundle();
                    Intent intent = new Intent(activity, MeetAct_SignUpActivity.class);
                    bundle.putString("headTitle", getString(R.string.string_modify_participation_status));
                    bundle.putInt("sign", sign);
                    bundle.putSerializable("FileList", objFileBeans);
                    bundle.putBoolean("isFromDetail", true);//区分来自详情还是列表
                    bundle.putString("strName", activityViewBean.getStrAtName());
                    bundle.putString("strStartDate", activityViewBean.getStrStartDate());
                    bundle.putString("strEndDate", activityViewBean.getStrEndDate());
                    bundle.putString("strPlace", activityViewBean.getStrPlace());
                    bundle.putString("strId", activityViewBean.getStrId());
                    bundle.putString("strType", activityViewBean.getStrMtType());
                    bundle.putString("state", Utils.getAttendST(Integer.parseInt(intAttend)));//出席状态
                    bundle.putString("leaveTypeId", activityViewBean.getStrAbsentType());//请假类型
                    bundle.putString("strReason", activityViewBean.getStrReason());//请假原因
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, 0);
                } else if (sign == DefineUtil.wdhy) {
                    bundle = new Bundle();
                    Intent intent = new Intent(activity, MeetAct_SignUpActivity.class);
                    bundle.putString("headTitle", getString(R.string.string_modify_participation_status));
                    bundle.putInt("sign", sign);
                    bundle.putSerializable("FileList", objFileMeetingBeans);
                    bundle.putBoolean("isFromDetail", true);//区分来自详情还是列表
                    bundle.putString("strName", meetingViewBean.getStrMtName());
                    bundle.putString("strStartDate", meetingViewBean.getStrStartDate());
                    bundle.putString("strEndDate", meetingViewBean.getStrEndDate());
                    bundle.putString("strPlace", meetingViewBean.getStrPlace());
                    bundle.putString("strId", meetingViewBean.getStrId());
                    bundle.putString("strType", meetingViewBean.getStrMtType());
                    bundle.putString("state", Utils.getAttendST(Integer.parseInt(intAttend)));//出席状态
                    bundle.putString("leaveTypeId", meetingViewBean.getStrAbsentType());//请假类型
                    bundle.putString("strReason", meetingViewBean.getStrReason());//请假原因
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, 0);
                }
                break;
            case R.id.tv_leave_tag:
                if (leaveTag_TextView.isSelected()) {
                    leaveTag_TextView.setSelected(false);
                    leaveTag_TextView.setTextColor(getResources().getColor(R.color.font_content));
                } else {
                    leaveTag_TextView.setSelected(true);
                    leaveTag_TextView.setTextColor(getResources().getColor(R.color.view_head_bg));
                }
                break;
            case R.id.ll_file:
                bundle = new Bundle();
                if (activityViewBean != null) {
                    bundle.putSerializable("fileList", activityViewBean.getFileList());
                } else {
                    bundle.putSerializable("fileList", meetingViewBean.getFileList());
                }

                bundle.putString("strTitle", getString(R.string.string_relevant_attachments));
                showActivity(fragment, FileListActivity.class, bundle);
                break;
            default:
                break;
        }
    }

    /**
     * 更新会议、活动详情页面
     *
     * @param meetingViewBeans
     */
    public void update(ActivityViewBean activityViewBeans, MeetingViewBean meetingViewBeans) {
        activityViewBean = activityViewBeans;
        meetingViewBean = meetingViewBeans;
        setData();
    }
}
