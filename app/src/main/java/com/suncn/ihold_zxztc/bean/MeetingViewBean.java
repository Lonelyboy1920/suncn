package com.suncn.ihold_zxztc.bean;

import com.gavin.giframe.utils.GIStringUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 我的会议详情MeetingViewServlet
 */

public class MeetingViewBean extends BaseGlobal {
    private int intModify; // 办结状态（0-不能修改；1-可以修改）
    private String strId; // Id
    private String strType;//会议类型
    private String strHandleUnit;//举办单位
    private String strYear; // 年份
    private String strSessionCode; // 届次
    private String strName; // 会议名称
    private String strMtEasyName; // 会议简称
    private String strStartDate; // 开始时间
    private String strEndDate; // 结束时间
    private String strPlace; // 会议地点
    private String strContent; // 内容
    private String strPubDate; // 发布时间
    private String strPubUnit; // 发布单位
    private String AgreePub; // 是否发布
    private String sendDate; // 定时发送
    private String strMsgTxt; // 短信内容
    private String strAbsentType; // 请假类型
    private String strReason; // 请假原因
    private String intAttend; // 出席状况  1-出席，0-请假 2——缺席
    private String strNewsContent;
    private String strMemberType;
    private ArrayList<ChildMtForWorker> childMeetList;//工作人员出席情况次会列表
    private ArrayList<ObjChildMtListBean> childList; // 次会集合
    private ArrayList<ObjMemListBean> objMem_list; // 参会成员列表（管理员可见）
    private ArrayList<ObjFileBean> affix;
    private ArrayList<ObjFileBean> fileList;//会议附件
    private String strState;
    private String strAttendWill;
    private String strAttendResult;
    private String isLocationCheck;//是否开启定位签到 0开 1 关
    private String strLongitude;
    private String strLatitude;
    private String strAttendId;
    private String strIsEnd;
    private String strContentUrl;

    public String getStrContentUrl() {
        return strContentUrl;
    }
    public void setStrState(String strState) {
        this.strState = strState;
    }

    public String getStrIsEnd() {
        return strIsEnd;
    }

    public String getStrAttendId() {
        return strAttendId;
    }

    public String getStrType() {
        return strType;
    }

    public String getStrHandleUnit() {
        return strHandleUnit;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrPubDate() {
        return strPubDate;
    }

    public ArrayList<ObjChildMtListBean> getChildList() {
        return childList;
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

    public String getStrAttendResult() {
        return strAttendResult;
    }

    public String getStrAttendWill() {
        return strAttendWill;
    }

    public String getStrState() {
        return strState;
    }
    public ArrayList<ObjFileBean> getFileList() {
        return fileList;
    }

    public String getStrNewsContent() {
        if (GIStringUtil.isBlank(strNewsContent)) {
            strNewsContent = "无";
        }
        return strNewsContent;
    }

    public ArrayList<ChildMtForWorker> getChildMeetList() {
        return childMeetList;
    }

    public class ChildMtForWorker {
        private String strMeetChildId; // 次会id
        private String strMeetChildName; // 次会名称

        public String getStrMeetChildId() {
            return strMeetChildId;
        }

        public String getStrMeetChildName() {
            return strMeetChildName;
        }
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public ArrayList<ObjFileBean> getAffix() {
        return affix;
    }

    public ArrayList<ObjMemListBean> getObjMem_list() {
        return objMem_list;
    }

    public String getStrMemberType() {
        return strMemberType;
    }

    public String getStrAbsentType() {
        return strAbsentType;
    }

    public String getStrReason() {
        return strReason;
    }


    public String getIntAttend() {
        if (GIStringUtil.isBlank(intAttend)) {
            intAttend = "-2";
        }
        return intAttend;
    }

    public int getIntModify() {
        return intModify;
    }

    public String getStrId() {
        return strId;
    }


    public String getStrMtType() {
        return strType;
    }


    public String getStrHostUnit() {
        return strHandleUnit;
    }


    public String getStrYear() {
        return strYear;
    }


    public String getStrSessionCode() {
        return strSessionCode;
    }


    public String getStrMtName() {
        return strName;
    }

    public String getStrMtEasyName() {
        return strMtEasyName;
    }


    public String getStrStartDate() {
        return strStartDate;
    }


    public String getStrEndDate() {
        return strEndDate;
    }


    public String getStrPlace() {
        return strPlace;
    }


    public String getStrContent() {
        return strContent;
    }


    public String getDtPubDate() {
        return strPubDate;
    }


    public String getStrPubUnit() {
        return strPubUnit;
    }


    public String getAgreePub() {
        return AgreePub;
    }


    public String getSendDate() {
        return sendDate;
    }


    public String getStrMsgTxt() {
        return strMsgTxt;
    }


    public ArrayList<ObjChildMtListBean> getObjChildMt_list() {
        return childList;
    }


    public static class ObjChildMtListBean implements Serializable {
        private String strId; // 次会id
        private String strName; // 次会名称
        private int intAttend; // 出席状况  1-出席，0-请假 2——缺席
        private String strAbsentType; // 请假类型
        private String strReason; // 请假原因
        private int intModify;//1-可修改，0-不可修改
        private String strStartDate;//开始时间
        private String strEndDate;//结束时间
        private String strPlace;//次会地点
        private String strMemberType;
        private ArrayList<ObjFileBean> affix;

        public ArrayList<ObjFileBean> getAffix() {
            return affix;
        }

        public String getStrMemberType() {
            return strMemberType;
        }

        public int getIntModify() {
            return intModify;
        }

        public String getStrChildMtStartDate() {
            return strStartDate;
        }

        public String getStrChildMtEndDate() {
            return strEndDate;
        }

        public String getStrChildMtPlace() {
            return strPlace;
        }

        public String getStrAbsentType() {
            return strAbsentType;
        }

        public void setStrAbsentType(String strAbsentType) {
            this.strAbsentType = strAbsentType;
        }

        public String getStrReason() {
            return strReason;
        }

        public void setStrReason(String strReason) {
            this.strReason = strReason;
        }

        public String getStrChildMtId() {
            return strId;
        }

        public void setStrChildMtId(String strId) {
            this.strId = strId;
        }

        public String getStrChildMtName() {
            return strName;
        }


        public int getIntAttend() {
            return intAttend;
        }

        public void setIntAttend(int intAttend) {
            this.intAttend = intAttend;
        }
    }

    public static class ObjMemListBean implements Serializable {
        /*如果有次会则返回会议及参会人员*/
        private String strChName; // 次会名称
        private ArrayList<ObjPersonListBean> objPerson_list; // 人员集合（有次会时才有此字段）

        /*如果无次会则直接返回参会人员*/
        private String strName; // 委员姓名
        private String strMtName; // 主会名称（没有次会时显示）
        private String strFaction; // 党派
        private String strSector; // 界别
        private int intAttend; // 出席状态   0请假1出席2缺席
        private String strMobile; // 手机


        public String getStrName() {
            return strName;
        }

        public String getStrMtName() {
            return strMtName;
        }

        public String getStrFaction() {
            return strFaction;
        }

        public String getStrSector() {
            return strSector;
        }

        public int getIntAttend() {
            return intAttend;
        }

        public String getStrMobile() {
            return strMobile;
        }

        public String getStrChName() {
            return strChName;
        }

        public ArrayList<ObjPersonListBean> getObjPerson_list() {
            return objPerson_list;
        }
    }

}
