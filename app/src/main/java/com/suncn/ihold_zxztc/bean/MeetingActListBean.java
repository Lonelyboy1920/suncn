package com.suncn.ihold_zxztc.bean;

import java.util.List;

public class MeetingActListBean extends BaseGlobal {

    /**
     * strError :
     * intCurPage : 1
     * intAllCount : 19
     * strSid : f4cb474f4c014e0383c4a239e9e1fa3a
     * strRlt : true
     * intPageSize : 10
     * objList : [{"strId":"fd05eff48d154e59ac1c7acea050ed0e","strMemberType":"受邀会议","strType":"报告会","strName":"报告会","strPubState":"已发布","strPlace":"三十","strStartDate":"2019-04-28 10:17","strEndDate":"2019-04-30 10:17","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":0,"strSignWay":"","strState":"未报名","intModify":0,"objMeetingChild_list":[]},{"strId":"4091ac6360aa435cbcd76746f786592b","strMemberType":"受邀会议","strType":"学习培训","strName":"北戴河政协委员培训班(2)","strPubState":"已发布","strPlace":"全国政协北戴河培训中心","strStartDate":"2019-05-12 09:00","strEndDate":"2019-05-18 17:30","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":0,"strSignWay":"","strState":"缺席","intModify":0,"objMeetingChild_list":[]},{"strId":"7af1b68224194255a5ac755443ffa516","strMemberType":"受邀会议","strType":"专委会全体会议","strName":"市政协民宗委全体委员会议","strPubState":"已发布","strPlace":"市政协19楼会议室","strStartDate":"2019-03-06 09:30","strEndDate":"2019-03-06 11:30","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":0,"strSignWay":"","strState":"出席","intModify":0,"objMeetingChild_list":[]},{"strId":"badbfa483b4349b387e737bf2e3708d6","strMemberType":"受邀会议","strType":"全会","strName":"政协合肥市第十四届委员会第二次全体委员会议","strPubState":"已发布","strPlace":"市政务中心大会堂","strStartDate":"2019-01-07 09:00","strEndDate":"2019-01-10 17:30","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":1,"strSignWay":"","strState":"","objMeetingChild_list":[]},{"strId":"1543197987276203","strMemberType":"受邀会议","strType":"其他会议","strName":"合肥市社情民意座谈会","strPubState":"已办结","strPlace":"市政务中心10号会议室","strStartDate":"2018-11-28 14:40","strEndDate":"2018-11-28 17:30","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":0,"strSignWay":"","strState":"请假","intModify":0,"objMeetingChild_list":[]},{"strId":"1542267618320956","strMemberType":"受邀会议","strType":"专题协商会","strName":"关于协商会的通知","strPubState":"已办结","strPlace":"","strStartDate":"2018-11-20 09:00","strEndDate":"2018-11-20 11:30","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":0,"strSignWay":"","strState":"出席","intModify":0,"objMeetingChild_list":[]},{"strId":"1539657530061290","strMemberType":"受邀会议","strType":"其他会议","strName":"\u201c社会主义核心价值观进寺观教堂\u201d调研总结会暨对口协商筹备会","strPubState":"已办结","strPlace":"会议中心2楼12号会议室","strStartDate":"2018-10-17 09:00","strEndDate":"2018-10-17 11:30","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":0,"strSignWay":"","strState":"出席","intModify":0,"objMeetingChild_list":[]},{"strId":"1539586676839263","strMemberType":"受邀会议","strType":"其他会议","strName":"调研总结会","strPubState":"已办结","strPlace":"市政协9楼会议室","strStartDate":"2018-10-17 09:00","strEndDate":"2018-10-17 11:00","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":0,"strSignWay":"","strState":"出席","intModify":0,"objMeetingChild_list":[]},{"strId":"1535701239365051","strMemberType":"受邀会议","strType":"学习培训","strName":"浙江大学政协委员培训班","strPubState":"已办结","strPlace":"浙江大学","strStartDate":"2018-08-12 15:00","strEndDate":"2018-08-18 15:00","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":0,"strSignWay":"","strState":"出席","intModify":0,"objMeetingChild_list":[]},{"strId":"1535356143074137","strMemberType":"受邀会议","strType":"专题协商会","strName":"\u201c创业带动就业，推动更高质量就业\u201d专题协商会","strPubState":"已办结","strPlace":"市政协19楼会议室","strStartDate":"2018-08-31 15:00","strEndDate":"2018-08-31 17:30","strLongitude":"","strLatitude":"","strLocationSignDistance":"","intIsHavChild":0,"strSignWay":"","strState":"出席","intModify":0,"objMeetingChild_list":[]}]
     */

