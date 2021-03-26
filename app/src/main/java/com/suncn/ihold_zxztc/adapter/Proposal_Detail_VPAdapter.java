package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_Detail_AttendPersonFragment;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_Detail_AuditLeaveFragment;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetAct_Detail_BaseInfoFragment;
import com.suncn.ihold_zxztc.activity.application.proposal.ProposalHandlingFeedbackFragment;
import com.suncn.ihold_zxztc.activity.application.proposal.ProposalHandlingFragment;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_Detail_BaseInfoFragment;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_Detail_ContentFragment;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_Detail_InstructFragment;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_Detail_TransactFragment;
import com.suncn.ihold_zxztc.bean.ActivityViewBean;
import com.suncn.ihold_zxztc.bean.MeetingViewBean;
import com.suncn.ihold_zxztc.bean.ObjFeedBackListBean;
import com.suncn.ihold_zxztc.bean.ObjPersonListBean;
import com.suncn.ihold_zxztc.bean.ObjTransactBean;
import com.suncn.ihold_zxztc.bean.ProposalViewBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 政协提案、会议、活动详情的ViewPager Adapter
 */
public class Proposal_Detail_VPAdapter extends FragmentPagerAdapter {
    private String[] titleArray = {"基本信息", "提案内容", "办理情况", "领导批示"}; // tab菜单
    private ArrayList<String> baseInfoList; // 基本信息集合
    private ActivityViewBean activityViewBean;
    private MeetingViewBean meetingViewBean;
    private ArrayList<ObjPersonListBean> objPersonListBeen; // 参会人员（活动通知）
    private ArrayList<MeetingViewBean.ObjMemListBean> objMemListBeen; // 会议及其参会人员（参会人员）
    private String reason; // 情况分析
    private String way; // 具体建议
    private int sign;
    private ArrayList<String> tagList;
    private List<ObjTransactBean> transactBeens; // 办理情况集合
    private List<ProposalViewBean.MotionApproval> joinOpinions;//领导批示集合
    private ProposalViewBean.MemOpinions memOpinions;
    private FragmentManager fm;
    private boolean isWorker;
    private String strId;
    private String strType;
    private boolean isManager;
    private Context context;
    private String proposalDetailShowTab; //1(显示基本信息、提案内容)   2(办理中、待反馈的，显示基本信息、提案内容、办理情况)   3(已反馈的，显示基本信息、提案内容、办理情况、反馈情况)
    private String strAttendId;
    private List<ObjFeedBackListBean> objFeedBackListBeanList; // 反馈情况集合
    private String strFeedBackAllState; //整体评价
    private String strReasonTitle; //提案内容的命名
    private String strWayTitle;//解决方法的命名

    public void setStrReasonTitle(String strReasonTitle) {
        this.strReasonTitle = strReasonTitle;
    }

    public void setStrWayTitle(String strWayTitle) {
        this.strWayTitle = strWayTitle;
    }

    public String getStrFeedBackAllState() {
        return strFeedBackAllState;
    }

    public void setStrFeedBackAllState(String strFeedBackAllState) {
        this.strFeedBackAllState = strFeedBackAllState;
    }

    public List<ObjFeedBackListBean> getObjFeedBackListBeanList() {
        return objFeedBackListBeanList;
    }

    public void setObjFeedBackListBeanList(List<ObjFeedBackListBean> objFeedBackListBeanList) {
        this.objFeedBackListBeanList = objFeedBackListBeanList;
    }

    public void setStrAttendId(String strAttendId) {
        this.strAttendId = strAttendId;
    }

