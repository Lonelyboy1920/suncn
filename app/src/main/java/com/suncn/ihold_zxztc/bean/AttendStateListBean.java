package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-511-22.
 * 会议活动出席状态
 */
public class AttendStateListBean extends BaseGlobal {
    private ArrayList<AttendStateBean> objList;

    public ArrayList<AttendStateBean> getObjList() {
        return objList;
    }

    public class AttendStateBean {
        private String strAttendResultName;//名称
        private String strAttendResult;//编码

        public String getStrAttendResultName() {
            return strAttendResultName;
        }

        public String getStrAttendResult() {
            return strAttendResult;
        }
    }
}
