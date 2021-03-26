package com.suncn.ihold_zxztc.bean;

import com.gavin.giframe.utils.GIStringUtil;

import java.util.ArrayList;

public class PersonPageBean extends BaseGlobal {
    private MemberHomeBean memberHome;
    private String strLinkId;
    private String strQDUserId;

    public String getStrQDUserId() {
        return strQDUserId;
    }


    public String getStrLinkId() {
        return strLinkId;
    }

    public MemberHomeBean getMemberHome() {
        return memberHome;
    }

    private ArrayList<CircleListBean.DynamicBean> dynamicList;

    public ArrayList<CircleListBean.DynamicBean> getDynamicList() {
        return dynamicList;
    }

    public class MemberHomeBean {
        private String strPhotoPath;
        private String strUserName;
        private String strUserId;
        private String strFaction; // 党派
        private String strSector; // 界别
        private String strDuty;

        public String getStrFaction() {
            if (GIStringUtil.isBlank(strFaction)) {
                return "";
            }
            return strFaction;
        }

        public String getStrPhotoPath() {
            return strPhotoPath;
        }

        public String getStrUserName() {
            return strUserName;
        }

        public String getStrUserId() {
            return strUserId;
        }

        public String getStrSector() {
            return strSector;
        }

        public String getStrDuty() {
            return strDuty;
        }

        public boolean getLoginUserIsNotice() {
            return loginUserIsNotice;
        }

        public void setLoginUserIsNotice(boolean loginUserIsNotice) {
            this.loginUserIsNotice = loginUserIsNotice;
        }

        public String getIntPraiseAllCount() {
            return intPraiseAllCount;
        }

        public String getIntNoticedAllCount() {
            return intNoticedAllCount;
        }

        private boolean loginUserIsNotice;
        private String intPraiseAllCount;
        private String intNoticedAllCount;
        private String strShowInterestButton;

        public void setIntNoticedAllCount(String intNoticedAllCount) {
            this.intNoticedAllCount = intNoticedAllCount;
        }

        public String getStrShowInterestButton() {
            return strShowInterestButton;
        }
    }
}
