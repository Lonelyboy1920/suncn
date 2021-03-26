package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-9-14.
 * 请假审核列表
 */
public class CheckLeaveListBean extends BaseGlobal {
    private ArrayList<CheckLeaveList> objList;

    public ArrayList<CheckLeaveList> getObjList() {
        return objList;
    }

    public class CheckLeaveList {
        private String strApplyName;//申请人姓名
        private String strState;//是否审核(0-已审核，1-未审核、跳转请假审核页面) 2通过，3未通过
        private String strApplyId;//活动申请记录主键
        private String strPathUrl;//头像路径
        private String strStateName;//状态名称
        private String strMobile;//手机号

        public String getStrApplyName() {
            return strApplyName;
        }

        public String getStrState() {
            return strState;
        }

        public String getStrApplyId() {
            return strApplyId;
        }

        public String getStrPathUrl() {
            return strPathUrl;
        }

        public String getStrStateName() {
            return strStateName;
        }

        public String getStrMobile() {
            return strMobile;
        }
    }
}
