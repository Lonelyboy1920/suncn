package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * 我的活动详情ActivityViewServlet
 * Created by Administrator on 2017/7/7.
 */

public class ActivityViewBean extends BaseGlobal {
    private String strId = "";
    private String strType; // 活动类型
    private String strYear; // 年份
    private String strName; // 会议活动名称
    private int intIsAgreePub; // 是否发布 0:否
    private String strContent; // 正文内容
    private String strContentUrl;
    private String strStartDate; // 开始时间
    private String strEndDate; // 结束时间
    private String strPlace; // 地点
    private String strHandleUnit; // 主办单位
    private int intIsInside; // 是否在市内 0-否   1-是
    private String dtCreatDate; // 创建时间
    private int intModify; // 活动结束标识 1：结束 0：活动
    private String strAttendId; // 当前登录用户参加活动记录id
    private String strAttendResult; // 当前登录用户此活动参加状态 0：请假 1：出席 2：缺席
    private String strAbsentType; // 请假类型
    private String strReason; // 请假原因
    private String strPubDate; // 发布时间
    private String strPubUnit; // 发布单位
    private ArrayList<ObjPersonListBean> objMem_list; // 参会人员集合
    private ArrayList<ObjFileBean> affix;
    private String strOpenResult; //  1 显示 0不显示
    private ArrayList<ObjFileBean> fileList;//会议附件
    private String strAttendWill;
    private String strState;
    private String isLocationCheck;//是否开启定位签到 0开 1 关
    private String strLongitude;
    private String strLatitude;
    private String strIsEnd;

    public String getStrContentUrl() {
        return strContentUrl;
    }

    public void setStrState(String strState) {
        this.strState = strState;
    }
    public String getStrIsEnd() {
        return strIsEnd;
    }

    public String getStrType() {
        return strType;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrHandleUnit() {
        return strHandleUnit;
    }

    public String getStrAttendId() {
        return strAttendId;
    }

    public String getStrPubDate() {
        return strPubDate;
    }

    public String getStrLongitude() {
        return strLongitude;
    }

    public String getStrLatitude() {
        return strLatitude;
    }

    public String getStrLocationSignDistance() {
        return strLocationSignDistance;
    }

    private String strLocationSignDistance;
    public String getIsLocationCheck() {
        return isLocationCheck;
    }

    public String getStrState() {
        return strState;
    }

    public String getStrAttendWill() {
        return strAttendWill;
    }

    public ArrayList<ObjFileBean> getFileList() {
        return fileList;
    }

    public String getStrOpenResult() {
        return strOpenResult;
    }

    public ArrayList<ObjFileBean> getAffix() {
        return affix;
    }

    public String getDtPubDate() {
        return strPubDate;
    }

    public String getStrPubUnit() {
        return strPubUnit;
    }

    public String getStrEndDate() {
        return strEndDate;
    }

    public String getStrAbsentType() {
        return strAbsentType;
    }

    public String getStrReason() {
        return strReason;
    }

    public String getStrId() {
        return strId;
    }

    public String getStrMtType() {
        return strType;
    }

    public String getStrYear() {
        return strYear;
    }


    public String getStrAtName() {
        return strName;
    }


    public int getIntIsAgreePub() {
        return intIsAgreePub;
    }


    public String getStrContent() {
        return strContent;
    }


    public String getStrStartDate() {
        return strStartDate;
    }

    public String getStrPlace() {
        return strPlace;
    }


    public String getStrHostUnit() {
        return strHandleUnit;
    }


    public int getIntIsInside() {
        return intIsInside;
    }


    public String getDtCreatDate() {
        return dtCreatDate;
    }


   /* public int getIntState() {
        return intState;
    }*/

    public int getIntModify() {
        return intModify;
    }

    public String getStrMeetId() {
        return strAttendId;
    }


    public String getStrAttendResult() {
        return strAttendResult;
    }


    public ArrayList<ObjPersonListBean> getObjMem_list() {
        return objMem_list;
    }

}