    public String getStrAttendId() {
        return strAttendId;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public List<ProposalViewBean.MotionApproval> getJoinOpinions() {
        return joinOpinions;
    }

    public void setJoinOpinions(List<ProposalViewBean.MotionApproval> joinOpinions) {
        this.joinOpinions = joinOpinions;
    }

    public boolean isWorker() {
        return isWorker;
    }

    public void setWorker(boolean worker) {
        isWorker = worker;
    }

    public void setMemOpinions(ProposalViewBean.MemOpinions memOpinions) {
        this.memOpinions = memOpinions;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    //提案详情调用
    public Proposal_Detail_VPAdapter(FragmentManager fm, String strType, Context context, String proposalDetailShowTab) {
        super(fm);
        this.fm = fm;
        this.strType = strType;
        this.context = context;
        this.proposalDetailShowTab = proposalDetailShowTab;
        tagList = new ArrayList<>();
        switch (strType) {
            case "0":
                titleArray = new String[]{context.getString(R.string.string_basic_information), context.getString(R.string.string_proposal_content), context.getString(R.string.string_leader_instructions)}; // tab菜单
                break;
            case "1":
            case "2":
                titleArray = new String[]{context.getString(R.string.string_basic_information), context.getString(R.string.string_proposal_content), context.getString(R.string.string_handling_situation), context.getString(R.string.string_leader_instructions)}; // tab菜单
                break;
            default:
                break;
        }

        switch (proposalDetailShowTab) {
            case "1":
                titleArray = new String[]{context.getString(R.string.string_basic_information), context.getString(R.string.string_proposal_content)}; // tab菜单
                break;
            case "2":
                titleArray = new String[]{context.getString(R.string.string_basic_information), context.getString(R.string.string_proposal_content), context.getString(R.string.string_handling_situation)}; // tab菜单
                break;
            case "3":
                titleArray = new String[]{
                        context.getString(R.string.string_basic_information),
                        context.getString(R.string.string_proposal_content),
                        context.getString(R.string.string_feedback_happening)}; // tab菜单
                break;
            case "4":
                titleArray = new String[]{
                        context.getString(R.string.string_basic_information),
                        context.getString(R.string.string_proposal_content),
                        context.getString(R.string.string_handling_situation),
                        context.getString(R.string.string_feedback_happening)}; // tab菜单
                break;
        }
    }

    //会议活动相关功能调用
    public Proposal_Detail_VPAdapter(FragmentManager fm, int sign, int intUserRole, boolean b, Context context) {
        super(fm);
        this.sign = sign;
        this.isManager = b;
        this.fm = fm;
        tagList = new ArrayList<>();
        switch (sign) {
            case DefineUtil.xghd://相关活动
                titleArray = new String[]{context.getString(R.string.string_basic_information)};
                break;
            case DefineUtil.wdhd:
            case DefineUtil.hdgl:
                if (intUserRole == 1 && !isManager) { // 工作人员
                    titleArray = new String[]{context.getString(R.string.string_basic_information), context.getString(R.string.string_attendance), context.getString(R.string.string_leave_for_review)};
                } else {
                    titleArray = new String[]{context.getString(R.string.string_basic_information)};
                }
                break;
            case DefineUtil.wdhy:
            case DefineUtil.hygl:
                if (intUserRole == 1 && !isManager) {
                    titleArray = new String[]{context.getString(R.string.string_basic_information), context.getString(R.string.string_attendance), context.getString(R.string.string_leave_for_review)};
                } else {
                    titleArray = new String[]{context.getString(R.string.string_basic_information)};
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        tagList.add(makeFragmentName(container.getId(), getItemId(position))); //把tag存起来
        return super.instantiateItem(container, position);
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    public void setObjPersonListBeen(ArrayList<ObjPersonListBean> objPersonListBeen) {
        this.objPersonListBeen = objPersonListBeen;
    }

    public void setBaseInfoList(ArrayList<String> baseInfoList) {
        this.baseInfoList = baseInfoList;
    }

    public void setActivityViewBean(ActivityViewBean activityViewBean) {
        this.activityViewBean = activityViewBean;
    }

    public void setMeetingViewBean(MeetingViewBean meetingViewBean) {
        this.meetingViewBean = meetingViewBean;
    }

    public void setObjMemListBeen(ArrayList<MeetingViewBean.ObjMemListBean> objMemListBeen) {
        this.objMemListBeen = objMemListBeen;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public void setTransactBeens(List<ObjTransactBean> transactBeens) {
        this.transactBeens = transactBeens;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // 基本信息
                if (sign == DefineUtil.wdhd || sign == DefineUtil.hdgl)
                    return MeetAct_Detail_BaseInfoFragment.newInstance(activityViewBean, null, sign);
                else if (sign == DefineUtil.wdhy || sign == DefineUtil.hygl)
                    return MeetAct_Detail_BaseInfoFragment.newInstance(null, meetingViewBean, sign);
                else
                    return Proposal_Detail_BaseInfoFragment.newInstance(baseInfoList, strId);
            case 1:
                if (sign == DefineUtil.hdgl)
                    return MeetAct_Detail_AttendPersonFragment.newInstance(sign, strId, strAttendId, activityViewBean, null);  // 活动管理中的参会人员
                else if (sign == DefineUtil.hygl)
                    return MeetAct_Detail_AttendPersonFragment.newInstance(sign, strId, strAttendId, null, meetingViewBean);  // 会议管理中的参会人员
                else
                    return Proposal_Detail_ContentFragment.newInstance(reason, way, strReasonTitle, strWayTitle); // 提案详情中的正文内容
            case 2: // 办理情况
                if (sign == DefineUtil.hdgl)
                    return MeetAct_Detail_AuditLeaveFragment.newInstance(sign, strId);
                else if (sign == DefineUtil.hygl)
                    return MeetAct_Detail_AuditLeaveFragment.newInstance(sign, strId);
                else if (strType.equals("0"))
                    return Proposal_Detail_InstructFragment.newInstance(joinOpinions);
                else if ("2".equals(proposalDetailShowTab) || "4".equals(proposalDetailShowTab)) {
                    return ProposalHandlingFragment.newInstance(transactBeens);
                } else {
                    return ProposalHandlingFeedbackFragment.newInstance(objFeedBackListBeanList, strFeedBackAllState);
                }
            case 3: // 联名意见
                if ("4".equals(proposalDetailShowTab)) {  //反馈情况
                    return ProposalHandlingFeedbackFragment.newInstance(objFeedBackListBeanList, strFeedBackAllState);
                } else {
                    return Proposal_Detail_InstructFragment.newInstance(joinOpinions);
                }
        }
        return Proposal_Detail_BaseInfoFragment.newInstance(baseInfoList, strId);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleArray[position % titleArray.length].toUpperCase();
    }

    // 动态设置我们标题的方法
    public void setPageTitle(int position, String title) {
        if (position >= 0 && position < titleArray.length) {
            titleArray[position] = title;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return titleArray.length;
    }

    public void update(ActivityViewBean activityViewBean, MeetingViewBean meetingViewBean) {
        if (tagList != null) {
            for (int i = 0; i < tagList.size(); i++) {
                String tag = tagList.get(i);
                if (i == 0) {
                    MeetAct_Detail_BaseInfoFragment fragment = (MeetAct_Detail_BaseInfoFragment) fm.findFragmentByTag(tag);
                    if (fragment == null) {
                        return;
                    } else {
                        if (sign == DefineUtil.wdhd || sign == DefineUtil.xghd) {
                            fragment.update(activityViewBean, null);
                        } else {
                            fragment.update(null, meetingViewBean);
                        }
                    }
                } else if (i == 1) {
                    MeetAct_Detail_AttendPersonFragment fragment = (MeetAct_Detail_AttendPersonFragment) fm.findFragmentByTag(tag);
                    if (fragment == null) {
                        return;
                    } else {
                        fragment.update();
                    }
                } else if (i == 2) {
                    MeetAct_Detail_AuditLeaveFragment fragment = (MeetAct_Detail_AuditLeaveFragment) fm.findFragmentByTag(tag);
                    if (fragment == null) {
                        return;
                    } else {
                        fragment.update();
                    }
                } /*else if (i == 3) {
                    MemberDetail_OpinionListFragment fragment = (MemberDetail_OpinionListFragment) fm.findFragmentByTag(tag);
                    if (fragment == null) {
                        return;
                    } else {
                        fragment.upDate(objListBeans);
                    }
                }*/
            }
        }
    }

    public void setProposalDetailShowTab(String proposalDetailShowTab) {
        this.proposalDetailShowTab = proposalDetailShowTab;
        switch (proposalDetailShowTab) {
            case "1":
                titleArray = new String[]{context.getString(R.string.string_basic_information), context.getString(R.string.string_proposal_content)}; // tab菜单
                break;
            case "2":
                titleArray = new String[]{context.getString(R.string.string_basic_information), context.getString(R.string.string_proposal_content), context.getString(R.string.string_handling_situation)}; // tab菜单
                break;
            case "3":
                titleArray = new String[]{
                        context.getString(R.string.string_basic_information),
                        context.getString(R.string.string_proposal_content),
                        context.getString(R.string.string_feedback_happening)}; // tab菜单
                break;
            case "4":
                titleArray = new String[]{
                        context.getString(R.string.string_basic_information),
                        context.getString(R.string.string_proposal_content),
                        context.getString(R.string.string_handling_situation),
                        context.getString(R.string.string_feedback_happening)}; // tab菜单
                break;
        }
        notifyDataSetChanged();
    }

}
