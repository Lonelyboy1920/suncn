package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-5-25.
 * 会议活动报名请假类型
 */
public class LeaveTypeListBean extends BaseGlobal {
    private ArrayList<LeaveTypeBean> objList;

    public ArrayList<LeaveTypeBean> getObjList() {
        return objList;
    }

    public class LeaveTypeBean {
        private String strAbsentType;
        private String strAbsentName;

        public String getStrAbsentType() {
            return strAbsentType;
        }

        public String getStrAbsentName() {
            return strAbsentName;
        }
    }
}