    private List<ObjMeetingActBean> objList;

    public List<ObjMeetingActBean> getObjList() {
        return objList;
    }

    public void setObjList(List<ObjMeetingActBean> objList) {
        this.objList = objList;
    }

    public static class ObjMeetingActBean {
        /**
         * strId : fd05eff48d154e59ac1c7acea050ed0e
         * strMemberType : 受邀会议
         * strType : 报告会
         * strName : 报告会
         * strPubState : 已发布
         * strPlace : 三十
         * strStartDate : 2019-04-28 10:17
         * strEndDate : 2019-04-30 10:17
         * strLongitude :
         * strLatitude :
         * strLocationSignDistance :
         * intIsHavChild : 0
         * strSignWay :
         * strState : 未报名
         * intModify : 0
         */

        private String strId;
        private String strMemberType;
        private String strType;
        private String strName;
        private String strPubState;
        private String strPlace;
        private String strStartDate;
        private String strEndDate;
        private String strLongitude;
        private String strLatitude;
        private String strLocationSignDistance;
        private int intIsHavChild;//-是否有次会(1-有，0-无)
        private String strSignWay;
        private String strState;//0 请假 1出席 2缺席 （3去报名 4未签到 虚拟的状态值,方便移动进行判断)
        private String strStateName;//状态名称
        private int intModify;//0-不显示操作按钮，1-显示
        private String strSeat;
        private String strAttendId;//对应的出席记录Id

        public String getStrAttendId() {
            return strAttendId;
        }

        public String getStrSeat() {
            return strSeat;
        }

        public void setStrSeat(String strSeat) {
            this.strSeat = strSeat;
        }

        public String getStrStateName() {
            return strStateName;
        }

        public void setStrStateName(String strStateName) {
            this.strStateName = strStateName;
        }

        public String getStrId() {
            return strId;
        }

        public void setStrId(String strId) {
            this.strId = strId;
        }

        public String getStrMemberType() {
            return strMemberType;
        }

        public void setStrMemberType(String strMemberType) {
            this.strMemberType = strMemberType;
        }

        public String getStrType() {
            return strType;
        }

        public void setStrType(String strType) {
            this.strType = strType;
        }

        public String getStrName() {
            return strName;
        }

        public void setStrName(String strName) {
            this.strName = strName;
        }

        public String getStrPubState() {
            return strPubState;
        }

        public void setStrPubState(String strPubState) {
            this.strPubState = strPubState;
        }

        public String getStrPlace() {
            return strPlace;
        }

        public void setStrPlace(String strPlace) {
            this.strPlace = strPlace;
        }

        public String getStrStartDate() {
            return strStartDate;
        }

        public void setStrStartDate(String strStartDate) {
            this.strStartDate = strStartDate;
        }

        public String getStrEndDate() {
            return strEndDate;
        }

        public void setStrEndDate(String strEndDate) {
            this.strEndDate = strEndDate;
        }

        public String getStrLongitude() {
            return strLongitude;
        }

        public void setStrLongitude(String strLongitude) {
            this.strLongitude = strLongitude;
        }

        public String getStrLatitude() {
            return strLatitude;
        }

        public void setStrLatitude(String strLatitude) {
            this.strLatitude = strLatitude;
        }

        public String getStrLocationSignDistance() {
            return strLocationSignDistance;
        }

        public void setStrLocationSignDistance(String strLocationSignDistance) {
            this.strLocationSignDistance = strLocationSignDistance;
        }

        public int getIntIsHavChild() {
            return intIsHavChild;
        }

        public void setIntIsHavChild(int intIsHavChild) {
            this.intIsHavChild = intIsHavChild;
        }

        public String getStrSignWay() {
            return strSignWay;
        }

        public void setStrSignWay(String strSignWay) {
            this.strSignWay = strSignWay;
        }

        public String getStrState() {
            return strState;
        }

        public void setStrState(String strState) {
            this.strState = strState;
        }

        public int getIntModify() {
            return intModify;
        }

        public void setIntModify(int intModify) {
            this.intModify = intModify;
        }
    }
}
