package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * Created by whh on 2018-9-12.
 * 活动参与人员 EventViewAttendServlet
 * 会议参与人员 MeetViewAttendServlet
 */
public class AttendPersonListBean extends BaseGlobal {
    private int intMemTotleCount;
    private int intMeetApplyCount;
    private int intModify;

    public int getIntModify() {
        return intModify;
    }

    public int getIntMeetApplyCount() {
        return intMeetApplyCount;
    }

    public int getIntMemTotleCount() {
        return intMemTotleCount;
    }

    private List<ObjAttendMemBean> objList;

    public List<ObjAttendMemBean> getObjList() {
        return objList;
    }

    public void setObjList(List<ObjAttendMemBean> objList) {
        this.objList = objList;
    }
}
